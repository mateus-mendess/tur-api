package com.m2.tur.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String street;

    private String complement;

    private String neighborhood;

    private String city;

    @Column(name = "zip_code")
    private String zipcode;

    private Double latitude;

    private Double longitude;

    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "address")
     private TouristPoint touristPoint;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
