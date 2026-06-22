package com.m2.tur.model.repository;

import com.m2.tur.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
     Boolean existsByEmail(String email);

}
