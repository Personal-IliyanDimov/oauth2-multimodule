package org.imd.oauth2.resourceserver.services.acl;

import lombok.RequiredArgsConstructor;
import org.imd.oauth2.resourceserver.model.entities.PostEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostAclOperations {
    private final AclOperations aclOperations;

    public void createPostAcl(final Long postId,
                                     final Authentication authentication) {
        aclOperations.createInitialAcl(PostEntity.class,
                                       postId,
                                       authentication);
    }

    public void removePostAcl(final Long postId,
                              final Authentication authentication) {
        aclOperations.removeAcl(PostEntity.class, postId, authentication);
    }
}
