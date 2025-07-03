package com.inmobiliaria.gestion.contratorenta.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inmobiliaria.gestion.contratorenta.dto.ContratoRentaDTO;
import com.inmobiliaria.gestion.contratorenta.model.ContratoRenta;
import com.inmobiliaria.gestion.contratorenta.repository.ContratoRentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContratoRentaService {
    private final ContratoRentaRepository contratoRentaRepository;

    @Transactional
    public ContratoRentaDTO save(ContratoRentaDTO contratoRentaDTO) {
        ContratoRenta contratoRenta = new ContratoRenta();
        updateEntity(contratoRenta, contratoRentaDTO);
        return toDTO(contratoRentaRepository.save(contratoRenta));
    }

    @Transactional(readOnly = true)
    public List<ContratoRentaDTO> findAll() {
        return contratoRentaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ContratoRentaDTO> findById(Integer id) {
        return contratoRentaRepository.findById(id).map(this::toDTO);
    }

    @Transactional
    public Optional<ContratoRentaDTO> update(Integer id, ContratoRentaDTO contratoRentaDTO) {
        return contratoRentaRepository.findById(id).map(existingContrato -> {
            updateEntity(existingContrato, contratoRentaDTO);
            return toDTO(contratoRentaRepository.save(existingContrato));
        });
    }

    @Transactional
    public boolean deleteById(Integer id) {
        if (contratoRentaRepository.existsById(id)) {
            contratoRentaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void updateEntity(ContratoRenta contratoRenta, ContratoRentaDTO dto) {
        contratoRenta.setIdPropiedad(dto.idPropiedad());
        contratoRenta.setFechaInicioContrato(dto.fechaInicioContrato());
        contratoRenta.setFechaFinContrato(dto.fechaFinContrato());
        contratoRenta.setDuracionMeses(dto.duracionMeses());
        contratoRenta.setDepositoGarantia(dto.depositoGarantia());
        contratoRenta.setCondicionesEspeciales(dto.condicionesEspeciales());
        contratoRenta.setNotificacionDiasPrevios(dto.notificacionDiasPrevios());
        contratoRenta.setEmailNotificaciones(dto.emailNotificaciones());
        contratoRenta.setTelefonoNotificaciones(dto.telefonoNotificaciones());
        contratoRenta.setEstatusContrato(dto.estatusContrato());
    }

    private ContratoRentaDTO toDTO(ContratoRenta contratoRenta) {
        return new ContratoRentaDTO(
            contratoRenta.getIdContrato(),
            contratoRenta.getIdPropiedad(),
            contratoRenta.getFechaInicioContrato(),
            contratoRenta.getFechaFinContrato(),
            contratoRenta.getDuracionMeses(),
            contratoRenta.getDepositoGarantia(),
            contratoRenta.getCondicionesEspeciales(),
            contratoRenta.getNotificacionDiasPrevios(),
            contratoRenta.getEmailNotificaciones(),
            contratoRenta.getTelefonoNotificaciones(),
            contratoRenta.getEstatusContrato()
        );
    }
}
