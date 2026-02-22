package com.mandyk.expense.controller;

import com.mandyk.expense.dto.CategoryDTO;
import com.mandyk.expense.exception.ResourceNotFoundException;
import com.mandyk.expense.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryDTO category;

    @BeforeEach
    void setup() {
        category = new CategoryDTO(1, "Shopping", 1);
    }

    @Test
    public void shouldGetAllCategoriesForUserId() throws Exception {
        when(categoryService.getCategoriesByUserId(1)).thenReturn(List.of(category));

        mockMvc.perform(get("/api/categories/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect((jsonPath("$[0].name").value("Shopping")));
    }

    @Test
    void getCategoriesShouldReturnEmptyListWhenNoMatch() throws Exception {
        when(categoryService.getCategoriesByUserId(1)).thenReturn(List.of());

        mockMvc.perform(get("/api/categories/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getCategoriesShouldReturn404WhenUserNotFound() throws Exception {
        when(categoryService.getCategoriesByUserId(1)).thenThrow(new ResourceNotFoundException("User Not Found"));

        mockMvc.perform(get("/api/categories/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCategoryShouldReturnCreatedCategory() throws Exception {
        when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(category);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Shopping"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void createCategoryShouldReturn400WhenBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCategoryShouldReturn400WhenBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-valid-json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCategoryShouldReturn200WhenSuccessful() throws Exception {
        doNothing().when(categoryService).deleteCategory(1, 1);

        mockMvc.perform(delete("/api/categories/1").param("userId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCategoryShouldReturn404WhenCategoryNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Category not found"))
                .when(categoryService).deleteCategory(99, 1);

        mockMvc.perform(delete("/api/categories/99").param("userId", "1"))
                .andExpect(status().isNotFound());
    }


}
