package com.inmobiliaria.gestion.contratorenta.model;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "contrato_renta")
public class ContratoRenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idContrato;

    private Integer idPropiedad;
    private Date fechaInicioContrato;
    private Date fechaFinContrato;
    private Integer duracionMeses;
    private BigDecimal depositoGarantia;
    private String condicionesEspeciales;
    private Integer notificacionDiasPrevios;
    private String emailNotificaciones;
    private String telefonoNotificaciones;
    private String estatusContrato;
}
