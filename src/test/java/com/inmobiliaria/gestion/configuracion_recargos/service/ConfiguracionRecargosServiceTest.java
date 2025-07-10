package com.inmobiliaria.gestion.configuracion_recargos.service;

import com.inmobiliaria.gestion.configuracion_recargos.dto.ConfiguracionRecargosDTO;
import com.inmobiliaria.gestion.configuracion_recargos.model.ConfiguracionRecargos;
import com.inmobiliaria.gestion.configuracion_recargos.repository.ConfiguracionRecargosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConfiguracionRecargosService Tests")
class ConfiguracionRecargosServiceTest {

    @Mock
    private ConfiguracionRecargosRepository configuracionRecargosRepository;

    @InjectMocks
    private ConfiguracionRecargosService configuracionRecargosService;

    private ConfiguracionRecargos testConfiguracion;
    private ConfiguracionRecargosDTO testConfiguracionDTO;

    @BeforeEach
    void setUp() {
        testConfiguracion = createTestConfiguracion();
        testConfiguracionDTO = createTestConfiguracionDTO();
    }

    private ConfiguracionRecargos createTestConfiguracion() {
        ConfiguracionRecargos configuracion = new ConfiguracionRecargos();
        configuracion.setIdConfiguracionRecargo(1L);
        configuracion.setActiva(true);
        configuracion.setAplicaAConceptos("renta,servicios");
        configuracion.setDiasCorteServicios(10);
        configuracion.setDiasGracia(3);
        configuracion.setMontoRecargoFijo(BigDecimal.valueOf(100.00));
        configuracion.setNombrePolitica("Política de Prueba");
        configuracion.setPorcentajeRecargoDiario(BigDecimal.valueOf(1.5));
        configuracion.setRecargoMaximo(BigDecimal.valueOf(1000.00));
        configuracion.setTipoRecargo("mora_test");
        configuracion.setIdInmobiliaria(1L);
        configuracion.setTasaRecargoDiaria(BigDecimal.valueOf(0.05));
        configuracion.setTasaRecargoFija(BigDecimal.valueOf(0.10));
        configuracion.setActivo(true);
        configuracion.setDiaAplicacion(5);
        configuracion.setMonto(BigDecimal.valueOf(50.00));
        return configuracion;
    }

    private ConfiguracionRecargosDTO createTestConfiguracionDTO() {
        return new ConfiguracionRecargosDTO(
                1L,
                true,
                "renta,servicios",
                10,
                3,
                BigDecimal.valueOf(100.00),
                "Política de Prueba",
                BigDecimal.valueOf(1.5),
                BigDecimal.valueOf(1000.00),
                "mora_test",
                1L,
                BigDecimal.valueOf(0.05),
                BigDecimal.valueOf(0.10),
                true,
                5,
                BigDecimal.valueOf(50.00)
        );
    }

    @Nested
    @DisplayName("Save Operations")
    class SaveOperations {

        @Test
        @DisplayName("Should save configuracion successfully")
        void shouldSaveConfiguracionSuccessfully() {
            when(configuracionRecargosRepository.save(any(ConfiguracionRecargos.class))).thenReturn(testConfiguracion);

            ConfiguracionRecargosDTO result = configuracionRecargosService.save(testConfiguracionDTO);

            assertThat(result.tipoRecargo()).isEqualTo("mora_test");
            assertThat(result.idInmobiliaria()).isEqualTo(1L);
            assertThat(result.activo()).isTrue();
            verify(configuracionRecargosRepository, times(1)).save(any(ConfiguracionRecargos.class));
        }

        @Test
        @DisplayName("Should handle null values appropriately when saving")
        void shouldHandleNullValuesAppropriatelyWhenSaving() {
            ConfiguracionRecargosDTO dtoWithNulls = new ConfiguracionRecargosDTO(
                    null, true, "renta", 10, 3, BigDecimal.valueOf(100.00), "Test Policy",
                    BigDecimal.valueOf(1.5), BigDecimal.valueOf(1000.00), "test_type", 1L,
                    BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.10), true, 5, BigDecimal.valueOf(50.00)
            );
            
            when(configuracionRecargosRepository.save(any(ConfiguracionRecargos.class))).thenReturn(testConfiguracion);

            ConfiguracionRecargosDTO result = configuracionRecargosService.save(dtoWithNulls);

