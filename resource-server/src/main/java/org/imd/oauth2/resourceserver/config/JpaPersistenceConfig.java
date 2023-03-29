package org.imd.oauth2.resourceserver.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "org.imd.oauth2.resourceserver.model.repos")
// @PropertySource("classpath:com.baeldung.acl.datasource.properties") ?
@EntityScan(basePackages={ "org.imd.oauth2.resourceserver.model.entity" })
public class JpaPersistenceConfig {

}