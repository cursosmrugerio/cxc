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
    private Long idConfiguracionRecargo;

    @Column(name = "tipo_recargo", nullable = false)
    private String tipoRecargo;

    @Column(name = "monto", precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "dia_aplicacion", nullable = false)
    private Integer diaAplicacion;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "id_inmobiliaria", nullable = false)
    private Long idInmobiliaria;

}