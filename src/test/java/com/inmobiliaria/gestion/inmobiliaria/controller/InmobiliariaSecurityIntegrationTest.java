package com.inmobiliaria.gestion.inmobiliaria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmobiliaria.gestion.inmobiliaria.dto.InmobiliariaDTO;
import com.inmobiliaria.gestion.inmobiliaria.service.InmobiliariaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InmobiliariaSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InmobiliariaService inmobiliariaService;

    @Autowired
    private ObjectMapper objectMapper;

    private InmobiliariaDTO testInmobiliariaDTO;

    @BeforeEach
    void setUp() {
        testInmobiliariaDTO = new InmobiliariaDTO(
                1L,
                "Inmobiliaria Test",
                "Test Real Estate S.A. de C.V.",
                "TEST123456789",
                "+52 55 1234 5678",
                "test@inmobiliaria.com",
                "Av. Test 123, Col. Centro",
                "Test City",
                "Test State",
                "12345",
                "Juan Test",
                LocalDate.now(),
                "ACTIVE"
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllInmobiliarias_WithUserRole_ShouldReturnOk() throws Exception {
        when(inmobiliariaService.findAll(any())).thenReturn(new PageImpl<>(List.of(testInmobiliariaDTO)));

        mockMvc.perform(get("/api/v1/inmobiliarias"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllInmobiliarias_WithAdminRole_ShouldReturnOk() throws Exception {
        when(inmobiliariaService.findAll(any())).thenReturn(new PageImpl<>(List.of(testInmobiliariaDTO)));

        mockMvc.perform(get("/api/v1/inmobiliarias"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createInmobiliaria_WithAdminRole_ShouldReturnCreated() throws Exception {
        InmobiliariaDTO newInmobiliaria = new InmobiliariaDTO(
                null, "New Inmobiliaria", "New Real Estate S.A.", "NEW123456789",
                "+52 55 9876 5432", "new@inmobiliaria.com", "New Address", "New City", "New State",
                "54321", "Maria New", null, "ACTIVE"
        );
        when(inmobiliariaService.create(any(InmobiliariaDTO.class))).thenReturn(testInmobiliariaDTO);

        mockMvc.perform(post("/api/v1/inmobiliarias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newInmobiliaria)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createInmobiliaria_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/inmobiliarias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testInmobiliariaDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateInmobiliaria_WithAdminRole_ShouldReturnUpdated() throws Exception {
        when(inmobiliariaService.update(anyLong(), any(InmobiliariaDTO.class))).thenReturn(testInmobiliariaDTO);

        mockMvc.perform(put("/api/v1/inmobiliarias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testInmobiliariaDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateInmobiliaria_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/v1/inmobiliarias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testInmobiliariaDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteInmobiliaria_WithAdminRole_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/inmobiliarias/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteInmobiliaria_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/inmobiliarias/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void changeInmobiliariaStatus_WithAdminRole_ShouldReturnUpdated() throws Exception {
        when(inmobiliariaService.changeStatus(anyLong(), anyString())).thenReturn(testInmobiliariaDTO);

        mockMvc.perform(patch("/api/v1/inmobiliarias/1/status")
                .param("status", "INACTIVE"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void changeInmobiliariaStatus_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(patch("/api/v1/inmobiliarias/1/status")
                .param("status", "INACTIVE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllInmobiliarias_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/inmobiliarias"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createInmobiliaria_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/inmobiliarias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testInmobiliariaDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateInmobiliaria_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(put("/api/v1/inmobiliarias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testInmobiliariaDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteInmobiliaria_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/v1/inmobiliarias/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changeInmobiliariaStatus_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(patch("/api/v1/inmobiliarias/1/status")
                .param("status", "INACTIVE"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getInmobiliariaById_WithUserRole_ShouldReturnOk() throws Exception {
        when(inmobiliariaService.findById(1L)).thenReturn(Optional.of(testInmobiliariaDTO));
        mockMvc.perform(get("/api/v1/inmobiliarias/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getInmobiliariaByRfcNit_WithUserRole_ShouldReturnOk() throws Exception {
        when(inmobiliariaService.findByRfcNit("TEST123456789")).thenReturn(Optional.of(testInmobiliariaDTO));
        mockMvc.perform(get("/api/v1/inmobiliarias/rfc/TEST123456789"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void searchInmobiliarias_WithUserRole_ShouldReturnOk() throws Exception {
        when(inmobiliariaService.findByFilters(anyString(), anyString(), anyString(), anyString(), any())).thenReturn(new PageImpl<>(List.of(testInmobiliariaDTO)));
        mockMvc.perform(get("/api/v1/inmobiliarias/search"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getInmobiliariasByStatus_WithUserRole_ShouldReturnOk() throws Exception {
        when(inmobiliariaService.findByEstatus(anyString(), any())).thenReturn(new PageImpl<>(List.of(testInmobiliariaDTO)));
        mockMvc.perform(get("/api/v1/inmobiliarias/status/ACTIVE"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getStatistics_WithUserRole_ShouldReturnOk() throws Exception {
        when(inmobiliariaService.countByEstatus(anyString())).thenReturn(1L);
        when(inmobiliariaService.getDistinctCiudades()).thenReturn(List.of("City1"));
        when(inmobiliariaService.getDistinctEstados()).thenReturn(List.of("State1"));
        mockMvc.perform(get("/api/v1/inmobiliarias/statistics"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getDistinctCities_WithUserRole_ShouldReturnOk() throws Exception {
        when(inmobiliariaService.getDistinctCiudades()).thenReturn(List.of("City1"));
        mockMvc.perform(get("/api/v1/inmobiliarias/cities"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getDistinctStates_WithUserRole_ShouldReturnOk() throws Exception {
        when(inmobiliariaService.getDistinctEstados()).thenReturn(List.of("State1"));
        mockMvc.perform(get("/api/v1/inmobiliarias/states"))
                .andExpect(status().isOk());
    }
}
