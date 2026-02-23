package com.mandyk.expense.service;

import com.mandyk.expense.dto.TransactionCreateRequestDTO;
import com.mandyk.expense.dto.TransactionResponseDTO;
import com.mandyk.expense.entity.Account;
import com.mandyk.expense.entity.Category;
import com.mandyk.expense.entity.Transaction;
import com.mandyk.expense.entity.TransactionType;
import com.mandyk.expense.exception.ResourceNotFoundException;
import com.mandyk.expense.repository.AccountRepository;
import com.mandyk.expense.repository.CategoryRepository;
import com.mandyk.expense.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction savedTransaction;
    private Account savedAccount;
    private Category savedCategory;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        savedTransaction = new Transaction();
        savedTransaction.setId(1);
        savedTransaction.setUserId(1);
        savedTransaction.setAccountId(1);
        savedTransaction.setCategoryId(1);
        savedTransaction.setAmount(new BigDecimal("100.00"));
        savedTransaction.setDescription("Grocery shopping");
        savedTransaction.setTransactionType(TransactionType.EXPENSE);
        savedTransaction.setTransactionDate(LocalDateTime.now());

        savedAccount = new Account();
        savedAccount.setName("Savings");
        savedAccount.setUserId(1);
        savedAccount.setId(1);

        savedCategory = new Category();
        savedCategory.setName("Shopping");
        savedCategory.setUserId(1);
        savedCategory.setId(1);

        pageable = PageRequest.of(0, 10);
    }

    // --- createTransaction ---

    @Test
    void createTransactionShouldSaveAndReturnResponse() {


        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(accountRepository.findByIdAndUserId(any(Integer.class), any(Integer.class))).thenReturn(Optional.of(savedAccount));
        when(categoryRepository.findByIdAndUserId(any(Integer.class), any(Integer.class))).thenReturn(Optional.of(savedCategory));

        TransactionCreateRequestDTO request = new TransactionCreateRequestDTO();
        request.setAccountId(1);
        request.setCategoryId(1);
        request.setAmount(new BigDecimal("100.00"));
        request.setDescription("Grocery shopping");
        request.setTransactionType(TransactionType.EXPENSE);
        request.setTransactionDate(LocalDateTime.now());

        TransactionResponseDTO result = transactionService.createTransaction(request, 1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getAmount()).isEqualByComparingTo("100.00");
        assertThat(result.getDescription()).isEqualTo("Grocery shopping");
        verify(transactionRepository).save(any(Transaction.class));
    }

    // --- getTransactionsByUserId ---

    @Test
    void getTransactionsByUserIdShouldReturnPage() {
        Page<Transaction> page = new PageImpl<>(List.of(savedTransaction));
        when(transactionRepository.findByUserId(1, pageable)).thenReturn(page);

        Page<TransactionResponseDTO> result = transactionService.getTransactionsByUserId(1, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getDescription()).isEqualTo("Grocery shopping");
    }

    @Test
    void getTransactionsByUserIdShouldReturnEmptyPageWhenNoTransactions() {
        Page<Transaction> emptyPage = new PageImpl<>(List.of());
        when(transactionRepository.findByUserId(1, pageable)).thenReturn(emptyPage);

        Page<TransactionResponseDTO> result = transactionService.getTransactionsByUserId(1, pageable);

        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    // --- getTransactionsByAccountId ---

    @Test
    void getTransactionsByAccountIdShouldReturnPage() {
        Page<Transaction> page = new PageImpl<>(List.of(savedTransaction));
        when(transactionRepository.findByUserIdAndAccountId(1, 1, pageable)).thenReturn(page);
        when(accountRepository.findByIdAndUserId(any(Integer.class), any(Integer.class))).thenReturn(Optional.of(savedAccount));
        Page<TransactionResponseDTO> result = transactionService.getTransactionsByAccountId(1, 1, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getAccountId()).isEqualTo(1);
    }

    // --- getTransactionsByCategoryId ---

    @Test
    void getTransactionsByCategoryIdShouldReturnPage() {
        Page<Transaction> page = new PageImpl<>(List.of(savedTransaction));
        when(transactionRepository.findByUserIdAndCategoryId(1, 1, pageable)).thenReturn(page);

        Page<TransactionResponseDTO> result = transactionService.getTransactionsByCategoryId(1, 1, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getCategoryId()).isEqualTo(1);
    }

    // --- getTransaction ---

    @Test
    void getTransactionShouldReturnTransaction() {
        when(transactionRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(savedTransaction));

        TransactionResponseDTO result = transactionService.getTransaction(1, 1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getDescription()).isEqualTo("Grocery shopping");
    }

    @Test
    void getTransactionShouldThrowWhenNotFound() {
        when(transactionRepository.findByIdAndUserId(99, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getTransaction(99, 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Transaction not found");
    }

    // --- deleteTransaction ---

    @Test
    void deleteTransactionShouldDeleteSuccessfully() {
        when(transactionRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(savedTransaction));
        doNothing().when(transactionRepository).delete(savedTransaction);

        transactionService.deleteTransaction(1, 1);

        verify(transactionRepository).delete(savedTransaction);
    }

    @Test
    void deleteTransactionShouldThrowWhenNotFound() {
        when(transactionRepository.findByIdAndUserId(99, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.deleteTransaction(99, 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Transaction not found");

        verify(transactionRepository, never()).delete(any());
    }

    // --- getAccountBalance ---

    @Test
    void getAccountBalanceShouldReturnBalance() {
        when(transactionRepository.getAccountBalance(1, 1)).thenReturn(new BigDecimal("500.00"));
        when(accountRepository.findByIdAndUserId(any(Integer.class), any(Integer.class))).thenReturn(Optional.of(savedAccount));
        BigDecimal balance = transactionService.getAccountBalance(1, 1);

        assertThat(balance).isEqualByComparingTo("500.00");
    }

    @Test
    void getAccountBalanceShouldReturnZeroWhenNoTransactions() {
        when(transactionRepository.getAccountBalance(1, 1)).thenReturn(BigDecimal.ZERO);
        when(accountRepository.findByIdAndUserId(any(Integer.class), any(Integer.class))).thenReturn(Optional.of(savedAccount));
        BigDecimal balance = transactionService.getAccountBalance(1, 1);

        assertThat(balance).isEqualByComparingTo(BigDecimal.ZERO);
    }
}