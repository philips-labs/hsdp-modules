package com.philips.hsp.audit.core;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Module
public class AuditModule {
    final Map<String, String> properties = new HashMap<>();

    public AuditModule(Map<String, String> kvProps) {
        this.properties.putAll(kvProps);
    }

    @Provides
    @Singleton
    public HspApiSigning provideApiSigning() {
        return new HspApiSigning(provideAuditProperties());
    }

    @Provides
    @Singleton
    public AuditProperties provideAuditProperties() {
        AuditProperties auditProperties = new AuditProperties();
        auditProperties.init(properties);
        return auditProperties;
    }
}
