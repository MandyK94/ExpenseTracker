package com.mandyk.expense.service;

import com.mandyk.expense.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void deleteUserById(Integer userId) {
        userRepository.deleteById(userId);
    }


}
