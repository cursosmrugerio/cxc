package com.inmobiliaria.gestion.propiedad.repository;

import com.inmobiliaria.gestion.propiedad.model.Propiedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropiedadRepository extends JpaRepository<Propiedad, Integer> {
    
    List<Propiedad> findByIdInmobiliaria(Long idInmobiliaria);
    
    List<Propiedad> findByEstatusPropiedad(String estatusPropiedad);
    
    List<Propiedad> findByTipoPropiedad(String tipoPropiedad);
    
    List<Propiedad> findByIdInmobiliariaAndEstatusPropiedad(Long idInmobiliaria, String estatusPropiedad);
    
    List<Propiedad> findByIdInmobiliariaAndTipoPropiedad(Long idInmobiliaria, String tipoPropiedad);
    
    @Query("SELECT p FROM Propiedad p WHERE p.direccionCompleta LIKE %:direccion%")
    List<Propiedad> findByDireccionContaining(@Param("direccion") String direccion);
    
    @Query("SELECT p FROM Propiedad p WHERE p.idInmobiliaria = :idInmobiliaria AND p.direccionCompleta LIKE %:direccion%")
    List<Propiedad> findByIdInmobiliariaAndDireccionContaining(@Param("idInmobiliaria") Long idInmobiliaria, @Param("direccion") String direccion);
    
    Optional<Propiedad> findByIdPropiedadAndIdInmobiliaria(Integer idPropiedad, Long idInmobiliaria);
    
    boolean existsByIdPropiedadAndIdInmobiliaria(Integer idPropiedad, Long idInmobiliaria);
    
    long countByIdInmobiliaria(Long idInmobiliaria);
    
    long countByIdInmobiliariaAndEstatusPropiedad(Long idInmobiliaria, String estatusPropiedad);
}