package com.m2.tur.controller;

import com.m2.tur.model.dto.request.AddressRequest;
import com.m2.tur.service.AddressService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/addresses")
public class AddressController {
    private final AddressService addressService;

    @PutMapping("/tourist-point/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid AddressRequest request) {
        addressService.update(id, request);

       return ResponseEntity.ok().build();
    }

}
