package com.inmobiliaria.gestion.configuracionrecargos.controller;

import com.inmobiliaria.gestion.configuracionrecargos.dto.ConfiguracionRecargosDTO;
import com.inmobiliaria.gestion.configuracionrecargos.service.ConfiguracionRecargosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/configuracion-recargos")
@RequiredArgsConstructor
public class ConfiguracionRecargosController {

    private final ConfiguracionRecargosService configuracionRecargosService;

    @PostMapping
    @Operation(summary = "Create a new configuracion de recargos")
    @ApiResponse(responseCode = "201", description = "Configuracion de recargos created")
    public ResponseEntity<ConfiguracionRecargosDTO> createConfiguracionRecargos(@RequestBody ConfiguracionRecargosDTO configuracionRecargosDTO) {
        return ResponseEntity.status(201).body(configuracionRecargosService.save(configuracionRecargosDTO));
    }

    @GetMapping
    @Operation(summary = "Get all configuraciones de recargos")
    public List<ConfiguracionRecargosDTO> getAllConfiguracionRecargos() {
        return configuracionRecargosService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a configuracion de recargos by id")
    @ApiResponse(responseCode = "200", description = "Configuracion de recargos found")
    @ApiResponse(responseCode = "404", description = "Configuracion de recargos not found")
    public ResponseEntity<ConfiguracionRecargosDTO> getConfiguracionRecargosById(@PathVariable Integer id) {
        return configuracionRecargosService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a configuracion de recargos")
    @ApiResponse(responseCode = "200", description = "Configuracion de recargos updated")
    public ResponseEntity<ConfiguracionRecargosDTO> updateConfiguracionRecargos(@PathVariable Integer id, @RequestBody ConfiguracionRecargosDTO configuracionRecargosDTO) {
        // Ensure the ID in the path matches the ID in the body
        if (!id.equals(configuracionRecargosDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(configuracionRecargosService.save(configuracionRecargosDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a configuracion de recargos")
    @ApiResponse(responseCode = "204", description = "Configuracion de recargos deleted")
    public ResponseEntity<Void> deleteConfiguracionRecargos(@PathVariable Integer id) {
        configuracionRecargosService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
