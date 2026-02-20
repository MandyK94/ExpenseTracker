package com.mandyk.expense.controller;

import com.mandyk.expense.dto.AuthRequest;
import com.mandyk.expense.dto.AuthResponse;
import com.mandyk.expense.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(path="/register")
    public AuthResponse registerUser(@Valid @RequestBody AuthRequest request) {
        return authService.registerUser(request);
    }

    @PostMapping(path="/login")
    public AuthResponse loginUser(@Valid @RequestBody AuthRequest request) {
        return authService.loginUser(request);
    }

}
