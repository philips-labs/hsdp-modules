import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.AuditEvent;
import ca.uhn.fhir.model.dstu2.valueset.AuditEventActionEnum;
import ca.uhn.fhir.model.dstu2.valueset.AuditEventOutcomeEnum;
import com.philips.hsp.audit.core.AuditClient;
import com.philips.hsp.audit.core.AuditClientComponent;
import com.philips.hsp.audit.core.AuditModule;
import com.philips.hsp.audit.core.DaggerAuditClientComponent;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Audit core tryout...");
        Main app = new Main();
        AuditClientComponent auditClientComponent = DaggerAuditClientComponent.builder()
                .auditModule(new AuditModule(app.loadProperties())).build();
        AuditClient client = auditClientComponent.auditClient();
        client.send(new AuditEvent()
                .setEvent(
                        new AuditEvent.Event().setAction(AuditEventActionEnum.CREATE)
                                .setOutcome(AuditEventOutcomeEnum.SUCCESS)
                                .setOutcomeDesc("Test event created successfully")
                )
                .setSource(
                        new AuditEvent.Source().setSite("localhost")
                                .setIdentifier(new IdentifierDt().setValue("testApp"))
                )
                .setParticipant(Arrays.asList(
                        new AuditEvent.Participant().setName("IntelliJ")
                                .setRequestor(true)
                                .setUserId(new IdentifierDt().setValue("USR101"))
                )));
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
