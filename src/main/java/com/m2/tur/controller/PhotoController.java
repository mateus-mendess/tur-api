package com.m2.tur.controller;

import com.m2.tur.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Tag(name = "Photos", description = "Endpoints for uploading and removing photos from tourist points.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/photos")
public class PhotoController {
    private final PhotoService photoService;

    @Operation(summary = "uploads photos of tourist points", description = """
            Uploads a photo to a specific tourist point.
            Accepted formats: JPEG, PNG and WebP. Maximum size: 2MB.
            Each tourist point allows up to 4 photos.
            Requires authentication. Only the owner can upload photos.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Photo uploaded successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid file type, size exceeded or photo limit reached."),
            @ApiResponse(responseCode = "401", description = "User not authenticated."),
            @ApiResponse(responseCode = "403", description = "User not allowed to upload photos to this tourist point."),
            @ApiResponse(responseCode = "404", description = "Tourist point not found."),
            @ApiResponse(responseCode = "500", description = "Failed to upload file to storage.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/tourist-points/{id}")
    public ResponseEntity<Void> upload(
            @Parameter(description = "ID of the tourist point to upload the photo to.", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Image file to upload. Accepted formats: JPEG, PNG and WebP. Maximum size: 2MB.", required = true)
            @RequestParam MultipartFile file) {
        photoService.save(id, file);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Remove photos of tourist points", description = """
            Permanently removes a photo from a tourist point and from storage.
            Requires authentication. Only the owner can remove photos.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Photo removed successfully."),
            @ApiResponse(responseCode = "401", description = "User not authenticated."),
            @ApiResponse(responseCode = "403", description = "User not allowed to remove this photo."),
            @ApiResponse(responseCode = "404", description = "Tourist point or photo not found."),
            @ApiResponse(responseCode = "500", description = "Failed to delete file from storage.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the photo to be removed.", required = true)
            @PathVariable UUID id) {
        photoService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
