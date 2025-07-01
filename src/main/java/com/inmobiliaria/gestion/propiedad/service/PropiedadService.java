package com.inmobiliaria.gestion.propiedad.service;

import com.inmobiliaria.gestion.inmobiliaria.model.Inmobiliaria;
import com.inmobiliaria.gestion.inmobiliaria.repository.InmobiliariaRepository;
import com.inmobiliaria.gestion.propiedad.dto.PropiedadDTO;
import com.inmobiliaria.gestion.propiedad.model.Propiedad;
import com.inmobiliaria.gestion.propiedad.repository.PropiedadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropiedadService {
    private final PropiedadRepository propiedadRepository;
    private final InmobiliariaRepository inmobiliariaRepository;

    @Transactional
    public PropiedadDTO save(PropiedadDTO propiedadDTO) {
        Propiedad propiedad = toEntity(propiedadDTO);
        return toDTO(propiedadRepository.save(propiedad));
    }

    @Transactional(readOnly = true)
    public List<PropiedadDTO> findAll() {
        return propiedadRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<PropiedadDTO> findById(Integer id) {
        return propiedadRepository.findById(id).map(this::toDTO);
    }

    @Transactional
    public void deleteById(Integer id) {
        propiedadRepository.deleteById(id);
    }

    private PropiedadDTO toDTO(Propiedad propiedad) {
        return new PropiedadDTO(
                propiedad.getId(),
                propiedad.getInmobiliaria().getId(),
                propiedad.getDireccionCompleta(),
                propiedad.getTipoPropiedad(),
                propiedad.getSuperficieTotal(),
                propiedad.getSuperficieConstruida(),
                propiedad.getNumeroHabitaciones(),
                propiedad.getNumeroBanos(),
                propiedad.getCaracteristicasEspeciales(),
                propiedad.getFechaRegistro(),
                propiedad.getEstatusPropiedad()
        );
    }

    private Propiedad toEntity(PropiedadDTO dto) {
        Propiedad propiedad = new Propiedad();
        propiedad.setId(dto.id());
        Inmobiliaria inmobiliaria = inmobiliariaRepository.findById(dto.idInmobiliaria())
                .orElseThrow(() -> new RuntimeException("Inmobiliaria not found"));
        propiedad.setInmobiliaria(inmobiliaria);
        propiedad.setDireccionCompleta(dto.direccionCompleta());
        propiedad.setTipoPropiedad(dto.tipoPropiedad());
        propiedad.setSuperficieTotal(dto.superficieTotal());
        propiedad.setSuperficieConstruida(dto.superficieConstruida());
        propiedad.setNumeroHabitaciones(dto.numeroHabitaciones());
        propiedad.setNumeroBanos(dto.numeroBanos());
        propiedad.setCaracteristicasEspeciales(dto.caracteristicasEspeciales());
        propiedad.setFechaRegistro(dto.fechaRegistro());
        propiedad.setEstatusPropiedad(dto.estatusPropiedad());
        return propiedad;
    }
}
