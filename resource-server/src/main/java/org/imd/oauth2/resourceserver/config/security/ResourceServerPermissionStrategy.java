package org.imd.oauth2.resourceserver.config.security;

import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Permission;

import java.util.HashMap;
import java.util.Map;

public class ResourceServerPermissionStrategy extends DefaultPermissionGrantingStrategy {
    private static final Map<Permission, CumulativePermission> MATRIX = new HashMap<>();

    static {
        final CumulativePermission cpAdministration = new CumulativePermission();
        cpAdministration.set(BasePermission.ADMINISTRATION);
        cpAdministration.set(BasePermission.DELETE);
        cpAdministration.set(BasePermission.CREATE);
        cpAdministration.set(BasePermission.WRITE);
        cpAdministration.set(BasePermission.READ);

        final CumulativePermission cpDelete = new CumulativePermission();
        cpDelete.set(BasePermission.DELETE);
        cpDelete.set(BasePermission.READ);

        final CumulativePermission cpCreate = new CumulativePermission();
        cpCreate.set(BasePermission.CREATE);
        cpCreate.set(BasePermission.WRITE);
        cpCreate.set(BasePermission.DELETE);
        cpCreate.set(BasePermission.READ);

        final CumulativePermission cpWrite = new CumulativePermission();
        cpWrite.set(BasePermission.WRITE);
        cpWrite.set(BasePermission.READ);

        final CumulativePermission cpRead = new CumulativePermission();
        cpRead.set(BasePermission.READ);

        MATRIX.put(BasePermission.ADMINISTRATION, cpAdministration);
        MATRIX.put(BasePermission.DELETE, cpDelete);
        MATRIX.put(BasePermission.CREATE, cpCreate);
        MATRIX.put(BasePermission.WRITE, cpWrite);
        MATRIX.put(BasePermission.READ, cpRead);
    }

    public ResourceServerPermissionStrategy(AuditLogger auditLogger) {
        super(auditLogger);
    }

    @Override
    protected boolean isGranted(AccessControlEntry ace, Permission p) {
        final CumulativePermission cp = MATRIX.get(p);
        if (cp != null) {
            return compare(cp, p);
        }

        return ace.getPermission().getMask() == p.getMask();
    }

    private boolean compare(final CumulativePermission master, final Permission pretender) {
        return (master.getMask() & pretender.getMask()) != 0;
    }
}
