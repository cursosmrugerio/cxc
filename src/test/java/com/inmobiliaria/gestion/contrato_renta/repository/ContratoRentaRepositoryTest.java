package com.inmobiliaria.gestion.contrato_renta.repository;

import com.inmobiliaria.gestion.contrato_renta.model.ContratoRenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ContratoRentaRepository Tests")
class ContratoRentaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ContratoRentaRepository contratoRentaRepository;

    private ContratoRenta testContrato1;
    private ContratoRenta testContrato2;
    private ContratoRenta testContrato3;

    @BeforeEach
    void setUp() {
        contratoRentaRepository.deleteAll();
        testContrato1 = createTestContrato(1, "ACTIVO", LocalDateTime.of(2024, 1, 15, 10, 0), 12);
        testContrato2 = createTestContrato(2, "VENCIDO", LocalDateTime.of(2024, 2, 1, 9, 0), 6);
        testContrato3 = createTestContrato(1, "TERMINADO", LocalDateTime.of(2024, 3, 1, 8, 0), 24);
    }

    private ContratoRenta createTestContrato(Integer propiedadId, String estatus, LocalDateTime fechaInicio, Integer duracion) {
        return ContratoRenta.builder()
                .idPropiedad(propiedadId)
                .fechaInicioContrato(fechaInicio)
                .fechaFinContrato(fechaInicio.plusMonths(duracion))
                .condicionesEspeciales("No pets allowed")
                .emailNotificaciones("tenant" + propiedadId + "@example.com")
                .estatusContrato(estatus)
                .depositoGarantia(BigDecimal.valueOf(5000.00))
                .duracionMeses(duracion)
                .notificacionDiasPrevios(30)
                .telefonoNotificaciones("+52 55 1234 567" + propiedadId)
                .build();
    }

    @Nested
    @DisplayName("Basic CRUD Operations")
    class BasicCrudOperations {

        @Test
        @DisplayName("Should save and find contract by id")
        void shouldSaveAndFindContractById() {
            ContratoRenta saved = contratoRentaRepository.save(testContrato1);
            entityManager.flush();

            Optional<ContratoRenta> found = contratoRentaRepository.findById(saved.getIdContrato());

            assertThat(found).isPresent();
            assertThat(found.get().getIdPropiedad()).isEqualTo(1);
            assertThat(found.get().getEstatusContrato()).isEqualTo("ACTIVO");
            assertThat(found.get().getDuracionMeses()).isEqualTo(12);
        }

        @Test
        @DisplayName("Should find all contracts")
        void shouldFindAllContracts() {
            contratoRentaRepository.save(testContrato1);
            contratoRentaRepository.save(testContrato2);
            contratoRentaRepository.save(testContrato3);
            entityManager.flush();

            List<ContratoRenta> contratos = contratoRentaRepository.findAll();

            assertThat(contratos).hasSize(3);
        }

        @Test
        @DisplayName("Should delete contract by id")
        void shouldDeleteContractById() {
            ContratoRenta saved = contratoRentaRepository.save(testContrato1);
            entityManager.flush();

            contratoRentaRepository.deleteById(saved.getIdContrato());
            entityManager.flush();

            Optional<ContratoRenta> found = contratoRentaRepository.findById(saved.getIdContrato());
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should update contract successfully")
        void shouldUpdateContractSuccessfully() {
            ContratoRenta saved = contratoRentaRepository.save(testContrato1);
            entityManager.flush();

            saved.setEstatusContrato("SUSPENDIDO");
            saved.setCondicionesEspeciales("Updated conditions");
            ContratoRenta updated = contratoRentaRepository.save(saved);
            entityManager.flush();

            Optional<ContratoRenta> found = contratoRentaRepository.findById(updated.getIdContrato());
            assertThat(found).isPresent();
            assertThat(found.get().getEstatusContrato()).isEqualTo("SUSPENDIDO");
            assertThat(found.get().getCondicionesEspeciales()).isEqualTo("Updated conditions");
        }
    }

    @Nested
    @DisplayName("Find by Property Operations")
    class FindByPropertyOperations {

        @Test
        @DisplayName("Should find contracts by property id")
        void shouldFindContractsByPropertyId() {
            contratoRentaRepository.save(testContrato1);
            contratoRentaRepository.save(testContrato2);
            contratoRentaRepository.save(testContrato3);
            entityManager.flush();

            List<ContratoRenta> contratos = contratoRentaRepository.findByIdPropiedad(1);

            assertThat(contratos).hasSize(2);
            contratos.forEach(contrato -> assertThat(contrato.getIdPropiedad()).isEqualTo(1));
        }

        @Test
        @DisplayName("Should return empty list when property has no contracts")
        void shouldReturnEmptyListWhenPropertyHasNoContracts() {
            contratoRentaRepository.save(testContrato1);
            entityManager.flush();

            List<ContratoRenta> contratos = contratoRentaRepository.findByIdPropiedad(999);

            assertThat(contratos).isEmpty();
        }

        @Test
        @DisplayName("Should find contract by property and status")
        void shouldFindContractByPropertyAndStatus() {
            contratoRentaRepository.save(testContrato1);
            contratoRentaRepository.save(testContrato3);
            entityManager.flush();

            Optional<ContratoRenta> contrato = contratoRentaRepository.findByIdPropiedadAndEstatusContrato(1, "ACTIVO");

            assertThat(contrato).isPresent();
            assertThat(contrato.get().getIdPropiedad()).isEqualTo(1);
            assertThat(contrato.get().getEstatusContrato()).isEqualTo("ACTIVO");
        }

        @Test
        @DisplayName("Should return empty when property-status combination not found")
        void shouldReturnEmptyWhenPropertyStatusCombinationNotFound() {
            contratoRentaRepository.save(testContrato1);
            entityManager.flush();

            Optional<ContratoRenta> contrato = contratoRentaRepository.findByIdPropiedadAndEstatusContrato(1, "SUSPENDIDO");

            assertThat(contrato).isEmpty();
        }
    }

    @Nested
    @DisplayName("Find by Status Operations")
    class FindByStatusOperations {

        @Test
        @DisplayName("Should find contracts by status")
        void shouldFindContractsByStatus() {
            contratoRentaRepository.save(testContrato1);
            contratoRentaRepository.save(testContrato2);
            contratoRentaRepository.save(testContrato3);
            entityManager.flush();

            List<ContratoRenta> activosContratos = contratoRentaRepository.findByEstatusContrato("ACTIVO");
            List<ContratoRenta> vencidosContratos = contratoRentaRepository.findByEstatusContrato("VENCIDO");
            List<ContratoRenta> terminadosContratos = contratoRentaRepository.findByEstatusContrato("TERMINADO");

            assertThat(activosContratos).hasSize(1);
            assertThat(vencidosContratos).hasSize(1);
            assertThat(terminadosContratos).hasSize(1);
        }

        @Test
        @DisplayName("Should return empty list for non-existent status")
        void shouldReturnEmptyListForNonExistentStatus() {
            contratoRentaRepository.save(testContrato1);
            entityManager.flush();

            List<ContratoRenta> contratos = contratoRentaRepository.findByEstatusContrato("INEXISTENTE");

            assertThat(contratos).isEmpty();
        }

        @Test
        @DisplayName("Should count contracts by status")
        void shouldCountContractsByStatus() {
            contratoRentaRepository.save(testContrato1);
            contratoRentaRepository.save(testContrato2);
            contratoRentaRepository.save(testContrato3);
            entityManager.flush();

            long activosCount = contratoRentaRepository.countByEstatusContrato("ACTIVO");
            long vencidosCount = contratoRentaRepository.countByEstatusContrato("VENCIDO");
            long terminadosCount = contratoRentaRepository.countByEstatusContrato("TERMINADO");
            long inexistenteCount = contratoRentaRepository.countByEstatusContrato("INEXISTENTE");

            assertThat(activosCount).isEqualTo(1);
            assertThat(vencidosCount).isEqualTo(1);
            assertThat(terminadosCount).isEqualTo(1);
            assertThat(inexistenteCount).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Find by Contact Information")
    class FindByContactInformation {

        @Test
        @DisplayName("Should find contracts by email notifications")
        void shouldFindContractsByEmailNotifications() {
            contratoRentaRepository.save(testContrato1);
            contratoRentaRepository.save(testContrato2);
            entityManager.flush();

            List<ContratoRenta> contratos = contratoRentaRepository.findByEmailNotificaciones("tenant1@example.com");

            assertThat(contratos).hasSize(1);
            assertThat(contratos.get(0).getEmailNotificaciones()).isEqualTo("tenant1@example.com");
        }

        @Test
        @DisplayName("Should find contracts by phone notifications")
        void shouldFindContractsByPhoneNotifications() {
            contratoRentaRepository.save(testContrato1);
            contratoRentaRepository.save(testContrato2);
            entityManager.flush();

            List<ContratoRenta> contratos = contratoRentaRepository.findByTelefonoNotificaciones("+52 55 1234 5671");

            assertThat(contratos).hasSize(1);
            assertThat(contratos.get(0).getTelefonoNotificaciones()).isEqualTo("+52 55 1234 5671");
        }

        @Test
        @DisplayName("Should return empty list for non-existent email")
        void shouldReturnEmptyListForNonExistentEmail() {
            contratoRentaRepository.save(testContrato1);
            entityManager.flush();

            List<ContratoRenta> contratos = contratoRentaRepository.findByEmailNotificaciones("nonexistent@example.com");

            assertThat(contratos).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list for non-existent phone")
        void shouldReturnEmptyListForNonExistentPhone() {
            contratoRentaRepository.save(testContrato1);
            entityManager.flush();

            List<ContratoRenta> contratos = contratoRentaRepository.findByTelefonoNotificaciones("+52 55 9999 9999");

            assertThat(contratos).isEmpty();
        }
    }

    @Nested
    @DisplayName("Date Range Queries")
    class DateRangeQueries {

        @Test
        @DisplayName("Should find contracts by end date range")
        void shouldFindContractsByEndDateRange() {
            contratoRentaRepository.save(testContrato1);
            contratoRentaRepository.save(testContrato2);
            contratoRentaRepository.save(testContrato3);
            entityManager.flush();

            LocalDateTime startDate = LocalDateTime.of(2024, 12, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2025, 3, 31, 23, 59);

            List<ContratoRenta> contratos = contratoRentaRepository.findByFechaFinContratoBetween(startDate, endDate);

            assertThat(contratos).hasSize(2); // testContrato1 and testContrato3
        }

        @Test
        @DisplayName("Should find contracts by start date range")
        void shouldFindContractsByStartDateRange() {
            contratoRentaRepository.save(testContrato1);
            contratoRentaRepository.save(testContrato2);
            contratoRentaRepository.save(testContrato3);
            entityManager.flush();

            LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2024, 2, 28, 23, 59);

            List<ContratoRenta> contratos = contratoRentaRepository.findByFechaInicioContratoBetween(startDate, endDate);

            assertThat(contratos).hasSize(2); // testContrato1 and testContrato2
        }

        @Test
        @DisplayName("Should return empty list for date range with no contracts")
        void shouldReturnEmptyListForDateRangeWithNoContracts() {
            contratoRentaRepository.save(testContrato1);
            entityManager.flush();

            LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

            List<ContratoRenta> contratos = contratoRentaRepository.findByFechaFinContratoBetween(startDate, endDate);

            assertThat(contratos).isEmpty();
        }
    }

    @Nested
    @DisplayName("Active Contracts Expiration Queries")
    class ActiveContractsExpirationQueries {

        @Test
        @DisplayName("Should find active contracts expiring before date")
        void shouldFindActiveContractsExpiringBeforeDate() {
            // Create active contracts with different end dates
            ContratoRenta activoExpiringSoon = createTestContrato(1, "ACTIVO", LocalDateTime.of(2024, 1, 1, 10, 0), 1);
            ContratoRenta activoExpiringLater = createTestContrato(2, "ACTIVO", LocalDateTime.of(2024, 1, 1, 10, 0), 12);
            ContratoRenta vencido = createTestContrato(3, "VENCIDO", LocalDateTime.of(2024, 1, 1, 10, 0), 1);

            contratoRentaRepository.save(activoExpiringSoon);
            contratoRentaRepository.save(activoExpiringLater);
            contratoRentaRepository.save(vencido);
            entityManager.flush();

            LocalDateTime thresholdDate = LocalDateTime.of(2024, 6, 1, 0, 0);

            List<ContratoRenta> contratos = contratoRentaRepository.findActiveContractsExpiringBefore(thresholdDate);

            assertThat(contratos).hasSize(1);
            assertThat(contratos.get(0).getEstatusContrato()).isEqualTo("ACTIVO");
            assertThat(contratos.get(0).getFechaFinContrato()).isBefore(thresholdDate);
        }

        @Test
        @DisplayName("Should find active contracts expiring between dates")
        void shouldFindActiveContractsExpiringBetweenDates() {
            // Create contracts with different end dates and statuses
            ContratoRenta activo1 = createTestContrato(1, "ACTIVO", LocalDateTime.of(2024, 1, 1, 10, 0), 3);
            ContratoRenta activo2 = createTestContrato(2, "ACTIVO", LocalDateTime.of(2024, 1, 1, 10, 0), 6);
            ContratoRenta activo3 = createTestContrato(3, "ACTIVO", LocalDateTime.of(2024, 1, 1, 10, 0), 12);
            ContratoRenta vencido = createTestContrato(4, "VENCIDO", LocalDateTime.of(2024, 1, 1, 10, 0), 3);

            contratoRentaRepository.save(activo1);
            contratoRentaRepository.save(activo2);
            contratoRentaRepository.save(activo3);
            contratoRentaRepository.save(vencido);
            entityManager.flush();

            LocalDateTime startDate = LocalDateTime.of(2024, 3, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2024, 8, 31, 23, 59);

            List<ContratoRenta> contratos = contratoRentaRepository.findActiveContractsExpiringBetween(startDate, endDate);

            assertThat(contratos).hasSize(2); // activo1 and activo2
            contratos.forEach(contrato -> {
                assertThat(contrato.getEstatusContrato()).isEqualTo("ACTIVO");
                assertThat(contrato.getFechaFinContrato()).isBetween(startDate, endDate);
            });
        }

        @Test
        @DisplayName("Should count active contracts expiring before date")
        void shouldCountActiveContractsExpiringBeforeDate() {
            ContratoRenta activo1 = createTestContrato(1, "ACTIVO", LocalDateTime.of(2024, 1, 1, 10, 0), 1);
            ContratoRenta activo2 = createTestContrato(2, "ACTIVO", LocalDateTime.of(2024, 1, 1, 10, 0), 12);
            ContratoRenta vencido = createTestContrato(3, "VENCIDO", LocalDateTime.of(2024, 1, 1, 10, 0), 1);

            contratoRentaRepository.save(activo1);
            contratoRentaRepository.save(activo2);
            contratoRentaRepository.save(vencido);
            entityManager.flush();

            LocalDateTime thresholdDate = LocalDateTime.of(2024, 6, 1, 0, 0);

            long count = contratoRentaRepository.countActiveContractsExpiringBefore(thresholdDate);

            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("Should find contracts needing notification")
        void shouldFindContractsNeedingNotification() {
            // Create contracts with notification needed
            ContratoRenta needsNotification = createTestContrato(1, "ACTIVO", LocalDateTime.of(2024, 1, 1, 10, 0), 2);
            ContratoRenta noNotificationNeeded = createTestContrato(2, "ACTIVO", LocalDateTime.of(2024, 1, 1, 10, 0), 12);
            ContratoRenta inactiveContract = createTestContrato(3, "TERMINADO", LocalDateTime.of(2024, 1, 1, 10, 0), 2);

            contratoRentaRepository.save(needsNotification);
            contratoRentaRepository.save(noNotificationNeeded);
            contratoRentaRepository.save(inactiveContract);
            entityManager.flush();

            LocalDateTime notificationDate = LocalDateTime.of(2024, 6, 1, 0, 0);

            List<ContratoRenta> contratos = contratoRentaRepository.findContractsNeedingNotification(notificationDate);

            assertThat(contratos).hasSize(1);
            assertThat(contratos.get(0).getEstatusContrato()).isEqualTo("ACTIVO");
            assertThat(contratos.get(0).getFechaFinContrato()).isBeforeOrEqualTo(notificationDate);
        }
    }

    @Nested
    @DisplayName("Existence and Business Logic Queries")
    class ExistenceAndBusinessLogicQueries {

        @Test
        @DisplayName("Should check if contract exists by property and status")
        void shouldCheckIfContractExistsByPropertyAndStatus() {
            contratoRentaRepository.save(testContrato1);
            entityManager.flush();

            boolean exists = contratoRentaRepository.existsByIdPropiedadAndEstatusContrato(1, "ACTIVO");
            boolean notExists = contratoRentaRepository.existsByIdPropiedadAndEstatusContrato(1, "VENCIDO");
            boolean propertyNotExists = contratoRentaRepository.existsByIdPropiedadAndEstatusContrato(999, "ACTIVO");

            assertThat(exists).isTrue();
            assertThat(notExists).isFalse();
            assertThat(propertyNotExists).isFalse();
        }

        @Test
        @DisplayName("Should find contracts by duration")
        void shouldFindContractsByDuration() {
            contratoRentaRepository.save(testContrato1); // 12 months
            contratoRentaRepository.save(testContrato2); // 6 months
            contratoRentaRepository.save(testContrato3); // 24 months
            entityManager.flush();

            List<ContratoRenta> contracts12Months = contratoRentaRepository.findByDuracionMeses(12);
            List<ContratoRenta> contracts6Months = contratoRentaRepository.findByDuracionMeses(6);
            List<ContratoRenta> contracts24Months = contratoRentaRepository.findByDuracionMeses(24);

            assertThat(contracts12Months).hasSize(1);
            assertThat(contracts6Months).hasSize(1);
            assertThat(contracts24Months).hasSize(1);
        }

        @Test
        @DisplayName("Should find contracts by minimum deposit amount")
        void shouldFindContractsByMinimumDepositAmount() {
            ContratoRenta lowDeposit = createTestContrato(1, "ACTIVO", LocalDateTime.of(2024, 1, 1, 10, 0), 12);
            lowDeposit.setDepositoGarantia(BigDecimal.valueOf(3000.00));

            ContratoRenta highDeposit = createTestContrato(2, "ACTIVO", LocalDateTime.of(2024, 1, 1, 10, 0), 12);
            highDeposit.setDepositoGarantia(BigDecimal.valueOf(8000.00));

            contratoRentaRepository.save(lowDeposit);
            contratoRentaRepository.save(highDeposit);
            contratoRentaRepository.save(testContrato1); // 5000.00 deposit
            entityManager.flush();

            List<ContratoRenta> contractsHighDeposit = contratoRentaRepository.findByDepositoGarantiaGreaterThanEqual(BigDecimal.valueOf(5000.00));
            List<ContratoRenta> contractsVeryHighDeposit = contratoRentaRepository.findByDepositoGarantiaGreaterThanEqual(BigDecimal.valueOf(7000.00));

            assertThat(contractsHighDeposit).hasSize(2); // testContrato1 and highDeposit
            assertThat(contractsVeryHighDeposit).hasSize(1); // only highDeposit
        }
    }

    @Nested
    @DisplayName("Data Integrity and Edge Cases")
    class DataIntegrityAndEdgeCases {

        @Test
        @DisplayName("Should handle null values appropriately")
        void shouldHandleNullValuesAppropriately() {
            ContratoRenta contratoWithNulls = ContratoRenta.builder()
                    .idPropiedad(1)
                    .fechaInicioContrato(LocalDateTime.of(2024, 1, 1, 10, 0))
                    .estatusContrato("ACTIVO")
                    .duracionMeses(12)
                    .build();

            ContratoRenta saved = contratoRentaRepository.save(contratoWithNulls);
            entityManager.flush();

            Optional<ContratoRenta> found = contratoRentaRepository.findById(saved.getIdContrato());

            assertThat(found).isPresent();
            assertThat(found.get().getCondicionesEspeciales()).isNull();
            assertThat(found.get().getEmailNotificaciones()).isNull();
            assertThat(found.get().getTelefonoNotificaciones()).isNull();
        }

        @Test
        @DisplayName("Should handle decimal precision correctly")
        void shouldHandleDecimalPrecisionCorrectly() {
            ContratoRenta contrato = createTestContrato(1, "ACTIVO", LocalDateTime.of(2024, 1, 1, 10, 0), 12);
            contrato.setDepositoGarantia(new BigDecimal("12345.67"));

            ContratoRenta saved = contratoRentaRepository.save(contrato);
            entityManager.flush();

            Optional<ContratoRenta> found = contratoRentaRepository.findById(saved.getIdContrato());

            assertThat(found).isPresent();
            assertThat(found.get().getDepositoGarantia()).isEqualByComparingTo(new BigDecimal("12345.67"));
        }

        @Test
        @DisplayName("Should handle special characters in text fields")
        void shouldHandleSpecialCharactersInTextFields() {
            ContratoRenta contrato = createTestContrato(1, "ACTIVO", LocalDateTime.of(2024, 1, 1, 10, 0), 12);
            contrato.setCondicionesEspeciales("Condiciones especiales: áéíóú ñ @#$%");
            contrato.setEmailNotificaciones("usuario.especial+test@dominio-ejemplo.com");

            ContratoRenta saved = contratoRentaRepository.save(contrato);
            entityManager.flush();

            Optional<ContratoRenta> found = contratoRentaRepository.findById(saved.getIdContrato());

            assertThat(found).isPresent();
            assertThat(found.get().getCondicionesEspeciales()).contains("áéíóú ñ @#$%");
            assertThat(found.get().getEmailNotificaciones()).isEqualTo("usuario.especial+test@dominio-ejemplo.com");
        }

        @Test
        @DisplayName("Should handle large datasets efficiently")
        void shouldHandleLargeDatasetsEfficiently() {
            for (int i = 1; i <= 50; i++) {
                ContratoRenta contrato = createTestContrato(i % 10 + 1, "ACTIVO", LocalDateTime.of(2024, 1, i % 28 + 1, 10, 0), 12);
                contratoRentaRepository.save(contrato);
            }
            entityManager.flush();

            long totalCount = contratoRentaRepository.count();
            List<ContratoRenta> activosContratos = contratoRentaRepository.findByEstatusContrato("ACTIVO");
            List<ContratoRenta> propiedad1Contratos = contratoRentaRepository.findByIdPropiedad(1);

            assertThat(totalCount).isEqualTo(50);
            assertThat(activosContratos).hasSize(50);
            assertThat(propiedad1Contratos).hasSize(5);
        }

        @Test
        @DisplayName("Should handle empty search results gracefully")
        void shouldHandleEmptySearchResultsGracefully() {
            List<ContratoRenta> inexistenteProp = contratoRentaRepository.findByIdPropiedad(999);
            List<ContratoRenta> inexistenteEstatus = contratoRentaRepository.findByEstatusContrato("INEXISTENTE");
            List<ContratoRenta> inexistenteEmail = contratoRentaRepository.findByEmailNotificaciones("inexistente@example.com");

            assertThat(inexistenteProp).isEmpty();
            assertThat(inexistenteEstatus).isEmpty();
            assertThat(inexistenteEmail).isEmpty();
        }
    }

    @Nested
    @DisplayName("Entity Lifecycle Events")
    class EntityLifecycleEvents {

        @Test
        @DisplayName("Should automatically calculate end date on persist")
        void shouldAutomaticallyCalculateEndDateOnPersist() {
            ContratoRenta contrato = ContratoRenta.builder()
                    .idPropiedad(1)
                    .fechaInicioContrato(LocalDateTime.of(2024, 1, 15, 10, 0))
                    .duracionMeses(12)
                    .estatusContrato("ACTIVO")
                    .build();

            ContratoRenta saved = contratoRentaRepository.save(contrato);
            entityManager.flush();

            assertThat(saved.getFechaFinContrato()).isEqualTo(LocalDateTime.of(2025, 1, 15, 10, 0));
        }

        @Test
        @DisplayName("Should recalculate end date on update")
        void shouldRecalculateEndDateOnUpdate() {
            ContratoRenta contrato = createTestContrato(1, "ACTIVO", LocalDateTime.of(2024, 1, 15, 10, 0), 12);
            ContratoRenta saved = contratoRentaRepository.save(contrato);
            entityManager.flush();

            saved.setDuracionMeses(18);
            ContratoRenta updated = contratoRentaRepository.save(saved);
            entityManager.flush();

            assertThat(updated.getFechaFinContrato()).isEqualTo(LocalDateTime.of(2025, 7, 15, 10, 0));
        }

        @Test
        @DisplayName("Should not override manually set end date when duration is null")
        void shouldNotOverrideManuallySetEndDateWhenDurationIsNull() {
            ContratoRenta contrato = ContratoRenta.builder()
                    .idPropiedad(1)
                    .fechaInicioContrato(LocalDateTime.of(2024, 1, 15, 10, 0))
                    .fechaFinContrato(LocalDateTime.of(2024, 6, 30, 10, 0))
                    .estatusContrato("ACTIVO")
                    .build();

            ContratoRenta saved = contratoRentaRepository.save(contrato);
            entityManager.flush();

            assertThat(saved.getFechaFinContrato()).isEqualTo(LocalDateTime.of(2024, 6, 30, 10, 0));
        }
    }
}