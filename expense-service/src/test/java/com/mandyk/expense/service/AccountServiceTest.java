package com.mandyk.expense.service;

import com.mandyk.expense.dto.AccountDTO;
import com.mandyk.expense.entity.Account;
import com.mandyk.expense.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldCreateAccount() {

        // Arrange
        AccountDTO account = new AccountDTO();
        account.setName("Savings");
        account.setUserId(1);

        Account savedAccount = new Account();
        savedAccount.setUserId(1);
        savedAccount.setName("Savings");
        savedAccount.setId(111);

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        // Act
        AccountDTO result = accountService.createAccount(account);

        // Assert
        assertNotNull(result);
        assertEquals(111, result.getId());
        assertEquals("Savings", result.getName());
        assertEquals(1, result.getUserId());
    }
}
