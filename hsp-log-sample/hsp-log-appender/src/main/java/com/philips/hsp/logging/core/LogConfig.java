package com.philips.hsp.logging.core;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.avaje.inject.InjectModule;

@Factory
public class LogConfig {

    private  static final String HMAC_SHA_256 = "HmacSHA256";

    private final LogCredentials credentials;

    @InjectModule(requires = LogCredentials.class)
    public LogConfig(LogCredentials credentials) {
        this.credentials = credentials;
    }

    @Bean
    LogApiSigning apiSigning() {
        return new LogApiSigning(credentials.getSharedKey(), credentials.getSecretKey(), HMAC_SHA_256);
    }

    @Bean
    LogSender logSender() {

        return new LogSender(apiSigning(), credentials);
    }

    @Bean
    public LogProcessor logProcessor() {
        return new LogProcessor(logSender());
    }
}
