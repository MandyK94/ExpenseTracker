package com.mandyk.expense.controller;

import com.mandyk.expense.dto.TransactionCreateRequestDTO;
import com.mandyk.expense.dto.TransactionResponseDTO;
import com.mandyk.expense.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // CREATE
    @PostMapping
    public TransactionResponseDTO createTransaction(
            @RequestBody TransactionCreateRequestDTO requestDTO) {

        return transactionService.createTransaction(requestDTO);
    }


    // GET ALL BY USER (paginated)
    @GetMapping("/user/{userId}")
    public Page<TransactionResponseDTO> getTransactionsByUser(
            @PathVariable Integer userId,
            Pageable pageable) {

        return transactionService.getTransactionsByUserId(userId, pageable);
    }


    // GET BY ACCOUNT (paginated)
    @GetMapping("/account/{accountId}/user/{userId}")
    public Page<TransactionResponseDTO> getTransactionsByAccount(
            @PathVariable Integer accountId,
            @PathVariable Integer userId,
            Pageable pageable) {

        return transactionService.getTransactionsByAccountId(userId, accountId, pageable);
    }


    // GET BY CATEGORY (paginated)
    @GetMapping("/category/{categoryId}/user/{userId}")
    public Page<TransactionResponseDTO> getTransactionsByCategory(
            @PathVariable Integer categoryId,
            @PathVariable Integer userId,
            Pageable pageable) {

        return transactionService.getTransactionsByCategoryId(userId, categoryId, pageable);
    }


    // GET SINGLE
    @GetMapping("/{txnId}/user/{userId}")
    public TransactionResponseDTO getTransaction(
            @PathVariable Integer txnId,
            @PathVariable Integer userId) {

        return transactionService.getTransaction(txnId, userId);
    }


    // DELETE
    @DeleteMapping("/{txnId}/user/{userId}")
    public void deleteTransaction(
            @PathVariable Integer txnId,
            @PathVariable Integer userId) {

        transactionService.deleteTransaction(txnId, userId);
    }
}