package com.inmobiliaria.gestion.inmobiliaria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmobiliaria.gestion.inmobiliaria.dto.InmobiliariaDTO;
import com.inmobiliaria.gestion.inmobiliaria.service.InmobiliariaService;
import com.inmobiliaria.gestion.security.JwtAuthenticationEntryPoint;
import com.inmobiliaria.gestion.security.JwtAuthenticationFilter;
import com.inmobiliaria.gestion.security.JwtUtil;
import com.inmobiliaria.gestion.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InmobiliariaController.class)
@Import({SecurityConfig.class})
class InmobiliariaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InmobiliariaService inmobiliariaService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private JwtUtil jwtUtil;

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
    void getAllInmobiliarias_ShouldReturnPagedInmobiliarias() throws Exception {
        Page<InmobiliariaDTO> page = new PageImpl<>(List.of(testInmobiliariaDTO));
        when(inmobiliariaService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/inmobiliarias")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "nombreComercial")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].idInmobiliaria").value(1L))
                .andExpect(jsonPath("$.content[0].nombreComercial").value("Inmobiliaria Test"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getInmobiliariaById_WhenExists_ShouldReturnInmobiliaria() throws Exception {
        when(inmobiliariaService.findById(1L)).thenReturn(Optional.of(testInmobiliariaDTO));

        mockMvc.perform(get("/api/v1/inmobiliarias/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idInmobiliaria").value(1L))
                .andExpect(jsonPath("$.nombreComercial").value("Inmobiliaria Test"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getInmobiliariaById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(inmobiliariaService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/inmobiliarias/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getInmobiliariaByRfcNit_WhenExists_ShouldReturnInmobiliaria() throws Exception {
        when(inmobiliariaService.findByRfcNit("TEST123456789")).thenReturn(Optional.of(testInmobiliariaDTO));

        mockMvc.perform(get("/api/v1/inmobiliarias/rfc/TEST123456789"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rfcNit").value("TEST123456789"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void searchInmobiliarias_ShouldReturnFilteredResults() throws Exception {
        Page<InmobiliariaDTO> page = new PageImpl<>(List.of(testInmobiliariaDTO));
        when(inmobiliariaService.findByFilters(anyString(), anyString(), anyString(), anyString(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/inmobiliarias/search")
                .param("nombreComercial", "Test")
                .param("ciudad", "Test City")
                .param("estatus", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombreComercial").value("Inmobiliaria Test"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createInmobiliaria_WithValidData_ShouldReturnCreated() throws Exception {
        InmobiliariaDTO newInmobiliaria = new InmobiliariaDTO(
                null, "New Inmobiliaria", "New Real Estate S.A.", "NEW123456789",
                "+52 55 9876 5432", "new@inmobiliaria.com", "New Address", "New City", "New State",
                "54321", "Maria New", null, "ACTIVE"
        );

        InmobiliariaDTO createdInmobiliaria = new InmobiliariaDTO(
                2L, "New Inmobiliaria", "New Real Estate S.A.", "NEW123456789",
                "+52 55 9876 5432", "new@inmobiliaria.com", "New Address", "New City", "New State",
                "54321", "Maria New", LocalDate.now(), "ACTIVE"
        );

        when(inmobiliariaService.create(any(InmobiliariaDTO.class))).thenReturn(createdInmobiliaria);

        mockMvc.perform(post("/api/v1/inmobiliarias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newInmobiliaria)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idInmobiliaria").value(2L))
                .andExpect(jsonPath("$.nombreComercial").value("New Inmobiliaria"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createInmobiliaria_WithDuplicateRfcNit_ShouldReturnBadRequest() throws Exception {
        InmobiliariaDTO newInmobiliaria = new InmobiliariaDTO(
                null, "New Inmobiliaria", "New Real Estate S.A.", "TEST123456789",
                "+52 55 9876 5432", "new@inmobiliaria.com", "New Address", "New City", "New State",
                "54321", "Maria New", null, "ACTIVE"
        );

        when(inmobiliariaService.create(any(InmobiliariaDTO.class)))
                .thenThrow(new IllegalArgumentException("RFC/NIT already exists: TEST123456789"));

        mockMvc.perform(post("/api/v1/inmobiliarias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newInmobiliaria)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("RFC/NIT already exists: TEST123456789"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateInmobiliaria_WithValidData_ShouldReturnUpdated() throws Exception {
        InmobiliariaDTO updatedData = new InmobiliariaDTO(
                1L, "Updated Inmobiliaria", "Updated Real Estate S.A.", "TEST123456789",
                "+52 55 1111 2222", "updated@inmobiliaria.com", "Updated Address", "Updated City", "Updated State",
                "11111", "Updated Person", LocalDate.now(), "ACTIVE"
        );

        when(inmobiliariaService.update(eq(1L), any(InmobiliariaDTO.class))).thenReturn(updatedData);

        mockMvc.perform(put("/api/v1/inmobiliarias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreComercial").value("Updated Inmobiliaria"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateInmobiliaria_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(inmobiliariaService.update(eq(999L), any(InmobiliariaDTO.class)))
                .thenThrow(new IllegalArgumentException("Inmobiliaria not found with id: 999"));

        mockMvc.perform(put("/api/v1/inmobiliarias/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testInmobiliariaDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteInmobiliaria_WhenExists_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/inmobiliarias/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteInmobiliaria_WhenNotExists_ShouldReturnNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Inmobiliaria not found with id: 999"))
                .when(inmobiliariaService).deleteById(999L);

        mockMvc.perform(delete("/api/v1/inmobiliarias/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void changeInmobiliariaStatus_ShouldReturnUpdated() throws Exception {
        InmobiliariaDTO updatedInmobiliaria = new InmobiliariaDTO(
                1L, "Inmobiliaria Test", "Test Real Estate S.A. de C.V.", "TEST123456789",
                "+52 55 1234 5678", "test@inmobiliaria.com", "Av. Test 123, Col. Centro",
                "Test City", "Test State", "12345", "Juan Test", LocalDate.now(), "INACTIVE"
        );

        when(inmobiliariaService.changeStatus(1L, "INACTIVE")).thenReturn(updatedInmobiliaria);

        mockMvc.perform(patch("/api/v1/inmobiliarias/1/status")
                .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estatus").value("INACTIVE"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getInmobiliariasByStatus_ShouldReturnFilteredResults() throws Exception {
        Page<InmobiliariaDTO> page = new PageImpl<>(List.of(testInmobiliariaDTO));
        when(inmobiliariaService.findByEstatus(eq("ACTIVE"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/inmobiliarias/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].estatus").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getStatistics_ShouldReturnStatistics() throws Exception {
        when(inmobiliariaService.countByEstatus("ACTIVE")).thenReturn(5L);
        when(inmobiliariaService.countByEstatus("INACTIVE")).thenReturn(2L);
        when(inmobiliariaService.getDistinctCiudades()).thenReturn(List.of("Test City", "Another City"));
        when(inmobiliariaService.getDistinctEstados()).thenReturn(List.of("Test State", "Another State"));

        mockMvc.perform(get("/api/v1/inmobiliarias/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(7))
                .andExpect(jsonPath("$.active").value(5))
                .andExpect(jsonPath("$.inactive").value(2))
                .andExpect(jsonPath("$.cities").isArray())
                .andExpect(jsonPath("$.states").isArray());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getDistinctCities_ShouldReturnCitiesList() throws Exception {
        when(inmobiliariaService.getDistinctCiudades()).thenReturn(List.of("City A", "City B", "City C"));

        mockMvc.perform(get("/api/v1/inmobiliarias/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getDistinctStates_ShouldReturnStatesList() throws Exception {
        when(inmobiliariaService.getDistinctEstados()).thenReturn(List.of("State A", "State B"));

        mockMvc.perform(get("/api/v1/inmobiliarias/states"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllInmobiliarias_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/inmobiliarias"))
                .andExpect(status().isUnauthorized());
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
    @WithMockUser(roles = "USER")
    void updateInmobiliaria_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/v1/inmobiliarias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testInmobiliariaDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteInmobiliaria_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/inmobiliarias/1"))
                .andExpect(status().isForbidden());
    }
}