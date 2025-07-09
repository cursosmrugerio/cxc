package com.inmobiliaria.gestion.conceptos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "conceptos_pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConceptosPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_concepto")
    private Integer idConcepto;

    @NotNull(message = "ID inmobiliaria is required")
    @Column(name = "id_inmobiliaria")
    private Long idInmobiliaria;

    @Size(max = 255, message = "Concept name must not exceed 255 characters")
    @Column(name = "nombre_concepto")
    private String nombreConcepto;

    @Size(max = 255, message = "Concept description must not exceed 255 characters")
    @Column(name = "descripcion")
    private String descripcion;

    @Size(max = 255, message = "Concept type must not exceed 255 characters")
    @Column(name = "tipo_concepto")
    private String tipoConcepto;

    @Column(name = "permite_recargos")
    @Builder.Default
    private Boolean permiteRecargos = false;

    @Column(name = "activo")
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDate.now();
        }
    }
}