package com.inmobiliaria.gestion.configuracion_recargos.dto;

import java.math.BigDecimal;

public record ConfiguracionRecargosDTO(
        Long idConfiguracionRecargo,
        String tipoRecargo,
        BigDecimal monto,
        Integer diaAplicacion,
        Boolean activo,
        Long idInmobiliaria
) {
}
