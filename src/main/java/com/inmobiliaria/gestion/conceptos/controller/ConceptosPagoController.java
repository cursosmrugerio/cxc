package com.inmobiliaria.gestion.conceptos.controller;

import com.inmobiliaria.gestion.conceptos.dto.ConceptosPagoCreateRequest;
import com.inmobiliaria.gestion.conceptos.dto.ConceptosPagoDTO;
import com.inmobiliaria.gestion.conceptos.dto.ConceptosPagoUpdateRequest;
import com.inmobiliaria.gestion.conceptos.service.ConceptosPagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/conceptos-pago")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Conceptos de Pago", description = "Payment concepts management operations")
@SecurityRequirement(name = "Bearer Authentication")
public class ConceptosPagoController {

    private final ConceptosPagoService conceptosPagoService;

    @Operation(summary = "Get all payment concepts", description = "Retrieve a list of all payment concepts with optional pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved payment concepts",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<ConceptosPagoDTO>> getAllConceptosPago(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field", example = "nombreConcepto")
            @RequestParam(defaultValue = "nombreConcepto") String sortBy,
            @Parameter(description = "Sort direction", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Getting all conceptos de pago - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ConceptosPagoDTO> conceptos = conceptosPagoService.findAll(pageable);
        
        return ResponseEntity.ok(conceptos);
    }

    @Operation(summary = "Get payment concept by ID", description = "Retrieve a specific payment concept by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment concept found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConceptosPagoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Payment concept not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ConceptosPagoDTO> getConceptoPagoById(
            @Parameter(description = "Payment concept ID", required = true, example = "1")
            @PathVariable Integer id) {
        
        log.info("Getting concepto de pago by id: {}", id);
        
        return conceptosPagoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get payment concepts by inmobiliaria", description = "Retrieve payment concepts for a specific inmobiliaria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment concepts found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/inmobiliaria/{idInmobiliaria}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<ConceptosPagoDTO>> getConceptosPagoByInmobiliaria(
            @Parameter(description = "Inmobiliaria ID", required = true, example = "1")
            @PathVariable Long idInmobiliaria,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean activo,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "nombreConcepto") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Getting conceptos de pago by inmobiliaria: {}, activo: {}", idInmobiliaria, activo);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ConceptosPagoDTO> conceptos = (activo != null) 
                ? conceptosPagoService.findByInmobiliariaAndActivo(idInmobiliaria, activo, pageable)
                : conceptosPagoService.findByInmobiliaria(idInmobiliaria, pageable);
        
        return ResponseEntity.ok(conceptos);
    }

    @Operation(summary = "Search payment concepts", description = "Search payment concepts with various filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<ConceptosPagoDTO>> searchConceptosPago(
            @Parameter(description = "Inmobiliaria ID to filter by") @RequestParam(required = false) Long idInmobiliaria,
            @Parameter(description = "Concept name to search for") @RequestParam(required = false) String nombreConcepto,
            @Parameter(description = "Concept type to filter by") @RequestParam(required = false) String tipoConcepto,
            @Parameter(description = "Active status to filter by") @RequestParam(required = false) Boolean activo,
            @Parameter(description = "Allows charges filter") @RequestParam(required = false) Boolean permiteRecargos,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "nombreConcepto") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Searching conceptos de pago with filters - inmobiliaria: {}, nombre: {}, tipo: {}, activo: {}, permite recargos: {}", 
                idInmobiliaria, nombreConcepto, tipoConcepto, activo, permiteRecargos);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ConceptosPagoDTO> conceptos = conceptosPagoService.findByFilters(
                idInmobiliaria, nombreConcepto, tipoConcepto, activo, permiteRecargos, pageable);
        
        return ResponseEntity.ok(conceptos);
    }

