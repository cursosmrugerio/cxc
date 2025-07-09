package com.inmobiliaria.gestion.configuracion_recargos.service;

import com.inmobiliaria.gestion.configuracion_recargos.dto.ConfiguracionRecargosDTO;
import com.inmobiliaria.gestion.configuracion_recargos.model.ConfiguracionRecargos;
import com.inmobiliaria.gestion.configuracion_recargos.repository.ConfiguracionRecargosRepository;
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

    @Transactional
    public ConfiguracionRecargosDTO save(ConfiguracionRecargosDTO dto) {
        ConfiguracionRecargos entity = new ConfiguracionRecargos();
        updateEntityFromDto(entity, dto);
        ConfiguracionRecargos savedEntity = configuracionRecargosRepository.save(entity);
        return toDto(savedEntity);
    }

    @Transactional(readOnly = true)
    public List<ConfiguracionRecargosDTO> findAll() {
        return configuracionRecargosRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ConfiguracionRecargosDTO> findById(Long id) {
        return configuracionRecargosRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public Optional<ConfiguracionRecargosDTO> update(Long id, ConfiguracionRecargosDTO dto) {
        return configuracionRecargosRepository.findById(id)
                .map(entity -> {
                    updateEntityFromDto(entity, dto);
                    return toDto(configuracionRecargosRepository.save(entity));
                });
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (configuracionRecargosRepository.existsById(id)) {
            configuracionRecargosRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<ConfiguracionRecargosDTO> findByInmobiliaria(Long idInmobiliaria) {
        return configuracionRecargosRepository.findByIdInmobiliaria(idInmobiliaria).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConfiguracionRecargosDTO> findActiveByInmobiliaria(Long idInmobiliaria) {
        return configuracionRecargosRepository.findByIdInmobiliaria(idInmobiliaria).stream()
                .filter(ConfiguracionRecargos::getActivo)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConfiguracionRecargosDTO> findByTipoRecargo(String tipoRecargo) {
        return configuracionRecargosRepository.findByTipoRecargo(tipoRecargo).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ConfiguracionRecargosDTO> findByInmobiliariaAndTipoRecargo(Long idInmobiliaria, String tipoRecargo) {
        return configuracionRecargosRepository.findByIdInmobiliariaAndTipoRecargo(idInmobiliaria, tipoRecargo)
                .map(this::toDto);
    }

    @Transactional
    public Optional<ConfiguracionRecargosDTO> toggleActive(Long id) {
        return configuracionRecargosRepository.findById(id)
                .map(entity -> {
                    entity.setActivo(!entity.getActivo());
                    return toDto(configuracionRecargosRepository.save(entity));
                });
    }

    private ConfiguracionRecargosDTO toDto(ConfiguracionRecargos entity) {
        return new ConfiguracionRecargosDTO(
                entity.getIdConfiguracionRecargo(),
                entity.getActiva(),
                entity.getAplicaAConceptos(),
                entity.getDiasCorteServicios(),
                entity.getDiasGracia(),
                entity.getMontoRecargoFijo(),
                entity.getNombrePolitica(),
                entity.getPorcentajeRecargoDiario(),
                entity.getRecargoMaximo(),
                entity.getTipoRecargo(),
                entity.getIdInmobiliaria(),
                entity.getTasaRecargoDiaria(),
                entity.getTasaRecargoFija(),
                entity.getActivo(),
                entity.getDiaAplicacion(),
                entity.getMonto()
        );
    }

    private void updateEntityFromDto(ConfiguracionRecargos entity, ConfiguracionRecargosDTO dto) {
        entity.setActiva(dto.activa());
        entity.setAplicaAConceptos(dto.aplicaAConceptos());
        entity.setDiasCorteServicios(dto.diasCorteServicios());
        entity.setDiasGracia(dto.diasGracia());
        entity.setMontoRecargoFijo(dto.montoRecargoFijo());
        entity.setNombrePolitica(dto.nombrePolitica());
        entity.setPorcentajeRecargoDiario(dto.porcentajeRecargoDiario());
        entity.setRecargoMaximo(dto.recargoMaximo());
        entity.setTipoRecargo(dto.tipoRecargo());
        entity.setIdInmobiliaria(dto.idInmobiliaria());
        entity.setTasaRecargoDiaria(dto.tasaRecargoDiaria());
        entity.setTasaRecargoFija(dto.tasaRecargoFija());
        entity.setActivo(dto.activo());
        entity.setDiaAplicacion(dto.diaAplicacion());
        entity.setMonto(dto.monto());
    }
}
