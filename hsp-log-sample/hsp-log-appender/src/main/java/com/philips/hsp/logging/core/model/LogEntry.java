package com.philips.hsp.logging.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public class LogEntry {

    private String category = "TraceLog";
    private String eventId;
    private String applicationVersion;
    private String component;
    private String applicationName;
    private String applicationInstance;
    private String serverName;
    private String transactionId;
    private String serviceName;
    private String logTime;
    private String originatingUser;
    private LogData logData;
    private String severity;
    private String traceId;
    private String spanId;
    private Map<String, String> custom;
    private String resourceType = "LogEvent";
    private String id;
}
