package com.inmobiliaria.gestion.conceptopago.controller;

import com.inmobiliaria.gestion.conceptopago.dto.ConceptoPagoDTO;
import com.inmobiliaria.gestion.conceptopago.service.ConceptoPagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conceptos-pago")
@RequiredArgsConstructor
public class ConceptoPagoController {

    private final ConceptoPagoService conceptoPagoService;

    @PostMapping
    @Operation(summary = "Create a new concepto de pago")
    @ApiResponse(responseCode = "201", description = "Concepto de pago created")
    public ResponseEntity<ConceptoPagoDTO> createConceptoPago(@RequestBody ConceptoPagoDTO conceptoPagoDTO) {
        return ResponseEntity.status(201).body(conceptoPagoService.save(conceptoPagoDTO));
    }

    @GetMapping
    @Operation(summary = "Get all conceptos de pago")
    public List<ConceptoPagoDTO> getAllConceptosPago() {
        return conceptoPagoService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a concepto de pago by id")
    @ApiResponse(responseCode = "200", description = "Concepto de pago found")
    @ApiResponse(responseCode = "404", description = "Concepto de pago not found")
    public ResponseEntity<ConceptoPagoDTO> getConceptoPagoById(@PathVariable Integer id) {
        return conceptoPagoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a concepto de pago")
    @ApiResponse(responseCode = "200", description = "Concepto de pago updated")
    public ResponseEntity<ConceptoPagoDTO> updateConceptoPago(@PathVariable Integer id, @RequestBody ConceptoPagoDTO conceptoPagoDTO) {
        // Ensure the ID in the path matches the ID in the body
        if (!id.equals(conceptoPagoDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(conceptoPagoService.save(conceptoPagoDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a concepto de pago")
    @ApiResponse(responseCode = "204", description = "Concepto de pago deleted")
    public ResponseEntity<Void> deleteConceptoPago(@PathVariable Integer id) {
        conceptoPagoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
