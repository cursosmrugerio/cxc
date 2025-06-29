package com.inmobiliaria.gestion.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Generic message response")
public record MessageResponse(
    @Schema(description = "Response message")
    String message
) {}