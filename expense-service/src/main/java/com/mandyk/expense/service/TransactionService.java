package com.mandyk.expense.service;

import com.mandyk.expense.dto.TransactionCreateRequestDTO;
import com.mandyk.expense.dto.TransactionResponseDTO;
import com.mandyk.expense.entity.Transaction;
import com.mandyk.expense.exception.ResourceNotFoundException;
import com.mandyk.expense.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // CREATE
    public TransactionResponseDTO createTransaction(TransactionCreateRequestDTO request) {

        Transaction transaction = new Transaction();

        transaction.setUserId(request.getUserId());
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