package org.imd.oauth2.resourceserver.services.acl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AclOperations {

    private final MutableAclService aclService;

    /**
     * Creates the initial ACL when CREATING an entry
     * for the first time.
     *
     * @param javaType Entity class for which we create the entry.
     * @param id Identifier of the entity object
     * @param authentication The user authentication
     */
    public void createInitialAcl(final Class<?> childJavaType,
                                 final Long childId,
                                 final Class<?> parentJavaType,
                                 final Long parentId,
                                 final Authentication authentication) {
        // Prepare the information we'd like in our access control entry (ACE)
        final ObjectIdentity childObjId = new ObjectIdentityImpl(childJavaType, childId);
        final ObjectIdentity parentObjId = new ObjectIdentityImpl(parentJavaType, parentId);
        final Sid sid = new PrincipalSid(authentication);
        final Permission childPermission = BasePermission.CREATE;

        // Create or update the relevant child ACL
        MutableAcl childAcl;
        try {
            childAcl = (MutableAcl) aclService.readAclById(childObjId);
        } catch (NotFoundException nfe) {
            childAcl = aclService.createAcl(childObjId);
        }

        // Create or update the relevant parent ACL
        MutableAcl parentAcl;
        try {
            parentAcl = (MutableAcl) aclService.readAclById(parentObjId);
        } catch (NotFoundException nfe) {
            parentAcl = aclService.createAcl(parentObjId);
        }

        // Now grant some permissions via an access control entry (ACE)
        childAcl.insertAce(childAcl.getEntries().size(), childPermission, sid, true);
        aclService.updateAcl(childAcl);
    }

    /**
     * Remove the whole ACL when deleting the entity completely.
     *
     * @param javaType
     * @param id
     * @param authentication
     */
    public void removeAcl(final Class<?> javaType,
                          final Long id,
                          final Authentication authentication) {
        // Delete the whole ACL
        final ObjectIdentity oi = new ObjectIdentityImpl(javaType, id);
        aclService.deleteAcl(oi, true);
    }
}
