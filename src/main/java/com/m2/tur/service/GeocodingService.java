package com.m2.tur.service;

import com.m2.tur.model.dto.response.CoordinatesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Service
public class GeocodingService {
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    private final ObjectMapper objectMapper;

    public CoordinatesResponse getCoordinates(String fullAddress) throws IOException, InterruptedException {
        String url = String.format("%s?q=%s&format=jsonv2&addressdetails=1",
                NOMINATIM_URL,
                encode(fullAddress)
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "SolidarityNetwork/1.0 (mendes.profissional12@gmail.com)")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode root = objectMapper.readTree(response.body());
        Double latitude = root.get(0).get("lat").asDouble();
        Double longitude = root.get(0).get("lon").asDouble();

        return new CoordinatesResponse(latitude, longitude);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
