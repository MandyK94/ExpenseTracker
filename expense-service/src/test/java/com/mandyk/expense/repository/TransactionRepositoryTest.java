package com.mandyk.expense.repository;

import com.mandyk.expense.entity.Transaction;
import com.mandyk.expense.entity.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Transaction income1;
    private Transaction expense1;
    private Transaction expense2;
    private Transaction otherUserTxn;

    @BeforeEach
    void setup() {
        income1 = createTransaction(
                new BigDecimal("1000.00"),
                TransactionType.INCOME,
                1, 10, 100,
                LocalDateTime.of(2025, 1, 10, 10, 0)
        );

        expense1 = createTransaction(
                new BigDecimal("200.00"),
                TransactionType.EXPENSE,
                1, 10, 200,
                LocalDateTime.of(2025, 1, 15, 10, 0)
        );

        expense2 = createTransaction(
                new BigDecimal("300.00"),
                TransactionType.EXPENSE,
                1, 20, 200,
                LocalDateTime.of(2025, 2, 10, 10, 0)
        );

        otherUserTxn = createTransaction(
                new BigDecimal("999.00"),
                TransactionType.EXPENSE,
                2, 10, 200,
                LocalDateTime.of(2025, 1, 10, 10, 0)
        );

        testEntityManager.persist(income1);
        testEntityManager.persist(expense1);
        testEntityManager.persist(expense2);
        testEntityManager.persist(otherUserTxn);
        testEntityManager.flush();
        testEntityManager.clear();
    }

    private Transaction createTransaction(
            BigDecimal amount,
            TransactionType type,
            Integer userId,
            Integer accountId,
            Integer categoryId,
            LocalDateTime date) {
        Transaction txn = new Transaction();
        txn.setAmount(amount);
        txn.setTransactionType(type);
        txn.setUserId(userId);
        txn.setAccountId(accountId);
        txn.setCategoryId(categoryId);
        txn.setTransactionDate(date);
        txn.setCreatedAt(LocalDateTime.now());
        return txn;
    }

    @Test
    void shouldFindTxnsByUserId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> page = transactionRepository.findByUserId(1, pageable);
        assertThat(page.getTotalElements()).isEqualTo(3);
    }

    @Test
    void shouldFindByIdAndUserId() {

        Optional<Transaction> found = transactionRepository.findByIdAndUserId(income1.getId(), 1);

        assertThat(found).isPresent();
        assertThat(found.get().getAmount()).isEqualByComparingTo("1000.00");
    }

    @Test
    void shouldNotFindIfUserIdDoesNotMatch() {

        Optional<Transaction> found = transactionRepository.findByIdAndUserId(income1.getId(), 999);

        assertThat(found).isEmpty();
    }
    @Test
    void shouldFindExpensesByUserId() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> page = transactionRepository.findByUserIdAndTransactionType(
                        1,
                        TransactionType.EXPENSE,
                        pageable
                );

        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void shouldFindByAccountId() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> page = transactionRepository.findByUserIdAndAccountId(1, 10, pageable);

        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void shouldFindByDateRange() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> page = transactionRepository.findByUserIdAndTransactionDateBetween(
                        1,
                        LocalDateTime.of(2025, 1, 1, 0, 0),
                        LocalDateTime.of(2025, 1, 31, 23, 59),
                        pageable
                );

        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void shouldCalculateTotalIncome() {
        BigDecimal total = transactionRepository.getTotalIncomeByUserId(1);
        assertThat(total).isEqualByComparingTo("1000.00");
    }

    @Test
    void shouldCalculateTotalExpense() {
        BigDecimal total = transactionRepository.getTotalExpenseByUserId(1);
        assertThat(total).isEqualByComparingTo("500.00");
    }

    @Test
    void shouldGroupExpenseByCategory() {

        List<Object[]> results = transactionRepository.getExpenseByCategory(1);

        assertThat(results).hasSize(1);

        Integer categoryId = (Integer) results.get(0)[0];
        BigDecimal total = (BigDecimal) results.get(0)[1];

        assertThat(categoryId).isEqualTo(200);
        assertThat(total).isEqualByComparingTo("500.00");
    }

    @Test
    void shouldReturnMonthlyExpenseTrend() {

        List<Object[]> results = transactionRepository.getMonthlyExpenseTrendByUserId(1);

        assertThat(results).hasSize(2);

        BigDecimal janTotal = (BigDecimal) results.get(0)[1];
        BigDecimal febTotal = (BigDecimal) results.get(1)[1];

        assertThat(janTotal).isEqualByComparingTo("200.00");
        assertThat(febTotal).isEqualByComparingTo("300.00");
    }

    @Test
    void shouldSaveTransaction() {

        Transaction t = createTransaction(
                new BigDecimal("50.00"),
                TransactionType.EXPENSE,
                1,
                10,
                200,
                LocalDateTime.now()
        );
        Transaction saved = transactionRepository.save(t);

        assertThat(saved.getId()).isNotNull();
    }




}
