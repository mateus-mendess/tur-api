package com.m2.tur.controller;

import com.m2.tur.model.dto.response.StateResponse;
import com.m2.tur.service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/states")
public class StateController {
    private final StateService stateService;

    @GetMapping
    public ResponseEntity<List<StateResponse>> getStates() {
        return ResponseEntity.ok(stateService.findAll());
    }
}
