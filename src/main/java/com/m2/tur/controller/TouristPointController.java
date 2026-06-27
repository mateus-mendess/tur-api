package com.m2.tur.controller;

import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.service.TouristPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tourist-points")
public class TouristPointController {
    private final TouristPointService touristPointService;

    @GetMapping
    public ResponseEntity<List<TouristPointResponse>> getTouristPoint() {
        return ResponseEntity.ok(touristPointService.findAll());
    }
}
