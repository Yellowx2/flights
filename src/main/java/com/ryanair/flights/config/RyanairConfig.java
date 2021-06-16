package com.ryanair.flights.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.ryanair.flights.model.UrlDto;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
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

    /**
     * This bean is supposed to allow Jackson to process Java 8 DateTimes, but I
     * don't know why it doesn't work as intended. I tried it in several ways, using
     * Module bean, Spring properties but I couldn't obtain a satisfactory result
     * from it. I'll leave it for the future
     *
     * @return ObjectMapper bean
     */
    @Bean
    public ObjectMapper mapper() {
        return JsonMapper.builder()
            .addModules(new ParameterNamesModule(), new Jdk8Module(), new JavaTimeModule())
            // .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .build();
    }
}
