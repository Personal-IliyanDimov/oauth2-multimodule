package org.imd.oauth2.resourceserver.services;

import lombok.RequiredArgsConstructor;

import org.imd.oauth2.resourceserver.exception.post.PostAlreadyExistsException;
import org.imd.oauth2.resourceserver.exception.post.PostNotFoundException;
import org.imd.oauth2.resourceserver.exception.post.PostNotUpdatedException;
import org.imd.oauth2.resourceserver.model.domain.Post;
import org.imd.oauth2.resourceserver.model.entities.PostEntity;
import org.imd.oauth2.resourceserver.model.mapper.domain.PostDomainMapper;
import org.imd.oauth2.resourceserver.model.repos.PostCommentRepository;
import org.imd.oauth2.resourceserver.model.repos.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostDomainMapper pdMapper;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;

    @Transactional
    public List<Post> findAll() {
        List<PostEntity> allEntities = postRepository.findAll();
        return pdMapper.toPosts(allEntities);
    }

    @Transactional
    public Optional<Post> findPost(Long id) {
        Optional<PostEntity> entityOptional = postRepository.findById(id);
        return entityOptional.map(pe -> pdMapper.toPost(pe));
    }

    @Transactional
    public Post createPost(Post post) throws PostAlreadyExistsException {
        if (Objects.nonNull(post.getId())) {
            throw new IllegalStateException("Post param must be with id null.");
        }

        Optional<PostEntity> existingUserEntityOptional = postRepository.findByTitle(post.getTitle());
        if (existingUserEntityOptional.isPresent()) {
            throw new PostAlreadyExistsException(post.getTitle());
        }

        PostEntity userEntity = pdMapper.toPostEntity(post);
        PostEntity savedUserEntity = postRepository.save(userEntity);
        return pdMapper.toPost(savedUserEntity);
    }

    @Transactional
    public Post updatePost(Long id, Post post) throws PostNotFoundException, PostNotUpdatedException {
        Optional<PostEntity> existingPostEntityOptional = postRepository.findById(post.getId());
        existingPostEntityOptional.orElseThrow(() -> new PostNotFoundException(post.getId()));

        PostEntity existingPostEntity = existingPostEntityOptional.get();
        if (! id.equals(post.getId())) {
            throw new IllegalStateException("Parameter {id} value is different from {post.id} value.");
        }

        pdMapper.transfer(post, existingPostEntity);

        try {
            PostEntity savedUserEntity = postRepository.save(existingPostEntity);
            return pdMapper.toPost(savedUserEntity);
        } catch (RuntimeException re) {
            throw new PostNotUpdatedException(re);
        }
    }

    @Transactional
    public void deletePostById(Long id) throws PostNotFoundException {
        // delete children
        // delete parent

        boolean exists = postRepository.existsById(id);
        if (! exists) {
            throw new PostNotFoundException(id);
        }

        postRepository.deleteById(id);
    }

    @Transactional
    public boolean postExists(Long id) {
        return postRepository.existsById(id);
    }
}
