package com.inmobiliaria.gestion.inmobiliaria.service;

import com.inmobiliaria.gestion.inmobiliaria.dto.InmobiliariaDTO;
import com.inmobiliaria.gestion.inmobiliaria.model.Inmobiliaria;
import com.inmobiliaria.gestion.inmobiliaria.repository.InmobiliariaRepository;
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
public class InmobiliariaService {

    private final InmobiliariaRepository inmobiliariaRepository;

    @Transactional(readOnly = true)
    public List<InmobiliariaDTO> findAll() {
        log.debug("Finding all inmobiliarias");
        return inmobiliariaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<InmobiliariaDTO> findAll(Pageable pageable) {
        log.debug("Finding all inmobiliarias with pagination: {}", pageable);
        return inmobiliariaRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Optional<InmobiliariaDTO> findById(Long id) {
        log.debug("Finding inmobiliaria by id: {}", id);
        return inmobiliariaRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Optional<InmobiliariaDTO> findByRfcNit(String rfcNit) {
        log.debug("Finding inmobiliaria by RFC/NIT: {}", rfcNit);
        return inmobiliariaRepository.findByRfcNit(rfcNit)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<InmobiliariaDTO> findByEstatus(String estatus) {
        log.debug("Finding inmobiliarias by status: {}", estatus);
        return inmobiliariaRepository.findByEstatus(estatus)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<InmobiliariaDTO> findByEstatus(String estatus, Pageable pageable) {
        log.debug("Finding inmobiliarias by status: {} with pagination: {}", estatus, pageable);
        return inmobiliariaRepository.findByEstatus(estatus, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<InmobiliariaDTO> findByNombreComercial(String nombreComercial, Pageable pageable) {
        log.debug("Finding inmobiliarias by commercial name containing: {} with pagination: {}", nombreComercial, pageable);
        return inmobiliariaRepository.findByNombreComercialContainingIgnoreCase(nombreComercial, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<InmobiliariaDTO> findByCiudad(String ciudad, Pageable pageable) {
        log.debug("Finding inmobiliarias by city: {} with pagination: {}", ciudad, pageable);
        return inmobiliariaRepository.findByCiudadIgnoreCase(ciudad, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<InmobiliariaDTO> findByEstado(String estado, Pageable pageable) {
        log.debug("Finding inmobiliarias by state: {} with pagination: {}", estado, pageable);
        return inmobiliariaRepository.findByEstadoIgnoreCase(estado, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<InmobiliariaDTO> findByFilters(String nombreComercial, String ciudad, String estado, String estatus, Pageable pageable) {
        log.debug("Finding inmobiliarias by filters - name: {}, city: {}, state: {}, status: {} with pagination: {}", 
                 nombreComercial, ciudad, estado, estatus, pageable);
        return inmobiliariaRepository.findByFilters(nombreComercial, ciudad, estado, estatus, pageable)
                .map(this::convertToDTO);
    }

    public InmobiliariaDTO create(InmobiliariaDTO inmobiliariaDTO) {
        log.debug("Creating new inmobiliaria: {}", inmobiliariaDTO.nombreComercial());
        
        if (inmobiliariaRepository.existsByRfcNit(inmobiliariaDTO.rfcNit())) {
            throw new IllegalArgumentException("RFC/NIT already exists: " + inmobiliariaDTO.rfcNit());
        }

        Inmobiliaria inmobiliaria = convertToEntity(inmobiliariaDTO);
        inmobiliaria.setIdInmobiliaria(null); // Ensure it's a new entity
        if (inmobiliaria.getFechaRegistro() == null) {
            inmobiliaria.setFechaRegistro(LocalDate.now());
        }
        if (inmobiliaria.getEstatus() == null || inmobiliaria.getEstatus().isBlank()) {
            inmobiliaria.setEstatus("ACTIVE");
        }

        Inmobiliaria savedInmobiliaria = inmobiliariaRepository.save(inmobiliaria);
        log.info("Created inmobiliaria with id: {}", savedInmobiliaria.getIdInmobiliaria());
        
        return convertToDTO(savedInmobiliaria);
    }

    public InmobiliariaDTO update(Long id, InmobiliariaDTO inmobiliariaDTO) {
        log.debug("Updating inmobiliaria with id: {}", id);
        
        Inmobiliaria existingInmobiliaria = inmobiliariaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inmobiliaria not found with id: " + id));

        // Check if RFC/NIT is being changed and if it already exists
        if (!existingInmobiliaria.getRfcNit().equals(inmobiliariaDTO.rfcNit()) &&
            inmobiliariaRepository.existsByRfcNitAndIdInmobiliariaNot(inmobiliariaDTO.rfcNit(), id)) {
            throw new IllegalArgumentException("RFC/NIT already exists: " + inmobiliariaDTO.rfcNit());
        }

        // Update fields
        existingInmobiliaria.setNombreComercial(inmobiliariaDTO.nombreComercial());
        existingInmobiliaria.setRazonSocial(inmobiliariaDTO.razonSocial());
        existingInmobiliaria.setRfcNit(inmobiliariaDTO.rfcNit());
        existingInmobiliaria.setTelefonoPrincipal(inmobiliariaDTO.telefonoPrincipal());
        existingInmobiliaria.setEmailContacto(inmobiliariaDTO.emailContacto());
        existingInmobiliaria.setDireccionCompleta(inmobiliariaDTO.direccionCompleta());
        existingInmobiliaria.setCiudad(inmobiliariaDTO.ciudad());
        existingInmobiliaria.setEstado(inmobiliariaDTO.estado());
        existingInmobiliaria.setCodigoPostal(inmobiliariaDTO.codigoPostal());
        existingInmobiliaria.setPersonaContacto(inmobiliariaDTO.personaContacto());
        existingInmobiliaria.setEstatus(inmobiliariaDTO.estatus());

        Inmobiliaria updatedInmobiliaria = inmobiliariaRepository.save(existingInmobiliaria);
        log.info("Updated inmobiliaria with id: {}", updatedInmobiliaria.getIdInmobiliaria());
        
        return convertToDTO(updatedInmobiliaria);
    }

    public void deleteById(Long id) {
        log.debug("Deleting inmobiliaria with id: {}", id);
        
        if (!inmobiliariaRepository.existsById(id)) {
            throw new IllegalArgumentException("Inmobiliaria not found with id: " + id);
        }

        inmobiliariaRepository.deleteById(id);
        log.info("Deleted inmobiliaria with id: {}", id);
    }

    public InmobiliariaDTO changeStatus(Long id, String newStatus) {
        log.debug("Changing status of inmobiliaria with id: {} to: {}", id, newStatus);
        
        Inmobiliaria inmobiliaria = inmobiliariaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inmobiliaria not found with id: " + id));

        inmobiliaria.setEstatus(newStatus);
        Inmobiliaria updatedInmobiliaria = inmobiliariaRepository.save(inmobiliaria);
        log.info("Changed status of inmobiliaria with id: {} to: {}", id, newStatus);
        
        return convertToDTO(updatedInmobiliaria);
    }

    @Transactional(readOnly = true)
    public long countByEstatus(String estatus) {
        log.debug("Counting inmobiliarias by status: {}", estatus);
        return inmobiliariaRepository.countByEstatus(estatus);
    }

    @Transactional(readOnly = true)
    public List<String> getDistinctCiudades() {
        log.debug("Getting distinct cities");
        return inmobiliariaRepository.findDistinctCiudades();
    }

    @Transactional(readOnly = true)
    public List<String> getDistinctEstados() {
        log.debug("Getting distinct states");
        return inmobiliariaRepository.findDistinctEstados();
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return inmobiliariaRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByRfcNit(String rfcNit) {
        return inmobiliariaRepository.existsByRfcNit(rfcNit);
    }

    private InmobiliariaDTO convertToDTO(Inmobiliaria inmobiliaria) {
        return new InmobiliariaDTO(
                inmobiliaria.getIdInmobiliaria(),
                inmobiliaria.getNombreComercial(),
                inmobiliaria.getRazonSocial(),
                inmobiliaria.getRfcNit(),
                inmobiliaria.getTelefonoPrincipal(),
                inmobiliaria.getEmailContacto(),
                inmobiliaria.getDireccionCompleta(),
                inmobiliaria.getCiudad(),
                inmobiliaria.getEstado(),
                inmobiliaria.getCodigoPostal(),
                inmobiliaria.getPersonaContacto(),
                inmobiliaria.getFechaRegistro(),
                inmobiliaria.getEstatus()
        );
    }

    private Inmobiliaria convertToEntity(InmobiliariaDTO dto) {
        return Inmobiliaria.builder()
                .idInmobiliaria(dto.idInmobiliaria())
                .nombreComercial(dto.nombreComercial())
                .razonSocial(dto.razonSocial())
                .rfcNit(dto.rfcNit())
                .telefonoPrincipal(dto.telefonoPrincipal())
                .emailContacto(dto.emailContacto())
                .direccionCompleta(dto.direccionCompleta())
                .ciudad(dto.ciudad())
                .estado(dto.estado())
                .codigoPostal(dto.codigoPostal())
                .personaContacto(dto.personaContacto())
                .fechaRegistro(dto.fechaRegistro())
                .estatus(dto.estatus())
                .build();
    }
}