            assertThat(result).isNotNull();
            verify(configuracionRecargosRepository, times(1)).save(any(ConfiguracionRecargos.class));
        }
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperations {

        @Test
        @DisplayName("Should find all configuraciones")
        void shouldFindAllConfiguraciones() {
            List<ConfiguracionRecargos> configuraciones = Arrays.asList(testConfiguracion);
            when(configuracionRecargosRepository.findAll()).thenReturn(configuraciones);

            List<ConfiguracionRecargosDTO> result = configuracionRecargosService.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).tipoRecargo()).isEqualTo("mora_test");
            verify(configuracionRecargosRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should find configuracion by ID when exists")
        void shouldFindConfiguracionById() {
            when(configuracionRecargosRepository.findById(1L)).thenReturn(Optional.of(testConfiguracion));

            Optional<ConfiguracionRecargosDTO> result = configuracionRecargosService.findById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().idConfiguracionRecargo()).isEqualTo(1L);
            assertThat(result.get().tipoRecargo()).isEqualTo("mora_test");
            verify(configuracionRecargosRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should return empty when configuracion not found by ID")
        void shouldReturnEmptyWhenConfiguracionNotFoundById() {
            when(configuracionRecargosRepository.findById(999L)).thenReturn(Optional.empty());

            Optional<ConfiguracionRecargosDTO> result = configuracionRecargosService.findById(999L);

            assertThat(result).isEmpty();
            verify(configuracionRecargosRepository, times(1)).findById(999L);
        }

        @Test
        @DisplayName("Should find configuraciones by inmobiliaria")
        void shouldFindConfiguracionesByInmobiliaria() {
            List<ConfiguracionRecargos> configuraciones = Arrays.asList(testConfiguracion);
            when(configuracionRecargosRepository.findByIdInmobiliaria(1L)).thenReturn(configuraciones);

            List<ConfiguracionRecargosDTO> result = configuracionRecargosService.findByInmobiliaria(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).idInmobiliaria()).isEqualTo(1L);
            verify(configuracionRecargosRepository, times(1)).findByIdInmobiliaria(1L);
        }

        @Test
        @DisplayName("Should find active configuraciones by inmobiliaria")
        void shouldFindActiveConfiguracionesByInmobiliaria() {
            List<ConfiguracionRecargos> configuraciones = Arrays.asList(testConfiguracion);
            when(configuracionRecargosRepository.findByIdInmobiliaria(1L)).thenReturn(configuraciones);

            List<ConfiguracionRecargosDTO> result = configuracionRecargosService.findActiveByInmobiliaria(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).idInmobiliaria()).isEqualTo(1L);
            assertThat(result.get(0).activo()).isTrue();
            verify(configuracionRecargosRepository, times(1)).findByIdInmobiliaria(1L);
        }

