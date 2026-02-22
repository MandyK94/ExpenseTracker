package com.mandyk.expense.integration;

import com.mandyk.expense.dto.TransactionCreateRequestDTO;
import com.mandyk.expense.entity.Transaction;
import com.mandyk.expense.entity.TransactionType;
import com.mandyk.expense.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class TransactionControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction savedTransaction;

    @BeforeEach
    void setUp() {
        Transaction t = new Transaction();
        t.setUserId(1);
        t.setAccountId(1);
        t.setCategoryId(1);
        t.setAmount(new BigDecimal("100.00"));
        t.setDescription("Grocery shopping");
        t.setTransactionType(TransactionType.EXPENSE);
        t.setTransactionDate(LocalDateTime.now());
        savedTransaction = transactionRepository.save(t);
    }

    @Test
    void createTransactionShouldSaveAndReturnTransaction() throws Exception {
        TransactionCreateRequestDTO request = new TransactionCreateRequestDTO();
        request.setUserId(1);
        request.setAccountId(1);
        request.setCategoryId(1);
        request.setAmount(new BigDecimal("50.00"));
        request.setDescription("Coffee");
        request.setTransactionType(TransactionType.EXPENSE);
        request.setTransactionDate(LocalDateTime.now());

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.description").value("Coffee"))
                .andExpect(jsonPath("$.amount").value(50.00));
    }

    @Test
    void getTransactionsByUserShouldReturnPagedResults() throws Exception {
        mockMvc.perform(get("/api/transactions/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description").value("Grocery shopping"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getTransactionsByAccountShouldReturnPagedResults() throws Exception {
        mockMvc.perform(get("/api/transactions/account/1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].accountId").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getTransactionsByCategoryShouldReturnPagedResults() throws Exception {
        mockMvc.perform(get("/api/transactions/category/1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].categoryId").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getTransactionShouldReturnSingleTransaction() throws Exception {
        mockMvc.perform(get("/api/transactions/" + savedTransaction.getId() + "/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedTransaction.getId()))
                .andExpect(jsonPath("$.description").value("Grocery shopping"));
    }

    @Test
    void getBalanceShouldReturnCorrectBalance() throws Exception {
        mockMvc.perform(get("/api/transactions/1/user/1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.balance").isNumber());
    }

    @Test
    void deleteTransactionShouldRemoveTransaction() throws Exception {
        mockMvc.perform(delete("/api/transactions/" + savedTransaction.getId() + "/user/1"))
                .andExpect(status().isOk());

        // verify it's gone
        mockMvc.perform(get("/api/transactions/" + savedTransaction.getId() + "/user/1"))
                .andExpect(status().isNotFound());
    }
}