package com.inmobiliaria.gestion.conceptos.service;

import com.inmobiliaria.gestion.conceptos.dto.ConceptosPagoCreateRequest;
import com.inmobiliaria.gestion.conceptos.dto.ConceptosPagoDTO;
import com.inmobiliaria.gestion.conceptos.dto.ConceptosPagoUpdateRequest;
import com.inmobiliaria.gestion.conceptos.model.ConceptosPago;
import com.inmobiliaria.gestion.conceptos.repository.ConceptosPagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConceptosPagoService Tests")
class ConceptosPagoServiceTest {

    @Mock
    private ConceptosPagoRepository conceptosPagoRepository;

    @InjectMocks
    private ConceptosPagoService conceptosPagoService;

    private ConceptosPago testConcepto;
    private ConceptosPagoDTO testConceptoDTO;
    private ConceptosPagoCreateRequest testCreateRequest;
    private ConceptosPagoUpdateRequest testUpdateRequest;

    @BeforeEach
    void setUp() {
        testConcepto = createTestConcepto();
        testConceptoDTO = createTestConceptoDTO();
        testCreateRequest = createTestCreateRequest();
        testUpdateRequest = createTestUpdateRequest();
    }

    private ConceptosPago createTestConcepto() {
        ConceptosPago concepto = new ConceptosPago();
        concepto.setIdConcepto(1);
        concepto.setIdInmobiliaria(1L);
        concepto.setNombreConcepto("Renta Mensual");
        concepto.setDescripcion("Pago de renta mensual");
        concepto.setTipoConcepto("RENTA");
        concepto.setPermiteRecargos(true);
        concepto.setActivo(true);
        concepto.setFechaCreacion(LocalDate.now());
        return concepto;
    }

    private ConceptosPagoDTO createTestConceptoDTO() {
        return new ConceptosPagoDTO(
                1,
                1L,
                "Renta Mensual",
                "Pago de renta mensual",
                "RENTA",
                true,
                true,
                LocalDate.now()
        );
    }

    private ConceptosPagoCreateRequest createTestCreateRequest() {
        return new ConceptosPagoCreateRequest(
                1L,
                "Renta Mensual",
                "Pago de renta mensual",
                "RENTA",
                true,
                true
        );
    }

