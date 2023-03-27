package com.philips.hsp.audit.core;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.avaje.inject.InjectModule;
import io.avaje.inject.spi.DependencyMeta;

@Factory
public class AuditConfig {

    private final AuditProperties auditProperties;

    @InjectModule(requires = AuditProperties.class)
    public AuditConfig(AuditProperties properties) {
        auditProperties = properties;
    }

    @Bean
    HspApiSigning apiSigning() {
        return new HspApiSigning(auditProperties);
    }

    @Bean
    AuditClient auditClient() {
        return new AuditClient(apiSigning(), auditProperties);
    }
}