package com.inmobiliaria.gestion.inmobiliaria.repository;

import com.inmobiliaria.gestion.inmobiliaria.model.Inmobiliaria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InmobiliariaRepository extends JpaRepository<Inmobiliaria, Long> {

    Optional<Inmobiliaria> findByRfcNit(String rfcNit);

    List<Inmobiliaria> findByEstatus(String estatus);

    Page<Inmobiliaria> findByEstatus(String estatus, Pageable pageable);

    List<Inmobiliaria> findByNombreComercialContainingIgnoreCase(String nombreComercial);

    Page<Inmobiliaria> findByNombreComercialContainingIgnoreCase(String nombreComercial, Pageable pageable);

    List<Inmobiliaria> findByCiudadIgnoreCase(String ciudad);

    Page<Inmobiliaria> findByCiudadIgnoreCase(String ciudad, Pageable pageable);

    List<Inmobiliaria> findByEstadoIgnoreCase(String estado);

    Page<Inmobiliaria> findByEstadoIgnoreCase(String estado, Pageable pageable);

    @Query("SELECT i FROM Inmobiliaria i WHERE " +
           "(:nombreComercial IS NULL OR LOWER(i.nombreComercial) LIKE LOWER(CONCAT('%', :nombreComercial, '%'))) AND " +
           "(:ciudad IS NULL OR LOWER(i.ciudad) = LOWER(:ciudad)) AND " +
           "(:estado IS NULL OR LOWER(i.estado) = LOWER(:estado)) AND " +
           "(:estatus IS NULL OR i.estatus = :estatus)")
    Page<Inmobiliaria> findByFilters(
            @Param("nombreComercial") String nombreComercial,
            @Param("ciudad") String ciudad,
            @Param("estado") String estado,
            @Param("estatus") String estatus,
            Pageable pageable);

    @Query("SELECT COUNT(i) FROM Inmobiliaria i WHERE i.estatus = :estatus")
    long countByEstatus(@Param("estatus") String estatus);

    boolean existsByRfcNit(String rfcNit);

    boolean existsByRfcNitAndIdInmobiliariaNot(String rfcNit, Long idInmobiliaria);

    @Query("SELECT DISTINCT i.ciudad FROM Inmobiliaria i WHERE i.ciudad IS NOT NULL ORDER BY i.ciudad")
    List<String> findDistinctCiudades();

    @Query("SELECT DISTINCT i.estado FROM Inmobiliaria i WHERE i.estado IS NOT NULL ORDER BY i.estado")
    List<String> findDistinctEstados();
}