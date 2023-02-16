package com.example.hsp.cfenv.jdbc;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public class HspPSqlEnvProcessor implements CfEnvProcessor {

    private static final String HSDP_RDS = "hsdp-rds";

    @Override
    public boolean accept(CfService cfService) {
        if (cfService.existsByLabelStartsWith(HSDP_RDS)
                && cfService.existsByTagIgnoreCase("PostgreSQL")
                && cfService.existsByUriSchemeStartsWith("postgres")) {
            log.info("HSP PostgreSQL database service binding available");
            return true;
        }
        return false;
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        properties.put("spring.datasource.url",
                String.format("jdbc:postgresql://%s:%s/%s",
                        cfCredentials.getHost(),
                        cfCredentials.getPort(),
                        cfCredentials.getMap().get("db_name")));
        properties.put("spring.datasource.username", cfCredentials.getUsername());
        properties.put("spring.datasource.password", cfCredentials.getPassword());
        properties.put("spring.datasource.driver-class-name", "org.postgresql.Driver");
        properties.put("spring.datasource.hostname", cfCredentials.getMap().get("hostname"));
        log.info("CF Service RDS Host: {}", cfCredentials.getMap().get("hostname"));
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder()
                .propertyPrefixes("spring.datasource")
                .serviceName("HSP_RDS_POSTGRESQL")
                .build();
    }
}
