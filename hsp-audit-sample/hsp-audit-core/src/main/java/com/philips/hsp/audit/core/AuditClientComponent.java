package com.philips.hsp.audit.core;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = AuditModule.class)
public interface AuditClientComponent {
    AuditClient auditClient();
}
