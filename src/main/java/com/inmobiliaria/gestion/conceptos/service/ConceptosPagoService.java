package com.inmobiliaria.gestion.conceptos.service;

import com.inmobiliaria.gestion.conceptos.dto.ConceptosPagoCreateRequest;
import com.inmobiliaria.gestion.conceptos.dto.ConceptosPagoDTO;
import com.inmobiliaria.gestion.conceptos.dto.ConceptosPagoUpdateRequest;
import com.inmobiliaria.gestion.conceptos.model.ConceptosPago;
import com.inmobiliaria.gestion.conceptos.repository.ConceptosPagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ConceptosPagoService {

    private final ConceptosPagoRepository conceptosPagoRepository;

    @Transactional(readOnly = true)
    public List<ConceptosPagoDTO> findAll() {
        log.debug("Finding all conceptos de pago");
        return conceptosPagoRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ConceptosPagoDTO> findAll(Pageable pageable) {
        log.debug("Finding all conceptos de pago with pagination: {}", pageable);
        return conceptosPagoRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Optional<ConceptosPagoDTO> findById(Integer id) {
        log.debug("Finding concepto de pago by id: {}", id);
        return conceptosPagoRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<ConceptosPagoDTO> findByInmobiliaria(Long idInmobiliaria) {
        log.debug("Finding conceptos de pago by inmobiliaria id: {}", idInmobiliaria);
        return conceptosPagoRepository.findByIdInmobiliaria(idInmobiliaria)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ConceptosPagoDTO> findByInmobiliaria(Long idInmobiliaria, Pageable pageable) {
        log.debug("Finding conceptos de pago by inmobiliaria id: {} with pagination: {}", idInmobiliaria, pageable);
        return conceptosPagoRepository.findByIdInmobiliaria(idInmobiliaria, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<ConceptosPagoDTO> findByInmobiliariaAndActivo(Long idInmobiliaria, Boolean activo) {
        log.debug("Finding conceptos de pago by inmobiliaria id: {} and activo: {}", idInmobiliaria, activo);
        return conceptosPagoRepository.findByIdInmobiliariaAndActivo(idInmobiliaria, activo)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ConceptosPagoDTO> findByInmobiliariaAndActivo(Long idInmobiliaria, Boolean activo, Pageable pageable) {
        log.debug("Finding conceptos de pago by inmobiliaria id: {} and activo: {} with pagination: {}", idInmobiliaria, activo, pageable);
        return conceptosPagoRepository.findByIdInmobiliariaAndActivo(idInmobiliaria, activo, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<ConceptosPagoDTO> findByTipoConcepto(String tipoConcepto, Pageable pageable) {
        log.debug("Finding conceptos de pago by tipo concepto: {} with pagination: {}", tipoConcepto, pageable);
        return conceptosPagoRepository.findByTipoConcepto(tipoConcepto, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<ConceptosPagoDTO> findByInmobiliariaAndTipoConcepto(Long idInmobiliaria, String tipoConcepto, Pageable pageable) {
        log.debug("Finding conceptos de pago by inmobiliaria id: {} and tipo concepto: {} with pagination: {}", idInmobiliaria, tipoConcepto, pageable);
        return conceptosPagoRepository.findByIdInmobiliariaAndTipoConcepto(idInmobiliaria, tipoConcepto, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<ConceptosPagoDTO> findByNombreConcepto(String nombreConcepto, Pageable pageable) {
        log.debug("Finding conceptos de pago by nombre concepto containing: {} with pagination: {}", nombreConcepto, pageable);
        return conceptosPagoRepository.findByNombreConceptoContainingIgnoreCase(nombreConcepto, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<ConceptosPagoDTO> findByInmobiliariaAndNombreConcepto(Long idInmobiliaria, String nombreConcepto, Pageable pageable) {
        log.debug("Finding conceptos de pago by inmobiliaria id: {} and nombre concepto containing: {} with pagination: {}", idInmobiliaria, nombreConcepto, pageable);
        return conceptosPagoRepository.findByIdInmobiliariaAndNombreConceptoContainingIgnoreCase(idInmobiliaria, nombreConcepto, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<ConceptosPagoDTO> findByPermiteRecargos(Boolean permiteRecargos, Pageable pageable) {
        log.debug("Finding conceptos de pago by permite recargos: {} with pagination: {}", permiteRecargos, pageable);
        return conceptosPagoRepository.findByPermiteRecargos(permiteRecargos, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<ConceptosPagoDTO> findByInmobiliariaAndPermiteRecargos(Long idInmobiliaria, Boolean permiteRecargos, Pageable pageable) {
        log.debug("Finding conceptos de pago by inmobiliaria id: {} and permite recargos: {} with pagination: {}", idInmobiliaria, permiteRecargos, pageable);
        return conceptosPagoRepository.findByIdInmobiliariaAndPermiteRecargos(idInmobiliaria, permiteRecargos, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<ConceptosPagoDTO> findByFilters(Long idInmobiliaria, String nombreConcepto, String tipoConcepto, Boolean activo, Boolean permiteRecargos, Pageable pageable) {
        log.debug("Finding conceptos de pago by filters - inmobiliaria: {}, nombre: {}, tipo: {}, activo: {}, permite recargos: {} with pagination: {}", 
                 idInmobiliaria, nombreConcepto, tipoConcepto, activo, permiteRecargos, pageable);
        return conceptosPagoRepository.findByFilters(idInmobiliaria, nombreConcepto, tipoConcepto, activo, permiteRecargos, pageable)
                .map(this::convertToDTO);
    }

    public ConceptosPagoDTO create(ConceptosPagoCreateRequest request) {
        log.debug("Creating new concepto de pago: {}", request.nombreConcepto());
        
        if (conceptosPagoRepository.existsByIdInmobiliariaAndNombreConcepto(request.idInmobiliaria(), request.nombreConcepto())) {
            throw new IllegalArgumentException("Concept name already exists for this inmobiliaria: " + request.nombreConcepto());
        }

        ConceptosPago conceptoPago = convertToEntity(request);
        conceptoPago.setIdConcepto(null); // Ensure it's a new entity
        if (conceptoPago.getFechaCreacion() == null) {
            conceptoPago.setFechaCreacion(LocalDate.now());
        }
        if (conceptoPago.getActivo() == null) {
            conceptoPago.setActivo(true);
        }
        if (conceptoPago.getPermiteRecargos() == null) {
            conceptoPago.setPermiteRecargos(false);
        }

        ConceptosPago savedConcepto = conceptosPagoRepository.save(conceptoPago);
        log.info("Created concepto de pago with id: {}", savedConcepto.getIdConcepto());
        
        return convertToDTO(savedConcepto);
    }

    public ConceptosPagoDTO update(Integer id, ConceptosPagoUpdateRequest request) {
        log.debug("Updating concepto de pago with id: {}", id);
        
        ConceptosPago existingConcepto = conceptosPagoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Concepto de pago not found with id: " + id));

        // Check if concept name is being changed and if it already exists for this inmobiliaria
        if (!existingConcepto.getNombreConcepto().equals(request.nombreConcepto()) &&
            conceptosPagoRepository.existsByIdInmobiliariaAndNombreConceptoAndIdConceptoNot(
                    existingConcepto.getIdInmobiliaria(), request.nombreConcepto(), id)) {
            throw new IllegalArgumentException("Concept name already exists for this inmobiliaria: " + request.nombreConcepto());
        }

        // Update fields
        existingConcepto.setNombreConcepto(request.nombreConcepto());
        existingConcepto.setDescripcion(request.descripcion());
        existingConcepto.setTipoConcepto(request.tipoConcepto());
        existingConcepto.setPermiteRecargos(request.permiteRecargos() != null ? request.permiteRecargos() : false);
        existingConcepto.setActivo(request.activo() != null ? request.activo() : true);

        ConceptosPago updatedConcepto = conceptosPagoRepository.save(existingConcepto);
        log.info("Updated concepto de pago with id: {}", updatedConcepto.getIdConcepto());
        
        return convertToDTO(updatedConcepto);
    }

    public void deleteById(Integer id) {
        log.debug("Deleting concepto de pago with id: {}", id);
        
        if (!conceptosPagoRepository.existsById(id)) {
            throw new IllegalArgumentException("Concepto de pago not found with id: " + id);
        }

        conceptosPagoRepository.deleteById(id);
        log.info("Deleted concepto de pago with id: {}", id);
    }

    public ConceptosPagoDTO changeStatus(Integer id, Boolean activo) {
        log.debug("Changing status of concepto de pago with id: {} to: {}", id, activo);
        
        ConceptosPago concepto = conceptosPagoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Concepto de pago not found with id: " + id));

        concepto.setActivo(activo);
        ConceptosPago updatedConcepto = conceptosPagoRepository.save(concepto);
        log.info("Changed status of concepto de pago with id: {} to: {}", id, activo);
        
        return convertToDTO(updatedConcepto);
    }

    @Transactional(readOnly = true)
    public long countActiveByInmobiliaria(Long idInmobiliaria) {
        log.debug("Counting active conceptos de pago by inmobiliaria: {}", idInmobiliaria);
        return conceptosPagoRepository.countActiveByInmobiliaria(idInmobiliaria);
    }

    @Transactional(readOnly = true)
    public List<String> getDistinctTiposConcepto() {
        log.debug("Getting distinct tipos concepto");
        return conceptosPagoRepository.findDistinctTiposConcepto();
    }

    @Transactional(readOnly = true)
    public List<String> getDistinctTiposConceptoByInmobiliaria(Long idInmobiliaria) {
        log.debug("Getting distinct tipos concepto by inmobiliaria: {}", idInmobiliaria);
        return conceptosPagoRepository.findDistinctTiposConceptoByInmobiliaria(idInmobiliaria);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return conceptosPagoRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByInmobiliariaAndNombreConcepto(Long idInmobiliaria, String nombreConcepto) {
        return conceptosPagoRepository.existsByIdInmobiliariaAndNombreConcepto(idInmobiliaria, nombreConcepto);
    }

    private ConceptosPagoDTO convertToDTO(ConceptosPago concepto) {
        return new ConceptosPagoDTO(
                concepto.getIdConcepto(),
                concepto.getIdInmobiliaria(),
                concepto.getNombreConcepto(),
                concepto.getDescripcion(),
                concepto.getTipoConcepto(),
                concepto.getPermiteRecargos(),
                concepto.getActivo(),
                concepto.getFechaCreacion()
        );
    }

    private ConceptosPago convertToEntity(ConceptosPagoCreateRequest request) {
        return ConceptosPago.builder()
                .idInmobiliaria(request.idInmobiliaria())
                .nombreConcepto(request.nombreConcepto())
                .descripcion(request.descripcion())
                .tipoConcepto(request.tipoConcepto())
                .permiteRecargos(request.permiteRecargos())
                .activo(request.activo())
                .build();
    }
}