    @Operation(summary = "Create new payment concept", description = "Create a new payment concept")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment concept created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConceptosPagoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Concept name already exists for this inmobiliaria"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createConceptoPago(
            @Parameter(description = "Payment concept data", required = true)
            @Valid @RequestBody ConceptosPagoCreateRequest request) {
        
        log.info("Creating new concepto de pago: {}", request.nombreConcepto());
        
        try {
            ConceptosPagoDTO createdConcepto = conceptosPagoService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdConcepto);
        } catch (IllegalArgumentException e) {
            log.warn("Error creating concepto de pago: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Update payment concept", description = "Update an existing payment concept")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment concept updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Payment concept not found"),
            @ApiResponse(responseCode = "409", description = "Concept name already exists for this inmobiliaria"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateConceptoPago(
            @Parameter(description = "Payment concept ID", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Updated payment concept data", required = true)
            @Valid @RequestBody ConceptosPagoUpdateRequest request) {
        
        log.info("Updating concepto de pago with id: {}", id);
        
        try {
            ConceptosPagoDTO updatedConcepto = conceptosPagoService.update(id, request);
            return ResponseEntity.ok(updatedConcepto);
        } catch (IllegalArgumentException e) {
            log.warn("Error updating concepto de pago with id {}: {}", id, e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Delete payment concept", description = "Delete a payment concept by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Payment concept deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Payment concept not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteConceptoPago(
            @Parameter(description = "Payment concept ID", required = true)
            @PathVariable Integer id) {
        
        log.info("Deleting concepto de pago with id: {}", id);
        
        try {
            conceptosPagoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Error deleting concepto de pago with id {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Change payment concept status", description = "Change the active status of a payment concept")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status changed successfully"),
            @ApiResponse(responseCode = "404", description = "Payment concept not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeConceptoPagoStatus(
            @Parameter(description = "Payment concept ID", required = true)
            @PathVariable Integer id,
            @Parameter(description = "New active status", required = true)
            @RequestParam Boolean activo) {
        
        log.info("Changing status of concepto de pago with id: {} to: {}", id, activo);
        
        try {
            ConceptosPagoDTO updatedConcepto = conceptosPagoService.changeStatus(id, activo);
            return ResponseEntity.ok(updatedConcepto);
        } catch (IllegalArgumentException e) {
            log.warn("Error changing status of concepto de pago with id {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get payment concepts by type", description = "Get all payment concepts with a specific type")
    @GetMapping("/tipo/{tipoConcepto}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<ConceptosPagoDTO>> getConceptosPagoByTipo(
            @Parameter(description = "Concept type to filter by", required = true)
            @PathVariable String tipoConcepto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreConcepto") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Getting conceptos de pago by tipo: {}", tipoConcepto);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ConceptosPagoDTO> conceptos = conceptosPagoService.findByTipoConcepto(tipoConcepto, pageable);
        
        return ResponseEntity.ok(conceptos);
    }

    @Operation(summary = "Get payment concepts by charges permission", description = "Get all payment concepts that allow or don't allow charges")
    @GetMapping("/permite-recargos/{permiteRecargos}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<ConceptosPagoDTO>> getConceptosPagoByPermiteRecargos(
            @Parameter(description = "Whether concepts allow charges", required = true)
            @PathVariable Boolean permiteRecargos,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreConcepto") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Getting conceptos de pago by permite recargos: {}", permiteRecargos);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ConceptosPagoDTO> conceptos = conceptosPagoService.findByPermiteRecargos(permiteRecargos, pageable);
        
        return ResponseEntity.ok(conceptos);
    }

    @Operation(summary = "Get statistics", description = "Get statistics about payment concepts")
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStatistics(
            @Parameter(description = "Inmobiliaria ID to filter statistics") @RequestParam(required = false) Long idInmobiliaria) {
        
        log.info("Getting conceptos de pago statistics for inmobiliaria: {}", idInmobiliaria);
        
        Map<String, Object> statistics;
        
        if (idInmobiliaria != null) {
            statistics = Map.of(
                    "active", conceptosPagoService.countActiveByInmobiliaria(idInmobiliaria),
                    "tiposConcepto", conceptosPagoService.getDistinctTiposConceptoByInmobiliaria(idInmobiliaria)
            );
        } else {
            statistics = Map.of(
                    "tiposConcepto", conceptosPagoService.getDistinctTiposConcepto()
            );
        }
        
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Get distinct concept types", description = "Get all distinct concept types")
    @GetMapping("/tipos-concepto")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<String>> getDistinctTiposConcepto(
            @Parameter(description = "Inmobiliaria ID to filter types") @RequestParam(required = false) Long idInmobiliaria) {
        
        log.info("Getting distinct tipos concepto for inmobiliaria: {}", idInmobiliaria);
        
        List<String> tipos = (idInmobiliaria != null) 
                ? conceptosPagoService.getDistinctTiposConceptoByInmobiliaria(idInmobiliaria)
                : conceptosPagoService.getDistinctTiposConcepto();
        
        return ResponseEntity.ok(tipos);
    }
}