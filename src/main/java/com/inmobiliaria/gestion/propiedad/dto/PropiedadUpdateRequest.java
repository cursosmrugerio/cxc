package com.inmobiliaria.gestion.propiedad.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Request object for updating an existing Propiedad")
public record PropiedadUpdateRequest(
        
        @Size(max = 255, message = "Property type must not exceed 255 characters")
        @Schema(description = "Type of property", example = "CASA", allowableValues = {"CASA", "DEPARTAMENTO", "OFICINA", "LOCAL", "TERRENO", "BODEGA"})
        String tipoPropiedad,

        @Schema(description = "Total surface area in square meters", example = "150.50")
        BigDecimal superficieTotal,

        @Schema(description = "Constructed surface area in square meters", example = "120.75")
        BigDecimal superficieConstruida,

        @Size(max = 255, message = "Property status must not exceed 255 characters")
        @Schema(description = "Current status of the property", example = "DISPONIBLE", allowableValues = {"DISPONIBLE", "OCUPADA", "MANTENIMIENTO", "VENDIDA"})
        String estatusPropiedad,

        @Schema(description = "Special characteristics or features", example = "Jardín amplio, cochera techada, sistema de seguridad")
        String caracteristicasEspeciales,

        @Size(max = 255, message = "Full address must not exceed 255 characters")
        @Schema(description = "Complete address of the property", example = "Calle Reforma 123, Col. Centro, CP 12345")
        String direccionCompleta,

        @Schema(description = "Number of bathrooms", example = "2")
        Integer numeroBanos,

        @Schema(description = "Number of bedrooms", example = "3")
        Integer numeroHabitaciones
) {
}