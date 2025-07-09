package com.inmobiliaria.gestion.contrato_renta.controller;

import com.inmobiliaria.gestion.contrato_renta.dto.ContratoRentaCreateRequest;
import com.inmobiliaria.gestion.contrato_renta.dto.ContratoRentaDTO;
import com.inmobiliaria.gestion.contrato_renta.dto.ContratoRentaUpdateRequest;
import com.inmobiliaria.gestion.contrato_renta.service.ContratoRentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/contratos-renta")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contratos de Renta", description = "Rental contract management operations")
public class ContratoRentaController {

    private final ContratoRentaService contratoRentaService;

    @Operation(summary = "Get all rental contracts", description = "Retrieve a list of all rental contracts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contracts retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContratoRentaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<ContratoRentaDTO>> getAllContratos() {
        log.info("GET /api/v1/contratos-renta - Fetching all rental contracts");
        List<ContratoRentaDTO> contratos = contratoRentaService.getAllContratos();
        return ResponseEntity.ok(contratos);
    }

    @Operation(summary = "Get rental contract by ID", description = "Retrieve a specific rental contract by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContratoRentaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Contract not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ContratoRentaDTO> getContratoById(
            @Parameter(description = "Contract ID", required = true)
            @PathVariable Integer id) {
        log.info("GET /api/v1/contratos-renta/{} - Fetching contract by id", id);
        Optional<ContratoRentaDTO> contrato = contratoRentaService.getContratoById(id);
        return contrato.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get contracts by property", description = "Retrieve all contracts for a specific property")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contracts retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContratoRentaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/propiedad/{idPropiedad}")
    public ResponseEntity<List<ContratoRentaDTO>> getContratosByPropiedad(
            @Parameter(description = "Property ID", required = true)
            @PathVariable Integer idPropiedad) {
        log.info("GET /api/v1/contratos-renta/propiedad/{} - Fetching contracts by property", idPropiedad);
        List<ContratoRentaDTO> contratos = contratoRentaService.getContratosByPropiedad(idPropiedad);
        return ResponseEntity.ok(contratos);
    }

    @Operation(summary = "Get contracts by status", description = "Retrieve all contracts with a specific status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contracts retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContratoRentaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/estatus/{estatus}")
    public ResponseEntity<List<ContratoRentaDTO>> getContratosByEstatus(
            @Parameter(description = "Contract status", required = true)
            @PathVariable String estatus) {
        log.info("GET /api/v1/contratos-renta/estatus/{} - Fetching contracts by status", estatus);
        List<ContratoRentaDTO> contratos = contratoRentaService.getContratosByEstatus(estatus);
        return ResponseEntity.ok(contratos);
    }