        @Test
        @DisplayName("Should filter out inactive configuraciones")
        void shouldFilterOutInactiveConfiguraciones() {
            ConfiguracionRecargos inactiveConfiguracion = createTestConfiguracion();
            inactiveConfiguracion.setActivo(false);
            
            List<ConfiguracionRecargos> configuraciones = Arrays.asList(testConfiguracion, inactiveConfiguracion);
            when(configuracionRecargosRepository.findByIdInmobiliaria(1L)).thenReturn(configuraciones);

            List<ConfiguracionRecargosDTO> result = configuracionRecargosService.findActiveByInmobiliaria(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).activo()).isTrue();
        }

        @Test
        @DisplayName("Should find configuraciones by tipo recargo")
        void shouldFindConfiguracionesByTipoRecargo() {
            List<ConfiguracionRecargos> configuraciones = Arrays.asList(testConfiguracion);
            when(configuracionRecargosRepository.findByTipoRecargo("mora_test")).thenReturn(configuraciones);

            List<ConfiguracionRecargosDTO> result = configuracionRecargosService.findByTipoRecargo("mora_test");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).tipoRecargo()).isEqualTo("mora_test");
            verify(configuracionRecargosRepository, times(1)).findByTipoRecargo("mora_test");
        }

        @Test
        @DisplayName("Should find configuracion by inmobiliaria and tipo recargo")
        void shouldFindConfiguracionByInmobiliariaAndTipoRecargo() {
            when(configuracionRecargosRepository.findByIdInmobiliariaAndTipoRecargo(1L, "mora_test"))
                    .thenReturn(Optional.of(testConfiguracion));

            Optional<ConfiguracionRecargosDTO> result = configuracionRecargosService.findByInmobiliariaAndTipoRecargo(1L, "mora_test");

            assertThat(result).isPresent();
            assertThat(result.get().idInmobiliaria()).isEqualTo(1L);
            assertThat(result.get().tipoRecargo()).isEqualTo("mora_test");
            verify(configuracionRecargosRepository, times(1)).findByIdInmobiliariaAndTipoRecargo(1L, "mora_test");
        }
    }

    @Nested
    @DisplayName("Update Operations")
    class UpdateOperations {

        @Test
        @DisplayName("Should update configuracion successfully")
        void shouldUpdateConfiguracionSuccessfully() {
            ConfiguracionRecargos existingConfiguracion = createTestConfiguracion();
            ConfiguracionRecargos updatedConfiguracion = createTestConfiguracion();
            updatedConfiguracion.setTipoRecargo("mora_actualizada");

            when(configuracionRecargosRepository.findById(1L)).thenReturn(Optional.of(existingConfiguracion));
            when(configuracionRecargosRepository.save(any(ConfiguracionRecargos.class))).thenReturn(updatedConfiguracion);

            Optional<ConfiguracionRecargosDTO> result = configuracionRecargosService.update(1L, testConfiguracionDTO);

            assertThat(result).isPresent();
            assertThat(result.get().tipoRecargo()).isEqualTo("mora_actualizada");
            verify(configuracionRecargosRepository, times(1)).findById(1L);
            verify(configuracionRecargosRepository, times(1)).save(any(ConfiguracionRecargos.class));
        }

        @Test
        @DisplayName("Should return empty when configuracion not found for update")
        void shouldReturnEmptyWhenConfiguracionNotFoundForUpdate() {
            when(configuracionRecargosRepository.findById(999L)).thenReturn(Optional.empty());

            Optional<ConfiguracionRecargosDTO> result = configuracionRecargosService.update(999L, testConfiguracionDTO);

            assertThat(result).isEmpty();
            verify(configuracionRecargosRepository, times(1)).findById(999L);
            verify(configuracionRecargosRepository, never()).save(any(ConfiguracionRecargos.class));
        }

        @Test
        @DisplayName("Should update all fields correctly")
        void shouldUpdateAllFieldsCorrectly() {
            ConfiguracionRecargos existingConfiguracion = createTestConfiguracion();
            ConfiguracionRecargosDTO updateDto = new ConfiguracionRecargosDTO(
                    1L, false, "renta", 15, 5, BigDecimal.valueOf(150.00),
                    "Política Actualizada", BigDecimal.valueOf(2.0), BigDecimal.valueOf(1500.00),
                    "mora_actualizada", 2L, BigDecimal.valueOf(0.08), BigDecimal.valueOf(0.15),
                    false, 10, BigDecimal.valueOf(75.00)
            );

            when(configuracionRecargosRepository.findById(1L)).thenReturn(Optional.of(existingConfiguracion));
            when(configuracionRecargosRepository.save(any(ConfiguracionRecargos.class))).thenAnswer(invocation -> {
                ConfiguracionRecargos saved = invocation.getArgument(0);
                saved.setIdConfiguracionRecargo(1L);
                return saved;
            });

            Optional<ConfiguracionRecargosDTO> result = configuracionRecargosService.update(1L, updateDto);

            assertThat(result).isPresent();
            verify(configuracionRecargosRepository, times(1)).save(any(ConfiguracionRecargos.class));
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperations {

        @Test
        @DisplayName("Should delete configuracion successfully when it exists")
        void shouldDeleteConfiguracionSuccessfullyWhenItExists() {
            when(configuracionRecargosRepository.existsById(1L)).thenReturn(true);
            doNothing().when(configuracionRecargosRepository).deleteById(1L);

            boolean result = configuracionRecargosService.deleteById(1L);

            assertThat(result).isTrue();
            verify(configuracionRecargosRepository, times(1)).existsById(1L);
            verify(configuracionRecargosRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should return false when configuracion does not exist")
        void shouldReturnFalseWhenConfiguracionDoesNotExist() {
            when(configuracionRecargosRepository.existsById(999L)).thenReturn(false);

            boolean result = configuracionRecargosService.deleteById(999L);

            assertThat(result).isFalse();
            verify(configuracionRecargosRepository, times(1)).existsById(999L);
            verify(configuracionRecargosRepository, never()).deleteById(999L);
        }
    }

    @Nested
    @DisplayName("Toggle Active Operations")
    class ToggleActiveOperations {

        @Test
        @DisplayName("Should toggle active status from true to false")
        void shouldToggleActiveStatusFromTrueToFalse() {
            ConfiguracionRecargos existingConfiguracion = createTestConfiguracion();
            existingConfiguracion.setActivo(true);
            
            when(configuracionRecargosRepository.findById(1L)).thenReturn(Optional.of(existingConfiguracion));
            when(configuracionRecargosRepository.save(any(ConfiguracionRecargos.class))).thenAnswer(invocation -> {
                ConfiguracionRecargos saved = invocation.getArgument(0);
                return saved;
            });

            Optional<ConfiguracionRecargosDTO> result = configuracionRecargosService.toggleActive(1L);

            assertThat(result).isPresent();
            assertThat(result.get().activo()).isFalse();
            verify(configuracionRecargosRepository, times(1)).findById(1L);
            verify(configuracionRecargosRepository, times(1)).save(any(ConfiguracionRecargos.class));
        }

        @Test
        @DisplayName("Should toggle active status from false to true")
        void shouldToggleActiveStatusFromFalseToTrue() {
            ConfiguracionRecargos existingConfiguracion = createTestConfiguracion();
            existingConfiguracion.setActivo(false);
            
            when(configuracionRecargosRepository.findById(1L)).thenReturn(Optional.of(existingConfiguracion));
            when(configuracionRecargosRepository.save(any(ConfiguracionRecargos.class))).thenAnswer(invocation -> {
                ConfiguracionRecargos saved = invocation.getArgument(0);
                return saved;
            });

            Optional<ConfiguracionRecargosDTO> result = configuracionRecargosService.toggleActive(1L);

            assertThat(result).isPresent();
            assertThat(result.get().activo()).isTrue();
            verify(configuracionRecargosRepository, times(1)).findById(1L);
            verify(configuracionRecargosRepository, times(1)).save(any(ConfiguracionRecargos.class));
        }

        @Test
        @DisplayName("Should return empty when configuracion not found for toggle")
        void shouldReturnEmptyWhenConfiguracionNotFoundForToggle() {
            when(configuracionRecargosRepository.findById(999L)).thenReturn(Optional.empty());

            Optional<ConfiguracionRecargosDTO> result = configuracionRecargosService.toggleActive(999L);

            assertThat(result).isEmpty();
            verify(configuracionRecargosRepository, times(1)).findById(999L);
            verify(configuracionRecargosRepository, never()).save(any(ConfiguracionRecargos.class));
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Scenarios")
    class EdgeCasesAndErrorScenarios {

        @Test
        @DisplayName("Should handle empty list results")
        void shouldHandleEmptyListResults() {
            when(configuracionRecargosRepository.findAll()).thenReturn(Arrays.asList());

            List<ConfiguracionRecargosDTO> result = configuracionRecargosService.findAll();

            assertThat(result).isEmpty();
            verify(configuracionRecargosRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should handle multiple configuraciones for inmobiliaria")
        void shouldHandleMultipleConfiguracionesForInmobiliaria() {
            ConfiguracionRecargos configuracion1 = createTestConfiguracion();
            ConfiguracionRecargos configuracion2 = createTestConfiguracion();
            configuracion2.setIdConfiguracionRecargo(2L);
            configuracion2.setTipoRecargo("intereses");
            
            List<ConfiguracionRecargos> configuraciones = Arrays.asList(configuracion1, configuracion2);
            when(configuracionRecargosRepository.findByIdInmobiliaria(1L)).thenReturn(configuraciones);

            List<ConfiguracionRecargosDTO> result = configuracionRecargosService.findByInmobiliaria(1L);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).tipoRecargo()).isEqualTo("mora_test");
            assertThat(result.get(1).tipoRecargo()).isEqualTo("intereses");
        }

        @Test
        @DisplayName("Should handle null values in DTO conversion")
        void shouldHandleNullValuesInDtoConversion() {
            ConfiguracionRecargos configuracionWithNulls = new ConfiguracionRecargos();
            configuracionWithNulls.setIdConfiguracionRecargo(1L);
            configuracionWithNulls.setTipoRecargo("test");
            configuracionWithNulls.setIdInmobiliaria(1L);
            configuracionWithNulls.setActivo(true);
            
            when(configuracionRecargosRepository.findById(1L)).thenReturn(Optional.of(configuracionWithNulls));

            Optional<ConfiguracionRecargosDTO> result = configuracionRecargosService.findById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().idConfiguracionRecargo()).isEqualTo(1L);
            assertThat(result.get().tipoRecargo()).isEqualTo("test");
        }
    }
}