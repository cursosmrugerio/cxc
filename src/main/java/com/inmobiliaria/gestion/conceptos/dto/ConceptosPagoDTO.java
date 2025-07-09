package com.inmobiliaria.gestion.conceptos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Data Transfer Object for ConceptosPago")
public record ConceptosPagoDTO(
        
        @Schema(description = "Unique identifier for the payment concept", example = "1")
        Integer idConcepto,

        @NotNull(message = "Inmobiliaria ID is required")
        @Schema(description = "ID of the inmobiliaria that owns this concept", example = "1", required = true)
        Long idInmobiliaria,

        @NotBlank(message = "Concept name is required")
        @Size(max = 255, message = "Concept name must not exceed 255 characters")
        @Schema(description = "Name of the payment concept", example = "Renta Mensual", required = true)
        String nombreConcepto,

        @Size(max = 255, message = "Description must not exceed 255 characters")
        @Schema(description = "Description of the payment concept", example = "Pago mensual de renta")
        String descripcion,

        @Size(max = 255, message = "Concept type must not exceed 255 characters")
        @Schema(description = "Type of payment concept", example = "RENTA", allowableValues = {"RENTA", "SERVICIOS", "MANTENIMIENTO", "DEPOSITO", "OTRO"})
        String tipoConcepto,

        @Schema(description = "Whether this concept allows late payment charges", example = "true")
        Boolean permiteRecargos,

        @Schema(description = "Whether this concept is active", example = "true")
        Boolean activo,

        @Schema(description = "Date when the concept was created", example = "2024-01-15")
        LocalDate fechaCreacion
) {
}