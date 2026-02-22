package com.mandyk.expense.controller;

import com.mandyk.expense.dto.AccountDTO;
import com.mandyk.expense.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Get all accounts for user
    @GetMapping(path = "/users/{userId}")
    public List<AccountDTO> getAccounts(@PathVariable Integer userId) {
        return accountService.getAccountsByUserId(userId);
    }

    // Create account
    @PostMapping()
    public AccountDTO createAccount(@RequestBody AccountDTO dto) {
        return accountService.createAccount(dto);
    }

    // Get single account
    @GetMapping(path = "/{id}")
    public AccountDTO getAccountById(@PathVariable("id") Integer accountId, @RequestParam Integer userId) {
        return accountService.getAccountById(accountId, userId);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteAccountById(@PathVariable("id") Integer accountId, @RequestParam Integer userId) {
        accountService.deleteAccount(accountId, userId);
    }

}
