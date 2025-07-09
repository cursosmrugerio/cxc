package com.inmobiliaria.gestion.configuracion_recargos.dto;

import java.math.BigDecimal;

public record ConfiguracionRecargosCreateRequest(
        Integer diasGracia,
        BigDecimal tasaRecargoDiaria,
        BigDecimal tasaRecargoFija
) {
}
