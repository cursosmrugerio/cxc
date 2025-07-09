package com.inmobiliaria.gestion.configuracion_recargos.dto;

import java.math.BigDecimal;

public record ConfiguracionRecargosDTO(
        Long id,
        Integer diasGracia,
        BigDecimal tasaRecargoDiaria,
        BigDecimal tasaRecargoFija
) {
}
