package com.inmobiliaria.gestion.propiedad.model;

import com.inmobiliaria.gestion.inmobiliaria.model.Inmobiliaria;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "propiedad")
public class Propiedad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_propiedad")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_inmobiliaria", nullable = false)
    private Inmobiliaria inmobiliaria;

    @Column(name = "direccion_completa", length = 300)
    private String direccionCompleta;

    @Column(name = "tipo_propiedad", length = 50)
    private String tipoPropiedad;

    @Column(name = "superficie_total", precision = 10, scale = 2)
    private BigDecimal superficieTotal;

    @Column(name = "superficie_construida", precision = 10, scale = 2)
    private BigDecimal superficieConstruida;

    @Column(name = "numero_habitaciones")
    private Integer numeroHabitaciones;

    @Column(name = "numero_banos")
    private Integer numeroBanos;

    @Column(name = "caracteristicas_especiales", columnDefinition = "TEXT")
    private String caracteristicasEspeciales;

    @Column(name = "fecha_registro")
    @Temporal(TemporalType.DATE)
    private Date fechaRegistro;

    @Column(name = "estatus_propiedad", length = 30)
    private String estatusPropiedad;
}
