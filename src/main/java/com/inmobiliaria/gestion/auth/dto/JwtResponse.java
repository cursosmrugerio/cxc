package com.inmobiliaria.gestion.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "JWT authentication response")
public record JwtResponse(
    @Schema(description = "JWT access token")
    String token,
    
    @Schema(description = "Token type", example = "Bearer")
    String type,
    
    @Schema(description = "User ID")
    Long id,
    
    @Schema(description = "Username")
    String username,
    
    @Schema(description = "Email address")
    String email,
    
    @Schema(description = "User roles")
    List<String> roles
) {
    public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles) {
        this(accessToken, "Bearer", id, username, email, roles);
    }
}