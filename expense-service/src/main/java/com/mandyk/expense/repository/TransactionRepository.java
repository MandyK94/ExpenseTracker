package com.mandyk.expense.repository;

import com.mandyk.expense.entity.Transaction;
import com.mandyk.expense.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Page<Transaction> findByUserId(Integer userId, Pageable pageable);

    Optional<Transaction> findByIdAndUserId(Integer id, Integer userId);

    Page<Transaction> findByUserIdAndTransactionType(Integer userId, TransactionType transactionType, Pageable pageable);

    Page<Transaction> findByUserIdAndTransactionDateBetween(Integer userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Transaction> findByUserIdAndAccountId(Integer userId, Integer accountId, Pageable pageable);

    Page<Transaction> findByUserIdAndCategoryId(Integer userId, Integer categoryId, Pageable pageable);

    // Total income
    @Query("""
            select COALESCE(SUM(t.amount), 0)
            from Transaction t
            where t.userId=:userId
            AND t.transactionType='INCOME'
            """)
    BigDecimal getTotalIncomeByUserId(Integer userId);

    // Total expense
    @Query("""
            select COALESCE(SUM(t.amount), 0)
            from Transaction t
            where t.userId = :userId
            AND t.transactionType='EXPENSE'
            """)
    BigDecimal getTotalExpenseByUserId(Integer userId);



    // Monthly aggregation
    @Query("""
            select DATE_TRUNC('month', t.transactionDate),
            SUM(t.amount)
            from Transaction t
            where t.userId=:userId
            AND t.transactionType='EXPENSE'
            GROUP BY DATE_TRUNC('month', t.transactionDate)
            ORDER BY DATE_TRUNC('month', t.transactionDate)
            """)
    List<Object[]> getMonthlyExpenseTrendByUserId(Integer userId);

    // Expense by categories
    @Query("""
            select t.categoryId, SUM(t.amount)
            from Transaction t
            where t.userId=:userId
            and t.transactionType='EXPENSE'
            group by t.categoryId
            """)
    List<Object[]> getExpenseByCategory(Integer userId);

}
