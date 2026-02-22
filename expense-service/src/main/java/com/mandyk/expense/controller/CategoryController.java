package com.mandyk.expense.controller;

import com.mandyk.expense.dto.CategoryDTO;
import com.mandyk.expense.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Get categories by user
    @GetMapping(path="/users/{userId}")
    public List<CategoryDTO> getCategories(@PathVariable Integer userId) {
        return categoryService.getCategoriesByUserId(userId);
    }

    // Create category
    @PostMapping()
    public CategoryDTO createCategory(@RequestBody CategoryDTO dto) {
        return categoryService.createCategory(dto);
    }

    // Delete category
    @DeleteMapping("/{id}")
    public void deleteCategoryById(@PathVariable Integer id, @RequestParam Integer userId) {
        categoryService.deleteCategory(id, userId);
    }



}