    private ConceptosPagoUpdateRequest createTestUpdateRequest() {
        return new ConceptosPagoUpdateRequest(
                "Renta Mensual Actualizada",
                "Pago de renta mensual actualizada",
                "RENTA",
                true,
                true
        );
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperations {

        @Test
        @DisplayName("Should find all conceptos de pago without pagination")
        void shouldFindAllConceptosPago() {
            // Given
            List<ConceptosPago> conceptos = Arrays.asList(testConcepto);
            when(conceptosPagoRepository.findAll()).thenReturn(conceptos);

            // When
            List<ConceptosPagoDTO> result = conceptosPagoService.findAll();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).nombreConcepto()).isEqualTo("Renta Mensual");
            verify(conceptosPagoRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should find all conceptos de pago with pagination")
        void shouldFindAllConceptosPagoWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<ConceptosPago> page = new PageImpl<>(Arrays.asList(testConcepto));
            when(conceptosPagoRepository.findAll(pageable)).thenReturn(page);

            // When
            Page<ConceptosPagoDTO> result = conceptosPagoService.findAll(pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).nombreConcepto()).isEqualTo("Renta Mensual");
            verify(conceptosPagoRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Should find concepto de pago by ID when exists")
        void shouldFindConceptoPagoById() {
            // Given
            when(conceptosPagoRepository.findById(1)).thenReturn(Optional.of(testConcepto));

            // When
            Optional<ConceptosPagoDTO> result = conceptosPagoService.findById(1);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().idConcepto()).isEqualTo(1);
            assertThat(result.get().nombreConcepto()).isEqualTo("Renta Mensual");
            verify(conceptosPagoRepository, times(1)).findById(1);
        }

        @Test
        @DisplayName("Should return empty when concepto de pago not found by ID")
        void shouldReturnEmptyWhenConceptoPagoNotFoundById() {
            // Given
            when(conceptosPagoRepository.findById(999)).thenReturn(Optional.empty());

            // When
            Optional<ConceptosPagoDTO> result = conceptosPagoService.findById(999);

            // Then
            assertThat(result).isEmpty();
            verify(conceptosPagoRepository, times(1)).findById(999);
        }

        @Test
        @DisplayName("Should find conceptos de pago by inmobiliaria")
        void shouldFindConceptosPagoByInmobiliaria() {
            // Given
            List<ConceptosPago> conceptos = Arrays.asList(testConcepto);
            when(conceptosPagoRepository.findByIdInmobiliaria(1L)).thenReturn(conceptos);

            // When
            List<ConceptosPagoDTO> result = conceptosPagoService.findByInmobiliaria(1L);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).idInmobiliaria()).isEqualTo(1L);
            verify(conceptosPagoRepository, times(1)).findByIdInmobiliaria(1L);
        }

        @Test
        @DisplayName("Should find conceptos de pago by inmobiliaria with pagination")
        void shouldFindConceptosPagoByInmobiliariaWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<ConceptosPago> page = new PageImpl<>(Arrays.asList(testConcepto));
            when(conceptosPagoRepository.findByIdInmobiliaria(1L, pageable)).thenReturn(page);

            // When
            Page<ConceptosPagoDTO> result = conceptosPagoService.findByInmobiliaria(1L, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).idInmobiliaria()).isEqualTo(1L);
            verify(conceptosPagoRepository, times(1)).findByIdInmobiliaria(1L, pageable);
        }

        @Test
        @DisplayName("Should find conceptos de pago by inmobiliaria and activo")
        void shouldFindConceptosPagoByInmobiliariaAndActivo() {
            // Given
            List<ConceptosPago> conceptos = Arrays.asList(testConcepto);
            when(conceptosPagoRepository.findByIdInmobiliariaAndActivo(1L, true)).thenReturn(conceptos);

            // When
            List<ConceptosPagoDTO> result = conceptosPagoService.findByInmobiliariaAndActivo(1L, true);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).idInmobiliaria()).isEqualTo(1L);
            assertThat(result.get(0).activo()).isTrue();
            verify(conceptosPagoRepository, times(1)).findByIdInmobiliariaAndActivo(1L, true);
        }

        @Test
        @DisplayName("Should find conceptos de pago by filters")
        void shouldFindConceptosPagoByFilters() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<ConceptosPago> page = new PageImpl<>(Arrays.asList(testConcepto));
            when(conceptosPagoRepository.findByFilters(1L, "Renta", "RENTA", true, true, pageable)).thenReturn(page);

            // When
            Page<ConceptosPagoDTO> result = conceptosPagoService.findByFilters(1L, "Renta", "RENTA", true, true, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).nombreConcepto()).isEqualTo("Renta Mensual");
            verify(conceptosPagoRepository, times(1)).findByFilters(1L, "Renta", "RENTA", true, true, pageable);
        }
    }

    @Nested
    @DisplayName("Create Operations")
    class CreateOperations {

        @Test
        @DisplayName("Should create new concepto de pago successfully")
        void shouldCreateNewConceptoPago() {
            // Given
            ConceptosPago savedConcepto = createTestConcepto();
            when(conceptosPagoRepository.existsByIdInmobiliariaAndNombreConcepto(1L, "Renta Mensual")).thenReturn(false);
            when(conceptosPagoRepository.save(any(ConceptosPago.class))).thenReturn(savedConcepto);

            // When
            ConceptosPagoDTO result = conceptosPagoService.create(testCreateRequest);

            // Then
            assertThat(result.nombreConcepto()).isEqualTo("Renta Mensual");
            assertThat(result.idInmobiliaria()).isEqualTo(1L);
            assertThat(result.activo()).isTrue();
            verify(conceptosPagoRepository, times(1)).existsByIdInmobiliariaAndNombreConcepto(1L, "Renta Mensual");
            verify(conceptosPagoRepository, times(1)).save(any(ConceptosPago.class));
        }

        @Test
        @DisplayName("Should throw exception when concepto name already exists")
        void shouldThrowExceptionWhenConceptoNameAlreadyExists() {
            // Given
            when(conceptosPagoRepository.existsByIdInmobiliariaAndNombreConcepto(1L, "Renta Mensual")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> conceptosPagoService.create(testCreateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Concept name already exists for this inmobiliaria");

            verify(conceptosPagoRepository, times(1)).existsByIdInmobiliariaAndNombreConcepto(1L, "Renta Mensual");
            verify(conceptosPagoRepository, never()).save(any(ConceptosPago.class));
        }

        @Test
        @DisplayName("Should set default values when creating concepto")
        void shouldSetDefaultValuesWhenCreatingConcepto() {
            // Given
            ConceptosPagoCreateRequest requestWithNulls = new ConceptosPagoCreateRequest(
                    1L, "Test Concepto", "Test Description", "TEST", null, null
            );
            ConceptosPago savedConcepto = createTestConcepto();
            savedConcepto.setPermiteRecargos(false);
            savedConcepto.setActivo(true);

            when(conceptosPagoRepository.existsByIdInmobiliariaAndNombreConcepto(anyLong(), anyString())).thenReturn(false);
            when(conceptosPagoRepository.save(any(ConceptosPago.class))).thenReturn(savedConcepto);

            // When
            ConceptosPagoDTO result = conceptosPagoService.create(requestWithNulls);

            // Then
            assertThat(result.permiteRecargos()).isFalse();
            assertThat(result.activo()).isTrue();
            verify(conceptosPagoRepository, times(1)).save(any(ConceptosPago.class));
        }
    }

    @Nested
    @DisplayName("Update Operations")
    class UpdateOperations {

        @Test
        @DisplayName("Should update concepto de pago successfully")
        void shouldUpdateConceptoPago() {
            // Given
            ConceptosPago existingConcepto = createTestConcepto();
            ConceptosPago updatedConcepto = createTestConcepto();
            updatedConcepto.setNombreConcepto("Renta Mensual Actualizada");

            when(conceptosPagoRepository.findById(1)).thenReturn(Optional.of(existingConcepto));
            when(conceptosPagoRepository.existsByIdInmobiliariaAndNombreConceptoAndIdConceptoNot(1L, "Renta Mensual Actualizada", 1)).thenReturn(false);
            when(conceptosPagoRepository.save(any(ConceptosPago.class))).thenReturn(updatedConcepto);

            // When
            ConceptosPagoDTO result = conceptosPagoService.update(1, testUpdateRequest);

            // Then
            assertThat(result.nombreConcepto()).isEqualTo("Renta Mensual Actualizada");
            verify(conceptosPagoRepository, times(1)).findById(1);
            verify(conceptosPagoRepository, times(1)).save(any(ConceptosPago.class));
        }

        @Test
        @DisplayName("Should throw exception when concepto not found for update")
        void shouldThrowExceptionWhenConceptoNotFoundForUpdate() {
            // Given
            when(conceptosPagoRepository.findById(999)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> conceptosPagoService.update(999, testUpdateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Concepto de pago not found with id: 999");

            verify(conceptosPagoRepository, times(1)).findById(999);
            verify(conceptosPagoRepository, never()).save(any(ConceptosPago.class));
        }

        @Test
        @DisplayName("Should throw exception when updated name already exists")
        void shouldThrowExceptionWhenUpdatedNameAlreadyExists() {
            // Given
            ConceptosPago existingConcepto = createTestConcepto();
            when(conceptosPagoRepository.findById(1)).thenReturn(Optional.of(existingConcepto));
            when(conceptosPagoRepository.existsByIdInmobiliariaAndNombreConceptoAndIdConceptoNot(1L, "Renta Mensual Actualizada", 1)).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> conceptosPagoService.update(1, testUpdateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Concept name already exists for this inmobiliaria");

            verify(conceptosPagoRepository, times(1)).findById(1);
            verify(conceptosPagoRepository, never()).save(any(ConceptosPago.class));
        }

        @Test
        @DisplayName("Should set default values when updating concepto")
        void shouldSetDefaultValuesWhenUpdatingConcepto() {
            // Given
            ConceptosPago existingConcepto = createTestConcepto();
            ConceptosPagoUpdateRequest requestWithNulls = new ConceptosPagoUpdateRequest(
                    "Updated Name", "Updated Description", "UPDATED", null, null
            );

            when(conceptosPagoRepository.findById(1)).thenReturn(Optional.of(existingConcepto));
            when(conceptosPagoRepository.existsByIdInmobiliariaAndNombreConceptoAndIdConceptoNot(anyLong(), anyString(), anyInt())).thenReturn(false);
            when(conceptosPagoRepository.save(any(ConceptosPago.class))).thenReturn(existingConcepto);

            // When
            ConceptosPagoDTO result = conceptosPagoService.update(1, requestWithNulls);

            // Then
            verify(conceptosPagoRepository, times(1)).save(any(ConceptosPago.class));
        }
    }

    @Nested
    @DisplayName("Conditional Logic and Branch Coverage Tests")
    class ConditionalLogicTests {

        @Test
        @DisplayName("Should apply default values when fields are null in create")
        void shouldApplyDefaultValuesWhenFieldsAreNullInCreate() {
            // Given - Request without optional fields
            ConceptosPagoCreateRequest request = new ConceptosPagoCreateRequest(
                    1L,
                    "Test Concepto",
                    "Test Description",
                    "TEST",
                    null, // permiteRecargos null
                    null  // activo null
            );

            ConceptosPago conceptoToSave = new ConceptosPago();
            conceptoToSave.setIdInmobiliaria(1L);
            conceptoToSave.setNombreConcepto("Test Concepto");
            conceptoToSave.setDescripcion("Test Description");
            conceptoToSave.setTipoConcepto("TEST");
            // fechaCreacion will be null initially

            ConceptosPago savedConcepto = new ConceptosPago();
            savedConcepto.setIdConcepto(1);
            savedConcepto.setIdInmobiliaria(1L);
            savedConcepto.setNombreConcepto("Test Concepto");
            savedConcepto.setDescripcion("Test Description");
            savedConcepto.setTipoConcepto("TEST");
            savedConcepto.setPermiteRecargos(false); // default value applied
            savedConcepto.setActivo(true); // default value applied
            savedConcepto.setFechaCreacion(LocalDate.now()); // default value applied

            when(conceptosPagoRepository.existsByIdInmobiliariaAndNombreConcepto(1L, "Test Concepto"))
                    .thenReturn(false);
            when(conceptosPagoRepository.save(any(ConceptosPago.class))).thenReturn(savedConcepto);

            // When
            ConceptosPagoDTO result = conceptosPagoService.create(request);

            // Then - Verify default values were applied
            assertThat(result).isNotNull();
            assertThat(result.permiteRecargos()).isFalse(); // default applied
            assertThat(result.activo()).isTrue(); // default applied
            assertThat(result.fechaCreacion()).isEqualTo(LocalDate.now()); // default applied
        }

        @Test
        @DisplayName("Should preserve provided values when fields are not null in create")
        void shouldPreserveProvidedValuesWhenFieldsAreNotNullInCreate() {
            // Given - Request with all optional fields provided
            ConceptosPagoCreateRequest request = new ConceptosPagoCreateRequest(
                    1L,
                    "Test Concepto",
                    "Test Description",
                    "TEST",
                    true, // permiteRecargos provided
                    false // activo provided
            );

            ConceptosPago savedConcepto = new ConceptosPago();
            savedConcepto.setIdConcepto(1);
            savedConcepto.setIdInmobiliaria(1L);
            savedConcepto.setNombreConcepto("Test Concepto");
            savedConcepto.setDescripcion("Test Description");
            savedConcepto.setTipoConcepto("TEST");
            savedConcepto.setPermiteRecargos(true); // preserved value
            savedConcepto.setActivo(false); // preserved value
            savedConcepto.setFechaCreacion(LocalDate.now());

            when(conceptosPagoRepository.existsByIdInmobiliariaAndNombreConcepto(1L, "Test Concepto"))
                    .thenReturn(false);
            when(conceptosPagoRepository.save(any(ConceptosPago.class))).thenReturn(savedConcepto);

            // When
            ConceptosPagoDTO result = conceptosPagoService.create(request);

            // Then - Verify provided values were preserved
            assertThat(result).isNotNull();
            assertThat(result.permiteRecargos()).isTrue(); // preserved
            assertThat(result.activo()).isFalse(); // preserved
        }

        @Test
        @DisplayName("Should apply default values when fields are null in update")
        void shouldApplyDefaultValuesWhenFieldsAreNullInUpdate() {
            // Given - Update request with null optional fields
            ConceptosPagoUpdateRequest request = new ConceptosPagoUpdateRequest(
                    "Updated Name",
                    "Updated Description",
                    "UPDATED",
                    null, // permiteRecargos null
                    null  // activo null
            );

            when(conceptosPagoRepository.findById(1)).thenReturn(Optional.of(testConcepto));
            when(conceptosPagoRepository.existsByIdInmobiliariaAndNombreConceptoAndIdConceptoNot(
                    1L, "Updated Name", 1)).thenReturn(false);

            ConceptosPago updatedConcepto = new ConceptosPago();
            updatedConcepto.setIdConcepto(1);
            updatedConcepto.setIdInmobiliaria(1L);
            updatedConcepto.setNombreConcepto("Updated Name");
            updatedConcepto.setDescripcion("Updated Description");
            updatedConcepto.setTipoConcepto("UPDATED");
            updatedConcepto.setPermiteRecargos(false); // default applied
            updatedConcepto.setActivo(true); // default applied

            when(conceptosPagoRepository.save(any(ConceptosPago.class))).thenReturn(updatedConcepto);

            // When
            ConceptosPagoDTO result = conceptosPagoService.update(1, request);

            // Then - Verify default values were applied
            assertThat(result).isNotNull();
            assertThat(result.permiteRecargos()).isFalse(); // default applied
            assertThat(result.activo()).isTrue(); // default applied
        }

        @Test
        @DisplayName("Should preserve provided values when fields are not null in update")
        void shouldPreserveProvidedValuesWhenFieldsAreNotNullInUpdate() {
            // Given - Update request with provided optional fields
            ConceptosPagoUpdateRequest request = new ConceptosPagoUpdateRequest(
                    "Updated Name",
                    "Updated Description",
                    "UPDATED",
                    true, // permiteRecargos provided
                    false // activo provided
            );

            when(conceptosPagoRepository.findById(1)).thenReturn(Optional.of(testConcepto));
            when(conceptosPagoRepository.existsByIdInmobiliariaAndNombreConceptoAndIdConceptoNot(
                    1L, "Updated Name", 1)).thenReturn(false);

            ConceptosPago updatedConcepto = new ConceptosPago();
            updatedConcepto.setIdConcepto(1);
            updatedConcepto.setIdInmobiliaria(1L);
            updatedConcepto.setNombreConcepto("Updated Name");
            updatedConcepto.setDescripcion("Updated Description");
            updatedConcepto.setTipoConcepto("UPDATED");
            updatedConcepto.setPermiteRecargos(true); // preserved
            updatedConcepto.setActivo(false); // preserved

            when(conceptosPagoRepository.save(any(ConceptosPago.class))).thenReturn(updatedConcepto);

            // When
            ConceptosPagoDTO result = conceptosPagoService.update(1, request);

            // Then - Verify provided values were preserved
            assertThat(result).isNotNull();
            assertThat(result.permiteRecargos()).isTrue(); // preserved
            assertThat(result.activo()).isFalse(); // preserved
        }

        @Test
        @DisplayName("Should throw exception when duplicate name in create")
        void shouldThrowExceptionWhenDuplicateNameInCreate() {
            // Given - Request with duplicate name
            ConceptosPagoCreateRequest request = new ConceptosPagoCreateRequest(
                    1L, "Duplicate Name", "Description", "TEST", true, true
            );

            when(conceptosPagoRepository.existsByIdInmobiliariaAndNombreConcepto(1L, "Duplicate Name"))
                    .thenReturn(true);

            // When & Then - Should throw exception
            assertThatThrownBy(() -> conceptosPagoService.create(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Concept name already exists for this inmobiliaria: Duplicate Name");

            verify(conceptosPagoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when duplicate name in update")
        void shouldThrowExceptionWhenDuplicateNameInUpdate() {
            // Given - Update request with duplicate name
            ConceptosPagoUpdateRequest request = new ConceptosPagoUpdateRequest(
                    "Duplicate Name", "Description", "TEST", true, true
            );

            when(conceptosPagoRepository.findById(1)).thenReturn(Optional.of(testConcepto));
            when(conceptosPagoRepository.existsByIdInmobiliariaAndNombreConceptoAndIdConceptoNot(
                    1L, "Duplicate Name", 1)).thenReturn(true);

            // When & Then - Should throw exception
            assertThatThrownBy(() -> conceptosPagoService.update(1, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Concept name already exists for this inmobiliaria: Duplicate Name");

            verify(conceptosPagoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should not throw exception when same name but different inmobiliaria in create")
        void shouldNotThrowExceptionWhenSameNameButDifferentInmobiliariaInCreate() {
            // Given - Same name but different inmobiliaria should be allowed
            ConceptosPagoCreateRequest request = new ConceptosPagoCreateRequest(
                    2L, "Same Name", "Description", "TEST", true, true
            );

            when(conceptosPagoRepository.existsByIdInmobiliariaAndNombreConcepto(2L, "Same Name"))
                    .thenReturn(false);

            ConceptosPago savedConcepto = new ConceptosPago();
            savedConcepto.setIdConcepto(1);
            savedConcepto.setIdInmobiliaria(2L);
            savedConcepto.setNombreConcepto("Same Name");
            savedConcepto.setDescripcion("Description");
            savedConcepto.setTipoConcepto("TEST");
            savedConcepto.setPermiteRecargos(true);
            savedConcepto.setActivo(true);
            savedConcepto.setFechaCreacion(LocalDate.now());

            when(conceptosPagoRepository.save(any(ConceptosPago.class))).thenReturn(savedConcepto);

            // When
            ConceptosPagoDTO result = conceptosPagoService.create(request);

            // Then - Should create successfully
            assertThat(result).isNotNull();
            assertThat(result.idInmobiliaria()).isEqualTo(2L);
            assertThat(result.nombreConcepto()).isEqualTo("Same Name");
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperations {

        @Test
        @DisplayName("Should delete concepto de pago successfully")
        void shouldDeleteConceptoPago() {
            // Given
            when(conceptosPagoRepository.existsById(1)).thenReturn(true);
            doNothing().when(conceptosPagoRepository).deleteById(1);

            // When
            conceptosPagoService.deleteById(1);

            // Then
            verify(conceptosPagoRepository, times(1)).existsById(1);
            verify(conceptosPagoRepository, times(1)).deleteById(1);
        }

        @Test
        @DisplayName("Should throw exception when concepto not found for deletion")
        void shouldThrowExceptionWhenConceptoNotFoundForDeletion() {
            // Given
            when(conceptosPagoRepository.existsById(999)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> conceptosPagoService.deleteById(999))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Concepto de pago not found with id: 999");

            verify(conceptosPagoRepository, times(1)).existsById(999);
            verify(conceptosPagoRepository, never()).deleteById(999);
        }
    }

    @Nested
    @DisplayName("Status Operations")
    class StatusOperations {

        @Test
        @DisplayName("Should change concepto status successfully")
        void shouldChangeConceptoStatus() {
            // Given
            ConceptosPago existingConcepto = createTestConcepto();
            ConceptosPago updatedConcepto = createTestConcepto();
            updatedConcepto.setActivo(false);

            when(conceptosPagoRepository.findById(1)).thenReturn(Optional.of(existingConcepto));
            when(conceptosPagoRepository.save(any(ConceptosPago.class))).thenReturn(updatedConcepto);

            // When
            ConceptosPagoDTO result = conceptosPagoService.changeStatus(1, false);

            // Then
            assertThat(result.activo()).isFalse();
            verify(conceptosPagoRepository, times(1)).findById(1);
            verify(conceptosPagoRepository, times(1)).save(any(ConceptosPago.class));
        }

        @Test
        @DisplayName("Should throw exception when concepto not found for status change")
        void shouldThrowExceptionWhenConceptoNotFoundForStatusChange() {
            // Given
            when(conceptosPagoRepository.findById(999)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> conceptosPagoService.changeStatus(999, false))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Concepto de pago not found with id: 999");

            verify(conceptosPagoRepository, times(1)).findById(999);
            verify(conceptosPagoRepository, never()).save(any(ConceptosPago.class));
        }
    }

    @Nested
    @DisplayName("Count and Statistics Operations")
    class CountAndStatisticsOperations {

        @Test
        @DisplayName("Should count active conceptos by inmobiliaria")
        void shouldCountActiveConceptosByInmobiliaria() {
            // Given
            when(conceptosPagoRepository.countActiveByInmobiliaria(1L)).thenReturn(5L);

            // When
            long result = conceptosPagoService.countActiveByInmobiliaria(1L);

            // Then
            assertThat(result).isEqualTo(5L);
            verify(conceptosPagoRepository, times(1)).countActiveByInmobiliaria(1L);
        }

        @Test
        @DisplayName("Should get distinct tipos concepto")
        void shouldGetDistinctTiposConcepto() {
            // Given
            List<String> tipos = Arrays.asList("RENTA", "SERVICIOS", "MANTENIMIENTO");
            when(conceptosPagoRepository.findDistinctTiposConcepto()).thenReturn(tipos);

            // When
            List<String> result = conceptosPagoService.getDistinctTiposConcepto();

            // Then
            assertThat(result).hasSize(3);
            assertThat(result).containsExactlyInAnyOrder("RENTA", "SERVICIOS", "MANTENIMIENTO");
            verify(conceptosPagoRepository, times(1)).findDistinctTiposConcepto();
        }

        @Test
        @DisplayName("Should get distinct tipos concepto by inmobiliaria")
        void shouldGetDistinctTiposConceptoByInmobiliaria() {
            // Given
            List<String> tipos = Arrays.asList("RENTA", "SERVICIOS");
            when(conceptosPagoRepository.findDistinctTiposConceptoByInmobiliaria(1L)).thenReturn(tipos);

            // When
            List<String> result = conceptosPagoService.getDistinctTiposConceptoByInmobiliaria(1L);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder("RENTA", "SERVICIOS");
            verify(conceptosPagoRepository, times(1)).findDistinctTiposConceptoByInmobiliaria(1L);
        }
    }

    @Nested
    @DisplayName("Existence Checks")
    class ExistenceChecks {

        @Test
        @DisplayName("Should check if concepto exists by ID")
        void shouldCheckIfConceptoExistsById() {
            // Given
            when(conceptosPagoRepository.existsById(1)).thenReturn(true);

            // When
            boolean result = conceptosPagoService.existsById(1);

            // Then
            assertThat(result).isTrue();
            verify(conceptosPagoRepository, times(1)).existsById(1);
        }

        @Test
        @DisplayName("Should check if concepto exists by inmobiliaria and nombre")
        void shouldCheckIfConceptoExistsByInmobiliariaAndNombre() {
            // Given
            when(conceptosPagoRepository.existsByIdInmobiliariaAndNombreConcepto(1L, "Renta Mensual")).thenReturn(true);

            // When
            boolean result = conceptosPagoService.existsByInmobiliariaAndNombreConcepto(1L, "Renta Mensual");

            // Then
            assertThat(result).isTrue();
            verify(conceptosPagoRepository, times(1)).existsByIdInmobiliariaAndNombreConcepto(1L, "Renta Mensual");
        }
    }

    @Nested
    @DisplayName("Additional Find Operations")
    class AdditionalFindOperations {

        @Test
        @DisplayName("Should find conceptos by tipo concepto")
        void shouldFindConceptosByTipoConcepto() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<ConceptosPago> page = new PageImpl<>(Arrays.asList(testConcepto));
            when(conceptosPagoRepository.findByTipoConcepto("RENTA", pageable)).thenReturn(page);

            // When
            Page<ConceptosPagoDTO> result = conceptosPagoService.findByTipoConcepto("RENTA", pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).tipoConcepto()).isEqualTo("RENTA");
            verify(conceptosPagoRepository, times(1)).findByTipoConcepto("RENTA", pageable);
        }

        @Test
        @DisplayName("Should find conceptos by nombre concepto")
        void shouldFindConceptosByNombreConcepto() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<ConceptosPago> page = new PageImpl<>(Arrays.asList(testConcepto));
            when(conceptosPagoRepository.findByNombreConceptoContainingIgnoreCase("Renta", pageable)).thenReturn(page);

            // When
            Page<ConceptosPagoDTO> result = conceptosPagoService.findByNombreConcepto("Renta", pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).nombreConcepto()).contains("Renta");
            verify(conceptosPagoRepository, times(1)).findByNombreConceptoContainingIgnoreCase("Renta", pageable);
        }

        @Test
        @DisplayName("Should find conceptos by permite recargos")
        void shouldFindConceptosByPermiteRecargos() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<ConceptosPago> page = new PageImpl<>(Arrays.asList(testConcepto));
            when(conceptosPagoRepository.findByPermiteRecargos(true, pageable)).thenReturn(page);

            // When
            Page<ConceptosPagoDTO> result = conceptosPagoService.findByPermiteRecargos(true, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).permiteRecargos()).isTrue();
            verify(conceptosPagoRepository, times(1)).findByPermiteRecargos(true, pageable);
        }
    }
}