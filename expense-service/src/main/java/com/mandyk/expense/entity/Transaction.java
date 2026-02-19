package com.mandyk.expense.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="\"transaction\"",
    indexes = {
        @Index(name="idx_transactions_user_date", columnList = "user_id"),
        @Index(name="idx_transactions_account", columnList = "account_id")
    })
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(precision = 15, scale = 2)
    @DecimalMin("0.01")
    @NotNull
    private BigDecimal amount;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name="transaction_date", updatable = false)
    private LocalDateTime transactionDate;

    @Column(name="type")
    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name="account_id")
    @NotNull
    private Integer accountId;

    @Column(name="category_id")
    private Integer categoryId;

    @NotNull
    @Column(name="user_id")
    private Integer userId;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    public Transaction() {}

    public Transaction(Integer id, BigDecimal amount, LocalDateTime transactionDate, TransactionType transactionType, Integer accountId, Integer userId) {
        this.userId = userId;
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.id = id;
    }

    @PrePersist
    public void prePersist() {
        if(this.transactionDate==null) {
            this.transactionDate = LocalDateTime.now();
        }
        if(this.createdAt==null) {
            this.createdAt=LocalDateTime.now();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
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
