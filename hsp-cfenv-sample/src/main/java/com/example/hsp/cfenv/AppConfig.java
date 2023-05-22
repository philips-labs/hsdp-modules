package com.example.hsp.cfenv;

import com.example.hsp.cfenv.vault.HspVaultCfEnvProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class AppConfig {
    @Autowired
    private BeanFactory beanFactory;
    @Value("${spring.datasource.url}")
    String dataSourceUrl;
    @Value("${spring.datasource.username}")
    String dataSourceUsername;
    @Value("${spring.datasource.password}")
    String dataSourcePassword;
    @Value("${spring.datasource.driver-class-name}")
    String dataSourceDriver;

    @Bean
    public HspVaultCfEnvProcessor cfEnvProcessor() {
        return new HspVaultCfEnvProcessor();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dmds = new DriverManagerDataSource();
        dmds.setDriverClassName(dataSourceDriver);
        dmds.setUsername(dataSourceUsername);
        dmds.setPassword(dataSourcePassword);
        dmds.setUrl(dataSourceUrl);
        return dmds;
    }

}
