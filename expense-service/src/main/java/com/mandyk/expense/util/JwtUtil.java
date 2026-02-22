package com.mandyk.expense.util;

import com.mandyk.expense.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private JwtService jwtService;

    public JwtUtil(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public Integer getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null; // don't crash
        }
        String token = authHeader.substring(7);
        return jwtService.extractUserId(token);
    }
}
