package com.inmobiliaria.gestion.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Role information")
public record RoleResponse(
        @Schema(description = "Role ID", example = "1")
        Integer id,
        
        @Schema(description = "Role name", example = "ROLE_USER")
        String name
) {}