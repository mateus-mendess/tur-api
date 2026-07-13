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

    @Nested
    class FindAllCategories {
        @Test
        void should_return_all_categories() {
            //Arrange
            Category category = CategoryFactory.createEntity();
            CategoryResponse response = CategoryFactory.createResponse();

            when(categoryRepository.findAll()).thenReturn(List.of(category));
            when(categoryMapper.toResponse(category)).thenReturn(response);

            //Act & Assert
            var result = assertDoesNotThrow(() -> categoryService.findAllCategories());

            verify(categoryRepository).findAll();

            assertEquals(List.of(response), result);
        }
    }

    @Nested
    class Save {
        @Test
        void should_save_category_with_success() {
            //Arrange
            CategoryRequest request = CategoryFactory.createRequest();
            Category category = CategoryFactory.createEntity();
            User user = UserFactory.createEntity();

            when(categoryRepository.existsByName(any(String.class))).thenReturn(false);
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(categoryMapper.toEntity(request)).thenReturn(category);
            when(categoryRepository.save(category)).thenReturn(category);

            //Act & Assert
            assertDoesNotThrow(() -> categoryService.save(request));

            verify(authService).getAuthenticatedUser();
            verify(categoryRepository).save(captor.capture());

            var captured = captor.getValue();

            assertEquals(request.name(), captured.getName());
            assertEquals(user,  captured.getUser());
        }

        @Test
        void should_throw_category_already_exists_exception_when_category_exists() {
            //Arrange
            CategoryRequest request = CategoryFactory.createRequest();

            when(categoryRepository.existsByName(any(String.class))).thenReturn(true);

            //Act & Assert
            var result = assertThrows(CategoryAlreadyExistsException.class, () ->  categoryService.save(request));

            verify(categoryRepository, times(0)).save(any(Category.class));

            assertEquals("Category with name " + request.name() + " already exists", result.getMessage());
        }

        @Test
        void should_throw_not_found_exception_when_user_not_found() {
            //Arrange
            CategoryRequest request = CategoryFactory.createRequest();

            when(categoryRepository.existsByName(any(String.class))).thenReturn(false);
            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            var result = assertThrows(NotFoundException.class, () ->  categoryService.save(request));

            verify(categoryRepository, times(0)).save(any(Category.class));

            assertEquals("User not found", result.getMessage());
        }
    }
}
