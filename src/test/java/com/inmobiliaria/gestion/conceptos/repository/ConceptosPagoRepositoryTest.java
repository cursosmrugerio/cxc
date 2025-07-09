package com.inmobiliaria.gestion.conceptos.repository;

import com.inmobiliaria.gestion.conceptos.model.ConceptosPago;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ConceptosPagoRepository Tests")
class ConceptosPagoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ConceptosPagoRepository conceptosPagoRepository;

    private ConceptosPago testConcepto1;
    private ConceptosPago testConcepto2;
    private ConceptosPago testConcepto3;
    private ConceptosPago testConcepto4;

    @BeforeEach
    void setUp() {
        // Clean database
        entityManager.clear();

        // Create test data
        testConcepto1 = createConceptoPago(
                1L, "Renta Mensual", "Pago de renta mensual", "RENTA", true, true
        );

        testConcepto2 = createConceptoPago(
                1L, "Servicios Básicos", "Pago de servicios básicos", "SERVICIOS", true, true
        );

        testConcepto3 = createConceptoPago(
                2L, "Mantenimiento", "Pago de mantenimiento", "MANTENIMIENTO", false, true
        );

        testConcepto4 = createConceptoPago(
                1L, "Depósito en Garantía", "Depósito en garantía", "DEPOSITO", false, false
        );

        // Persist test data
        entityManager.persistAndFlush(testConcepto1);
        entityManager.persistAndFlush(testConcepto2);
        entityManager.persistAndFlush(testConcepto3);
        entityManager.persistAndFlush(testConcepto4);
    }

    private ConceptosPago createConceptoPago(Long idInmobiliaria, String nombre, String descripcion, 
                                           String tipo, Boolean permiteRecargos, Boolean activo) {
        ConceptosPago concepto = new ConceptosPago();
        concepto.setIdInmobiliaria(idInmobiliaria);
        concepto.setNombreConcepto(nombre);
        concepto.setDescripcion(descripcion);
        concepto.setTipoConcepto(tipo);
        concepto.setPermiteRecargos(permiteRecargos);
        concepto.setActivo(activo);
        concepto.setFechaCreacion(LocalDate.now());
        return concepto;
    }

    @Nested
    @DisplayName("Basic CRUD Operations")
    class BasicCrudOperations {

        @Test
        @DisplayName("Should save and find concepto de pago by ID")
        void shouldSaveAndFindConceptoPagoById() {
            // Given
            ConceptosPago newConcepto = createConceptoPago(
                    3L, "Test Concepto", "Test Description", "TEST", true, true
            );

            // When
            ConceptosPago saved = conceptosPagoRepository.save(newConcepto);
            Optional<ConceptosPago> found = conceptosPagoRepository.findById(saved.getIdConcepto());

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getNombreConcepto()).isEqualTo("Test Concepto");
            assertThat(found.get().getTipoConcepto()).isEqualTo("TEST");
            assertThat(found.get().getIdInmobiliaria()).isEqualTo(3L);
        }

        @Test
        @DisplayName("Should find all conceptos de pago")
        void shouldFindAllConceptosPago() {
            // When
            List<ConceptosPago> conceptos = conceptosPagoRepository.findAll();

            // Then
            assertThat(conceptos).hasSize(4);
            assertThat(conceptos).extracting(ConceptosPago::getNombreConcepto)
                    .containsExactlyInAnyOrder("Renta Mensual", "Servicios Básicos", "Mantenimiento", "Depósito en Garantía");
        }

        @Test
        @DisplayName("Should delete concepto de pago by ID")
        void shouldDeleteConceptoPagoById() {
            // Given
            Integer id = testConcepto1.getIdConcepto();

            // When
            conceptosPagoRepository.deleteById(id);
            Optional<ConceptosPago> found = conceptosPagoRepository.findById(id);

            // Then
            assertThat(found).isEmpty();
            assertThat(conceptosPagoRepository.findAll()).hasSize(3);
        }
    }

    @Nested
    @DisplayName("Find by Inmobiliaria")
    class FindByInmobiliaria {

        @Test
        @DisplayName("Should find by inmobiliaria ID")
        void shouldFindByIdInmobiliaria() {
            // When
            List<ConceptosPago> conceptos = conceptosPagoRepository.findByIdInmobiliaria(1L);

            // Then
            assertThat(conceptos).hasSize(3);
            assertThat(conceptos).extracting(ConceptosPago::getNombreConcepto)
                    .containsExactlyInAnyOrder("Renta Mensual", "Servicios Básicos", "Depósito en Garantía");
        }

        @Test
        @DisplayName("Should find by inmobiliaria ID with pagination")
        void shouldFindByIdInmobiliariaWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 2);

            // When
            Page<ConceptosPago> conceptos = conceptosPagoRepository.findByIdInmobiliaria(1L, pageable);

            // Then
            assertThat(conceptos.getContent()).hasSize(2);
            assertThat(conceptos.getTotalElements()).isEqualTo(3);
            assertThat(conceptos.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should find by inmobiliaria ID and activo status")
        void shouldFindByIdInmobiliariaAndActivo() {
            // When
            List<ConceptosPago> activeConceptos = conceptosPagoRepository.findByIdInmobiliariaAndActivo(1L, true);
            List<ConceptosPago> inactiveConceptos = conceptosPagoRepository.findByIdInmobiliariaAndActivo(1L, false);

            // Then
            assertThat(activeConceptos).hasSize(2);
            assertThat(inactiveConceptos).hasSize(1);
            assertThat(inactiveConceptos.get(0).getNombreConcepto()).isEqualTo("Depósito en Garantía");
        }

        @Test
        @DisplayName("Should find by inmobiliaria ID and activo status with pagination")
        void shouldFindByIdInmobiliariaAndActivoWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<ConceptosPago> activeConceptos = conceptosPagoRepository.findByIdInmobiliariaAndActivo(1L, true, pageable);

            // Then
            assertThat(activeConceptos.getContent()).hasSize(2);
            assertThat(activeConceptos.getTotalElements()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Find by Tipo Concepto")
    class FindByTipoConcepto {

        @Test
        @DisplayName("Should find by tipo concepto")
        void shouldFindByTipoConcepto() {
            // When
            List<ConceptosPago> rentaConceptos = conceptosPagoRepository.findByTipoConcepto("RENTA");
            List<ConceptosPago> serviciosConceptos = conceptosPagoRepository.findByTipoConcepto("SERVICIOS");

            // Then
            assertThat(rentaConceptos).hasSize(1);
            assertThat(rentaConceptos.get(0).getNombreConcepto()).isEqualTo("Renta Mensual");
            assertThat(serviciosConceptos).hasSize(1);
            assertThat(serviciosConceptos.get(0).getNombreConcepto()).isEqualTo("Servicios Básicos");
        }

        @Test
        @DisplayName("Should find by tipo concepto with pagination")
        void shouldFindByTipoConceptoWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<ConceptosPago> rentaConceptos = conceptosPagoRepository.findByTipoConcepto("RENTA", pageable);

            // Then
            assertThat(rentaConceptos.getContent()).hasSize(1);
            assertThat(rentaConceptos.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should find by inmobiliaria and tipo concepto")
        void shouldFindByIdInmobiliariaAndTipoConcepto() {
            // When
            List<ConceptosPago> conceptos = conceptosPagoRepository.findByIdInmobiliariaAndTipoConcepto(1L, "RENTA");

            // Then
            assertThat(conceptos).hasSize(1);
            assertThat(conceptos.get(0).getNombreConcepto()).isEqualTo("Renta Mensual");
        }

        @Test
        @DisplayName("Should find by inmobiliaria and tipo concepto with pagination")
        void shouldFindByIdInmobiliariaAndTipoConceptoWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<ConceptosPago> conceptos = conceptosPagoRepository.findByIdInmobiliariaAndTipoConcepto(1L, "RENTA", pageable);

            // Then
            assertThat(conceptos.getContent()).hasSize(1);
            assertThat(conceptos.getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Find by Nombre Concepto")
    class FindByNombreConcepto {

        @Test
        @DisplayName("Should find by nombre concepto containing (case insensitive)")
        void shouldFindByNombreConceptoContainingIgnoreCase() {
            // When
            List<ConceptosPago> conceptos = conceptosPagoRepository.findByNombreConceptoContainingIgnoreCase("renta");

            // Then
            assertThat(conceptos).hasSize(1);
            assertThat(conceptos.get(0).getNombreConcepto()).isEqualTo("Renta Mensual");
        }

        @Test
        @DisplayName("Should find by nombre concepto containing with pagination")
        void shouldFindByNombreConceptoContainingWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<ConceptosPago> conceptos = conceptosPagoRepository.findByNombreConceptoContainingIgnoreCase("servi", pageable);

            // Then
            assertThat(conceptos.getContent()).hasSize(1);
            assertThat(conceptos.getContent().get(0).getNombreConcepto()).isEqualTo("Servicios Básicos");
        }

        @Test
        @DisplayName("Should find by inmobiliaria and nombre concepto containing")
        void shouldFindByIdInmobiliariaAndNombreConceptoContaining() {
            // When
            List<ConceptosPago> conceptos = conceptosPagoRepository.findByIdInmobiliariaAndNombreConceptoContainingIgnoreCase(1L, "depósito");

            // Then
            assertThat(conceptos).hasSize(1);
            assertThat(conceptos.get(0).getNombreConcepto()).isEqualTo("Depósito en Garantía");
        }

        @Test
        @DisplayName("Should find by inmobiliaria and nombre concepto containing with pagination")
        void shouldFindByIdInmobiliariaAndNombreConceptoContainingWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<ConceptosPago> conceptos = conceptosPagoRepository.findByIdInmobiliariaAndNombreConceptoContainingIgnoreCase(1L, "a", pageable);

            // Then
            assertThat(conceptos.getContent()).hasSize(3); // All concepts from inmobiliaria 1 contain "a"
            assertThat(conceptos.getTotalElements()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("Find by Permite Recargos")
    class FindByPermiteRecargos {

        @Test
        @DisplayName("Should find by permite recargos")
        void shouldFindByPermiteRecargos() {
            // When
            List<ConceptosPago> permitenRecargos = conceptosPagoRepository.findByPermiteRecargos(true);
            List<ConceptosPago> noPermitrenRecargos = conceptosPagoRepository.findByPermiteRecargos(false);

            // Then
            assertThat(permitenRecargos).hasSize(2);
            assertThat(noPermitrenRecargos).hasSize(2);
        }

        @Test
        @DisplayName("Should find by permite recargos with pagination")
        void shouldFindByPermiteRecargosWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<ConceptosPago> permitenRecargos = conceptosPagoRepository.findByPermiteRecargos(true, pageable);

            // Then
            assertThat(permitenRecargos.getContent()).hasSize(2);
            assertThat(permitenRecargos.getTotalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should find by inmobiliaria and permite recargos")
        void shouldFindByIdInmobiliariaAndPermiteRecargos() {
            // When
            List<ConceptosPago> conceptos = conceptosPagoRepository.findByIdInmobiliariaAndPermiteRecargos(1L, true);

            // Then
            assertThat(conceptos).hasSize(2);
            assertThat(conceptos).extracting(ConceptosPago::getNombreConcepto)
                    .containsExactlyInAnyOrder("Renta Mensual", "Servicios Básicos");
        }

        @Test
        @DisplayName("Should find by inmobiliaria and permite recargos with pagination")
        void shouldFindByIdInmobiliariaAndPermiteRecargosWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<ConceptosPago> conceptos = conceptosPagoRepository.findByIdInmobiliariaAndPermiteRecargos(1L, false, pageable);

            // Then
            assertThat(conceptos.getContent()).hasSize(1);
            assertThat(conceptos.getContent().get(0).getNombreConcepto()).isEqualTo("Depósito en Garantía");
        }
    }

    @Nested
    @DisplayName("Existence Checks")
    class ExistenceChecks {

        @Test
        @DisplayName("Should check if concepto exists by inmobiliaria and nombre")
        void shouldCheckIfConceptoExistsByInmobiliariaAndNombre() {
            // When
            boolean exists = conceptosPagoRepository.existsByIdInmobiliariaAndNombreConcepto(1L, "Renta Mensual");
            boolean notExists = conceptosPagoRepository.existsByIdInmobiliariaAndNombreConcepto(1L, "No Existe");

            // Then
            assertThat(exists).isTrue();
            assertThat(notExists).isFalse();
        }

        @Test
        @DisplayName("Should check if concepto exists for different inmobiliaria")
        void shouldCheckIfConceptoExistsForDifferentInmobiliaria() {
            // When
            boolean existsForDifferent = conceptosPagoRepository.existsByIdInmobiliariaAndNombreConceptoAndIdConceptoNot(
                    1L, "Renta Mensual", testConcepto2.getIdConcepto());
            boolean notExistsForSame = conceptosPagoRepository.existsByIdInmobiliariaAndNombreConceptoAndIdConceptoNot(
                    1L, "Renta Mensual", testConcepto1.getIdConcepto());

            // Then
            assertThat(existsForDifferent).isTrue();
            assertThat(notExistsForSame).isFalse();
        }
    }

    @Nested
    @DisplayName("Count Operations")
    class CountOperations {

        @Test
        @DisplayName("Should count active conceptos by inmobiliaria")
        void shouldCountActiveConceptosByInmobiliaria() {
            // When
            long activeCount1 = conceptosPagoRepository.countActiveByInmobiliaria(1L);
            long activeCount2 = conceptosPagoRepository.countActiveByInmobiliaria(2L);
            long activeCount3 = conceptosPagoRepository.countActiveByInmobiliaria(3L);

            // Then
            assertThat(activeCount1).isEqualTo(2);
            assertThat(activeCount2).isEqualTo(1);
            assertThat(activeCount3).isZero();
        }
    }

    @Nested
    @DisplayName("Distinct Values")
    class DistinctValues {

        @Test
        @DisplayName("Should find distinct tipos concepto")
        void shouldFindDistinctTiposConcepto() {
            // When
            List<String> tipos = conceptosPagoRepository.findDistinctTiposConcepto();

            // Then
            assertThat(tipos).hasSize(4);
            assertThat(tipos).containsExactlyInAnyOrder("RENTA", "SERVICIOS", "MANTENIMIENTO", "DEPOSITO");
        }

        @Test
        @DisplayName("Should find distinct tipos concepto by inmobiliaria")
        void shouldFindDistinctTiposConceptoByInmobiliaria() {
            // When
            List<String> tipos1 = conceptosPagoRepository.findDistinctTiposConceptoByInmobiliaria(1L);
            List<String> tipos2 = conceptosPagoRepository.findDistinctTiposConceptoByInmobiliaria(2L);

            // Then
            assertThat(tipos1).hasSize(3);
            assertThat(tipos1).containsExactlyInAnyOrder("RENTA", "SERVICIOS", "DEPOSITO");
            assertThat(tipos2).hasSize(1);
            assertThat(tipos2).containsExactly("MANTENIMIENTO");
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
            Page<ConceptosPago> result = conceptosPagoRepository.findByFilters(
                    1L, "Renta", "RENTA", true, true, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getNombreConcepto()).isEqualTo("Renta Mensual");
        }

        @Test
        @DisplayName("Should find by filters with partial parameters")
        void shouldFindByFiltersWithPartialParameters() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<ConceptosPago> result = conceptosPagoRepository.findByFilters(
                    1L, null, null, true, null, pageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).extracting(ConceptosPago::getNombreConcepto)
                    .containsExactlyInAnyOrder("Renta Mensual", "Servicios Básicos");
        }

        @Test
        @DisplayName("Should find by filters with no parameters (find all)")
        void shouldFindByFiltersWithNoParameters() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<ConceptosPago> result = conceptosPagoRepository.findByFilters(
                    null, null, null, null, null, pageable);

            // Then
            assertThat(result.getContent()).hasSize(4);
        }

        @Test
        @DisplayName("Should find by filters with pagination")
        void shouldFindByFiltersWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 2);

            // When
            Page<ConceptosPago> result = conceptosPagoRepository.findByFilters(
                    null, null, null, null, null, pageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(4);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should handle case insensitive search")
        void shouldHandleCaseInsensitiveSearch() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<ConceptosPago> result = conceptosPagoRepository.findByFilters(
                    1L, "SERVICIOS", "servicios", true, true, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getNombreConcepto()).isEqualTo("Servicios Básicos");
        }

        @Test
        @DisplayName("Should return empty result when no matches")
        void shouldReturnEmptyResultWhenNoMatches() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<ConceptosPago> result = conceptosPagoRepository.findByFilters(
                    1L, "NotFound", null, null, null, pageable);

            // Then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("Data Integrity")
    class DataIntegrity {

        @Test
        @DisplayName("Should auto-generate fecha creacion on persist")
        void shouldAutoGenerateFechaCreacionOnPersist() {
            // Given
            LocalDate beforeSave = LocalDate.now().minusDays(1);
            ConceptosPago newConcepto = createConceptoPago(
                    3L, "Auto Date Test", "Auto Date Description", "TEST", true, true
            );
            newConcepto.setFechaCreacion(null); // Clear the date to test auto-generation

            // When
            ConceptosPago saved = entityManager.persistAndFlush(newConcepto);
            LocalDate afterSave = LocalDate.now().plusDays(1);

            // Then
            assertThat(saved.getFechaCreacion()).isNotNull();
            assertThat(saved.getFechaCreacion()).isAfterOrEqualTo(beforeSave);
            assertThat(saved.getFechaCreacion()).isBeforeOrEqualTo(afterSave);
        }

        @Test
        @DisplayName("Should set default values when not provided")
        void shouldSetDefaultValuesWhenNotProvided() {
            // Given
            ConceptosPago newConcepto = createConceptoPago(
                    3L, "Default Values Test", "Default Values Description", "TEST", null, null
            );
            newConcepto.setPermiteRecargos(null);
            newConcepto.setActivo(null);

            // When
            ConceptosPago saved = entityManager.persistAndFlush(newConcepto);

            // Then
            assertThat(saved.getPermiteRecargos()).isFalse();
            assertThat(saved.getActivo()).isTrue();
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should handle large dataset efficiently")
        void shouldHandleLargeDatasetEfficiently() {
            // Given - Create multiple test records
            for (int i = 1; i <= 100; i++) {
                ConceptosPago concepto = createConceptoPago(
                        (long) (i % 5 + 1), // Distribute across 5 inmobiliarias
                        "Concepto " + i,
                        "Descripción " + i,
                        "TIPO" + (i % 3 + 1),
                        i % 2 == 0,
                        i % 4 != 0
                );
                entityManager.persist(concepto);
            }
            entityManager.flush();

            // When
            long startTime = System.currentTimeMillis();
            Page<ConceptosPago> result = conceptosPagoRepository.findByFilters(
                    null, null, null, true, null, PageRequest.of(0, 20));
            long endTime = System.currentTimeMillis();

            // Then
            assertThat(result.getContent()).hasSizeGreaterThan(0);
            assertThat(endTime - startTime).isLessThan(1000); // Should complete within 1 second
        }
    }
}