package com.inmobiliaria.gestion.inmobiliaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "inmobiliaria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inmobiliaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inmobiliaria")
    private Long idInmobiliaria;

    @Size(max = 255, message = "Commercial name must not exceed 255 characters")
    @Column(name = "nombre_comercial")
    private String nombreComercial;

    @Size(max = 255, message = "Business name must not exceed 255 characters")
    @Column(name = "razon_social")
    private String razonSocial;

    @Size(max = 255, message = "RFC/NIT must not exceed 255 characters")
    @Column(name = "rfc_nit")
    private String rfcNit;

    @Size(max = 255, message = "Main phone must not exceed 255 characters")
    @Column(name = "telefono_principal")
    private String telefonoPrincipal;

    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email_contacto")
    private String emailContacto;

    @Size(max = 255, message = "Full address must not exceed 255 characters")
    @Column(name = "direccion_completa")
    private String direccionCompleta;

    @Size(max = 255, message = "City must not exceed 255 characters")
    @Column(name = "ciudad")
    private String ciudad;

    @Size(max = 255, message = "State must not exceed 255 characters")
    @Column(name = "estado")
    private String estado;

    @Size(max = 255, message = "Postal code must not exceed 255 characters")
    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Size(max = 255, message = "Contact person must not exceed 255 characters")
    @Column(name = "persona_contacto")
    private String personaContacto;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @Size(max = 255, message = "Status must not exceed 255 characters")
    @Column(name = "estatus")
    @Builder.Default
    private String estatus = "ACTIVE";

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDate.now();
        }
    }
}