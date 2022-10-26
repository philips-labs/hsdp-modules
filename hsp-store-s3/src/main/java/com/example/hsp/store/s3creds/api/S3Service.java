package com.example.hsp.store.s3creds.api;

import com.example.hsp.store.s3creds.hsp.AwsCreds;
import com.example.hsp.store.s3creds.hsp.HspS3Client;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
@Log4j2
public class S3Service {

    @Value("${hsp.cdl.s3.region}")
    String s3Region;
    @Value("${hsp.cdl.s3.bucket}")
    String s3Bucket;
    @Value("${hsp.cdl.s3.upload-key}")
    String s3UploadKey;

    @Value("${hsp.cdl.s3.download-key}")
    String s3DownloadKey;

    public UploadResult upload(MultipartFile file, String accessKey, String secretKey, String sessionKey) {
        UploadResult result = new UploadResult();
        String fName = file.getOriginalFilename();
        String key = s3UploadKey + fName;
        HspS3Client hspS3Client = new HspS3Client(new AwsCreds(accessKey, secretKey, sessionKey),
                s3Region, s3Bucket);
        try (InputStream fileStream = file.getInputStream()) {
            Optional<String> response = hspS3Client.upload(key, fileStream, file.getContentType());
            if (response.isPresent()) {
                result.setStatus("SUCCESS");
                result.setMessage(response.get());
            }
            URL url = hspS3Client.generatePreSignedUrl(key, add10MinsFromNow());
            result.setSignedUrl(url.toString());
        } catch (IOException ex) {
            log.error("Failed to read the source file to upload, error={}", ex.getMessage());
        }
        return result;
    }

    private Date add10MinsFromNow() {
        Date current = DateTime.now().toDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.MINUTE, 10);
        return calendar.getTime();
    }

    public StreamingResponseBody download(String fileName, String accessKey, String secretKey, String sessionKey) {
        String key = s3DownloadKey + fileName;
        HspS3Client hspS3Client = new HspS3Client(new AwsCreds(accessKey, secretKey, sessionKey),
                s3Region, s3Bucket);
        return hspS3Client.download(key);
    }

}
