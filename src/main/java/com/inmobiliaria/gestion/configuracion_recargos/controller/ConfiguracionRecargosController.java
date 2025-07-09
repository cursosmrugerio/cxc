package com.inmobiliaria.gestion.configuracion_recargos.controller;

import com.inmobiliaria.gestion.configuracion_recargos.dto.ConfiguracionRecargosDTO;
import com.inmobiliaria.gestion.configuracion_recargos.service.ConfiguracionRecargosService;
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
    public ConfiguracionRecargosDTO create(@RequestBody ConfiguracionRecargosDTO dto) {
        return configuracionRecargosService.save(dto);
    }

    @GetMapping
    public List<ConfiguracionRecargosDTO> findAll() {
        return configuracionRecargosService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConfiguracionRecargosDTO> findById(@PathVariable Long id) {
        return configuracionRecargosService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConfiguracionRecargosDTO> update(@PathVariable Long id, @RequestBody ConfiguracionRecargosDTO dto) {
        return configuracionRecargosService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (configuracionRecargosService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
