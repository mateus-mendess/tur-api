package com.m2.tur.controller;

import com.m2.tur.model.dto.request.TouristPointRequest;
import com.m2.tur.model.dto.request.TouristPointUpdateRequest;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.service.TouristPointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tourist-points")
public class TouristPointController {
    private final TouristPointService touristPointService;

    @GetMapping
    public ResponseEntity<List<TouristPointResponse>> getTouristPoints() {
        return ResponseEntity.ok(touristPointService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TouristPointResponse> getTouristPointById(@PathVariable UUID id) {
        return ResponseEntity.ok(touristPointService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TouristPointResponse> create(@RequestBody @Valid TouristPointRequest request) {
        TouristPointResponse response = touristPointService.save(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid TouristPointUpdateRequest request) {
        touristPointService.update(request, id);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        touristPointService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
