package com.inmobiliaria.gestion.configuracion_recargos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmobiliaria.gestion.configuracion_recargos.dto.ConfiguracionRecargosDTO;
import com.inmobiliaria.gestion.configuracion_recargos.service.ConfiguracionRecargosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConfiguracionRecargosController.class)
@DisplayName("ConfiguracionRecargosController Tests")
class ConfiguracionRecargosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConfiguracionRecargosService configuracionRecargosService;

    private ConfiguracionRecargosDTO testConfiguracionDTO;

    @BeforeEach
    void setUp() {
        testConfiguracionDTO = createTestConfiguracionDTO();
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
    @DisplayName("POST /api/v1/configuracion-recargos")
    class CreateConfiguracion {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should create configuracion successfully")
        void shouldCreateConfiguracionSuccessfully() throws Exception {
            when(configuracionRecargosService.save(any(ConfiguracionRecargosDTO.class)))
                    .thenReturn(testConfiguracionDTO);

            mockMvc.perform(post("/api/v1/configuracion-recargos")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testConfiguracionDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.tipoRecargo").value("mora_test"))
                    .andExpect(jsonPath("$.nombrePolitica").value("Política de Prueba"))
                    .andExpect(jsonPath("$.activo").value(true))
                    .andExpect(jsonPath("$.monto").value(50.00));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return bad request for invalid input")
        void shouldReturnBadRequestForInvalidInput() throws Exception {
            ConfiguracionRecargosDTO invalidDto = new ConfiguracionRecargosDTO(
                    null, null, null, -1, -1, null, null, null, null, null, null, null, null, null, null, null
            );

            mockMvc.perform(post("/api/v1/configuracion-recargos")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should require authentication")
        void shouldRequireAuthentication() throws Exception {
            mockMvc.perform(post("/api/v1/configuracion-recargos")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testConfiguracionDTO)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/configuracion-recargos")
    class GetAllConfiguraciones {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return all configuraciones successfully")
        void shouldReturnAllConfiguracionesSuccessfully() throws Exception {
            List<ConfiguracionRecargosDTO> configuraciones = Arrays.asList(testConfiguracionDTO);
            when(configuracionRecargosService.findAll()).thenReturn(configuraciones);

            mockMvc.perform(get("/api/v1/configuracion-recargos"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].tipoRecargo").value("mora_test"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should allow user role to read configuraciones")
        void shouldAllowUserRoleToReadConfiguraciones() throws Exception {
            List<ConfiguracionRecargosDTO> configuraciones = Arrays.asList(testConfiguracionDTO);
            when(configuracionRecargosService.findAll()).thenReturn(configuraciones);

            mockMvc.perform(get("/api/v1/configuracion-recargos"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/configuracion-recargos/{id}")
    class GetConfiguracionById {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return configuracion by id")
        void shouldReturnConfiguracionById() throws Exception {
            when(configuracionRecargosService.findById(1L)).thenReturn(Optional.of(testConfiguracionDTO));

            mockMvc.perform(get("/api/v1/configuracion-recargos/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idConfiguracionRecargo").value(1))
                    .andExpect(jsonPath("$.tipoRecargo").value("mora_test"))
                    .andExpect(jsonPath("$.nombrePolitica").value("Política de Prueba"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return not found when configuracion does not exist")
        void shouldReturnNotFoundWhenConfiguracionDoesNotExist() throws Exception {
            when(configuracionRecargosService.findById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/v1/configuracion-recargos/{id}", 999L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/configuracion-recargos/{id}")
    class UpdateConfiguracion {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should update configuracion successfully")
        void shouldUpdateConfiguracionSuccessfully() throws Exception {
            ConfiguracionRecargosDTO updatedDto = new ConfiguracionRecargosDTO(
                    1L, false, "renta", 15, 5, BigDecimal.valueOf(150.00),
                    "Política Actualizada", BigDecimal.valueOf(2.0), BigDecimal.valueOf(1500.00),
                    "mora_actualizada", 1L, BigDecimal.valueOf(0.08), BigDecimal.valueOf(0.15),
                    false, 10, BigDecimal.valueOf(75.00)
            );

            when(configuracionRecargosService.update(eq(1L), any(ConfiguracionRecargosDTO.class)))
                    .thenReturn(Optional.of(updatedDto));

            mockMvc.perform(put("/api/v1/configuracion-recargos/{id}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tipoRecargo").value("mora_actualizada"))
                    .andExpect(jsonPath("$.nombrePolitica").value("Política Actualizada"))
                    .andExpect(jsonPath("$.activo").value(false));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return not found when configuracion does not exist")
        void shouldReturnNotFoundWhenConfiguracionDoesNotExist() throws Exception {
            when(configuracionRecargosService.update(eq(999L), any(ConfiguracionRecargosDTO.class)))
                    .thenReturn(Optional.empty());

            mockMvc.perform(put("/api/v1/configuracion-recargos/{id}", 999L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testConfiguracionDTO)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should require admin role for update operations")
        void shouldRequireAdminRoleForUpdateOperations() throws Exception {
            mockMvc.perform(put("/api/v1/configuracion-recargos/{id}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testConfiguracionDTO)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/configuracion-recargos/{id}")
    class DeleteConfiguracion {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should delete configuracion successfully")
        void shouldDeleteConfiguracionSuccessfully() throws Exception {
            when(configuracionRecargosService.deleteById(1L)).thenReturn(true);

            mockMvc.perform(delete("/api/v1/configuracion-recargos/{id}", 1L)
                            .with(csrf()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return not found when configuracion does not exist")
        void shouldReturnNotFoundWhenConfiguracionDoesNotExist() throws Exception {
            when(configuracionRecargosService.deleteById(999L)).thenReturn(false);

            mockMvc.perform(delete("/api/v1/configuracion-recargos/{id}", 999L)
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should require admin role for delete operations")
        void shouldRequireAdminRoleForDeleteOperations() throws Exception {
            mockMvc.perform(delete("/api/v1/configuracion-recargos/{id}", 1L)
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/configuracion-recargos/inmobiliaria/{idInmobiliaria}")
    class GetConfiguracionesByInmobiliaria {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return configuraciones by inmobiliaria")
        void shouldReturnConfiguracionesByInmobiliaria() throws Exception {
            List<ConfiguracionRecargosDTO> configuraciones = Arrays.asList(testConfiguracionDTO);
            when(configuracionRecargosService.findByInmobiliaria(1L)).thenReturn(configuraciones);

            mockMvc.perform(get("/api/v1/configuracion-recargos/inmobiliaria/{idInmobiliaria}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].idInmobiliaria").value(1));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/configuracion-recargos/inmobiliaria/{idInmobiliaria}/activas")
    class GetActiveConfiguracionesByInmobiliaria {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return active configuraciones by inmobiliaria")
        void shouldReturnActiveConfiguracionesByInmobiliaria() throws Exception {
            List<ConfiguracionRecargosDTO> configuraciones = Arrays.asList(testConfiguracionDTO);
            when(configuracionRecargosService.findActiveByInmobiliaria(1L)).thenReturn(configuraciones);

            mockMvc.perform(get("/api/v1/configuracion-recargos/inmobiliaria/{idInmobiliaria}/activas", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].activo").value(true));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/configuracion-recargos/tipo/{tipoRecargo}")
    class GetConfiguracionesByTipo {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return configuraciones by tipo recargo")
        void shouldReturnConfiguracionesByTipoRecargo() throws Exception {
            List<ConfiguracionRecargosDTO> configuraciones = Arrays.asList(testConfiguracionDTO);
            when(configuracionRecargosService.findByTipoRecargo("mora_test")).thenReturn(configuraciones);

            mockMvc.perform(get("/api/v1/configuracion-recargos/tipo/{tipoRecargo}", "mora_test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].tipoRecargo").value("mora_test"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/configuracion-recargos/inmobiliaria/{idInmobiliaria}/tipo/{tipoRecargo}")
    class GetConfiguracionByInmobiliariaAndTipo {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return configuracion by inmobiliaria and tipo")
        void shouldReturnConfiguracionByInmobiliariaAndTipo() throws Exception {
            when(configuracionRecargosService.findByInmobiliariaAndTipoRecargo(1L, "mora_test"))
                    .thenReturn(Optional.of(testConfiguracionDTO));

            mockMvc.perform(get("/api/v1/configuracion-recargos/inmobiliaria/{idInmobiliaria}/tipo/{tipoRecargo}", 1L, "mora_test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idInmobiliaria").value(1))
                    .andExpect(jsonPath("$.tipoRecargo").value("mora_test"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return not found when configuracion does not exist")
        void shouldReturnNotFoundWhenConfiguracionDoesNotExist() throws Exception {
            when(configuracionRecargosService.findByInmobiliariaAndTipoRecargo(1L, "inexistente"))
                    .thenReturn(Optional.empty());

            mockMvc.perform(get("/api/v1/configuracion-recargos/inmobiliaria/{idInmobiliaria}/tipo/{tipoRecargo}", 1L, "inexistente"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/configuracion-recargos/{id}/toggle-active")
    class ToggleActiveStatus {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should toggle active status successfully")
        void shouldToggleActiveStatusSuccessfully() throws Exception {
            ConfiguracionRecargosDTO toggledDto = new ConfiguracionRecargosDTO(
                    1L, true, "renta,servicios", 10, 3, BigDecimal.valueOf(100.00),
                    "Política de Prueba", BigDecimal.valueOf(1.5), BigDecimal.valueOf(1000.00),
                    "mora_test", 1L, BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.10),
                    false, 5, BigDecimal.valueOf(50.00)
            );

            when(configuracionRecargosService.toggleActive(1L)).thenReturn(Optional.of(toggledDto));

            mockMvc.perform(patch("/api/v1/configuracion-recargos/{id}/toggle-active", 1L)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.activo").value(false));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return not found when configuracion does not exist")
        void shouldReturnNotFoundWhenConfiguracionDoesNotExist() throws Exception {
            when(configuracionRecargosService.toggleActive(999L)).thenReturn(Optional.empty());

            mockMvc.perform(patch("/api/v1/configuracion-recargos/{id}/toggle-active", 999L)
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should require admin role for toggle operations")
        void shouldRequireAdminRoleForToggleOperations() throws Exception {
            mockMvc.perform(patch("/api/v1/configuracion-recargos/{id}/toggle-active", 1L)
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should handle malformed JSON")
        void shouldHandleMalformedJson() throws Exception {
            mockMvc.perform(post("/api/v1/configuracion-recargos")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid json"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should handle missing content type")
        void shouldHandleMissingContentType() throws Exception {
            mockMvc.perform(post("/api/v1/configuracion-recargos")
                            .with(csrf())
                            .content(objectMapper.writeValueAsString(testConfiguracionDTO)))
                    .andExpect(status().isUnsupportedMediaType());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should handle invalid path parameters")
        void shouldHandleInvalidPathParameters() throws Exception {
            mockMvc.perform(get("/api/v1/configuracion-recargos/{id}", "invalid-id"))
                    .andExpect(status().isBadRequest());
        }
    }
}