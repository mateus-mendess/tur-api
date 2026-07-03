package com.m2.tur.service;

import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.mapper.CommentMapper;
import com.m2.tur.model.dto.request.CommentRequest;
import com.m2.tur.model.dto.response.CommentResponse;
import com.m2.tur.model.entity.Comment;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.repository.CommentRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final TouristPointRepository touristPointRepository;

    public List<CommentResponse> findAllComments(UUID touristPointId) {
        return commentRepository.findAllByTouristPointId(touristPointId)
                .stream()
                .map(commentMapper::toResponse)
                .toList();
    }

    @Transactional
    public void save(UUID touristPointId, CommentRequest request) {
        TouristPoint touristPoint = touristPointRepository.findById(touristPointId)
                .orElseThrow(() -> new NotFoundException("Tourist Point Not Found."));

        Comment comment = commentMapper.toEntity(request);
        comment.setTouristPoint(touristPoint);

        commentRepository.save(comment);
    }
}
