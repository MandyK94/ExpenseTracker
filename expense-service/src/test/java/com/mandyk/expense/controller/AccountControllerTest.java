package com.mandyk.expense.controller;

import com.mandyk.expense.dto.AccountDTO;
import com.mandyk.expense.exception.ResourceNotFoundException;
import com.mandyk.expense.service.AccountService;
import com.mandyk.expense.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private AccountDTO account;

    @BeforeEach
    void setUp() {
        account = new AccountDTO(1, "Checking", 1, LocalDateTime.now());
    }

    @Test
    void getAccountShouldReturnListOfAccounts() throws Exception {

        when(accountService.getAccountsByUserId(1)).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Checking"))
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    void getAccountShouldReturnEmptyListWhenNoAccounts() throws Exception {
        when(accountService.getAccountsByUserId(1)).thenReturn(List.of());

        mockMvc.perform(get("/api/accounts/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnCreatedAccount() throws Exception {

        when(accountService.createAccount(any(AccountDTO.class))).thenReturn(account);

        mockMvc.perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Checking"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void createAccountShouldReturn400WhenBadBody() throws Exception {
        mockMvc.perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnAccountById() throws Exception {
        when(accountService.getAccountById(1, 1)).thenReturn(account);

        mockMvc.perform(get("/api/accounts/1").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Checking"));
    }

    @Test
    void getAccountBtIdShouldReturn400WhenUserIdIsMissing() throws Exception {
        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAccountByIdShouldReturn404WhenNotFound() throws Exception {
        when(accountService.getAccountById(99, 1)).thenThrow(new ResourceNotFoundException("Account Not Found"));
        mockMvc.perform(get("/api/accounts/99").param("userId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAccountShouldReturn200WhenSuccesful() throws Exception {
        doNothing().when(accountService).deleteAccount(1, 1);
        mockMvc.perform(delete("/api/accounts/1").param("userId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAccountShouldReturn400WhenMissingUserId() throws Exception {
        mockMvc.perform(delete("/api/accounts/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAccountShouldReturn404WhenAccountNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Account Not Found")).when(accountService).deleteAccount(99, 1);

        mockMvc.perform(delete("/api/accounts/99").param("userId", "1"))
                .andExpect(status().isNotFound());
    }

}
