package com.inmobiliaria.gestion.configuracionrecargos.dto;

import java.math.BigDecimal;

public record ConfiguracionRecargosDTO(
        Integer id,
        Long idInmobiliaria,
        String nombrePolitica,
        Integer diasGracia,
        String tipoRecargo,
        BigDecimal porcentajeRecargoDiario,
        BigDecimal montoRecargoFijo,
        BigDecimal recargoMaximo,
        Integer diasCorteServicios,
        String aplicaAConceptos,
        Boolean activa
) {
}
