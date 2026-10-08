package com.m2.tur.infra.client;

import com.m2.tur.config.SupabaseConfig;
import com.m2.tur.core.exception.StorageException;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Component
public class SupabaseStorageClient {
    private final RestClient restClient;

    public SupabaseStorageClient(SupabaseConfig config) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(5));

        this.restClient = RestClient.builder()
                .baseUrl(config.getUrl() + config.getBucket())
                .defaultHeader("apikey", config.getAnonKey())
                .defaultHeader("Authorization", "Bearer " + config.getServiceRoleKey())
                .requestFactory(factory)
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
        } catch (IOException | RestClientException e) {
            throw new StorageException("Failed to upload file.");
        }
    }

    public void delete(String filePath) {
        try {
            restClient.delete()
                    .uri("/" + filePath)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, ((request, response) -> {

                    }))
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new StorageException("Failed to delete file");
        }
    }
}
