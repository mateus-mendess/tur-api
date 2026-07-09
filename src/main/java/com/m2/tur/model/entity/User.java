package com.m2.tur.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String email;

    private String password;

    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user")
    private Set<Category> categories;

    @OneToMany(mappedBy = "user")
    private Set<TouristPoint> touristPoints;

    @PrePersist
    private void prePersist() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }
}
