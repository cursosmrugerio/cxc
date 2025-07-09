package com.inmobiliaria.gestion.conceptos.util;

import com.inmobiliaria.gestion.conceptos.dto.ConceptosPagoCreateRequest;
import com.inmobiliaria.gestion.conceptos.dto.ConceptosPagoDTO;
import com.inmobiliaria.gestion.conceptos.dto.ConceptosPagoUpdateRequest;
import com.inmobiliaria.gestion.conceptos.model.ConceptosPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ConceptosPagoTestUtils {

    public static final Long DEFAULT_INMOBILIARIA_ID = 1L;
    public static final String DEFAULT_NOMBRE_CONCEPTO = "Renta Mensual";
    public static final String DEFAULT_DESCRIPCION = "Pago de renta mensual";
    public static final String DEFAULT_TIPO_CONCEPTO = "RENTA";
    public static final Boolean DEFAULT_PERMITE_RECARGOS = true;
    public static final Boolean DEFAULT_ACTIVO = true;
    public static final LocalDate DEFAULT_FECHA_CREACION = LocalDate.now();

    // Model builders
    public static ConceptosPago.ConceptosPagoBuilder defaultConceptosPagoBuilder() {
        return ConceptosPago.builder()
                .idInmobiliaria(DEFAULT_INMOBILIARIA_ID)
                .nombreConcepto(DEFAULT_NOMBRE_CONCEPTO)
                .descripcion(DEFAULT_DESCRIPCION)
                .tipoConcepto(DEFAULT_TIPO_CONCEPTO)
                .permiteRecargos(DEFAULT_PERMITE_RECARGOS)
                .activo(DEFAULT_ACTIVO)
                .fechaCreacion(DEFAULT_FECHA_CREACION);
    }

    public static ConceptosPago createDefaultConceptosPago() {
        return defaultConceptosPagoBuilder().build();
    }

    public static ConceptosPago createConceptosPagoWithId(Integer id) {
        return defaultConceptosPagoBuilder()
                .idConcepto(id)
                .build();
    }

    public static ConceptosPago createConceptosPagoForInmobiliaria(Long idInmobiliaria) {
        return defaultConceptosPagoBuilder()
                .idInmobiliaria(idInmobiliaria)
                .nombreConcepto("Concepto para Inmobiliaria " + idInmobiliaria)
                .build();
    }

    public static ConceptosPago createInactiveConceptosPago() {
        return defaultConceptosPagoBuilder()
                .activo(false)
                .nombreConcepto("Concepto Inactivo")
                .build();
    }

    public static ConceptosPago createConceptosPagoWithoutRecargos() {
        return defaultConceptosPagoBuilder()
                .permiteRecargos(false)
                .nombreConcepto("Concepto Sin Recargos")
                .tipoConcepto("DEPOSITO")
                .build();
    }

    public static ConceptosPago createConceptosPagoWithType(String tipoConcepto) {
        return defaultConceptosPagoBuilder()
                .tipoConcepto(tipoConcepto)
                .nombreConcepto("Concepto " + tipoConcepto)
                .build();
    }

    // DTO builders
    public static ConceptosPagoDTO createDefaultConceptosPagoDTO() {
        return new ConceptosPagoDTO(
                1,
                DEFAULT_INMOBILIARIA_ID,
                DEFAULT_NOMBRE_CONCEPTO,
                DEFAULT_DESCRIPCION,
                DEFAULT_TIPO_CONCEPTO,
                DEFAULT_PERMITE_RECARGOS,
                DEFAULT_ACTIVO,
                DEFAULT_FECHA_CREACION
        );
    }

    public static ConceptosPagoDTO createConceptosPagoDTOWithId(Integer id) {
        return new ConceptosPagoDTO(
                id,
                DEFAULT_INMOBILIARIA_ID,
                DEFAULT_NOMBRE_CONCEPTO,
                DEFAULT_DESCRIPCION,
                DEFAULT_TIPO_CONCEPTO,
                DEFAULT_PERMITE_RECARGOS,
                DEFAULT_ACTIVO,
                DEFAULT_FECHA_CREACION
        );
    }

    public static ConceptosPagoDTO createConceptosPagoDTOForInmobiliaria(Long idInmobiliaria) {
        return new ConceptosPagoDTO(
                1,
                idInmobiliaria,
                "Concepto para Inmobiliaria " + idInmobiliaria,
                DEFAULT_DESCRIPCION,
                DEFAULT_TIPO_CONCEPTO,
                DEFAULT_PERMITE_RECARGOS,
                DEFAULT_ACTIVO,
                DEFAULT_FECHA_CREACION
        );
    }

    // Request builders
    public static ConceptosPagoCreateRequest createDefaultCreateRequest() {
        return new ConceptosPagoCreateRequest(
                DEFAULT_INMOBILIARIA_ID,
                DEFAULT_NOMBRE_CONCEPTO,
                DEFAULT_DESCRIPCION,
                DEFAULT_TIPO_CONCEPTO,
                DEFAULT_PERMITE_RECARGOS,
                DEFAULT_ACTIVO
        );
    }

    public static ConceptosPagoCreateRequest createCreateRequestForInmobiliaria(Long idInmobiliaria) {
        return new ConceptosPagoCreateRequest(
                idInmobiliaria,
                "Concepto para Inmobiliaria " + idInmobiliaria,
                DEFAULT_DESCRIPCION,
                DEFAULT_TIPO_CONCEPTO,
                DEFAULT_PERMITE_RECARGOS,
                DEFAULT_ACTIVO
        );
    }

    public static ConceptosPagoCreateRequest createInvalidCreateRequest() {
        return new ConceptosPagoCreateRequest(
                null, // Invalid: null inmobiliaria ID
                "", // Invalid: empty name
                DEFAULT_DESCRIPCION,
                DEFAULT_TIPO_CONCEPTO,
                DEFAULT_PERMITE_RECARGOS,
                DEFAULT_ACTIVO
        );
    }

    public static ConceptosPagoUpdateRequest createDefaultUpdateRequest() {
        return new ConceptosPagoUpdateRequest(
                DEFAULT_NOMBRE_CONCEPTO + " Actualizado",
                DEFAULT_DESCRIPCION + " actualizada",
                DEFAULT_TIPO_CONCEPTO,
                DEFAULT_PERMITE_RECARGOS,
                DEFAULT_ACTIVO
        );
    }

    public static ConceptosPagoUpdateRequest createUpdateRequestWithStatus(Boolean activo) {
        return new ConceptosPagoUpdateRequest(
                DEFAULT_NOMBRE_CONCEPTO,
                DEFAULT_DESCRIPCION,
                DEFAULT_TIPO_CONCEPTO,
                DEFAULT_PERMITE_RECARGOS,
                activo
        );
    }

    public static ConceptosPagoUpdateRequest createInvalidUpdateRequest() {
        return new ConceptosPagoUpdateRequest(
                "", // Invalid: empty name
                DEFAULT_DESCRIPCION,
                DEFAULT_TIPO_CONCEPTO,
                DEFAULT_PERMITE_RECARGOS,
                DEFAULT_ACTIVO
        );
    }

    // Collection builders
    public static List<ConceptosPago> createMultipleConceptosPago(int count) {
        return Arrays.asList(
                createConceptosPagoWithType("RENTA"),
                createConceptosPagoWithType("SERVICIOS"),
                createConceptosPagoWithType("MANTENIMIENTO"),
                createConceptosPagoWithType("DEPOSITO"),
                createConceptosPagoWithType("OTRO")
        ).subList(0, Math.min(count, 5));
    }

    public static List<ConceptosPagoDTO> createMultipleConceptosPagoDTO(int count) {
        return Arrays.asList(
                new ConceptosPagoDTO(1, 1L, "Concepto RENTA", "Descripción RENTA", "RENTA", true, true, LocalDate.now()),
                new ConceptosPagoDTO(2, 1L, "Concepto SERVICIOS", "Descripción SERVICIOS", "SERVICIOS", true, true, LocalDate.now()),
                new ConceptosPagoDTO(3, 1L, "Concepto MANTENIMIENTO", "Descripción MANTENIMIENTO", "MANTENIMIENTO", false, true, LocalDate.now()),
                new ConceptosPagoDTO(4, 1L, "Concepto DEPOSITO", "Descripción DEPOSITO", "DEPOSITO", false, true, LocalDate.now()),
                new ConceptosPagoDTO(5, 1L, "Concepto OTRO", "Descripción OTRO", "OTRO", true, true, LocalDate.now())
        ).subList(0, Math.min(count, 5));
    }

    public static Page<ConceptosPago> createPageOfConceptosPago(List<ConceptosPago> conceptos, Pageable pageable) {
        return new PageImpl<>(conceptos, pageable, conceptos.size());
    }

    public static Page<ConceptosPagoDTO> createPageOfConceptosPagoDTO(List<ConceptosPagoDTO> conceptos, Pageable pageable) {
        return new PageImpl<>(conceptos, pageable, conceptos.size());
    }

    // Optional builders
    public static Optional<ConceptosPago> createOptionalConceptosPago() {
        return Optional.of(createDefaultConceptosPago());
    }

    public static Optional<ConceptosPago> createEmptyOptionalConceptosPago() {
        return Optional.empty();
    }

    public static Optional<ConceptosPagoDTO> createOptionalConceptosPagoDTO() {
        return Optional.of(createDefaultConceptosPagoDTO());
    }

    public static Optional<ConceptosPagoDTO> createEmptyOptionalConceptosPagoDTO() {
        return Optional.empty();
    }

    // Validation helpers
    public static boolean isValidConceptosPago(ConceptosPago concepto) {
        return concepto != null
                && concepto.getIdInmobiliaria() != null
                && concepto.getNombreConcepto() != null
                && !concepto.getNombreConcepto().trim().isEmpty()
                && concepto.getTipoConcepto() != null
                && concepto.getPermiteRecargos() != null
                && concepto.getActivo() != null
                && concepto.getFechaCreacion() != null;
    }

    public static boolean isValidConceptosPagoDTO(ConceptosPagoDTO dto) {
        return dto != null
                && dto.idInmobiliaria() != null
                && dto.nombreConcepto() != null
                && !dto.nombreConcepto().trim().isEmpty()
                && dto.tipoConcepto() != null
                && dto.permiteRecargos() != null
                && dto.activo() != null
                && dto.fechaCreacion() != null;
    }

    public static boolean isValidCreateRequest(ConceptosPagoCreateRequest request) {
        return request != null
                && request.idInmobiliaria() != null
                && request.nombreConcepto() != null
                && !request.nombreConcepto().trim().isEmpty();
    }

    public static boolean isValidUpdateRequest(ConceptosPagoUpdateRequest request) {
        return request != null
                && request.nombreConcepto() != null
                && !request.nombreConcepto().trim().isEmpty();
    }

    // Comparison helpers
    public static boolean areConceptosEqual(ConceptosPago concepto1, ConceptosPago concepto2) {
        if (concepto1 == null || concepto2 == null) {
            return concepto1 == concepto2;
        }
        
        return concepto1.getIdInmobiliaria().equals(concepto2.getIdInmobiliaria())
                && concepto1.getNombreConcepto().equals(concepto2.getNombreConcepto())
                && concepto1.getTipoConcepto().equals(concepto2.getTipoConcepto())
                && concepto1.getPermiteRecargos().equals(concepto2.getPermiteRecargos())
                && concepto1.getActivo().equals(concepto2.getActivo());
    }

    public static boolean areConceptoDTOsEqual(ConceptosPagoDTO dto1, ConceptosPagoDTO dto2) {
        if (dto1 == null || dto2 == null) {
            return dto1 == dto2;
        }
        
        return dto1.idInmobiliaria().equals(dto2.idInmobiliaria())
                && dto1.nombreConcepto().equals(dto2.nombreConcepto())
                && dto1.tipoConcepto().equals(dto2.tipoConcepto())
                && dto1.permiteRecargos().equals(dto2.permiteRecargos())
                && dto1.activo().equals(dto2.activo());
    }

    // Common test data sets
    public static class TestDataSets {
        public static final List<String> VALID_TIPOS_CONCEPTO = Arrays.asList(
                "RENTA", "SERVICIOS", "MANTENIMIENTO", "DEPOSITO", "OTRO"
        );
        
        public static final List<String> INVALID_NOMBRES_CONCEPTO = Arrays.asList(
                "", "   ", null
        );
        
        public static final List<Long> VALID_INMOBILIARIA_IDS = Arrays.asList(
                1L, 2L, 3L, 100L, 999L
        );
        
        public static final List<Long> INVALID_INMOBILIARIA_IDS = Arrays.asList(
                null, 0L, -1L
        );
    }
}