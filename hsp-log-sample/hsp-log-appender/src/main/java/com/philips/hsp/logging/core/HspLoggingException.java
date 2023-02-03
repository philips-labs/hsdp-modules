package com.philips.hsp.logging.core;

public class HspLoggingException extends RuntimeException {

    public HspLoggingException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
