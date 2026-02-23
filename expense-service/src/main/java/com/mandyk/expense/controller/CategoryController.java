package com.mandyk.expense.controller;

import com.mandyk.expense.dto.CategoryDTO;
import com.mandyk.expense.service.CategoryService;
import com.mandyk.expense.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private CategoryService categoryService;
    private JwtUtil jwtUtil;

    public CategoryController(CategoryService categoryService, JwtUtil jwtUtil) {
        this.categoryService = categoryService;
        this.jwtUtil = jwtUtil;
    }

    // Get categories by user
    @GetMapping(path="/users")
    public List<CategoryDTO> getCategories(HttpServletRequest request) {
        return categoryService.getCategoriesByUserId(jwtUtil.getUserIdFromRequest(request));
    }

    // Create category
    @PostMapping()
    public CategoryDTO createCategory(@Valid @RequestBody CategoryDTO dto, HttpServletRequest request) {
        dto.setUserId(jwtUtil.getUserIdFromRequest(request));
        return categoryService.createCategory(dto);
    }

    // Delete category
    @DeleteMapping("/{id}")
    public void deleteCategoryById(@PathVariable Integer id, HttpServletRequest request) {
        categoryService.deleteCategory(id, jwtUtil.getUserIdFromRequest(request));
    }



}
