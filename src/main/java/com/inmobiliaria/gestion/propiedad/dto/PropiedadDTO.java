package com.inmobiliaria.gestion.propiedad.dto;

import java.math.BigDecimal;
import java.util.Date;

public record PropiedadDTO(
        Integer id,
        Long idInmobiliaria,
        String direccionCompleta,
        String tipoPropiedad,
        BigDecimal superficieTotal,
        BigDecimal superficieConstruida,
        Integer numeroHabitaciones,
        Integer numeroBanos,
        String caracteristicasEspeciales,
        Date fechaRegistro,
        String estatusPropiedad
) {
}
