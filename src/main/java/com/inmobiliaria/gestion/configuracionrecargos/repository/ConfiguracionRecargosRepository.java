package com.inmobiliaria.gestion.configuracionrecargos.repository;

import com.inmobiliaria.gestion.configuracionrecargos.model.ConfiguracionRecargos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionRecargosRepository extends JpaRepository<ConfiguracionRecargos, Integer> {
}
