package com.philips.hsp.audit.core;

public class HspAuditException extends RuntimeException {
    public HspAuditException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
