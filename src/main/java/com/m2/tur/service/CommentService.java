package com.m2.tur.service;

import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.mapper.CommentMapper;
import com.m2.tur.dto.request.CommentRequest;
import com.m2.tur.dto.response.CommentResponse;
import com.m2.tur.model.entity.Comment;
import com.m2.tur.model.entity.TouristPoint;
import com.m2.tur.model.repository.CommentRepository;
import com.m2.tur.model.repository.TouristPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final TouristPointRepository touristPointRepository;

    public Page<CommentResponse> findAllComments(UUID touristPointId, Pageable pageable) {
        return commentRepository.findAllByTouristPointId(touristPointId, pageable)
                .map(commentMapper::toResponse);
    }

    @Transactional
    @CacheEvict(cacheNames = "tourist-point", key = "#touristPointId")
    public void save(UUID touristPointId, CommentRequest request) {
        TouristPoint touristPoint = touristPointRepository.findById(touristPointId)
                .orElseThrow(() -> new NotFoundException("Tourist Point Not Found."));

        Comment comment = commentMapper.toEntity(request);
        comment.setTouristPoint(touristPoint);

        commentRepository.save(comment);
    }
}
