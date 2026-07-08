package com.m2.tur.infra.client;

import com.m2.tur.config.SupabaseConfig;
import com.m2.tur.infra.exception.StorageException;
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

    public String upload(MultipartFile file) {
        try {
            String filePath = UUID.randomUUID().toString();

            restClient.post()
                    .uri("/" + filePath)
                    .body(file.getBytes())
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .retrieve()
                    .toBodilessEntity();

            return filePath;
        } catch (IOException e) {
            throw new StorageException("Failed to upload file.");
        }
    }

    public void delete(String filePath) {
        try {
            restClient.delete()
                    .uri("/" + filePath)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw new StorageException("Failed to delete file.");
        }
    }
}
