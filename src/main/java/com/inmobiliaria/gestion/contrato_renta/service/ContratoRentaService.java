package com.inmobiliaria.gestion.contrato_renta.service;

import com.inmobiliaria.gestion.contrato_renta.dto.ContratoRentaCreateRequest;
import com.inmobiliaria.gestion.contrato_renta.dto.ContratoRentaDTO;
import com.inmobiliaria.gestion.contrato_renta.dto.ContratoRentaUpdateRequest;
import com.inmobiliaria.gestion.contrato_renta.model.ContratoRenta;
import com.inmobiliaria.gestion.contrato_renta.repository.ContratoRentaRepository;
import com.inmobiliaria.gestion.propiedad.repository.PropiedadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContratoRentaService {

    private final ContratoRentaRepository contratoRentaRepository;
    private final PropiedadRepository propiedadRepository;

    public List<ContratoRentaDTO> getAllContratos() {
        log.info("Fetching all rental contracts");
        return contratoRentaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ContratoRentaDTO> getContratoById(Integer id) {
        log.info("Fetching rental contract with id: {}", id);
        return contratoRentaRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<ContratoRentaDTO> getContratosByPropiedad(Integer idPropiedad) {
        log.info("Fetching rental contracts for property: {}", idPropiedad);
        return contratoRentaRepository.findByIdPropiedad(idPropiedad)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ContratoRentaDTO> getContratosByEstatus(String estatus) {
        log.info("Fetching rental contracts with status: {}", estatus);
        return contratoRentaRepository.findByEstatusContrato(estatus)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ContratoRentaDTO> getContratosExpiringBetween(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching rental contracts expiring between {} and {}", startDate, endDate);
        return contratoRentaRepository.findByFechaFinContratoBetween(startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ContratoRentaDTO> getActiveContractsExpiringBefore(LocalDateTime date) {
        log.info("Fetching active rental contracts expiring before: {}", date);
        return contratoRentaRepository.findActiveContractsExpiringBefore(date)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ContratoRentaDTO> getContractsNeedingNotification() {
        log.info("Fetching contracts needing notification");
        LocalDateTime now = LocalDateTime.now();
        return contratoRentaRepository.findContractsNeedingNotification(now)
                .stream()
                .filter(contract -> {
                    if (contract.getNotificacionDiasPrevios() != null) {
                        LocalDateTime notificationDate = contract.getFechaFinContrato()
                                .minusDays(contract.getNotificacionDiasPrevios());
                        return now.isAfter(notificationDate) || now.isEqual(notificationDate);
                    }
                    return false;
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ContratoRentaDTO createContrato(ContratoRentaCreateRequest request) {
        log.info("Creating new rental contract for property: {}", request.idPropiedad());
        
        if (!propiedadRepository.existsById(request.idPropiedad())) {
            throw new IllegalArgumentException("Property with id " + request.idPropiedad() + " does not exist");
        }

        if (contratoRentaRepository.existsByIdPropiedadAndEstatusContrato(request.idPropiedad(), "ACTIVO")) {
            throw new IllegalArgumentException("Property already has an active rental contract");
        }

        ContratoRenta contrato = ContratoRenta.builder()
                .idPropiedad(request.idPropiedad())
                .fechaInicioContrato(request.fechaInicioContrato())
                .condicionesEspeciales(request.condicionesEspeciales())
                .emailNotificaciones(request.emailNotificaciones())
                .estatusContrato("ACTIVO")
                .depositoGarantia(request.depositoGarantia())
                .duracionMeses(request.duracionMeses())
                .notificacionDiasPrevios(request.notificacionDiasPrevios())
                .telefonoNotificaciones(request.telefonoNotificaciones())
                .build();

        ContratoRenta savedContrato = contratoRentaRepository.save(contrato);
        log.info("Rental contract created successfully with id: {}", savedContrato.getIdContrato());
        
        return convertToDTO(savedContrato);
    }

    public Optional<ContratoRentaDTO> updateContrato(Integer id, ContratoRentaUpdateRequest request) {
        log.info("Updating rental contract with id: {}", id);
        
        return contratoRentaRepository.findById(id)
                .map(existingContrato -> {
                    updateContratoFields(existingContrato, request);
                    ContratoRenta updatedContrato = contratoRentaRepository.save(existingContrato);
                    log.info("Rental contract updated successfully with id: {}", updatedContrato.getIdContrato());
                    return convertToDTO(updatedContrato);
                });
    }

    public boolean deleteContrato(Integer id) {
        log.info("Deleting rental contract with id: {}", id);
        
        if (contratoRentaRepository.existsById(id)) {
            contratoRentaRepository.deleteById(id);
            log.info("Rental contract deleted successfully with id: {}", id);
            return true;
        }
        
        log.warn("Rental contract not found with id: {}", id);
        return false;
    }

    public boolean terminateContrato(Integer id) {
        log.info("Terminating rental contract with id: {}", id);
        
        return contratoRentaRepository.findById(id)
                .map(contrato -> {
                    contrato.setEstatusContrato("TERMINADO");
                    contrato.setFechaFinContrato(LocalDateTime.now());
                    contratoRentaRepository.save(contrato);
                    log.info("Rental contract terminated successfully with id: {}", id);
                    return true;
                })
                .orElse(false);
    }

    public boolean renewContrato(Integer id, Integer newDurationMonths) {
        log.info("Renewing rental contract with id: {} for {} months", id, newDurationMonths);
        
        return contratoRentaRepository.findById(id)
                .map(contrato -> {
                    if (!"ACTIVO".equals(contrato.getEstatusContrato())) {
                        throw new IllegalArgumentException("Only active contracts can be renewed");
                    }
                    
                    LocalDateTime newEndDate = contrato.getFechaFinContrato().plusMonths(newDurationMonths);
                    contrato.setFechaFinContrato(newEndDate);
                    contrato.setDuracionMeses(contrato.getDuracionMeses() + newDurationMonths);
                    
                    contratoRentaRepository.save(contrato);
                    log.info("Rental contract renewed successfully with id: {}", id);
                    return true;
                })
                .orElse(false);
    }

    public long countContratosByEstatus(String estatus) {
        return contratoRentaRepository.countByEstatusContrato(estatus);
    }

    public long countActiveContractsExpiringBefore(LocalDateTime date) {
        return contratoRentaRepository.countActiveContractsExpiringBefore(date);
    }

    private void updateContratoFields(ContratoRenta contrato, ContratoRentaUpdateRequest request) {
        if (request.fechaInicioContrato() != null) {
            contrato.setFechaInicioContrato(request.fechaInicioContrato());
        }
        if (request.condicionesEspeciales() != null) {
            contrato.setCondicionesEspeciales(request.condicionesEspeciales());
        }
        if (request.emailNotificaciones() != null) {
            contrato.setEmailNotificaciones(request.emailNotificaciones());
        }
        if (request.estatusContrato() != null) {
            contrato.setEstatusContrato(request.estatusContrato());
        }
        if (request.depositoGarantia() != null) {
            contrato.setDepositoGarantia(request.depositoGarantia());
        }
        if (request.duracionMeses() != null) {
            contrato.setDuracionMeses(request.duracionMeses());
        }
        if (request.notificacionDiasPrevios() != null) {
            contrato.setNotificacionDiasPrevios(request.notificacionDiasPrevios());
        }
        if (request.telefonoNotificaciones() != null) {
            contrato.setTelefonoNotificaciones(request.telefonoNotificaciones());
        }
    }

    private ContratoRentaDTO convertToDTO(ContratoRenta contrato) {
        return new ContratoRentaDTO(
                contrato.getIdContrato(),
                contrato.getIdPropiedad(),
                contrato.getFechaInicioContrato(),
                contrato.getFechaFinContrato(),
                contrato.getCondicionesEspeciales(),
                contrato.getEmailNotificaciones(),
                contrato.getEstatusContrato(),
                contrato.getDepositoGarantia(),
                contrato.getDuracionMeses(),
                contrato.getNotificacionDiasPrevios(),
                contrato.getTelefonoNotificaciones()
        );
    }
}