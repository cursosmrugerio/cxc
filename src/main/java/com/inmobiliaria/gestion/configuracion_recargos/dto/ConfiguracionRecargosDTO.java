package com.inmobiliaria.gestion.configuracion_recargos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "DTO para configuración de recargos por mora")
public record ConfiguracionRecargosDTO(
        @Schema(description = "ID único de la configuración de recargos", example = "1")
        Long idConfiguracionRecargo,

        @Schema(description = "Indica si la configuración está activa", example = "true")
        Boolean activa,

        @Schema(description = "Conceptos a los que aplica el recargo", example = "renta,servicios")
        @Size(max = 200)
        String aplicaAConceptos,

        @Schema(description = "Días después del vencimiento para corte de servicios", example = "10")
        @Min(0)
        Integer diasCorteServicios,

        @Schema(description = "Días de gracia antes de aplicar recargo", example = "5")
        @Min(0)
        Integer diasGracia,

        @Schema(description = "Monto fijo del recargo", example = "100.00")
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal montoRecargoFijo,

        @Schema(description = "Nombre de la política de recargos", example = "Política Estándar")
        @Size(max = 100)
        String nombrePolitica,

        @Schema(description = "Porcentaje de recargo diario", example = "1.5")
        @DecimalMin(value = "0.0")
        @DecimalMax(value = "100.0")
        BigDecimal porcentajeRecargoDiario,

        @Schema(description = "Monto máximo de recargo", example = "1000.00")
        @DecimalMin(value = "0.0")
        BigDecimal recargoMaximo,

        @Schema(description = "Tipo de recargo", example = "mora", required = true)
        @NotBlank
        @Size(max = 255)
        String tipoRecargo,

        @Schema(description = "ID de la inmobiliaria", example = "1", required = true)
        @NotNull
        Long idInmobiliaria,

        @Schema(description = "Tasa de recargo diaria", example = "0.05")
        @DecimalMin(value = "0.0")
        BigDecimal tasaRecargoDiaria,

        @Schema(description = "Tasa de recargo fija", example = "0.10")
        @DecimalMin(value = "0.0")
        BigDecimal tasaRecargoFija,

        @Schema(description = "Estado activo/inactivo", example = "true", required = true)
        @NotNull
        Boolean activo,

        @Schema(description = "Día del mes en que se aplica el recargo", example = "15", required = true)
        @NotNull
        @Min(1)
        @Max(31)
        Integer diaAplicacion,

        @Schema(description = "Monto base del recargo", example = "50.00", required = true)
        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal monto
) {
}
