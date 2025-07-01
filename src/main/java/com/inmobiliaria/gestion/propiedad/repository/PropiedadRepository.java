package com.inmobiliaria.gestion.propiedad.repository;

import com.inmobiliaria.gestion.propiedad.model.Propiedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropiedadRepository extends JpaRepository<Propiedad, Integer> {
}
