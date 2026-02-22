package com.mandyk.expense.controller;

import com.mandyk.expense.dto.AccountDTO;
import com.mandyk.expense.service.AccountService;
import com.mandyk.expense.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private AccountService accountService;
    private JwtUtil jwtUtil;

    public AccountController(AccountService accountService, JwtUtil jwtUtil) {
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
    }

    // Get all accounts for user
    @GetMapping(path = "/users")
    public List<AccountDTO> getAccounts(HttpServletRequest request) {
        Integer userId = jwtUtil.getUserIdFromRequest(request);
        return accountService.getAccountsByUserId(userId);
    }

    // Create account
    @PostMapping()
    public AccountDTO createAccount(@RequestBody AccountDTO dto, HttpServletRequest request) {
        dto.setUserId(jwtUtil.getUserIdFromRequest(request));
        return accountService.createAccount(dto);
    }

    // Get single account
    @GetMapping(path = "/{id}")
    public AccountDTO getAccountById(@PathVariable("id") Integer accountId, HttpServletRequest request) {
        return accountService.getAccountById(accountId, jwtUtil.getUserIdFromRequest(request));
    }

    @DeleteMapping(path = "/{id}")
    public void deleteAccountById(@PathVariable("id") Integer accountId, HttpServletRequest request) {
        accountService.deleteAccount(accountId, jwtUtil.getUserIdFromRequest(request));
    }

}
