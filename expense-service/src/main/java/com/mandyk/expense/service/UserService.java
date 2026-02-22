package com.mandyk.expense.service;

import com.mandyk.expense.dto.ChangePasswordDTO;
import com.mandyk.expense.dto.UpdateProfileDTO;
import com.mandyk.expense.dto.UserDTO;
import com.mandyk.expense.entity.User;
import com.mandyk.expense.exception.InvalidPasswordException;
import com.mandyk.expense.exception.ResourceNotFoundException;
import com.mandyk.expense.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // GET PROFILE
    public UserDTO getProfile(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToDTO(user);
    }

    // UPDATE PROFILE
    public UserDTO updateProfile(UpdateProfileDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        User saved = userRepository.save(user);

        return mapToDTO(saved);
    }

    // CHANGE PASSWORD
    public void changePassword(ChangePasswordDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // TODO: use BCrypt later
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Old password incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        userRepository.save(user);
    }

    // DELETE USER
    public void deleteUserById(Integer userId) {
        if(!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
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