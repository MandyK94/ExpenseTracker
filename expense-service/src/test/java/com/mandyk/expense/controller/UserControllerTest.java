package com.mandyk.expense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mandyk.expense.dto.ChangePasswordDTO;
import com.mandyk.expense.dto.UpdateProfileDTO;
import com.mandyk.expense.dto.UserDTO;
import com.mandyk.expense.exception.InvalidPasswordException;
import com.mandyk.expense.exception.ResourceNotFoundException;
import com.mandyk.expense.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO sampleUser;
    private UpdateProfileDTO updateProfileDTO;
    private ChangePasswordDTO changePasswordDTO;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        sampleUser = new UserDTO(1, "Sonia", "sonia@email.com", LocalDateTime.now());

        updateProfileDTO = new UpdateProfileDTO();
        updateProfileDTO.setUserId(1);
        updateProfileDTO.setName("Sonia Updated");
        updateProfileDTO.setEmail("sonia.updated@email.com");

        changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setUserId(1);
        changePasswordDTO.setOldPassword("oldPass123");
        changePasswordDTO.setNewPassword("newPass456");
    }

    @Test
    void getProfileShouldReturnUserDTO() throws Exception {
        when(userService.getProfile(1)).thenReturn(sampleUser);

        mockMvc.perform(get("/api/users/me").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sonia"))
                .andExpect(jsonPath("$.email").value("sonia@email.com"));
    }

    @Test
    void getProfileShouldReturn400WhenUserIdMissing() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProfileShouldReturn404WhenUserNotFound() throws Exception {
        when(userService.getProfile(99)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/me").param("userId", "99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProfileShouldReturnUpdatedUser() throws Exception {
        UserDTO updatedUser = new UserDTO(1, "Sonia Updated", "sonia.updated@email.com", LocalDateTime.now());
        when(userService.updateProfile(any(UpdateProfileDTO.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProfileDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sonia Updated"))
                .andExpect(jsonPath("$.email").value("sonia.updated@email.com"));
    }

    @Test
    void updateProfileShouldReturn400WhenBodyIsMissing() throws Exception {
        mockMvc.perform(put("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProfileShouldReturn404WhenUserNotFound() throws Exception {
        when(userService.updateProfile(any(UpdateProfileDTO.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(put("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProfileDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void changePasswordShouldReturn200WhenSuccessful() throws Exception {
        doNothing().when(userService).changePassword(any(ChangePasswordDTO.class));

        mockMvc.perform(put("/api/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void changePasswordShouldReturn400WhenBodyIsMissing() throws Exception {
        mockMvc.perform(put("/api/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePasswordShouldReturn400WhenPasswordIsWrong() throws Exception {
        doThrow(new InvalidPasswordException("Incorrect old password"))
                .when(userService).changePassword(any(ChangePasswordDTO.class));

        mockMvc.perform(put("/api/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCurrentUserShouldReturn200WhenSuccessful() throws Exception {
        doNothing().when(userService).deleteUserById(1);

        mockMvc.perform(delete("/api/users/me").param("userId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCurrentUserShouldReturn400WhenUserIdMissing() throws Exception {
        mockMvc.perform(delete("/api/users/me"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCurrentUserShouldReturn404WhenUserNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User not found"))
                .when(userService).deleteUserById(99);

        mockMvc.perform(delete("/api/users/me").param("userId", "99"))
                .andExpect(status().isNotFound());
    }

}
