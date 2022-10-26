package com.example.hsp.store.s3creds.hsp;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Optional;

@Log4j2
public class HspS3Client {
    private static final long MIN_UPLOAD_PART_SIZE = 10 * 1024 * 1024;

    private final AmazonS3 awsS3;

    private final String bucket;

    public HspS3Client(AwsCreds awsCreds, String region, String bucket) {
        AWSCredentials awsCredential = new BasicSessionCredentials(awsCreds.getAccessKey(),
                awsCreds.getSecretKey(), awsCreds.getSessionKey());
        awsS3 = AmazonS3ClientBuilder.standard()
                .disableChunkedEncoding()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredential))
                .withRegion(region)
                .build();
        this.bucket = bucket;
    }

    public HspS3Client(AwsCreds awsCreds,
                       String region, String bucket,
                       String proxyHost, String proxyPort) {
        AWSCredentials awsCredential = new BasicSessionCredentials(awsCreds.getAccessKey(),
                awsCreds.getSecretKey(), awsCreds.getSessionKey());
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setProxyHost(proxyHost);
        configuration.setProxyPort(Integer.parseInt(proxyPort));
        awsS3 = AmazonS3ClientBuilder.standard()
                .disableChunkedEncoding()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredential))
                .withRegion(region)
                .withClientConfiguration(configuration).build();
        this.bucket = bucket;
    }

    public TransferManager getTransferManager() {
        return TransferManagerBuilder.standard().withS3Client(awsS3).withMinimumUploadPartSize(MIN_UPLOAD_PART_SIZE)
                .withMultipartUploadThreshold(MIN_UPLOAD_PART_SIZE).build(); //NOSONAR
    }

    public AmazonS3 getAwsS3Client() {
        return awsS3;//NOSONAR
    }

    public String getBucket() {
        return bucket;
    }

    public Optional<String> upload(String key, File file, String contentType) {
        try (InputStream fileStream = new FileInputStream(file)) {
            return upload(key, fileStream, contentType);
        } catch (IOException ex) {
            log.error("Failed to read file object, error={}", ex.getMessage());
            return Optional.empty();
        }
    }

    public Optional<String> upload(String key, InputStream fileStream, String contentType) {
        TransferManager transferManager = getTransferManager();
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            if (StringUtils.hasText(contentType)) {
                objectMetadata.setContentType(contentType);
            }
            objectMetadata.setContentLength(fileStream.available());
            Upload upload = transferManager.upload(bucket, key, fileStream, objectMetadata);
            UploadResult result = upload.waitForUploadResult();
            return Optional.of(result.getETag());
        } catch (SdkClientException | InterruptedException ex) {
            log.error("Failed to upload S3 object to bucket={}, key={}, error={}",
                    bucket, key, ex.getMessage());
            return Optional.empty();
        } catch (IOException ex) {
            log.error("Failed to read file stream, error={}", ex.getMessage());
            return Optional.empty();
        } finally {
            transferManager.shutdownNow(false);
        }
    }

    public void download(String key, File file) {
        TransferManager transferManager = getTransferManager();
        try {
            Download download = transferManager.download(bucket, key, file);
            download.waitForCompletion();
        } catch (SdkClientException | InterruptedException ex) {
            log.error("Failed to download S3 object from bucket={}, key={}, error={}",
                    bucket, key, ex.getMessage());
        } finally {
            transferManager.shutdownNow(false);
        }
    }

    public StreamingResponseBody download(String key) {
        S3Object s3Object = getAwsS3Client().getObject(bucket, key);

        try (S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent()) {
            final StreamingResponseBody data = outputStream -> {
                int nBytesToWrite = 0;
                byte[] bytes = new byte[1024];
                while ((nBytesToWrite = s3ObjectInputStream.read(bytes, 0, bytes.length)) != -1) {
                    outputStream.write(bytes);
                }
            };
            return data;
        } catch (IOException ex) {
            log.error("Failed to read S3 object stream, error={}", ex.getMessage());
        }
        return null;
    }

    public void delete(String key) {
        try {
            getAwsS3Client().deleteObject(bucket, key);
        } catch (SdkClientException ex) {
            log.error("AwsSdk: Failed to delete S3 object from bucket={}, key={}, error={}",
                    bucket, key, ex.getMessage());
        }
    }

    public URL generatePreSignedUrl(String bucketKeyPath, Date expirationTime) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, bucketKeyPath)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expirationTime);
        return getAwsS3Client().generatePresignedUrl(generatePresignedUrlRequest);
    }


}
