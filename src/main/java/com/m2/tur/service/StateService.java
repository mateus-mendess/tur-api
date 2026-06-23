package com.m2.tur.service;

import com.m2.tur.mapper.StateMapper;
import com.m2.tur.model.dto.response.StateResponse;
import com.m2.tur.model.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StateService {
    private final StateRepository stateRepository;
    private final StateMapper stateMapper;

    public List<StateResponse> findAll() {
        return stateRepository.findAll()
                .stream()
                .map(stateMapper::toResponse)
                .toList();
    }

}
