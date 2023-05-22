package com.philips.hsp.logging.core;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = LogModule.class)
public interface LogClientComponent {
    LogProcessor logProcessor();
}
