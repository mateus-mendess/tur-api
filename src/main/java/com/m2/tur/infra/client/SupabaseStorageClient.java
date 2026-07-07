package com.m2.tur.infra.client;

import com.m2.tur.config.SupabaseConfig;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
public class SupabaseStorageClient {
    private final RestClient restClient;

    public SupabaseStorageClient(SupabaseConfig config) {
        this.restClient = RestClient.builder()
                .baseUrl(config.getUrl() + config.getBucket())
                .defaultHeader("apikey", config.getAnonKey())
                .defaultHeader("Authorization", "Bearer " + config.getServiceRoleKey())
                .build();
    }

    public String upload(MultipartFile file) throws IOException {
        String filePath = UUID.randomUUID().toString();

        restClient.post()
                .uri("/" + filePath)
                .body(file.getBytes())
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .retrieve()
                .toBodilessEntity();

        return filePath;
    }

    public void delete(String filePath) {
        restClient.delete()
                .uri("/" + filePath)
                .retrieve()
                .toBodilessEntity();
    }
}
