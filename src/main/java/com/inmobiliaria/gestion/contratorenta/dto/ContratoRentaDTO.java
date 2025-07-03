package com.inmobiliaria.gestion.contratorenta.dto;

import java.math.BigDecimal;
import java.util.Date;

public record ContratoRentaDTO(
    Integer idContrato,
    Integer idPropiedad,
    Date fechaInicioContrato,
    Date fechaFinContrato,
    Integer duracionMeses,
    BigDecimal depositoGarantia,
    String condicionesEspeciales,
    Integer notificacionDiasPrevios,
    String emailNotificaciones,
    String telefonoNotificaciones,
    String estatusContrato
) {}
