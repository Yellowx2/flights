package com.ryanair.flights.config;

import com.ryanair.flights.model.UrlDto;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration file that picks data from application.yaml
 */
@Configuration
@ConfigurationProperties(prefix = "ryanair")
@Data
public class RyanairConfig {

    private UrlDto route;

    private UrlDto schedule;
}
