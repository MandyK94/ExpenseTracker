package com.mandyk.expense.dto;

import com.mandyk.expense.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionCreateRequestDTO {

    @NotNull(message="Amount is required")
    @DecimalMin(value="0.01", message = "Amount must be greater than zero")
    @Digits(integer = 13, fraction = 2, message = "Invalid amount format")
    private BigDecimal amount;
    @Size(max=255, message = "Description is too long")
    private String description;
    @NotNull(message = "Transaction Date is required")
    private LocalDateTime transactionDate;
    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @NotNull(message = "Account id is required")
    private Integer accountId;
    private Integer categoryId;

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

}
