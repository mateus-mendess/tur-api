package com.m2.tur.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "geocoding")
public class GeocodingConfig {
    private String geocodingUrl;

    private String userAgent;
}
