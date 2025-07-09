package com.inmobiliaria.gestion.conceptos.config;

import com.inmobiliaria.gestion.conceptos.model.ConceptosPago;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@TestConfiguration
@Profile("test")
public class ConceptosPagoTestConfig {

    @Bean
    public ConceptosPagoTestDataFactory conceptosPagoTestDataFactory() {
        return new ConceptosPagoTestDataFactory();
    }

    public static class ConceptosPagoTestDataFactory {

        public ConceptosPago createRentaConcepto() {
            return createConcepto(
                    "Renta Mensual",
                    "Pago de renta mensual",
                    "RENTA",
                    1L,
                    true,
                    true
            );
        }

        public ConceptosPago createServiciosConcepto() {
            return createConcepto(
                    "Servicios Básicos",
                    "Pago de servicios básicos (agua, luz, gas)",
                    "SERVICIOS",
                    1L,
                    true,
                    true
            );
        }

        public ConceptosPago createMantenimientoConcepto() {
            return createConcepto(
                    "Mantenimiento",
                    "Pago de mantenimiento de áreas comunes",
                    "MANTENIMIENTO",
                    1L,
                    false,
                    true
            );
        }

        public ConceptosPago createDepositoConcepto() {
            return createConcepto(
                    "Depósito en Garantía",
                    "Depósito en garantía por daños",
                    "DEPOSITO",
                    1L,
                    false,
                    true
            );
        }

        public ConceptosPago createInactiveConcepto() {
            return createConcepto(
                    "Concepto Inactivo",
                    "Concepto que está inactivo",
                    "OTRO",
                    1L,
                    false,
                    false
            );
        }

        public ConceptosPago createConceptoForInmobiliaria(Long idInmobiliaria) {
            return createConcepto(
                    "Concepto Inmobiliaria " + idInmobiliaria,
                    "Concepto específico para inmobiliaria " + idInmobiliaria,
                    "RENTA",
                    idInmobiliaria,
                    true,
                    true
            );
        }

        public ConceptosPago createConcepto(String nombre, String descripcion, String tipo, 
                                          Long idInmobiliaria, Boolean permiteRecargos, Boolean activo) {
            ConceptosPago concepto = new ConceptosPago();
            concepto.setNombreConcepto(nombre);
            concepto.setDescripcion(descripcion);
            concepto.setTipoConcepto(tipo);
            concepto.setIdInmobiliaria(idInmobiliaria);
            concepto.setPermiteRecargos(permiteRecargos);
            concepto.setActivo(activo);
            concepto.setFechaCreacion(LocalDate.now());
            return concepto;
        }

        public ConceptosPago createConceptoWithId(Integer id, String nombre, String descripcion, 
                                                String tipo, Long idInmobiliaria, Boolean permiteRecargos, Boolean activo) {
            ConceptosPago concepto = createConcepto(nombre, descripcion, tipo, idInmobiliaria, permiteRecargos, activo);
            concepto.setIdConcepto(id);
            return concepto;
        }

        public List<ConceptosPago> createMultipleConceptos(int count, Long idInmobiliaria) {
            return Arrays.asList(
                    createConcepto("Concepto 1", "Descripción 1", "RENTA", idInmobiliaria, true, true),
                    createConcepto("Concepto 2", "Descripción 2", "SERVICIOS", idInmobiliaria, true, true),
                    createConcepto("Concepto 3", "Descripción 3", "MANTENIMIENTO", idInmobiliaria, false, true),
                    createConcepto("Concepto 4", "Descripción 4", "DEPOSITO", idInmobiliaria, false, false),
                    createConcepto("Concepto 5", "Descripción 5", "OTRO", idInmobiliaria, true, true)
            ).subList(0, Math.min(count, 5));
        }

        public List<ConceptosPago> createConceptosForDifferentInmobiliarias() {
            return Arrays.asList(
                    createConcepto("Renta Inmobiliaria 1", "Renta para inmobiliaria 1", "RENTA", 1L, true, true),
                    createConcepto("Renta Inmobiliaria 2", "Renta para inmobiliaria 2", "RENTA", 2L, true, true),
                    createConcepto("Servicios Inmobiliaria 1", "Servicios para inmobiliaria 1", "SERVICIOS", 1L, true, true),
                    createConcepto("Servicios Inmobiliaria 3", "Servicios para inmobiliaria 3", "SERVICIOS", 3L, true, true)
            );
        }

        public List<ConceptosPago> createConceptosWithDifferentStatus() {
            return Arrays.asList(
                    createConcepto("Concepto Activo 1", "Concepto activo", "RENTA", 1L, true, true),
                    createConcepto("Concepto Activo 2", "Concepto activo", "SERVICIOS", 1L, true, true),
                    createConcepto("Concepto Inactivo 1", "Concepto inactivo", "MANTENIMIENTO", 1L, false, false),
                    createConcepto("Concepto Inactivo 2", "Concepto inactivo", "DEPOSITO", 1L, false, false)
            );
        }

        public List<ConceptosPago> createConceptosWithDifferentRecargos() {
            return Arrays.asList(
                    createConcepto("Con Recargos 1", "Permite recargos", "RENTA", 1L, true, true),
                    createConcepto("Con Recargos 2", "Permite recargos", "SERVICIOS", 1L, true, true),
                    createConcepto("Sin Recargos 1", "No permite recargos", "MANTENIMIENTO", 1L, false, true),
                    createConcepto("Sin Recargos 2", "No permite recargos", "DEPOSITO", 1L, false, true)
            );
        }
    }
}