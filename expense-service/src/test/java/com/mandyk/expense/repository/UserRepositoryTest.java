package com.mandyk.expense.repository;

import com.mandyk.expense.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setEmail("example1@gmail.com");
        user.setPassword("password");
        testEntityManager.persist(user);
        testEntityManager.flush();
        testEntityManager.clear();
    }

    @Test
    @DisplayName("should save new user")
    void shouldSaveUser() {

        // Arrange
        User newUser = new User();
        newUser.setEmail("example2@gmail.com");
        newUser.setPassword("password");

        // Act
        User saved = userRepository.save(newUser);

        // Assertion
        assertNotNull(saved.getId());
    }


    @Test
    @DisplayName("should return a user by email id")
    void shouldReturnUserByEmail() {

        // Arrange
        User user = new User("example3@gmail.com", "password1");
        testEntityManager.persist(user);
        testEntityManager.flush();

        // Act
        Optional<User> result = userRepository.findByEmail("example3@gmail.com");

        // Assertion
        assertThat(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
    }

    @Test
    @DisplayName("should throw exception for saving user with duplicate email")
    void shouldThrowExceptionDuplicateEmail() {
        User duplicate = new User();
        duplicate.setEmail("example1@gmail.com");
        duplicate.setPassword("password");

        assertThrows(Exception.class, ()->{
            testEntityManager.persistAndFlush(duplicate);
        });
    }

    @Test
    @DisplayName("shuold return true if user with email exists")
    void shouldReturnTrueIfExists() {
        boolean exists = userRepository.existsByEmail("example1@gmail.com");
        assertTrue(exists);
    }

    @Test
    @DisplayName("shuold return false if user with email does not exist")
    void shouldReturnFalseIfExists() {
        boolean exists = userRepository.existsByEmail("example0@gmail.com");
        assertFalse(exists);
    }
}