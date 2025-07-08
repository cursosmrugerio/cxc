package com.inmobiliaria.gestion.propiedad.service;

import com.inmobiliaria.gestion.propiedad.dto.PropiedadCreateRequest;
import com.inmobiliaria.gestion.propiedad.dto.PropiedadDTO;
import com.inmobiliaria.gestion.propiedad.dto.PropiedadUpdateRequest;
import com.inmobiliaria.gestion.propiedad.model.Propiedad;
import com.inmobiliaria.gestion.propiedad.repository.PropiedadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PropiedadService {

    private final PropiedadRepository propiedadRepository;

    public List<PropiedadDTO> getAllPropiedades() {
        log.info("Fetching all propiedades");
        return propiedadRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<PropiedadDTO> getPropiedadById(Integer id) {
        log.info("Fetching propiedad with id: {}", id);
        return propiedadRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<PropiedadDTO> getPropiedadesByInmobiliaria(Long idInmobiliaria) {
        log.info("Fetching propiedades for inmobiliaria: {}", idInmobiliaria);
        return propiedadRepository.findByIdInmobiliaria(idInmobiliaria)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PropiedadDTO> getPropiedadesByEstatus(String estatus) {
        log.info("Fetching propiedades with estatus: {}", estatus);
        return propiedadRepository.findByEstatusPropiedad(estatus)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PropiedadDTO> getPropiedadesByTipo(String tipo) {
        log.info("Fetching propiedades with tipo: {}", tipo);
        return propiedadRepository.findByTipoPropiedad(tipo)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PropiedadDTO> getPropiedadesByInmobiliariaAndEstatus(Long idInmobiliaria, String estatus) {
        log.info("Fetching propiedades for inmobiliaria: {} with estatus: {}", idInmobiliaria, estatus);
        return propiedadRepository.findByIdInmobiliariaAndEstatusPropiedad(idInmobiliaria, estatus)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PropiedadDTO> getPropiedadesByInmobiliariaAndTipo(Long idInmobiliaria, String tipo) {
        log.info("Fetching propiedades for inmobiliaria: {} with tipo: {}", idInmobiliaria, tipo);
        return propiedadRepository.findByIdInmobiliariaAndTipoPropiedad(idInmobiliaria, tipo)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PropiedadDTO> searchPropiedadesByDireccion(String direccion) {
        log.info("Searching propiedades by direccion containing: {}", direccion);
        return propiedadRepository.findByDireccionContaining(direccion)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PropiedadDTO createPropiedad(PropiedadCreateRequest request) {
        log.info("Creating new propiedad for inmobiliaria: {}", request.idInmobiliaria());
        
        Propiedad propiedad = Propiedad.builder()
                .idInmobiliaria(request.idInmobiliaria())
                .tipoPropiedad(request.tipoPropiedad())
                .superficieTotal(request.superficieTotal())
                .superficieConstruida(request.superficieConstruida())
                .estatusPropiedad(request.estatusPropiedad() != null ? request.estatusPropiedad() : "DISPONIBLE")
                .caracteristicasEspeciales(request.caracteristicasEspeciales())
                .direccionCompleta(request.direccionCompleta())
                .numeroBanos(request.numeroBanos())
                .numeroHabitaciones(request.numeroHabitaciones())
                .build();

        Propiedad savedPropiedad = propiedadRepository.save(propiedad);
        log.info("Propiedad created successfully with id: {}", savedPropiedad.getIdPropiedad());
        
        return convertToDTO(savedPropiedad);
    }

    public Optional<PropiedadDTO> updatePropiedad(Integer id, PropiedadUpdateRequest request) {
        log.info("Updating propiedad with id: {}", id);
        
        return propiedadRepository.findById(id)
                .map(existingPropiedad -> {
                    updatePropiedadFields(existingPropiedad, request);
                    Propiedad updatedPropiedad = propiedadRepository.save(existingPropiedad);
                    log.info("Propiedad updated successfully with id: {}", updatedPropiedad.getIdPropiedad());
                    return convertToDTO(updatedPropiedad);
                });
    }

    public boolean deletePropiedad(Integer id) {
        log.info("Deleting propiedad with id: {}", id);
        
        if (propiedadRepository.existsById(id)) {
            propiedadRepository.deleteById(id);
            log.info("Propiedad deleted successfully with id: {}", id);
            return true;
        }
        
        log.warn("Propiedad not found with id: {}", id);
        return false;
    }

    public boolean existsPropiedadByIdAndInmobiliaria(Integer idPropiedad, Long idInmobiliaria) {
        return propiedadRepository.existsByIdPropiedadAndIdInmobiliaria(idPropiedad, idInmobiliaria);
    }

    public long countPropiedadesByInmobiliaria(Long idInmobiliaria) {
        return propiedadRepository.countByIdInmobiliaria(idInmobiliaria);
    }

    public long countPropiedadesByInmobiliariaAndEstatus(Long idInmobiliaria, String estatus) {
        return propiedadRepository.countByIdInmobiliariaAndEstatusPropiedad(idInmobiliaria, estatus);
    }

    private void updatePropiedadFields(Propiedad propiedad, PropiedadUpdateRequest request) {
        if (request.tipoPropiedad() != null) {
            propiedad.setTipoPropiedad(request.tipoPropiedad());
        }
        if (request.superficieTotal() != null) {
            propiedad.setSuperficieTotal(request.superficieTotal());
        }
        if (request.superficieConstruida() != null) {
            propiedad.setSuperficieConstruida(request.superficieConstruida());
        }
        if (request.estatusPropiedad() != null) {
            propiedad.setEstatusPropiedad(request.estatusPropiedad());
        }
        if (request.caracteristicasEspeciales() != null) {
            propiedad.setCaracteristicasEspeciales(request.caracteristicasEspeciales());
        }
        if (request.direccionCompleta() != null) {
            propiedad.setDireccionCompleta(request.direccionCompleta());
        }
        if (request.numeroBanos() != null) {
            propiedad.setNumeroBanos(request.numeroBanos());
        }
        if (request.numeroHabitaciones() != null) {
            propiedad.setNumeroHabitaciones(request.numeroHabitaciones());
        }
    }

    private PropiedadDTO convertToDTO(Propiedad propiedad) {
        return new PropiedadDTO(
                propiedad.getIdPropiedad(),
                propiedad.getIdInmobiliaria(),
                propiedad.getTipoPropiedad(),
                propiedad.getSuperficieTotal(),
                propiedad.getSuperficieConstruida(),
                propiedad.getFechaRegistro(),
                propiedad.getEstatusPropiedad(),
                propiedad.getCaracteristicasEspeciales(),
                propiedad.getDireccionCompleta(),
                propiedad.getNumeroBanos(),
                propiedad.getNumeroHabitaciones()
        );
    }
}