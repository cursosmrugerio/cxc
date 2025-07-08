package com.inmobiliaria.gestion.inmobiliaria.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Inmobiliaria Entity Tests")
class InmobiliariaEntityTest {

    private Validator validator;
    private Inmobiliaria validInmobiliaria;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        validInmobiliaria = Inmobiliaria.builder()
                .nombreComercial("Test Real Estate")
                .razonSocial("Test Real Estate S.A. de C.V.")
                .rfcNit("TEST123456789")
                .telefonoPrincipal("+52 55 1234 5678")
                .emailContacto("contact@test.com")
                .direccionCompleta("Av. Test 123, Col. Centro")
                .ciudad("Test City")
                .estado("Test State")
                .codigoPostal("12345")
                .personaContacto("Juan Test")
                .build();
    }

    @Nested
    @DisplayName("Constructor and Builder Tests")
    class ConstructorAndBuilderTests {

        @Test
        @DisplayName("Should create inmobiliaria with no args constructor")
        void shouldCreateInmobiliariaWithNoArgsConstructor() {
            Inmobiliaria inmobiliaria = new Inmobiliaria();
            assertNotNull(inmobiliaria);
            assertEquals("ACTIVE", inmobiliaria.getEstatus());
        }

        @Test
        @DisplayName("Should create inmobiliaria with builder")
        void shouldCreateInmobiliariaWithBuilder() {
            LocalDate testDate = LocalDate.of(2025, 1, 1);
            
            Inmobiliaria inmobiliaria = Inmobiliaria.builder()
                    .idInmobiliaria(1L)
                    .nombreComercial("Test Real Estate")
                    .razonSocial("Test Real Estate S.A.")
                    .rfcNit("TEST123456789")
                    .telefonoPrincipal("+52 55 1234 5678")
                    .emailContacto("test@example.com")
                    .direccionCompleta("Test Address")
                    .ciudad("Test City")
                    .estado("Test State")
                    .codigoPostal("12345")
                    .personaContacto("Test Person")
                    .fechaRegistro(testDate)
                    .estatus("ACTIVE")
                    .build();
            
            assertEquals(1L, inmobiliaria.getIdInmobiliaria());
            assertEquals("Test Real Estate", inmobiliaria.getNombreComercial());
            assertEquals("Test Real Estate S.A.", inmobiliaria.getRazonSocial());
            assertEquals("TEST123456789", inmobiliaria.getRfcNit());
            assertEquals("+52 55 1234 5678", inmobiliaria.getTelefonoPrincipal());
            assertEquals("test@example.com", inmobiliaria.getEmailContacto());
            assertEquals("Test Address", inmobiliaria.getDireccionCompleta());
            assertEquals("Test City", inmobiliaria.getCiudad());
            assertEquals("Test State", inmobiliaria.getEstado());
            assertEquals("12345", inmobiliaria.getCodigoPostal());
            assertEquals("Test Person", inmobiliaria.getPersonaContacto());
            assertEquals(testDate, inmobiliaria.getFechaRegistro());
            assertEquals("ACTIVE", inmobiliaria.getEstatus());
        }

        @Test
        @DisplayName("Should create inmobiliaria with all args constructor")
        void shouldCreateInmobiliariaWithAllArgsConstructor() {
            LocalDate testDate = LocalDate.now();
            
            Inmobiliaria inmobiliaria = new Inmobiliaria(
                    1L, "Test", "Test S.A.", "TEST123", "+52", "test@test.com",
                    "Address", "City", "State", "12345", "Person", testDate, "ACTIVE"
            );
            
            assertEquals(1L, inmobiliaria.getIdInmobiliaria());
            assertEquals("Test", inmobiliaria.getNombreComercial());
            assertEquals("Test S.A.", inmobiliaria.getRazonSocial());
            assertEquals("TEST123", inmobiliaria.getRfcNit());
            assertEquals("+52", inmobiliaria.getTelefonoPrincipal());
            assertEquals("test@test.com", inmobiliaria.getEmailContacto());
            assertEquals("Address", inmobiliaria.getDireccionCompleta());
            assertEquals("City", inmobiliaria.getCiudad());
            assertEquals("State", inmobiliaria.getEstado());
            assertEquals("12345", inmobiliaria.getCodigoPostal());
            assertEquals("Person", inmobiliaria.getPersonaContacto());
            assertEquals(testDate, inmobiliaria.getFechaRegistro());
            assertEquals("ACTIVE", inmobiliaria.getEstatus());
        }
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should have default status as ACTIVE")
        void shouldHaveDefaultStatusAsActive() {
            Inmobiliaria inmobiliaria = Inmobiliaria.builder()
                    .nombreComercial("Test")
                    .build();
            
            assertEquals("ACTIVE", inmobiliaria.getEstatus());
        }

        @Test
        @DisplayName("Should preserve custom status when provided")
        void shouldPreserveCustomStatusWhenProvided() {
            Inmobiliaria inmobiliaria = Inmobiliaria.builder()
                    .nombreComercial("Test")
                    .estatus("INACTIVE")
                    .build();
            
            assertEquals("INACTIVE", inmobiliaria.getEstatus());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should pass validation with valid inmobiliaria")
        void shouldPassValidationWithValidInmobiliaria() {
            Set<ConstraintViolation<Inmobiliaria>> violations = validator.validate(validInmobiliaria);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when commercial name exceeds max size")
        void shouldFailValidationWhenCommercialNameExceedsMaxSize() {
            validInmobiliaria.setNombreComercial("a".repeat(256)); // Max is 255
            
            Set<ConstraintViolation<Inmobiliaria>> violations = validator.validate(validInmobiliaria);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("nombreComercial")
                            && v.getMessage().contains("Commercial name must not exceed 255 characters")));
        }

        @Test
        @DisplayName("Should fail validation when business name exceeds max size")
        void shouldFailValidationWhenBusinessNameExceedsMaxSize() {
            validInmobiliaria.setRazonSocial("a".repeat(256)); // Max is 255
            
            Set<ConstraintViolation<Inmobiliaria>> violations = validator.validate(validInmobiliaria);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("razonSocial")
                            && v.getMessage().contains("Business name must not exceed 255 characters")));
        }

        @Test
        @DisplayName("Should fail validation when RFC/NIT exceeds max size")
        void shouldFailValidationWhenRfcNitExceedsMaxSize() {
            validInmobiliaria.setRfcNit("a".repeat(256)); // Max is 255
            
            Set<ConstraintViolation<Inmobiliaria>> violations = validator.validate(validInmobiliaria);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("rfcNit")
                            && v.getMessage().contains("RFC/NIT must not exceed 255 characters")));
        }

        @Test
        @DisplayName("Should fail validation when main phone exceeds max size")
        void shouldFailValidationWhenMainPhoneExceedsMaxSize() {
            validInmobiliaria.setTelefonoPrincipal("a".repeat(256)); // Max is 255
            
            Set<ConstraintViolation<Inmobiliaria>> violations = validator.validate(validInmobiliaria);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("telefonoPrincipal")
                            && v.getMessage().contains("Main phone must not exceed 255 characters")));
        }

        @Test
        @DisplayName("Should fail validation when email is invalid format")
        void shouldFailValidationWhenEmailIsInvalidFormat() {
            validInmobiliaria.setEmailContacto("invalid-email");
            
            Set<ConstraintViolation<Inmobiliaria>> violations = validator.validate(validInmobiliaria);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("emailContacto")
                            && v.getMessage().contains("Email should be valid")));
        }

        @Test
        @DisplayName("Should fail validation when email exceeds max size")
        void shouldFailValidationWhenEmailExceedsMaxSize() {
            validInmobiliaria.setEmailContacto("a".repeat(250) + "@test.com"); // Max is 255
            
            Set<ConstraintViolation<Inmobiliaria>> violations = validator.validate(validInmobiliaria);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("emailContacto")
                            && v.getMessage().contains("Email must not exceed 255 characters")));
        }

        @Test
        @DisplayName("Should fail validation when full address exceeds max size")
        void shouldFailValidationWhenFullAddressExceedsMaxSize() {
            validInmobiliaria.setDireccionCompleta("a".repeat(256)); // Max is 255
            
            Set<ConstraintViolation<Inmobiliaria>> violations = validator.validate(validInmobiliaria);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("direccionCompleta")
                            && v.getMessage().contains("Full address must not exceed 255 characters")));
        }

        @Test
        @DisplayName("Should fail validation when city exceeds max size")
        void shouldFailValidationWhenCityExceedsMaxSize() {
            validInmobiliaria.setCiudad("a".repeat(256)); // Max is 255
            
            Set<ConstraintViolation<Inmobiliaria>> violations = validator.validate(validInmobiliaria);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("ciudad")
                            && v.getMessage().contains("City must not exceed 255 characters")));
        }

        @Test
        @DisplayName("Should fail validation when state exceeds max size")
        void shouldFailValidationWhenStateExceedsMaxSize() {
            validInmobiliaria.setEstado("a".repeat(256)); // Max is 255
            
            Set<ConstraintViolation<Inmobiliaria>> violations = validator.validate(validInmobiliaria);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("estado")
                            && v.getMessage().contains("State must not exceed 255 characters")));
        }

        @Test
        @DisplayName("Should fail validation when postal code exceeds max size")
        void shouldFailValidationWhenPostalCodeExceedsMaxSize() {
            validInmobiliaria.setCodigoPostal("a".repeat(256)); // Max is 255
            
            Set<ConstraintViolation<Inmobiliaria>> violations = validator.validate(validInmobiliaria);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("codigoPostal")
                            && v.getMessage().contains("Postal code must not exceed 255 characters")));
        }

        @Test
        @DisplayName("Should fail validation when contact person exceeds max size")
        void shouldFailValidationWhenContactPersonExceedsMaxSize() {
            validInmobiliaria.setPersonaContacto("a".repeat(256)); // Max is 255
            
            Set<ConstraintViolation<Inmobiliaria>> violations = validator.validate(validInmobiliaria);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("personaContacto")
                            && v.getMessage().contains("Contact person must not exceed 255 characters")));
        }

        @Test
        @DisplayName("Should fail validation when status exceeds max size")
        void shouldFailValidationWhenStatusExceedsMaxSize() {
            validInmobiliaria.setEstatus("a".repeat(256)); // Max is 255
            
            Set<ConstraintViolation<Inmobiliaria>> violations = validator.validate(validInmobiliaria);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("estatus")
                            && v.getMessage().contains("Status must not exceed 255 characters")));
        }

        @Test
        @DisplayName("Should pass validation with null optional fields")
        void shouldPassValidationWithNullOptionalFields() {
            Inmobiliaria inmobiliaria = Inmobiliaria.builder()
                    .nombreComercial(null)
                    .razonSocial(null)
                    .rfcNit(null)
                    .telefonoPrincipal(null)
                    .emailContacto(null)
                    .direccionCompleta(null)
                    .ciudad(null)
                    .estado(null)
                    .codigoPostal(null)
                    .personaContacto(null)
                    .build();
            
            Set<ConstraintViolation<Inmobiliaria>> violations = validator.validate(inmobiliaria);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should pass validation with valid email formats")
        void shouldPassValidationWithValidEmailFormats() {
            String[] validEmails = {
                    "test@example.com",
                    "user.name@domain.co.uk",
                    "user+tag@example.org",
                    "firstname.lastname@subdomain.example.com"
            };
            
            for (String email : validEmails) {
                validInmobiliaria.setEmailContacto(email);
                Set<ConstraintViolation<Inmobiliaria>> violations = validator.validate(validInmobiliaria);
                assertTrue(violations.isEmpty(), "Email " + email + " should be valid");
            }
        }
    }

    @Nested
    @DisplayName("Lifecycle Methods Tests")
    class LifecycleMethodsTests {

        @Test
        @DisplayName("Should set registration date on onCreate when null")
        void shouldSetRegistrationDateOnCreateWhenNull() {
            Inmobiliaria inmobiliaria = new Inmobiliaria();
            assertNull(inmobiliaria.getFechaRegistro());
            
            inmobiliaria.onCreate();
            
            assertNotNull(inmobiliaria.getFechaRegistro());
            assertEquals(LocalDate.now(), inmobiliaria.getFechaRegistro());
        }

        @Test
        @DisplayName("Should not override registration date on onCreate when already set")
        void shouldNotOverrideRegistrationDateOnCreateWhenAlreadySet() {
            LocalDate existingDate = LocalDate.of(2024, 1, 1);
            Inmobiliaria inmobiliaria = new Inmobiliaria();
            inmobiliaria.setFechaRegistro(existingDate);
            
            inmobiliaria.onCreate();
            
            assertEquals(existingDate, inmobiliaria.getFechaRegistro());
        }
    }

    @Nested
    @DisplayName("Equality and HashCode Tests")
    class EqualityAndHashCodeTests {

        @Test
        @DisplayName("Should be equal when same object")
        void shouldBeEqualWhenSameObject() {
            assertEquals(validInmobiliaria, validInmobiliaria);
            assertEquals(validInmobiliaria.hashCode(), validInmobiliaria.hashCode());
        }

        @Test
        @DisplayName("Should be equal when same data")
        void shouldBeEqualWhenSameData() {
            Inmobiliaria inmobiliaria1 = Inmobiliaria.builder()
                    .idInmobiliaria(1L)
                    .nombreComercial("Test")
                    .razonSocial("Test S.A.")
                    .rfcNit("TEST123")
                    .estatus("ACTIVE")
                    .build();
            
            Inmobiliaria inmobiliaria2 = Inmobiliaria.builder()
                    .idInmobiliaria(1L)
                    .nombreComercial("Test")
                    .razonSocial("Test S.A.")
                    .rfcNit("TEST123")
                    .estatus("ACTIVE")
                    .build();
            
            assertEquals(inmobiliaria1, inmobiliaria2);
            assertEquals(inmobiliaria1.hashCode(), inmobiliaria2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different data")
        void shouldNotBeEqualWhenDifferentData() {
            Inmobiliaria inmobiliaria1 = Inmobiliaria.builder()
                    .idInmobiliaria(1L)
                    .nombreComercial("Test1")
                    .build();
            
            Inmobiliaria inmobiliaria2 = Inmobiliaria.builder()
                    .idInmobiliaria(2L)
                    .nombreComercial("Test2")
                    .build();
            
            assertNotEquals(inmobiliaria1, inmobiliaria2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, validInmobiliaria);
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            assertNotEquals("string", validInmobiliaria);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should contain all fields in toString")
        void shouldContainAllFieldsInToString() {
            Inmobiliaria inmobiliaria = Inmobiliaria.builder()
                    .idInmobiliaria(1L)
                    .nombreComercial("Test Real Estate")
                    .razonSocial("Test S.A.")
                    .rfcNit("TEST123")
                    .estatus("ACTIVE")
                    .build();
            
            String toString = inmobiliaria.toString();
            
            assertTrue(toString.contains("Inmobiliaria"));
            assertTrue(toString.contains("idInmobiliaria=1"));
            assertTrue(toString.contains("nombreComercial=Test Real Estate"));
            assertTrue(toString.contains("razonSocial=Test S.A."));
            assertTrue(toString.contains("rfcNit=TEST123"));
            assertTrue(toString.contains("estatus=ACTIVE"));
        }
    }

    @Nested
    @DisplayName("Field Accessibility Tests")
    class FieldAccessibilityTests {

        @Test
        @DisplayName("Should set and get all fields correctly")
        void shouldSetAndGetAllFieldsCorrectly() {
            Inmobiliaria inmobiliaria = new Inmobiliaria();
            LocalDate testDate = LocalDate.of(2025, 1, 1);
            
            inmobiliaria.setIdInmobiliaria(1L);
            inmobiliaria.setNombreComercial("Test Real Estate");
            inmobiliaria.setRazonSocial("Test Real Estate S.A.");
            inmobiliaria.setRfcNit("TEST123456789");
            inmobiliaria.setTelefonoPrincipal("+52 55 1234 5678");
            inmobiliaria.setEmailContacto("test@example.com");
            inmobiliaria.setDireccionCompleta("Test Address");
            inmobiliaria.setCiudad("Test City");
            inmobiliaria.setEstado("Test State");
            inmobiliaria.setCodigoPostal("12345");
            inmobiliaria.setPersonaContacto("Test Person");
            inmobiliaria.setFechaRegistro(testDate);
            inmobiliaria.setEstatus("INACTIVE");
            
            assertEquals(1L, inmobiliaria.getIdInmobiliaria());
            assertEquals("Test Real Estate", inmobiliaria.getNombreComercial());
            assertEquals("Test Real Estate S.A.", inmobiliaria.getRazonSocial());
            assertEquals("TEST123456789", inmobiliaria.getRfcNit());
            assertEquals("+52 55 1234 5678", inmobiliaria.getTelefonoPrincipal());
            assertEquals("test@example.com", inmobiliaria.getEmailContacto());
            assertEquals("Test Address", inmobiliaria.getDireccionCompleta());
            assertEquals("Test City", inmobiliaria.getCiudad());
            assertEquals("Test State", inmobiliaria.getEstado());
            assertEquals("12345", inmobiliaria.getCodigoPostal());
            assertEquals("Test Person", inmobiliaria.getPersonaContacto());
            assertEquals(testDate, inmobiliaria.getFechaRegistro());
            assertEquals("INACTIVE", inmobiliaria.getEstatus());
        }
    }
}