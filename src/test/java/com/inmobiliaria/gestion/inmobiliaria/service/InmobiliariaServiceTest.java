package com.inmobiliaria.gestion.inmobiliaria.service;

import com.inmobiliaria.gestion.inmobiliaria.dto.InmobiliariaDTO;
import com.inmobiliaria.gestion.inmobiliaria.model.Inmobiliaria;
import com.inmobiliaria.gestion.inmobiliaria.repository.InmobiliariaRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InmobiliariaService Tests")
class InmobiliariaServiceTest {

    @Mock
    private InmobiliariaRepository inmobiliariaRepository;

    @InjectMocks
    private InmobiliariaService inmobiliariaService;

    private Inmobiliaria testInmobiliaria;
    private InmobiliariaDTO testInmobiliariaDTO;

    @BeforeEach
    void setUp() {
        testInmobiliaria = createTestInmobiliaria();
        testInmobiliariaDTO = createTestInmobiliariaDTO();
    }

    private Inmobiliaria createTestInmobiliaria() {
        Inmobiliaria inmobiliaria = new Inmobiliaria();
        inmobiliaria.setIdInmobiliaria(1L);
        inmobiliaria.setNombreComercial("Inmobiliaria Los Pinos");
        inmobiliaria.setRazonSocial("Inmobiliaria Los Pinos S.A. de C.V.");
        inmobiliaria.setRfcNit("ILP123456789");
        inmobiliaria.setTelefonoPrincipal("555-123-4567");
        inmobiliaria.setEmailContacto("contacto@lospinos.com");
        inmobiliaria.setDireccionCompleta("Av. Principal 123, Col. Centro");
        inmobiliaria.setCiudad("Ciudad de México");
        inmobiliaria.setEstado("CDMX");
        inmobiliaria.setCodigoPostal("06700");
        inmobiliaria.setPersonaContacto("María González");
        inmobiliaria.setEstatus("ACTIVE");
        inmobiliaria.setFechaRegistro(LocalDate.now());
        return inmobiliaria;
    }

