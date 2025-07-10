package com.inmobiliaria.gestion.configuracion_recargos.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConfiguracionRecargos Entity Tests")
class ConfiguracionRecargosEntityTest {

    private Validator validator;
    private ConfiguracionRecargos validConfiguracion;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        validConfiguracion = createValidConfiguracion();
    }

    private ConfiguracionRecargos createValidConfiguracion() {
        ConfiguracionRecargos configuracion = new ConfiguracionRecargos();
        configuracion.setActiva(true);
        configuracion.setAplicaAConceptos("renta,servicios");
        configuracion.setDiasCorteServicios(10);
        configuracion.setDiasGracia(3);
        configuracion.setMontoRecargoFijo(BigDecimal.valueOf(100.00));
        configuracion.setNombrePolitica("Política de Prueba");
        configuracion.setPorcentajeRecargoDiario(BigDecimal.valueOf(1.5));
        configuracion.setRecargoMaximo(BigDecimal.valueOf(1000.00));
        configuracion.setTipoRecargo("mora_test");
        configuracion.setIdInmobiliaria(1L);
        configuracion.setTasaRecargoDiaria(BigDecimal.valueOf(0.05));
        configuracion.setTasaRecargoFija(BigDecimal.valueOf(0.10));
        configuracion.setActivo(true);  // This field is @NotNull
        configuracion.setDiaAplicacion(5);  // This field is @NotNull @Min(1) @Max(31)
        configuracion.setMonto(BigDecimal.valueOf(50.00));  // This field is @NotNull @DecimalMin(value = "0.0", inclusive = false)
        return configuracion;
    }

    @Nested
    @DisplayName("Entity Validation Tests")
    class EntityValidationTests {

        @Test
        @DisplayName("Should validate successfully with all required fields")
        void shouldValidateSuccessfullyWithAllRequiredFields() {
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when inmobiliaria id is null")
        void shouldFailValidationWhenInmobiliariaIdIsNull() {
            validConfiguracion.setIdInmobiliaria(null);
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("idInmobiliaria");
        }

        @Test
        @DisplayName("Should fail validation when tipo recargo is blank")
        void shouldFailValidationWhenTipoRecargoIsBlank() {
            validConfiguracion.setTipoRecargo("");
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("tipoRecargo");
        }

        @Test
        @DisplayName("Should allow blank nombre politica as it's not required")
        void shouldAllowBlankNombrePolitica() {
            validConfiguracion.setNombrePolitica("");
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).isEmpty();
        }
        
        @Test
        @DisplayName("Should fail validation when activo is null")
        void shouldFailValidationWhenActivoIsNull() {
            validConfiguracion.setActivo(null);
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("activo");
        }
        
        @Test
        @DisplayName("Should fail validation when dia aplicacion is null")
        void shouldFailValidationWhenDiaAplicacionIsNull() {
            validConfiguracion.setDiaAplicacion(null);
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("diaAplicacion");
        }
        
        @Test
        @DisplayName("Should fail validation when monto is null")
        void shouldFailValidationWhenMontoIsNull() {
            validConfiguracion.setMonto(null);
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("monto");
        }
    }

    @Nested
    @DisplayName("Numeric Field Validation Tests")
    class NumericFieldValidationTests {

        @Test
        @DisplayName("Should fail validation when dias corte servicios is negative")
        void shouldFailValidationWhenDiasCorteServiciosIsNegative() {
            validConfiguracion.setDiasCorteServicios(-1);
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("diasCorteServicios");
        }
        
        @Test
        @DisplayName("Should allow zero for dias corte servicios")
        void shouldAllowZeroForDiasCorteServicios() {
            validConfiguracion.setDiasCorteServicios(0);
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when dias gracia is negative")
        void shouldFailValidationWhenDiasGraciaIsNegative() {
            validConfiguracion.setDiasGracia(-1);
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("diasGracia");
        }

        @Test
        @DisplayName("Should fail validation when monto recargo fijo is negative")
        void shouldFailValidationWhenMontoRecargoFijoIsNegative() {
            validConfiguracion.setMontoRecargoFijo(BigDecimal.valueOf(-100.0));
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("montoRecargoFijo");
        }
        
        @Test
        @DisplayName("Should fail validation when monto recargo fijo is zero (inclusive false)")
        void shouldFailValidationWhenMontoRecargoFijoIsZero() {
            validConfiguracion.setMontoRecargoFijo(BigDecimal.ZERO);
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("montoRecargoFijo");
        }

        @Test
        @DisplayName("Should fail validation when porcentaje recargo diario is negative")
        void shouldFailValidationWhenPorcentajeRecargoDiarioIsNegative() {
            validConfiguracion.setPorcentajeRecargoDiario(BigDecimal.valueOf(-1.0));
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("porcentajeRecargoDiario");
        }

        @Test
        @DisplayName("Should fail validation when recargo maximo is negative")
        void shouldFailValidationWhenRecargoMaximoIsNegative() {
            validConfiguracion.setRecargoMaximo(BigDecimal.valueOf(-500.0));
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("recargoMaximo");
        }

        @Test
        @DisplayName("Should fail validation when tasa recargo diaria is negative")
        void shouldFailValidationWhenTasaRecargoDiariaIsNegative() {
            validConfiguracion.setTasaRecargoDiaria(BigDecimal.valueOf(-0.01));
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("tasaRecargoDiaria");
        }

        @Test
        @DisplayName("Should fail validation when monto is negative")
        void shouldFailValidationWhenMontoIsNegative() {
            validConfiguracion.setMonto(BigDecimal.valueOf(-25.0));
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("monto");
        }
        
        @Test
        @DisplayName("Should fail validation when monto is zero (inclusive false)")
        void shouldFailValidationWhenMontoIsZero() {
            validConfiguracion.setMonto(BigDecimal.ZERO);
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("monto");
        }
    }

    @Nested
    @DisplayName("Size Validation Tests")
    class SizeValidationTests {

        @Test
        @DisplayName("Should fail validation when tipo recargo exceeds max length")
        void shouldFailValidationWhenTipoRecargoExceedsMaxLength() {
            validConfiguracion.setTipoRecargo("a".repeat(256));
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("tipoRecargo");
        }

        @Test
        @DisplayName("Should fail validation when nombre politica exceeds max length")
        void shouldFailValidationWhenNombrePoliticaExceedsMaxLength() {
            validConfiguracion.setNombrePolitica("a".repeat(101));
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("nombrePolitica");
        }

        @Test
        @DisplayName("Should allow max length for aplica a conceptos (no size constraint)")
        void shouldAllowMaxLengthForAplicaAConceptos() {
            validConfiguracion.setAplicaAConceptos("a".repeat(200));
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Range Validation Tests")
    class RangeValidationTests {

        @Test
        @DisplayName("Should fail validation when dia aplicacion is out of range")
        void shouldFailValidationWhenDiaAplicacionIsOutOfRange() {
            validConfiguracion.setDiaAplicacion(32);
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("diaAplicacion");
        }

        @Test
        @DisplayName("Should fail validation when porcentaje recargo exceeds 100")
        void shouldFailValidationWhenPorcentajeRecargoExceeds100() {
            validConfiguracion.setPorcentajeRecargoDiario(BigDecimal.valueOf(101.0));
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("porcentajeRecargoDiario");
        }

        @Test
        @DisplayName("Should allow any positive value for tasa recargo diaria (no max constraint)")
        void shouldAllowAnyPositiveValueForTasaRecargoDiaria() {
            validConfiguracion.setTasaRecargoDiaria(BigDecimal.valueOf(100.0));
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Entity Behavior Tests")
    class EntityBehaviorTests {

        @Test
        @DisplayName("Should create entity with valid constructor")
        void shouldCreateEntityWithValidConstructor() {
            ConfiguracionRecargos configuracion = new ConfiguracionRecargos();
            
            assertThat(configuracion).isNotNull();
            assertThat(configuracion.getIdConfiguracionRecargo()).isNull();
            assertThat(configuracion.getActiva()).isNull();
            assertThat(configuracion.getActivo()).isNull();
        }

        @Test
        @DisplayName("Should set and get all properties correctly")
        void shouldSetAndGetAllPropertiesCorrectly() {
            ConfiguracionRecargos configuracion = new ConfiguracionRecargos();
            
            configuracion.setIdConfiguracionRecargo(1L);
            configuracion.setActiva(true);
            configuracion.setAplicaAConceptos("renta,servicios,mantenimiento");
            configuracion.setDiasCorteServicios(15);
            configuracion.setDiasGracia(5);
            configuracion.setMontoRecargoFijo(BigDecimal.valueOf(200.00));
            configuracion.setNombrePolitica("Test Policy");
            configuracion.setPorcentajeRecargoDiario(BigDecimal.valueOf(2.5));
            configuracion.setRecargoMaximo(BigDecimal.valueOf(2000.00));
            configuracion.setTipoRecargo("test_tipo");
            configuracion.setIdInmobiliaria(2L);
            configuracion.setTasaRecargoDiaria(BigDecimal.valueOf(0.08));
            configuracion.setTasaRecargoFija(BigDecimal.valueOf(0.15));
            configuracion.setActivo(true);
            configuracion.setDiaAplicacion(10);
            configuracion.setMonto(BigDecimal.valueOf(75.00));
            
            assertThat(configuracion.getIdConfiguracionRecargo()).isEqualTo(1L);
            assertThat(configuracion.getActiva()).isTrue();
            assertThat(configuracion.getAplicaAConceptos()).isEqualTo("renta,servicios,mantenimiento");
            assertThat(configuracion.getDiasCorteServicios()).isEqualTo(15);
            assertThat(configuracion.getDiasGracia()).isEqualTo(5);
            assertThat(configuracion.getMontoRecargoFijo()).isEqualByComparingTo(BigDecimal.valueOf(200.00));
            assertThat(configuracion.getNombrePolitica()).isEqualTo("Test Policy");
            assertThat(configuracion.getPorcentajeRecargoDiario()).isEqualByComparingTo(BigDecimal.valueOf(2.5));
            assertThat(configuracion.getRecargoMaximo()).isEqualByComparingTo(BigDecimal.valueOf(2000.00));
            assertThat(configuracion.getTipoRecargo()).isEqualTo("test_tipo");
            assertThat(configuracion.getIdInmobiliaria()).isEqualTo(2L);
            assertThat(configuracion.getTasaRecargoDiaria()).isEqualByComparingTo(BigDecimal.valueOf(0.08));
            assertThat(configuracion.getTasaRecargoFija()).isEqualByComparingTo(BigDecimal.valueOf(0.15));
            assertThat(configuracion.getActivo()).isTrue();
            assertThat(configuracion.getDiaAplicacion()).isEqualTo(10);
            assertThat(configuracion.getMonto()).isEqualByComparingTo(BigDecimal.valueOf(75.00));
        }

        @Test
        @DisplayName("Should handle null values appropriately")
        void shouldHandleNullValuesAppropriately() {
            ConfiguracionRecargos configuracion = new ConfiguracionRecargos();
            
            configuracion.setAplicaAConceptos(null);
            configuracion.setMontoRecargoFijo(null);
            configuracion.setPorcentajeRecargoDiario(null);
            configuracion.setRecargoMaximo(null);
            
            assertThat(configuracion.getAplicaAConceptos()).isNull();
            assertThat(configuracion.getMontoRecargoFijo()).isNull();
            assertThat(configuracion.getPorcentajeRecargoDiario()).isNull();
            assertThat(configuracion.getRecargoMaximo()).isNull();
        }

        @Test
        @DisplayName("Should support decimal precision for rates and amounts")
        void shouldSupportDecimalPrecisionForRatesAndAmounts() {
            ConfiguracionRecargos configuracion = new ConfiguracionRecargos();
            
            BigDecimal preciseTasa = new BigDecimal("0.05678");
            BigDecimal preciseMonto = new BigDecimal("123.4567");
            
            configuracion.setTasaRecargoDiaria(preciseTasa);
            configuracion.setMonto(preciseMonto);
            
            assertThat(configuracion.getTasaRecargoDiaria()).isEqualByComparingTo(preciseTasa);
            assertThat(configuracion.getMonto()).isEqualByComparingTo(preciseMonto);
        }
    }

    @Nested
    @DisplayName("Business Logic Validation Tests")
    class BusinessLogicValidationTests {

        @Test
        @DisplayName("Should validate business rules for percentage values")
        void shouldValidateBusinessRulesForPercentageValues() {
            validConfiguracion.setPorcentajeRecargoDiario(BigDecimal.valueOf(50.0));
            validConfiguracion.setTasaRecargoDiaria(BigDecimal.valueOf(0.5));
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should validate consistent active status flags")
        void shouldValidateConsistentActiveStatusFlags() {
            validConfiguracion.setActiva(true);
            validConfiguracion.setActivo(true);
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should validate day application range")
        void shouldValidateDayApplicationRange() {
            validConfiguracion.setDiaAplicacion(1);
            Set<ConstraintViolation<ConfiguracionRecargos>> violations1 = validator.validate(validConfiguracion);
            assertThat(violations1).isEmpty();
            
            validConfiguracion.setDiaAplicacion(31);
            Set<ConstraintViolation<ConfiguracionRecargos>> violations2 = validator.validate(validConfiguracion);
            assertThat(violations2).isEmpty();
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle zero values for some numeric fields but fail for others")
        void shouldHandleZeroValuesForSomeNumericFields() {
            validConfiguracion.setDiasCorteServicios(0);  // @Min(0) - valid
            validConfiguracion.setDiasGracia(0);  // @Min(0) - valid 
            validConfiguracion.setPorcentajeRecargoDiario(BigDecimal.ZERO);  // @DecimalMin("0.0") - valid
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).isEmpty();
        }
        
        @Test
        @DisplayName("Should fail validation for zero on fields with inclusive=false")
        void shouldFailValidationForZeroOnFieldsWithInclusiveFalse() {
            validConfiguracion.setMonto(BigDecimal.ZERO);  // @DecimalMin(value = "0.0", inclusive = false) - invalid
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("monto");
        }
        
        @Test
        @DisplayName("Should fail validation when dia aplicacion is zero")
        void shouldFailValidationWhenDiaAplicacionIsZero() {
            validConfiguracion.setDiaAplicacion(0);  // @Min(1) - invalid
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("diaAplicacion");
        }

        @Test
        @DisplayName("Should handle minimum valid string lengths")
        void shouldHandleMinimumValidStringLengths() {
            validConfiguracion.setTipoRecargo("A");
            validConfiguracion.setNombrePolitica("B");
            validConfiguracion.setAplicaAConceptos("C");
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle maximum valid string lengths")
        void shouldHandleMaximumValidStringLengths() {
            validConfiguracion.setTipoRecargo("a".repeat(255));
            validConfiguracion.setNombrePolitica("b".repeat(100));
            validConfiguracion.setAplicaAConceptos("c".repeat(200));
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle maximum valid percentage values")
        void shouldHandleMaximumValidPercentageValues() {
            validConfiguracion.setPorcentajeRecargoDiario(BigDecimal.valueOf(100.0));  // @DecimalMax("100.0")
            validConfiguracion.setTasaRecargoDiaria(BigDecimal.valueOf(999.0));  // No max constraint
            validConfiguracion.setTasaRecargoFija(BigDecimal.valueOf(999.0));  // No max constraint
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle valid day range boundaries")
        void shouldHandleValidDayRangeBoundaries() {
            validConfiguracion.setDiaAplicacion(1);
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations1 = validator.validate(validConfiguracion);
            assertThat(violations1).isEmpty();
            
            validConfiguracion.setDiaAplicacion(31);
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations2 = validator.validate(validConfiguracion);
            assertThat(violations2).isEmpty();
        }

        @Test
        @DisplayName("Should handle complex concept combinations")
        void shouldHandleComplexConceptCombinations() {
            validConfiguracion.setAplicaAConceptos("renta,servicios,mantenimiento,administracion,seguros");
            
            Set<ConstraintViolation<ConfiguracionRecargos>> violations = validator.validate(validConfiguracion);
            
            assertThat(violations).isEmpty();
        }
    }
}