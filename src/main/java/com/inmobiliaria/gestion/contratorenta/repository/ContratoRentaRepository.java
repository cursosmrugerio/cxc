package com.inmobiliaria.gestion.contratorenta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inmobiliaria.gestion.contratorenta.model.ContratoRenta;

@Repository
public interface ContratoRentaRepository extends JpaRepository<ContratoRenta, Integer> {
}
