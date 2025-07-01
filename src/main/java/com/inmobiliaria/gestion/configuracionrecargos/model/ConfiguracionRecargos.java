package com.inmobiliaria.gestion.configuracionrecargos.model;

import com.inmobiliaria.gestion.inmobiliaria.model.Inmobiliaria;
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
    @Column(name = "id_configuracion")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_inmobiliaria", nullable = false)
    private Inmobiliaria inmobiliaria;

    @Column(name = "nombre_politica", length = 100)
    private String nombrePolitica;

    @Column(name = "dias_gracia")
    private Integer diasGracia;

    @Column(name = "tipo_recargo", length = 20)
    private String tipoRecargo;

    @Column(name = "porcentaje_recargo_diario", precision = 5, scale = 4)
    private BigDecimal porcentajeRecargoDiario;

    @Column(name = "monto_recargo_fijo", precision = 10, scale = 2)
    private BigDecimal montoRecargoFijo;

    @Column(name = "recargo_maximo", precision = 10, scale = 2)
    private BigDecimal recargoMaximo;

    @Column(name = "dias_corte_servicios")
    private Integer diasCorteServicios;

    @Column(name = "aplica_a_conceptos", length = 200)
    private String aplicaAConceptos;

    @Column(name = "activa")
    private Boolean activa;
}
