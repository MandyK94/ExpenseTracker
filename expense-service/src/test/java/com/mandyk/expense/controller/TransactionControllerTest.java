package com.mandyk.expense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mandyk.expense.dto.BalanceDTO;
import com.mandyk.expense.dto.TransactionCreateRequestDTO;
import com.mandyk.expense.dto.TransactionResponseDTO;
import com.mandyk.expense.entity.TransactionType;
import com.mandyk.expense.exception.ResourceNotFoundException;
import com.mandyk.expense.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionResponseDTO response;
    private TransactionCreateRequestDTO request;
    private BalanceDTO balance;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        request = new TransactionCreateRequestDTO();
        request.setAmount(new BigDecimal("100.00"));
        request.setDescription("Grocery shopping");
        request.setTransactionDate(LocalDateTime.now());
        request.setTransactionType(TransactionType.EXPENSE);
        request.setAccountId(1);
        request.setCategoryId(1);
        request.setUserId(1);

        response = new TransactionResponseDTO();
        response.setId(1);
        response.setAmount(new BigDecimal("100.00"));
        response.setDescription("Grocery shopping");
        response.setTransactionDate(LocalDateTime.now());
        response.setTransactionType(TransactionType.EXPENSE);
        response.setAccountId(1);
        response.setCategoryId(1);
        response.setUserId(1);
        response.setCreatedAt(LocalDateTime.now());

        balance = new BalanceDTO();
        balance.setAccountId(1);
        balance.setBalance(new BigDecimal("500.00"));
    }

    @Test
    void createTransactionShouldReturnCreatedTransaction() throws Exception {
        when(transactionService.createTransaction(any(TransactionCreateRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.description").value("Grocery shopping"))
                .andExpect(jsonPath("$.transactionType").value("EXPENSE"));
    }

    @Test
    void createTransactionShouldReturn400WhenBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTransactionShouldReturn400WhenBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-valid-json"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getTransactionsByUserShouldReturnPageOfTransactions() throws Exception {
        Page<TransactionResponseDTO> page = new PageImpl<>(List.of(response));
        when(transactionService.getTransactionsByUserId(eq(1), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/transactions/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].description").value("Grocery shopping"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getTransactionsByUserShouldReturnEmptyPageWhenNoTransactions() throws Exception {
        Page<TransactionResponseDTO> emptyPage = new PageImpl<>(List.of());
        when(transactionService.getTransactionsByUserId(eq(1), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/transactions/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void getTransactionsByUserShouldSupportPaginationParams() throws Exception {
        Page<TransactionResponseDTO> page = new PageImpl<>(List.of(response));
        when(transactionService.getTransactionsByUserId(eq(1), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/transactions/user/1")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk());
    }

    @Test
    void getTransactionsByAccountShouldReturnPageOfTransactions() throws Exception {
        Page<TransactionResponseDTO> page = new PageImpl<>(List.of(response));
        when(transactionService.getTransactionsByAccountId(eq(1), eq(1), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/transactions/account/1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].accountId").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getTransactionsByAccountShouldReturnEmptyPageWhenNoTransactions() throws Exception {
        Page<TransactionResponseDTO> emptyPage = new PageImpl<>(List.of());
        when(transactionService.getTransactionsByAccountId(eq(1), eq(1), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/transactions/account/1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void getTransactionsByCategoryShouldReturnPageOfTransactions() throws Exception {
        Page<TransactionResponseDTO> page = new PageImpl<>(List.of(response));
        when(transactionService.getTransactionsByCategoryId(eq(1), eq(1), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/transactions/category/1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].categoryId").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getTransactionsByCategoryShouldReturnEmptyPage_whenNoTransactions() throws Exception {
        Page<TransactionResponseDTO> emptyPage = new PageImpl<>(List.of());
        when(transactionService.getTransactionsByCategoryId(eq(1), eq(1), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/transactions/category/1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    // --- GET /api/transactions/{txnId}/user/{userId} ---

    @Test
    void getTransactionShouldReturnSingleTransaction() throws Exception {
        when(transactionService.getTransaction(1, 1)).thenReturn(response);

        mockMvc.perform(get("/api/transactions/1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Grocery shopping"));
    }

    @Test
    void getTransactionShouldReturn404WhenNotFound() throws Exception {
        when(transactionService.getTransaction(99, 1))
                .thenThrow(new ResourceNotFoundException("Transaction not found"));

        mockMvc.perform(get("/api/transactions/99/user/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBalanceShouldReturnBalanceDTO() throws Exception {
        when(transactionService.getAccountBalance(1, 1))
                .thenReturn(new BigDecimal("500.00"));

        mockMvc.perform(get("/api/transactions/1/user/1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.balance").value(500.00));
    }

    @Test
    void getBalanceShouldReturn404WhenAccountNotFound() throws Exception {
        when(transactionService.getAccountBalance(1, 99))
                .thenThrow(new ResourceNotFoundException("Account not found"));

        mockMvc.perform(get("/api/transactions/1/user/99/balance"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTransactionShouldReturn200WhenSuccessful() throws Exception {
        doNothing().when(transactionService).deleteTransaction(1, 1);

        mockMvc.perform(delete("/api/transactions/1/user/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTransactionShouldReturn404WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Transaction not found"))
                .when(transactionService).deleteTransaction(99, 1);

        mockMvc.perform(delete("/api/transactions/99/user/1"))
                .andExpect(status().isNotFound());
    }
}
