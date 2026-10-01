package com.m2.tur.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheManagerConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();

        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(10_000)
        );

        manager.registerCustomCache("user-registration-cache", Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10)).build());
        manager.registerCustomCache("password-reset-cache", Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10)).build());
        manager.registerCustomCache("password-reset-token", Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(5)).build());

        return manager;
    }
}
