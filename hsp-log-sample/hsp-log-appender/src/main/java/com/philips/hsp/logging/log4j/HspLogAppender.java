package com.philips.hsp.logging.log4j;

import com.philips.hsp.logging.core.LogCredentials;
import com.philips.hsp.logging.core.LogProcessor;
import io.avaje.inject.BeanScope;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Plugin(name = "HspLogAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
public class HspLogAppender extends AbstractAppender {

    private final BeanScope beanScope;

    protected HspLogAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
        Map<String, String> kvPairs = Arrays.stream(getPropertyArray())
                .filter(e -> !e.getRawValue().isEmpty())
                .collect(Collectors.toMap(Property::getName, Property::getRawValue));
        this.beanScope = BeanScope.builder()
                .bean(LogCredentials.class,
                new LogCredentials(kvPairs))
                .shutdownHook(true).build();
    }

    @Override
    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        beanScope.close();
        this.setStopped();
        return true;
    }

    @PluginFactory
    public static HspLogAppender createAppender(
            @PluginAttribute("name") @Required(message = "Parameter 'name' is required") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("productKey") @Required(message = "Parameter 'productKey' is required") String logProductKey,
            @PluginAttribute("sharedKey") @Required(message = "Parameter 'sharedKey' is required") String logSharedKey,
            @PluginAttribute("secretKey") @Required(message = "Parameter 'secretKey' is required") String logSecretKey,
            @PluginAttribute("logIngestUrl") @Required(message = "Parameter 'logIngestUrl' is required") String logIngestUrl,
            @PluginAttribute("applicationName") String appName,
            @PluginAttribute("applicationVersion") String appVersion,
            @PluginAttribute("applicationInstance") String appInstance,
            @PluginAttribute("serviceName") String serviceName,
            @PluginAttribute("serverName") String serverName,
            @PluginAttribute("category") String category) {
        if (layout == null) {
            layout = PatternLayout.newBuilder()
                    .withPattern("%d{ISO8601} [%t] %-5p %X{id} %X{traceId} %X{spanId} %c{1.}.%M:%L - %msg%n%ex{separator(\\u2028)}")
                    .build();
        }
        try {
            if (serverName == null) {
                serverName = InetAddress.getLocalHost().getHostName();
            }
        } catch (UnknownHostException e) {
            System.err.printf("Failed to get hostname: %s", e.getMessage());
        }
        Property[] properties = new Property[] {
                Property.createProperty("productKey", logProductKey),
                Property.createProperty("sharedKey", logSharedKey),
                Property.createProperty("secretKey", logSecretKey),
                Property.createProperty("logIngestUrl", logIngestUrl),
                Property.createProperty("applicationName", appName),
                Property.createProperty("applicationVersion", appVersion),
                Property.createProperty("applicationInstance", appInstance),
                Property.createProperty("serviceName", serviceName),
                Property.createProperty("serverName", serverName),
                Property.createProperty("category", category),
        };

        return new HspLogAppender(name, filter, layout, false, properties);
    }

    @Override
    public void append(LogEvent event) {
        LogProcessor logProcessor = beanScope.get(LogProcessor.class);
        final byte[] bytes = getLayout().toByteArray(event);
        logProcessor.process(event, bytes);
    }
}
