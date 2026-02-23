package com.mandyk.expense.integration;

import com.mandyk.expense.dto.TransactionCreateRequestDTO;
import com.mandyk.expense.entity.*;
import com.mandyk.expense.repository.AccountRepository;
import com.mandyk.expense.repository.CategoryRepository;
import com.mandyk.expense.repository.TransactionRepository;
import com.mandyk.expense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class TransactionControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String token;
    private Account savedAccount;
    private Category savedCategory;
    private User savedUser;
    private Transaction savedTransaction;

    @BeforeEach
    void setUp() {
        savedUser = new User();
        savedUser.setName("Mandeep");
        savedUser.setEmail("mandeep@email.com");
        savedUser.setPassword(passwordEncoder.encode("Password123"));
        savedUser = userRepository.save(savedUser);

        token = generateTestToken(savedUser.getId(), savedUser.getEmail());
        savedAccount = accountRepository.save(new Account("Checking", savedUser.getId()));
        savedCategory = categoryRepository.save(new Category(savedUser.getId(), "Food"));

        Transaction t = new Transaction();
        t.setUserId(savedUser.getId());
        t.setAccountId(savedAccount.getId());
        t.setCategoryId(savedCategory.getId());
        t.setAmount(new BigDecimal("100.00"));
        t.setDescription("Grocery shopping");
        t.setTransactionType(TransactionType.EXPENSE);
        t.setTransactionDate(LocalDateTime.now().minusDays(1));
        savedTransaction = transactionRepository.save(t);
    }

    @Test
    void createTransactionShouldSaveAndReturnTransaction() throws Exception {
        TransactionCreateRequestDTO request = new TransactionCreateRequestDTO();
        request.setAccountId(savedAccount.getId());
        request.setCategoryId(savedCategory.getId());
        request.setAmount(new BigDecimal("50.00"));
        request.setDescription("Coffee");
        request.setTransactionType(TransactionType.EXPENSE);
        request.setTransactionDate(LocalDateTime.now());
        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.description").value("Coffee"))
                .andExpect(jsonPath("$.amount").value(50.00));
    }

    @Test
    void getTransactionsByUserShouldReturnPagedResults() throws Exception {
        mockMvc.perform(get("/api/transactions/user")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description").value("Grocery shopping"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getTransactionsByAccountShouldReturnPagedResults() throws Exception {
        mockMvc.perform(get("/api/transactions/account/"+savedAccount.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].accountId").value(savedAccount.getId()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getTransactionsByCategoryShouldReturnPagedResults() throws Exception {
        mockMvc.perform(get("/api/transactions/category/"+savedCategory.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].categoryId").value(savedCategory.getId()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getTransactionShouldReturnSingleTransaction() throws Exception {
        mockMvc.perform(get("/api/transactions/" + savedTransaction.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedTransaction.getId()))
                .andExpect(jsonPath("$.description").value("Grocery shopping"));
    }

    @Test
    void getBalanceShouldReturnCorrectBalance() throws Exception {
        mockMvc.perform(get("/api/transactions/"+ savedAccount.getId()+ "/balance")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(savedAccount.getId()))
                .andExpect(jsonPath("$.balance").isNumber());
    }

    @Test
    void deleteTransactionShouldRemoveTransaction() throws Exception {
        mockMvc.perform(delete("/api/transactions/" + savedTransaction.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk());

        // verify it's gone
        mockMvc.perform(get("/api/transactions/" + savedTransaction.getId())
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }
}