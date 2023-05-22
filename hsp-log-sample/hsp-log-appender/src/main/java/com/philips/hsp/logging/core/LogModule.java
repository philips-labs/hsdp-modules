package com.philips.hsp.logging.core;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Module
public class LogModule {
    final Map<String, String> properties = new HashMap<>();

    public LogModule(Map<String, String> kvProps) {
        this.properties.putAll(kvProps);
    }

    @Provides
    @Singleton
    public LogApiSigning provideApiSigning() {
        return new LogApiSigning(provideLogProperties());
    }

    @Provides
    @Singleton
    public LogSender provideLogSender() {
        return new LogSender(provideApiSigning(), provideLogProperties());
    }

    @Provides
    @Singleton
    public LogProperties provideLogProperties() {
        LogProperties logProperties = new LogProperties();
        logProperties.init(properties);
        return logProperties;
    }
}
