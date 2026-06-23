package com.m2.tur.model.repository;

import com.m2.tur.model.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends JpaRepository<State,Integer> {
}
