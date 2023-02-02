package org.imd.oauth2.authorizationserver.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
public class SecuritySettings {
    @Value("${server.port}")
    private String authorizationServerPort;
}
