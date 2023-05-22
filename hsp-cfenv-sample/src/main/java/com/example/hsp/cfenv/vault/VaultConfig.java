package com.example.hsp.cfenv.vault;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.authentication.AppRoleAuthentication;
import org.springframework.vault.authentication.AppRoleAuthenticationOptions;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@Profile("cloud")
@Log4j2
public class VaultConfig extends AbstractVaultConfiguration {

    @Value("${spring.cloud.vault.app-role.role-id}")
    private String roleId;
    @Value("${spring.cloud.vault.app-role.secret-id}")
    private String secretId;
    @Value("${spring.cloud.vault.host}")
    private String endpoint;

    @Override
    public VaultEndpoint vaultEndpoint() {
        log.info("Profile(CLOUD): Setting vault endpoint: {}", endpoint);
        try {
            return VaultEndpoint.from(new URI(endpoint));
        } catch (URISyntaxException e) {
            log.warn("Failed to initialize Vault endpoint: {}", e.getMessage());
        }
        return new VaultEndpoint();
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        log.info("Profile(CLOUD): Vault AppRole authentication with role_id={}", roleId);
        AppRoleAuthenticationOptions options = AppRoleAuthenticationOptions.builder()
                .roleId(AppRoleAuthenticationOptions.RoleId.provided(roleId))
                .secretId(AppRoleAuthenticationOptions.SecretId.provided(secretId)).build();
        return new AppRoleAuthentication(options, restOperations());
    }
}
