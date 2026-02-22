package com.mandyk.expense.dto;

import java.math.BigDecimal;

public class BalanceDTO {

    private Integer accountId;
    private BigDecimal balance;

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}