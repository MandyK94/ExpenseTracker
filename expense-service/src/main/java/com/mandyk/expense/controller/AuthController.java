package com.mandyk.expense.controller;

import com.mandyk.expense.dto.AuthRequest;
import com.mandyk.expense.dto.AuthResponse;
import com.mandyk.expense.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(path="/register")
    public AuthResponse registerUser(@Valid @RequestBody AuthRequest request) {
        log.debug("user signup: "+request.getEmail());
        return authService.registerUser(request);
    }

    @PostMapping(path="/login")
    public AuthResponse loginUser(@Valid @RequestBody AuthRequest request) {
        log.debug("User login "+ request.getEmail());
        return authService.loginUser(request);
    }

}
