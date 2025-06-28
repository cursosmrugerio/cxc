
package com.inmobiliaria.gestion.inmobiliaria.dto;

import java.time.LocalDate;

public record InmobiliariaDTO(
    Long id,
    String nombreComercial,
    String razonSocial,
    String rfcNit,
    String telefonoPrincipal,
    String emailContacto,
    String direccionCompleta,
    String ciudad,
    String estado,
    String codigoPostal,
    String personaContacto,
    LocalDate fechaRegistro,
    String estatus
) {}
