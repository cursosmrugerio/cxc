package com.inmobiliaria.gestion.conceptos.repository;

import com.inmobiliaria.gestion.conceptos.model.ConceptosPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConceptosPagoRepository extends JpaRepository<ConceptosPago, Integer> {

    List<ConceptosPago> findByIdInmobiliaria(Long idInmobiliaria);

    Page<ConceptosPago> findByIdInmobiliaria(Long idInmobiliaria, Pageable pageable);

    List<ConceptosPago> findByIdInmobiliariaAndActivo(Long idInmobiliaria, Boolean activo);

    Page<ConceptosPago> findByIdInmobiliariaAndActivo(Long idInmobiliaria, Boolean activo, Pageable pageable);

    List<ConceptosPago> findByTipoConcepto(String tipoConcepto);

    Page<ConceptosPago> findByTipoConcepto(String tipoConcepto, Pageable pageable);

    List<ConceptosPago> findByIdInmobiliariaAndTipoConcepto(Long idInmobiliaria, String tipoConcepto);

    Page<ConceptosPago> findByIdInmobiliariaAndTipoConcepto(Long idInmobiliaria, String tipoConcepto, Pageable pageable);

    List<ConceptosPago> findByNombreConceptoContainingIgnoreCase(String nombreConcepto);

    Page<ConceptosPago> findByNombreConceptoContainingIgnoreCase(String nombreConcepto, Pageable pageable);

    List<ConceptosPago> findByIdInmobiliariaAndNombreConceptoContainingIgnoreCase(Long idInmobiliaria, String nombreConcepto);

    Page<ConceptosPago> findByIdInmobiliariaAndNombreConceptoContainingIgnoreCase(Long idInmobiliaria, String nombreConcepto, Pageable pageable);

    List<ConceptosPago> findByPermiteRecargos(Boolean permiteRecargos);

    Page<ConceptosPago> findByPermiteRecargos(Boolean permiteRecargos, Pageable pageable);

    List<ConceptosPago> findByIdInmobiliariaAndPermiteRecargos(Long idInmobiliaria, Boolean permiteRecargos);

    Page<ConceptosPago> findByIdInmobiliariaAndPermiteRecargos(Long idInmobiliaria, Boolean permiteRecargos, Pageable pageable);

    @Query("SELECT c FROM ConceptosPago c WHERE " +
           "(:idInmobiliaria IS NULL OR c.idInmobiliaria = :idInmobiliaria) AND " +
           "(:nombreConcepto IS NULL OR LOWER(c.nombreConcepto) LIKE LOWER(CONCAT('%', :nombreConcepto, '%'))) AND " +
           "(:tipoConcepto IS NULL OR c.tipoConcepto = :tipoConcepto) AND " +
           "(:activo IS NULL OR c.activo = :activo) AND " +
           "(:permiteRecargos IS NULL OR c.permiteRecargos = :permiteRecargos)")
    Page<ConceptosPago> findByFilters(
            @Param("idInmobiliaria") Long idInmobiliaria,
            @Param("nombreConcepto") String nombreConcepto,
            @Param("tipoConcepto") String tipoConcepto,
            @Param("activo") Boolean activo,
            @Param("permiteRecargos") Boolean permiteRecargos,
            Pageable pageable);

    @Query("SELECT COUNT(c) FROM ConceptosPago c WHERE c.idInmobiliaria = :idInmobiliaria AND c.activo = true")
    long countActiveByInmobiliaria(@Param("idInmobiliaria") Long idInmobiliaria);

    @Query("SELECT DISTINCT c.tipoConcepto FROM ConceptosPago c WHERE c.tipoConcepto IS NOT NULL ORDER BY c.tipoConcepto")
    List<String> findDistinctTiposConcepto();

    @Query("SELECT DISTINCT c.tipoConcepto FROM ConceptosPago c WHERE c.idInmobiliaria = :idInmobiliaria AND c.tipoConcepto IS NOT NULL ORDER BY c.tipoConcepto")
    List<String> findDistinctTiposConceptoByInmobiliaria(@Param("idInmobiliaria") Long idInmobiliaria);

    boolean existsByIdInmobiliariaAndNombreConcepto(Long idInmobiliaria, String nombreConcepto);

    boolean existsByIdInmobiliariaAndNombreConceptoAndIdConceptoNot(Long idInmobiliaria, String nombreConcepto, Integer idConcepto);
}