
package com.inmobiliaria.gestion.inmobiliaria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmobiliaria.gestion.inmobiliaria.dto.InmobiliariaDTO;
import com.inmobiliaria.gestion.inmobiliaria.service.InmobiliariaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InmobiliariaController.class)
class InmobiliariaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InmobiliariaService inmobiliariaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create() throws Exception {
        InmobiliariaDTO dto = new InmobiliariaDTO(1L, "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", LocalDate.now(), "Active");
        when(inmobiliariaService.save(any(InmobiliariaDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/inmobiliarias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAll() throws Exception {
        InmobiliariaDTO dto = new InmobiliariaDTO(1L, "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", LocalDate.now(), "Active");
        when(inmobiliariaService.findAll()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/v1/inmobiliarias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getById() throws Exception {
        InmobiliariaDTO dto = new InmobiliariaDTO(1L, "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", LocalDate.now(), "Active");
        when(inmobiliariaService.findById(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/v1/inmobiliarias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getById_notFound() throws Exception {
        when(inmobiliariaService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/inmobiliarias/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void update() throws Exception {
        InmobiliariaDTO dto = new InmobiliariaDTO(1L, "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", LocalDate.now(), "Active");
        when(inmobiliariaService.save(any(InmobiliariaDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/v1/inmobiliarias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deleteTest() throws Exception {
        mockMvc.perform(delete("/api/v1/inmobiliarias/1"))
                .andExpect(status().isNoContent());
    }
}
