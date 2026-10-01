package com.m2.tur.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${jwt.private.key}")
    private RSAPrivateKey privateKey;

    @Value("${jwt.public.key}")
    private RSAPublicKey publicKey;

    private final CookieUtil cookieUtil;

    @Bean
    public SecurityFilterChain securityFilter(HttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/error").permitAll()
                                .requestMatchers(HttpMethod.GET, "/tourist-points").permitAll()
                                .requestMatchers(HttpMethod.GET, "/tourist-points/{id}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/tourist-points/{touristPointId}/comments").permitAll()
                                .requestMatchers(HttpMethod.GET, "/categories").permitAll()
                                .requestMatchers(HttpMethod.GET, "/states").permitAll()
                                .requestMatchers(HttpMethod.GET, "/accessibility-types").permitAll()
                                .requestMatchers(HttpMethod.GET, "/stats").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                                .requestMatchers(HttpMethod.POST, "/users/forgot-password").permitAll()
                                .requestMatchers(HttpMethod.POST, "/users/forgot-password/verify").permitAll()
                                .requestMatchers(HttpMethod.POST, "/tourist-points/{touristPointId}/comments").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/users/verify").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/users/reset-password").permitAll()
                                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> {
                    oauth2.bearerTokenResolver(bearerTokenResolver());
                    oauth2.jwt(Customizer.withDefaults());
                })
                .build();
    }

    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        return cookieUtil::extractToken;
    }

    @Bean
    public JwtDecoder publicKey() {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    public JwtEncoder privateKey() {
        var jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        var jwkSet = new ImmutableJWKSet<>(new JWKSet(jwk));

        return new NimbusJwtEncoder(jwkSet);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
