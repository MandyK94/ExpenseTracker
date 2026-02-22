package com.mandyk.expense.integration;

import com.mandyk.expense.dto.AuthRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
public class AuthControllerIntegrationTest extends BaseIntegrationTest {

    private AuthRequest buildRequest(String email, String password) {
        AuthRequest request = new AuthRequest();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }


    // -- Register --

    @Test
    void registerShouldSaveUserAndReturnResponse() throws Exception {
        AuthRequest request = buildRequest("mandeep@email.com", "password");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("mandeep@email.com"));
    }

    @Test
    void registerShouldFailWhenEmailAlreadyExists() throws Exception {
        AuthRequest request = buildRequest("duplicate@email.com", "password123");

        // register once
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // register again with same email
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerShouldFailWhenEmailIsInvalid() throws Exception {
        AuthRequest request = buildRequest("not-an-email", "password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // --- Login ---

    @Test
    void loginShouldReturnResponseWhenCredentialaAreCorrect() throws Exception {
        AuthRequest request = buildRequest("login@email.com", "password");

        // register first
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // then login
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("login@email.com"));

    }

    @Test
    void loginShouldFailWhenEmailDoesNotExist() throws Exception {
        AuthRequest request = buildRequest("ghost@email.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginShouldFailWhenPasswordIsWrong() throws Exception {
        AuthRequest request = buildRequest("wrongpass@email.com", "password123");

        // register first
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // login with wrong password
        AuthRequest wrongRequest = buildRequest("wrongpass@email.com", "wrongpassword");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongRequest)))
                .andExpect(status().isBadRequest());
    }

}
