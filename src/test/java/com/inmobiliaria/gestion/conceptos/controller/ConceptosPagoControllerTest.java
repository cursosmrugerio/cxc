package com.inmobiliaria.gestion.conceptos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmobiliaria.gestion.conceptos.dto.ConceptosPagoCreateRequest;
import com.inmobiliaria.gestion.conceptos.dto.ConceptosPagoDTO;
import com.inmobiliaria.gestion.conceptos.dto.ConceptosPagoUpdateRequest;
import com.inmobiliaria.gestion.conceptos.service.ConceptosPagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConceptosPagoController.class)
@SpringJUnitWebConfig
@DisplayName("ConceptosPagoController Tests")
class ConceptosPagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConceptosPagoService conceptosPagoService;

    @Autowired
    private ObjectMapper objectMapper;

    private ConceptosPagoDTO testConceptoDTO;
    private ConceptosPagoCreateRequest testCreateRequest;
    private ConceptosPagoUpdateRequest testUpdateRequest;

    @BeforeEach
    void setUp() {
        testConceptoDTO = new ConceptosPagoDTO(
                1,
                1L,
                "Renta Mensual",
                "Pago de renta mensual",
                "RENTA",
                true,
                true,
                LocalDate.now()
        );

        testCreateRequest = new ConceptosPagoCreateRequest(
                1L,
                "Renta Mensual",
                "Pago de renta mensual",
                "RENTA",
                true,
                true
        );

        testUpdateRequest = new ConceptosPagoUpdateRequest(
                "Renta Mensual Actualizada",
                "Pago de renta mensual actualizada",
                "RENTA",
                true,
                true
        );
    }

    @Nested
    @DisplayName("GET /api/v1/conceptos-pago")
    class GetAllConceptosPago {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return paged conceptos de pago")
        void shouldReturnPagedConceptosPago() throws Exception {
            // Given
            Page<ConceptosPagoDTO> page = new PageImpl<>(List.of(testConceptoDTO));
            when(conceptosPagoService.findAll(any(Pageable.class))).thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/v1/conceptos-pago")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sortBy", "nombreConcepto")
                    .param("sortDir", "asc"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content[0].idConcepto").value(1))
                    .andExpect(jsonPath("$.content[0].nombreConcepto").value("Renta Mensual"))
                    .andExpect(jsonPath("$.content[0].tipoConcepto").value("RENTA"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return sorted results with desc direction")
        void shouldReturnSortedResultsWithDescDirection() throws Exception {
            // Given
            Page<ConceptosPagoDTO> page = new PageImpl<>(List.of(testConceptoDTO));
            when(conceptosPagoService.findAll(any(Pageable.class))).thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/v1/conceptos-pago")
                    .param("sortDir", "desc")
                    .param("sortBy", "fechaCreacion"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/conceptos-pago/{id}")
    class GetConceptoPagoById {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return concepto de pago when exists")
        void shouldReturnConceptoPagoWhenExists() throws Exception {
            // Given
            when(conceptosPagoService.findById(1)).thenReturn(Optional.of(testConceptoDTO));

            // When & Then
            mockMvc.perform(get("/api/v1/conceptos-pago/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.idConcepto").value(1))
                    .andExpect(jsonPath("$.nombreConcepto").value("Renta Mensual"))
                    .andExpect(jsonPath("$.idInmobiliaria").value(1L));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return 404 when concepto not exists")
        void shouldReturn404WhenConceptoNotExists() throws Exception {
            // Given
            when(conceptosPagoService.findById(999)).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/v1/conceptos-pago/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/conceptos-pago/inmobiliaria/{idInmobiliaria}")
    class GetConceptosPagoByInmobiliaria {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return conceptos by inmobiliaria")
        void shouldReturnConceptosByInmobiliaria() throws Exception {
            // Given
            Page<ConceptosPagoDTO> page = new PageImpl<>(List.of(testConceptoDTO));
            when(conceptosPagoService.findByInmobiliaria(eq(1L), any(Pageable.class))).thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/v1/conceptos-pago/inmobiliaria/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].idInmobiliaria").value(1L));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return conceptos by inmobiliaria and activo status")
        void shouldReturnConceptosByInmobiliariaAndActivo() throws Exception {
            // Given
            Page<ConceptosPagoDTO> page = new PageImpl<>(List.of(testConceptoDTO));
            when(conceptosPagoService.findByInmobiliariaAndActivo(eq(1L), eq(true), any(Pageable.class))).thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/v1/conceptos-pago/inmobiliaria/1")
                    .param("activo", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].activo").value(true));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/conceptos-pago/search")
    class SearchConceptosPago {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return filtered results")
        void shouldReturnFilteredResults() throws Exception {
            // Given
            Page<ConceptosPagoDTO> page = new PageImpl<>(List.of(testConceptoDTO));
            when(conceptosPagoService.findByFilters(anyLong(), anyString(), anyString(), anyBoolean(), anyBoolean(), any(Pageable.class)))
                    .thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/v1/conceptos-pago/search")
                    .param("idInmobiliaria", "1")
                    .param("nombreConcepto", "Renta")
                    .param("tipoConcepto", "RENTA")
                    .param("activo", "true")
                    .param("permiteRecargos", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].nombreConcepto").value("Renta Mensual"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should handle search with no filters")
        void shouldHandleSearchWithNoFilters() throws Exception {
            // Given
            Page<ConceptosPagoDTO> page = new PageImpl<>(List.of(testConceptoDTO));
            when(conceptosPagoService.findByFilters(isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                    .thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/v1/conceptos-pago/search"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/conceptos-pago")
    class CreateConceptoPago {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should create concepto de pago successfully")
        void shouldCreateConceptoPagoSuccessfully() throws Exception {
            // Given
            when(conceptosPagoService.create(any(ConceptosPagoCreateRequest.class))).thenReturn(testConceptoDTO);

            // When & Then
            mockMvc.perform(post("/api/v1/conceptos-pago")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testCreateRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.nombreConcepto").value("Renta Mensual"))
                    .andExpect(jsonPath("$.idInmobiliaria").value(1L));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 400 when concept name already exists")
        void shouldReturn400WhenConceptNameAlreadyExists() throws Exception {
            // Given
            when(conceptosPagoService.create(any(ConceptosPagoCreateRequest.class)))
                    .thenThrow(new IllegalArgumentException("Concept name already exists for this inmobiliaria"));

            // When & Then
            mockMvc.perform(post("/api/v1/conceptos-pago")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testCreateRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Concept name already exists for this inmobiliaria"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 400 when request is invalid")
        void shouldReturn400WhenRequestIsInvalid() throws Exception {
            // Given
            ConceptosPagoCreateRequest invalidRequest = new ConceptosPagoCreateRequest(
                    null, // Invalid: null inmobiliaria ID
                    "",   // Invalid: empty name
                    "Description",
                    "RENTA",
                    true,
                    true
            );

            // When & Then
            mockMvc.perform(post("/api/v1/conceptos-pago")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return 403 when user has insufficient permissions")
        void shouldReturn403WhenUserHasInsufficientPermissions() throws Exception {
            mockMvc.perform(post("/api/v1/conceptos-pago")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testCreateRequest)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/conceptos-pago/{id}")
    class UpdateConceptoPago {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should update concepto de pago successfully")
        void shouldUpdateConceptoPagoSuccessfully() throws Exception {
            // Given
            ConceptosPagoDTO updatedConcepto = new ConceptosPagoDTO(
                    1, 1L, "Renta Mensual Actualizada", "Pago de renta mensual actualizada", 
                    "RENTA", true, true, LocalDate.now()
            );
            when(conceptosPagoService.update(eq(1), any(ConceptosPagoUpdateRequest.class))).thenReturn(updatedConcepto);

            // When & Then
            mockMvc.perform(put("/api/v1/conceptos-pago/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUpdateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombreConcepto").value("Renta Mensual Actualizada"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 when concepto not found")
        void shouldReturn404WhenConceptoNotFound() throws Exception {
            // Given
            when(conceptosPagoService.update(eq(999), any(ConceptosPagoUpdateRequest.class)))
                    .thenThrow(new IllegalArgumentException("Concepto de pago not found with id: 999"));

            // When & Then
            mockMvc.perform(put("/api/v1/conceptos-pago/999")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUpdateRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 400 when concept name already exists")
        void shouldReturn400WhenConceptNameAlreadyExists() throws Exception {
            // Given
            when(conceptosPagoService.update(eq(1), any(ConceptosPagoUpdateRequest.class)))
                    .thenThrow(new IllegalArgumentException("Concept name already exists for this inmobiliaria"));

            // When & Then
            mockMvc.perform(put("/api/v1/conceptos-pago/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUpdateRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Concept name already exists for this inmobiliaria"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/conceptos-pago/{id}")
    class DeleteConceptoPago {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should delete concepto de pago successfully")
        void shouldDeleteConceptoPagoSuccessfully() throws Exception {
            // Given
            doNothing().when(conceptosPagoService).deleteById(1);

            // When & Then
            mockMvc.perform(delete("/api/v1/conceptos-pago/1")
                    .with(csrf()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 when concepto not found")
        void shouldReturn404WhenConceptoNotFound() throws Exception {
            // Given
            doThrow(new IllegalArgumentException("Concepto de pago not found with id: 999"))
                    .when(conceptosPagoService).deleteById(999);

            // When & Then
            mockMvc.perform(delete("/api/v1/conceptos-pago/999")
                    .with(csrf()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return 403 when user has insufficient permissions")
        void shouldReturn403WhenUserHasInsufficientPermissions() throws Exception {
            mockMvc.perform(delete("/api/v1/conceptos-pago/1")
                    .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/conceptos-pago/{id}/status")
    class ChangeConceptoPagoStatus {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should change concepto status successfully")
        void shouldChangeConceptoStatusSuccessfully() throws Exception {
            // Given
            ConceptosPagoDTO inactiveConcepto = new ConceptosPagoDTO(
                    1, 1L, "Renta Mensual", "Pago de renta mensual", 
                    "RENTA", true, false, LocalDate.now()
            );
            when(conceptosPagoService.changeStatus(1, false)).thenReturn(inactiveConcepto);

            // When & Then
            mockMvc.perform(patch("/api/v1/conceptos-pago/1/status")
                    .with(csrf())
                    .param("activo", "false"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.activo").value(false));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 when concepto not found")
        void shouldReturn404WhenConceptoNotFound() throws Exception {
            // Given
            when(conceptosPagoService.changeStatus(999, false))
                    .thenThrow(new IllegalArgumentException("Concepto de pago not found with id: 999"));

            // When & Then
            mockMvc.perform(patch("/api/v1/conceptos-pago/999/status")
                    .with(csrf())
                    .param("activo", "false"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/conceptos-pago/tipo/{tipoConcepto}")
    class GetConceptosPagoByTipo {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return conceptos by tipo")
        void shouldReturnConceptosByTipo() throws Exception {
            // Given
            Page<ConceptosPagoDTO> page = new PageImpl<>(List.of(testConceptoDTO));
            when(conceptosPagoService.findByTipoConcepto(eq("RENTA"), any(Pageable.class))).thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/v1/conceptos-pago/tipo/RENTA"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].tipoConcepto").value("RENTA"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/conceptos-pago/permite-recargos/{permiteRecargos}")
    class GetConceptosPagoByPermiteRecargos {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return conceptos by permite recargos")
        void shouldReturnConceptosByPermiteRecargos() throws Exception {
            // Given
            Page<ConceptosPagoDTO> page = new PageImpl<>(List.of(testConceptoDTO));
            when(conceptosPagoService.findByPermiteRecargos(eq(true), any(Pageable.class))).thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/v1/conceptos-pago/permite-recargos/true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].permiteRecargos").value(true));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/conceptos-pago/statistics")
    class GetStatistics {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return statistics for all conceptos")
        void shouldReturnStatisticsForAllConceptos() throws Exception {
            // Given
            List<String> tipos = Arrays.asList("RENTA", "SERVICIOS", "MANTENIMIENTO");
            when(conceptosPagoService.getDistinctTiposConcepto()).thenReturn(tipos);

            // When & Then
            mockMvc.perform(get("/api/v1/conceptos-pago/statistics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tiposConcepto").isArray())
                    .andExpect(jsonPath("$.tiposConcepto.length()").value(3));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return statistics for specific inmobiliaria")
        void shouldReturnStatisticsForSpecificInmobiliaria() throws Exception {
            // Given
            List<String> tipos = Arrays.asList("RENTA", "SERVICIOS");
            when(conceptosPagoService.countActiveByInmobiliaria(1L)).thenReturn(5L);
            when(conceptosPagoService.getDistinctTiposConceptoByInmobiliaria(1L)).thenReturn(tipos);

            // When & Then
            mockMvc.perform(get("/api/v1/conceptos-pago/statistics")
                    .param("idInmobiliaria", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.active").value(5))
                    .andExpect(jsonPath("$.tiposConcepto").isArray())
                    .andExpect(jsonPath("$.tiposConcepto.length()").value(2));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/conceptos-pago/tipos-concepto")
    class GetDistinctTiposConcepto {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return distinct tipos concepto")
        void shouldReturnDistinctTiposConcepto() throws Exception {
            // Given
            List<String> tipos = Arrays.asList("RENTA", "SERVICIOS", "MANTENIMIENTO");
            when(conceptosPagoService.getDistinctTiposConcepto()).thenReturn(tipos);

            // When & Then
            mockMvc.perform(get("/api/v1/conceptos-pago/tipos-concepto"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0]").value("RENTA"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return distinct tipos concepto by inmobiliaria")
        void shouldReturnDistinctTiposConceptoByInmobiliaria() throws Exception {
            // Given
            List<String> tipos = Arrays.asList("RENTA", "SERVICIOS");
            when(conceptosPagoService.getDistinctTiposConceptoByInmobiliaria(1L)).thenReturn(tipos);

            // When & Then
            mockMvc.perform(get("/api/v1/conceptos-pago/tipos-concepto")
                    .param("idInmobiliaria", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2));
        }
    }
}