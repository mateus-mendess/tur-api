package com.m2.tur.controller;

import com.m2.tur.model.dto.request.CommentRequest;
import com.m2.tur.model.dto.response.CommentResponse;
import com.m2.tur.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tourist-points/{touristPointId}/comments")
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable UUID touristPointId) {
        return ResponseEntity.ok(commentService.findAllComments(touristPointId));
    }

    @PostMapping
    public ResponseEntity<Void> create(@PathVariable UUID touristPointId, @RequestBody @Valid CommentRequest request) {
        commentService.save(touristPointId, request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
