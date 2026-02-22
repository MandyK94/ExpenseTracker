package com.mandyk.expense.service;

import com.mandyk.expense.dto.AuthRequest;
import com.mandyk.expense.dto.AuthResponse;
import com.mandyk.expense.entity.User;
import com.mandyk.expense.exception.InvalidPasswordException;
import com.mandyk.expense.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse registerUser(AuthRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user = userRepository.save(user);

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getPassword()
        );
    }

    public AuthResponse loginUser(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new InvalidPasswordException("Invalid email or password"));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid email or Password");
        }
        return new AuthResponse(user.getId(), user.getEmail(), user.getPassword());
    }

}
