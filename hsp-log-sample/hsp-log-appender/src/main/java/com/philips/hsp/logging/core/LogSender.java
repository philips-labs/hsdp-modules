package com.philips.hsp.logging.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.philips.hsp.logging.core.model.Entry;
import com.philips.hsp.logging.core.model.LogMessage;
import io.avaje.inject.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Singleton
public class LogSender {

    private static final DateTimeFormatter SIGNED_DATE_FORMAT = DateTimeFormat
            .forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final String HMAC_SHA_256 = "HmacSHA256";
    private static final String HEADER_API_VERSION = "API-Version";
    private static final String HEADER_SIGNED_DATE = "SignedDate";
    private static final String HEADER_HSDP_API_SIGNATURE = "HSDP-API-Signature";

    private final LogApiSigning apiSigning;
    private final LogCredentials credentials;
    private final ObjectMapper mapper = new ObjectMapper();
    private final CloseableHttpClient httpClient = HttpClients.createSystem();

    @PreDestroy
    public void destroy() throws IOException {
        httpClient.close();
    }

    public LogSender(LogApiSigning apiSigning, LogCredentials credentials) {
        this.apiSigning = apiSigning;
        this.credentials = credentials;
    }

    public LogCredentials getCredentials() {
        return this.credentials;
    }

    public void send(List<Entry> entries) {
        sendInternal(buildLogMessage(entries));
    }

    private LogMessage buildLogMessage(List<Entry> entries) {
        LogMessage message = new LogMessage();
        message.setEntry(entries);
        message.setTotal(entries.size());
        message.setProductKey(credentials.getProductKey());
        message.setResourceType("Bundle");
        return message;
    }

    private void sendInternal(LogMessage logMessage) {
        try (CloseableHttpClient httpClient = HttpClients.createSystem()){
            HttpPost post = new HttpPost(credentials.getLogIngestUrl());
            String signedDate = SIGNED_DATE_FORMAT.print(DateTime.now(DateTimeZone.UTC));
            String signature = apiSigning.getSignature(signedDate);
            post.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
            post.addHeader(HEADER_API_VERSION, "1");
            post.addHeader(HEADER_SIGNED_DATE, signedDate);
            post.addHeader(HEADER_HSDP_API_SIGNATURE, signature);
            String message = mapper.writeValueAsString(logMessage);
            HttpEntity body = new ByteArrayEntity(message.getBytes(StandardCharsets.UTF_8));
            post.setEntity(body);
            try (CloseableHttpResponse httpResponse = httpClient.execute(post)) {
                int status = httpResponse.getStatusLine().getStatusCode();
                if ((status != 201) && (status != 200)) {
                    System.out.printf("HSP Log HttpPost event failed, code=%d, message=%s%n",
                            status, httpResponse.getStatusLine().getReasonPhrase());
                }
            } catch (Exception ex) {
                System.err.println("HSP Log HttpResponse error occurred: " + ex.getMessage());
            }

        } catch (Exception ex) {
            System.err.printf("HSP Log HttpClient error occurred: %s%n", ex.getMessage());
        }
    }
}
