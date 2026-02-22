package com.mandyk.expense.controller;

import com.mandyk.expense.dto.ChangePasswordDTO;
import com.mandyk.expense.dto.UpdateProfileDTO;
import com.mandyk.expense.dto.UserDTO;
import com.mandyk.expense.service.UserService;
import com.mandyk.expense.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;
    private JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // GET PROFILE
    @GetMapping("/me")
    public UserDTO getProfile(HttpServletRequest request) {

        return userService.getProfile(jwtUtil.getUserIdFromRequest(request));
    }

    // UPDATE PROFILE
    @PutMapping("/me")
    public UserDTO updateProfile(@RequestBody UpdateProfileDTO dto, HttpServletRequest request) {

        return userService.updateProfile(dto, jwtUtil.getUserIdFromRequest(request));
    }

    // CHANGE PASSWORD
    @PutMapping("/me/password")
    public void changePassword(@RequestBody ChangePasswordDTO dto, HttpServletRequest request) {

        userService.changePassword(dto, jwtUtil.getUserIdFromRequest(request));
    }

    // DELETE USER
    @DeleteMapping("/me")
    public void deleteCurrentUser(HttpServletRequest request) {

        userService.deleteUserById(jwtUtil.getUserIdFromRequest(request));
    }
}