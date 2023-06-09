package org.imd.oauth2.resourceserver.services.acl;

import lombok.RequiredArgsConstructor;
import org.imd.oauth2.resourceserver.model.entities.PostCommentEntity;
import org.imd.oauth2.resourceserver.model.entities.PostEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCommentAclOperations {
    private final AclOperations aclOperations;

    public void createPostCommentAcl(final Long postCommentId,
                                     final Long postId,
                                     final Authentication authentication) {
        aclOperations.createInitialAcl(PostCommentEntity.class,
                                       postCommentId,
                                       authentication);
    }

    public void removePostCommentAcl(final Long postCommentId,
                                     final Authentication authentication) {
        aclOperations.removeAcl(PostCommentEntity.class, postCommentId, authentication);
    }
}
