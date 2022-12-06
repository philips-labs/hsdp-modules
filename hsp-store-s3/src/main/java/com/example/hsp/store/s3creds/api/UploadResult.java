package com.example.hsp.store.s3creds.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadResult {
    private String status;
    private String signedUrl;
    private String message;
}
