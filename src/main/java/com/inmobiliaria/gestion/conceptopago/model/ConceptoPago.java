package com.inmobiliaria.gestion.conceptopago.model;

import com.inmobiliaria.gestion.inmobiliaria.model.Inmobiliaria;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "conceptos_pago")
public class ConceptoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_concepto")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_inmobiliaria", nullable = false)
    private Inmobiliaria inmobiliaria;

    @Column(name = "nombre_concepto", length = 100)
    private String nombreConcepto;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "tipo_concepto", length = 30)
    private String tipoConcepto;

    @Column(name = "permite_recargos")
    private Boolean permiteRecargos;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;
}
