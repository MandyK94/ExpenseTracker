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
        String token = generateTestToken(1, "mandeep@email.com");
        mockMvc.perform(get("/api/categories/users")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Food"))
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    void getCategoriesShouldReturnEmptyWhenUserHasNoCategories() throws Exception {
        String token = generateTestToken(999, "mandeep@email.com");
        mockMvc.perform(get("/api/categories/users")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void createCategoryShouldSaveAndReturnCategory() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Transport");
        dto.setUserId(1);
        String token = generateTestToken(1, "mandeep@email.com");

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Transport"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void deleteCategoryShouldRemoveCategory() throws Exception {
        String token = generateTestToken(savedCategory.getUserId(), "mandeep@email.com");
        mockMvc.perform(delete("/api/categories/" + savedCategory.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // verify it's gone
        mockMvc.perform(get("/api/categories/users")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}