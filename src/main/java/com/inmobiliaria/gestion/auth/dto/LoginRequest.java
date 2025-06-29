package com.inmobiliaria.gestion.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login request payload")
public record LoginRequest(
    @Schema(description = "Username", example = "admin")
    @NotBlank(message = "Username is required")
    String username,
    
    @Schema(description = "Password", example = "password123")  
    @NotBlank(message = "Password is required")
    String password
) {}