package com.inmobiliaria.gestion.propiedad.controller;

import com.inmobiliaria.gestion.propiedad.dto.PropiedadCreateRequest;
import com.inmobiliaria.gestion.propiedad.dto.PropiedadDTO;
import com.inmobiliaria.gestion.propiedad.dto.PropiedadUpdateRequest;
import com.inmobiliaria.gestion.propiedad.service.PropiedadService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/propiedades")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Propiedades", description = "Property management operations")
public class PropiedadController {

    private final PropiedadService propiedadService;

    @Operation(summary = "Get all properties", description = "Retrieve a list of all properties")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Properties retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PropiedadDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<PropiedadDTO>> getAllPropiedades() {
        log.info("GET /api/v1/propiedades - Fetching all propiedades");
        List<PropiedadDTO> propiedades = propiedadService.getAllPropiedades();
        return ResponseEntity.ok(propiedades);
    }

    @Operation(summary = "Get property by ID", description = "Retrieve a specific property by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PropiedadDTO.class))),
            @ApiResponse(responseCode = "404", description = "Property not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PropiedadDTO> getPropiedadById(
            @Parameter(description = "Property ID", required = true)
            @PathVariable Integer id) {
        log.info("GET /api/v1/propiedades/{} - Fetching propiedad by id", id);
        Optional<PropiedadDTO> propiedad = propiedadService.getPropiedadById(id);
        return propiedad.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get properties by inmobiliaria", description = "Retrieve all properties belonging to a specific inmobiliaria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Properties retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PropiedadDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/inmobiliaria/{idInmobiliaria}")
    public ResponseEntity<List<PropiedadDTO>> getPropiedadesByInmobiliaria(
            @Parameter(description = "Inmobiliaria ID", required = true)
            @PathVariable Long idInmobiliaria) {
        log.info("GET /api/v1/propiedades/inmobiliaria/{} - Fetching propiedades by inmobiliaria", idInmobiliaria);
        List<PropiedadDTO> propiedades = propiedadService.getPropiedadesByInmobiliaria(idInmobiliaria);
        return ResponseEntity.ok(propiedades);
    }

    @Operation(summary = "Get properties by status", description = "Retrieve all properties with a specific status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Properties retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PropiedadDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/estatus/{estatus}")
    public ResponseEntity<List<PropiedadDTO>> getPropiedadesByEstatus(
            @Parameter(description = "Property status", required = true)
            @PathVariable String estatus) {
        log.info("GET /api/v1/propiedades/estatus/{} - Fetching propiedades by estatus", estatus);
        List<PropiedadDTO> propiedades = propiedadService.getPropiedadesByEstatus(estatus);
        return ResponseEntity.ok(propiedades);
    }

    @Operation(summary = "Get properties by type", description = "Retrieve all properties of a specific type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Properties retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PropiedadDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<PropiedadDTO>> getPropiedadesByTipo(
            @Parameter(description = "Property type", required = true)
            @PathVariable String tipo) {
        log.info("GET /api/v1/propiedades/tipo/{} - Fetching propiedades by tipo", tipo);
        List<PropiedadDTO> propiedades = propiedadService.getPropiedadesByTipo(tipo);
        return ResponseEntity.ok(propiedades);
    }

    @Operation(summary = "Search properties by address", description = "Search properties by address containing the specified text")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Properties retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PropiedadDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<List<PropiedadDTO>> searchPropiedadesByDireccion(
            @Parameter(description = "Address search term", required = true)
            @RequestParam String direccion) {
        log.info("GET /api/v1/propiedades/search?direccion={} - Searching propiedades by direccion", direccion);
        List<PropiedadDTO> propiedades = propiedadService.searchPropiedadesByDireccion(direccion);
        return ResponseEntity.ok(propiedades);
    }

    @Operation(summary = "Get properties by inmobiliaria and status", description = "Retrieve properties filtered by inmobiliaria and status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Properties retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PropiedadDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/inmobiliaria/{idInmobiliaria}/estatus/{estatus}")
    public ResponseEntity<List<PropiedadDTO>> getPropiedadesByInmobiliariaAndEstatus(
            @Parameter(description = "Inmobiliaria ID", required = true)
            @PathVariable Long idInmobiliaria,
            @Parameter(description = "Property status", required = true)
            @PathVariable String estatus) {
        log.info("GET /api/v1/propiedades/inmobiliaria/{}/estatus/{} - Fetching propiedades by inmobiliaria and estatus", idInmobiliaria, estatus);
        List<PropiedadDTO> propiedades = propiedadService.getPropiedadesByInmobiliariaAndEstatus(idInmobiliaria, estatus);
        return ResponseEntity.ok(propiedades);
    }

    @Operation(summary = "Create a new property", description = "Create a new property with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Property created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PropiedadDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<PropiedadDTO> createPropiedad(
            @Parameter(description = "Property creation request", required = true)
            @Valid @RequestBody PropiedadCreateRequest request) {
        log.info("POST /api/v1/propiedades - Creating new propiedad");
        PropiedadDTO createdPropiedad = propiedadService.createPropiedad(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPropiedad);
    }

    @Operation(summary = "Update an existing property", description = "Update an existing property with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PropiedadDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Property not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PropiedadDTO> updatePropiedad(
            @Parameter(description = "Property ID", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Property update request", required = true)
            @Valid @RequestBody PropiedadUpdateRequest request) {
        log.info("PUT /api/v1/propiedades/{} - Updating propiedad", id);
        Optional<PropiedadDTO> updatedPropiedad = propiedadService.updatePropiedad(id, request);
        return updatedPropiedad.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a property", description = "Delete a property by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Property deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Property not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePropiedad(
            @Parameter(description = "Property ID", required = true)
            @PathVariable Integer id) {
        log.info("DELETE /api/v1/propiedades/{} - Deleting propiedad", id);
        boolean deleted = propiedadService.deletePropiedad(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get property count by inmobiliaria", description = "Get the total count of properties for a specific inmobiliaria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/inmobiliaria/{idInmobiliaria}/count")
    public ResponseEntity<Long> countPropiedadesByInmobiliaria(
            @Parameter(description = "Inmobiliaria ID", required = true)
            @PathVariable Long idInmobiliaria) {
        log.info("GET /api/v1/propiedades/inmobiliaria/{}/count - Counting propiedades by inmobiliaria", idInmobiliaria);
        long count = propiedadService.countPropiedadesByInmobiliaria(idInmobiliaria);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Get property count by inmobiliaria and status", description = "Get the count of properties for a specific inmobiliaria and status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/inmobiliaria/{idInmobiliaria}/estatus/{estatus}/count")
    public ResponseEntity<Long> countPropiedadesByInmobiliariaAndEstatus(
            @Parameter(description = "Inmobiliaria ID", required = true)
            @PathVariable Long idInmobiliaria,
            @Parameter(description = "Property status", required = true)
            @PathVariable String estatus) {
        log.info("GET /api/v1/propiedades/inmobiliaria/{}/estatus/{}/count - Counting propiedades by inmobiliaria and estatus", idInmobiliaria, estatus);
        long count = propiedadService.countPropiedadesByInmobiliariaAndEstatus(idInmobiliaria, estatus);
        return ResponseEntity.ok(count);
    }
}