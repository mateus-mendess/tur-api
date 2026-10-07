package com.m2.tur.model.repository;

import com.m2.tur.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findAllByTouristPointId(UUID touristPointId, Pageable pageable);

    @Query("SELECT COALESCE(AVG(c.note), 0) FROM Comment c WHERE c.touristPoint.id = :touristPointId")
    Double findAverageRatingByTouristPointId(UUID touristPointId);

}
