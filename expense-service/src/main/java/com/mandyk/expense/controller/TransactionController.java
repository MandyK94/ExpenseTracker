package com.mandyk.expense.controller;

import com.mandyk.expense.dto.BalanceDTO;
import com.mandyk.expense.dto.TransactionCreateRequestDTO;
import com.mandyk.expense.dto.TransactionResponseDTO;
import com.mandyk.expense.service.TransactionService;
import com.mandyk.expense.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private TransactionService transactionService;
    private JwtUtil jwtUtil;

    public TransactionController(TransactionService transactionService, JwtUtil jwtUtil) {
        this.transactionService = transactionService;
        this.jwtUtil = jwtUtil;
    }

    // CREATE
    @PostMapping
    public TransactionResponseDTO createTransaction(@RequestBody TransactionCreateRequestDTO requestDTO, HttpServletRequest request) {
        return transactionService.createTransaction(requestDTO, jwtUtil.getUserIdFromRequest(request));
    }


    // GET ALL BY USER (paginated)
    @GetMapping("/user")
    public Page<TransactionResponseDTO> getTransactionsByUser(HttpServletRequest request, Pageable pageable) {

        return transactionService.getTransactionsByUserId(jwtUtil.getUserIdFromRequest(request), pageable);
    }


    // GET BY ACCOUNT (paginated)
    @GetMapping("/account/{accountId}")
    public Page<TransactionResponseDTO> getTransactionsByAccount(
            @PathVariable Integer accountId, HttpServletRequest request,
            Pageable pageable) {

        return transactionService.getTransactionsByAccountId(jwtUtil.getUserIdFromRequest(request), accountId, pageable);
    }


    // GET BY CATEGORY (paginated)
    @GetMapping("/category/{categoryId}")
    public Page<TransactionResponseDTO> getTransactionsByCategory(@PathVariable Integer categoryId, HttpServletRequest request, Pageable pageable) {

        return transactionService.getTransactionsByCategoryId(jwtUtil.getUserIdFromRequest(request), categoryId, pageable);
    }


    // GET SINGLE
    @GetMapping("/{txnId}")
    public TransactionResponseDTO getTransaction(@PathVariable Integer txnId, HttpServletRequest request) {

        return transactionService.getTransaction(txnId, jwtUtil.getUserIdFromRequest(request));
    }

    @GetMapping("/{accountId}/balance")
    public BalanceDTO getBalance(@PathVariable Integer accountId, HttpServletRequest request) {

        BigDecimal balance =
                transactionService.getAccountBalance(accountId, jwtUtil.getUserIdFromRequest(request));

        BalanceDTO dto = new BalanceDTO();
        dto.setAccountId(accountId);
        dto.setBalance(balance);

        return dto;
    }

    // DELETE
    @DeleteMapping("/{txnId}")
    public void deleteTransaction(@PathVariable Integer txnId, HttpServletRequest request) {

        transactionService.deleteTransaction(txnId, jwtUtil.getUserIdFromRequest(request));
    }
}