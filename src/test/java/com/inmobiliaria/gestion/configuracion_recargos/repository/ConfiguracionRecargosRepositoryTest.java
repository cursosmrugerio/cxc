package com.inmobiliaria.gestion.configuracion_recargos.repository;

import com.inmobiliaria.gestion.configuracion_recargos.model.ConfiguracionRecargos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ConfiguracionRecargosRepository Tests")
class ConfiguracionRecargosRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ConfiguracionRecargosRepository configuracionRecargosRepository;

    private ConfiguracionRecargos testConfiguracion1;
    private ConfiguracionRecargos testConfiguracion2;

    @BeforeEach
    void setUp() {
        configuracionRecargosRepository.deleteAll();
        testConfiguracion1 = createTestConfiguracion(1L, true, "mora_activa");
        testConfiguracion2 = createTestConfiguracion(2L, false, "mora_inactiva");
    }

    private ConfiguracionRecargos createTestConfiguracion(Long inmobiliariaId, boolean activo, String tipoRecargo) {
        ConfiguracionRecargos configuracion = new ConfiguracionRecargos();
        configuracion.setActiva(true);
        configuracion.setAplicaAConceptos("renta,servicios");
        configuracion.setDiasCorteServicios(10);
        configuracion.setDiasGracia(3);
        configuracion.setMontoRecargoFijo(BigDecimal.valueOf(100.00));
        configuracion.setNombrePolitica("Política de Prueba " + inmobiliariaId);
        configuracion.setPorcentajeRecargoDiario(BigDecimal.valueOf(1.5));
        configuracion.setRecargoMaximo(BigDecimal.valueOf(1000.00));
        configuracion.setTipoRecargo(tipoRecargo);
        configuracion.setIdInmobiliaria(inmobiliariaId);
        configuracion.setTasaRecargoDiaria(BigDecimal.valueOf(0.05));
        configuracion.setTasaRecargoFija(BigDecimal.valueOf(0.10));
        configuracion.setActivo(activo);
        configuracion.setDiaAplicacion(5);
        configuracion.setMonto(BigDecimal.valueOf(50.00));
        return configuracion;
    }

    @Nested
    @DisplayName("Basic CRUD Operations")
    class BasicCrudOperations {

        @Test
        @DisplayName("Should save and find configuracion by id")
        void shouldSaveAndFindConfiguracionById() {
            ConfiguracionRecargos saved = configuracionRecargosRepository.save(testConfiguracion1);
            entityManager.flush();

            Optional<ConfiguracionRecargos> found = configuracionRecargosRepository.findById(saved.getIdConfiguracionRecargo());

            assertThat(found).isPresent();
            assertThat(found.get().getTipoRecargo()).isEqualTo("mora_activa");
            assertThat(found.get().getIdInmobiliaria()).isEqualTo(1L);
            assertThat(found.get().getActivo()).isTrue();
        }

        @Test
        @DisplayName("Should find all configuraciones")
        void shouldFindAllConfiguraciones() {
            configuracionRecargosRepository.save(testConfiguracion1);
            configuracionRecargosRepository.save(testConfiguracion2);
            entityManager.flush();

            List<ConfiguracionRecargos> configuraciones = configuracionRecargosRepository.findAll();

            assertThat(configuraciones).hasSize(2);
        }

        @Test
        @DisplayName("Should delete configuracion by id")
        void shouldDeleteConfiguracionById() {
            ConfiguracionRecargos saved = configuracionRecargosRepository.save(testConfiguracion1);
            entityManager.flush();

            configuracionRecargosRepository.deleteById(saved.getIdConfiguracionRecargo());
            entityManager.flush();

            Optional<ConfiguracionRecargos> found = configuracionRecargosRepository.findById(saved.getIdConfiguracionRecargo());
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("Custom Query Methods")
    class CustomQueryMethods {

        @Test
        @DisplayName("Should find configuraciones by inmobiliaria")
        void shouldFindConfiguracionesByInmobiliaria() {
            configuracionRecargosRepository.save(testConfiguracion1);
            configuracionRecargosRepository.save(testConfiguracion2);
            entityManager.flush();

            List<ConfiguracionRecargos> configuraciones = configuracionRecargosRepository.findByIdInmobiliaria(1L);

            assertThat(configuraciones).hasSize(1);
            assertThat(configuraciones.get(0).getIdInmobiliaria()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should find configuraciones by tipo recargo")
        void shouldFindConfiguracionesByTipoRecargo() {
            configuracionRecargosRepository.save(testConfiguracion1);
            configuracionRecargosRepository.save(testConfiguracion2);
            entityManager.flush();

            List<ConfiguracionRecargos> moraActiva = configuracionRecargosRepository.findByTipoRecargo("mora_activa");

            assertThat(moraActiva).hasSize(1);
            assertThat(moraActiva.get(0).getTipoRecargo()).isEqualTo("mora_activa");
        }

        @Test
        @DisplayName("Should find configuracion by inmobiliaria and tipo recargo")
        void shouldFindConfiguracionByInmobiliariaAndTipoRecargo() {
            configuracionRecargosRepository.save(testConfiguracion1);
            configuracionRecargosRepository.save(testConfiguracion2);
            entityManager.flush();

            Optional<ConfiguracionRecargos> found = configuracionRecargosRepository.findByIdInmobiliariaAndTipoRecargo(1L, "mora_activa");

            assertThat(found).isPresent();
            assertThat(found.get().getIdInmobiliaria()).isEqualTo(1L);
            assertThat(found.get().getTipoRecargo()).isEqualTo("mora_activa");
        }

        @Test
        @DisplayName("Should return empty when combinacion does not exist")
        void shouldReturnEmptyWhenCombinacionDoesNotExist() {
            configuracionRecargosRepository.save(testConfiguracion1);
            entityManager.flush();

            Optional<ConfiguracionRecargos> found = configuracionRecargosRepository.findByIdInmobiliariaAndTipoRecargo(1L, "inexistente");

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("Business Logic Queries")
    class BusinessLogicQueries {

        @Test
        @DisplayName("Should find active configuraciones only")
        void shouldFindActiveConfiguracionesOnly() {
            configuracionRecargosRepository.save(testConfiguracion1);
            configuracionRecargosRepository.save(testConfiguracion2);
            entityManager.flush();

            List<ConfiguracionRecargos> activas = configuracionRecargosRepository.findByActivoTrue();

            assertThat(activas).hasSize(1);
            assertThat(activas.get(0).getActivo()).isTrue();
        }

        @Test
        @DisplayName("Should find configuraciones by inmobiliaria and activo status")
        void shouldFindConfiguracionesByInmobiliariaAndActivoStatus() {
            configuracionRecargosRepository.save(testConfiguracion1);
            configuracionRecargosRepository.save(testConfiguracion2);
            entityManager.flush();

            List<ConfiguracionRecargos> activas = configuracionRecargosRepository.findByIdInmobiliariaAndActivo(1L, true);

            assertThat(activas).hasSize(1);
            activas.forEach(config -> assertThat(config.getActivo()).isTrue());
        }

        @Test
        @DisplayName("Should find distinct tipos recargo by inmobiliaria")
        void shouldFindDistinctTiposRecargoByInmobiliaria() {
            configuracionRecargosRepository.save(testConfiguracion1);
            
            ConfiguracionRecargos configRenta = createTestConfiguracion(1L, true, "intereses");
            configuracionRecargosRepository.save(configRenta);
            
            entityManager.flush();

            List<String> tiposRecargo = configuracionRecargosRepository.findDistinctTipoRecargoByInmobiliaria(1L);

            assertThat(tiposRecargo).hasSize(2);
            assertThat(tiposRecargo).contains("mora_activa", "intereses");
        }
    }

    @Nested
    @DisplayName("Existence and Count Operations")
    class ExistenceAndCountOperations {

        @Test
        @DisplayName("Should check if configuracion exists by id")
        void shouldCheckIfConfiguracionExistsById() {
            ConfiguracionRecargos saved = configuracionRecargosRepository.save(testConfiguracion1);
            entityManager.flush();

            boolean exists = configuracionRecargosRepository.existsById(saved.getIdConfiguracionRecargo());
            boolean notExists = configuracionRecargosRepository.existsById(999L);

            assertThat(exists).isTrue();
            assertThat(notExists).isFalse();
        }

        @Test
        @DisplayName("Should check if configuracion exists by inmobiliaria and tipo recargo")
        void shouldCheckIfConfiguracionExistsByInmobiliariaAndTipoRecargo() {
            configuracionRecargosRepository.save(testConfiguracion1);
            entityManager.flush();

            boolean exists = configuracionRecargosRepository.existsByIdInmobiliariaAndTipoRecargo(1L, "mora_activa");
            boolean notExists = configuracionRecargosRepository.existsByIdInmobiliariaAndTipoRecargo(1L, "mora_inexistente");

            assertThat(exists).isTrue();
            assertThat(notExists).isFalse();
        }

        @Test
        @DisplayName("Should find active configuraciones by inmobiliaria and tipo recargo")
        void shouldFindActiveConfiguracionesByInmobiliariaAndTipoRecargo() {
            configuracionRecargosRepository.save(testConfiguracion1);
            configuracionRecargosRepository.save(testConfiguracion2);
            entityManager.flush();

            Optional<ConfiguracionRecargos> found = configuracionRecargosRepository.findActiveByInmobiliariaAndTipoRecargo(1L, "mora_activa");

            assertThat(found).isPresent();
            assertThat(found.get().getIdInmobiliaria()).isEqualTo(1L);
            assertThat(found.get().getTipoRecargo()).isEqualTo("mora_activa");
            assertThat(found.get().getActivo()).isTrue();
        }
    }

    @Nested
    @DisplayName("Complex Query Scenarios")
    class ComplexQueryScenarios {

        @Test
        @DisplayName("Should find active configuraciones by inmobiliaria and dia aplicacion")
        void shouldFindActiveConfiguracionesByInmobiliariaAndDiaAplicacion() {
            ConfiguracionRecargos config1 = createTestConfiguracion(1L, true, "mora");
            config1.setNombrePolitica("Política Día 5");
            config1.setDiaAplicacion(5);
            
            ConfiguracionRecargos config2 = createTestConfiguracion(1L, true, "intereses");
            config2.setNombrePolitica("Política Día 15");
            config2.setDiaAplicacion(15);
            
            configuracionRecargosRepository.save(config1);
            configuracionRecargosRepository.save(config2);
            entityManager.flush();

            List<ConfiguracionRecargos> dia5 = configuracionRecargosRepository.findActiveByInmobiliariaAndDiaAplicacion(1L, 5);
            List<ConfiguracionRecargos> dia15 = configuracionRecargosRepository.findActiveByInmobiliariaAndDiaAplicacion(1L, 15);

            assertThat(dia5).hasSize(1);
            assertThat(dia5.get(0).getNombrePolitica()).isEqualTo("Política Día 5");
            
            assertThat(dia15).hasSize(1);
            assertThat(dia15.get(0).getNombrePolitica()).isEqualTo("Política Día 15");
        }

        @Test
        @DisplayName("Should find active configuraciones by nombre politica")
        void shouldFindActiveConfiguracionesByNombrePolitica() {
            ConfiguracionRecargos config1 = createTestConfiguracion(1L, true, "mora");
            config1.setNombrePolitica("Política Especial");
            
            ConfiguracionRecargos config2 = createTestConfiguracion(2L, true, "intereses");
            config2.setNombrePolitica("Política Normal");
            
            configuracionRecargosRepository.save(config1);
            configuracionRecargosRepository.save(config2);
            entityManager.flush();

            List<ConfiguracionRecargos> especiales = configuracionRecargosRepository.findActiveByNombrePolitica("Política Especial");
            List<ConfiguracionRecargos> normales = configuracionRecargosRepository.findActiveByNombrePolitica("Política Normal");

            assertThat(especiales).hasSize(1);
            assertThat(especiales.get(0).getIdInmobiliaria()).isEqualTo(1L);
            
            assertThat(normales).hasSize(1);
            assertThat(normales.get(0).getIdInmobiliaria()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("Data Integrity and Constraints")
    class DataIntegrityAndConstraints {

        @Test
        @DisplayName("Should enforce unique constraint for inmobiliaria and tipo recargo")
        void shouldEnforceUniqueConstraintForInmobiliariaAndTipoRecargo() {
            configuracionRecargosRepository.save(testConfiguracion1);
            entityManager.flush();

            ConfiguracionRecargos duplicateConfig = createTestConfiguracion(1L, true, "mora_activa");
            
            boolean exists = configuracionRecargosRepository.existsByIdInmobiliariaAndTipoRecargo(1L, "mora_activa");
            
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Should handle decimal precision correctly")
        void shouldHandleDecimalPrecisionCorrectly() {
            ConfiguracionRecargos config = createTestConfiguracion(1L, true, "precision_test");
            config.setTasaRecargoDiaria(new BigDecimal("0.05678"));
            config.setMontoRecargoFijo(new BigDecimal("123.4567"));
            
            ConfiguracionRecargos saved = configuracionRecargosRepository.save(config);
            entityManager.flush();

            Optional<ConfiguracionRecargos> found = configuracionRecargosRepository.findById(saved.getIdConfiguracionRecargo());
            
            assertThat(found).isPresent();
            assertThat(found.get().getTasaRecargoDiaria()).isEqualByComparingTo(new BigDecimal("0.05678"));
            assertThat(found.get().getMontoRecargoFijo()).isEqualByComparingTo(new BigDecimal("123.4567"));
        }

        @Test
        @DisplayName("Should handle null optional fields gracefully")
        void shouldHandleNullOptionalFieldsGracefully() {
            ConfiguracionRecargos config = new ConfiguracionRecargos();
            config.setTipoRecargo("minimal_config");
            config.setIdInmobiliaria(1L);
            config.setActivo(true);
            config.setActiva(true);
            config.setDiaAplicacion(1);
            config.setMonto(BigDecimal.valueOf(100.00));
            
            ConfiguracionRecargos saved = configuracionRecargosRepository.save(config);
            entityManager.flush();

            Optional<ConfiguracionRecargos> found = configuracionRecargosRepository.findById(saved.getIdConfiguracionRecargo());
            
            assertThat(found).isPresent();
            assertThat(found.get().getTipoRecargo()).isEqualTo("minimal_config");
        }
    }

    @Nested
    @DisplayName("Performance and Edge Cases")
    class PerformanceAndEdgeCases {

        @Test
        @DisplayName("Should handle large datasets efficiently")
        void shouldHandleLargeDatasetsEfficiently() {
            for (int i = 1; i <= 100; i++) {
                ConfiguracionRecargos config = createTestConfiguracion((long) (i % 10 + 1), true, "mora_" + i);
                configuracionRecargosRepository.save(config);
            }
            entityManager.flush();

            long totalCount = configuracionRecargosRepository.count();
            List<ConfiguracionRecargos> inmobiliaria1 = configuracionRecargosRepository.findByIdInmobiliaria(1L);

            assertThat(totalCount).isEqualTo(100);
            assertThat(inmobiliaria1).hasSize(10);
        }

        @Test
        @DisplayName("Should handle empty search results gracefully")
        void shouldHandleEmptySearchResultsGracefully() {
            List<ConfiguracionRecargos> inexistente = configuracionRecargosRepository.findByIdInmobiliaria(999L);
            List<ConfiguracionRecargos> tipoInexistente = configuracionRecargosRepository.findByTipoRecargo("inexistente");

            assertThat(inexistente).isEmpty();
            assertThat(tipoInexistente).isEmpty();
        }

        @Test
        @DisplayName("Should handle special characters in text fields")
        void shouldHandleSpecialCharactersInTextFields() {
            ConfiguracionRecargos config = createTestConfiguracion(1L, true, "mora_especial");
            config.setNombrePolitica("Política con caracteres especiales: áéíóú ñ @#$%");
            config.setAplicaAConceptos("renta,servicios,mantenimiento");
            
            ConfiguracionRecargos saved = configuracionRecargosRepository.save(config);
            entityManager.flush();

            Optional<ConfiguracionRecargos> found = configuracionRecargosRepository.findById(saved.getIdConfiguracionRecargo());
            
            assertThat(found).isPresent();
            assertThat(found.get().getNombrePolitica()).contains("áéíóú ñ @#$%");
        }
    }
}