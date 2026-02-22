package com.mandyk.expense.service;

import com.mandyk.expense.dto.ChangePasswordDTO;
import com.mandyk.expense.dto.UpdateProfileDTO;
import com.mandyk.expense.dto.UserDTO;
import com.mandyk.expense.entity.User;
import com.mandyk.expense.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET PROFILE
    public UserDTO getProfile(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToDTO(user);
    }

    // UPDATE PROFILE
    public UserDTO updateProfile(UpdateProfileDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        User saved = userRepository.save(user);

        return mapToDTO(saved);
    }

    // CHANGE PASSWORD
    public void changePassword(ChangePasswordDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // TODO: use BCrypt later
        if (!user.getPassword().equals(dto.getOldPassword())) {
            throw new RuntimeException("Old password incorrect");
        }

        user.setPassword(dto.getNewPassword());

        userRepository.save(user);
    }

    // DELETE USER
    public void deleteUserById(Integer userId) {
        userRepository.deleteById(userId);
    }

    // MAPPER
    private UserDTO mapToDTO(User user) {

        UserDTO dto = new UserDTO();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());

        return dto;
    }
}