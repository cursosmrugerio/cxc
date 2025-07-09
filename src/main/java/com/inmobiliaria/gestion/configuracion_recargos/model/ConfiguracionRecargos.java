package com.inmobiliaria.gestion.configuracion_recargos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @Column(name = "activa")
    private Boolean activa;

    @Column(name = "aplica_a_conceptos", length = 200)
    private String aplicaAConceptos;

    @Column(name = "dias_corte_servicios")
    @Min(0)
    private Integer diasCorteServicios;

    @Column(name = "dias_gracia")
    @Min(0)
    private Integer diasGracia;

    @Column(name = "monto_recargo_fijo", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal montoRecargoFijo;

    @Column(name = "nombre_politica", length = 100)
    @Size(max = 100)
    private String nombrePolitica;

    @Column(name = "porcentaje_recargo_diario", precision = 5, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal porcentajeRecargoDiario;

    @Column(name = "recargo_maximo", precision = 10, scale = 2)
    @DecimalMin(value = "0.0")
    private BigDecimal recargoMaximo;

    @Column(name = "tipo_recargo", length = 255)
    @NotBlank
    @Size(max = 255)
    private String tipoRecargo;

    @Column(name = "id_inmobiliaria", nullable = false)
    @NotNull
    private Long idInmobiliaria;

    @Column(name = "tasa_recargo_diaria", precision = 10, scale = 2)
    @DecimalMin(value = "0.0")
    private BigDecimal tasaRecargoDiaria;

    @Column(name = "tasa_recargo_fija", precision = 10, scale = 2)
    @DecimalMin(value = "0.0")
    private BigDecimal tasaRecargoFija;

    @Column(name = "activo", nullable = false)
    @NotNull
    private Boolean activo;

    @Column(name = "dia_aplicacion", nullable = false)
    @NotNull
    @Min(1)
    @Max(31)
    private Integer diaAplicacion;

    @Column(name = "monto", precision = 10, scale = 2, nullable = false)
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal monto;

}