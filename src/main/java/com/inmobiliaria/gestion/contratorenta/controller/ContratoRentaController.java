package com.inmobiliaria.gestion.contratorenta.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inmobiliaria.gestion.contratorenta.dto.ContratoRentaDTO;
import com.inmobiliaria.gestion.contratorenta.service.ContratoRentaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/contratos-renta")
@RequiredArgsConstructor
public class ContratoRentaController {

    private final ContratoRentaService contratoRentaService;

    @PostMapping
    @Operation(summary = "Crear un nuevo contrato de renta")
    @ApiResponse(responseCode = "200", description = "Contrato de renta creado exitosamente")
    public ContratoRentaDTO createContratoRenta(@RequestBody ContratoRentaDTO contratoRentaDTO) {
        return contratoRentaService.save(contratoRentaDTO);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los contratos de renta")
    public List<ContratoRentaDTO> getAllContratosRenta() {
        return contratoRentaService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un contrato de renta por ID")
    @ApiResponse(responseCode = "200", description = "Contrato de renta encontrado")
    @ApiResponse(responseCode = "404", description = "Contrato de renta no encontrado")
    public ResponseEntity<ContratoRentaDTO> getContratoRentaById(@PathVariable Integer id) {
        return contratoRentaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un contrato de renta existente")
    @ApiResponse(responseCode = "200", description = "Contrato de renta actualizado exitosamente")
    @ApiResponse(responseCode = "404", description = "Contrato de renta no encontrado")
    public ResponseEntity<ContratoRentaDTO> updateContratoRenta(@PathVariable Integer id, @RequestBody ContratoRentaDTO contratoRentaDTO) {
        return contratoRentaService.update(id, contratoRentaDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un contrato de renta por ID")
    @ApiResponse(responseCode = "204", description = "Contrato de renta eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Contrato de renta no encontrado")
    public ResponseEntity<Void> deleteContratoRenta(@PathVariable Integer id) {
        return contratoRentaService.deleteById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
