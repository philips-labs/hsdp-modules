package com.example.hsp.cfenv.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class Version {
    String name;
    String description;
    String version;
    String build;
    String environment;
    String instance;
}
