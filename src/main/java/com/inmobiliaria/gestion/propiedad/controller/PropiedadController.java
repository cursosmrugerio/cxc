package com.inmobiliaria.gestion.propiedad.controller;

import com.inmobiliaria.gestion.propiedad.dto.PropiedadDTO;
import com.inmobiliaria.gestion.propiedad.service.PropiedadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/propiedades")
@RequiredArgsConstructor
public class PropiedadController {

    private final PropiedadService propiedadService;

    @PostMapping
    @Operation(summary = "Create a new propiedad")
    @ApiResponse(responseCode = "201", description = "Propiedad created")
    public ResponseEntity<PropiedadDTO> createPropiedad(@RequestBody PropiedadDTO propiedadDTO) {
        return ResponseEntity.status(201).body(propiedadService.save(propiedadDTO));
    }

    @GetMapping
    @Operation(summary = "Get all propiedades")
    public List<PropiedadDTO> getAllPropiedades() {
        return propiedadService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a propiedad by id")
    @ApiResponse(responseCode = "200", description = "Propiedad found")
    @ApiResponse(responseCode = "404", description = "Propiedad not found")
    public ResponseEntity<PropiedadDTO> getPropiedadById(@PathVariable Integer id) {
        return propiedadService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a propiedad")
    @ApiResponse(responseCode = "200", description = "Propiedad updated")
    public ResponseEntity<PropiedadDTO> updatePropiedad(@PathVariable Integer id, @RequestBody PropiedadDTO propiedadDTO) {
        // Ensure the ID in the path matches the ID in the body
        if (!id.equals(propiedadDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(propiedadService.save(propiedadDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a propiedad")
    @ApiResponse(responseCode = "204", description = "Propiedad deleted")
    public ResponseEntity<Void> deletePropiedad(@PathVariable Integer id) {
        propiedadService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
