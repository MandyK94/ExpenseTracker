package com.mandyk.expense.controller;

import com.mandyk.expense.dto.ChangePasswordDTO;
import com.mandyk.expense.dto.UpdateProfileDTO;
import com.mandyk.expense.dto.UserDTO;
import com.mandyk.expense.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET PROFILE
    @GetMapping("/me")
    public UserDTO getProfile(@RequestParam Integer userId) {

        return userService.getProfile(userId);
    }

    // UPDATE PROFILE
    @PutMapping("/me")
    public UserDTO updateProfile(@RequestBody UpdateProfileDTO dto) {

        return userService.updateProfile(dto);
    }

    // CHANGE PASSWORD
    @PutMapping("/me/password")
    public void changePassword(@RequestBody ChangePasswordDTO dto) {

        userService.changePassword(dto);
    }

    // DELETE USER
    @DeleteMapping("/me")
    public void deleteCurrentUser(@RequestParam Integer userId) {

        userService.deleteUserById(userId);
    }
}