package com.philips.hsp.audit.core;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.dstu2.resource.AuditEvent;
import ca.uhn.fhir.model.dstu2.valueset.AuditEventSourceTypeEnum;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.parser.JsonParser;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.cfg.DatatypeFeature;
import com.google.gson.Gson;
import io.avaje.inject.PreDestroy;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Singleton
public class AuditClient {
    private static final DateTimeFormatter SIGNED_DATE_FORMAT = DateTimeFormatter.ISO_INSTANT;
    private static final String AUDIT_API_VER = "2";
    private static final String HEADER_API_VERSION = "API-Version";
    private static final String HEADER_SIGNED_DATE = "SignedDate";
    private static final String HEADER_HSDP_API_SIGNATURE = "HSDP-API-Signature";
    private static final String AUDIT_BASE_PATH = "/core/audit/AuditEvent";
    private final FhirContext fhirContext = FhirContext.forDstu2();

    private final HspApiSigning apiSigning;
    private final AuditProperties properties;
    private final ObjectMapper mapper = new ObjectMapper();
    private final CloseableHttpClient httpClient = HttpClients.createSystem();

    @PreDestroy
    public void destroy() throws IOException {
        httpClient.close();
    }

    public AuditClient(HspApiSigning apiSigning, AuditProperties properties) {
        this.apiSigning = apiSigning;
        this.properties = properties;
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
            HttpEntity body = new ByteArrayEntity(auditMessage.getBytes(StandardCharsets.UTF_8));
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