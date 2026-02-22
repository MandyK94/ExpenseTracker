package com.mandyk.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountDTO {

    private Integer id;
    private String name;
    private Integer userId;
    private LocalDateTime createdAt;

    public AccountDTO() {}

    public AccountDTO(Integer id, String name, Integer userId, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
