package org.imd.oauth2.resourceserver.services;

import lombok.RequiredArgsConstructor;

import org.imd.oauth2.resourceserver.exception.post.PostNotFoundException;
import org.imd.oauth2.resourceserver.exception.postcomment.PostCommentNotFoundException;
import org.imd.oauth2.resourceserver.model.domain.Post;
import org.imd.oauth2.resourceserver.model.domain.PostComment;
import org.imd.oauth2.resourceserver.model.entities.PostCommentEntity;
import org.imd.oauth2.resourceserver.model.entities.PostEntity;
import org.imd.oauth2.resourceserver.model.mapper.domain.PostCommentDomainMapper;
import org.imd.oauth2.resourceserver.model.mapper.domain.PostDomainMapper;
import org.imd.oauth2.resourceserver.model.repos.PostCommentRepository;
import org.imd.oauth2.resourceserver.services.acl.PostCommentAclOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostCommentService {

    private final PostCommentDomainMapper pcdMapper;
    private final PostCommentRepository pcRepository;

    private final PostService postService;
    private final PostDomainMapper pdMapper;
    private final PostCommentAclOperations pcAclOperations;

    @Transactional
    public List<PostComment> findAll(Long pid) {
        List<PostCommentEntity> allEntities = pcRepository.findAllByPostId(pid);
        return pcdMapper.toPostComments(allEntities);
    }

    @Transactional
    public Optional<PostComment> findPostComment(Long pid, Long cid) {
        Optional<PostCommentEntity> pcOptional = pcRepository.findById(cid);
        if (! pcOptional.isEmpty()) {
            if (! pcOptional.get().getPost().getId().equals(pid)) {
                return Optional.empty();
            }
        }

        return pcOptional.map(pce -> pcdMapper.toPostComment(pce));
    }

    @Transactional
    public PostComment createPostComment(Long pid, PostComment pc) throws PostNotFoundException {
        checkPostExists(pid);
        if (Objects.nonNull(pc.getId())) {
            throw new IllegalStateException("Parameter pc must have id set to null. ");
        }
        final PostCommentEntity pcEntity = pcdMapper.toPostCommentEntity(pc);

        final Optional<Post> postOptional = postService.findPost(pid);
        final Post post = postOptional.orElseThrow(() -> new PostNotFoundException(pid));
        PostEntity pEntity = pdMapper.toPostEntity(post);

        pcEntity.setPost(pEntity);
        pEntity.addComment(pcEntity);

        final PostCommentEntity savedPcEntity = pcRepository.save(pcEntity);

        pcAclOperations.createPostCommentAcl(savedPcEntity.getId(), pEntity.getId(),
                SecurityContextHolder.getContext().getAuthentication());

        return pcdMapper.toPostComment(savedPcEntity);
    }

    @Transactional
    public PostComment updatePostComment(Long pid, Long cid, PostComment pc) throws PostNotFoundException, PostCommentNotFoundException {
        if (! postCommentExists(pid, cid)) {
            throw new PostCommentNotFoundException(pid, cid);
        }

        if (! cid.equals(pc.getId())) {
            throw new IllegalStateException("Parameter {cid} value is different from {pc.id} value.");
        }

        final PostCommentEntity existingPcEntity = pcRepository.findById(cid).get();
        pcdMapper.transfer(pc, existingPcEntity);

        final PostCommentEntity updatedPcEntity = pcRepository.save(existingPcEntity);
        return pcdMapper.toPostComment(updatedPcEntity);
    }

    @Transactional
    public void deletePostCommentById(Long pid, Long cid) {
        if (postCommentExists(pid, cid)) {
            pcRepository.deleteById(cid);
        }

        pcAclOperations.removePostCommentAcl(cid,
            SecurityContextHolder.getContext().getAuthentication());
    }

    @Transactional
    public boolean postCommentExists(Long pid, Long cid) {
        boolean result = false;
        final Optional<PostCommentEntity> pcOptional = pcRepository.findById(cid);
        if (! pcOptional.isEmpty()) {
            result = pcOptional.get().getPost().getId().equals(pid);
        }

        return result;
    }

    private void checkPostExists(Long pid) throws PostNotFoundException {
        if (! postService.postExists(pid)) {
            throw new PostNotFoundException(pid);
        }
    }
}
