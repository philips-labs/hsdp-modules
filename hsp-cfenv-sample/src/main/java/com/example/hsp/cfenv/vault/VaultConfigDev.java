package com.example.hsp.cfenv.vault;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

@Configuration
@Profile("default")
@Log4j2
public class VaultConfigDev extends AbstractVaultConfiguration {

    @Value("${spring.cloud.vault.host}")
    private String endpoint;
    @Value("${spring.cloud.vault.token}")
    private String vaultToken;

    @Override
    public VaultEndpoint vaultEndpoint() {
        log.info("Profile(DEV): Setting vault endpoint: {}", endpoint);
        return new VaultEndpoint();
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        log.info("Profile(DEV): Vault token authentication with token={}", vaultToken);
        return new TokenAuthentication(vaultToken);
    }
}
