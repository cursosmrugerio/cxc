package com.inmobiliaria.gestion.propiedad.service;

import com.inmobiliaria.gestion.propiedad.dto.PropiedadCreateRequest;
import com.inmobiliaria.gestion.propiedad.dto.PropiedadDTO;
import com.inmobiliaria.gestion.propiedad.dto.PropiedadUpdateRequest;
import com.inmobiliaria.gestion.propiedad.model.Propiedad;
import com.inmobiliaria.gestion.propiedad.repository.PropiedadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropiedadServiceTest {

    @Mock
    private PropiedadRepository propiedadRepository;

    @InjectMocks
    private PropiedadService propiedadService;

    private Propiedad testPropiedad;
    private PropiedadCreateRequest createRequest;
    private PropiedadUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testPropiedad = Propiedad.builder()
                .idPropiedad(1)
                .idInmobiliaria(1L)
                .tipoPropiedad("CASA")
                .superficieTotal(BigDecimal.valueOf(150.50))
                .superficieConstruida(BigDecimal.valueOf(120.75))
                .fechaRegistro(LocalDate.now())
                .estatusPropiedad("DISPONIBLE")
                .caracteristicasEspeciales("Jardín amplio")
                .direccionCompleta("Calle Reforma 123")
                .numeroBanos(2)
                .numeroHabitaciones(3)
                .build();

        createRequest = new PropiedadCreateRequest(
                1L,
                "CASA",
                BigDecimal.valueOf(150.50),
                BigDecimal.valueOf(120.75),
                "DISPONIBLE",
                "Jardín amplio",
                "Calle Reforma 123",
                2,
                3
        );

        updateRequest = new PropiedadUpdateRequest(
                "DEPARTAMENTO",
                BigDecimal.valueOf(200.00),
                BigDecimal.valueOf(180.00),
                "OCUPADA",
                "Balcón amplio",
                "Av. Insurgentes 456",
                3,
                4
        );
    }

    @Test
    void getAllPropiedades_ShouldReturnAllPropiedades() {
        // Given
        List<Propiedad> propiedades = Arrays.asList(testPropiedad);
        when(propiedadRepository.findAll()).thenReturn(propiedades);

        // When
        List<PropiedadDTO> result = propiedadService.getAllPropiedades();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).idPropiedad()).isEqualTo(1);
        assertThat(result.get(0).tipoPropiedad()).isEqualTo("CASA");
        verify(propiedadRepository).findAll();
    }

    @Test
    void getPropiedadById_WhenExists_ShouldReturnPropiedad() {
        // Given
        when(propiedadRepository.findById(1)).thenReturn(Optional.of(testPropiedad));

        // When
        Optional<PropiedadDTO> result = propiedadService.getPropiedadById(1);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().idPropiedad()).isEqualTo(1);
        assertThat(result.get().tipoPropiedad()).isEqualTo("CASA");
        verify(propiedadRepository).findById(1);
    }

    @Test
    void getPropiedadById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(propiedadRepository.findById(1)).thenReturn(Optional.empty());

        // When
        Optional<PropiedadDTO> result = propiedadService.getPropiedadById(1);

        // Then
        assertThat(result).isEmpty();
        verify(propiedadRepository).findById(1);
    }

    @Test
    void getPropiedadesByInmobiliaria_ShouldReturnPropiedadesByInmobiliaria() {
        // Given
        List<Propiedad> propiedades = Arrays.asList(testPropiedad);
        when(propiedadRepository.findByIdInmobiliaria(1L)).thenReturn(propiedades);

        // When
        List<PropiedadDTO> result = propiedadService.getPropiedadesByInmobiliaria(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).idInmobiliaria()).isEqualTo(1L);
        verify(propiedadRepository).findByIdInmobiliaria(1L);
    }

    @Test
    void getPropiedadesByEstatus_ShouldReturnPropiedadesByEstatus() {
        // Given
        List<Propiedad> propiedades = Arrays.asList(testPropiedad);
        when(propiedadRepository.findByEstatusPropiedad("DISPONIBLE")).thenReturn(propiedades);

        // When
        List<PropiedadDTO> result = propiedadService.getPropiedadesByEstatus("DISPONIBLE");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).estatusPropiedad()).isEqualTo("DISPONIBLE");
        verify(propiedadRepository).findByEstatusPropiedad("DISPONIBLE");
    }

    @Test
    void createPropiedad_ShouldCreateAndReturnPropiedad() {
        // Given
        when(propiedadRepository.save(any(Propiedad.class))).thenReturn(testPropiedad);

        // When
        PropiedadDTO result = propiedadService.createPropiedad(createRequest);

        // Then
        assertThat(result.idPropiedad()).isEqualTo(1);
        assertThat(result.tipoPropiedad()).isEqualTo("CASA");
        assertThat(result.idInmobiliaria()).isEqualTo(1L);
        verify(propiedadRepository).save(any(Propiedad.class));
    }

    @Test
    void createPropiedad_WithNullEstatus_ShouldSetDefaultEstatus() {
        // Given
        PropiedadCreateRequest requestWithNullEstatus = new PropiedadCreateRequest(
                1L, "CASA", BigDecimal.valueOf(150.50), BigDecimal.valueOf(120.75),
                null, "Jardín amplio", "Calle Reforma 123", 2, 3
        );
        
        Propiedad expectedPropiedad = Propiedad.builder()
                .idPropiedad(1)
                .idInmobiliaria(1L)
                .tipoPropiedad("CASA")
                .superficieTotal(BigDecimal.valueOf(150.50))
                .superficieConstruida(BigDecimal.valueOf(120.75))
                .fechaRegistro(LocalDate.now())
                .estatusPropiedad("DISPONIBLE")
                .caracteristicasEspeciales("Jardín amplio")
                .direccionCompleta("Calle Reforma 123")
                .numeroBanos(2)
                .numeroHabitaciones(3)
                .build();

        when(propiedadRepository.save(any(Propiedad.class))).thenReturn(expectedPropiedad);

        // When
        PropiedadDTO result = propiedadService.createPropiedad(requestWithNullEstatus);

        // Then
        assertThat(result.estatusPropiedad()).isEqualTo("DISPONIBLE");
        verify(propiedadRepository).save(any(Propiedad.class));
    }

    @Test
    void updatePropiedad_WhenExists_ShouldUpdateAndReturnPropiedad() {
        // Given
        Propiedad updatedPropiedad = Propiedad.builder()
                .idPropiedad(1)
                .idInmobiliaria(1L)
                .tipoPropiedad("DEPARTAMENTO")
                .superficieTotal(BigDecimal.valueOf(200.00))
                .superficieConstruida(BigDecimal.valueOf(180.00))
                .fechaRegistro(LocalDate.now())
                .estatusPropiedad("OCUPADA")
                .caracteristicasEspeciales("Balcón amplio")
                .direccionCompleta("Av. Insurgentes 456")
                .numeroBanos(3)
                .numeroHabitaciones(4)
                .build();

        when(propiedadRepository.findById(1)).thenReturn(Optional.of(testPropiedad));
        when(propiedadRepository.save(any(Propiedad.class))).thenReturn(updatedPropiedad);

        // When
        Optional<PropiedadDTO> result = propiedadService.updatePropiedad(1, updateRequest);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().tipoPropiedad()).isEqualTo("DEPARTAMENTO");
        assertThat(result.get().estatusPropiedad()).isEqualTo("OCUPADA");
        verify(propiedadRepository).findById(1);
        verify(propiedadRepository).save(any(Propiedad.class));
    }

    @Test
    void updatePropiedad_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(propiedadRepository.findById(1)).thenReturn(Optional.empty());

        // When
        Optional<PropiedadDTO> result = propiedadService.updatePropiedad(1, updateRequest);

        // Then
        assertThat(result).isEmpty();
        verify(propiedadRepository).findById(1);
        verify(propiedadRepository, never()).save(any(Propiedad.class));
    }

    @Test
    void deletePropiedad_WhenExists_ShouldReturnTrue() {
        // Given
        when(propiedadRepository.existsById(1)).thenReturn(true);

        // When
        boolean result = propiedadService.deletePropiedad(1);

        // Then
        assertThat(result).isTrue();
        verify(propiedadRepository).existsById(1);
        verify(propiedadRepository).deleteById(1);
    }

    @Test
    void deletePropiedad_WhenNotExists_ShouldReturnFalse() {
        // Given
        when(propiedadRepository.existsById(1)).thenReturn(false);

        // When
        boolean result = propiedadService.deletePropiedad(1);

        // Then
        assertThat(result).isFalse();
        verify(propiedadRepository).existsById(1);
        verify(propiedadRepository, never()).deleteById(anyInt());
    }

    @Test
    void existsPropiedadByIdAndInmobiliaria_ShouldReturnCorrectResult() {
        // Given
        when(propiedadRepository.existsByIdPropiedadAndIdInmobiliaria(1, 1L)).thenReturn(true);

        // When
        boolean result = propiedadService.existsPropiedadByIdAndInmobiliaria(1, 1L);

        // Then
        assertThat(result).isTrue();
        verify(propiedadRepository).existsByIdPropiedadAndIdInmobiliaria(1, 1L);
    }

    @Test
    void countPropiedadesByInmobiliaria_ShouldReturnCorrectCount() {
        // Given
        when(propiedadRepository.countByIdInmobiliaria(1L)).thenReturn(5L);

        // When
        long result = propiedadService.countPropiedadesByInmobiliaria(1L);

        // Then
        assertThat(result).isEqualTo(5L);
        verify(propiedadRepository).countByIdInmobiliaria(1L);
    }

    @Test
    void countPropiedadesByInmobiliariaAndEstatus_ShouldReturnCorrectCount() {
        // Given
        when(propiedadRepository.countByIdInmobiliariaAndEstatusPropiedad(1L, "DISPONIBLE")).thenReturn(3L);

        // When
        long result = propiedadService.countPropiedadesByInmobiliariaAndEstatus(1L, "DISPONIBLE");

        // Then
        assertThat(result).isEqualTo(3L);
        verify(propiedadRepository).countByIdInmobiliariaAndEstatusPropiedad(1L, "DISPONIBLE");
    }

    @Test
    void searchPropiedadesByDireccion_ShouldReturnMatchingPropiedades() {
        // Given
        List<Propiedad> propiedades = Arrays.asList(testPropiedad);
        when(propiedadRepository.findByDireccionContaining("Reforma")).thenReturn(propiedades);

        // When
        List<PropiedadDTO> result = propiedadService.searchPropiedadesByDireccion("Reforma");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).direccionCompleta()).contains("Reforma");
        verify(propiedadRepository).findByDireccionContaining("Reforma");
    }
}