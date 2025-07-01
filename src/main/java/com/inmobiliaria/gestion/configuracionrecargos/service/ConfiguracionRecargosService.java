package com.inmobiliaria.gestion.configuracionrecargos.service;

import com.inmobiliaria.gestion.configuracionrecargos.dto.ConfiguracionRecargosDTO;
import com.inmobiliaria.gestion.configuracionrecargos.model.ConfiguracionRecargos;
import com.inmobiliaria.gestion.configuracionrecargos.repository.ConfiguracionRecargosRepository;
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
public class ConfiguracionRecargosService {
    private final ConfiguracionRecargosRepository configuracionRecargosRepository;
    private final InmobiliariaRepository inmobiliariaRepository;

    @Transactional
    public ConfiguracionRecargosDTO save(ConfiguracionRecargosDTO configuracionRecargosDTO) {
        ConfiguracionRecargos configuracionRecargos = toEntity(configuracionRecargosDTO);
        return toDTO(configuracionRecargosRepository.save(configuracionRecargos));
    }

    @Transactional(readOnly = true)
    public List<ConfiguracionRecargosDTO> findAll() {
        return configuracionRecargosRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ConfiguracionRecargosDTO> findById(Integer id) {
        return configuracionRecargosRepository.findById(id).map(this::toDTO);
    }

    @Transactional
    public void deleteById(Integer id) {
        configuracionRecargosRepository.deleteById(id);
    }

    private ConfiguracionRecargosDTO toDTO(ConfiguracionRecargos configuracionRecargos) {
        return new ConfiguracionRecargosDTO(
                configuracionRecargos.getId(),
                configuracionRecargos.getInmobiliaria().getId(),
                configuracionRecargos.getNombrePolitica(),
                configuracionRecargos.getDiasGracia(),
                configuracionRecargos.getTipoRecargo(),
                configuracionRecargos.getPorcentajeRecargoDiario(),
                configuracionRecargos.getMontoRecargoFijo(),
                configuracionRecargos.getRecargoMaximo(),
                configuracionRecargos.getDiasCorteServicios(),
                configuracionRecargos.getAplicaAConceptos(),
                configuracionRecargos.getActiva()
        );
    }

    private ConfiguracionRecargos toEntity(ConfiguracionRecargosDTO dto) {
        ConfiguracionRecargos configuracionRecargos = new ConfiguracionRecargos();
        configuracionRecargos.setId(dto.id());
        Inmobiliaria inmobiliaria = inmobiliariaRepository.findById(dto.idInmobiliaria())
                .orElseThrow(() -> new RuntimeException("Inmobiliaria not found"));
        configuracionRecargos.setInmobiliaria(inmobiliaria);
        configuracionRecargos.setNombrePolitica(dto.nombrePolitica());
        configuracionRecargos.setDiasGracia(dto.diasGracia());
        configuracionRecargos.setTipoRecargo(dto.tipoRecargo());
        configuracionRecargos.setPorcentajeRecargoDiario(dto.porcentajeRecargoDiario());
        configuracionRecargos.setMontoRecargoFijo(dto.montoRecargoFijo());
        configuracionRecargos.setRecargoMaximo(dto.recargoMaximo());
        configuracionRecargos.setDiasCorteServicios(dto.diasCorteServicios());
        configuracionRecargos.setAplicaAConceptos(dto.aplicaAConceptos());
        configuracionRecargos.setActiva(dto.activa());
        return configuracionRecargos;
    }
}
