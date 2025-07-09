package com.inmobiliaria.gestion.conceptos.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConceptosPago DTO Validation Tests")
class ConceptosPagoDTOValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("ConceptosPagoCreateRequest Validation")
    class CreateRequestValidation {

        @Test
        @DisplayName("Should pass validation with valid data")
        void shouldPassValidationWithValidData() {
            // Given
            ConceptosPagoCreateRequest request = new ConceptosPagoCreateRequest(
                    1L,
                    "Renta Mensual",
                    "Pago de renta mensual",
                    "RENTA",
                    true,
                    true
            );

            // When
            Set<ConstraintViolation<ConceptosPagoCreateRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when inmobiliaria ID is null")
        void shouldFailValidationWhenInmobiliariaIdIsNull() {
            // Given
            ConceptosPagoCreateRequest request = new ConceptosPagoCreateRequest(
                    null, // Invalid: null inmobiliaria ID
                    "Renta Mensual",
                    "Pago de renta mensual",
                    "RENTA",
                    true,
                    true
            );

            // When
            Set<ConstraintViolation<ConceptosPagoCreateRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).contains("Inmobiliaria ID is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should fail validation when concept name is null, empty or blank")
        void shouldFailValidationWhenConceptNameIsNullEmptyOrBlank(String nombreConcepto) {
            // Given
            ConceptosPagoCreateRequest request = new ConceptosPagoCreateRequest(
                    1L,
                    nombreConcepto, // Invalid: null, empty or blank
                    "Pago de renta mensual",
                    "RENTA",
                    true,
                    true
            );

            // When
            Set<ConstraintViolation<ConceptosPagoCreateRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getMessage().contains("Concept name is required"));
        }

        @Test
        @DisplayName("Should fail validation when concept name exceeds max length")
        void shouldFailValidationWhenConceptNameExceedsMaxLength() {
            // Given
            String longName = "A".repeat(256); // Exceeds 255 characters
            ConceptosPagoCreateRequest request = new ConceptosPagoCreateRequest(
                    1L,
                    longName,
                    "Pago de renta mensual",
                    "RENTA",
                    true,
                    true
            );

            // When
            Set<ConstraintViolation<ConceptosPagoCreateRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).contains("must not exceed 255 characters");
        }

        @Test
        @DisplayName("Should fail validation when description exceeds max length")
        void shouldFailValidationWhenDescriptionExceedsMaxLength() {
            // Given
            String longDescription = "A".repeat(256); // Exceeds 255 characters
            ConceptosPagoCreateRequest request = new ConceptosPagoCreateRequest(
                    1L,
                    "Renta Mensual",
                    longDescription,
                    "RENTA",
                    true,
                    true
            );

            // When
            Set<ConstraintViolation<ConceptosPagoCreateRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).contains("must not exceed 255 characters");
        }

        @Test
        @DisplayName("Should fail validation when tipo concepto exceeds max length")
        void shouldFailValidationWhenTipoConceptoExceedsMaxLength() {
            // Given
            String longTipo = "A".repeat(256); // Exceeds 255 characters
            ConceptosPagoCreateRequest request = new ConceptosPagoCreateRequest(
                    1L,
                    "Renta Mensual",
                    "Pago de renta mensual",
                    longTipo,
                    true,
                    true
            );

            // When
            Set<ConstraintViolation<ConceptosPagoCreateRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).contains("must not exceed 255 characters");
        }

        @Test
        @DisplayName("Should pass validation with null optional fields")
        void shouldPassValidationWithNullOptionalFields() {
            // Given
            ConceptosPagoCreateRequest request = new ConceptosPagoCreateRequest(
                    1L,
                    "Renta Mensual",
                    null, // Optional: description
                    null, // Optional: tipo concepto
                    null, // Optional: permite recargos
                    null  // Optional: activo
            );

            // When
            Set<ConstraintViolation<ConceptosPagoCreateRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("ConceptosPagoUpdateRequest Validation")
    class UpdateRequestValidation {

        @Test
        @DisplayName("Should pass validation with valid data")
        void shouldPassValidationWithValidData() {
            // Given
            ConceptosPagoUpdateRequest request = new ConceptosPagoUpdateRequest(
                    "Renta Mensual Actualizada",
                    "Pago de renta mensual actualizada",
                    "RENTA",
                    true,
                    false
            );

            // When
            Set<ConstraintViolation<ConceptosPagoUpdateRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should fail validation when concept name is null, empty or blank")
        void shouldFailValidationWhenConceptNameIsNullEmptyOrBlank(String nombreConcepto) {
            // Given
            ConceptosPagoUpdateRequest request = new ConceptosPagoUpdateRequest(
                    nombreConcepto, // Invalid: null, empty or blank
                    "Pago de renta mensual actualizada",
                    "RENTA",
                    true,
                    false
            );

            // When
            Set<ConstraintViolation<ConceptosPagoUpdateRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getMessage().contains("Concept name is required"));
        }

        @Test
        @DisplayName("Should fail validation when concept name exceeds max length")
        void shouldFailValidationWhenConceptNameExceedsMaxLength() {
            // Given
            String longName = "A".repeat(256); // Exceeds 255 characters
            ConceptosPagoUpdateRequest request = new ConceptosPagoUpdateRequest(
                    longName,
                    "Pago de renta mensual actualizada",
                    "RENTA",
                    true,
                    false
            );

            // When
            Set<ConstraintViolation<ConceptosPagoUpdateRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).contains("must not exceed 255 characters");
        }

        @Test
        @DisplayName("Should pass validation with null optional fields")
        void shouldPassValidationWithNullOptionalFields() {
            // Given
            ConceptosPagoUpdateRequest request = new ConceptosPagoUpdateRequest(
                    "Renta Mensual Actualizada",
                    null, // Optional: description
                    null, // Optional: tipo concepto
                    null, // Optional: permite recargos
                    null  // Optional: activo
            );

            // When
            Set<ConstraintViolation<ConceptosPagoUpdateRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("ConceptosPagoDTO Validation")
    class DTOValidation {

        @Test
        @DisplayName("Should pass validation with valid data")
        void shouldPassValidationWithValidData() {
            // Given
            ConceptosPagoDTO dto = new ConceptosPagoDTO(
                    1,
                    1L,
                    "Renta Mensual",
                    "Pago de renta mensual",
                    "RENTA",
                    true,
                    true,
                    LocalDate.now()
            );

            // When
            Set<ConstraintViolation<ConceptosPagoDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when inmobiliaria ID is null")
        void shouldFailValidationWhenInmobiliariaIdIsNull() {
            // Given
            ConceptosPagoDTO dto = new ConceptosPagoDTO(
                    1,
                    null, // Invalid: null inmobiliaria ID
                    "Renta Mensual",
                    "Pago de renta mensual",
                    "RENTA",
                    true,
                    true,
                    LocalDate.now()
            );

            // When
            Set<ConstraintViolation<ConceptosPagoDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).contains("Inmobiliaria ID is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should fail validation when concept name is null, empty or blank")
        void shouldFailValidationWhenConceptNameIsNullEmptyOrBlank(String nombreConcepto) {
            // Given
            ConceptosPagoDTO dto = new ConceptosPagoDTO(
                    1,
                    1L,
                    nombreConcepto, // Invalid: null, empty or blank
                    "Pago de renta mensual",
                    "RENTA",
                    true,
                    true,
                    LocalDate.now()
            );

            // When
            Set<ConstraintViolation<ConceptosPagoDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getMessage().contains("Concept name is required"));
        }

        @Test
        @DisplayName("Should pass validation with null optional fields")
        void shouldPassValidationWithNullOptionalFields() {
            // Given
            ConceptosPagoDTO dto = new ConceptosPagoDTO(
                    1,
                    1L,
                    "Renta Mensual",
                    null, // Optional: description
                    null, // Optional: tipo concepto
                    null, // Optional: permite recargos
                    null, // Optional: activo
                    null  // Optional: fecha creacion
            );

            // When
            Set<ConstraintViolation<ConceptosPagoDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle minimum valid concept name length")
        void shouldHandleMinimumValidConceptNameLength() {
            // Given
            ConceptosPagoCreateRequest request = new ConceptosPagoCreateRequest(
                    1L,
                    "A", // Minimum valid length
                    "Description",
                    "RENTA",
                    true,
                    true
            );

            // When
            Set<ConstraintViolation<ConceptosPagoCreateRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle maximum valid concept name length")
        void shouldHandleMaximumValidConceptNameLength() {
            // Given
            String maxLengthName = "A".repeat(255); // Maximum valid length
            ConceptosPagoCreateRequest request = new ConceptosPagoCreateRequest(
                    1L,
                    maxLengthName,
                    "Description",
                    "RENTA",
                    true,
                    true
            );

            // When
            Set<ConstraintViolation<ConceptosPagoCreateRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle special characters in concept name")
        void shouldHandleSpecialCharactersInConceptName() {
            // Given
            ConceptosPagoCreateRequest request = new ConceptosPagoCreateRequest(
                    1L,
                    "Renta & Servicios (Agua/Luz) - Mensual 2024",
                    "Description",
                    "RENTA",
                    true,
                    true
            );

            // When
            Set<ConstraintViolation<ConceptosPagoCreateRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle unicode characters in concept name")
        void shouldHandleUnicodeCharactersInConceptName() {
            // Given
            ConceptosPagoCreateRequest request = new ConceptosPagoCreateRequest(
                    1L,
                    "Renta Mensual - Año 2024 🏠",
                    "Descripción con acentos: año, niño, corazón",
                    "RENTA",
                    true,
                    true
            );

            // When
            Set<ConstraintViolation<ConceptosPagoCreateRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }
}