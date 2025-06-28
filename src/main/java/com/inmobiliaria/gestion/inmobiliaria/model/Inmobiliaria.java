
package com.inmobiliaria.gestion.inmobiliaria.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "inmobiliaria")
public class Inmobiliaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inmobiliaria")
    private Long id;

    @Column(name = "nombre_comercial")
    private String nombreComercial;

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(name = "rfc_nit")
    private String rfcNit;

    @Column(name = "telefono_principal")
    private String telefonoPrincipal;

    @Column(name = "email_contacto")
    private String emailContacto;

    @Column(name = "direccion_completa")
    private String direccionCompleta;

    private String ciudad;

    private String estado;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Column(name = "persona_contacto")
    private String personaContacto;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    private String estatus;
}
