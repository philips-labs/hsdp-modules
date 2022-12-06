package com.example.hsp.store.s3creds.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
public class S3Controller {

    @Autowired
    S3Service s3Service;

    @PostMapping("/s3/upload")
    public ResponseEntity<UploadResult> upload(@RequestParam("file")MultipartFile file,
                                               @RequestHeader("S3-Access-Key") String accessKey,
                                               @RequestHeader("S3-Secret-Key") String secretKey,
                                               @RequestHeader("S3-Session-Key") String sessionKey) {
        UploadResult response = s3Service.upload(file, accessKey, secretKey, sessionKey);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/s3/download/{file}", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<StreamingResponseBody> download(@PathVariable("file") String fileName,
                                                          @RequestHeader("S3-Access-Key") String accessKey,
                                                          @RequestHeader("S3-Secret-Key") String secretKey,
                                                          @RequestHeader("S3-Session-Key") String sessionKey) {
        return new ResponseEntity<>(
                s3Service.download(fileName, accessKey, secretKey, sessionKey),
                HttpStatus.OK);
    }
}
