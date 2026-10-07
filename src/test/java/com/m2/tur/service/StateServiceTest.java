package com.m2.tur.service;

import com.m2.tur.factory.StateFactory;
import com.m2.tur.mapper.StateMapper;
import com.m2.tur.model.dto.response.StateResponse;
import com.m2.tur.model.entity.State;
import com.m2.tur.model.repository.StateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StateServiceTest {
    @Mock
    private StateRepository stateRepository;

    @Mock
    private StateMapper stateMapper;

    @InjectMocks
    private StateService stateService;

    private State state;
    private StateResponse response;

    @BeforeEach
    void setUp() {
        state = StateFactory.createEntity();
        response = StateFactory.createResponse();
    }

    @Nested
    class FindAll {
        @Test
        void should_return_all_state_with_success() {
            //Arrange
            when(stateRepository.findAll()).thenReturn(List.of(state));
            when(stateMapper.toResponse(state)).thenReturn(response);

            //Act & Assert
            var result =  stateService.findAll();

            assertSame(response, result.get(0));
        }
    }
}
