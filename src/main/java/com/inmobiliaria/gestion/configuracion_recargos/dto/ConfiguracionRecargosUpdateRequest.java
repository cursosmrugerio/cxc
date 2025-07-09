package com.inmobiliaria.gestion.configuracion_recargos.dto;

import java.math.BigDecimal;

public record ConfiguracionRecargosUpdateRequest(
        Integer diasGracia,
        BigDecimal tasaRecargoDiaria,
        BigDecimal tasaRecargoFija
) {
}
