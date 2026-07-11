package com.m2.tur.controller;

import com.m2.tur.model.dto.request.CommentRequest;
import com.m2.tur.model.dto.response.CommentResponse;
import com.m2.tur.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Comments", description = "Endpoints for listing and submitting comments on tourist points.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/tourist-points/{touristPointId}/comments")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "Lists all comments for a tourist point.", description = """
            Returns all comments submitted for a specific tourist point.
            No authentication required.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully.")
    })
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable UUID touristPointId) {
        return ResponseEntity.ok(commentService.findAllComments(touristPointId));
    }

    @Operation(summary = "Post a comment about a tourist point.", description = """
            Submits a comment with a rating (1-5) on a specific tourist point.
            No authentication required.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Comment submitted successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request data."),
            @ApiResponse(responseCode = "404", description = "Tourist point not found.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<Void> create(@PathVariable UUID touristPointId, @RequestBody @Valid CommentRequest request) {
        commentService.save(touristPointId, request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
