package org.imd.oauth2.resourceserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AclConfig {

    private static final String EMPTY_ROLE_PREFIX = "";

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DataSource dataSource;

    @Bean
    public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
        // TODO: CHECK WHETHER IT IS APPROPRIATELY USED
        final RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("BLOG_ADMIN > BLOG_USER \n");

        final AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService());

        final DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setDefaultRolePrefix(EMPTY_ROLE_PREFIX);
        expressionHandler.setRoleHierarchy(roleHierarchy);
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }

    // We expose GrantedAuthorityDefaults using a static method to ensure that Spring publishes it
    // before it initializes Spring Security’s method security @Configuration classes.
    //
    // Prefix authorities with empty space, so they can be used without ROLE_ prefix or SCOPE_ prefix.
    // They look exactly as they are define in the token.

    @Bean
    static public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        // can be also configured with http.oauth2ResourceServer().jwt(Customizer.withDefaults())
        // if we change the customizer and put for JwtConverter instance the appropriate
        // configured here jwtAuthenticationConverter

        final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(EMPTY_ROLE_PREFIX);

        final JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    public JdbcMutableAclService aclService() {
        final JdbcMutableAclService aclService = new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
        aclService.setSidIdentityQuery("SELECT currval('acl_sid_id_seq')");
        aclService.setClassIdentityQuery("SELECT currval('acl_class_id_seq')");

        return aclService;
    }

    @Bean
    public LookupStrategy lookupStrategy() {
        return new BasicLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(), permissionGrantingStrategy());
    }

    @Bean
    public AclCache aclCache() {
        return new SpringCacheBasedAclCache(
                cacheManager.getCache("aclCache"),
                permissionGrantingStrategy(),
                aclAuthorizationStrategy()
        );
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger()) {
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
        };
    }

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
