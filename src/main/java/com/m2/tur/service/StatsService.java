package com.m2.tur.service;

import com.m2.tur.dto.response.StatsResponse;
import com.m2.tur.model.repository.PhotoRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import com.m2.tur.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StatsService {
    private final TouristPointRepository touristPointRepository;
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;

    @Cacheable(cacheNames = "stats-cache")
    public StatsResponse getCommunityStats() {
        return new StatsResponse(touristPointRepository.count(), photoRepository.count(), userRepository.countByActiveTrue());
    }
}
