package com.inmobiliaria.gestion.configuracion_recargos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "configuracion_recargos")
public class ConfiguracionRecargos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion", nullable = false)
    private Long id;

    @Column(name = "dias_gracia")
    private Integer diasGracia;

    @Column(name = "tasa_recargo_diaria", precision = 5, scale = 2)
    private BigDecimal tasaRecargoDiaria;

    @Column(name = "tasa_recargo_fija", precision = 10, scale = 2)
    private BigDecimal tasaRecargoFija;

}