package com.mandyk.expense.integration;

import com.mandyk.expense.dto.CategoryDTO;
import com.mandyk.expense.entity.Category;
import com.mandyk.expense.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class CategoryControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category savedCategory;

    @BeforeEach
    void setUp() {
        savedCategory = categoryRepository.save(new Category(1, "Food"));
    }

    @Test
    void getCategoriesShouldReturnCategoriesForUser() throws Exception {
        mockMvc.perform(get("/api/categories/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Food"))
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    void getCategoriesShouldReturnEmptyWhenUserHasNoCategories() throws Exception {
        mockMvc.perform(get("/api/categories/users/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void createCategoryShouldSaveAndReturnCategory() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Transport");
        dto.setUserId(1);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Transport"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void deleteCategoryShouldRemoveCategory() throws Exception {
        mockMvc.perform(delete("/api/categories/" + savedCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", savedCategory.getUserId().toString()))
                .andExpect(status().isOk());

        // verify it's gone
        mockMvc.perform(get("/api/categories/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}