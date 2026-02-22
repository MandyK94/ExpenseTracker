package com.mandyk.expense.service;

import com.mandyk.expense.dto.ChangePasswordDTO;
import com.mandyk.expense.dto.UpdateProfileDTO;
import com.mandyk.expense.dto.UserDTO;
import com.mandyk.expense.entity.User;
import com.mandyk.expense.exception.InvalidPasswordException;
import com.mandyk.expense.exception.ResourceNotFoundException;
import com.mandyk.expense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = new User("Mandeep", "mandeep@email.com", "password123");
        savedUser.setId(1);
    }

    // --- getProfile ---

    @Test
    void getProfileShouldReturnUserDTO() {
        when(userRepository.findById(1)).thenReturn(Optional.of(savedUser));

        UserDTO result = userService.getProfile(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Mandeep");
        assertThat(result.getEmail()).isEqualTo("mandeep@email.com");
    }

    @Test
    void getProfileShouldThrowWhenUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfile(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    // --- updateProfile ---

    @Test
    void updateProfileShouldUpdateAndReturnUserDTO() {
        UpdateProfileDTO dto = new UpdateProfileDTO();
        dto.setUserId(1);
        dto.setName("Mandeep Updated");
        dto.setEmail("mandeep.updated@email.com");

        User updatedUser = new User("Mandeep Updated", "mandeep.updated@email.com", "password123");
        updatedUser.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDTO result = userService.updateProfile(dto);

        assertThat(result.getName()).isEqualTo("Mandeep Updated");
        assertThat(result.getEmail()).isEqualTo("mandeep.updated@email.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateProfileShouldThrowWhenUserNotFound() {
        UpdateProfileDTO dto = new UpdateProfileDTO();
        dto.setUserId(99);
        dto.setName("Ghost");
        dto.setEmail("ghost@email.com");

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateProfile(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository, never()).save(any());
    }

    // --- changePassword ---

    @Test
    void changePasswordShouldSucceedWhenOldPasswordMatches() {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setUserId(1);
        dto.setOldPassword("password123");
        dto.setNewPassword("newPassword456");

        when(userRepository.findById(1)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(passwordEncoder.matches("password123", "password123")).thenReturn(true);

        assertThatNoException().isThrownBy(() -> userService.changePassword(dto));

        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePasswordShouldThrowWhenOldPasswordIsWrong() {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setUserId(1);
        dto.setOldPassword("wrongPassword");
        dto.setNewPassword("newPassword456");

        when(userRepository.findById(1)).thenReturn(Optional.of(savedUser));

        assertThatThrownBy(() -> userService.changePassword(dto))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Old password incorrect");

        verify(userRepository, never()).save(any());
    }

    @Test
    void changePasswordShouldThrowWhenUserNotFound() {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setUserId(99);
        dto.setOldPassword("password123");
        dto.setNewPassword("newPassword456");

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository, never()).save(any());
    }

    // --- deleteUserById ---

    @Test
    void deleteUserByIdShouldCallRepositoryDelete() {
        when(userRepository.existsById(1)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1);

        userService.deleteUserById(1);

        verify(userRepository).deleteById(1);
    }

    @Test
    void deleteUserByIdShouldThrowWhenUserNotFound() {
        when(userRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUserById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}