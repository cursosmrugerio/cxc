package com.inmobiliaria.gestion.conceptopago.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmobiliaria.gestion.conceptopago.dto.ConceptoPagoDTO;
import com.inmobiliaria.gestion.conceptopago.service.ConceptoPagoService;
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

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConceptoPagoController.class)
@ComponentScan(basePackages = "com.inmobiliaria.gestion")
@EnableJpaRepositories(basePackages = "com.inmobiliaria.gestion")
@AutoConfigureDataJpa
public class ConceptoPagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConceptoPagoService conceptoPagoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void createConceptoPago() throws Exception {
        ConceptoPagoDTO dto = new ConceptoPagoDTO(1, 1L, "Renta", "Renta Mensual", "Fijo", true, true, new Date());
        when(conceptoPagoService.save(any(ConceptoPagoDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/conceptos-pago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void getAllConceptosPago() throws Exception {
        ConceptoPagoDTO dto = new ConceptoPagoDTO(1, 1L, "Renta", "Renta Mensual", "Fijo", true, true, new Date());
        when(conceptoPagoService.findAll()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/v1/conceptos-pago"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser
    void getConceptoPagoById() throws Exception {
        ConceptoPagoDTO dto = new ConceptoPagoDTO(1, 1L, "Renta", "Renta Mensual", "Fijo", true, true, new Date());
        when(conceptoPagoService.findById(1)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/v1/conceptos-pago/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void getConceptoPagoById_notFound() throws Exception {
        when(conceptoPagoService.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/conceptos-pago/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateConceptoPago() throws Exception {
        ConceptoPagoDTO dto = new ConceptoPagoDTO(1, 1L, "Renta", "Renta Mensual", "Fijo", true, true, new Date());
        when(conceptoPagoService.save(any(ConceptoPagoDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/v1/conceptos-pago/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void deleteConceptoPago() throws Exception {
        mockMvc.perform(delete("/api/v1/conceptos-pago/1"))
                .andExpect(status().isNoContent());
    }
}
