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

    private ConfiguracionRecargosDTO toDto(ConfiguracionRecargos entity) {
        return new ConfiguracionRecargosDTO(
                entity.getIdConfiguracionRecargo(),
                entity.getTipoRecargo(),
                entity.getMonto(),
                entity.getDiaAplicacion(),
                entity.getActivo(),
                entity.getIdInmobiliaria()
        );
    }

    private void updateEntityFromDto(ConfiguracionRecargos entity, ConfiguracionRecargosDTO dto) {
        entity.setTipoRecargo(dto.tipoRecargo());
        entity.setMonto(dto.monto());
        entity.setDiaAplicacion(dto.diaAplicacion());
        entity.setActivo(dto.activo());
        entity.setIdInmobiliaria(dto.idInmobiliaria());
    }
}
