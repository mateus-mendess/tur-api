package com.m2.tur.service;

import com.m2.tur.infra.exception.EmailAlreadyExistsException;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.mapper.UserMapper;
import com.m2.tur.model.dto.request.UserRequest;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User findEntityById(UUID id) {
     return userRepository.findById(id)
             .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Transactional
    public void save(UserRequest request) {
        validate(request);

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }

    private void validate(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists.");
        }
    }

}
