package com.example.hsp.cfenv.vault;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

import java.util.Map;

@Profile("cloud")
@Log4j2
public class HspVaultCfEnvProcessor implements CfEnvProcessor {

    private static final String HSDP_VAULT = "hsdp-vault";

    @Override
    public boolean accept(CfService cfService) {
        if (cfService.existsByLabelStartsWith(HSDP_VAULT)
                && cfService.existsByTagIgnoreCase("Vault")) {
            log.info("HSP Vault service binding available");
            return true;
        }
        return false;
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        properties.put("spring.cloud.vault.host", cfCredentials.getMap().get("endpoint"));
        properties.put("spring.cloud.vault.app-role.role-id", cfCredentials.getMap().get("role_id"));
        properties.put("spring.cloud.vault.app-role.secret-id", cfCredentials.getMap().get("secret_id"));
        String spaceSecretPath = cfCredentials.getMap().get("space_secret_path").toString();
        spaceSecretPath = StringUtils.replace(spaceSecretPath, "/v1/", "/");
        properties.put("spring.cloud.vault.secret-path", spaceSecretPath);
        log.info("CF Service Vault Host: {}", cfCredentials.getMap().get("endpoint"));
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder()
                .propertyPrefixes("spring.cloud.vault")
                .serviceName("HSP_VAULT")
                .build();
    }
}
