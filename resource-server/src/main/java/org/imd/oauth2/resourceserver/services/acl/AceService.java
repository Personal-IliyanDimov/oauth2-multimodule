package org.imd.oauth2.resourceserver.services.acl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AceService {

    private final MutableAclService aclService;

    public void prepareInitialAcl(final Class<?> javaType, final Long id, final String principal) {
        // Prepare the information we'd like in our access control entry (ACE)
        final ObjectIdentity oi = new ObjectIdentityImpl(javaType, id);
        final Sid sid = new PrincipalSid(principal);
        Permission p = BasePermission.ADMINISTRATION;

        // Create or update the relevant ACL
        MutableAcl acl = null;
        try {
            acl = (MutableAcl) aclService.readAclById(oi);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(oi);
        }

        // Now grant some permissions via an access control entry (ACE)
        acl.insertAce(acl.getEntries().size(), p, sid, true);
        aclService.updateAcl(acl);
    }

}
