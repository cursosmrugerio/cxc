package com.inmobiliaria.gestion.propiedad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmobiliaria.gestion.propiedad.dto.PropiedadDTO;
import com.inmobiliaria.gestion.propiedad.service.PropiedadService;
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
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PropiedadController.class)
@ComponentScan(basePackages = "com.inmobiliaria.gestion")
@EnableJpaRepositories(basePackages = "com.inmobiliaria.gestion")
@AutoConfigureDataJpa
public class PropiedadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropiedadService propiedadService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void createPropiedad() throws Exception {
        PropiedadDTO dto = new PropiedadDTO(1, 1L, "Direccion", "Casa", BigDecimal.ONE, BigDecimal.ONE, 1, 1, "Ninguna", new Date(), "Activo");
        when(propiedadService.save(any(PropiedadDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/propiedades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void getAllPropiedades() throws Exception {
        PropiedadDTO dto = new PropiedadDTO(1, 1L, "Direccion", "Casa", BigDecimal.ONE, BigDecimal.ONE, 1, 1, "Ninguna", new Date(), "Activo");
        when(propiedadService.findAll()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/v1/propiedades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser
    void getPropiedadById() throws Exception {
        PropiedadDTO dto = new PropiedadDTO(1, 1L, "Direccion", "Casa", BigDecimal.ONE, BigDecimal.ONE, 1, 1, "Ninguna", new Date(), "Activo");
        when(propiedadService.findById(1)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/v1/propiedades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void getPropiedadById_notFound() throws Exception {
        when(propiedadService.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/propiedades/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updatePropiedad() throws Exception {
        PropiedadDTO dto = new PropiedadDTO(1, 1L, "Direccion", "Casa", BigDecimal.ONE, BigDecimal.ONE, 1, 1, "Ninguna", new Date(), "Activo");
        when(propiedadService.save(any(PropiedadDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/v1/propiedades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void deletePropiedad() throws Exception {
        mockMvc.perform(delete("/api/v1/propiedades/1"))
                .andExpect(status().isNoContent());
    }
}
