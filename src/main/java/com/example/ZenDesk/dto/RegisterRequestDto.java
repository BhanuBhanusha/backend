package com.example.ZenDesk.dto;

import com.example.ZenDesk.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegisterRequestDto {
    
    @NotBlank
    @Email(message="email is invalid")
    String email;

    @NotBlank(message="password cannot be null")
    String password;

    @NotBlank(message="name cannot be null")
    String fullName;

    @NotNull(message="role must be specified")
    Role role;
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    
}
