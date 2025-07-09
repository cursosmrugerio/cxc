package com.inmobiliaria.gestion.configuracion_recargos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmobiliaria.gestion.configuracion_recargos.dto.ConfiguracionRecargosDTO;
import com.inmobiliaria.gestion.configuracion_recargos.model.ConfiguracionRecargos;
import com.inmobiliaria.gestion.configuracion_recargos.repository.ConfiguracionRecargosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ConfiguracionRecargos Integration Tests")
class ConfiguracionRecargosIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConfiguracionRecargosRepository repository;

    private ConfiguracionRecargosDTO testDto;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        testDto = new ConfiguracionRecargosDTO(
                null,
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

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create configuracion recargo successfully")
    void shouldCreateConfiguracionRecargoSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/configuracion-recargos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoRecargo").value("mora_test"))
                .andExpect(jsonPath("$.nombrePolitica").value("Política de Prueba"))
                .andExpect(jsonPath("$.monto").value(50.00))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get all configuraciones recargos")
    void shouldGetAllConfiguracionesRecargos() throws Exception {
        ConfiguracionRecargos entity = createTestEntity();
        repository.save(entity);

        mockMvc.perform(get("/api/v1/configuracion-recargos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipoRecargo").value("mora_test"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get configuracion recargo by id")
    void shouldGetConfiguracionRecargoById() throws Exception {
        ConfiguracionRecargos entity = createTestEntity();
        ConfiguracionRecargos saved = repository.save(entity);

        mockMvc.perform(get("/api/v1/configuracion-recargos/{id}", saved.getIdConfiguracionRecargo()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoRecargo").value("mora_test"))
                .andExpect(jsonPath("$.nombrePolitica").value("Política de Prueba"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update configuracion recargo")
    void shouldUpdateConfiguracionRecargo() throws Exception {
        ConfiguracionRecargos entity = createTestEntity();
        ConfiguracionRecargos saved = repository.save(entity);

        ConfiguracionRecargosDTO updateDto = new ConfiguracionRecargosDTO(
                saved.getIdConfiguracionRecargo(),
                false,
                "renta",
                15,
                5,
                BigDecimal.valueOf(150.00),
                "Política Actualizada",
                BigDecimal.valueOf(2.0),
                BigDecimal.valueOf(1500.00),
                "mora_actualizada",
                1L,
                BigDecimal.valueOf(0.08),
                BigDecimal.valueOf(0.15),
                false,
                10,
                BigDecimal.valueOf(75.00)
        );

        mockMvc.perform(put("/api/v1/configuracion-recargos/{id}", saved.getIdConfiguracionRecargo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoRecargo").value("mora_actualizada"))
                .andExpect(jsonPath("$.nombrePolitica").value("Política Actualizada"))
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete configuracion recargo")
    void shouldDeleteConfiguracionRecargo() throws Exception {
        ConfiguracionRecargos entity = createTestEntity();
        ConfiguracionRecargos saved = repository.save(entity);

        mockMvc.perform(delete("/api/v1/configuracion-recargos/{id}", saved.getIdConfiguracionRecargo()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/configuracion-recargos/{id}", saved.getIdConfiguracionRecargo()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get configuraciones by inmobiliaria")
    void shouldGetConfiguracionesByInmobiliaria() throws Exception {
        ConfiguracionRecargos entity = createTestEntity();
        repository.save(entity);

        mockMvc.perform(get("/api/v1/configuracion-recargos/inmobiliaria/{idInmobiliaria}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].idInmobiliaria").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should toggle active status")
    void shouldToggleActiveStatus() throws Exception {
        ConfiguracionRecargos entity = createTestEntity();
        ConfiguracionRecargos saved = repository.save(entity);

        mockMvc.perform(patch("/api/v1/configuracion-recargos/{id}/toggle-active", saved.getIdConfiguracionRecargo()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    private ConfiguracionRecargos createTestEntity() {
        ConfiguracionRecargos entity = new ConfiguracionRecargos();
        entity.setActiva(true);
        entity.setAplicaAConceptos("renta,servicios");
        entity.setDiasCorteServicios(10);
        entity.setDiasGracia(3);
        entity.setMontoRecargoFijo(BigDecimal.valueOf(100.00));
        entity.setNombrePolitica("Política de Prueba");
        entity.setPorcentajeRecargoDiario(BigDecimal.valueOf(1.5));
        entity.setRecargoMaximo(BigDecimal.valueOf(1000.00));
        entity.setTipoRecargo("mora_test");
        entity.setIdInmobiliaria(1L);
        entity.setTasaRecargoDiaria(BigDecimal.valueOf(0.05));
        entity.setTasaRecargoFija(BigDecimal.valueOf(0.10));
        entity.setActivo(true);
        entity.setDiaAplicacion(5);
        entity.setMonto(BigDecimal.valueOf(50.00));
        return entity;
    }
}