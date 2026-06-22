package com.m2.tur.service;

import com.m2.tur.infra.exception.EmailAlreadyExistsException;
import com.m2.tur.mapper.UserMapper;
import com.m2.tur.model.dto.request.UserRequest;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Transactional
    public void save(UserRequest request) {
        validate(request);

        User user = userMapper.toEntity(request);

        userRepository.save(user);
    }

    private void validate(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists.");
        }
    }

}
