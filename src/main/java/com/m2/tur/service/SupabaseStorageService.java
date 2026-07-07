package com.m2.tur.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SupabaseStorageService {
    private final RestClient supabaseStorageClient;

    public String upload(MultipartFile file) throws IOException {
        String filePath = UUID.randomUUID().toString();

        supabaseStorageClient.post()
                .uri("/" + filePath)
                .body(file.getBytes())
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .retrieve()
                .toBodilessEntity();

        return filePath;
    }

    public void delete(String filePath) {
        supabaseStorageClient.delete()
                .uri(filePath)
                .retrieve()
                .toBodilessEntity();
    }
}