    @Operation(summary = "Get contracts expiring between dates", description = "Retrieve contracts expiring within a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contracts retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContratoRentaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/expirando")
    public ResponseEntity<List<ContratoRentaDTO>> getContratosExpiringBetween(
            @Parameter(description = "Start date (yyyy-MM-dd'T'HH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (yyyy-MM-dd'T'HH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("GET /api/v1/contratos-renta/expirando - Fetching contracts expiring between {} and {}", startDate, endDate);
        List<ContratoRentaDTO> contratos = contratoRentaService.getContratosExpiringBetween(startDate, endDate);
        return ResponseEntity.ok(contratos);
    }

    @Operation(summary = "Get active contracts expiring soon", description = "Retrieve active contracts expiring before a specific date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contracts retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContratoRentaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/activos/expirando")
    public ResponseEntity<List<ContratoRentaDTO>> getActiveContractsExpiringBefore(
            @Parameter(description = "Date threshold (yyyy-MM-dd'T'HH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        log.info("GET /api/v1/contratos-renta/activos/expirando - Fetching active contracts expiring before {}", date);
        List<ContratoRentaDTO> contratos = contratoRentaService.getActiveContractsExpiringBefore(date);
        return ResponseEntity.ok(contratos);
    }

    @Operation(summary = "Get contracts needing notification", description = "Retrieve contracts that need expiration notifications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contracts retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContratoRentaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/notificaciones")
    public ResponseEntity<List<ContratoRentaDTO>> getContractsNeedingNotification() {
        log.info("GET /api/v1/contratos-renta/notificaciones - Fetching contracts needing notification");
        List<ContratoRentaDTO> contratos = contratoRentaService.getContractsNeedingNotification();
        return ResponseEntity.ok(contratos);
    }

    @Operation(summary = "Create a new rental contract", description = "Create a new rental contract with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contract created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContratoRentaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or property already has active contract"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ContratoRentaDTO> createContrato(
            @Parameter(description = "Contract creation request", required = true)
            @Valid @RequestBody ContratoRentaCreateRequest request) {
        log.info("POST /api/v1/contratos-renta - Creating new rental contract");
        try {
            ContratoRentaDTO createdContrato = contratoRentaService.createContrato(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdContrato);
        } catch (IllegalArgumentException e) {
            log.error("Error creating contract: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update an existing rental contract", description = "Update an existing rental contract with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContratoRentaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Contract not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ContratoRentaDTO> updateContrato(
            @Parameter(description = "Contract ID", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Contract update request", required = true)
            @Valid @RequestBody ContratoRentaUpdateRequest request) {
        log.info("PUT /api/v1/contratos-renta/{} - Updating rental contract", id);
        Optional<ContratoRentaDTO> updatedContrato = contratoRentaService.updateContrato(id, request);
        return updatedContrato.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a rental contract", description = "Delete a rental contract by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contract deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Contract not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContrato(
            @Parameter(description = "Contract ID", required = true)
            @PathVariable Integer id) {
        log.info("DELETE /api/v1/contratos-renta/{} - Deleting rental contract", id);
        boolean deleted = contratoRentaService.deleteContrato(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Terminate a rental contract", description = "Terminate an active rental contract")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract terminated successfully"),
            @ApiResponse(responseCode = "404", description = "Contract not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{id}/terminar")
    public ResponseEntity<Void> terminateContrato(
            @Parameter(description = "Contract ID", required = true)
            @PathVariable Integer id) {
        log.info("PATCH /api/v1/contratos-renta/{}/terminar - Terminating rental contract", id);
        boolean terminated = contratoRentaService.terminateContrato(id);
        return terminated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Renew a rental contract", description = "Renew an active rental contract for additional months")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract renewed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or contract cannot be renewed"),
            @ApiResponse(responseCode = "404", description = "Contract not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{id}/renovar")
    public ResponseEntity<Void> renewContrato(
            @Parameter(description = "Contract ID", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Additional months for renewal", required = true)
            @RequestParam Integer meses) {
        log.info("PATCH /api/v1/contratos-renta/{}/renovar - Renewing rental contract for {} months", id, meses);
        try {
            boolean renewed = contratoRentaService.renewContrato(id, meses);
            return renewed ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error("Error renewing contract: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get contract count by status", description = "Get the total count of contracts for a specific status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/estatus/{estatus}/count")
    public ResponseEntity<Long> countContratosByEstatus(
            @Parameter(description = "Contract status", required = true)
            @PathVariable String estatus) {
        log.info("GET /api/v1/contratos-renta/estatus/{}/count - Counting contracts by status", estatus);
        long count = contratoRentaService.countContratosByEstatus(estatus);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Get count of active contracts expiring soon", description = "Get the count of active contracts expiring before a specific date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/activos/expirando/count")
    public ResponseEntity<Long> countActiveContractsExpiringBefore(
            @Parameter(description = "Date threshold (yyyy-MM-dd'T'HH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        log.info("GET /api/v1/contratos-renta/activos/expirando/count - Counting active contracts expiring before {}", date);
        long count = contratoRentaService.countActiveContractsExpiringBefore(date);
        return ResponseEntity.ok(count);
    }
}