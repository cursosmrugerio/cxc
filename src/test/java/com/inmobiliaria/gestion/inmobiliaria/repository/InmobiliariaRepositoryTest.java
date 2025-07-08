package com.inmobiliaria.gestion.inmobiliaria.repository;

import com.inmobiliaria.gestion.inmobiliaria.model.Inmobiliaria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("InmobiliariaRepository Tests")
class InmobiliariaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InmobiliariaRepository inmobiliariaRepository;

    private Inmobiliaria testInmobiliaria1;
    private Inmobiliaria testInmobiliaria2;
    private Inmobiliaria testInmobiliaria3;

    @BeforeEach
    void setUp() {
        // Clean database
        entityManager.clear();

        // Create test data
        testInmobiliaria1 = createInmobiliaria(
                "Inmobiliaria Los Pinos",
                "Inmobiliaria Los Pinos S.A. de C.V.",
                "ILP123456789",
                "555-123-4567",
                "contacto@lospinos.com",
                "Av. Principal 123",
                "Ciudad de México",
                "CDMX",
                "06700",
                "María González",
                "ACTIVE"
        );

        testInmobiliaria2 = createInmobiliaria(
                "Inmobiliaria del Valle",
                "Inmobiliaria del Valle S.A.",
                "IDV987654321",
                "555-987-6543",
                "info@delvalle.com",
                "Calle Secundaria 456",
                "Guadalajara",
                "Jalisco",
                "44100",
                "Juan Pérez",
                "ACTIVE"
        );

        testInmobiliaria3 = createInmobiliaria(
                "Inmobiliaria Norteña",
                "Inmobiliaria Norteña S.C.",
                "INR555666777",
                "555-555-5555",
                "contacto@nortena.com",
                "Av. Norte 789",
                "Monterrey",
                "Nuevo León",
                "64000",
                "Ana López",
                "INACTIVE"
        );

        // Persist test data
        entityManager.persistAndFlush(testInmobiliaria1);
        entityManager.persistAndFlush(testInmobiliaria2);
        entityManager.persistAndFlush(testInmobiliaria3);
    }

    private Inmobiliaria createInmobiliaria(String nombreComercial, String razonSocial, String rfcNit,
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
        inmobiliaria.setFechaRegistro(LocalDateTime.now());
        return inmobiliaria;
    }

    @Nested
    @DisplayName("Basic CRUD Operations")
    class BasicCrudOperations {

        @Test
        @DisplayName("Should save and find inmobiliaria by ID")
        void shouldSaveAndFindInmobiliariaById() {
            // Given
            Inmobiliaria newInmobiliaria = createInmobiliaria(
                    "Test Inmobiliaria", "Test Legal", "TEST123", "123456789", "test@test.com",
                    "Test Address", "Test City", "Test State", "12345", "Test Contact", "ACTIVE"
            );

            // When
            Inmobiliaria saved = inmobiliariaRepository.save(newInmobiliaria);
            Optional<Inmobiliaria> found = inmobiliariaRepository.findById(saved.getIdInmobiliaria());

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getNombreComercial()).isEqualTo("Test Inmobiliaria");
            assertThat(found.get().getRfcNit()).isEqualTo("TEST123");
        }

        @Test
        @DisplayName("Should find all inmobiliarias")
        void shouldFindAllInmobiliarias() {
            // When
            List<Inmobiliaria> inmobiliarias = inmobiliariaRepository.findAll();

            // Then
            assertThat(inmobiliarias).hasSize(3);
            assertThat(inmobiliarias).extracting(Inmobiliaria::getNombreComercial)
                    .containsExactlyInAnyOrder("Inmobiliaria Los Pinos", "Inmobiliaria del Valle", "Inmobiliaria Norteña");
        }

        @Test
        @DisplayName("Should delete inmobiliaria by ID")
        void shouldDeleteInmobiliariaById() {
            // Given
            Long id = testInmobiliaria1.getIdInmobiliaria();

            // When
            inmobiliariaRepository.deleteById(id);
            Optional<Inmobiliaria> found = inmobiliariaRepository.findById(id);

            // Then
            assertThat(found).isEmpty();
            assertThat(inmobiliariaRepository.findAll()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Custom Query Methods")
    class CustomQueryMethods {

        @Test
        @DisplayName("Should find by RFC/NIT")
        void shouldFindByRfcNit() {
            // When
            Optional<Inmobiliaria> found = inmobiliariaRepository.findByRfcNit("ILP123456789");

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getNombreComercial()).isEqualTo("Inmobiliaria Los Pinos");
        }

        @Test
        @DisplayName("Should return empty when RFC/NIT not found")
        void shouldReturnEmptyWhenRfcNitNotFound() {
            // When
            Optional<Inmobiliaria> found = inmobiliariaRepository.findByRfcNit("NOTFOUND");

            // Then
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should find by status")
        void shouldFindByEstatus() {
            // When
            List<Inmobiliaria> activeInmobiliarias = inmobiliariaRepository.findByEstatus("ACTIVE");
            List<Inmobiliaria> inactiveInmobiliarias = inmobiliariaRepository.findByEstatus("INACTIVE");

            // Then
            assertThat(activeInmobiliarias).hasSize(2);
            assertThat(inactiveInmobiliarias).hasSize(1);
            assertThat(inactiveInmobiliarias.get(0).getNombreComercial()).isEqualTo("Inmobiliaria Norteña");
        }

        @Test
        @DisplayName("Should find by nombre comercial containing (case insensitive)")
        void shouldFindByNombreComercialContainingIgnoreCase() {
            // When
            List<Inmobiliaria> found = inmobiliariaRepository.findByNombreComercialContainingIgnoreCase("pinos");

            // Then
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getNombreComercial()).isEqualTo("Inmobiliaria Los Pinos");
        }

        @Test
        @DisplayName("Should find by ciudad (case insensitive)")
        void shouldFindByCiudadIgnoreCase() {
            // When
            List<Inmobiliaria> found = inmobiliariaRepository.findByCiudadIgnoreCase("CIUDAD DE MÉXICO");

            // Then
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getCiudad()).isEqualTo("Ciudad de México");
        }

        @Test
        @DisplayName("Should find by estado (case insensitive)")
        void shouldFindByEstadoIgnoreCase() {
            // When
            List<Inmobiliaria> found = inmobiliariaRepository.findByEstadoIgnoreCase("jalisco");

            // Then
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getEstado()).isEqualTo("Jalisco");
        }
    }

    @Nested
    @DisplayName("Existence Checks")
    class ExistenceChecks {

        @Test
        @DisplayName("Should check if RFC/NIT exists")
        void shouldCheckIfRfcNitExists() {
            // When
            boolean exists = inmobiliariaRepository.existsByRfcNit("ILP123456789");
            boolean notExists = inmobiliariaRepository.existsByRfcNit("NOTFOUND");

            // Then
            assertThat(exists).isTrue();
            assertThat(notExists).isFalse();
        }

        @Test
        @DisplayName("Should check if RFC/NIT exists for different inmobiliaria")
        void shouldCheckIfRfcNitExistsForDifferentInmobiliaria() {
            // When
            boolean existsForDifferent = inmobiliariaRepository.existsByRfcNitAndIdInmobiliariaNot(
                    "ILP123456789", testInmobiliaria2.getIdInmobiliaria());
            boolean notExistsForSame = inmobiliariaRepository.existsByRfcNitAndIdInmobiliariaNot(
                    "ILP123456789", testInmobiliaria1.getIdInmobiliaria());

            // Then
            assertThat(existsForDifferent).isTrue();
            assertThat(notExistsForSame).isFalse();
        }
    }

    @Nested
    @DisplayName("Count Operations")
    class CountOperations {

        @Test
        @DisplayName("Should count by status")
        void shouldCountByEstatus() {
            // When
            long activeCount = inmobiliariaRepository.countByEstatus("ACTIVE");
            long inactiveCount = inmobiliariaRepository.countByEstatus("INACTIVE");
            long suspendedCount = inmobiliariaRepository.countByEstatus("SUSPENDED");

            // Then
            assertThat(activeCount).isEqualTo(2);
            assertThat(inactiveCount).isEqualTo(1);
            assertThat(suspendedCount).isZero();
        }
    }

    @Nested
    @DisplayName("Distinct Values")
    class DistinctValues {

        @Test
        @DisplayName("Should find distinct cities")
        void shouldFindDistinctCiudades() {
            // When
            List<String> cities = inmobiliariaRepository.findDistinctCiudades();

            // Then
            assertThat(cities).hasSize(3);
            assertThat(cities).containsExactlyInAnyOrder("Ciudad de México", "Guadalajara", "Monterrey");
        }

        @Test
        @DisplayName("Should find distinct states")
        void shouldFindDistinctEstados() {
            // When
            List<String> states = inmobiliariaRepository.findDistinctEstados();

            // Then
            assertThat(states).hasSize(3);
            assertThat(states).containsExactlyInAnyOrder("CDMX", "Jalisco", "Nuevo León");
        }
    }

    @Nested
    @DisplayName("Filter Search")
    class FilterSearch {

        @Test
        @DisplayName("Should find by filters with all parameters")
        void shouldFindByFiltersWithAllParameters() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Inmobiliaria> result = inmobiliariaRepository.findByFilters(
                    "Los Pinos", "CDMX", "Ciudad", "ACTIVE", "ILP", pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getNombreComercial()).isEqualTo("Inmobiliaria Los Pinos");
        }

        @Test
        @DisplayName("Should find by filters with partial parameters")
        void shouldFindByFiltersWithPartialParameters() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Inmobiliaria> result = inmobiliariaRepository.findByFilters(
                    null, "Jalisco", null, "ACTIVE", null, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getNombreComercial()).isEqualTo("Inmobiliaria del Valle");
        }

        @Test
        @DisplayName("Should find by filters with no parameters (find all)")
        void shouldFindByFiltersWithNoParameters() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Inmobiliaria> result = inmobiliariaRepository.findByFilters(
                    null, null, null, null, null, pageable);

            // Then
            assertThat(result.getContent()).hasSize(3);
        }

        @Test
        @DisplayName("Should find by filters with pagination")
        void shouldFindByFiltersWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 2);

            // When
            Page<Inmobiliaria> result = inmobiliariaRepository.findByFilters(
                    null, null, null, null, null, pageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should handle case insensitive search")
        void shouldHandleCaseInsensitiveSearch() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Inmobiliaria> result = inmobiliariaRepository.findByFilters(
                    "VALLE", "jalisco", "guadalajara", "active", "idv", pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getNombreComercial()).isEqualTo("Inmobiliaria del Valle");
        }

        @Test
        @DisplayName("Should return empty result when no matches")
        void shouldReturnEmptyResultWhenNoMatches() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Inmobiliaria> result = inmobiliariaRepository.findByFilters(
                    "NotFound", null, null, null, null, pageable);

            // Then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("Data Integrity")
    class DataIntegrity {

        @Test
        @DisplayName("Should enforce unique RFC/NIT constraint")
        void shouldEnforceUniqueRfcNitConstraint() {
            // Given
            Inmobiliaria duplicateRfc = createInmobiliaria(
                    "Duplicate RFC", "Duplicate Legal", "ILP123456789", // Same RFC as testInmobiliaria1
                    "999-999-9999", "duplicate@test.com", "Duplicate Address",
                    "Duplicate City", "Duplicate State", "99999", "Duplicate Contact", "ACTIVE"
            );

            // When & Then
            try {
                entityManager.persistAndFlush(duplicateRfc);
            } catch (Exception e) {
                // Expected: Unique constraint violation
                assertThat(e).hasMessageContaining("could not execute statement");
            }
        }

        @Test
        @DisplayName("Should auto-generate fecha registro on persist")
        void shouldAutoGenerateFechaRegistroOnPersist() {
            // Given
            LocalDateTime beforeSave = LocalDateTime.now().minusSeconds(1);
            Inmobiliaria newInmobiliaria = createInmobiliaria(
                    "Auto Date Test", "Auto Date Legal", "ADT123456789",
                    "111-111-1111", "autodate@test.com", "Auto Date Address",
                    "Auto Date City", "Auto Date State", "11111", "Auto Date Contact", "ACTIVE"
            );
            newInmobiliaria.setFechaRegistro(null); // Clear the date to test auto-generation

            // When
            Inmobiliaria saved = entityManager.persistAndFlush(newInmobiliaria);
            LocalDateTime afterSave = LocalDateTime.now().plusSeconds(1);

            // Then
            assertThat(saved.getFechaRegistro()).isNotNull();
            assertThat(saved.getFechaRegistro()).isAfter(beforeSave);
            assertThat(saved.getFechaRegistro()).isBefore(afterSave);
        }

        @Test
        @DisplayName("Should set default status when not provided")
        void shouldSetDefaultStatusWhenNotProvided() {
            // Given
            Inmobiliaria newInmobiliaria = createInmobiliaria(
                    "Default Status Test", "Default Status Legal", "DST123456789",
                    "222-222-2222", "defaultstatus@test.com", "Default Status Address",
                    "Default Status City", "Default Status State", "22222", "Default Status Contact", null
            );
            newInmobiliaria.setEstatus(null);

            // When
            Inmobiliaria saved = entityManager.persistAndFlush(newInmobiliaria);

            // Then
            assertThat(saved.getEstatus()).isEqualTo("ACTIVE");
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should handle large dataset efficiently")
        void shouldHandleLargeDatasetEfficiently() {
            // Given - Create multiple test records
            for (int i = 1; i <= 50; i++) {
                Inmobiliaria inmobiliaria = createInmobiliaria(
                        "Inmobiliaria " + i,
                        "Inmobiliaria Legal " + i,
                        "RFC" + String.format("%09d", i),
                        "555-" + String.format("%03d", i) + "-0000",
                        "contacto" + i + "@test.com",
                        "Direccion " + i,
                        "Ciudad " + i,
                        "Estado " + i,
                        String.format("%05d", i),
                        "Contacto " + i,
                        i % 3 == 0 ? "INACTIVE" : "ACTIVE"
                );
                entityManager.persist(inmobiliaria);
            }
            entityManager.flush();

            // When
            long startTime = System.currentTimeMillis();
            Page<Inmobiliaria> result = inmobiliariaRepository.findByFilters(
                    null, null, null, "ACTIVE", null, PageRequest.of(0, 20));
            long endTime = System.currentTimeMillis();

            // Then
            assertThat(result.getContent()).hasSizeGreaterThan(0);
            assertThat(endTime - startTime).isLessThan(1000); // Should complete within 1 second
        }
    }
}