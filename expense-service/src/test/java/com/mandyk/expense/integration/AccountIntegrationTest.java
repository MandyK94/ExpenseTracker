package com.mandyk.expense.integration;

import com.mandyk.expense.dto.AccountDTO;
import com.mandyk.expense.repository.BaseRepositoryTest;
import com.mandyk.expense.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AccountIntegrationTest extends BaseRepositoryTest {

    @Autowired
    private AccountService accountService;

    @Test
    void shouldCreateAccount() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setName("Bank1");
        accountDTO.setUserId(1);

        AccountDTO saved = accountService.createAccount(accountDTO);

        assertNotNull(saved.getId());
    }

}
