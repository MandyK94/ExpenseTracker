package com.mandyk.expense.service;

import com.mandyk.expense.dto.AccountDTO;
import com.mandyk.expense.entity.Account;
import com.mandyk.expense.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // Get all accounts for a user
    public List<AccountDTO> getAccountsByUserId(Integer userId) {

        List<Account> accounts = accountRepository.findByUserId(userId);

        return accounts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get single account
    public AccountDTO getAccountById(Integer accountId, Integer userId) {

        Account account = accountRepository
                .findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return mapToDTO(account);
    }

    // Create account
    public AccountDTO createAccount(AccountDTO dto) {

        Account account = new Account();

        account.setUserId(dto.getUserId());
        account.setName(dto.getName());

        Account saved = accountRepository.save(account);

        return mapToDTO(saved);
    }

    // Delete account
    public void deleteAccount(Integer accountId, Integer userId) {

        Account account = accountRepository
                .findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        accountRepository.delete(account);
    }

    // Mapper
    private AccountDTO mapToDTO(Account account) {

        AccountDTO dto = new AccountDTO();

        dto.setId(account.getId());
        dto.setName(account.getName());
        dto.setUserId(account.getUserId());
        dto.setCreatedAt(account.getCreatedAt());

        return dto;
    }

}
