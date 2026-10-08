package com.m2.tur.infra.client;

import com.m2.tur.config.GeocodingConfig;
import com.m2.tur.core.exception.GeocodingException;
import com.m2.tur.dto.response.CoordinatesResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.JsonNode;

import java.time.Duration;

@Service
public class GeocodingClient {
    private final RestClient restClient;

    public GeocodingClient(GeocodingConfig config) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(5));

        this.restClient = RestClient.builder()
                .baseUrl(config.getUrl())
                .defaultHeader("User-Agent", config.getUserAgent())
                .requestFactory(factory)
                .build();
    }

    public CoordinatesResponse getCoordinates(String fullAddress) {
        try {
            JsonNode[] response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("q", fullAddress)
                            .queryParam("format", "jsonv2")
                            .queryParam("addressdetails", "1")
                            .build()
                    )
                    .retrieve()
                    .body(JsonNode[].class);

            if (response == null || response.length == 0) {
                throw new GeocodingException("No coordinates found");
            }

            Double latitude = response[0].get("lat").asDouble();
            Double longitude = response[0].get("lon").asDouble();

            return new CoordinatesResponse(latitude, longitude);
        } catch (RestClientException e) {
            throw new GeocodingException("Failed to retrieve coordinates");
        }
    }
}
