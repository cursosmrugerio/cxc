package com.inmobiliaria.gestion.inmobiliaria.controller;

import com.inmobiliaria.gestion.inmobiliaria.dto.InmobiliariaDTO;
import com.inmobiliaria.gestion.inmobiliaria.service.InmobiliariaService;
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
@RequestMapping("/api/v1/inmobiliarias")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inmobiliaria", description = "Inmobiliaria management operations")
@SecurityRequirement(name = "Bearer Authentication")
public class InmobiliariaController {

    private final InmobiliariaService inmobiliariaService;

    @Operation(summary = "Get all inmobiliarias", description = "Retrieve a list of all inmobiliarias with optional pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved inmobiliarias",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<InmobiliariaDTO>> getAllInmobiliarias(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field", example = "nombreComercial")
            @RequestParam(defaultValue = "nombreComercial") String sortBy,
            @Parameter(description = "Sort direction", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Getting all inmobiliarias - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InmobiliariaDTO> inmobiliarias = inmobiliariaService.findAll(pageable);
        
        return ResponseEntity.ok(inmobiliarias);
    }

    @Operation(summary = "Get inmobiliaria by ID", description = "Retrieve a specific inmobiliaria by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inmobiliaria found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InmobiliariaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Inmobiliaria not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<InmobiliariaDTO> getInmobiliariaById(
            @Parameter(description = "Inmobiliaria ID", required = true, example = "1")
            @PathVariable Long id) {
        
        log.info("Getting inmobiliaria by id: {}", id);
        
        return inmobiliariaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get inmobiliaria by RFC/NIT", description = "Retrieve a specific inmobiliaria by its RFC/NIT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inmobiliaria found"),
            @ApiResponse(responseCode = "404", description = "Inmobiliaria not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/rfc/{rfcNit}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<InmobiliariaDTO> getInmobiliariaByRfcNit(
            @Parameter(description = "RFC/NIT", required = true, example = "ABC123456789")
            @PathVariable String rfcNit) {
        
        log.info("Getting inmobiliaria by RFC/NIT: {}", rfcNit);
        
        return inmobiliariaService.findByRfcNit(rfcNit)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Search inmobiliarias", description = "Search inmobiliarias with various filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<InmobiliariaDTO>> searchInmobiliarias(
            @Parameter(description = "Commercial name to search for") @RequestParam(required = false) String nombreComercial,
            @Parameter(description = "City to filter by") @RequestParam(required = false) String ciudad,
            @Parameter(description = "State to filter by") @RequestParam(required = false) String estado,
            @Parameter(description = "Status to filter by") @RequestParam(required = false) String estatus,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "nombreComercial") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Searching inmobiliarias with filters - name: {}, city: {}, state: {}, status: {}", 
                nombreComercial, ciudad, estado, estatus);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InmobiliariaDTO> inmobiliarias = inmobiliariaService.findByFilters(
                nombreComercial, ciudad, estado, estatus, pageable);
        
        return ResponseEntity.ok(inmobiliarias);
    }

    @Operation(summary = "Create new inmobiliaria", description = "Create a new inmobiliaria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inmobiliaria created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InmobiliariaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "RFC/NIT already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createInmobiliaria(
            @Parameter(description = "Inmobiliaria data", required = true)
            @Valid @RequestBody InmobiliariaDTO inmobiliariaDTO) {
        
        log.info("Creating new inmobiliaria: {}", inmobiliariaDTO.nombreComercial());
        
        try {
            InmobiliariaDTO createdInmobiliaria = inmobiliariaService.create(inmobiliariaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdInmobiliaria);
        } catch (IllegalArgumentException e) {
            log.warn("Error creating inmobiliaria: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Update inmobiliaria", description = "Update an existing inmobiliaria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inmobiliaria updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Inmobiliaria not found"),
            @ApiResponse(responseCode = "409", description = "RFC/NIT already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateInmobiliaria(
            @Parameter(description = "Inmobiliaria ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated inmobiliaria data", required = true)
            @Valid @RequestBody InmobiliariaDTO inmobiliariaDTO) {
        
        log.info("Updating inmobiliaria with id: {}", id);
        
        try {
            InmobiliariaDTO updatedInmobiliaria = inmobiliariaService.update(id, inmobiliariaDTO);
            return ResponseEntity.ok(updatedInmobiliaria);
        } catch (IllegalArgumentException e) {
            log.warn("Error updating inmobiliaria with id {}: {}", id, e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Delete inmobiliaria", description = "Delete an inmobiliaria by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inmobiliaria deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Inmobiliaria not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteInmobiliaria(
            @Parameter(description = "Inmobiliaria ID", required = true)
            @PathVariable Long id) {
        
        log.info("Deleting inmobiliaria with id: {}", id);
        
        try {
            inmobiliariaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Error deleting inmobiliaria with id {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Change inmobiliaria status", description = "Change the status of an inmobiliaria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status changed successfully"),
            @ApiResponse(responseCode = "404", description = "Inmobiliaria not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeInmobiliariaStatus(
            @Parameter(description = "Inmobiliaria ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "New status", required = true)
            @RequestParam String status) {
        
        log.info("Changing status of inmobiliaria with id: {} to: {}", id, status);
        
        try {
            InmobiliariaDTO updatedInmobiliaria = inmobiliariaService.changeStatus(id, status);
            return ResponseEntity.ok(updatedInmobiliaria);
        } catch (IllegalArgumentException e) {
            log.warn("Error changing status of inmobiliaria with id {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get inmobiliarias by status", description = "Get all inmobiliarias with a specific status")
    @GetMapping("/status/{estatus}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<InmobiliariaDTO>> getInmobiliariasByStatus(
            @Parameter(description = "Status to filter by", required = true)
            @PathVariable String estatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreComercial") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Getting inmobiliarias by status: {}", estatus);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InmobiliariaDTO> inmobiliarias = inmobiliariaService.findByEstatus(estatus, pageable);
        
        return ResponseEntity.ok(inmobiliarias);
    }

    @Operation(summary = "Get statistics", description = "Get statistics about inmobiliarias")
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.info("Getting inmobiliarias statistics");
        
        Map<String, Object> statistics = Map.of(
                "total", inmobiliariaService.countByEstatus("ACTIVE") + inmobiliariaService.countByEstatus("INACTIVE"),
                "active", inmobiliariaService.countByEstatus("ACTIVE"),
                "inactive", inmobiliariaService.countByEstatus("INACTIVE"),
                "cities", inmobiliariaService.getDistinctCiudades(),
                "states", inmobiliariaService.getDistinctEstados()
        );
        
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Get distinct cities", description = "Get all distinct cities where inmobiliarias are located")
    @GetMapping("/cities")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<String>> getDistinctCities() {
        log.info("Getting distinct cities");
        List<String> cities = inmobiliariaService.getDistinctCiudades();
        return ResponseEntity.ok(cities);
    }

    @Operation(summary = "Get distinct states", description = "Get all distinct states where inmobiliarias are located")
    @GetMapping("/states")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<String>> getDistinctStates() {
        log.info("Getting distinct states");
        List<String> states = inmobiliariaService.getDistinctEstados();
        return ResponseEntity.ok(states);
    }
}