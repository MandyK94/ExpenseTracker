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
    private String token;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("Sonia");
        user.setEmail("sonia@email.com");
        user.setPassword(passwordEncoder.encode("password123"));
        savedUser = userRepository.save(user);
        token = generateTestToken(savedUser.getId(), savedUser.getEmail());

    }

    // --- GET /api/users/me ---

    @Test
    void getProfileShouldReturnUserProfile() throws Exception {
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("Sonia"))
                .andExpect(jsonPath("$.email").value("sonia@email.com"));
    }

    @Test
    void getProfileShouldFailWhenUserNotFound() throws Exception {
        String token = generateTestToken(999, "mandeep@email.com");
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProfileShouldReturn403WhenNoToken() throws Exception {

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProfileShouldReturn200WhenValidToken() throws Exception {

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("sonia@email.com"));
    }

    // --- PUT /api/users/me ---

    @Test
    void updateProfileShouldUpdateAndReturnUser() throws Exception {
        UpdateProfileDTO dto = new UpdateProfileDTO();
        dto.setName("Sonia Updated");
        dto.setEmail("sonia.updated@email.com");

        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sonia Updated"))
                .andExpect(jsonPath("$.email").value("sonia.updated@email.com"));
    }

    @Test
    void updateProfileShouldFailWhenUserNotFound() throws Exception {
        UpdateProfileDTO dto = new UpdateProfileDTO();
        dto.setName("Ghost");
        dto.setEmail("ghost@email.com");
        String token = generateTestToken(999, "mandeep@email.com");
        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // --- PUT /api/users/me/password ---

    @Test
    void changePasswordShouldSucceedWhenOldPasswordIsCorrect() throws Exception {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setOldPassword("password123");
        dto.setNewPassword("newPassword456");
        mockMvc.perform(put("/api/users/me/password")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void changePasswordShouldFailWhenOldPasswordIsWrong() throws Exception {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setOldPassword("wrongPassword");
        dto.setNewPassword("newPassword456");
        mockMvc.perform(put("/api/users/me/password")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // --- DELETE /api/users/me ---

    @Test
    void deleteUserShouldRemoveUser() throws Exception {
        mockMvc.perform(delete("/api/users/me")
                .header("Authorization", token))
                .andExpect(status().isOk());

        // verify user is gone
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

}