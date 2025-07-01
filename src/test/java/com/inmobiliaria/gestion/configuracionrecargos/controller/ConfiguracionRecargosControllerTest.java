package com.inmobiliaria.gestion.configuracionrecargos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmobiliaria.gestion.configuracionrecargos.dto.ConfiguracionRecargosDTO;
import com.inmobiliaria.gestion.configuracionrecargos.service.ConfiguracionRecargosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConfiguracionRecargosController.class)
@ComponentScan(basePackages = "com.inmobiliaria.gestion")
@EnableJpaRepositories(basePackages = "com.inmobiliaria.gestion")
@AutoConfigureDataJpa
public class ConfiguracionRecargosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfiguracionRecargosService configuracionRecargosService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void createConfiguracionRecargos() throws Exception {
        ConfiguracionRecargosDTO dto = new ConfiguracionRecargosDTO(1, 1L, "Politica 1", 5, "Fijo", BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ONE, 1, "Todos", true);
        when(configuracionRecargosService.save(any(ConfiguracionRecargosDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/configuracion-recargos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void getAllConfiguracionRecargos() throws Exception {
        ConfiguracionRecargosDTO dto = new ConfiguracionRecargosDTO(1, 1L, "Politica 1", 5, "Fijo", BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ONE, 1, "Todos", true);
        when(configuracionRecargosService.findAll()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/v1/configuracion-recargos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser
    void getConfiguracionRecargosById() throws Exception {
        ConfiguracionRecargosDTO dto = new ConfiguracionRecargosDTO(1, 1L, "Politica 1", 5, "Fijo", BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ONE, 1, "Todos", true);
        when(configuracionRecargosService.findById(1)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/v1/configuracion-recargos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void getConfiguracionRecargosById_notFound() throws Exception {
        when(configuracionRecargosService.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/configuracion-recargos/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateConfiguracionRecargos() throws Exception {
        ConfiguracionRecargosDTO dto = new ConfiguracionRecargosDTO(1, 1L, "Politica 1", 5, "Fijo", BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ONE, 1, "Todos", true);
        when(configuracionRecargosService.save(any(ConfiguracionRecargosDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/v1/configuracion-recargos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void deleteConfiguracionRecargos() throws Exception {
        mockMvc.perform(delete("/api/v1/configuracion-recargos/1"))
                .andExpect(status().isNoContent());
    }
}
