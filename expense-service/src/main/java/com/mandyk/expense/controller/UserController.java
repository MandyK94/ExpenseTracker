package com.mandyk.expense.controller;

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

    @GetMapping(path="/me")
    public UserDTO getProfile() {

    }

    @PutMapping(path="/me")
    public UserDTO updateProfile() {

    }

    @PutMapping("/me/password")
    public UserDTO changePassword() {

    }

    @DeleteMapping("/me")
    public void deleterCurrentUser(@RequestParam Integer userId) {
        userService.deleteUserById(userId);
    }

}
