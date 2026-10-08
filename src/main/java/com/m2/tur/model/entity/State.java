package com.m2.tur.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String abbreviation;

    @CreationTimestamp
    private LocalDate createdAt;

    @OneToMany(mappedBy = "state")
    private Set<Address> addresses;
}
