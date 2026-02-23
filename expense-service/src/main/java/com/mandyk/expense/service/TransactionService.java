package com.mandyk.expense.service;

import com.mandyk.expense.dto.TransactionCreateRequestDTO;
import com.mandyk.expense.dto.TransactionResponseDTO;
import com.mandyk.expense.entity.Transaction;
import com.mandyk.expense.exception.ResourceNotFoundException;
import com.mandyk.expense.repository.AccountRepository;
import com.mandyk.expense.repository.CategoryRepository;
import com.mandyk.expense.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    // CREATE
    public TransactionResponseDTO createTransaction(TransactionCreateRequestDTO request, Integer userId) {

        accountRepository.findByIdAndUserId(request.getAccountId(), userId)
                .orElseThrow(()-> new ResourceNotFoundException("Account not found"));

        if(request.getCategoryId() != null) {
            categoryRepository.findByIdAndUserId(request.getCategoryId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        if(request.getTransactionDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Transaction date cannot be in future");
        }

        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAccountId(request.getAccountId());
        transaction.setCategoryId(request.getCategoryId());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setTransactionDate(request.getTransactionDate());

        Transaction saved = transactionRepository.save(transaction);

        return mapToResponse(saved);
    }


    // GET ALL BY USER (paginated)
    public Page<TransactionResponseDTO> getTransactionsByUserId(
            Integer userId,
            Pageable pageable) {

        return transactionRepository
                .findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }


    // GET BY ACCOUNT (paginated)
    public Page<TransactionResponseDTO> getTransactionsByAccountId(
            Integer userId,
            Integer accountId,
            Pageable pageable) {

        accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        return transactionRepository
                .findByUserIdAndAccountId(userId, accountId, pageable)
                .map(this::mapToResponse);
    }


    // GET BY CATEGORY (paginated)
    public Page<TransactionResponseDTO> getTransactionsByCategoryId(
            Integer userId,
            Integer categoryId,
            Pageable pageable) {

        return transactionRepository
                .findByUserIdAndCategoryId(userId, categoryId, pageable)
                .map(this::mapToResponse);
    }


    // GET SINGLE
    public TransactionResponseDTO getTransaction(
            Integer txnId,
            Integer userId) {

        Transaction transaction = transactionRepository
                .findByIdAndUserId(txnId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transaction not found"));

        return mapToResponse(transaction);
    }


    // DELETE
    public void deleteTransaction(
            Integer txnId,
            Integer userId) {

        Transaction transaction = transactionRepository
                .findByIdAndUserId(txnId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transaction not found"));

        transactionRepository.delete(transaction);
    }

    // GET BALANCE
    public BigDecimal getAccountBalance(Integer accountId, Integer userId) {

        accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        return transactionRepository.getAccountBalance(userId, accountId);
    }

    private TransactionResponseDTO mapToResponse(Transaction t) {

        TransactionResponseDTO dto = new TransactionResponseDTO();

        dto.setId(t.getId());
        dto.setUserId(t.getUserId());
        dto.setAccountId(t.getAccountId());
        dto.setCategoryId(t.getCategoryId());
        dto.setAmount(t.getAmount());
        dto.setDescription(t.getDescription());
        dto.setTransactionType(t.getTransactionType());
        dto.setTransactionDate(t.getTransactionDate());
        dto.setCreatedAt(t.getCreatedAt());

        return dto;
    }
}