package com.m2.tur.service;

import com.m2.tur.factory.StateFactory;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.mapper.StateMapper;
import com.m2.tur.model.dto.response.StateResponse;
import com.m2.tur.model.entity.State;
import com.m2.tur.model.repository.StateRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class StateServiceTest {
    @Mock
    private StateRepository stateRepository;

    @Mock
    private StateMapper stateMapper;

    @InjectMocks
    private StateService stateService;

    @Nested
    class FindEntityById {
        @Test
        void should_return_state_with_success() {
            //Arrange
            Long id = 1L;
            State state = StateFactory.createEntity();

            when(stateRepository.findById(id)).thenReturn(Optional.of(state));

            //Act & Assert
            var result = assertDoesNotThrow(() -> stateService.findEntityById(id));

            assertInstanceOf(State.class, result);
            assertEquals(id, result.getId());
        }

        @Test
        void should_throw_not_found_exception_when_state_not_found() {
            //Arrange
            Long id = 1L;

            when(stateRepository.findById(id)).thenReturn(Optional.empty());

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () ->  stateService.findEntityById(id));

            assertEquals("State not found.", result.getMessage());
        }
    }

    @Nested
    class FindAll {
        @Test
        void should_return_all_state_with_success() {
            //Arrange
            State state = StateFactory.createEntity();
            StateResponse response = StateFactory.createResponse();

            when(stateRepository.findAll()).thenReturn(List.of(state));
            when(stateMapper.toResponse(state)).thenReturn(response);

            //Act & Assert
            var result = assertDoesNotThrow(() -> stateService.findAll());

            assertNotNull(result);
            assertInstanceOf(StateResponse.class, result.get(0));
        }
    }
}
