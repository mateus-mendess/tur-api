package com.m2.tur.service;

import com.m2.tur.infra.exception.CategoryAlreadyExistsException;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.mapper.CategoryMapper;
import com.m2.tur.model.dto.request.CategoryRequest;
import com.m2.tur.model.dto.response.CategoryResponse;
import com.m2.tur.model.entity.Category;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AuthService authService;

    public List<CategoryResponse> findAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Transactional
    public void save(CategoryRequest request) {
        validate(request);

        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new NotFoundException("User not found"));

        Category category = categoryMapper.toEntity(request);
        category.setUser(user);

        categoryRepository.save(category);
    }

    private void validate(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new CategoryAlreadyExistsException("Category with name " + request.name() + " already exists");
        }
    }

}
