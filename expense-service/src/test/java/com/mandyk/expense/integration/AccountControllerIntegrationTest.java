package com.mandyk.expense.integration;

import com.mandyk.expense.dto.AccountDTO;
import com.mandyk.expense.entity.Account;
import com.mandyk.expense.entity.User;
import com.mandyk.expense.repository.AccountRepository;
import com.mandyk.expense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class AccountControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private Account savedAccount;

    private String token;


    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("sonia@email.com");
        user.setName("Sonia");
        user.setPassword(passwordEncoder.encode("password123"));
        User savedUser = userRepository.save(user);
        token = generateTestToken(savedUser.getId(), savedUser.getEmail());

        savedAccount = accountRepository.save(new Account("Checking", savedUser.getId()));
    }

    @Test
    void getAccountsShouldReturnAccountsForUser() throws Exception {
        mockMvc.perform(get("/api/accounts/users")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Checking"));
    }

    @Test
    void getAccountsShouldReturnEmptyWhenUserHasNoAccounts() throws Exception {
        // create a second user with no accounts
        User otherUser = new User();
        otherUser.setEmail("other@email.com");
        otherUser.setName("Other");
        otherUser.setPassword(passwordEncoder.encode("password123"));
        User savedOtherUser = userRepository.save(otherUser);

        String otherToken = generateTestToken(savedOtherUser.getId(), savedOtherUser.getEmail());

        mockMvc.perform(get("/api/accounts/users")
                        .header("Authorization", otherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAccountByIdShouldReturnAccount() throws Exception {

        mockMvc.perform(get("/api/accounts/" + savedAccount.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Checking"));
    }

    @Test
    void getAccountByIdShouldFailWhenNotFound() throws Exception {

        mockMvc.perform(get("/api/accounts/9999")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAccountShouldSaveAndReturnAccount() throws Exception {
        AccountDTO dto = new AccountDTO();
        dto.setName("Savings");
        dto.setUserId(1);



        mockMvc.perform(post("/api/accounts")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Savings"));
    }

    @Test
    void deleteAccountShouldRemoveAccount() throws Exception {
        mockMvc.perform(delete("/api/accounts/" + savedAccount.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk());

        // verify it's gone
        mockMvc.perform(get("/api/accounts/" + savedAccount.getId())
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }
}