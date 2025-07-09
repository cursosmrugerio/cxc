package com.inmobiliaria.gestion.contrato_renta.model;

import com.inmobiliaria.gestion.propiedad.model.Propiedad;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "contrato_renta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratoRenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contrato")
    private Integer idContrato;

    @Column(name = "id_propiedad")
    private Integer idPropiedad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_propiedad", insertable = false, updatable = false)
    private Propiedad propiedad;

    @Column(name = "fecha_inicio_contrato")
    private LocalDateTime fechaInicioContrato;

    @Column(name = "fecha_fin_contrato")
    private LocalDateTime fechaFinContrato;

    @Size(max = 255, message = "Special conditions must not exceed 255 characters")
    @Column(name = "condiciones_especiales")
    private String condicionesEspeciales;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email_notificaciones")
    private String emailNotificaciones;

    @Size(max = 255, message = "Contract status must not exceed 255 characters")
    @Column(name = "estatus_contrato")
    @Builder.Default
    private String estatusContrato = "ACTIVO";

    @Column(name = "deposito_garantia", precision = 38, scale = 2)
    private BigDecimal depositoGarantia;

    @Min(value = 1, message = "Duration must be at least 1 month")
    @Column(name = "duracion_meses")
    private Integer duracionMeses;

    @Min(value = 1, message = "Notification days must be at least 1")
    @Column(name = "notificacion_dias_previos")
    private Integer notificacionDiasPrevios;

    @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{7,15}$", message = "Invalid phone number format")
    @Size(max = 255, message = "Phone must not exceed 255 characters")
    @Column(name = "telefono_notificaciones")
    private String telefonoNotificaciones;

    @PrePersist
    protected void onCreate() {
        if (fechaInicioContrato != null && fechaFinContrato == null && duracionMeses != null) {
            fechaFinContrato = fechaInicioContrato.plusMonths(duracionMeses);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (fechaInicioContrato != null && duracionMeses != null) {
            fechaFinContrato = fechaInicioContrato.plusMonths(duracionMeses);
        }
    }
}