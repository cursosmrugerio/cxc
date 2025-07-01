package com.inmobiliaria.gestion.conceptopago.repository;

import com.inmobiliaria.gestion.conceptopago.model.ConceptoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConceptoPagoRepository extends JpaRepository<ConceptoPago, Integer> {
}
