package com.mandyk.expense.repository;

import com.mandyk.expense.entity.Account;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.*;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("Should find accounts by user id")
    void shouldFindAccountByUserId() {

        //Arrange
        Account account1 = new Account("Savings", 1);
        Account account2 = new Account("Current", 1);

        testEntityManager.persist(account1);
        testEntityManager.persist(account2);
        testEntityManager.flush();
        testEntityManager.clear();

        // Act
        List<Account> accounts = accountRepository.findByUserId(1);

        // Assert
        assertThat(accounts).hasSize(2);
        assertThat(accounts)
                .extracting(Account::getName)
                .contains("Savings", "Current");
    }

    @Test
    @DisplayName("should find account by account id and user id")
    void shouldFindByIdAndUserId() {
        // Arrange
        Account account = new Account("Investments", 2);
        testEntityManager.persist(account);
        testEntityManager.flush();
        testEntityManager.clear();

        // Act
        Optional<Account> result = accountRepository.findByIdAndUserId(account.getId(), 2);

        // Assertions
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Investments");
    }

    @Test
    @DisplayName("Should return empty when account does not belong to user")
    void shouldReturnEmptyWhenWrongUser() {
        Account account = new Account("Private", 3);
        testEntityManager.persist(account);
        testEntityManager.flush();
        testEntityManager.clear();

        // Act
        Optional<Account> result = accountRepository.findByIdAndUserId(account.getId(), 1);

        // Assertions
        assertThat(result).isEmpty();
    }
}
