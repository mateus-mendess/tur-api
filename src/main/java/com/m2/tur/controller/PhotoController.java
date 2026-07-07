package com.m2.tur.controller;

import com.m2.tur.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/photos")
public class PhotoController {
    private final PhotoService photoService;

    @PostMapping("/tourist-points/{id}")
    public ResponseEntity<Void> upload(@PathVariable UUID id, @RequestParam MultipartFile file) throws IOException {
        photoService.save(id, file);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        photoService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
