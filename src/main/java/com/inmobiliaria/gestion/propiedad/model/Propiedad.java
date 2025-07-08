package com.inmobiliaria.gestion.propiedad.model;

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
@Table(name = "propiedad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Propiedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_propiedad")
    private Integer idPropiedad;

    @NotNull(message = "Inmobiliaria ID is required")
    @Column(name = "id_inmobiliaria", nullable = false)
    private Long idInmobiliaria;

    @Size(max = 255, message = "Property type must not exceed 255 characters")
    @Column(name = "tipo_propiedad")
    private String tipoPropiedad;

    @Column(name = "superficie_total", precision = 10, scale = 2)
    private BigDecimal superficieTotal;

    @Column(name = "superficie_construida", precision = 10, scale = 2)
    private BigDecimal superficieConstruida;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @Size(max = 255, message = "Property status must not exceed 255 characters")
    @Column(name = "estatus_propiedad")
    @Builder.Default
    private String estatusPropiedad = "DISPONIBLE";

    @Column(name = "caracteristicas_especiales", columnDefinition = "TEXT")
    private String caracteristicasEspeciales;

    @Size(max = 255, message = "Full address must not exceed 255 characters")
    @Column(name = "direccion_completa")
    private String direccionCompleta;

    @Column(name = "numero_banos")
    private Integer numeroBanos;

    @Column(name = "numero_habitaciones")
    private Integer numeroHabitaciones;

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDate.now();
        }
    }
}