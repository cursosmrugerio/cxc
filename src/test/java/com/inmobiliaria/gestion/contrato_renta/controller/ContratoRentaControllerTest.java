package com.inmobiliaria.gestion.contrato_renta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmobiliaria.gestion.contrato_renta.dto.ContratoRentaCreateRequest;
import com.inmobiliaria.gestion.contrato_renta.dto.ContratoRentaDTO;
import com.inmobiliaria.gestion.contrato_renta.dto.ContratoRentaUpdateRequest;
import com.inmobiliaria.gestion.contrato_renta.service.ContratoRentaService;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContratoRentaController.class)
@DisplayName("ContratoRentaController Tests")
class ContratoRentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContratoRentaService contratoRentaService;

    private ContratoRentaDTO testContratoDTO;
    private ContratoRentaCreateRequest testCreateRequest;
    private ContratoRentaUpdateRequest testUpdateRequest;

    @BeforeEach
    void setUp() {
        testContratoDTO = createTestContratoDTO();
        testCreateRequest = createTestCreateRequest();
        testUpdateRequest = createTestUpdateRequest();
    }

    private ContratoRentaDTO createTestContratoDTO() {
        return new ContratoRentaDTO(
                1,
                1,
                LocalDateTime.of(2024, 1, 15, 10, 0),
                LocalDateTime.of(2024, 12, 15, 10, 0),
                "No pets allowed",
                "tenant@example.com",
                "ACTIVO",
                BigDecimal.valueOf(5000.00),
                12,
                30,
                "+52 55 1234 5678"
        );
    }

    private ContratoRentaCreateRequest createTestCreateRequest() {
        return new ContratoRentaCreateRequest(
                1,
                LocalDateTime.of(2024, 1, 15, 10, 0),
                "No pets allowed",
                "tenant@example.com",
                BigDecimal.valueOf(5000.00),
                12,
                30,
                "+52 55 1234 5678"
        );
    }

    private ContratoRentaUpdateRequest createTestUpdateRequest() {
        return new ContratoRentaUpdateRequest(
                LocalDateTime.of(2024, 1, 15, 10, 0),
                "No pets allowed, No smoking",
                "newtenant@example.com",
                "ACTIVO",
                BigDecimal.valueOf(6000.00),
                15,
                45,
                "+52 55 9876 5432"
        );
    }

    @Nested
    @DisplayName("GET /api/v1/contratos-renta")
    class GetAllContratos {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return all contracts successfully")
        void shouldReturnAllContractsSuccessfully() throws Exception {
            List<ContratoRentaDTO> contratos = Arrays.asList(testContratoDTO);
            when(contratoRentaService.getAllContratos()).thenReturn(contratos);

            mockMvc.perform(get("/api/v1/contratos-renta"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].idContrato").value(1))
                    .andExpect(jsonPath("$[0].estatusContrato").value("ACTIVO"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should allow user role to read contracts")
        void shouldAllowUserRoleToReadContracts() throws Exception {
            List<ContratoRentaDTO> contratos = Arrays.asList(testContratoDTO);
            when(contratoRentaService.getAllContratos()).thenReturn(contratos);

            mockMvc.perform(get("/api/v1/contratos-renta"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return empty list when no contracts exist")
        void shouldReturnEmptyListWhenNoContractsExist() throws Exception {
            when(contratoRentaService.getAllContratos()).thenReturn(Arrays.asList());

            mockMvc.perform(get("/api/v1/contratos-renta"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/contratos-renta/{id}")
    class GetContratoById {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return contract by id when exists")
        void shouldReturnContractByIdWhenExists() throws Exception {
            when(contratoRentaService.getContratoById(1)).thenReturn(Optional.of(testContratoDTO));

            mockMvc.perform(get("/api/v1/contratos-renta/{id}", 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idContrato").value(1))
                    .andExpect(jsonPath("$.idPropiedad").value(1))
                    .andExpect(jsonPath("$.estatusContrato").value("ACTIVO"))
                    .andExpect(jsonPath("$.emailNotificaciones").value("tenant@example.com"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return not found when contract does not exist")
        void shouldReturnNotFoundWhenContractDoesNotExist() throws Exception {
            when(contratoRentaService.getContratoById(999)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/v1/contratos-renta/{id}", 999))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should handle invalid id parameter")
        void shouldHandleInvalidIdParameter() throws Exception {
            mockMvc.perform(get("/api/v1/contratos-renta/{id}", "invalid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/contratos-renta/propiedad/{idPropiedad}")
    class GetContratosByPropiedad {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return contracts by property")
        void shouldReturnContractsByProperty() throws Exception {
            List<ContratoRentaDTO> contratos = Arrays.asList(testContratoDTO);
            when(contratoRentaService.getContratosByPropiedad(1)).thenReturn(contratos);

            mockMvc.perform(get("/api/v1/contratos-renta/propiedad/{idPropiedad}", 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].idPropiedad").value(1));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return empty list when property has no contracts")
        void shouldReturnEmptyListWhenPropertyHasNoContracts() throws Exception {
            when(contratoRentaService.getContratosByPropiedad(999)).thenReturn(Arrays.asList());

            mockMvc.perform(get("/api/v1/contratos-renta/propiedad/{idPropiedad}", 999))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/contratos-renta/estatus/{estatus}")
    class GetContratosByEstatus {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return contracts by status")
        void shouldReturnContractsByStatus() throws Exception {
            List<ContratoRentaDTO> contratos = Arrays.asList(testContratoDTO);
            when(contratoRentaService.getContratosByEstatus("ACTIVO")).thenReturn(contratos);

            mockMvc.perform(get("/api/v1/contratos-renta/estatus/{estatus}", "ACTIVO"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].estatusContrato").value("ACTIVO"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return empty list for status with no contracts")
        void shouldReturnEmptyListForStatusWithNoContracts() throws Exception {
            when(contratoRentaService.getContratosByEstatus("SUSPENDIDO")).thenReturn(Arrays.asList());

            mockMvc.perform(get("/api/v1/contratos-renta/estatus/{estatus}", "SUSPENDIDO"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/contratos-renta/expirando")
    class GetContratosExpiringBetween {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return contracts expiring between dates")
        void shouldReturnContractsExpiringBetweenDates() throws Exception {
            List<ContratoRentaDTO> contratos = Arrays.asList(testContratoDTO);
            LocalDateTime startDate = LocalDateTime.of(2024, 11, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);
            
            when(contratoRentaService.getContratosExpiringBetween(startDate, endDate)).thenReturn(contratos);

            mockMvc.perform(get("/api/v1/contratos-renta/expirando")
                            .param("startDate", "2024-11-01T00:00:00")
                            .param("endDate", "2024-12-31T23:59:00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return bad request for invalid date format")
        void shouldReturnBadRequestForInvalidDateFormat() throws Exception {
            mockMvc.perform(get("/api/v1/contratos-renta/expirando")
                            .param("startDate", "invalid-date")
                            .param("endDate", "2024-12-31T23:59:00"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return bad request when missing required parameters")
        void shouldReturnBadRequestWhenMissingRequiredParameters() throws Exception {
            mockMvc.perform(get("/api/v1/contratos-renta/expirando")
                            .param("startDate", "2024-11-01T00:00:00"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/contratos-renta/activos/expirando")
    class GetActiveContractsExpiringBefore {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return active contracts expiring before date")
        void shouldReturnActiveContractsExpiringBeforeDate() throws Exception {
            List<ContratoRentaDTO> contratos = Arrays.asList(testContratoDTO);
            LocalDateTime date = LocalDateTime.of(2024, 12, 31, 23, 59);
            
            when(contratoRentaService.getActiveContractsExpiringBefore(date)).thenReturn(contratos);

            mockMvc.perform(get("/api/v1/contratos-renta/activos/expirando")
                            .param("date", "2024-12-31T23:59:00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/contratos-renta/notificaciones")
    class GetContractsNeedingNotification {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return contracts needing notification")
        void shouldReturnContractsNeedingNotification() throws Exception {
            List<ContratoRentaDTO> contratos = Arrays.asList(testContratoDTO);
            when(contratoRentaService.getContractsNeedingNotification()).thenReturn(contratos);

            mockMvc.perform(get("/api/v1/contratos-renta/notificaciones"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/contratos-renta")
    class CreateContrato {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should create contract successfully")
        void shouldCreateContractSuccessfully() throws Exception {
            when(contratoRentaService.createContrato(any(ContratoRentaCreateRequest.class)))
                    .thenReturn(testContratoDTO);

            mockMvc.perform(post("/api/v1/contratos-renta")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testCreateRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idContrato").value(1))
                    .andExpect(jsonPath("$.idPropiedad").value(1))
                    .andExpect(jsonPath("$.estatusContrato").value("ACTIVO"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return bad request for invalid input")
        void shouldReturnBadRequestForInvalidInput() throws Exception {
            ContratoRentaCreateRequest invalidRequest = new ContratoRentaCreateRequest(
                    null, // Invalid: null property ID
                    null, // Invalid: null start date
                    null,
                    "invalid-email", // Invalid email format
                    BigDecimal.valueOf(-100), // Invalid: negative deposit
                    0, // Invalid: zero duration
                    0, // Invalid: zero notification days
                    "invalid-phone" // Invalid phone format
            );

            mockMvc.perform(post("/api/v1/contratos-renta")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should handle service exceptions")
        void shouldHandleServiceExceptions() throws Exception {
            when(contratoRentaService.createContrato(any(ContratoRentaCreateRequest.class)))
                    .thenThrow(new IllegalArgumentException("Property already has active contract"));

            mockMvc.perform(post("/api/v1/contratos-renta")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testCreateRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should require authentication")
        void shouldRequireAuthentication() throws Exception {
            mockMvc.perform(post("/api/v1/contratos-renta")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testCreateRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should require admin role for create operations")
        void shouldRequireAdminRoleForCreateOperations() throws Exception {
            mockMvc.perform(post("/api/v1/contratos-renta")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testCreateRequest)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/contratos-renta/{id}")
    class UpdateContrato {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should update contract successfully")
        void shouldUpdateContractSuccessfully() throws Exception {
            ContratoRentaDTO updatedContrato = new ContratoRentaDTO(
                    1, 1, LocalDateTime.of(2024, 1, 15, 10, 0),
                    LocalDateTime.of(2025, 3, 15, 10, 0), // Updated end date
                    "No pets allowed, No smoking", "newtenant@example.com", "ACTIVO",
                    BigDecimal.valueOf(6000.00), 15, 45, "+52 55 9876 5432"
            );

            when(contratoRentaService.updateContrato(eq(1), any(ContratoRentaUpdateRequest.class)))
                    .thenReturn(Optional.of(updatedContrato));

            mockMvc.perform(put("/api/v1/contratos-renta/{id}", 1)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testUpdateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.emailNotificaciones").value("newtenant@example.com"))
                    .andExpect(jsonPath("$.duracionMeses").value(15))
                    .andExpect(jsonPath("$.depositoGarantia").value(6000.00));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return not found when contract does not exist")
        void shouldReturnNotFoundWhenContractDoesNotExist() throws Exception {
            when(contratoRentaService.updateContrato(eq(999), any(ContratoRentaUpdateRequest.class)))
                    .thenReturn(Optional.empty());

            mockMvc.perform(put("/api/v1/contratos-renta/{id}", 999)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testUpdateRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should require admin role for update operations")
        void shouldRequireAdminRoleForUpdateOperations() throws Exception {
            mockMvc.perform(put("/api/v1/contratos-renta/{id}", 1)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testUpdateRequest)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/contratos-renta/{id}")
    class DeleteContrato {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should delete contract successfully")
        void shouldDeleteContractSuccessfully() throws Exception {
            when(contratoRentaService.deleteContrato(1)).thenReturn(true);

            mockMvc.perform(delete("/api/v1/contratos-renta/{id}", 1)
                            .with(csrf()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return not found when contract does not exist")
        void shouldReturnNotFoundWhenContractDoesNotExist() throws Exception {
            when(contratoRentaService.deleteContrato(999)).thenReturn(false);

            mockMvc.perform(delete("/api/v1/contratos-renta/{id}", 999)
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should require admin role for delete operations")
        void shouldRequireAdminRoleForDeleteOperations() throws Exception {
            mockMvc.perform(delete("/api/v1/contratos-renta/{id}", 1)
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/contratos-renta/{id}/terminar")
    class TerminateContrato {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should terminate contract successfully")
        void shouldTerminateContractSuccessfully() throws Exception {
            when(contratoRentaService.terminateContrato(1)).thenReturn(true);

            mockMvc.perform(patch("/api/v1/contratos-renta/{id}/terminar", 1)
                            .with(csrf()))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return not found when contract does not exist")
        void shouldReturnNotFoundWhenContractDoesNotExist() throws Exception {
            when(contratoRentaService.terminateContrato(999)).thenReturn(false);

            mockMvc.perform(patch("/api/v1/contratos-renta/{id}/terminar", 999)
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should require admin role for terminate operations")
        void shouldRequireAdminRoleForTerminateOperations() throws Exception {
            mockMvc.perform(patch("/api/v1/contratos-renta/{id}/terminar", 1)
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/contratos-renta/{id}/renovar")
    class RenewContrato {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should renew contract successfully")
        void shouldRenewContractSuccessfully() throws Exception {
            when(contratoRentaService.renewContrato(1, 6)).thenReturn(true);

            mockMvc.perform(patch("/api/v1/contratos-renta/{id}/renovar", 1)
                            .with(csrf())
                            .param("meses", "6"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return not found when contract does not exist")
        void shouldReturnNotFoundWhenContractDoesNotExist() throws Exception {
            when(contratoRentaService.renewContrato(999, 6)).thenReturn(false);

            mockMvc.perform(patch("/api/v1/contratos-renta/{id}/renovar", 999)
                            .with(csrf())
                            .param("meses", "6"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should handle service exceptions during renewal")
        void shouldHandleServiceExceptionsDuringRenewal() throws Exception {
            when(contratoRentaService.renewContrato(1, 6))
                    .thenThrow(new IllegalArgumentException("Contract cannot be renewed"));

            mockMvc.perform(patch("/api/v1/contratos-renta/{id}/renovar", 1)
                            .with(csrf())
                            .param("meses", "6"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return bad request for missing months parameter")
        void shouldReturnBadRequestForMissingMonthsParameter() throws Exception {
            mockMvc.perform(patch("/api/v1/contratos-renta/{id}/renovar", 1)
                            .with(csrf()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should require admin role for renew operations")
        void shouldRequireAdminRoleForRenewOperations() throws Exception {
            mockMvc.perform(patch("/api/v1/contratos-renta/{id}/renovar", 1)
                            .with(csrf())
                            .param("meses", "6"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/contratos-renta/estatus/{estatus}/count")
    class CountContratosByEstatus {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return count of contracts by status")
        void shouldReturnCountOfContractsByStatus() throws Exception {
            when(contratoRentaService.countContratosByEstatus("ACTIVO")).thenReturn(5L);

            mockMvc.perform(get("/api/v1/contratos-renta/estatus/{estatus}/count", "ACTIVO"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("5"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return zero for status with no contracts")
        void shouldReturnZeroForStatusWithNoContracts() throws Exception {
            when(contratoRentaService.countContratosByEstatus("SUSPENDIDO")).thenReturn(0L);

            mockMvc.perform(get("/api/v1/contratos-renta/estatus/{estatus}/count", "SUSPENDIDO"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("0"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/contratos-renta/activos/expirando/count")
    class CountActiveContractsExpiringBefore {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return count of active contracts expiring before date")
        void shouldReturnCountOfActiveContractsExpiringBeforeDate() throws Exception {
            LocalDateTime date = LocalDateTime.of(2024, 12, 31, 23, 59);
            when(contratoRentaService.countActiveContractsExpiringBefore(date)).thenReturn(3L);

            mockMvc.perform(get("/api/v1/contratos-renta/activos/expirando/count")
                            .param("date", "2024-12-31T23:59:00"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("3"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return bad request for invalid date format")
        void shouldReturnBadRequestForInvalidDateFormat() throws Exception {
            mockMvc.perform(get("/api/v1/contratos-renta/activos/expirando/count")
                            .param("date", "invalid-date"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should handle malformed JSON")
        void shouldHandleMalformedJson() throws Exception {
            mockMvc.perform(post("/api/v1/contratos-renta")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid json"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should handle missing content type")
        void shouldHandleMissingContentType() throws Exception {
            mockMvc.perform(post("/api/v1/contratos-renta")
                            .with(csrf())
                            .content(objectMapper.writeValueAsString(testCreateRequest)))
                    .andExpect(status().isUnsupportedMediaType());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should handle invalid path parameters")
        void shouldHandleInvalidPathParameters() throws Exception {
            mockMvc.perform(get("/api/v1/contratos-renta/{id}", "invalid-id"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should handle empty request body")
        void shouldHandleEmptyRequestBody() throws Exception {
            mockMvc.perform(post("/api/v1/contratos-renta")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(""))
                    .andExpect(status().isBadRequest());
        }
    }
}