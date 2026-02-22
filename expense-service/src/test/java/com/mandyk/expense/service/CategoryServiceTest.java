package com.mandyk.expense.service;

import com.mandyk.expense.dto.CategoryDTO;
import com.mandyk.expense.entity.Category;
import com.mandyk.expense.exception.ResourceNotFoundException;
import com.mandyk.expense.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category savedCategory;

    @BeforeEach
    void setUp() {
        savedCategory = new Category(1, "Food");
        savedCategory.setId(1);
    }

    // --- getCategoriesByUserId ---

    @Test
    void getCategoriesByUserIdShouldReturnSortedList() {
        when(categoryRepository.findByUserIdSorted(1)).thenReturn(List.of(savedCategory));

        List<CategoryDTO> result = categoryService.getCategoriesByUserId(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Food");
        assertThat(result.get(0).getUserId()).isEqualTo(1);
    }

    @Test
    void getCategoriesByUserIdShouldReturnEmptyListWhenNoCategories() {
        when(categoryRepository.findByUserIdSorted(1)).thenReturn(List.of());

        List<CategoryDTO> result = categoryService.getCategoriesByUserId(1);

        assertThat(result).isEmpty();
    }

    // --- createCategory ---

    @Test
    void createCategoryShouldSaveAndReturnCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Food");
        dto.setUserId(1);

        CategoryDTO result = categoryService.createCategory(dto);

        assertThat(result.getName()).isEqualTo("Food");
        assertThat(result.getUserId()).isEqualTo(1);
        verify(categoryRepository).save(any(Category.class));
    }

    // --- deleteCategory ---

    @Test
    void deleteCategoryShouldDeleteSuccessfully() {
        when(categoryRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(savedCategory));
        doNothing().when(categoryRepository).delete(savedCategory);

        categoryService.deleteCategory(1, 1);

        verify(categoryRepository).delete(savedCategory);
    }

    @Test
    void deleteCategoryShouldThrowWhenNotFound() {
        when(categoryRepository.findByIdAndUserId(99, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory(99, 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found");

        verify(categoryRepository, never()).delete(any());
    }
}