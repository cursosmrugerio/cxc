package com.inmobiliaria.gestion.conceptos.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConceptosPago Entity Tests")
class ConceptosPagoEntityTest {

    private ConceptosPago conceptoPago;

    @BeforeEach
    void setUp() {
        conceptoPago = new ConceptosPago();
        conceptoPago.setIdInmobiliaria(1L);
        conceptoPago.setNombreConcepto("Test Concepto");
        conceptoPago.setDescripcion("Test Description");
        conceptoPago.setTipoConcepto("TEST");
        conceptoPago.setPermiteRecargos(true);
        conceptoPago.setActivo(true);
    }

    @Nested
    @DisplayName("JPA Lifecycle Callback Tests")
    class JpaLifecycleCallbackTests {

        @Test
        @DisplayName("Should set creation date when fechaCreacion is null in onCreate")
        void shouldSetCreationDateWhenFechaCreacionIsNullInOnCreate() {
            // Given - ConceptosPago without fechaCreacion
            conceptoPago.setFechaCreacion(null);
            LocalDate beforeCallback = LocalDate.now();

            // When - Trigger the @PrePersist callback
            conceptoPago.onCreate();

            // Then - fechaCreacion should be set to current date
            assertThat(conceptoPago.getFechaCreacion()).isNotNull();
            assertThat(conceptoPago.getFechaCreacion()).isEqualTo(beforeCallback);
        }

        @Test
        @DisplayName("Should not override existing fechaCreacion in onCreate")
        void shouldNotOverrideExistingFechaCreacionInOnCreate() {
            // Given - ConceptosPago with existing fechaCreacion
            LocalDate existingDate = LocalDate.of(2023, 1, 1);
            conceptoPago.setFechaCreacion(existingDate);

            // When - Trigger the @PrePersist callback
            conceptoPago.onCreate();

            // Then - fechaCreacion should remain unchanged
            assertThat(conceptoPago.getFechaCreacion()).isEqualTo(existingDate);
        }

        @Test
        @DisplayName("Should handle onCreate with different date scenarios")
        void shouldHandleOnCreateWithDifferentDateScenarios() {
            // Test 1: Future date should be preserved
            LocalDate futureDate = LocalDate.now().plusDays(30);
            conceptoPago.setFechaCreacion(futureDate);
            conceptoPago.onCreate();
            assertThat(conceptoPago.getFechaCreacion()).isEqualTo(futureDate);

            // Test 2: Past date should be preserved
            LocalDate pastDate = LocalDate.now().minusDays(30);
            conceptoPago.setFechaCreacion(pastDate);
            conceptoPago.onCreate();
            assertThat(conceptoPago.getFechaCreacion()).isEqualTo(pastDate);

            // Test 3: Today's date should be preserved
            LocalDate today = LocalDate.now();
            conceptoPago.setFechaCreacion(today);
            conceptoPago.onCreate();
            assertThat(conceptoPago.getFechaCreacion()).isEqualTo(today);
        }

        @Test
        @DisplayName("Should handle onCreate multiple times without changing existing date")
        void shouldHandleOnCreateMultipleTimesWithoutChangingExistingDate() {
            // Given - Set initial date
            LocalDate initialDate = LocalDate.of(2023, 6, 15);
            conceptoPago.setFechaCreacion(initialDate);

            // When - Call onCreate multiple times
            conceptoPago.onCreate();
            conceptoPago.onCreate();
            conceptoPago.onCreate();

            // Then - Date should remain the same
            assertThat(conceptoPago.getFechaCreacion()).isEqualTo(initialDate);
        }
    }

    @Nested
    @DisplayName("Entity State Tests")
    class EntityStateTests {

