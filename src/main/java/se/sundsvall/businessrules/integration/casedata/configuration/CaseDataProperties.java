package se.sundsvall.businessrules.integration.casedata.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.casedata")
public record CaseDataProperties(int connectTimeout, int readTimeout) {}
