package com.ryanair.flights.config;

import com.ryanair.flights.model.UrlDto;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@ConfigurationProperties(prefix = "ryanair")
@Getter
public class RyanairConfig {

    private UrlDto route;
}
