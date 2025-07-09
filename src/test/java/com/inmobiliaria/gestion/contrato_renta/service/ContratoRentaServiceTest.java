package com.inmobiliaria.gestion.contrato_renta.service;

import com.inmobiliaria.gestion.contrato_renta.dto.ContratoRentaCreateRequest;
import com.inmobiliaria.gestion.contrato_renta.dto.ContratoRentaDTO;
import com.inmobiliaria.gestion.contrato_renta.dto.ContratoRentaUpdateRequest;
import com.inmobiliaria.gestion.contrato_renta.model.ContratoRenta;
import com.inmobiliaria.gestion.contrato_renta.repository.ContratoRentaRepository;
import com.inmobiliaria.gestion.propiedad.repository.PropiedadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContratoRentaServiceTest {

    @Mock
    private ContratoRentaRepository contratoRentaRepository;

    @Mock
    private PropiedadRepository propiedadRepository;

    @InjectMocks
    private ContratoRentaService contratoRentaService;

    private ContratoRenta testContrato;
    private ContratoRentaCreateRequest createRequest;
    private ContratoRentaUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusMonths(12);

        testContrato = ContratoRenta.builder()
                .idContrato(1)
                .idPropiedad(1)
                .fechaInicioContrato(now)
                .fechaFinContrato(futureDate)
                .condicionesEspeciales("No pets allowed")
                .emailNotificaciones("tenant@example.com")
                .estatusContrato("ACTIVO")
                .depositoGarantia(BigDecimal.valueOf(5000.00))
                .duracionMeses(12)
                .notificacionDiasPrevios(30)
                .telefonoNotificaciones("+52 55 1234 5678")
                .build();

        createRequest = new ContratoRentaCreateRequest(
                1,
                now,
                "No pets allowed",
                "tenant@example.com",
                BigDecimal.valueOf(5000.00),
                12,
                30,
                "+52 55 1234 5678"
        );

        updateRequest = new ContratoRentaUpdateRequest(
                now.plusDays(1),
                "No pets allowed, No smoking",
                "newtenant@example.com",
                "ACTIVO",
                BigDecimal.valueOf(6000.00),
                12,
                15,
                "+52 55 9876 5432"
        );
    }

    @Test
    void getAllContratos_ShouldReturnAllContratos() {
        // Given
        List<ContratoRenta> contratos = Arrays.asList(testContrato);
        when(contratoRentaRepository.findAll()).thenReturn(contratos);

        // When
        List<ContratoRentaDTO> result = contratoRentaService.getAllContratos();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).idContrato()).isEqualTo(1);
        assertThat(result.get(0).estatusContrato()).isEqualTo("ACTIVO");
        verify(contratoRentaRepository).findAll();
    }

    @Test
    void getContratoById_WhenExists_ShouldReturnContrato() {
        // Given
        when(contratoRentaRepository.findById(1)).thenReturn(Optional.of(testContrato));

        // When
        Optional<ContratoRentaDTO> result = contratoRentaService.getContratoById(1);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().idContrato()).isEqualTo(1);
        assertThat(result.get().estatusContrato()).isEqualTo("ACTIVO");
        verify(contratoRentaRepository).findById(1);
    }

    @Test
    void getContratoById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(contratoRentaRepository.findById(1)).thenReturn(Optional.empty());

        // When
        Optional<ContratoRentaDTO> result = contratoRentaService.getContratoById(1);

        // Then
        assertThat(result).isEmpty();
        verify(contratoRentaRepository).findById(1);
    }

    @Test
    void getContratosByPropiedad_ShouldReturnContratosByPropiedad() {
        // Given
        List<ContratoRenta> contratos = Arrays.asList(testContrato);
        when(contratoRentaRepository.findByIdPropiedad(1)).thenReturn(contratos);

        // When
        List<ContratoRentaDTO> result = contratoRentaService.getContratosByPropiedad(1);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).idPropiedad()).isEqualTo(1);
        verify(contratoRentaRepository).findByIdPropiedad(1);
    }

    @Test
    void getContratosByEstatus_ShouldReturnContratosByEstatus() {
        // Given
        List<ContratoRenta> contratos = Arrays.asList(testContrato);
        when(contratoRentaRepository.findByEstatusContrato("ACTIVO")).thenReturn(contratos);

        // When
        List<ContratoRentaDTO> result = contratoRentaService.getContratosByEstatus("ACTIVO");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).estatusContrato()).isEqualTo("ACTIVO");
        verify(contratoRentaRepository).findByEstatusContrato("ACTIVO");
    }

    @Test
    void getContratosExpiringBetween_ShouldReturnContratosInDateRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().plusMonths(11);
        LocalDateTime endDate = LocalDateTime.now().plusMonths(13);
        List<ContratoRenta> contratos = Arrays.asList(testContrato);
        when(contratoRentaRepository.findByFechaFinContratoBetween(startDate, endDate)).thenReturn(contratos);

        // When
        List<ContratoRentaDTO> result = contratoRentaService.getContratosExpiringBetween(startDate, endDate);

        // Then
        assertThat(result).hasSize(1);
        verify(contratoRentaRepository).findByFechaFinContratoBetween(startDate, endDate);
    }

    @Test
    void createContrato_WithValidData_ShouldCreateAndReturnContrato() {
        // Given
        when(propiedadRepository.existsById(1)).thenReturn(true);
        when(contratoRentaRepository.existsByIdPropiedadAndEstatusContrato(1, "ACTIVO")).thenReturn(false);
        when(contratoRentaRepository.save(any(ContratoRenta.class))).thenReturn(testContrato);

        // When
        ContratoRentaDTO result = contratoRentaService.createContrato(createRequest);

        // Then
        assertThat(result.idContrato()).isEqualTo(1);
        assertThat(result.estatusContrato()).isEqualTo("ACTIVO");
        assertThat(result.idPropiedad()).isEqualTo(1);
        verify(propiedadRepository).existsById(1);
        verify(contratoRentaRepository).existsByIdPropiedadAndEstatusContrato(1, "ACTIVO");
        verify(contratoRentaRepository).save(any(ContratoRenta.class));
    }

    @Test
    void createContrato_WithNonExistentProperty_ShouldThrowException() {
        // Given
        when(propiedadRepository.existsById(1)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> contratoRentaService.createContrato(createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Property with id 1 does not exist");

        verify(propiedadRepository).existsById(1);
        verify(contratoRentaRepository, never()).save(any(ContratoRenta.class));
    }

    @Test
    void createContrato_WithExistingActiveContract_ShouldThrowException() {
        // Given
        when(propiedadRepository.existsById(1)).thenReturn(true);
        when(contratoRentaRepository.existsByIdPropiedadAndEstatusContrato(1, "ACTIVO")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> contratoRentaService.createContrato(createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Property already has an active rental contract");

        verify(propiedadRepository).existsById(1);
        verify(contratoRentaRepository).existsByIdPropiedadAndEstatusContrato(1, "ACTIVO");
        verify(contratoRentaRepository, never()).save(any(ContratoRenta.class));
    }

    @Test
    void updateContrato_WhenExists_ShouldUpdateAndReturnContrato() {
        // Given
        ContratoRenta updatedContrato = ContratoRenta.builder()
                .idContrato(1)
                .idPropiedad(1)
                .fechaInicioContrato(testContrato.getFechaInicioContrato().plusDays(1))
                .fechaFinContrato(testContrato.getFechaFinContrato())
                .condicionesEspeciales("No pets allowed, No smoking")
                .emailNotificaciones("newtenant@example.com")
                .estatusContrato("ACTIVO")
                .depositoGarantia(BigDecimal.valueOf(6000.00))
                .duracionMeses(12)
                .notificacionDiasPrevios(15)
                .telefonoNotificaciones("+52 55 9876 5432")
                .build();

        when(contratoRentaRepository.findById(1)).thenReturn(Optional.of(testContrato));
        when(contratoRentaRepository.save(any(ContratoRenta.class))).thenReturn(updatedContrato);

        // When
        Optional<ContratoRentaDTO> result = contratoRentaService.updateContrato(1, updateRequest);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().emailNotificaciones()).isEqualTo("newtenant@example.com");
        assertThat(result.get().depositoGarantia()).isEqualTo(BigDecimal.valueOf(6000.00));
        verify(contratoRentaRepository).findById(1);
        verify(contratoRentaRepository).save(any(ContratoRenta.class));
    }

    @Test
    void updateContrato_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(contratoRentaRepository.findById(1)).thenReturn(Optional.empty());

        // When
        Optional<ContratoRentaDTO> result = contratoRentaService.updateContrato(1, updateRequest);

        // Then
        assertThat(result).isEmpty();
        verify(contratoRentaRepository).findById(1);
        verify(contratoRentaRepository, never()).save(any(ContratoRenta.class));
    }

    @Test
    void deleteContrato_WhenExists_ShouldReturnTrue() {
        // Given
        when(contratoRentaRepository.existsById(1)).thenReturn(true);

        // When
        boolean result = contratoRentaService.deleteContrato(1);

        // Then
        assertThat(result).isTrue();
        verify(contratoRentaRepository).existsById(1);
        verify(contratoRentaRepository).deleteById(1);
    }

    @Test
    void deleteContrato_WhenNotExists_ShouldReturnFalse() {
        // Given
        when(contratoRentaRepository.existsById(1)).thenReturn(false);

        // When
        boolean result = contratoRentaService.deleteContrato(1);

        // Then
        assertThat(result).isFalse();
        verify(contratoRentaRepository).existsById(1);
        verify(contratoRentaRepository, never()).deleteById(anyInt());
    }

    @Test
    void terminateContrato_WhenExists_ShouldTerminateContract() {
        // Given
        when(contratoRentaRepository.findById(1)).thenReturn(Optional.of(testContrato));
        when(contratoRentaRepository.save(any(ContratoRenta.class))).thenReturn(testContrato);

        // When
        boolean result = contratoRentaService.terminateContrato(1);

        // Then
        assertThat(result).isTrue();
        verify(contratoRentaRepository).findById(1);
        verify(contratoRentaRepository).save(any(ContratoRenta.class));
    }

    @Test
    void terminateContrato_WhenNotExists_ShouldReturnFalse() {
        // Given
        when(contratoRentaRepository.findById(1)).thenReturn(Optional.empty());

        // When
        boolean result = contratoRentaService.terminateContrato(1);

        // Then
        assertThat(result).isFalse();
        verify(contratoRentaRepository).findById(1);
        verify(contratoRentaRepository, never()).save(any(ContratoRenta.class));
    }

    @Test
    void renewContrato_WithActiveContract_ShouldRenewSuccessfully() {
        // Given
        when(contratoRentaRepository.findById(1)).thenReturn(Optional.of(testContrato));
        when(contratoRentaRepository.save(any(ContratoRenta.class))).thenReturn(testContrato);

        // When
        boolean result = contratoRentaService.renewContrato(1, 6);

        // Then
        assertThat(result).isTrue();
        verify(contratoRentaRepository).findById(1);
        verify(contratoRentaRepository).save(any(ContratoRenta.class));
    }

    @Test
    void renewContrato_WithInactiveContract_ShouldThrowException() {
        // Given
        testContrato.setEstatusContrato("TERMINADO");
        when(contratoRentaRepository.findById(1)).thenReturn(Optional.of(testContrato));

        // When & Then
        assertThatThrownBy(() -> contratoRentaService.renewContrato(1, 6))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only active contracts can be renewed");

        verify(contratoRentaRepository).findById(1);
        verify(contratoRentaRepository, never()).save(any(ContratoRenta.class));
    }

    @Test
    void renewContrato_WhenNotExists_ShouldReturnFalse() {
        // Given
        when(contratoRentaRepository.findById(1)).thenReturn(Optional.empty());

        // When
        boolean result = contratoRentaService.renewContrato(1, 6);

        // Then
        assertThat(result).isFalse();
        verify(contratoRentaRepository).findById(1);
        verify(contratoRentaRepository, never()).save(any(ContratoRenta.class));
    }

    @Test
    void countContratosByEstatus_ShouldReturnCorrectCount() {
        // Given
        when(contratoRentaRepository.countByEstatusContrato("ACTIVO")).thenReturn(5L);

        // When
        long result = contratoRentaService.countContratosByEstatus("ACTIVO");

        // Then
        assertThat(result).isEqualTo(5L);
        verify(contratoRentaRepository).countByEstatusContrato("ACTIVO");
    }

    @Test
    void countActiveContractsExpiringBefore_ShouldReturnCorrectCount() {
        // Given
        LocalDateTime date = LocalDateTime.now().plusDays(30);
        when(contratoRentaRepository.countActiveContractsExpiringBefore(date)).thenReturn(3L);

        // When
        long result = contratoRentaService.countActiveContractsExpiringBefore(date);

        // Then
        assertThat(result).isEqualTo(3L);
        verify(contratoRentaRepository).countActiveContractsExpiringBefore(date);
    }

    @Test
    void getActiveContractsExpiringBefore_ShouldReturnMatchingContracts() {
        // Given
        LocalDateTime date = LocalDateTime.now().plusDays(30);
        List<ContratoRenta> contratos = Arrays.asList(testContrato);
        when(contratoRentaRepository.findActiveContractsExpiringBefore(date)).thenReturn(contratos);

        // When
        List<ContratoRentaDTO> result = contratoRentaService.getActiveContractsExpiringBefore(date);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).estatusContrato()).isEqualTo("ACTIVO");
        verify(contratoRentaRepository).findActiveContractsExpiringBefore(date);
    }

    @Test
    void getContractsNeedingNotification_ShouldReturnContractsNeedingNotification() {
        // Given
        LocalDateTime pastDate = LocalDateTime.now().minusDays(35);
        testContrato.setFechaFinContrato(pastDate);
        List<ContratoRenta> contratos = Arrays.asList(testContrato);
        when(contratoRentaRepository.findContractsNeedingNotification(any(LocalDateTime.class))).thenReturn(contratos);

        // When
        List<ContratoRentaDTO> result = contratoRentaService.getContractsNeedingNotification();

        // Then
        assertThat(result).hasSize(1);
        verify(contratoRentaRepository).findContractsNeedingNotification(any(LocalDateTime.class));
    }
}