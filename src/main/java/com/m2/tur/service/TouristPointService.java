package com.m2.tur.service;

import com.m2.tur.mapper.TouristPointMapper;
import com.m2.tur.model.dto.response.TouristPointResponse;
import com.m2.tur.model.repository.TouristPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TouristPointService {
    private final TouristPointRepository touristPointRepository;
    private final TouristPointMapper touristPointMapper;
    private final AuthService authService;

    public List<TouristPointResponse> findAll() {
        return touristPointRepository.findAll()
                .stream()
                .map(touristPointMapper::toResponse)
                .toList();
    }

}
