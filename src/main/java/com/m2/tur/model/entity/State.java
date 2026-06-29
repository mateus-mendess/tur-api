package com.m2.tur.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "states")
@Entity
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private String abbreviation;

    private LocalDate createdAt;

    @OneToMany(mappedBy = "state")
    private Set<Address> addresses;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
    }
}
