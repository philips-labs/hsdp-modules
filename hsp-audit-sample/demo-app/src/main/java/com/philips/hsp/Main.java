package com.philips.hsp;

import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.AuditEvent;
import ca.uhn.fhir.model.dstu2.valueset.AuditEventActionEnum;
import ca.uhn.fhir.model.dstu2.valueset.AuditEventOutcomeEnum;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierTypeCodesEnum;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum;
import ca.uhn.fhir.model.primitive.InstantDt;
import com.philips.hsp.audit.core.AuditBeanInitializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Main {
    private static final String SYSTEM = "http://hsdp.io/core/audit";

    public static void main(String[] args) {
        System.out.println("HSP audit test app");
        Main app = new Main();
        Map<String, String> kvProps = app.loadProperties();
        AuditBeanInitializer beanInitializer = new AuditBeanInitializer(kvProps);
        beanInitializer.auditClient().send(
                new AuditEvent()
                        .setEvent(
                             new AuditEvent.Event()
                                     .setType(new CodingDt(SYSTEM, "TYPE101"))
                                     .setSubtype(List.of(
                                             new CodingDt(SYSTEM, "SUBTYPE101")
                                     ))
                                     .setAction(AuditEventActionEnum.CREATE)
                                     .setOutcome(AuditEventOutcomeEnum.SUCCESS)
                                     .setOutcomeDesc("Test resource created successfully")
                                     .setDateTime(new InstantDt(new Date()))
                        )
                        .setSource(
                                new AuditEvent.Source()
                                        .setSite("localhost")
                                        .setIdentifier(new IdentifierDt()
                                                .setValue(kvProps.get("applicationName"))
                                                .setType(IdentifierTypeCodesEnum.PLACER_IDENTIFIER)
                                                .setUse(IdentifierUseEnum.USUAL)
                                                .setSystem(SYSTEM))
                        )
                        .setParticipant(Collections.singletonList(
                                new AuditEvent.Participant()
                                        .setUserId(new IdentifierDt()
                                                .setValue(UUID.randomUUID().toString())
                                                .setSystem(SYSTEM)
                                                .setUse(IdentifierUseEnum.USUAL))
                                        .setRequestor(true)
                                        .setName("ClientApp")
                        ))
        );
        beanInitializer.close();
    }

    private Map<String, String> loadProperties() {
        Map<String, String> kvProps = new HashMap<>();
        ClassLoader loader = getClass().getClassLoader();
        Properties properties = new Properties();
        try (InputStream resource = loader.getResourceAsStream("application.properties")) {
            properties.load(resource);
            Iterator<?> keysIter = properties.keys().asIterator();
            while (keysIter.hasNext()) {
                String key = keysIter.next().toString();
                kvProps.put(key, properties.getProperty(key));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return kvProps;
    }

}