        @Test
        @DisplayName("Should create entity with all fields set correctly")
        void shouldCreateEntityWithAllFieldsSetCorrectly() {
            // Given - Entity with all fields
            ConceptosPago concepto = new ConceptosPago();
            concepto.setIdConcepto(1);
            concepto.setIdInmobiliaria(1L);
            concepto.setNombreConcepto("Renta Mensual");
            concepto.setDescripcion("Pago de renta mensual");
            concepto.setTipoConcepto("RENTA");
            concepto.setPermiteRecargos(true);
            concepto.setActivo(true);
            concepto.setFechaCreacion(LocalDate.now());

            // Then - All fields should be set correctly
            assertThat(concepto.getIdConcepto()).isEqualTo(1);
            assertThat(concepto.getIdInmobiliaria()).isEqualTo(1L);
            assertThat(concepto.getNombreConcepto()).isEqualTo("Renta Mensual");
            assertThat(concepto.getDescripcion()).isEqualTo("Pago de renta mensual");
            assertThat(concepto.getTipoConcepto()).isEqualTo("RENTA");
            assertThat(concepto.getPermiteRecargos()).isTrue();
            assertThat(concepto.getActivo()).isTrue();
            assertThat(concepto.getFechaCreacion()).isEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("Should handle null values appropriately")
        void shouldHandleNullValuesAppropriately() {
            // Given - Entity with some null values
            ConceptosPago concepto = new ConceptosPago();
            concepto.setIdInmobiliaria(null);
            concepto.setNombreConcepto(null);
            concepto.setDescripcion(null);
            concepto.setTipoConcepto(null);
            concepto.setPermiteRecargos(null);
            concepto.setActivo(null);
            concepto.setFechaCreacion(null);

            // Then - Should handle nulls without throwing exceptions
            assertThat(concepto.getIdInmobiliaria()).isNull();
            assertThat(concepto.getNombreConcepto()).isNull();
            assertThat(concepto.getDescripcion()).isNull();
            assertThat(concepto.getTipoConcepto()).isNull();
            assertThat(concepto.getPermiteRecargos()).isNull();
            assertThat(concepto.getActivo()).isNull();
            assertThat(concepto.getFechaCreacion()).isNull();
        }

        @Test
        @DisplayName("Should handle boolean field state changes")
        void shouldHandleBooleanFieldStateChanges() {
            // Given - Initial state
            conceptoPago.setPermiteRecargos(true);
            conceptoPago.setActivo(true);

            // When - Change boolean states
            conceptoPago.setPermiteRecargos(false);
            conceptoPago.setActivo(false);

            // Then - Should reflect changes
            assertThat(conceptoPago.getPermiteRecargos()).isFalse();
            assertThat(conceptoPago.getActivo()).isFalse();

            // When - Change back
            conceptoPago.setPermiteRecargos(true);
            conceptoPago.setActivo(true);

            // Then - Should reflect changes again
            assertThat(conceptoPago.getPermiteRecargos()).isTrue();
            assertThat(conceptoPago.getActivo()).isTrue();
        }

        @Test
        @DisplayName("Should handle edge case values for string fields")
        void shouldHandleEdgeCaseValuesForStringFields() {
            // Test empty strings
            conceptoPago.setNombreConcepto("");
            conceptoPago.setDescripcion("");
            conceptoPago.setTipoConcepto("");

            assertThat(conceptoPago.getNombreConcepto()).isEmpty();
            assertThat(conceptoPago.getDescripcion()).isEmpty();
            assertThat(conceptoPago.getTipoConcepto()).isEmpty();

            // Test very long strings
            String longString = "A".repeat(1000);
            conceptoPago.setNombreConcepto(longString);
            conceptoPago.setDescripcion(longString);
            conceptoPago.setTipoConcepto(longString);

            assertThat(conceptoPago.getNombreConcepto()).hasSize(1000);
            assertThat(conceptoPago.getDescripcion()).hasSize(1000);
            assertThat(conceptoPago.getTipoConcepto()).hasSize(1000);

            // Test special characters
            String specialChars = "!@#$%^&*()_+{}|:<>?[];',./";
            conceptoPago.setNombreConcepto(specialChars);
            conceptoPago.setDescripcion(specialChars);
            conceptoPago.setTipoConcepto(specialChars);

            assertThat(conceptoPago.getNombreConcepto()).isEqualTo(specialChars);
            assertThat(conceptoPago.getDescripcion()).isEqualTo(specialChars);
            assertThat(conceptoPago.getTipoConcepto()).isEqualTo(specialChars);
        }
    }

    @Nested
    @DisplayName("Entity Equality and Consistency Tests")
    class EntityEqualityTests {

        @Test
        @DisplayName("Should maintain entity consistency after onCreate callback")
        void shouldMaintainEntityConsistencyAfterOnCreateCallback() {
            // Given - Entity in known state
            String originalNombre = conceptoPago.getNombreConcepto();
            String originalDescripcion = conceptoPago.getDescripcion();
            String originalTipo = conceptoPago.getTipoConcepto();
            Long originalInmobiliaria = conceptoPago.getIdInmobiliaria();
            Boolean originalPermiteRecargos = conceptoPago.getPermiteRecargos();
            Boolean originalActivo = conceptoPago.getActivo();

            // When - Call onCreate
            conceptoPago.onCreate();

            // Then - Only fechaCreacion should change (if it was null), other fields remain same
            assertThat(conceptoPago.getNombreConcepto()).isEqualTo(originalNombre);
            assertThat(conceptoPago.getDescripcion()).isEqualTo(originalDescripcion);
            assertThat(conceptoPago.getTipoConcepto()).isEqualTo(originalTipo);
            assertThat(conceptoPago.getIdInmobiliaria()).isEqualTo(originalInmobiliaria);
            assertThat(conceptoPago.getPermiteRecargos()).isEqualTo(originalPermiteRecargos);
            assertThat(conceptoPago.getActivo()).isEqualTo(originalActivo);
        }

        @Test
        @DisplayName("Should handle default constructor properly")
        void shouldHandleDefaultConstructorProperly() {
            // When - Create with default constructor
            ConceptosPago newConcepto = new ConceptosPago();

            // Then - Should have expected default state (some fields have @Builder.Default values)
            assertThat(newConcepto.getIdConcepto()).isNull();
            assertThat(newConcepto.getIdInmobiliaria()).isNull();
            assertThat(newConcepto.getNombreConcepto()).isNull();
            assertThat(newConcepto.getDescripcion()).isNull();
            assertThat(newConcepto.getTipoConcepto()).isNull();
            assertThat(newConcepto.getPermiteRecargos()).isFalse(); // Has @Builder.Default = false
            assertThat(newConcepto.getActivo()).isTrue(); // Has @Builder.Default = true
            assertThat(newConcepto.getFechaCreacion()).isNull();
        }
    }
}