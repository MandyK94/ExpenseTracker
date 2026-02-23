package com.mandyk.expense.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryDTO {

    private Integer id;
    @NotBlank(message="Category name is required")
    @Size(max=100, message = "Category name too long")
    private String name;
    private Integer userId;

    public CategoryDTO() {}

    public CategoryDTO(Integer id, String name, Integer userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
