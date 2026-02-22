package com.mandyk.expense;

import com.mandyk.expense.repository.BaseRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ExpenseServiceApplicationTests extends BaseRepositoryTest {

	@Test
	void contextLoads() {
	}

}
