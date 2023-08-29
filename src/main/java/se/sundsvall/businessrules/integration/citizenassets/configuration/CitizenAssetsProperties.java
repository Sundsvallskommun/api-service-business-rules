package se.sundsvall.businessrules.integration.citizenassets.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.citizenassets")
public record CitizenAssetsProperties(int connectTimeout, int readTimeout) {}
