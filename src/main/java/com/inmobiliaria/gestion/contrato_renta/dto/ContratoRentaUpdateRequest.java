package com.inmobiliaria.gestion.contrato_renta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Request object for updating an existing rental contract")
public record ContratoRentaUpdateRequest(
        
        @Schema(description = "Contract start date", example = "2024-01-15T10:00:00")
        LocalDateTime fechaInicioContrato,

        @Size(max = 255, message = "Special conditions must not exceed 255 characters")
        @Schema(description = "Special conditions for the rental contract", example = "No pets allowed, No smoking")
        String condicionesEspeciales,

        @Email(message = "Invalid email format")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        @Schema(description = "Email for notifications", example = "tenant@example.com")
        String emailNotificaciones,

        @Size(max = 255, message = "Contract status must not exceed 255 characters")
        @Schema(description = "Current status of the contract", example = "ACTIVO", allowableValues = {"ACTIVO", "VENCIDO", "TERMINADO", "SUSPENDIDO"})
        String estatusContrato,

        @DecimalMin(value = "0.0", inclusive = false, message = "Security deposit must be greater than 0")
        @Schema(description = "Security deposit amount", example = "5000.00")
        BigDecimal depositoGarantia,

        @Min(value = 1, message = "Duration must be at least 1 month")
        @Schema(description = "Duration of the contract in months", example = "12")
        Integer duracionMeses,

        @Min(value = 1, message = "Notification days must be at least 1")
        @Schema(description = "Days before contract expiration to send notifications", example = "30")
        Integer notificacionDiasPrevios,

        @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{7,15}$", message = "Invalid phone number format")
        @Size(max = 255, message = "Phone must not exceed 255 characters")
        @Schema(description = "Phone number for notifications", example = "+52 55 1234 5678")
        String telefonoNotificaciones
) {
}