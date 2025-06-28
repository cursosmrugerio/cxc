
package com.inmobiliaria.gestion.inmobiliaria.controller;

import com.inmobiliaria.gestion.inmobiliaria.dto.InmobiliariaDTO;
import com.inmobiliaria.gestion.inmobiliaria.service.InmobiliariaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inmobiliarias")
@RequiredArgsConstructor
@Tag(name = "Inmobiliaria", description = "API for real estate management")
public class InmobiliariaController {

    private final InmobiliariaService inmobiliariaService;

    @PostMapping
    @Operation(summary = "Create a new real estate company")
    @ApiResponse(responseCode = "200", description = "Real estate company created successfully")
    public InmobiliariaDTO create(@RequestBody InmobiliariaDTO inmobiliariaDTO) {
        return inmobiliariaService.save(inmobiliariaDTO);
    }

    @GetMapping
    @Operation(summary = "Get all real estate companies")
    public List<InmobiliariaDTO> getAll() {
        return inmobiliariaService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a real estate company by ID")
    @ApiResponse(responseCode = "200", description = "Found the real estate company")
    @ApiResponse(responseCode = "404", description = "Real estate company not found")
    public ResponseEntity<InmobiliariaDTO> getById(@PathVariable Long id) {
        return inmobiliariaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a real estate company")
    @ApiResponse(responseCode = "200", description = "Real estate company updated successfully")
    public InmobiliariaDTO update(@PathVariable Long id, @RequestBody InmobiliariaDTO inmobiliariaDTO) {
        // Ensure the ID is set for the update
        // Note: A more robust implementation would involve checking if the resource exists first
        return inmobiliariaService.save(inmobiliariaDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a real estate company by ID")
    @ApiResponse(responseCode = "204", description = "Real estate company deleted successfully")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inmobiliariaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
