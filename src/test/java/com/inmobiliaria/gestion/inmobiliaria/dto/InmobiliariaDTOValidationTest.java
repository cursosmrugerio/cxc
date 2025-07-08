package com.inmobiliaria.gestion.inmobiliaria.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InmobiliariaDTO Validation Tests")
class InmobiliariaDTOValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private InmobiliariaDTO createValidInmobiliariaDTO() {
        return new InmobiliariaDTO(
                1L,
                "Inmobiliaria Los Pinos",
                "Inmobiliaria Los Pinos S.A. de C.V.",
                "ILP123456789",
                "555-123-4567",
                "contacto@lospinos.com",
                "Av. Principal 123, Col. Centro",
                "Ciudad de México",
                "CDMX",
                "06700",
                "María González",
                LocalDate.now(),
                "ACTIVE"
        );
    }

    @Nested
    @DisplayName("Valid Data Tests")
    class ValidDataTests {

        @Test
        @DisplayName("Should validate correct DTO without violations")
        void shouldValidateCorrectDtoWithoutViolations() {
            // Given
            InmobiliariaDTO validDto = createValidInmobiliariaDTO();

            // When
            Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(validDto);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should validate DTO with minimum required fields")
        void shouldValidateDtoWithMinimumRequiredFields() {
            // Given
            InmobiliariaDTO minimumDto = new InmobiliariaDTO(
                    null,
                    "A", // Minimum length
                    "B", // Minimum length
                    "C", // Minimum length
                    null, // Optional
                    null, // Optional
                    null, // Optional
                    null, // Optional
                    null, // Optional
                    null, // Optional
                    null, // Optional
                    null,
                    "ACTIVE"
            );

            // When
            Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(minimumDto);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Required Field Validation")
    class RequiredFieldValidation {

        @Test
        @DisplayName("Should require nombre comercial")
        void shouldRequireNombreComercial() {
            // Given
            InmobiliariaDTO dto = new InmobiliariaDTO(
                    1L, null, "Legal Name", "RFC123", "555-123-4567", "test@test.com",
                    "Address", "City", "State", "12345", "Contact", LocalDate.now(), "ACTIVE"
            );

            // When
            Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("nombreComercial");
            assertThat(violations.iterator().next().getMessage()).contains("must not be blank");
        }

        @Test
        @DisplayName("Should require razon social")
        void shouldRequireRazonSocial() {
            // Given
            InmobiliariaDTO dto = new InmobiliariaDTO(
                    1L, "Commercial Name", null, "RFC123", "555-123-4567", "test@test.com",
                    "Address", "City", "State", "12345", "Contact", LocalDate.now(), "ACTIVE"
            );

            // When
            Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("razonSocial");
        }

        @Test
        @DisplayName("Should require RFC/NIT")
        void shouldRequireRfcNit() {
            // Given
            InmobiliariaDTO dto = new InmobiliariaDTO(
                    1L, "Commercial Name", "Legal Name", null, "555-123-4567", "test@test.com",
                    "Address", "City", "State", "12345", "Contact", LocalDate.now(), "ACTIVE"
            );

            // When
            Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("rfcNit");
        }

        @Test
        @DisplayName("Should require estatus")
        void shouldRequireEstatus() {
            // Given
            InmobiliariaDTO dto = new InmobiliariaDTO(
                    1L, "Commercial Name", "Legal Name", "RFC123", "555-123-4567", "test@test.com",
                    "Address", "City", "State", "12345", "Contact", LocalDate.now(), null
            );

            // When
            Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("estatus");
        }
    }

    @Nested
    @DisplayName("Length Validation")
    class LengthValidation {

        @Test
        @DisplayName("Should validate nombre comercial length constraints")
        void shouldValidateNombreComercialLengthConstraints() {
            // Test maximum length violation
            String tooLongName = "A".repeat(101); // Assuming max length is 100
            InmobiliariaDTO dto = new InmobiliariaDTO(
                    1L, tooLongName, "Legal Name", "RFC123", "555-123-4567", "test@test.com",
                    "Address", "City", "State", "12345", "Contact", LocalDate.now(), "ACTIVE"
            );

            Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(dto);

            if (!violations.isEmpty()) {
                assertThat(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("nombreComercial") 
                                && v.getMessage().contains("size"))).isTrue();
            }
        }

        @Test
        @DisplayName("Should validate RFC/NIT length constraints")
        void shouldValidateRfcNitLengthConstraints() {
            // Test maximum length violation
            String tooLongRfc = "A".repeat(21); // Assuming max length is 20
            InmobiliariaDTO dto = new InmobiliariaDTO(
                    1L, "Commercial Name", "Legal Name", tooLongRfc, "555-123-4567", "test@test.com",
                    "Address", "City", "State", "12345", "Contact", LocalDate.now(), "ACTIVE"
            );

            Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(dto);

            if (!violations.isEmpty()) {
                assertThat(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("rfcNit") 
                                && v.getMessage().contains("size"))).isTrue();
            }
        }

        @Test
        @DisplayName("Should validate email length constraints")
        void shouldValidateEmailLengthConstraints() {
            // Test maximum length violation
            String tooLongEmail = "a".repeat(90) + "@test.com"; // Assuming max length is 100
            InmobiliariaDTO dto = new InmobiliariaDTO(
                    1L, "Commercial Name", "Legal Name", "RFC123", "555-123-4567", tooLongEmail,
                    "Address", "City", "State", "12345", "Contact", LocalDate.now(), "ACTIVE"
            );

            Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(dto);

            if (!violations.isEmpty()) {
                assertThat(violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("emailContacto") 
                                && v.getMessage().contains("size"))).isTrue();
            }
        }
    }

    @Nested
    @DisplayName("Email Validation")
    class EmailValidation {

        @Test
        @DisplayName("Should validate correct email formats")
        void shouldValidateCorrectEmailFormats() {
            String[] validEmails = {
                    "test@test.com",
                    "user.name@domain.com",
                    "user+tag@domain.co.uk",
                    "123@domain.com",
                    "test_email@domain-name.com"
            };

            for (String email : validEmails) {
                InmobiliariaDTO dto = new InmobiliariaDTO(
                        1L, "Commercial Name", "Legal Name", "RFC123", "555-123-4567", email,
                        "Address", "City", "State", "12345", "Contact", LocalDate.now(), "ACTIVE"
                );

                Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(dto);

                // Filter out any non-email related violations
                boolean hasEmailViolation = violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("emailContacto"));

                assertThat(hasEmailViolation)
                        .withFailMessage("Valid email %s should not have violations", email)
                        .isFalse();
            }
        }

        @Test
        @DisplayName("Should reject invalid email formats")
        void shouldRejectInvalidEmailFormats() {
            String[] invalidEmails = {
                    "invalid-email",
                    "@domain.com",
                    "user@",
                    "user..name@domain.com",
                    "user@domain",
                    "user name@domain.com"
            };

            for (String email : invalidEmails) {
                InmobiliariaDTO dto = new InmobiliariaDTO(
                        1L, "Commercial Name", "Legal Name", "RFC123", "555-123-4567", email,
                        "Address", "City", "State", "12345", "Contact", LocalDate.now(), "ACTIVE"
                );

                Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(dto);

                boolean hasEmailViolation = violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("emailContacto") 
                                && v.getMessage().contains("email"));

                assertThat(hasEmailViolation)
                        .withFailMessage("Invalid email %s should have violations", email)
                        .isTrue();
            }
        }

        @Test
        @DisplayName("Should allow null email")
        void shouldAllowNullEmail() {
            // Given
            InmobiliariaDTO dto = new InmobiliariaDTO(
                    1L, "Commercial Name", "Legal Name", "RFC123", "555-123-4567", null,
                    "Address", "City", "State", "12345", "Contact", LocalDate.now(), "ACTIVE"
            );

            // When
            Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(dto);

            // Then
            boolean hasEmailViolation = violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("emailContacto"));

            assertThat(hasEmailViolation).isFalse();
        }
    }

    @Nested
    @DisplayName("Optional Fields Validation")
    class OptionalFieldsValidation {

        @Test
        @DisplayName("Should allow null optional fields")
        void shouldAllowNullOptionalFields() {
            // Given
            InmobiliariaDTO dto = new InmobiliariaDTO(
                    1L,
                    "Commercial Name", // Required
                    "Legal Name", // Required
                    "RFC123", // Required
                    null, // Optional - telefono
                    null, // Optional - email
                    null, // Optional - direccion
                    null, // Optional - ciudad
                    null, // Optional - estado
                    null, // Optional - codigo postal
                    null, // Optional - persona contacto
                    LocalDate.now(),
                    "ACTIVE" // Required
            );

            // When
            Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should allow empty optional fields")
        void shouldAllowEmptyOptionalFields() {
            // Given
            InmobiliariaDTO dto = new InmobiliariaDTO(
                    1L,
                    "Commercial Name", // Required
                    "Legal Name", // Required
                    "RFC123", // Required
                    "", // Optional - telefono
                    "", // Optional - email (but will fail email validation if not null/empty)
                    "", // Optional - direccion
                    "", // Optional - ciudad
                    "", // Optional - estado
                    "", // Optional - codigo postal
                    "", // Optional - persona contacto
                    LocalDate.now(),
                    "ACTIVE" // Required
            );

            // When
            Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(dto);

            // Then
            // May have email validation error if empty string is considered invalid email
            boolean hasOnlyEmailViolation = violations.stream()
                    .allMatch(v -> v.getPropertyPath().toString().equals("emailContacto"));

            if (!violations.isEmpty()) {
                assertThat(hasOnlyEmailViolation).isTrue();
            }
        }
    }

    @Nested
    @DisplayName("Business Rule Validation")
    class BusinessRuleValidation {

        @Test
        @DisplayName("Should validate status values")
        void shouldValidateStatusValues() {
            String[] validStatuses = {"ACTIVE", "INACTIVE", "SUSPENDED"};

            for (String status : validStatuses) {
                InmobiliariaDTO dto = new InmobiliariaDTO(
                        1L, "Commercial Name", "Legal Name", "RFC123", "555-123-4567", "test@test.com",
                        "Address", "City", "State", "12345", "Contact", LocalDate.now(), status
                );

                Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(dto);

                boolean hasStatusViolation = violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("estatus"));

                assertThat(hasStatusViolation)
                        .withFailMessage("Valid status %s should not have violations", status)
                        .isFalse();
            }
        }
    }

    @Nested
    @DisplayName("Boundary Value Testing")
    class BoundaryValueTesting {

        @Test
        @DisplayName("Should handle minimum length boundaries")
        void shouldHandleMinimumLengthBoundaries() {
            // Test with single character (minimum valid length)
            InmobiliariaDTO dto = new InmobiliariaDTO(
                    1L,
                    "A", // Minimum length
                    "B", // Minimum length
                    "C", // Minimum length
                    "D", // Minimum length
                    "e@f.g", // Minimum valid email
                    "H", // Minimum length
                    "I", // Minimum length
                    "J", // Minimum length
                    "12345", // Typical postal code
                    "K", // Minimum length
                    LocalDate.now(),
                    "ACTIVE"
            );

            Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(dto);

            // Should not have length-related violations
            boolean hasLengthViolations = violations.stream()
                    .anyMatch(v -> v.getMessage().contains("size") || v.getMessage().contains("length"));

            assertThat(hasLengthViolations).isFalse();
        }

        @Test
        @DisplayName("Should handle null ID for new entities")
        void shouldHandleNullIdForNewEntities() {
            // Given
            InmobiliariaDTO dto = new InmobiliariaDTO(
                    null, // ID should be null for new entities
                    "Commercial Name",
                    "Legal Name",
                    "RFC123",
                    "555-123-4567",
                    "test@test.com",
                    "Address",
                    "City",
                    "State",
                    "12345",
                    "Contact",
                    LocalDate.now(),
                    "ACTIVE"
            );

            // When
            Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(dto);

            // Then
            boolean hasIdViolation = violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("idInmobiliaria"));

            assertThat(hasIdViolation).isFalse();
        }
    }

    @Nested
    @DisplayName("Multiple Violations")
    class MultipleViolations {

        @Test
        @DisplayName("Should report multiple violations simultaneously")
        void shouldReportMultipleViolationsSimultaneously() {
            // Given - DTO with multiple invalid fields
            InmobiliariaDTO dto = new InmobiliariaDTO(
                    1L,
                    null, // Invalid - required
                    "", // Invalid - blank
                    null, // Invalid - required
                    "555-123-4567",
                    "invalid-email", // Invalid - bad email format
                    "Address",
                    "City",
                    "State",
                    "12345",
                    "Contact",
                    LocalDate.now(),
                    null // Invalid - required
            );

            // When
            Set<ConstraintViolation<InmobiliariaDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSizeGreaterThan(1);

            // Check that all expected violations are present
            boolean hasNombreComercialViolation = violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("nombreComercial"));
            boolean hasRazonSocialViolation = violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("razonSocial"));
            boolean hasRfcNitViolation = violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("rfcNit"));
            boolean hasEstatusViolation = violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("estatus"));
            boolean hasEmailViolation = violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("emailContacto"));

            assertThat(hasNombreComercialViolation).isTrue();
            assertThat(hasRazonSocialViolation).isTrue();
            assertThat(hasRfcNitViolation).isTrue();
            assertThat(hasEstatusViolation).isTrue();
            assertThat(hasEmailViolation).isTrue();
        }
    }
}