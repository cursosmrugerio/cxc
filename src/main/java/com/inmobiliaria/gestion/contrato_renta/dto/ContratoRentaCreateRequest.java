package com.inmobiliaria.gestion.contrato_renta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Request object for creating a new rental contract")
public record ContratoRentaCreateRequest(
        
        @NotNull(message = "Property ID is required")
        @Schema(description = "ID of the property to rent", example = "1", required = true)
        Integer idPropiedad,

        @NotNull(message = "Contract start date is required")
        @Schema(description = "Contract start date", example = "2024-01-15T10:00:00", required = true)
        LocalDateTime fechaInicioContrato,

        @Size(max = 255, message = "Special conditions must not exceed 255 characters")
        @Schema(description = "Special conditions for the rental contract", example = "No pets allowed, No smoking")
        String condicionesEspeciales,

        @Email(message = "Invalid email format")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        @Schema(description = "Email for notifications", example = "tenant@example.com")
        String emailNotificaciones,

        @DecimalMin(value = "0.0", inclusive = false, message = "Security deposit must be greater than 0")
        @Schema(description = "Security deposit amount", example = "5000.00")
        BigDecimal depositoGarantia,

        @NotNull(message = "Duration in months is required")
        @Min(value = 1, message = "Duration must be at least 1 month")
        @Schema(description = "Duration of the contract in months", example = "12", required = true)
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