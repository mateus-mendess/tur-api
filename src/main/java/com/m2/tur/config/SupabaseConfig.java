package com.m2.tur.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "supabase")
public class SupabaseConfig {
    private String url;

    private String urlPublic;

    private String anonKey;

    private String serviceRoleKey;

    private String bucket;
}
