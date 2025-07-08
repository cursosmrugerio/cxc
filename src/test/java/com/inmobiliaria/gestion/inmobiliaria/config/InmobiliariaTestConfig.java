package com.inmobiliaria.gestion.inmobiliaria.config;

import com.inmobiliaria.gestion.inmobiliaria.dto.InmobiliariaDTO;
import com.inmobiliaria.gestion.inmobiliaria.model.Inmobiliaria;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.LocalDate;

/**
 * Test configuration class for Inmobiliaria tests.
 * Provides common test data builders and utilities.
 */
@TestConfiguration
public class InmobiliariaTestConfig {

    /**
     * Test data builder for creating Inmobiliaria entities.
     */
    @Bean
    @Primary
    public InmobiliariaTestDataBuilder inmobiliariaTestDataBuilder() {
        return new InmobiliariaTestDataBuilder();
    }

    /**
     * Utility class for building test data consistently across all tests.
     */
    public static class InmobiliariaTestDataBuilder {

        public Inmobiliaria buildDefaultInmobiliaria() {
            return buildInmobiliaria(
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
                    "ACTIVE"
            );
        }

        public InmobiliariaDTO buildDefaultInmobiliariaDTO() {
            return buildInmobiliariaDTO(
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
                    "ACTIVE"
            );
        }

        public Inmobiliaria buildInmobiliaria(String nombreComercial, String razonSocial, String rfcNit,
                                            String telefono, String email, String direccion, String ciudad,
                                            String estado, String codigoPostal, String contacto, String estatus) {
            Inmobiliaria inmobiliaria = new Inmobiliaria();
            inmobiliaria.setNombreComercial(nombreComercial);
            inmobiliaria.setRazonSocial(razonSocial);
            inmobiliaria.setRfcNit(rfcNit);
            inmobiliaria.setTelefonoPrincipal(telefono);
            inmobiliaria.setEmailContacto(email);
            inmobiliaria.setDireccionCompleta(direccion);
            inmobiliaria.setCiudad(ciudad);
            inmobiliaria.setEstado(estado);
            inmobiliaria.setCodigoPostal(codigoPostal);
            inmobiliaria.setPersonaContacto(contacto);
            inmobiliaria.setEstatus(estatus);
            inmobiliaria.setFechaRegistro(LocalDate.now());
            return inmobiliaria;
        }

        public InmobiliariaDTO buildInmobiliariaDTO(Long id, String nombreComercial, String razonSocial, String rfcNit,
                                                  String telefono, String email, String direccion, String ciudad,
                                                  String estado, String codigoPostal, String contacto, String estatus) {
            return new InmobiliariaDTO(
                    id, nombreComercial, razonSocial, rfcNit, telefono, email, direccion,
                    ciudad, estado, codigoPostal, contacto, LocalDate.now(), estatus
            );
        }

        public Inmobiliaria buildInmobiliariaWithId(Long id) {
            Inmobiliaria inmobiliaria = buildDefaultInmobiliaria();
            inmobiliaria.setIdInmobiliaria(id);
            return inmobiliaria;
        }

        public InmobiliariaDTO buildNewInmobiliariaDTO() {
            return buildInmobiliariaDTO(
                    null, // No ID for new entity
                    "Nueva Inmobiliaria",
                    "Nueva Inmobiliaria S.A.",
                    "NIN987654321",
                    "555-987-6543",
                    "contacto@nueva.com",
                    "Calle Nueva 456",
                    "Guadalajara",
                    "Jalisco",
                    "44100",
                    "Juan Pérez",
                    "ACTIVE"
            );
        }

        public Inmobiliaria buildInactiveInmobiliaria() {
            Inmobiliaria inmobiliaria = buildDefaultInmobiliaria();
            inmobiliaria.setEstatus("INACTIVE");
            inmobiliaria.setNombreComercial("Inmobiliaria Inactiva");
            inmobiliaria.setRfcNit("IIN999888777");
            return inmobiliaria;
        }

        public InmobiliariaDTO buildInvalidEmailDTO() {
            return buildInmobiliariaDTO(
                    null,
                    "Test Inmobiliaria",
                    "Test Legal",
                    "TEST123456789",
                    "555-999-9999",
                    "invalid-email", // Invalid email format
                    "Test Address",
                    "Test City",
                    "Test State",
                    "99999",
                    "Test Contact",
                    "ACTIVE"
            );
        }

        public InmobiliariaDTO buildMinimalValidDTO() {
            return new InmobiliariaDTO(
                    null,
                    "A", // Minimum valid length
                    "B", // Minimum valid length
                    "C", // Minimum valid length
                    null, // Optional
                    null, // Optional
                    null, // Optional
                    null, // Optional
                    null, // Optional
                    null, // Optional
                    null, // Optional
                    LocalDate.now(),
                    "ACTIVE"
            );
        }

