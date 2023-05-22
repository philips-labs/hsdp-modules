package com.example.hsp.cfenv.vault;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Component
public class Secrets {

    @JsonProperty("hsp.iam.client_id")
    private String clientId;
    @JsonProperty("hsp.iam.client_secret")
    private String clientSecret;
    @JsonProperty("hsp.audit.shared_key")
    private String sharedKey;
    @JsonProperty("hsp.audit.secret_key")
    private String secretKey;
}
