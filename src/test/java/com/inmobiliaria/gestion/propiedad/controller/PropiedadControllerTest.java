package com.inmobiliaria.gestion.propiedad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmobiliaria.gestion.propiedad.dto.PropiedadCreateRequest;
import com.inmobiliaria.gestion.propiedad.dto.PropiedadDTO;
import com.inmobiliaria.gestion.propiedad.dto.PropiedadUpdateRequest;
import com.inmobiliaria.gestion.propiedad.service.PropiedadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PropiedadController.class)
class PropiedadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropiedadService propiedadService;

    @Autowired
    private ObjectMapper objectMapper;

    private PropiedadDTO testPropiedadDTO;
    private PropiedadCreateRequest createRequest;
    private PropiedadUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testPropiedadDTO = new PropiedadDTO(
                1,
                1L,
                "CASA",
                BigDecimal.valueOf(150.50),
                BigDecimal.valueOf(120.75),
                LocalDate.now(),
                "DISPONIBLE",
                "Jardín amplio",
                "Calle Reforma 123",
                2,
                3
        );

        createRequest = new PropiedadCreateRequest(
                1L,
                "CASA",
                BigDecimal.valueOf(150.50),
                BigDecimal.valueOf(120.75),
                "DISPONIBLE",
                "Jardín amplio",
                "Calle Reforma 123",
                2,
                3
        );

        updateRequest = new PropiedadUpdateRequest(
                "DEPARTAMENTO",
                BigDecimal.valueOf(200.00),
                BigDecimal.valueOf(180.00),
                "OCUPADA",
                "Balcón amplio",
                "Av. Insurgentes 456",
                3,
                4
        );
    }

    @Test
    void getAllPropiedades_ShouldReturnAllPropiedades() throws Exception {
        // Given
        List<PropiedadDTO> propiedades = Arrays.asList(testPropiedadDTO);
        when(propiedadService.getAllPropiedades()).thenReturn(propiedades);

        // When & Then
        mockMvc.perform(get("/api/v1/propiedades")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idPropiedad", is(1)))
                .andExpect(jsonPath("$[0].tipoPropiedad", is("CASA")))
                .andExpect(jsonPath("$[0].idInmobiliaria", is(1)));
    }

    @Test
    void getPropiedadById_WhenExists_ShouldReturnPropiedad() throws Exception {
        // Given
        when(propiedadService.getPropiedadById(1)).thenReturn(Optional.of(testPropiedadDTO));

        // When & Then
        mockMvc.perform(get("/api/v1/propiedades/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPropiedad", is(1)))
                .andExpect(jsonPath("$.tipoPropiedad", is("CASA")))
                .andExpect(jsonPath("$.estatusPropiedad", is("DISPONIBLE")));
    }

    @Test
    void getPropiedadById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(propiedadService.getPropiedadById(1)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/propiedades/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPropiedadesByInmobiliaria_ShouldReturnPropiedades() throws Exception {
        // Given
        List<PropiedadDTO> propiedades = Arrays.asList(testPropiedadDTO);
        when(propiedadService.getPropiedadesByInmobiliaria(1L)).thenReturn(propiedades);

        // When & Then
        mockMvc.perform(get("/api/v1/propiedades/inmobiliaria/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idInmobiliaria", is(1)));
    }

    @Test
    void getPropiedadesByEstatus_ShouldReturnPropiedades() throws Exception {
        // Given
        List<PropiedadDTO> propiedades = Arrays.asList(testPropiedadDTO);
        when(propiedadService.getPropiedadesByEstatus("DISPONIBLE")).thenReturn(propiedades);

        // When & Then
        mockMvc.perform(get("/api/v1/propiedades/estatus/DISPONIBLE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].estatusPropiedad", is("DISPONIBLE")));
    }

    @Test
    void getPropiedadesByTipo_ShouldReturnPropiedades() throws Exception {
        // Given
        List<PropiedadDTO> propiedades = Arrays.asList(testPropiedadDTO);
        when(propiedadService.getPropiedadesByTipo("CASA")).thenReturn(propiedades);

        // When & Then
        mockMvc.perform(get("/api/v1/propiedades/tipo/CASA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipoPropiedad", is("CASA")));
    }

    @Test
    void searchPropiedadesByDireccion_ShouldReturnMatchingPropiedades() throws Exception {
        // Given
        List<PropiedadDTO> propiedades = Arrays.asList(testPropiedadDTO);
        when(propiedadService.searchPropiedadesByDireccion("Reforma")).thenReturn(propiedades);

        // When & Then
        mockMvc.perform(get("/api/v1/propiedades/search")
                        .param("direccion", "Reforma")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].direccionCompleta", is("Calle Reforma 123")));
    }

    @Test
    void getPropiedadesByInmobiliariaAndEstatus_ShouldReturnPropiedades() throws Exception {
        // Given
        List<PropiedadDTO> propiedades = Arrays.asList(testPropiedadDTO);
        when(propiedadService.getPropiedadesByInmobiliariaAndEstatus(1L, "DISPONIBLE")).thenReturn(propiedades);

        // When & Then
        mockMvc.perform(get("/api/v1/propiedades/inmobiliaria/1/estatus/DISPONIBLE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idInmobiliaria", is(1)))
                .andExpect(jsonPath("$[0].estatusPropiedad", is("DISPONIBLE")));
    }

    @Test
    void createPropiedad_WithValidData_ShouldReturnCreated() throws Exception {
        // Given
        when(propiedadService.createPropiedad(any(PropiedadCreateRequest.class))).thenReturn(testPropiedadDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/propiedades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPropiedad", is(1)))
                .andExpect(jsonPath("$.tipoPropiedad", is("CASA")))
                .andExpect(jsonPath("$.idInmobiliaria", is(1)));
    }

    @Test
    void createPropiedad_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        PropiedadCreateRequest invalidRequest = new PropiedadCreateRequest(
                null, // Invalid - null idInmobiliaria
                "CASA",
                BigDecimal.valueOf(150.50),
                BigDecimal.valueOf(120.75),
                "DISPONIBLE",
                "Jardín amplio",
                "Calle Reforma 123",
                2,
                3
        );

        // When & Then
        mockMvc.perform(post("/api/v1/propiedades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePropiedad_WhenExists_ShouldReturnUpdated() throws Exception {
        // Given
        PropiedadDTO updatedDTO = new PropiedadDTO(
                1,
                1L,
                "DEPARTAMENTO",
                BigDecimal.valueOf(200.00),
                BigDecimal.valueOf(180.00),
                LocalDate.now(),
                "OCUPADA",
                "Balcón amplio",
                "Av. Insurgentes 456",
                3,
                4
        );
        when(propiedadService.updatePropiedad(anyInt(), any(PropiedadUpdateRequest.class))).thenReturn(Optional.of(updatedDTO));

        // When & Then
        mockMvc.perform(put("/api/v1/propiedades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPropiedad", is(1)))
                .andExpect(jsonPath("$.tipoPropiedad", is("DEPARTAMENTO")))
                .andExpect(jsonPath("$.estatusPropiedad", is("OCUPADA")));
    }

    @Test
    void updatePropiedad_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(propiedadService.updatePropiedad(anyInt(), any(PropiedadUpdateRequest.class))).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/v1/propiedades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePropiedad_WhenExists_ShouldReturnNoContent() throws Exception {
        // Given
        when(propiedadService.deletePropiedad(1)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/v1/propiedades/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePropiedad_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(propiedadService.deletePropiedad(1)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/v1/propiedades/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void countPropiedadesByInmobiliaria_ShouldReturnCount() throws Exception {
        // Given
        when(propiedadService.countPropiedadesByInmobiliaria(1L)).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/api/v1/propiedades/inmobiliaria/1/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void countPropiedadesByInmobiliariaAndEstatus_ShouldReturnCount() throws Exception {
        // Given
        when(propiedadService.countPropiedadesByInmobiliariaAndEstatus(1L, "DISPONIBLE")).thenReturn(3L);

        // When & Then
        mockMvc.perform(get("/api/v1/propiedades/inmobiliaria/1/estatus/DISPONIBLE/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }
}