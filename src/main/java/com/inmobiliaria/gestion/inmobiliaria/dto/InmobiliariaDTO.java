package com.inmobiliaria.gestion.inmobiliaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Data Transfer Object for Inmobiliaria")
public record InmobiliariaDTO(
        
        @Schema(description = "Unique identifier for the inmobiliaria", example = "1")
        Long idInmobiliaria,

        @NotBlank(message = "Commercial name is required")
        @Size(max = 100, message = "Commercial name must not exceed 100 characters")
        @Schema(description = "Commercial name of the inmobiliaria", example = "Inmobiliaria ABC", required = true)
        String nombreComercial,

        @NotBlank(message = "Business name is required")
        @Size(max = 150, message = "Business name must not exceed 150 characters")
        @Schema(description = "Legal business name", example = "ABC Real Estate S.A. de C.V.", required = true)
        String razonSocial,

        @NotBlank(message = "RFC/NIT is required")
        @Size(max = 20, message = "RFC/NIT must not exceed 20 characters")
        @Schema(description = "Tax identification number (RFC/NIT)", example = "ABC123456789", required = true)
        String rfcNit,

        @Size(max = 20, message = "Main phone must not exceed 20 characters")
        @Schema(description = "Main contact phone number", example = "+52 55 1234 5678")
        String telefonoPrincipal,

        @Email(message = "Email should be valid")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        @Schema(description = "Contact email address", example = "contacto@inmobiliariaabc.com")
        String emailContacto,

        @Size(max = 200, message = "Full address must not exceed 200 characters")
        @Schema(description = "Complete business address", example = "Av. Reforma 123, Col. Centro")
        String direccionCompleta,

        @Size(max = 100, message = "City must not exceed 100 characters")
        @Schema(description = "City where the business is located", example = "Mexico City")
        String ciudad,

        @Size(max = 100, message = "State must not exceed 100 characters")
        @Schema(description = "State or province", example = "CDMX")
        String estado,

        @Size(max = 10, message = "Postal code must not exceed 10 characters")
        @Schema(description = "Postal/ZIP code", example = "06000")
        String codigoPostal,

        @Size(max = 100, message = "Contact person must not exceed 100 characters")
        @Schema(description = "Main contact person name", example = "Juan Pérez")
        String personaContacto,

        @Schema(description = "Registration date", example = "2024-01-15")
        LocalDate fechaRegistro,

        @NotBlank(message = "Status is required")
        @Size(max = 20, message = "Status must not exceed 20 characters")
        @Schema(description = "Current status of the inmobiliaria", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED"})
        String estatus
) {
}