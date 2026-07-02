package com.m2.tur.service;

import com.m2.tur.mapper.CommentMapper;
import com.m2.tur.model.dto.response.CommentResponse;
import com.m2.tur.model.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public List<CommentResponse> findAllComments(UUID touristPointId) {
        return commentRepository.findAllByTouristPointId(touristPointId)
                .stream()
                .map(commentMapper::toResponse)
                .toList();
    }
}
