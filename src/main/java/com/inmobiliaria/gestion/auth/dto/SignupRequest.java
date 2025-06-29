package com.inmobiliaria.gestion.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(description = "User registration request payload")
public record SignupRequest(
    @Schema(description = "Username", example = "newuser")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    String username,
    
    @Schema(description = "Email address", example = "user@example.com")
    @NotBlank(message = "Email is required")
    @Size(max = 50, message = "Email must not exceed 50 characters")
    @Email(message = "Email should be valid")
    String email,
    
    @Schema(description = "Password", example = "password123")
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    String password,
    
    @Schema(description = "User roles", example = "[\"user\"]")
    Set<String> role
) {}