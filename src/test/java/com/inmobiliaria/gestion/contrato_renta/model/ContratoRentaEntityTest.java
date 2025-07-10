package com.inmobiliaria.gestion.contrato_renta.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ContratoRenta Entity Tests")
class ContratoRentaEntityTest {

    private Validator validator;
    private ContratoRenta validContrato;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        validContrato = createValidContrato();
    }

    private ContratoRenta createValidContrato() {
        return ContratoRenta.builder()
                .idPropiedad(1)
                .fechaInicioContrato(LocalDateTime.of(2024, 1, 15, 10, 0))
                .fechaFinContrato(LocalDateTime.of(2025, 1, 15, 10, 0))
                .condicionesEspeciales("No pets allowed")
                .emailNotificaciones("tenant@example.com")
                .estatusContrato("ACTIVO")
                .depositoGarantia(BigDecimal.valueOf(5000.00))
                .duracionMeses(12)
                .notificacionDiasPrevios(30)
                .telefonoNotificaciones("+52 55 1234 5678")
                .build();
    }

    @Nested
    @DisplayName("Entity Validation Tests")
    class EntityValidationTests {

        @Test
        @DisplayName("Should validate successfully with all valid fields")
        void shouldValidateSuccessfullyWithAllValidFields() {
            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should validate successfully with minimal required fields")
        void shouldValidateSuccessfullyWithMinimalRequiredFields() {
            ContratoRenta minimalContrato = ContratoRenta.builder()
                    .idPropiedad(1)
                    .fechaInicioContrato(LocalDateTime.of(2024, 1, 15, 10, 0))
                    .duracionMeses(12)
                    .estatusContrato("ACTIVO")
                    .notificacionDiasPrevios(30)
                    .build();

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(minimalContrato);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when duration is zero")
        void shouldFailValidationWhenDurationIsZero() {
            validContrato.setDuracionMeses(0);

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("duracionMeses");
            assertThat(violations.iterator().next().getMessage()).contains("Duration must be at least 1 month");
        }

        @Test
        @DisplayName("Should fail validation when duration is negative")
        void shouldFailValidationWhenDurationIsNegative() {
            validContrato.setDuracionMeses(-5);

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("duracionMeses");
        }

        @Test
        @DisplayName("Should fail validation when notification days is zero")
        void shouldFailValidationWhenNotificationDaysIsZero() {
            validContrato.setNotificacionDiasPrevios(0);

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("notificacionDiasPrevios");
            assertThat(violations.iterator().next().getMessage()).contains("Notification days must be at least 1");
        }

        @Test
        @DisplayName("Should fail validation when notification days is negative")
        void shouldFailValidationWhenNotificationDaysIsNegative() {
            validContrato.setNotificacionDiasPrevios(-10);

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("notificacionDiasPrevios");
        }
    }

    @Nested
    @DisplayName("Email Validation Tests")
    class EmailValidationTests {

        @Test
        @DisplayName("Should validate valid email formats")
        void shouldValidateValidEmailFormats() {
            String[] validEmails = {
                    "user@example.com",
                    "test.email@domain.org",
                    "user+tag@example.co.uk",
                    "first.last@subdomain.example.com",
                    "user123@example123.com"
            };

            for (String email : validEmails) {
                validContrato.setEmailNotificaciones(email);
                Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);
                assertThat(violations).isEmpty();
            }
        }

        @Test
        @DisplayName("Should fail validation for invalid email formats")
        void shouldFailValidationForInvalidEmailFormats() {
            String[] invalidEmails = {
                    "invalid-email",
                    "@example.com",
                    "user@",
                    "user..name@example.com",
                    "user@.com",
                    "user name@example.com"
            };

            for (String email : invalidEmails) {
                validContrato.setEmailNotificaciones(email);
                Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);
                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("emailNotificaciones");
            }
        }

        @Test
        @DisplayName("Should fail validation when email exceeds max length")
        void shouldFailValidationWhenEmailExceedsMaxLength() {
            String longEmail = "a".repeat(250) + "@example.com";
            validContrato.setEmailNotificaciones(longEmail);

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);

            assertThat(violations).hasSize(2);
            assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("emailNotificaciones") && 
                    v.getMessage().contains("Email must not exceed 255 characters"))).isTrue();
        }

        @Test
        @DisplayName("Should allow null email")
        void shouldAllowNullEmail() {
            validContrato.setEmailNotificaciones(null);

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);

            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Phone Validation Tests")
    class PhoneValidationTests {

        @Test
        @DisplayName("Should validate valid phone formats")
        void shouldValidateValidPhoneFormats() {
            String[] validPhones = {
                    "+52 55 1234 5678",
                    "5551234567",
                    "+521234567890",
                    "1234567",
                    "+52 123456789"
            };

            for (String phone : validPhones) {
                validContrato.setTelefonoNotificaciones(phone);
                Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);
                assertThat(violations).isEmpty();
            }
        }

        @Test
        @DisplayName("Should fail validation for invalid phone formats")
        void shouldFailValidationForInvalidPhoneFormats() {
            String[] invalidPhones = {
                    "123", // Too short
                    "abcd1234567890", // Contains letters
                    "++52 55 1234 5678", // Double plus
                    "1234567890123456", // Too long
                    "@#$%^&*()", // Invalid characters
                    ""
            };

            for (String phone : invalidPhones) {
                validContrato.setTelefonoNotificaciones(phone);
                Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);
                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("telefonoNotificaciones");
            }
        }

        @Test
        @DisplayName("Should fail validation when phone exceeds max length")
        void shouldFailValidationWhenPhoneExceedsMaxLength() {
            String longPhone = "1".repeat(256);
            validContrato.setTelefonoNotificaciones(longPhone);

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);

            assertThat(violations).hasSize(2);
            assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("telefonoNotificaciones") && 
                    v.getMessage().contains("Phone must not exceed 255 characters"))).isTrue();
        }

        @Test
        @DisplayName("Should allow null phone")
        void shouldAllowNullPhone() {
            validContrato.setTelefonoNotificaciones(null);

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);

            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Size Validation Tests")
    class SizeValidationTests {

        @Test
        @DisplayName("Should fail validation when special conditions exceed max length")
        void shouldFailValidationWhenSpecialConditionsExceedMaxLength() {
            validContrato.setCondicionesEspeciales("a".repeat(256));

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("condicionesEspeciales");
            assertThat(violations.iterator().next().getMessage()).contains("Special conditions must not exceed 255 characters");
        }

        @Test
        @DisplayName("Should fail validation when contract status exceeds max length")
        void shouldFailValidationWhenContractStatusExceedsMaxLength() {
            validContrato.setEstatusContrato("a".repeat(256));

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("estatusContrato");
            assertThat(violations.iterator().next().getMessage()).contains("Contract status must not exceed 255 characters");
        }

        @Test
        @DisplayName("Should allow maximum valid string lengths")
        void shouldAllowMaximumValidStringLengths() {
            validContrato.setCondicionesEspeciales("a".repeat(255));
            validContrato.setEstatusContrato("b".repeat(255));
            validContrato.setEmailNotificaciones("validuser@example.com");
            validContrato.setTelefonoNotificaciones("+52 " + "1".repeat(12));

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);

            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Entity Behavior Tests")
    class EntityBehaviorTests {

        @Test
        @DisplayName("Should create entity with valid constructor")
        void shouldCreateEntityWithValidConstructor() {
            ContratoRenta contrato = new ContratoRenta();

            assertThat(contrato).isNotNull();
            assertThat(contrato.getIdContrato()).isNull();
            assertThat(contrato.getEstatusContrato()).isEqualTo("ACTIVO");
        }

        @Test
        @DisplayName("Should create entity with builder pattern")
        void shouldCreateEntityWithBuilderPattern() {
            ContratoRenta contrato = ContratoRenta.builder()
                    .idPropiedad(1)
                    .fechaInicioContrato(LocalDateTime.of(2024, 1, 15, 10, 0))
                    .duracionMeses(12)
                    .estatusContrato("ACTIVO")
                    .build();

            assertThat(contrato).isNotNull();
            assertThat(contrato.getIdPropiedad()).isEqualTo(1);
            assertThat(contrato.getFechaInicioContrato()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 0));
            assertThat(contrato.getDuracionMeses()).isEqualTo(12);
            assertThat(contrato.getEstatusContrato()).isEqualTo("ACTIVO");
        }

        @Test
        @DisplayName("Should set and get all properties correctly")
        void shouldSetAndGetAllPropertiesCorrectly() {
            ContratoRenta contrato = new ContratoRenta();

            contrato.setIdContrato(1);
            contrato.setIdPropiedad(2);
            contrato.setFechaInicioContrato(LocalDateTime.of(2024, 2, 1, 9, 30));
            contrato.setFechaFinContrato(LocalDateTime.of(2025, 2, 1, 9, 30));
            contrato.setCondicionesEspeciales("Updated conditions");
            contrato.setEmailNotificaciones("updated@example.com");
            contrato.setEstatusContrato("VENCIDO");
            contrato.setDepositoGarantia(BigDecimal.valueOf(7500.00));
            contrato.setDuracionMeses(18);
            contrato.setNotificacionDiasPrevios(45);
            contrato.setTelefonoNotificaciones("+52 55 9876 5432");

            assertThat(contrato.getIdContrato()).isEqualTo(1);
            assertThat(contrato.getIdPropiedad()).isEqualTo(2);
            assertThat(contrato.getFechaInicioContrato()).isEqualTo(LocalDateTime.of(2024, 2, 1, 9, 30));
            assertThat(contrato.getFechaFinContrato()).isEqualTo(LocalDateTime.of(2025, 2, 1, 9, 30));
            assertThat(contrato.getCondicionesEspeciales()).isEqualTo("Updated conditions");
            assertThat(contrato.getEmailNotificaciones()).isEqualTo("updated@example.com");
            assertThat(contrato.getEstatusContrato()).isEqualTo("VENCIDO");
            assertThat(contrato.getDepositoGarantia()).isEqualByComparingTo(BigDecimal.valueOf(7500.00));
            assertThat(contrato.getDuracionMeses()).isEqualTo(18);
            assertThat(contrato.getNotificacionDiasPrevios()).isEqualTo(45);
            assertThat(contrato.getTelefonoNotificaciones()).isEqualTo("+52 55 9876 5432");
        }

        @Test
        @DisplayName("Should handle null values appropriately")
        void shouldHandleNullValuesAppropriately() {
            ContratoRenta contrato = new ContratoRenta();

            contrato.setCondicionesEspeciales(null);
            contrato.setEmailNotificaciones(null);
            contrato.setDepositoGarantia(null);
            contrato.setTelefonoNotificaciones(null);

            assertThat(contrato.getCondicionesEspeciales()).isNull();
            assertThat(contrato.getEmailNotificaciones()).isNull();
            assertThat(contrato.getDepositoGarantia()).isNull();
            assertThat(contrato.getTelefonoNotificaciones()).isNull();
        }

        @Test
        @DisplayName("Should support decimal precision for deposit amounts")
        void shouldSupportDecimalPrecisionForDepositAmounts() {
            ContratoRenta contrato = new ContratoRenta();

            BigDecimal preciseDeposit = new BigDecimal("12345.67");
            contrato.setDepositoGarantia(preciseDeposit);

            assertThat(contrato.getDepositoGarantia()).isEqualByComparingTo(preciseDeposit);
        }

        @Test
        @DisplayName("Should use default status when created with builder")
        void shouldUseDefaultStatusWhenCreatedWithBuilder() {
            ContratoRenta contrato = ContratoRenta.builder()
                    .idPropiedad(1)
                    .fechaInicioContrato(LocalDateTime.of(2024, 1, 15, 10, 0))
                    .duracionMeses(12)
                    .build();

            assertThat(contrato.getEstatusContrato()).isEqualTo("ACTIVO");
        }
    }

    @Nested
    @DisplayName("Lifecycle Event Tests")
    class LifecycleEventTests {

        @Test
        @DisplayName("Should calculate end date on create when start date and duration are set")
        void shouldCalculateEndDateOnCreateWhenStartDateAndDurationAreSet() {
            ContratoRenta contrato = new ContratoRenta();
            contrato.setFechaInicioContrato(LocalDateTime.of(2024, 1, 15, 10, 0));
            contrato.setDuracionMeses(12);

            contrato.onCreate();

            assertThat(contrato.getFechaFinContrato()).isEqualTo(LocalDateTime.of(2025, 1, 15, 10, 0));
        }

        @Test
        @DisplayName("Should not calculate end date on create when end date is already set")
        void shouldNotCalculateEndDateOnCreateWhenEndDateIsAlreadySet() {
            ContratoRenta contrato = new ContratoRenta();
            contrato.setFechaInicioContrato(LocalDateTime.of(2024, 1, 15, 10, 0));
            contrato.setFechaFinContrato(LocalDateTime.of(2024, 6, 30, 10, 0));
            contrato.setDuracionMeses(12);

            contrato.onCreate();

            assertThat(contrato.getFechaFinContrato()).isEqualTo(LocalDateTime.of(2024, 6, 30, 10, 0));
        }

        @Test
        @DisplayName("Should not calculate end date on create when start date is null")
        void shouldNotCalculateEndDateOnCreateWhenStartDateIsNull() {
            ContratoRenta contrato = new ContratoRenta();
            contrato.setDuracionMeses(12);

            contrato.onCreate();

            assertThat(contrato.getFechaFinContrato()).isNull();
        }

        @Test
        @DisplayName("Should not calculate end date on create when duration is null")
        void shouldNotCalculateEndDateOnCreateWhenDurationIsNull() {
            ContratoRenta contrato = new ContratoRenta();
            contrato.setFechaInicioContrato(LocalDateTime.of(2024, 1, 15, 10, 0));

            contrato.onCreate();

            assertThat(contrato.getFechaFinContrato()).isNull();
        }

        @Test
        @DisplayName("Should recalculate end date on update")
        void shouldRecalculateEndDateOnUpdate() {
            ContratoRenta contrato = new ContratoRenta();
            contrato.setFechaInicioContrato(LocalDateTime.of(2024, 1, 15, 10, 0));
            contrato.setDuracionMeses(12);
            contrato.setFechaFinContrato(LocalDateTime.of(2025, 1, 15, 10, 0));

            contrato.setDuracionMeses(18);
            contrato.onUpdate();

            assertThat(contrato.getFechaFinContrato()).isEqualTo(LocalDateTime.of(2025, 7, 15, 10, 0));
        }

        @Test
        @DisplayName("Should not recalculate end date on update when start date is null")
        void shouldNotRecalculateEndDateOnUpdateWhenStartDateIsNull() {
            ContratoRenta contrato = new ContratoRenta();
            contrato.setDuracionMeses(12);
            contrato.setFechaFinContrato(LocalDateTime.of(2025, 1, 15, 10, 0));

            contrato.onUpdate();

            assertThat(contrato.getFechaFinContrato()).isEqualTo(LocalDateTime.of(2025, 1, 15, 10, 0));
        }

        @Test
        @DisplayName("Should not recalculate end date on update when duration is null")
        void shouldNotRecalculateEndDateOnUpdateWhenDurationIsNull() {
            ContratoRenta contrato = new ContratoRenta();
            contrato.setFechaInicioContrato(LocalDateTime.of(2024, 1, 15, 10, 0));
            contrato.setFechaFinContrato(LocalDateTime.of(2025, 1, 15, 10, 0));

            contrato.onUpdate();

            assertThat(contrato.getFechaFinContrato()).isEqualTo(LocalDateTime.of(2025, 1, 15, 10, 0));
        }
    }

    @Nested
    @DisplayName("Business Logic Validation Tests")
    class BusinessLogicValidationTests {

        @Test
        @DisplayName("Should validate different contract statuses")
        void shouldValidateDifferentContractStatuses() {
            String[] validStatuses = {"ACTIVO", "VENCIDO", "TERMINADO", "SUSPENDIDO"};

            for (String status : validStatuses) {
                validContrato.setEstatusContrato(status);
                Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);
                assertThat(violations).isEmpty();
            }
        }

        @Test
        @DisplayName("Should validate reasonable duration ranges")
        void shouldValidateReasonableDurationRanges() {
            Integer[] validDurations = {1, 6, 12, 18, 24, 36, 60};

            for (Integer duration : validDurations) {
                validContrato.setDuracionMeses(duration);
                Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);
                assertThat(violations).isEmpty();
            }
        }

        @Test
        @DisplayName("Should validate reasonable notification periods")
        void shouldValidateReasonableNotificationPeriods() {
            Integer[] validNotificationDays = {1, 7, 15, 30, 45, 60, 90};

            for (Integer days : validNotificationDays) {
                validContrato.setNotificacionDiasPrevios(days);
                Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);
                assertThat(violations).isEmpty();
            }
        }

        @Test
        @DisplayName("Should handle complex special conditions")
        void shouldHandleComplexSpecialConditions() {
            String complexConditions = "No pets allowed, No smoking, Maximum 2 visitors at a time, " +
                    "Quiet hours from 10 PM to 8 AM, No alterations without written permission";

            validContrato.setCondicionesEspeciales(complexConditions);

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle minimum valid values")
        void shouldHandleMinimumValidValues() {
            validContrato.setDuracionMeses(1);
            validContrato.setNotificacionDiasPrevios(1);

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle very large valid values")
        void shouldHandleVeryLargeValidValues() {
            validContrato.setDuracionMeses(999);
            validContrato.setNotificacionDiasPrevios(365);
            validContrato.setDepositoGarantia(new BigDecimal("999999.99"));

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle special characters in text fields")
        void shouldHandleSpecialCharactersInTextFields() {
            validContrato.setCondicionesEspeciales("Condiciones con caracteres especiales: áéíóú ñ @#$% 中文");
            validContrato.setEmailNotificaciones("usuario.especial+test@dominio-ejemplo.com");

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle boundary date values")
        void shouldHandleBoundaryDateValues() {
            validContrato.setFechaInicioContrato(LocalDateTime.of(1970, 1, 1, 0, 0));
            validContrato.setFechaFinContrato(LocalDateTime.of(2099, 12, 31, 23, 59));

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle zero deposit amount")
        void shouldHandleZeroDepositAmount() {
            validContrato.setDepositoGarantia(BigDecimal.ZERO);

            Set<ConstraintViolation<ContratoRenta>> violations = validator.validate(validContrato);

            assertThat(violations).isEmpty();
        }
    }
}