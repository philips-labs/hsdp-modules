package com.philips.hsp.logging.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.philips.hsp.logging.core.model.Entry;
import com.philips.hsp.logging.core.model.LogMessage;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LogSender {

    private static final DateTimeFormatter SIGNED_DATE_FORMAT = DateTimeFormat
            .forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final String HEADER_API_VERSION = "API-Version";
    private static final String HEADER_SIGNED_DATE = "SignedDate";
    private static final String HEADER_HSDP_API_SIGNATURE = "HSDP-API-Signature";

    private final LogApiSigning apiSigning;
    private final LogProperties credentials;
    private final ObjectMapper mapper = new ObjectMapper();

    @Inject
    public LogSender(LogApiSigning apiSigning, LogProperties credentials) {
        this.apiSigning = apiSigning;
        this.credentials = credentials;
    }

    public LogProperties getCredentials() {
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
            HttpEntity body = new ByteArrayEntity(message.getBytes(StandardCharsets.UTF_8),
                    ContentType.APPLICATION_JSON);
            post.setEntity(body);
            httpClient.execute(
                    post, response -> {
                        int status = response.getCode();
                        if ((status != 201) && (status != 200)) {
                            System.out.printf("HSP Log HttpPost event failed, code=%d, message=%s%n",
                                    status, response.getReasonPhrase());
                        }
                        return null;
                    });
        } catch (Exception ex) {
            System.err.printf("HSP Log HttpClient error occurred: %s%n", ex.getMessage());
        }
    }
}
