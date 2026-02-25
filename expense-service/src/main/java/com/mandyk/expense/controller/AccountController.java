package com.mandyk.expense.controller;

import com.mandyk.expense.dto.AccountDTO;
import com.mandyk.expense.service.AccountService;
import com.mandyk.expense.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

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
        log.debug("List all the accounts for user: "+ userId);
        return accountService.getAccountsByUserId(userId);
    }

    // Create account
    @PostMapping()
    public AccountDTO createAccount(@Valid @RequestBody AccountDTO dto, HttpServletRequest request) {
        Integer userId = jwtUtil.getUserIdFromRequest(request);
        dto.setUserId(userId);
        log.debug("Create account for user: "+ userId);
        return accountService.createAccount(dto);
    }

    // Get single account
    @GetMapping(path = "/{id}")
    public AccountDTO getAccountById(@PathVariable("id") Integer accountId, HttpServletRequest request) {
        Integer userId = jwtUtil.getUserIdFromRequest(request);
        log.debug("Create account " + accountId +" for user: "+ userId);
        return accountService.getAccountById(accountId, userId);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteAccountById(@PathVariable("id") Integer accountId, HttpServletRequest request) {
        Integer userId = jwtUtil.getUserIdFromRequest(request);
        log.debug("Delete account " + accountId +" for user: "+ userId);
        accountService.deleteAccount(accountId, userId);
    }

}
