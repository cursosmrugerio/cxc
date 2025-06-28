
package com.inmobiliaria.gestion.inmobiliaria.service;

import com.inmobiliaria.gestion.inmobiliaria.dto.InmobiliariaDTO;
import com.inmobiliaria.gestion.inmobiliaria.model.Inmobiliaria;
import com.inmobiliaria.gestion.inmobiliaria.repository.InmobiliariaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InmobiliariaService {

    private final InmobiliariaRepository inmobiliariaRepository;

    @Transactional
    public InmobiliariaDTO save(InmobiliariaDTO inmobiliariaDTO) {
        Inmobiliaria inmobiliaria = toEntity(inmobiliariaDTO);
        inmobiliaria = inmobiliariaRepository.save(inmobiliaria);
        return toDTO(inmobiliaria);
    }

    @Transactional(readOnly = true)
    public List<InmobiliariaDTO> findAll() {
        return inmobiliariaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<InmobiliariaDTO> findById(Long id) {
        return inmobiliariaRepository.findById(id).map(this::toDTO);
    }

    @Transactional
    public void deleteById(Long id) {
        inmobiliariaRepository.deleteById(id);
    }

    private InmobiliariaDTO toDTO(Inmobiliaria inmobiliaria) {
        return new InmobiliariaDTO(
                inmobiliaria.getId(),
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

    private Inmobiliaria toEntity(InmobiliariaDTO dto) {
        Inmobiliaria inmobiliaria = new Inmobiliaria();
        inmobiliaria.setId(dto.id());
        inmobiliaria.setNombreComercial(dto.nombreComercial());
        inmobiliaria.setRazonSocial(dto.razonSocial());
        inmobiliaria.setRfcNit(dto.rfcNit());
        inmobiliaria.setTelefonoPrincipal(dto.telefonoPrincipal());
        inmobiliaria.setEmailContacto(dto.emailContacto());
        inmobiliaria.setDireccionCompleta(dto.direccionCompleta());
        inmobiliaria.setCiudad(dto.ciudad());
        inmobiliaria.setEstado(dto.estado());
        inmobiliaria.setCodigoPostal(dto.codigoPostal());
        inmobiliaria.setPersonaContacto(dto.personaContacto());
        inmobiliaria.setFechaRegistro(dto.fechaRegistro());
        inmobiliaria.setEstatus(dto.estatus());
        return inmobiliaria;
    }
}
