package com.inmobiliaria.gestion.configuracion_recargos.controller;

import com.inmobiliaria.gestion.configuracion_recargos.dto.ConfiguracionRecargosDTO;
import com.inmobiliaria.gestion.configuracion_recargos.service.ConfiguracionRecargosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/configuracion-recargos")
@RequiredArgsConstructor
@Tag(name = "Configuración de Recargos", description = "API para gestionar configuraciones de recargos por mora")
public class ConfiguracionRecargosController {

    private final ConfiguracionRecargosService configuracionRecargosService;

    @Operation(summary = "Crear nueva configuración de recargo", description = "Crea una nueva configuración de recargos para una inmobiliaria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Configuración creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflicto - configuración ya existe")
    })
    @PostMapping
    public ResponseEntity<ConfiguracionRecargosDTO> create(
            @Valid @RequestBody ConfiguracionRecargosDTO dto) {
        ConfiguracionRecargosDTO created = configuracionRecargosService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Obtener todas las configuraciones", description = "Recupera todas las configuraciones de recargos del sistema")
    @ApiResponse(responseCode = "200", description = "Lista de configuraciones obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<ConfiguracionRecargosDTO>> findAll() {
        List<ConfiguracionRecargosDTO> configurations = configuracionRecargosService.findAll();
        return ResponseEntity.ok(configurations);
    }

    @Operation(summary = "Obtener configuración por ID", description = "Recupera una configuración específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuración encontrada"),
            @ApiResponse(responseCode = "404", description = "Configuración no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ConfiguracionRecargosDTO> findById(
            @Parameter(description = "ID de la configuración") @PathVariable Long id) {
        return configuracionRecargosService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar configuración", description = "Actualiza una configuración existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuración actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Configuración no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ConfiguracionRecargosDTO> update(
            @Parameter(description = "ID de la configuración") @PathVariable Long id,
            @Valid @RequestBody ConfiguracionRecargosDTO dto) {
        return configuracionRecargosService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar configuración", description = "Elimina una configuración del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Configuración eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Configuración no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "ID de la configuración") @PathVariable Long id) {
        if (configuracionRecargosService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener configuraciones por inmobiliaria", description = "Recupera todas las configuraciones de una inmobiliaria específica")
    @ApiResponse(responseCode = "200", description = "Lista de configuraciones por inmobiliaria obtenida exitosamente")
    @GetMapping("/inmobiliaria/{idInmobiliaria}")
    public ResponseEntity<List<ConfiguracionRecargosDTO>> findByInmobiliaria(
            @Parameter(description = "ID de la inmobiliaria") @PathVariable Long idInmobiliaria) {
        List<ConfiguracionRecargosDTO> configurations = configuracionRecargosService.findByInmobiliaria(idInmobiliaria);
        return ResponseEntity.ok(configurations);
    }

    @Operation(summary = "Obtener configuraciones activas por inmobiliaria", description = "Recupera las configuraciones activas de una inmobiliaria específica")
    @ApiResponse(responseCode = "200", description = "Lista de configuraciones activas obtenida exitosamente")
    @GetMapping("/inmobiliaria/{idInmobiliaria}/activas")
    public ResponseEntity<List<ConfiguracionRecargosDTO>> findActiveByInmobiliaria(
            @Parameter(description = "ID de la inmobiliaria") @PathVariable Long idInmobiliaria) {
        List<ConfiguracionRecargosDTO> configurations = configuracionRecargosService.findActiveByInmobiliaria(idInmobiliaria);
        return ResponseEntity.ok(configurations);
    }

    @Operation(summary = "Obtener configuraciones por tipo de recargo", description = "Recupera configuraciones filtradas por tipo de recargo")
    @ApiResponse(responseCode = "200", description = "Lista de configuraciones por tipo obtenida exitosamente")
    @GetMapping("/tipo/{tipoRecargo}")
    public ResponseEntity<List<ConfiguracionRecargosDTO>> findByTipoRecargo(
            @Parameter(description = "Tipo de recargo") @PathVariable String tipoRecargo) {
        List<ConfiguracionRecargosDTO> configurations = configuracionRecargosService.findByTipoRecargo(tipoRecargo);
        return ResponseEntity.ok(configurations);
    }

    @Operation(summary = "Obtener configuración por inmobiliaria y tipo", description = "Recupera una configuración específica por inmobiliaria y tipo de recargo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuración encontrada"),
            @ApiResponse(responseCode = "404", description = "Configuración no encontrada")
    })
    @GetMapping("/inmobiliaria/{idInmobiliaria}/tipo/{tipoRecargo}")
    public ResponseEntity<ConfiguracionRecargosDTO> findByInmobiliariaAndTipoRecargo(
            @Parameter(description = "ID de la inmobiliaria") @PathVariable Long idInmobiliaria,
            @Parameter(description = "Tipo de recargo") @PathVariable String tipoRecargo) {
        return configuracionRecargosService.findByInmobiliariaAndTipoRecargo(idInmobiliaria, tipoRecargo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Cambiar estado activo/inactivo", description = "Cambia el estado activo de una configuración")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado cambiado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Configuración no encontrada")
    })
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ConfiguracionRecargosDTO> toggleActive(
            @Parameter(description = "ID de la configuración") @PathVariable Long id) {
        return configuracionRecargosService.toggleActive(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
