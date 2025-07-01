package com.inmobiliaria.gestion.conceptopago.service;

import com.inmobiliaria.gestion.conceptopago.dto.ConceptoPagoDTO;
import com.inmobiliaria.gestion.conceptopago.model.ConceptoPago;
import com.inmobiliaria.gestion.conceptopago.repository.ConceptoPagoRepository;
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
public class ConceptoPagoService {
    private final ConceptoPagoRepository conceptoPagoRepository;
    private final InmobiliariaRepository inmobiliariaRepository;

    @Transactional
    public ConceptoPagoDTO save(ConceptoPagoDTO conceptoPagoDTO) {
        ConceptoPago conceptoPago = toEntity(conceptoPagoDTO);
        return toDTO(conceptoPagoRepository.save(conceptoPago));
    }

    @Transactional(readOnly = true)
    public List<ConceptoPagoDTO> findAll() {
        return conceptoPagoRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ConceptoPagoDTO> findById(Integer id) {
        return conceptoPagoRepository.findById(id).map(this::toDTO);
    }

    @Transactional
    public void deleteById(Integer id) {
        conceptoPagoRepository.deleteById(id);
    }

    private ConceptoPagoDTO toDTO(ConceptoPago conceptoPago) {
        return new ConceptoPagoDTO(
                conceptoPago.getId(),
                conceptoPago.getInmobiliaria().getId(),
                conceptoPago.getNombreConcepto(),
                conceptoPago.getDescripcion(),
                conceptoPago.getTipoConcepto(),
                conceptoPago.getPermiteRecargos(),
                conceptoPago.getActivo(),
                conceptoPago.getFechaCreacion()
        );
    }

    private ConceptoPago toEntity(ConceptoPagoDTO dto) {
        ConceptoPago conceptoPago = new ConceptoPago();
        conceptoPago.setId(dto.id());
        Inmobiliaria inmobiliaria = inmobiliariaRepository.findById(dto.idInmobiliaria())
                .orElseThrow(() -> new RuntimeException("Inmobiliaria not found"));
        conceptoPago.setInmobiliaria(inmobiliaria);
        conceptoPago.setNombreConcepto(dto.nombreConcepto());
        conceptoPago.setDescripcion(dto.descripcion());
        conceptoPago.setTipoConcepto(dto.tipoConcepto());
        conceptoPago.setPermiteRecargos(dto.permiteRecargos());
        conceptoPago.setActivo(dto.activo());
        conceptoPago.setFechaCreacion(dto.fechaCreacion());
        return conceptoPago;
    }
}
