package com.mandyk.expense.integration;

import com.mandyk.expense.dto.ChangePasswordDTO;
import com.mandyk.expense.dto.UpdateProfileDTO;
import com.mandyk.expense.entity.User;
import com.mandyk.expense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private User savedUser;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("Sonia");
        user.setEmail("sonia@email.com");
        user.setPassword(passwordEncoder.encode("password123"));
        savedUser = userRepository.save(user);
    }

    // --- GET /api/users/me ---

    @Test
    void getProfileShouldReturnUserProfile() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .param("userId", savedUser.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("Sonia"))
                .andExpect(jsonPath("$.email").value("sonia@email.com"));
    }

    @Test
    void getProfileShouldFailWhenUserNotFound() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .param("userId", "9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProfileShouldReturn400WhenUserIdMissing() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isBadRequest());
    }

    // --- PUT /api/users/me ---

    @Test
    void updateProfileShouldUpdateAndReturnUser() throws Exception {
        UpdateProfileDTO dto = new UpdateProfileDTO();
        dto.setUserId(savedUser.getId());
        dto.setName("Sonia Updated");
        dto.setEmail("sonia.updated@email.com");

        mockMvc.perform(put("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sonia Updated"))
                .andExpect(jsonPath("$.email").value("sonia.updated@email.com"));
    }

    @Test
    void updateProfileShouldFailWhenUserNotFound() throws Exception {
        UpdateProfileDTO dto = new UpdateProfileDTO();
        dto.setUserId(9999);
        dto.setName("Ghost");
        dto.setEmail("ghost@email.com");

        mockMvc.perform(put("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // --- PUT /api/users/me/password ---

    @Test
    void changePasswordShouldSucceedWhenOldPasswordIsCorrect() throws Exception {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setUserId(savedUser.getId());
        dto.setOldPassword("password123");
        dto.setNewPassword("newPassword456");

        mockMvc.perform(put("/api/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void changePasswordShouldFailWhenOldPasswordIsWrong() throws Exception {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setUserId(savedUser.getId());
        dto.setOldPassword("wrongPassword");
        dto.setNewPassword("newPassword456");

        mockMvc.perform(put("/api/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // --- DELETE /api/users/me ---

    @Test
    void deleteUserShouldRemoveUser() throws Exception {
        mockMvc.perform(delete("/api/users/me")
                        .param("userId", savedUser.getId().toString()))
                .andExpect(status().isOk());

        // verify user is gone
        mockMvc.perform(get("/api/users/me")
                        .param("userId", savedUser.getId().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserShouldReturn400WhenUserIdMissing() throws Exception {
        mockMvc.perform(delete("/api/users/me"))
                .andExpect(status().isBadRequest());
    }
}