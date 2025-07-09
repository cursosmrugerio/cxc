package com.inmobiliaria.gestion.conceptos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmobiliaria.gestion.conceptos.dto.ConceptosPagoCreateRequest;
import com.inmobiliaria.gestion.conceptos.dto.ConceptosPagoUpdateRequest;
import com.inmobiliaria.gestion.conceptos.model.ConceptosPago;
import com.inmobiliaria.gestion.conceptos.repository.ConceptosPagoRepository;
import com.inmobiliaria.gestion.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ConceptosPago Integration Tests")
class ConceptosPagoIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ConceptosPagoRepository conceptosPagoRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String adminToken;
    private String userToken;
    private ConceptosPago testConcepto1;
    private ConceptosPago testConcepto2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        // Clean repository
        conceptosPagoRepository.deleteAll();

        // Generate JWT tokens for testing
        adminToken = generateTokenForUser("admin", Arrays.asList("ROLE_ADMIN", "ROLE_USER"));
        userToken = generateTokenForUser("user", Arrays.asList("ROLE_USER"));

        // Create test data
        testConcepto1 = createTestConcepto("Renta Mensual", "Pago de renta mensual", "RENTA", 1L);
        testConcepto2 = createTestConcepto("Servicios Básicos", "Pago de servicios básicos", "SERVICIOS", 1L);
        
        testConcepto1 = conceptosPagoRepository.save(testConcepto1);
        testConcepto2 = conceptosPagoRepository.save(testConcepto2);
    }

    private String generateTokenForUser(String username, List<String> roles) {
        return jwtUtil.generateTokenFromUsername(username);
    }

    private ConceptosPago createTestConcepto(String nombre, String descripcion, String tipo, Long idInmobiliaria) {
        ConceptosPago concepto = new ConceptosPago();
        concepto.setNombreConcepto(nombre);
        concepto.setDescripcion(descripcion);
        concepto.setTipoConcepto(tipo);
        concepto.setIdInmobiliaria(idInmobiliaria);
        concepto.setPermiteRecargos(true);
        concepto.setActivo(true);
        concepto.setFechaCreacion(LocalDate.now());
        return concepto;
    }

    @Nested
    @DisplayName("GET /api/v1/conceptos-pago")
    class GetAllConceptosPago {

        @Test
        @DisplayName("Should return all conceptos de pago with user authentication")
        void shouldReturnAllConceptosPagoWithUserAuth() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].nombreConcepto").exists())
                    .andExpect(jsonPath("$.content[0].tipoConcepto").exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("Should return 401 when no authentication provided")
        void shouldReturn401WhenNoAuthProvided() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should support pagination and sorting")
        void shouldSupportPaginationAndSorting() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago")
                    .param("page", "0")
                    .param("size", "1")
                    .param("sortBy", "nombreConcepto")
                    .param("sortDir", "asc")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(2)));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/conceptos-pago/{id}")
    class GetConceptoPagoById {

        @Test
        @DisplayName("Should return concepto de pago by ID")
        void shouldReturnConceptoPagoById() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago/{id}", testConcepto1.getIdConcepto())
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.idConcepto", is(testConcepto1.getIdConcepto())))
                    .andExpect(jsonPath("$.nombreConcepto", is("Renta Mensual")))
                    .andExpect(jsonPath("$.tipoConcepto", is("RENTA")));
        }

        @Test
        @DisplayName("Should return 404 when concepto not found")
        void shouldReturn404WhenConceptoNotFound() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago/{id}", 999)
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/conceptos-pago/inmobiliaria/{idInmobiliaria}")
    class GetConceptosPagoByInmobiliaria {

        @Test
        @DisplayName("Should return conceptos by inmobiliaria ID")
        void shouldReturnConceptosByInmobiliariaId() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago/inmobiliaria/{idInmobiliaria}", 1L)
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].idInmobiliaria", is(1)))
                    .andExpect(jsonPath("$.content[1].idInmobiliaria", is(1)));
        }

        @Test
        @DisplayName("Should filter by activo status")
        void shouldFilterByActivoStatus() throws Exception {
            // Make one concepto inactive
            testConcepto2.setActivo(false);
            conceptosPagoRepository.save(testConcepto2);

            mockMvc.perform(get("/api/v1/conceptos-pago/inmobiliaria/{idInmobiliaria}", 1L)
                    .param("activo", "true")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].activo", is(true)));
        }

        @Test
        @DisplayName("Should return empty result for non-existent inmobiliaria")
        void shouldReturnEmptyResultForNonExistentInmobiliaria() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago/inmobiliaria/{idInmobiliaria}", 999L)
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/conceptos-pago/search")
    class SearchConceptosPago {

        @Test
        @DisplayName("Should search by multiple filters")
        void shouldSearchByMultipleFilters() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago/search")
                    .param("idInmobiliaria", "1")
                    .param("nombreConcepto", "Renta")
                    .param("tipoConcepto", "RENTA")
                    .param("activo", "true")
                    .param("permiteRecargos", "true")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].nombreConcepto").value("Renta Mensual"));
        }

        @Test
        @DisplayName("Should search by nombre concepto partial match")
        void shouldSearchByNombreConceptoPartialMatch() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago/search")
                    .param("nombreConcepto", "servi")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].nombreConcepto").value("Servicios Básicos"));
        }

        @Test
        @DisplayName("Should return all results when no filters provided")
        void shouldReturnAllResultsWhenNoFilters() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago/search")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/conceptos-pago")
    class CreateConceptoPago {

        @Test
        @DisplayName("Should create concepto de pago with admin authentication")
        void shouldCreateConceptoPagoWithAdminAuth() throws Exception {
            ConceptosPagoCreateRequest createRequest = new ConceptosPagoCreateRequest(
                    2L,
                    "Mantenimiento",
                    "Pago de mantenimiento",
                    "MANTENIMIENTO",
                    false,
                    true
            );

            mockMvc.perform(post("/api/v1/conceptos-pago")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.nombreConcepto", is("Mantenimiento")))
                    .andExpect(jsonPath("$.tipoConcepto", is("MANTENIMIENTO")))
                    .andExpect(jsonPath("$.idInmobiliaria", is(2)))
                    .andExpect(jsonPath("$.activo", is(true)))
                    .andExpect(jsonPath("$.permiteRecargos", is(false)));

            // Verify it was actually saved
            assertThat(conceptosPagoRepository.count()).isEqualTo(3);
        }

        @Test
        @DisplayName("Should return 403 when user tries to create concepto")
        void shouldReturn403WhenUserTriesToCreate() throws Exception {
            ConceptosPagoCreateRequest createRequest = new ConceptosPagoCreateRequest(
                    2L, "Test", "Test Description", "TEST", true, true
            );

            mockMvc.perform(post("/api/v1/conceptos-pago")
                    .header("Authorization", "Bearer " + userToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should return 400 when concept name already exists")
        void shouldReturn400WhenConceptNameAlreadyExists() throws Exception {
            ConceptosPagoCreateRequest createRequest = new ConceptosPagoCreateRequest(
                    1L,
                    "Renta Mensual", // This already exists
                    "Duplicate concept",
                    "RENTA",
                    true,
                    true
            );

            mockMvc.perform(post("/api/v1/conceptos-pago")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Concept name already exists for this inmobiliaria: Renta Mensual"));
        }

        @Test
        @DisplayName("Should return 400 when request validation fails")
        void shouldReturn400WhenRequestValidationFails() throws Exception {
            ConceptosPagoCreateRequest invalidRequest = new ConceptosPagoCreateRequest(
                    null, // Invalid: null inmobiliaria ID
                    "", // Invalid: empty name
                    "Description",
                    "RENTA",
                    true,
                    true
            );

            mockMvc.perform(post("/api/v1/conceptos-pago")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/conceptos-pago/{id}")
    class UpdateConceptoPago {

        @Test
        @DisplayName("Should update concepto de pago with admin authentication")
        void shouldUpdateConceptoPagoWithAdminAuth() throws Exception {
            ConceptosPagoUpdateRequest updateRequest = new ConceptosPagoUpdateRequest(
                    "Renta Mensual Actualizada",
                    "Pago de renta mensual actualizada",
                    "RENTA",
                    true,
                    false // Change to inactive
            );

            mockMvc.perform(put("/api/v1/conceptos-pago/{id}", testConcepto1.getIdConcepto())
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.nombreConcepto", is("Renta Mensual Actualizada")))
                    .andExpect(jsonPath("$.activo", is(false)));

            // Verify it was actually updated
            ConceptosPago updatedConcepto = conceptosPagoRepository.findById(testConcepto1.getIdConcepto()).orElse(null);
            assertThat(updatedConcepto).isNotNull();
            assertThat(updatedConcepto.getNombreConcepto()).isEqualTo("Renta Mensual Actualizada");
            assertThat(updatedConcepto.getActivo()).isFalse();
        }

        @Test
        @DisplayName("Should return 404 when concepto not found")
        void shouldReturn404WhenConceptoNotFound() throws Exception {
            ConceptosPagoUpdateRequest updateRequest = new ConceptosPagoUpdateRequest(
                    "Updated Name", "Updated Description", "UPDATED", true, true
            );

            mockMvc.perform(put("/api/v1/conceptos-pago/{id}", 999)
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 403 when user tries to update concepto")
        void shouldReturn403WhenUserTriesToUpdate() throws Exception {
            ConceptosPagoUpdateRequest updateRequest = new ConceptosPagoUpdateRequest(
                    "Updated Name", "Updated Description", "UPDATED", true, true
            );

            mockMvc.perform(put("/api/v1/conceptos-pago/{id}", testConcepto1.getIdConcepto())
                    .header("Authorization", "Bearer " + userToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/conceptos-pago/{id}")
    class DeleteConceptoPago {

        @Test
        @DisplayName("Should delete concepto de pago with admin authentication")
        void shouldDeleteConceptoPagoWithAdminAuth() throws Exception {
            Integer conceptoId = testConcepto1.getIdConcepto();

            mockMvc.perform(delete("/api/v1/conceptos-pago/{id}", conceptoId)
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNoContent());

            // Verify it was actually deleted
            assertThat(conceptosPagoRepository.findById(conceptoId)).isEmpty();
            assertThat(conceptosPagoRepository.count()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should return 404 when concepto not found")
        void shouldReturn404WhenConceptoNotFound() throws Exception {
            mockMvc.perform(delete("/api/v1/conceptos-pago/{id}", 999)
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 403 when user tries to delete concepto")
        void shouldReturn403WhenUserTriesToDelete() throws Exception {
            mockMvc.perform(delete("/api/v1/conceptos-pago/{id}", testConcepto1.getIdConcepto())
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/conceptos-pago/{id}/status")
    class ChangeConceptoPagoStatus {

        @Test
        @DisplayName("Should change concepto status with admin authentication")
        void shouldChangeConceptoStatusWithAdminAuth() throws Exception {
            mockMvc.perform(patch("/api/v1/conceptos-pago/{id}/status", testConcepto1.getIdConcepto())
                    .param("activo", "false")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.activo", is(false)));

            // Verify it was actually updated
            ConceptosPago updatedConcepto = conceptosPagoRepository.findById(testConcepto1.getIdConcepto()).orElse(null);
            assertThat(updatedConcepto).isNotNull();
            assertThat(updatedConcepto.getActivo()).isFalse();
        }

        @Test
        @DisplayName("Should return 404 when concepto not found")
        void shouldReturn404WhenConceptoNotFound() throws Exception {
            mockMvc.perform(patch("/api/v1/conceptos-pago/{id}/status", 999)
                    .param("activo", "false")
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/conceptos-pago/statistics")
    class GetStatistics {

        @Test
        @DisplayName("Should return general statistics")
        void shouldReturnGeneralStatistics() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago/statistics")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.tiposConcepto").isArray())
                    .andExpect(jsonPath("$.tiposConcepto", hasSize(2)));
        }

        @Test
        @DisplayName("Should return statistics for specific inmobiliaria")
        void shouldReturnStatisticsForSpecificInmobiliaria() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago/statistics")
                    .param("idInmobiliaria", "1")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.active", is(2)))
                    .andExpect(jsonPath("$.tiposConcepto").isArray())
                    .andExpect(jsonPath("$.tiposConcepto", hasSize(2)));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/conceptos-pago/tipos-concepto")
    class GetDistinctTiposConcepto {

        @Test
        @DisplayName("Should return distinct tipos concepto")
        void shouldReturnDistinctTiposConcepto() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago/tipos-concepto")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("Should return tipos concepto for specific inmobiliaria")
        void shouldReturnTiposConceptoForSpecificInmobiliaria() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago/tipos-concepto")
                    .param("idInmobiliaria", "1")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(2)));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/conceptos-pago/tipo/{tipoConcepto}")
    class GetConceptosPagoByTipo {

        @Test
        @DisplayName("Should return conceptos by tipo concepto")
        void shouldReturnConceptosByTipoConcepto() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago/tipo/{tipoConcepto}", "RENTA")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].tipoConcepto", is("RENTA")));
        }

        @Test
        @DisplayName("Should return empty result for non-existent tipo")
        void shouldReturnEmptyResultForNonExistentTipo() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago/tipo/{tipoConcepto}", "NONEXISTENT")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/conceptos-pago/permite-recargos/{permiteRecargos}")
    class GetConceptosPagoByPermiteRecargos {

        @Test
        @DisplayName("Should return conceptos by permite recargos")
        void shouldReturnConceptosByPermiteRecargos() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago/permite-recargos/{permiteRecargos}", "true")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].permiteRecargos", is(true)))
                    .andExpect(jsonPath("$.content[1].permiteRecargos", is(true)));
        }

        @Test
        @DisplayName("Should return empty result when no conceptos match")
        void shouldReturnEmptyResultWhenNoConceptosMatch() throws Exception {
            mockMvc.perform(get("/api/v1/conceptos-pago/permite-recargos/{permiteRecargos}", "false")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)));
        }
    }
}