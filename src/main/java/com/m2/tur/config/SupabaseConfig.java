package com.m2.tur.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class SupabaseConfig {
    @Value("${SUPABASE_URL}")
    private String baseUrl;

    @Value("${SUPABASE_ANON_KEY}")
    private String anonKey;

    @Value("${SUPABASE_SERVICE_ROLE_KEY}")
    private String serviceKey;

    @Value("${SUPABASE_BUCKET_NAME}")
    private String bucketName;

    @Bean
    RestClient supabaseStorageClient() {
        return RestClient.builder()
                .baseUrl(baseUrl + bucketName)
                .defaultHeader("apikey", anonKey)
                .defaultHeader("Authorization", "Bearer " + serviceKey)
                .build();
    }
}
