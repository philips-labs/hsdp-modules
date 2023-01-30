package com.philips.hsp.logging.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter @Setter
public class LogMessage {

    private int total;
    private List<Entry> entry;
    private String type = "transaction";
    private String resourceType;
    private String productKey;
}
