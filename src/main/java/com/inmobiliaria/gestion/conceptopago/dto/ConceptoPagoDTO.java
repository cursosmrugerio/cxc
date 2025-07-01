package com.inmobiliaria.gestion.conceptopago.dto;

import java.util.Date;

public record ConceptoPagoDTO(
        Integer id,
        Long idInmobiliaria,
        String nombreConcepto,
        String descripcion,
        String tipoConcepto,
        Boolean permiteRecargos,
        Boolean activo,
        Date fechaCreacion
) {
}
