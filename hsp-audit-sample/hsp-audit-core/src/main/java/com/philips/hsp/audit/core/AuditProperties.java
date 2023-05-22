package com.philips.hsp.audit.core;

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

    public AuditProperties() {}

    public void init(Map<String, String> kvProps) {
        this.productKey = kvProps.get("productKey");
        this.tenant = kvProps.get("tenant");
        this.applicationName = kvProps.get("applicationName");
        this.applicationVersion = kvProps.get("applicationVersion");
        this.applicationInstance = kvProps.getOrDefault("applicationInstance", "1");
        this.serverName = kvProps.getOrDefault("serverName", "na");
        this.serviceName = kvProps.getOrDefault("serviceName", "na");
        this.componentName = kvProps.getOrDefault("componentName", "na");
        this.sharedKey = kvProps.get("sharedKey");
        this.secretKey = kvProps.get("secretKey");
        this.auditUrl = kvProps.get("auditUrl");
    }

    public String getAlgorithm() {
        return ALGORITHM;
    }
}
