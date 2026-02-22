package com.mandyk.expense.integration;

import com.mandyk.expense.dto.AccountDTO;
import com.mandyk.expense.entity.Account;
import com.mandyk.expense.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class AccountControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    private Account savedAccount;

    @BeforeEach
    void setUp() {
        savedAccount = accountRepository.save(new Account("Checking", 1));
    }

    @Test
    void getAccountsShouldReturnAccountsForUser() throws Exception {
        mockMvc.perform(get("/api/accounts/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Checking"))
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    void getAccountsShouldReturnEmptyWhenUserHasNoAccounts() throws Exception {
        mockMvc.perform(get("/api/accounts/users/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAccountByIdShouldReturnAccount() throws Exception {
        mockMvc.perform(get("/api/accounts/" + savedAccount.getId())
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Checking"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void getAccountByIdShouldFailWhenNotFound() throws Exception {
        mockMvc.perform(get("/api/accounts/9999")
                        .param("userId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAccountShouldSaveAndReturnAccount() throws Exception {
        AccountDTO dto = new AccountDTO();
        dto.setName("Savings");
        dto.setUserId(1);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Savings"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void deleteAccountShouldRemoveAccount() throws Exception {
        mockMvc.perform(delete("/api/accounts/" + savedAccount.getId())
                        .param("userId", "1"))
                .andExpect(status().isOk());

        // verify it's gone
        mockMvc.perform(get("/api/accounts/" + savedAccount.getId())
                        .param("userId", "1"))
                .andExpect(status().isNotFound());
    }
}