package com.inmobiliaria.gestion.configuracion_recargos.repository;

import com.inmobiliaria.gestion.configuracion_recargos.model.ConfiguracionRecargos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConfiguracionRecargosRepository extends JpaRepository<ConfiguracionRecargos, Long> {
    
    List<ConfiguracionRecargos> findByIdInmobiliaria(Long idInmobiliaria);
    
    List<ConfiguracionRecargos> findByIdInmobiliariaAndActivo(Long idInmobiliaria, Boolean activo);
    
    List<ConfiguracionRecargos> findByTipoRecargo(String tipoRecargo);
    
    Optional<ConfiguracionRecargos> findByIdInmobiliariaAndTipoRecargo(Long idInmobiliaria, String tipoRecargo);
    
    List<ConfiguracionRecargos> findByActivoTrue();
    
    @Query("SELECT c FROM ConfiguracionRecargos c WHERE c.idInmobiliaria = :idInmobiliaria AND c.activo = true AND c.tipoRecargo = :tipoRecargo")
    Optional<ConfiguracionRecargos> findActiveByInmobiliariaAndTipoRecargo(@Param("idInmobiliaria") Long idInmobiliaria, @Param("tipoRecargo") String tipoRecargo);
    
    @Query("SELECT c FROM ConfiguracionRecargos c WHERE c.idInmobiliaria = :idInmobiliaria AND c.diaAplicacion = :diaAplicacion AND c.activo = true")
    List<ConfiguracionRecargos> findActiveByInmobiliariaAndDiaAplicacion(@Param("idInmobiliaria") Long idInmobiliaria, @Param("diaAplicacion") Integer diaAplicacion);
    
    @Query("SELECT c FROM ConfiguracionRecargos c WHERE c.nombrePolitica = :nombrePolitica AND c.activo = true")
    List<ConfiguracionRecargos> findActiveByNombrePolitica(@Param("nombrePolitica") String nombrePolitica);
    
    @Query("SELECT DISTINCT c.tipoRecargo FROM ConfiguracionRecargos c WHERE c.idInmobiliaria = :idInmobiliaria")
    List<String> findDistinctTipoRecargoByInmobiliaria(@Param("idInmobiliaria") Long idInmobiliaria);
    
    boolean existsByIdInmobiliariaAndTipoRecargo(Long idInmobiliaria, String tipoRecargo);
}
