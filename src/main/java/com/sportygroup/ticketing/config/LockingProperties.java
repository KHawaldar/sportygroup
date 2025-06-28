package com.sportygroup.ticketing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ticketing.lock")
public record LockingProperties(int waitTime, int leaseTime) {
}