        public InmobiliariaDTO buildInvalidDTO() {
            return new InmobiliariaDTO(
                    null,
                    null, // Invalid - required field
                    "", // Invalid - blank
                    null, // Invalid - required field
                    "555-123-4567",
                    "invalid-email", // Invalid email format
                    "Address",
                    "City",
                    "State",
                    "12345",
                    "Contact",
                    null, // Invalid - required field
                    null
            );
        }

        public InmobiliariaDTO buildDuplicateRfcDTO(String existingRfc) {
            return buildInmobiliariaDTO(
                    null,
                    "Duplicate RFC Test",
                    "Duplicate RFC Legal",
                    existingRfc, // Using existing RFC to trigger duplicate error
                    "555-111-2222",
                    "duplicate@test.com",
                    "Duplicate Address",
                    "Duplicate City",
                    "Duplicate State",
                    "11111",
                    "Duplicate Contact",
                    "ACTIVE"
            );
        }
    }

    /**
     * Common test constants used across Inmobiliaria tests.
     */
    public static class TestConstants {
        public static final String DEFAULT_RFC = "ILP123456789";
        public static final String DEFAULT_NOMBRE_COMERCIAL = "Inmobiliaria Los Pinos";
        public static final String DEFAULT_RAZON_SOCIAL = "Inmobiliaria Los Pinos S.A. de C.V.";
        public static final String DEFAULT_EMAIL = "contacto@lospinos.com";
        public static final String DEFAULT_TELEFONO = "555-123-4567";
        public static final String DEFAULT_CIUDAD = "Ciudad de México";
        public static final String DEFAULT_ESTADO = "CDMX";
        public static final String DEFAULT_ESTATUS = "ACTIVE";

        public static final String INVALID_EMAIL = "invalid-email";
        public static final String INVALID_ESTATUS = "INVALID_STATUS";

        public static final String API_BASE_PATH = "/api/v1/inmobiliarias";
        public static final String API_SEARCH_PATH = API_BASE_PATH + "/search";
        public static final String API_RFC_PATH = API_BASE_PATH + "/rfc";
        public static final String API_STATUS_PATH = API_BASE_PATH + "/status";
        public static final String API_STATISTICS_PATH = API_BASE_PATH + "/statistics";
        public static final String API_CITIES_PATH = API_BASE_PATH + "/cities";
        public static final String API_STATES_PATH = API_BASE_PATH + "/states";

        public static final String ROLE_ADMIN = "ROLE_ADMIN";
        public static final String ROLE_USER = "ROLE_USER";

        public static final int DEFAULT_PAGE_SIZE = 10;
        public static final int DEFAULT_PAGE_NUMBER = 0;
        public static final String DEFAULT_SORT_BY = "nombreComercial";
        public static final String DEFAULT_SORT_DIR = "asc";
    }

    /**
     * Helper methods for test assertions and validations.
     */
    public static class TestAssertions {

        public static void assertInmobiliariaEquals(Inmobiliaria expected, Inmobiliaria actual) {
            if (expected == null && actual == null) {
                return;
            }
            
            if (expected == null || actual == null) {
                throw new AssertionError("One of the inmobiliarias is null");
            }

            assert expected.getNombreComercial().equals(actual.getNombreComercial()) : "Nombre comercial mismatch";
            assert expected.getRazonSocial().equals(actual.getRazonSocial()) : "Razón social mismatch";
            assert expected.getRfcNit().equals(actual.getRfcNit()) : "RFC/NIT mismatch";
            assert expected.getEstatus().equals(actual.getEstatus()) : "Status mismatch";
        }

        public static void assertInmobiliariaDTOEquals(InmobiliariaDTO expected, InmobiliariaDTO actual) {
            if (expected == null && actual == null) {
                return;
            }
            
            if (expected == null || actual == null) {
                throw new AssertionError("One of the inmobiliaria DTOs is null");
            }

            assert expected.nombreComercial().equals(actual.nombreComercial()) : "Nombre comercial mismatch";
            assert expected.razonSocial().equals(actual.razonSocial()) : "Razón social mismatch";
            assert expected.rfcNit().equals(actual.rfcNit()) : "RFC/NIT mismatch";
            assert expected.estatus().equals(actual.estatus()) : "Status mismatch";
        }

        public static void assertValidInmobiliariaData(InmobiliariaDTO dto) {
            assert dto != null : "DTO should not be null";
            assert dto.nombreComercial() != null && !dto.nombreComercial().trim().isEmpty() : "Nombre comercial required";
            assert dto.razonSocial() != null && !dto.razonSocial().trim().isEmpty() : "Razón social required";
            assert dto.rfcNit() != null && !dto.rfcNit().trim().isEmpty() : "RFC/NIT required";
            assert dto.estatus() != null && !dto.estatus().trim().isEmpty() : "Estatus required";
        }
    }
}