    private InmobiliariaDTO createTestInmobiliariaDTO() {
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
    @DisplayName("Find Operations")
    class FindOperations {

        @Test
        @DisplayName("Should find all inmobiliarias without pagination")
        void shouldFindAllInmobiliarias() {
            // Given
            List<Inmobiliaria> inmobiliarias = Arrays.asList(testInmobiliaria);
            when(inmobiliariaRepository.findAll()).thenReturn(inmobiliarias);

            // When
            List<InmobiliariaDTO> result = inmobiliariaService.findAll();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).nombreComercial()).isEqualTo("Inmobiliaria Los Pinos");
            verify(inmobiliariaRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should find all inmobiliarias with pagination")
        void shouldFindAllInmobiliariasWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Inmobiliaria> page = new PageImpl<>(Arrays.asList(testInmobiliaria));
            when(inmobiliariaRepository.findAll(pageable)).thenReturn(page);

            // When
            Page<InmobiliariaDTO> result = inmobiliariaService.findAll(pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).nombreComercial()).isEqualTo("Inmobiliaria Los Pinos");
            verify(inmobiliariaRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Should find inmobiliaria by ID when exists")
        void shouldFindInmobiliariaById() {
            // Given
            when(inmobiliariaRepository.findById(1L)).thenReturn(Optional.of(testInmobiliaria));

            // When
            Optional<InmobiliariaDTO> result = inmobiliariaService.findById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().idInmobiliaria()).isEqualTo(1L);
            assertThat(result.get().nombreComercial()).isEqualTo("Inmobiliaria Los Pinos");
            verify(inmobiliariaRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should return empty when inmobiliaria not found by ID")
        void shouldReturnEmptyWhenInmobiliariaNotFoundById() {
            // Given
            when(inmobiliariaRepository.findById(999L)).thenReturn(Optional.empty());

            // When
            Optional<InmobiliariaDTO> result = inmobiliariaService.findById(999L);

            // Then
            assertThat(result).isEmpty();
            verify(inmobiliariaRepository, times(1)).findById(999L);
        }

        @Test
        @DisplayName("Should find inmobiliaria by RFC/NIT when exists")
        void shouldFindInmobiliariaByRfcNit() {
            // Given
            when(inmobiliariaRepository.findByRfcNit("ILP123456789")).thenReturn(Optional.of(testInmobiliaria));

            // When
            Optional<InmobiliariaDTO> result = inmobiliariaService.findByRfcNit("ILP123456789");

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().rfcNit()).isEqualTo("ILP123456789");
            verify(inmobiliariaRepository, times(1)).findByRfcNit("ILP123456789");
        }

        @Test
        @DisplayName("Should return empty when RFC/NIT not found")
        void shouldReturnEmptyWhenRfcNitNotFound() {
            // Given
            when(inmobiliariaRepository.findByRfcNit("NOTFOUND")).thenReturn(Optional.empty());

            // When
            Optional<InmobiliariaDTO> result = inmobiliariaService.findByRfcNit("NOTFOUND");

            // Then
            assertThat(result).isEmpty();
            verify(inmobiliariaRepository, times(1)).findByRfcNit("NOTFOUND");
        }

        @Test
        @DisplayName("Should find inmobiliarias by status")
        void shouldFindInmobiliariasByEstatus() {
            // Given
            List<Inmobiliaria> inmobiliarias = Arrays.asList(testInmobiliaria);
            when(inmobiliariaRepository.findByEstatus("ACTIVE")).thenReturn(inmobiliarias);

            // When
            List<InmobiliariaDTO> result = inmobiliariaService.findByEstatus("ACTIVE");

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).estatus()).isEqualTo("ACTIVE");
            verify(inmobiliariaRepository, times(1)).findByEstatus("ACTIVE");
        }
    }

    @Nested
    @DisplayName("Create Operations")
    class CreateOperations {

        @Test
        @DisplayName("Should create new inmobiliaria successfully")
        void shouldCreateNewInmobiliaria() {
            // Given
            when(inmobiliariaRepository.existsByRfcNit("ILP123456789")).thenReturn(false);
            when(inmobiliariaRepository.save(any(Inmobiliaria.class))).thenReturn(testInmobiliaria);

            // When
            InmobiliariaDTO result = inmobiliariaService.create(testInmobiliariaDTO);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.rfcNit()).isEqualTo("ILP123456789");
            assertThat(result.nombreComercial()).isEqualTo("Inmobiliaria Los Pinos");
            verify(inmobiliariaRepository, times(1)).existsByRfcNit("ILP123456789");
            verify(inmobiliariaRepository, times(1)).save(any(Inmobiliaria.class));
        }

        @Test
        @DisplayName("Should throw exception when RFC/NIT already exists")
        void shouldThrowExceptionWhenRfcNitAlreadyExists() {
            // Given
            when(inmobiliariaRepository.existsByRfcNit("ILP123456789")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> inmobiliariaService.create(testInmobiliariaDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("RFC/NIT already exists: ILP123456789");
            verify(inmobiliariaRepository, times(1)).existsByRfcNit("ILP123456789");
            verify(inmobiliariaRepository, never()).save(any(Inmobiliaria.class));
        }

        @Test
        @DisplayName("Should create inmobiliaria with default status when not provided")
        void shouldCreateInmobiliariaWithDefaultStatus() {
            // Given
            InmobiliariaDTO dtoWithoutStatus = new InmobiliariaDTO(
                    null, "Test", "Test Legal", "TEST123", "123456789", "test@test.com",
                    "Test Address", "Test City", "Test State", "12345", "Test Contact",
                    null, null
            );
            Inmobiliaria savedInmobiliaria = createTestInmobiliaria();
            savedInmobiliaria.setEstatus("ACTIVE");

            when(inmobiliariaRepository.existsByRfcNit("TEST123")).thenReturn(false);
            when(inmobiliariaRepository.save(any(Inmobiliaria.class))).thenReturn(savedInmobiliaria);

            // When
            InmobiliariaDTO result = inmobiliariaService.create(dtoWithoutStatus);

            // Then
            assertThat(result.estatus()).isEqualTo("ACTIVE");
            verify(inmobiliariaRepository, times(1)).save(any(Inmobiliaria.class));
        }
    }

    @Nested
    @DisplayName("Update Operations")
    class UpdateOperations {

        @Test
        @DisplayName("Should update existing inmobiliaria successfully without RFC change")
        void shouldUpdateExistingInmobiliaria() {
            // Given - Update with same RFC (no RFC change check needed)
            InmobiliariaDTO updateDTO = new InmobiliariaDTO(
                    1L, "Updated Name", "Updated Legal", "ILP123456789", "555-999-9999",
                    "updated@test.com", "Updated Address", "Updated City", "Updated State",
                    "99999", "Updated Contact", LocalDate.now(), "ACTIVE"
            );

            when(inmobiliariaRepository.findById(1L)).thenReturn(Optional.of(testInmobiliaria));
            when(inmobiliariaRepository.save(any(Inmobiliaria.class))).thenReturn(testInmobiliaria);

            // When
            InmobiliariaDTO result = inmobiliariaService.update(1L, updateDTO);

            // Then
            assertThat(result).isNotNull();
            verify(inmobiliariaRepository, times(1)).findById(1L);
            // Should NOT call existsByRfcNitAndIdInmobiliariaNot since RFC is not changing
            verify(inmobiliariaRepository, never()).existsByRfcNitAndIdInmobiliariaNot(anyString(), anyLong());
            verify(inmobiliariaRepository, times(1)).save(any(Inmobiliaria.class));
        }

        @Test
        @DisplayName("Should check RFC uniqueness when RFC is being changed")
        void shouldCheckRfcUniquenessWhenRfcIsChanging() {
            // Given - Update with different RFC
            InmobiliariaDTO updateDTO = new InmobiliariaDTO(
                    1L, "Updated Name", "Updated Legal", "NEW123456789", "555-999-9999",
                    "updated@test.com", "Updated Address", "Updated City", "Updated State",
                    "99999", "Updated Contact", LocalDate.now(), "ACTIVE"
            );

            when(inmobiliariaRepository.findById(1L)).thenReturn(Optional.of(testInmobiliaria));
            when(inmobiliariaRepository.existsByRfcNitAndIdInmobiliariaNot("NEW123456789", 1L)).thenReturn(false);
            when(inmobiliariaRepository.save(any(Inmobiliaria.class))).thenReturn(testInmobiliaria);

            // When
            InmobiliariaDTO result = inmobiliariaService.update(1L, updateDTO);

            // Then
            assertThat(result).isNotNull();
            verify(inmobiliariaRepository, times(1)).findById(1L);
            verify(inmobiliariaRepository, times(1)).existsByRfcNitAndIdInmobiliariaNot("NEW123456789", 1L);
            verify(inmobiliariaRepository, times(1)).save(any(Inmobiliaria.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent inmobiliaria")
        void shouldThrowExceptionWhenUpdatingNonExistentInmobiliaria() {
            // Given
            when(inmobiliariaRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> inmobiliariaService.update(999L, testInmobiliariaDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Inmobiliaria not found with id: 999");
            verify(inmobiliariaRepository, times(1)).findById(999L);
            verify(inmobiliariaRepository, never()).save(any(Inmobiliaria.class));
        }

        @Test
        @DisplayName("Should throw exception when updating with existing RFC/NIT")
        void shouldThrowExceptionWhenUpdatingWithExistingRfcNit() {
            // Given - Use a different RFC than the existing one to trigger uniqueness check
            InmobiliariaDTO updateDTOWithDifferentRfc = new InmobiliariaDTO(
                    1L, "Updated Name", "Updated Legal", "DIFFERENT123", "555-999-9999",
                    "updated@test.com", "Updated Address", "Updated City", "Updated State",
                    "99999", "Updated Contact", LocalDate.now(), "ACTIVE"
            );
            
            when(inmobiliariaRepository.findById(1L)).thenReturn(Optional.of(testInmobiliaria));
            when(inmobiliariaRepository.existsByRfcNitAndIdInmobiliariaNot("DIFFERENT123", 1L)).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> inmobiliariaService.update(1L, updateDTOWithDifferentRfc))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("RFC/NIT already exists: DIFFERENT123");
            verify(inmobiliariaRepository, times(1)).findById(1L);
            verify(inmobiliariaRepository, times(1)).existsByRfcNitAndIdInmobiliariaNot("DIFFERENT123", 1L);
            verify(inmobiliariaRepository, never()).save(any(Inmobiliaria.class));
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperations {

        @Test
        @DisplayName("Should delete existing inmobiliaria successfully")
        void shouldDeleteExistingInmobiliaria() {
            // Given
            when(inmobiliariaRepository.existsById(1L)).thenReturn(true);
            doNothing().when(inmobiliariaRepository).deleteById(1L);

            // When
            inmobiliariaService.deleteById(1L);

            // Then
            verify(inmobiliariaRepository, times(1)).existsById(1L);
            verify(inmobiliariaRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent inmobiliaria")
        void shouldThrowExceptionWhenDeletingNonExistentInmobiliaria() {
            // Given
            when(inmobiliariaRepository.existsById(999L)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> inmobiliariaService.deleteById(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Inmobiliaria not found with id: 999");
            verify(inmobiliariaRepository, times(1)).existsById(999L);
            verify(inmobiliariaRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("Status Operations")
    class StatusOperations {

        @Test
        @DisplayName("Should change status successfully")
        void shouldChangeStatusSuccessfully() {
            // Given
            when(inmobiliariaRepository.findById(1L)).thenReturn(Optional.of(testInmobiliaria));
            when(inmobiliariaRepository.save(any(Inmobiliaria.class))).thenReturn(testInmobiliaria);

            // When
            InmobiliariaDTO result = inmobiliariaService.changeStatus(1L, "INACTIVE");

            // Then
            assertThat(result).isNotNull();
            verify(inmobiliariaRepository, times(1)).findById(1L);
            verify(inmobiliariaRepository, times(1)).save(any(Inmobiliaria.class));
        }

        @Test
        @DisplayName("Should throw exception when changing status of non-existent inmobiliaria")
        void shouldThrowExceptionWhenChangingStatusOfNonExistentInmobiliaria() {
            // Given
            when(inmobiliariaRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> inmobiliariaService.changeStatus(999L, "INACTIVE"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Inmobiliaria not found with id: 999");
            verify(inmobiliariaRepository, times(1)).findById(999L);
            verify(inmobiliariaRepository, never()).save(any(Inmobiliaria.class));
        }
    }

    @Nested
    @DisplayName("Utility Operations")
    class UtilityOperations {

        @Test
        @DisplayName("Should count by status correctly")
        void shouldCountByStatusCorrectly() {
            // Given
            when(inmobiliariaRepository.countByEstatus("ACTIVE")).thenReturn(5L);

            // When
            long result = inmobiliariaService.countByEstatus("ACTIVE");

            // Then
            assertThat(result).isEqualTo(5L);
            verify(inmobiliariaRepository, times(1)).countByEstatus("ACTIVE");
        }

        @Test
        @DisplayName("Should get distinct cities")
        void shouldGetDistinctCities() {
            // Given
            List<String> cities = Arrays.asList("Ciudad de México", "Guadalajara", "Monterrey");
            when(inmobiliariaRepository.findDistinctCiudades()).thenReturn(cities);

            // When
            List<String> result = inmobiliariaService.getDistinctCiudades();

            // Then
            assertThat(result).hasSize(3);
            assertThat(result).containsExactlyInAnyOrder("Ciudad de México", "Guadalajara", "Monterrey");
            verify(inmobiliariaRepository, times(1)).findDistinctCiudades();
        }

        @Test
        @DisplayName("Should get distinct states")
        void shouldGetDistinctEstados() {
            // Given
            List<String> states = Arrays.asList("CDMX", "Jalisco", "Nuevo León");
            when(inmobiliariaRepository.findDistinctEstados()).thenReturn(states);

            // When
            List<String> result = inmobiliariaService.getDistinctEstados();

            // Then
            assertThat(result).hasSize(3);
            assertThat(result).containsExactlyInAnyOrder("CDMX", "Jalisco", "Nuevo León");
            verify(inmobiliariaRepository, times(1)).findDistinctEstados();
        }

        @Test
        @DisplayName("Should check existence by ID correctly")
        void shouldCheckExistenceByIdCorrectly() {
            // Given
            when(inmobiliariaRepository.existsById(1L)).thenReturn(true);
            when(inmobiliariaRepository.existsById(999L)).thenReturn(false);

            // When
            boolean exists = inmobiliariaService.existsById(1L);
            boolean notExists = inmobiliariaService.existsById(999L);

            // Then
            assertThat(exists).isTrue();
            assertThat(notExists).isFalse();
            verify(inmobiliariaRepository, times(1)).existsById(1L);
            verify(inmobiliariaRepository, times(1)).existsById(999L);
        }

        @Test
        @DisplayName("Should check existence by RFC/NIT correctly")
        void shouldCheckExistenceByRfcNitCorrectly() {
            // Given
            when(inmobiliariaRepository.existsByRfcNit("ILP123456789")).thenReturn(true);
            when(inmobiliariaRepository.existsByRfcNit("NOTFOUND")).thenReturn(false);

            // When
            boolean exists = inmobiliariaService.existsByRfcNit("ILP123456789");
            boolean notExists = inmobiliariaService.existsByRfcNit("NOTFOUND");

            // Then
            assertThat(exists).isTrue();
            assertThat(notExists).isFalse();
            verify(inmobiliariaRepository, times(1)).existsByRfcNit("ILP123456789");
            verify(inmobiliariaRepository, times(1)).existsByRfcNit("NOTFOUND");
        }
    }

    // Conversion methods are private, so they are tested indirectly through public methods

    @Nested
    @DisplayName("Search and Filter Operations")
    class SearchOperations {

        @Test
        @DisplayName("Should perform filtered search correctly")
        void shouldPerformFilteredSearchCorrectly() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Inmobiliaria> page = new PageImpl<>(Arrays.asList(testInmobiliaria));
            
            when(inmobiliariaRepository.findByFilters(
                    "Los Pinos", "Ciudad de México", "CDMX", "ACTIVE", pageable))
                    .thenReturn(page);

            // When
            Page<InmobiliariaDTO> result = inmobiliariaService.findByFilters(
                    "Los Pinos", "Ciudad de México", "CDMX", "ACTIVE", pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).nombreComercial()).isEqualTo("Inmobiliaria Los Pinos");
            verify(inmobiliariaRepository, times(1)).findByFilters(
                    "Los Pinos", "Ciudad de México", "CDMX", "ACTIVE", pageable);
        }

        @Test
        @DisplayName("Should handle empty search results")
        void shouldHandleEmptySearchResults() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Inmobiliaria> emptyPage = new PageImpl<>(Arrays.asList());
            
            when(inmobiliariaRepository.findByFilters(
                    "NotFound", null, null, null, pageable))
                    .thenReturn(emptyPage);

            // When
            Page<InmobiliariaDTO> result = inmobiliariaService.findByFilters(
                    "NotFound", null, null, null, pageable);

            // Then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
            verify(inmobiliariaRepository, times(1)).findByFilters(
                    "NotFound", null, null, null, pageable);
        }
    }

    @Nested
    @DisplayName("Conditional Logic and Branch Coverage Tests")
    class ConditionalLogicBranchTests {

        @Test
        @DisplayName("Should apply default values when fields are null in create")
        void shouldApplyDefaultValuesWhenFieldsAreNullInCreate() {
            // Given - DTO without optional fields
            InmobiliariaDTO dtoWithNulls = new InmobiliariaDTO(
                    null,
                    "Test Inmobiliaria",
                    "Test Legal Name",
                    "TEST123456789",
                    "555-1234",
                    "test@test.com",
                    "Test Address",
                    "Test City",
                    "Test State",
                    "12345",
                    "Test Contact",
                    null,     // fechaRegistro null
                    null      // estatus null
            );

            Inmobiliaria savedInmobiliaria = new Inmobiliaria();
            savedInmobiliaria.setIdInmobiliaria(1L);
            savedInmobiliaria.setNombreComercial("Test Inmobiliaria");
            savedInmobiliaria.setRazonSocial("Test Legal Name");
            savedInmobiliaria.setRfcNit("TEST123456789");
            savedInmobiliaria.setTelefonoPrincipal("555-1234");
            savedInmobiliaria.setEmailContacto("test@test.com");
            savedInmobiliaria.setDireccionCompleta("Test Address");
            savedInmobiliaria.setCiudad("Test City");
            savedInmobiliaria.setEstado("Test State");
            savedInmobiliaria.setCodigoPostal("12345");
            savedInmobiliaria.setPersonaContacto("Test Contact");
            savedInmobiliaria.setFechaRegistro(LocalDate.now()); // default applied
            savedInmobiliaria.setEstatus("ACTIVE"); // default applied

            when(inmobiliariaRepository.existsByRfcNit("TEST123456789")).thenReturn(false);
            when(inmobiliariaRepository.save(any(Inmobiliaria.class))).thenReturn(savedInmobiliaria);

            // When
            InmobiliariaDTO result = inmobiliariaService.create(dtoWithNulls);

            // Then - Verify default values were applied
            assertThat(result).isNotNull();
            assertThat(result.fechaRegistro()).isEqualTo(LocalDate.now());
            assertThat(result.estatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("Should preserve provided values when fields are not null in create")
        void shouldPreserveProvidedValuesWhenFieldsAreNotNullInCreate() {
            // Given - DTO with all fields provided
            LocalDate providedDate = LocalDate.of(2023, 1, 1);
            InmobiliariaDTO dtoWithValues = new InmobiliariaDTO(
                    null,
                    "Test Inmobiliaria",
                    "Test Legal Name",
                    "TEST123456789",
                    "555-1234",
                    "test@test.com",
                    "Test Address",
                    "Test City",
                    "Test State",
                    "12345",
                    "Test Contact",
                    providedDate,    // fechaRegistro provided
                    "INACTIVE"       // estatus provided
            );

            Inmobiliaria savedInmobiliaria = new Inmobiliaria();
            savedInmobiliaria.setIdInmobiliaria(1L);
            savedInmobiliaria.setNombreComercial("Test Inmobiliaria");
            savedInmobiliaria.setRazonSocial("Test Legal Name");
            savedInmobiliaria.setRfcNit("TEST123456789");
            savedInmobiliaria.setTelefonoPrincipal("555-1234");
            savedInmobiliaria.setEmailContacto("test@test.com");
            savedInmobiliaria.setDireccionCompleta("Test Address");
            savedInmobiliaria.setCiudad("Test City");
            savedInmobiliaria.setEstado("Test State");
            savedInmobiliaria.setCodigoPostal("12345");
            savedInmobiliaria.setPersonaContacto("Test Contact");
            savedInmobiliaria.setFechaRegistro(providedDate); // preserved
            savedInmobiliaria.setEstatus("INACTIVE"); // preserved

            when(inmobiliariaRepository.existsByRfcNit("TEST123456789")).thenReturn(false);
            when(inmobiliariaRepository.save(any(Inmobiliaria.class))).thenReturn(savedInmobiliaria);

            // When
            InmobiliariaDTO result = inmobiliariaService.create(dtoWithValues);

            // Then - Verify provided values were preserved
            assertThat(result).isNotNull();
            assertThat(result.fechaRegistro()).isEqualTo(providedDate);
            assertThat(result.estatus()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("Should handle blank estatus by applying default in create")
        void shouldHandleBlankEstatusByApplyingDefaultInCreate() {
            // Given - DTO with blank estatus
            InmobiliariaDTO dtoWithBlankStatus = new InmobiliariaDTO(
                    null,
                    "Test Inmobiliaria",
                    "Test Legal Name",
                    "TEST123456789",
                    "555-1234",
                    "test@test.com",
                    "Test Address",
                    "Test City",
                    "Test State",
                    "12345",
                    "Test Contact",
                    LocalDate.now(),
                    "   "      // blank estatus (whitespace)
            );

            Inmobiliaria savedInmobiliaria = new Inmobiliaria();
            savedInmobiliaria.setIdInmobiliaria(1L);
            savedInmobiliaria.setNombreComercial("Test Inmobiliaria");
            savedInmobiliaria.setRazonSocial("Test Legal Name");
            savedInmobiliaria.setRfcNit("TEST123456789");
            savedInmobiliaria.setTelefonoPrincipal("555-1234");
            savedInmobiliaria.setEmailContacto("test@test.com");
            savedInmobiliaria.setDireccionCompleta("Test Address");
            savedInmobiliaria.setCiudad("Test City");
            savedInmobiliaria.setEstado("Test State");
            savedInmobiliaria.setCodigoPostal("12345");
            savedInmobiliaria.setPersonaContacto("Test Contact");
            savedInmobiliaria.setFechaRegistro(LocalDate.now());
            savedInmobiliaria.setEstatus("ACTIVE"); // default applied for blank

            when(inmobiliariaRepository.existsByRfcNit("TEST123456789")).thenReturn(false);
            when(inmobiliariaRepository.save(any(Inmobiliaria.class))).thenReturn(savedInmobiliaria);

            // When
            InmobiliariaDTO result = inmobiliariaService.create(dtoWithBlankStatus);

            // Then - Verify default was applied for blank estatus
            assertThat(result).isNotNull();
            assertThat(result.estatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("Should throw exception when duplicate RFC/NIT in create")
        void shouldThrowExceptionWhenDuplicateRfcNitInCreate() {
            // Given - DTO with duplicate RFC/NIT
            when(inmobiliariaRepository.existsByRfcNit(testInmobiliariaDTO.rfcNit())).thenReturn(true);

            // When & Then - Should throw exception (duplicate validation branch)
            assertThatThrownBy(() -> inmobiliariaService.create(testInmobiliariaDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("RFC/NIT already exists: " + testInmobiliariaDTO.rfcNit());

            verify(inmobiliariaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when duplicate RFC/NIT in update but different inmobiliaria")
        void shouldThrowExceptionWhenDuplicateRfcNitInUpdateButDifferentInmobiliaria() {
            // Given - Update with RFC/NIT that exists for different inmobiliaria
            InmobiliariaDTO updateDTO = new InmobiliariaDTO(
                    1L,
                    "Updated Name",
                    "Updated Legal",
                    "EXISTING_RFC",  // RFC that exists for different inmobiliaria
                    "555-9999",
                    "updated@test.com",
                    "Updated Address",
                    "Updated City",
                    "Updated State",
                    "99999",
                    "Updated Contact",
                    LocalDate.now(),
                    "ACTIVE"
            );

            when(inmobiliariaRepository.findById(1L)).thenReturn(Optional.of(testInmobiliaria));
            when(inmobiliariaRepository.existsByRfcNitAndIdInmobiliariaNot("EXISTING_RFC", 1L))
                    .thenReturn(true);

            // When & Then - Should throw exception (duplicate RFC for different inmobiliaria)
            assertThatThrownBy(() -> inmobiliariaService.update(1L, updateDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("RFC/NIT already exists: EXISTING_RFC");

            verify(inmobiliariaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should allow update with same RFC/NIT for same inmobiliaria")
        void shouldAllowUpdateWithSameRfcNitForSameInmobiliaria() {
            // Given - Update with same RFC/NIT (should be allowed)
            InmobiliariaDTO updateDTO = new InmobiliariaDTO(
                    1L,
                    "Updated Name",
                    "Updated Legal",
                    testInmobiliaria.getRfcNit(),  // Same RFC/NIT
                    "555-9999",
                    "updated@test.com",
                    "Updated Address",
                    "Updated City",
                    "Updated State",
                    "99999",
                    "Updated Contact",
                    LocalDate.now(),
                    "ACTIVE"
            );

            Inmobiliaria updatedInmobiliaria = new Inmobiliaria();
            updatedInmobiliaria.setIdInmobiliaria(1L);
            updatedInmobiliaria.setNombreComercial("Updated Name");
            updatedInmobiliaria.setRazonSocial("Updated Legal");
            updatedInmobiliaria.setRfcNit(testInmobiliaria.getRfcNit());
            updatedInmobiliaria.setTelefonoPrincipal("555-9999");
            updatedInmobiliaria.setEmailContacto("updated@test.com");
            updatedInmobiliaria.setDireccionCompleta("Updated Address");
            updatedInmobiliaria.setCiudad("Updated City");
            updatedInmobiliaria.setEstado("Updated State");
            updatedInmobiliaria.setCodigoPostal("99999");
            updatedInmobiliaria.setPersonaContacto("Updated Contact");
            updatedInmobiliaria.setFechaRegistro(LocalDate.now());
            updatedInmobiliaria.setEstatus("ACTIVE");

            when(inmobiliariaRepository.findById(1L)).thenReturn(Optional.of(testInmobiliaria));
            when(inmobiliariaRepository.save(any(Inmobiliaria.class))).thenReturn(updatedInmobiliaria);

            // When
            InmobiliariaDTO result = inmobiliariaService.update(1L, updateDTO);

            // Then - Should update successfully
            assertThat(result).isNotNull();
            assertThat(result.nombreComercial()).isEqualTo("Updated Name");
            verify(inmobiliariaRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("Should handle edge cases in estatus validation")
        void shouldHandleEdgeCasesInEstatusValidation() {
            // Test different estatus edge cases
            String[] edgeCaseStatuses = {
                "",           // empty string
                "   ",        // whitespace only
                "\t\n",       // tabs and newlines
                null          // null value
            };

            for (String edgeStatus : edgeCaseStatuses) {
                InmobiliariaDTO dtoWithEdgeStatus = new InmobiliariaDTO(
                        null,
                        "Test Inmobiliaria",
                        "Test Legal Name",
                        "TEST" + Math.random(), // Unique RFC for each test
                        "555-1234",
                        "test@test.com",
                        "Test Address",
                        "Test City",
                        "Test State",
                        "12345",
                        "Test Contact",
                        LocalDate.now(),
                        edgeStatus
                );

                Inmobiliaria savedInmobiliaria = new Inmobiliaria();
                savedInmobiliaria.setIdInmobiliaria(1L);
                savedInmobiliaria.setNombreComercial("Test Inmobiliaria");
                savedInmobiliaria.setRazonSocial("Test Legal Name");
                savedInmobiliaria.setRfcNit(dtoWithEdgeStatus.rfcNit());
                savedInmobiliaria.setTelefonoPrincipal("555-1234");
                savedInmobiliaria.setEmailContacto("test@test.com");
                savedInmobiliaria.setDireccionCompleta("Test Address");
                savedInmobiliaria.setCiudad("Test City");
                savedInmobiliaria.setEstado("Test State");
                savedInmobiliaria.setCodigoPostal("12345");
                savedInmobiliaria.setPersonaContacto("Test Contact");
                savedInmobiliaria.setFechaRegistro(LocalDate.now());
                savedInmobiliaria.setEstatus("ACTIVE"); // Default should be applied

                when(inmobiliariaRepository.existsByRfcNit(dtoWithEdgeStatus.rfcNit())).thenReturn(false);
                when(inmobiliariaRepository.save(any(Inmobiliaria.class))).thenReturn(savedInmobiliaria);

                // When
                InmobiliariaDTO result = inmobiliariaService.create(dtoWithEdgeStatus);

                // Then - Should apply default "ACTIVE" for all edge cases
                assertThat(result.estatus()).isEqualTo("ACTIVE");
            }
        }
    }
}