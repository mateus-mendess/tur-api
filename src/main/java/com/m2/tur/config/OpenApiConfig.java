package com.m2.tur.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI().info(
                new Info()
                        .title("Tur. API")
                        .description("Open source REST API for discovering and sharing tourist spots across Brazil. Built with Java and Spring Boot.")
                        .version("v1")
        );
    }
}
