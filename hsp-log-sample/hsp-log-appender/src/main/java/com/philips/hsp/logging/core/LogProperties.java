package com.philips.hsp.logging.core;

import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;
import java.util.Map;

@Getter @Setter
public class LogProperties {
    private static final String ALGORITHM = "HmacSHA256";
    private String logIngestUrl;
    private String sharedKey;
    private String secretKey;
    private String productKey;
    private String applicationName;
    private String applicationVersion;
    private String applicationInstance;
    private String serviceName;
    private String category;
    private String serverName;

    @Inject public LogProperties() {}

    public void init(Map<String, String> properties) {
        this.productKey = properties.get("productKey");
        this.applicationName = properties.get("applicationName");
        this.applicationVersion = properties.get("applicationVersion");
        this.applicationInstance = properties.getOrDefault("applicationInstance", "1");
        this.serverName = properties.getOrDefault("serverName", "na");
        this.serviceName = properties.getOrDefault("serviceName", "na");
        this.category = properties.getOrDefault("category", "na");
        this.sharedKey = properties.get("sharedKey");
        this.secretKey = properties.get("secretKey");
        this.logIngestUrl = properties.get("logIngestUrl");
    }

    public String getAlgorithm() {
        return ALGORITHM;
    }
}
