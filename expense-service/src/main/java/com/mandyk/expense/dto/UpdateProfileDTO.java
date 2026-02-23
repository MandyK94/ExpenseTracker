package com.mandyk.expense.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateProfileDTO {

    @NotBlank(message="Name is required")
    @Size(max=100, message = "Name is too long")
    private String name;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}