package com.philips.hsp.audit.core;

import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class AuditProperties {
    private static final String ALGORITHM = "HmacSHA256";
    private String auditUrl;
    private String sharedKey;
    private String secretKey;
    private String productKey;
    private String tenant;
    private String applicationName;
    private String applicationVersion;
    private String applicationInstance;
    private String serviceName;
    private String componentName;
    private String serverName;

    public AuditProperties(Map<String, String> properties) {
        this.productKey = properties.get("productKey");
        this.tenant = properties.get("tenant");
        this.applicationName = properties.get("applicationName");
        this.applicationVersion = properties.get("applicationVersion");
        this.applicationInstance = properties.getOrDefault("applicationInstance", "1");
        this.serverName = properties.getOrDefault("serverName", "na");
        this.serviceName = properties.getOrDefault("serviceName", "na");
        this.componentName = properties.getOrDefault("componentName", "na");
        this.sharedKey = properties.get("sharedKey");
        this.secretKey = properties.get("secretKey");
        this.auditUrl = properties.get("auditUrl");
    }

    public String getAlgorithm() {
        return ALGORITHM;
    }
}
