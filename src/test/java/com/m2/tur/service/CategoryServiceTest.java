package com.m2.tur.service;

import com.m2.tur.factory.CategoryFactory;
import com.m2.tur.factory.UserFactory;
import com.m2.tur.infra.exception.CategoryAlreadyExistsException;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.mapper.CategoryMapper;
import com.m2.tur.model.dto.request.CategoryRequest;
import com.m2.tur.model.dto.response.CategoryResponse;
import com.m2.tur.model.entity.Category;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private CategoryService categoryService;

    @Captor
    private ArgumentCaptor<Category> captor;

    private CategoryRequest categoryRequest;
    private Category category;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        categoryRequest = CategoryFactory.createRequest();
        category = CategoryFactory.createEntity();
        categoryResponse = CategoryFactory.createResponse();
    }

    @Nested
    class FindAllCategories {
        @Test
        void should_return_all_categories() {
            //Arrange
            when(categoryRepository.findAll()).thenReturn(List.of(category));
            when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

            //Act & Assert
            var result = categoryService.findAllCategories();

            assertNotNull(result);
        }
    }

    @Nested
    class Save {
        @Test
        void should_save_category_with_success() {
            //Arrange
            User user = UserFactory.createEntity();

            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(categoryMapper.toEntity(categoryRequest)).thenReturn(category);

            //Act & Assert
            categoryService.save(categoryRequest);

            verify(categoryRepository).save(captor.capture());

            var captured = captor.getValue();

            assertNotNull(captured.getUser());
        }

        @Test
        void should_throw_category_already_exists_exception_when_category_exists() {
            //Arrange
            when(categoryRepository.existsByName(any(String.class))).thenReturn(true);

            //Act & Assert
            assertThrows(CategoryAlreadyExistsException.class, () ->  categoryService.save(categoryRequest));

            verify(categoryRepository, times(0)).save(any(Category.class));
        }

        @Test
        void should_throw_not_found_exception_when_user_not_found() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(NotFoundException.class, () ->  categoryService.save(categoryRequest));

            verify(categoryRepository, times(0)).save(any(Category.class));
        }
    }
}
