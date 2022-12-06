package com.example.hsp.store.s3creds.hsp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AwsCreds {
    private String accessKey;
    private String secretKey;
    private String sessionKey;
}
