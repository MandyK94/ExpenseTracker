package com.mandyk.expense.service;

import com.mandyk.expense.dto.CategoryDTO;
import com.mandyk.expense.entity.Category;
import com.mandyk.expense.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Get all categories for a user
    public List<CategoryDTO> getCategoriesByUserId(Integer userId) {

        List<Category> categories = categoryRepository.findByUserIdSorted(userId);

        return categories.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Create category
    public CategoryDTO createCategory(CategoryDTO dto) {

        Category category = new Category();
        category.setUserId(dto.getUserId());
        category.setName(dto.getName());

        Category saved = categoryRepository.save(category);

        return mapToDTO(saved);
    }

    // Delete category
    public void deleteCategory(Integer id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        categoryRepository.delete(category);
    }

    // Mapper
    private CategoryDTO mapToDTO(Category category) {

        CategoryDTO dto = new CategoryDTO();

        dto.setId(category.getId());
        dto.setUserId(category.getUserId());
        dto.setName(category.getName());

        return dto;
    }
}
