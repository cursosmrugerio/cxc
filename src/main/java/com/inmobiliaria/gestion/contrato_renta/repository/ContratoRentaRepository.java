package com.inmobiliaria.gestion.contrato_renta.repository;

import com.inmobiliaria.gestion.contrato_renta.model.ContratoRenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoRentaRepository extends JpaRepository<ContratoRenta, Integer> {
    
    List<ContratoRenta> findByIdPropiedad(Integer idPropiedad);
    
    List<ContratoRenta> findByEstatusContrato(String estatusContrato);
    
    Optional<ContratoRenta> findByIdPropiedadAndEstatusContrato(Integer idPropiedad, String estatusContrato);
    
    List<ContratoRenta> findByEmailNotificaciones(String emailNotificaciones);
    
    List<ContratoRenta> findByTelefonoNotificaciones(String telefonoNotificaciones);
    
    @Query("SELECT c FROM ContratoRenta c WHERE c.fechaFinContrato BETWEEN :startDate AND :endDate")
    List<ContratoRenta> findByFechaFinContratoBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT c FROM ContratoRenta c WHERE c.fechaInicioContrato BETWEEN :startDate AND :endDate")
    List<ContratoRenta> findByFechaInicioContratoBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT c FROM ContratoRenta c WHERE c.fechaFinContrato <= :date AND c.estatusContrato = 'ACTIVO'")
    List<ContratoRenta> findActiveContractsExpiringBefore(@Param("date") LocalDateTime date);
    
    @Query("SELECT c FROM ContratoRenta c WHERE c.fechaFinContrato BETWEEN :startDate AND :endDate AND c.estatusContrato = 'ACTIVO'")
    List<ContratoRenta> findActiveContractsExpiringBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT c FROM ContratoRenta c WHERE c.estatusContrato = 'ACTIVO' AND " +
           "c.fechaFinContrato <= :notificationDate")
    List<ContratoRenta> findContractsNeedingNotification(@Param("notificationDate") LocalDateTime notificationDate);
    
    boolean existsByIdPropiedadAndEstatusContrato(Integer idPropiedad, String estatusContrato);
    
    long countByEstatusContrato(String estatusContrato);
    
    @Query("SELECT COUNT(c) FROM ContratoRenta c WHERE c.fechaFinContrato <= :date AND c.estatusContrato = 'ACTIVO'")
    long countActiveContractsExpiringBefore(@Param("date") LocalDateTime date);
    
    @Query("SELECT c FROM ContratoRenta c WHERE c.duracionMeses = :duracion")
    List<ContratoRenta> findByDuracionMeses(@Param("duracion") Integer duracion);
    
    @Query("SELECT c FROM ContratoRenta c WHERE c.depositoGarantia >= :minDeposito")
    List<ContratoRenta> findByDepositoGarantiaGreaterThanEqual(@Param("minDeposito") java.math.BigDecimal minDeposito);
}