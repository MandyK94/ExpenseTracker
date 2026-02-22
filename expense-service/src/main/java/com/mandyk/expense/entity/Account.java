package com.mandyk.expense.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String name;

    @Column(name="user_id", nullable = false)
    private Integer userId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Account() {}

    public Account(String name, Integer userId) {
        this.name = name;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

}
