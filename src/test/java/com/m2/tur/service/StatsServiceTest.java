package com.m2.tur.service;

import com.m2.tur.dto.response.StatsResponse;
import com.m2.tur.model.repository.PhotoRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import com.m2.tur.model.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class StatsServiceTest {
    @Mock
    private TouristPointRepository touristPointRepository;

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StatsService statsService;

    @Nested
    class GetCommunityStats {
        @Test
        void getCommunityStats() {
            //Arrange
            when(touristPointRepository.count()).thenReturn(10L);
            when(photoRepository.count()).thenReturn(40L);
            when(userRepository.count()).thenReturn(10L);

            //Act & Assert
            var result = statsService.getCommunityStats();

            assertThat(result).isEqualTo(new StatsResponse(10L, 40L, 10L));
        }
    }
}
