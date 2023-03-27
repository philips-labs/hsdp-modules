package com.philips.hsp.audit.core;

import io.avaje.inject.BeanScope;

import java.util.Map;

public class AuditBeanInitializer {
    private final BeanScope beanScope;

    public AuditBeanInitializer(Map<String, String> kvProps) {
        this.beanScope = BeanScope.builder()
                .bean(AuditProperties.class, new AuditProperties(kvProps))
                .shutdownHook(true).build();
    }

    public void close() {
        this.beanScope.close();
    }

    public AuditClient auditClient() {
        return this.beanScope.get(AuditClient.class);
    }
}
