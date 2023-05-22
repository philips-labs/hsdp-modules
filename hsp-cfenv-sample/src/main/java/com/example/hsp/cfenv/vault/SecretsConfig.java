package com.example.hsp.cfenv.vault;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;

@Configuration
@Log4j2
public class SecretsConfig {
    private static final String VAULT_KEY_PATH = "/data";

    @Autowired
    private VaultTemplate vaultTemplate;

    @Value("${spring.cloud.vault.secret-path}")
    private String vaultSecretPath;
    @Value("${hsp.iam.client_id}")
    private String hspClientId;
    @Value("${hsp.iam.client_secret}")
    private String hspClientSecret;
    @Value("${hsp.audit.shared_key}")
    private String hspSharedKey;
    @Value("${hsp.audit.secret_key}")
    private String hspSecretKey;

    @Bean
    public Secrets getSecrets() {
        try {
            VaultResponseSupport<Secrets> response = vaultTemplate
                    .read(vaultSecretPath + VAULT_KEY_PATH, Secrets.class);
            if (null == response) {
                log.warn("Failed to load vault secrets from path - {}", vaultSecretPath);
                return defaultSecrets();
            }
            log.info("Retrieved secrets from Cloud Vault successfully");
            return response.getData();
        } catch (Exception ex) {
            log.warn("Vault template failure, error={}", ex.getMessage());
            return defaultSecrets();
        }
    }

    private Secrets defaultSecrets() {
        log.warn("Loading default secrets from application.properties");
        Secrets secrets = new Secrets();
        secrets.setClientId(hspClientId);
        secrets.setClientSecret(hspClientSecret);
        secrets.setSharedKey(hspSharedKey);
        secrets.setSecretKey(hspSecretKey);
        return secrets;
    }
}
