package com.mandyk.expense.service;

import com.mandyk.expense.dto.AuthRequest;
import com.mandyk.expense.dto.AuthResponse;
import com.mandyk.expense.entity.User;
import com.mandyk.expense.exception.InvalidPasswordException;
import com.mandyk.expense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User savedUser;
    private AuthRequest request;

    @BeforeEach
    void setUp() {
        request = new AuthRequest();
        request.setEmail("mandeep@email.com");
        request.setPassword("password123");

        savedUser = new User("Mandeep", "mandeep@email.com", "encodedPassword");
        savedUser.setId(1);
    }

    // -- register User --
    @Test
    void registerShouldSaveAndReturnUser() {
        when(userRepository.existsByEmail("mandeep@email.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AuthResponse response = authService.registerUser(request);

        assertThat(response.getEmail()).isEqualTo("mandeep@email.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUserShouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("mandeep@email.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.registerUser(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    void loginUserShouldReturnResponseWhenCredentialsAreCorrect() {
        when(userRepository.findByEmail("mandeep@email.com")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        AuthResponse response = authService.loginUser(request);

        assertThat(response.getEmail()).isEqualTo("mandeep@email.com");
        assertThat(response.getId()).isEqualTo(1);
    }

    @Test
    void loginUserShouldThrowWhenEmailNotFound() {
        when(userRepository.findByEmail("mandeep@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.loginUser(request))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void loginUserShouldThrowWhenPasswordIsWrong() {
        when(userRepository.findByEmail("mandeep@email.com")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.loginUser(request))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Invalid email or Password");
    }

}
