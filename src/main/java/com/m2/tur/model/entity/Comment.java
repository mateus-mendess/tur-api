package com.m2.tur.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments", indexes = {
        @Index(name = "idx_comments_tp_id", columnList = "tourist_point_id")
})
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String content;

    private Integer note;

    @Column(name = "author_name")
    private String authorName;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "tourist_point_id")
    private TouristPoint touristPoint;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
