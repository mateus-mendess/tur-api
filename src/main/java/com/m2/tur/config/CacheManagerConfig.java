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
        manager.registerCustomCache("code",
                Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(10)).build());
        manager.registerCustomCache("password-reset-token",
                Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(5)).build());

        return manager;
    }
}
