package org.imd.oauth2.resourceserver.services;

import lombok.RequiredArgsConstructor;

import org.imd.oauth2.resourceserver.exception.post.PostNotFoundException;
import org.imd.oauth2.resourceserver.exception.postcomment.PostCommentNotFoundException;
import org.imd.oauth2.resourceserver.model.domain.PostComment;
import org.imd.oauth2.resourceserver.model.entities.PostCommentEntity;
import org.imd.oauth2.resourceserver.model.entities.PostEntity;
import org.imd.oauth2.resourceserver.model.mapper.domain.PostCommentDomainMapper;
import org.imd.oauth2.resourceserver.model.repos.PostCommentRepository;
import org.imd.oauth2.resourceserver.model.repos.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostCommentService {

    private final PostCommentDomainMapper pcdMapper;
    private final PostRepository pRepository;
    private final PostCommentRepository pcRepository;

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

        final Optional<PostEntity> postOptional = pRepository.findById(pid);
        PostEntity pEntity = postOptional.orElseThrow(() -> new PostNotFoundException(pid));

        pcEntity.setPost(postOptional.get());
        pEntity.addComment(pcEntity);

        final PostCommentEntity savedPcEntity = pcRepository.save(pcEntity);
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
    public void deletePostById(Long pid, Long cid) {
        if (postCommentExists(pid, cid)) {
            pcRepository.deleteById(cid);
        }
    }

    @Transactional
    public boolean postCommentExists(Long pid, Long cid) {
        boolean result = false;
        Optional<PostCommentEntity> pcOptional = pcRepository.findById(cid);
        if (! pcOptional.isEmpty()) {
            result = pcOptional.get().getPost().getId().equals(pid);
        }

        return result;
    }

    private void checkPostExists(Long pid) throws PostNotFoundException {
        if (! pRepository.existsById(pid)) {
            throw new PostNotFoundException(pid);
        }
    }
}
