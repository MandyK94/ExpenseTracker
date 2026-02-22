package com.mandyk.expense.service;

import com.mandyk.expense.dto.AccountDTO;
import com.mandyk.expense.entity.Account;
import com.mandyk.expense.exception.ResourceNotFoundException;
import com.mandyk.expense.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account savedAccount;

    @BeforeEach
    void setUp() {
        savedAccount = new Account("Checking", 1);
        savedAccount.setId(1);
    }

    // --- getAccountsByUserId ---

    @Test
    void getAccountsByUserIdShouldReturnListOfAccounts() {
        when(accountRepository.findByUserId(1)).thenReturn(List.of(savedAccount));

        List<AccountDTO> result = accountService.getAccountsByUserId(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Checking");
        assertThat(result.get(0).getUserId()).isEqualTo(1);
    }

    @Test
    void getAccountsByUserIdShouldReturnEmptyListWhenNoAccounts() {
        when(accountRepository.findByUserId(1)).thenReturn(List.of());

        List<AccountDTO> result = accountService.getAccountsByUserId(1);

        assertThat(result).isEmpty();
    }

    // --- getAccountById ---

    @Test
    void getAccountByIdShouldReturnAccount() {
        when(accountRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(savedAccount));

        AccountDTO result = accountService.getAccountById(1, 1);

        assertThat(result.getName()).isEqualTo("Checking");
        assertThat(result.getUserId()).isEqualTo(1);
    }

    @Test
    void getAccountByIdShouldThrow_whenNotFound() {
        when(accountRepository.findByIdAndUserId(99, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccountById(99, 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Account not found");
    }

    // --- createAccount ---

    @Test
    void createAccountShouldSaveAndReturnAccount() {
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        AccountDTO dto = new AccountDTO();
        dto.setName("Checking");
        dto.setUserId(1);

        AccountDTO result = accountService.createAccount(dto);

        assertThat(result.getName()).isEqualTo("Checking");
        assertThat(result.getUserId()).isEqualTo(1);
        verify(accountRepository).save(any(Account.class));
    }

    // --- deleteAccount ---

    @Test
    void deleteAccountShouldDeleteSuccessfully() {
        when(accountRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(savedAccount));
        doNothing().when(accountRepository).delete(savedAccount);

        accountService.deleteAccount(1, 1);

        verify(accountRepository).delete(savedAccount);
    }

    @Test
    void deleteAccountShouldThrowWhenNotFound() {
        when(accountRepository.findByIdAndUserId(99, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.deleteAccount(99, 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Account not found");

        verify(accountRepository, never()).delete(any());
    }
}