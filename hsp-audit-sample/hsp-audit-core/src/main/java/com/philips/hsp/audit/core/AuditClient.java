package com.philips.hsp.audit.core;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.dstu2.resource.AuditEvent;
import ca.uhn.fhir.model.primitive.StringDt;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuditClient {
    private static final DateTimeFormatter SIGNED_DATE_FORMAT = DateTimeFormatter.ISO_INSTANT;
    private static final String AUDIT_API_VER = "2";
    private static final String HEADER_API_VERSION = "API-Version";
    private static final String HEADER_SIGNED_DATE = "SignedDate";
    private static final String HEADER_HSDP_API_SIGNATURE = "HSDP-API-Signature";
    private static final String AUDIT_BASE_PATH = "/core/audit/AuditEvent";
    private final FhirContext fhirContext = FhirContext.forDstu2();

    HspApiSigning apiSigning;
    AuditProperties properties;

    @Inject
    public AuditClient(HspApiSigning apiSigning, AuditProperties auditProperties) {
        this.apiSigning = apiSigning;
        this.properties = auditProperties;
    }
    public void send(AuditEvent event) {
        try {
            sendInternal(buildAuditMessage(event));
        } catch (JsonProcessingException e) {
            throw new HspAuditException("Failed to parse AuditEvent json.", e);
        }
    }

    private void sendInternal(String auditMessage) {
        try (CloseableHttpClient httpClient = HttpClients.createSystem()) {
            HttpPost post = new HttpPost(properties.getAuditUrl()+AUDIT_BASE_PATH);
            String signedDate = SIGNED_DATE_FORMAT.format(ZonedDateTime.now());
            String signature = apiSigning.getSignature(signedDate);
            post.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
            post.addHeader(HEADER_API_VERSION, AUDIT_API_VER);
            post.addHeader(HEADER_SIGNED_DATE, signedDate);
            post.addHeader(HEADER_HSDP_API_SIGNATURE, signature);
            HttpEntity body = new ByteArrayEntity(auditMessage.getBytes(StandardCharsets.UTF_8),
                    ContentType.APPLICATION_JSON);
            post.setEntity(body);
            httpClient.execute(post, response -> {
                int status = response.getCode();
                if ((status != 201) && (status != 200)) {
                    System.out.printf("HSP Audit HttpPost event failed, code=%d, message=%s%n",
                            status, response.getReasonPhrase());
                }
               return null;
            });
        } catch (Exception ex) {
            System.err.printf("HSP Audit HttpClient error occurred: %s%n", ex.getMessage());
        }
    }

    private String buildAuditMessage(AuditEvent event) throws JsonProcessingException {
        event.setId(UUID.randomUUID().toString());
        addSourceExtension(event);
        return fhirContext.newJsonParser().encodeResourceToString(event);
    }

    private void addSourceExtension(AuditEvent event) {
        String sourceUrl = "/audit/source/extensions";
        List<ExtensionDt> extensionDts = new ArrayList<>();
        extensionDts.add(new ExtensionDt(false,
                "applicationName", new StringDt(properties.getApplicationName())));
        extensionDts.add(new ExtensionDt(false,
                "applicationVersion", new StringDt(properties.getApplicationVersion())));
        extensionDts.add(new ExtensionDt(false,
                "productKey", new StringDt(properties.getProductKey())));
        extensionDts.add(new ExtensionDt(false,
                "tenant", new StringDt(properties.getTenant())));
        extensionDts.add(new ExtensionDt(false,
                "transactionId", new StringDt(UUID.randomUUID().toString())));
        event.getSource()
                .addUndeclaredExtension(false, sourceUrl)
                .getUndeclaredExtensions()
                .addAll(extensionDts);
    }
}