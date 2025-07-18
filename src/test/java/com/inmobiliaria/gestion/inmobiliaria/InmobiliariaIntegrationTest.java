package com.inmobiliaria.gestion.inmobiliaria;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmobiliaria.gestion.inmobiliaria.dto.InmobiliariaDTO;
import com.inmobiliaria.gestion.inmobiliaria.model.Inmobiliaria;
import com.inmobiliaria.gestion.inmobiliaria.repository.InmobiliariaRepository;
import com.inmobiliaria.gestion.auth.model.Role;
import com.inmobiliaria.gestion.auth.model.User;
import com.inmobiliaria.gestion.auth.repository.RoleRepository;
import com.inmobiliaria.gestion.auth.repository.UserRepository;
import com.inmobiliaria.gestion.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Inmobiliaria Integration Tests")
class InmobiliariaIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private InmobiliariaRepository inmobiliariaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String adminToken;
    private String userToken;
    private Inmobiliaria testInmobiliaria;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        // Clean repositories
        inmobiliariaRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create roles
        Role adminRole = createRole("ROLE_ADMIN");
        Role userRole = createRole("ROLE_USER");

        // Create test users
        User adminUser = createUser("admin", "admin@test.com", "password", Set.of(adminRole, userRole));
        User normalUser = createUser("user", "user@test.com", "password", Set.of(userRole));

        // Generate JWT tokens for testing
        adminToken = jwtUtil.generateTokenFromUsername("admin");
        userToken = jwtUtil.generateTokenFromUsername("user");

        // Create test data
        testInmobiliaria = createTestInmobiliaria();
        testInmobiliaria = inmobiliariaRepository.save(testInmobiliaria);
    }

    private Role createRole(String roleName) {
        Role role = new Role();
        role.setName(Role.ERole.valueOf(roleName));
        return roleRepository.save(role);
    }

    private User createUser(String username, String email, String password, Set<Role> roles) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        return userRepository.save(user);
    }

    private Inmobiliaria createTestInmobiliaria() {
        Inmobiliaria inmobiliaria = new Inmobiliaria();
        inmobiliaria.setNombreComercial("Inmobiliaria Los Pinos");
        inmobiliaria.setRazonSocial("Inmobiliaria Los Pinos S.A. de C.V.");
        inmobiliaria.setRfcNit("ILP123456789");
        inmobiliaria.setTelefonoPrincipal("555-123-4567");
        inmobiliaria.setEmailContacto("contacto@lospinos.com");
        inmobiliaria.setDireccionCompleta("Av. Principal 123, Col. Centro");
        inmobiliaria.setCiudad("Ciudad de México");
        inmobiliaria.setEstado("CDMX");
        inmobiliaria.setCodigoPostal("06700");
        inmobiliaria.setPersonaContacto("María González");
        inmobiliaria.setEstatus("ACTIVE");
        inmobiliaria.setFechaRegistro(LocalDate.now());
        return inmobiliaria;
    }

    private InmobiliariaDTO createTestInmobiliariaDTO() {
        return new InmobiliariaDTO(
                null,
                "Nueva Inmobiliaria",
                "Nueva Inmobiliaria S.A.",
                "NIN987654321",
                "555-987-6543",
                "contacto@nueva.com",
                "Calle Nueva 456",
                "Guadalajara",
                "Jalisco",
                "44100",
                "Juan Pérez",
                null,
                "ACTIVE"
        );
    }

    @Nested
    @DisplayName("Authentication and Authorization Tests")
    class AuthenticationAndAuthorizationTests {

        @Test
        @DisplayName("Should require authentication for protected endpoints")
        void shouldRequireAuthenticationForProtectedEndpoints() throws Exception {
            mockMvc.perform(get("/api/v1/inmobiliarias"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should allow authenticated users to read inmobiliarias")
        void shouldAllowAuthenticatedUsersToReadInmobiliarias() throws Exception {
            mockMvc.perform(get("/api/v1/inmobiliarias")
                            .header("Authorization", "Bearer " + userToken))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should require admin role for create operations")
        void shouldRequireAdminRoleForCreateOperations() throws Exception {
            InmobiliariaDTO newInmobiliaria = createTestInmobiliariaDTO();

            // User should not be able to create
            mockMvc.perform(post("/api/v1/inmobiliarias")
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newInmobiliaria)))
                    .andDo(print())
                    .andExpect(status().isForbidden());

            // Admin should be able to create
            mockMvc.perform(post("/api/v1/inmobiliarias")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newInmobiliaria)))
                    .andDo(print())
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Should require admin role for update operations")
        void shouldRequireAdminRoleForUpdateOperations() throws Exception {
            InmobiliariaDTO updateData = new InmobiliariaDTO(
                    testInmobiliaria.getIdInmobiliaria(),
                    "Updated Name",
                    testInmobiliaria.getRazonSocial(),
                    testInmobiliaria.getRfcNit(),
                    testInmobiliaria.getTelefonoPrincipal(),
                    testInmobiliaria.getEmailContacto(),
                    testInmobiliaria.getDireccionCompleta(),
                    testInmobiliaria.getCiudad(),
                    testInmobiliaria.getEstado(),
                    testInmobiliaria.getCodigoPostal(),
                    testInmobiliaria.getPersonaContacto(),
                    testInmobiliaria.getFechaRegistro(),
                    testInmobiliaria.getEstatus()
            );

            // User should not be able to update
            mockMvc.perform(put("/api/v1/inmobiliarias/{id}", testInmobiliaria.getIdInmobiliaria())
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateData)))
                    .andDo(print())
                    .andExpect(status().isForbidden());

            // Admin should be able to update
            mockMvc.perform(put("/api/v1/inmobiliarias/{id}", testInmobiliaria.getIdInmobiliaria())
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateData)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should require admin role for delete operations")
        void shouldRequireAdminRoleForDeleteOperations() throws Exception {
            // User should not be able to delete
            mockMvc.perform(delete("/api/v1/inmobiliarias/{id}", testInmobiliaria.getIdInmobiliaria())
                            .header("Authorization", "Bearer " + userToken))
                    .andDo(print())
                    .andExpect(status().isForbidden());

            // Admin should be able to delete
            mockMvc.perform(delete("/api/v1/inmobiliarias/{id}", testInmobiliaria.getIdInmobiliaria())
                            .header("Authorization", "Bearer " + adminToken))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("CRUD Operations Integration")
    class CrudOperationsIntegration {

        @Test
        @DisplayName("Should perform complete CRUD lifecycle")
        void shouldPerformCompleteCrudLifecycle() throws Exception {
            InmobiliariaDTO newInmobiliaria = createTestInmobiliariaDTO();

            // 1. Create
            String createResponse = mockMvc.perform(post("/api/v1/inmobiliarias")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newInmobiliaria)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.nombreComercial", is("Nueva Inmobiliaria")))
                    .andExpect(jsonPath("$.rfcNit", is("NIN987654321")))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            InmobiliariaDTO created = objectMapper.readValue(createResponse, InmobiliariaDTO.class);
            Long createdId = created.idInmobiliaria();

            // 2. Read
            mockMvc.perform(get("/api/v1/inmobiliarias/{id}", createdId)
                            .header("Authorization", "Bearer " + userToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idInmobiliaria", is(createdId.intValue())))
                    .andExpect(jsonPath("$.nombreComercial", is("Nueva Inmobiliaria")));

            // 3. Update
            InmobiliariaDTO updateData = new InmobiliariaDTO(
                    createdId, "Updated Nueva Inmobiliaria", created.razonSocial(), created.rfcNit(),
                    created.telefonoPrincipal(), created.emailContacto(), created.direccionCompleta(),
                    created.ciudad(), created.estado(), created.codigoPostal(), created.personaContacto(),
                    created.fechaRegistro(), created.estatus()
            );

            mockMvc.perform(put("/api/v1/inmobiliarias/{id}", createdId)
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateData)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombreComercial", is("Updated Nueva Inmobiliaria")));

            // 4. Delete
            mockMvc.perform(delete("/api/v1/inmobiliarias/{id}", createdId)
                            .header("Authorization", "Bearer " + adminToken))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            // 5. Verify deletion
            mockMvc.perform(get("/api/v1/inmobiliarias/{id}", createdId)
                            .header("Authorization", "Bearer " + userToken))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should handle business rule violations")
        void shouldHandleBusinessRuleViolations() throws Exception {
            InmobiliariaDTO duplicateRfc = new InmobiliariaDTO(
                    null, "Duplicate RFC", "Duplicate Legal", "ILP123456789", // Same RFC as test data
                    "999-999-9999", "duplicate@test.com", "Duplicate Address",
                    "Duplicate City", "Duplicate State", "99999", "Duplicate Contact", null, "ACTIVE"
            );

            // Should reject duplicate RFC/NIT
            mockMvc.perform(post("/api/v1/inmobiliarias")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(duplicateRfc)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Search and Filter Integration")
    class SearchAndFilterIntegration {

        @BeforeEach
        void setUpSearchData() {
            // Create additional test data for search
            Inmobiliaria inmobiliaria2 = new Inmobiliaria();
            inmobiliaria2.setNombreComercial("Inmobiliaria del Valle");
            inmobiliaria2.setRazonSocial("Inmobiliaria del Valle S.A.");
            inmobiliaria2.setRfcNit("IDV987654321");
            inmobiliaria2.setTelefonoPrincipal("555-987-6543");
            inmobiliaria2.setEmailContacto("info@delvalle.com");
            inmobiliaria2.setDireccionCompleta("Calle Secundaria 456");
            inmobiliaria2.setCiudad("Guadalajara");
            inmobiliaria2.setEstado("Jalisco");
            inmobiliaria2.setCodigoPostal("44100");
            inmobiliaria2.setPersonaContacto("Juan Pérez");
            inmobiliaria2.setEstatus("ACTIVE");
            inmobiliaria2.setFechaRegistro(LocalDate.now());
            inmobiliariaRepository.save(inmobiliaria2);

            Inmobiliaria inmobiliaria3 = new Inmobiliaria();
            inmobiliaria3.setNombreComercial("Inmobiliaria Norteña");
            inmobiliaria3.setRazonSocial("Inmobiliaria Norteña S.C.");
            inmobiliaria3.setRfcNit("INR555666777");
            inmobiliaria3.setTelefonoPrincipal("555-555-5555");
            inmobiliaria3.setEmailContacto("contacto@nortena.com");
            inmobiliaria3.setDireccionCompleta("Av. Norte 789");
            inmobiliaria3.setCiudad("Monterrey");
            inmobiliaria3.setEstado("Nuevo León");
            inmobiliaria3.setCodigoPostal("64000");
            inmobiliaria3.setPersonaContacto("Ana López");
            inmobiliaria3.setEstatus("INACTIVE");
            inmobiliaria3.setFechaRegistro(LocalDate.now());
            inmobiliariaRepository.save(inmobiliaria3);
        }

        @Test
        @DisplayName("Should search with filters")
        void shouldSearchWithFilters() throws Exception {
            mockMvc.perform(get("/api/v1/inmobiliarias/search")
                            .header("Authorization", "Bearer " + userToken)
                            .param("nombreComercial", "Valle")
                            .param("estado", "Jalisco")
                            .param("estatus", "ACTIVE"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].nombreComercial", is("Inmobiliaria del Valle")));
        }

        @Test
        @DisplayName("Should handle pagination in search")
        void shouldHandlePaginationInSearch() throws Exception {
            mockMvc.perform(get("/api/v1/inmobiliarias/search")
                            .header("Authorization", "Bearer " + userToken)
                            .param("page", "0")
                            .param("size", "2")
                            .param("sortBy", "nombreComercial")
                            .param("sortDir", "asc"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.totalElements", is(3)))
                    .andExpect(jsonPath("$.totalPages", is(2)));
        }

        @Test
        @DisplayName("Should find by status")
        void shouldFindByStatus() throws Exception {
            mockMvc.perform(get("/api/v1/inmobiliarias/status/ACTIVE")
                            .header("Authorization", "Bearer " + userToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)));

            mockMvc.perform(get("/api/v1/inmobiliarias/status/INACTIVE")
                            .header("Authorization", "Bearer " + userToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)));
        }

        @Test
        @DisplayName("Should get statistics")
        void shouldGetStatistics() throws Exception {
            mockMvc.perform(get("/api/v1/inmobiliarias/statistics")
                            .header("Authorization", "Bearer " + userToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.total", is(3)))
                    .andExpect(jsonPath("$.active", is(2)))
                    .andExpect(jsonPath("$.inactive", is(1)));
        }

        @Test
        @DisplayName("Should get distinct cities and states")
        void shouldGetDistinctCitiesAndStates() throws Exception {
            mockMvc.perform(get("/api/v1/inmobiliarias/cities")
                            .header("Authorization", "Bearer " + userToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)));

            mockMvc.perform(get("/api/v1/inmobiliarias/states")
                            .header("Authorization", "Bearer " + userToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)));
        }
    }

    @Nested
    @DisplayName("Data Validation Integration")
    class DataValidationIntegration {

        @Test
        @DisplayName("Should validate required fields")
        void shouldValidateRequiredFields() throws Exception {
            InmobiliariaDTO invalidData = new InmobiliariaDTO(
                    null, "", "", "", "", "", "", "", "", "", "", null, "" // Empty required fields
            );

            mockMvc.perform(post("/api/v1/inmobiliarias")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidData)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should validate email format")
        void shouldValidateEmailFormat() throws Exception {
            InmobiliariaDTO invalidEmail = createTestInmobiliariaDTO();
            invalidEmail = new InmobiliariaDTO(
                    invalidEmail.idInmobiliaria(), invalidEmail.nombreComercial(), invalidEmail.razonSocial(),
                    "VALID123456789", invalidEmail.telefonoPrincipal(), "invalid-email", // Invalid email
                    invalidEmail.direccionCompleta(), invalidEmail.ciudad(), invalidEmail.estado(),
                    invalidEmail.codigoPostal(), invalidEmail.personaContacto(),
                    invalidEmail.fechaRegistro(), invalidEmail.estatus()
            );

            mockMvc.perform(post("/api/v1/inmobiliarias")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidEmail)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Status Management Integration")
    class StatusManagementIntegration {

        @Test
        @DisplayName("Should change status successfully")
        void shouldChangeStatusSuccessfully() throws Exception {
            mockMvc.perform(patch("/api/v1/inmobiliarias/{id}/status", testInmobiliaria.getIdInmobiliaria())
                            .header("Authorization", "Bearer " + adminToken)
                            .param("status", "INACTIVE"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.estatus", is("INACTIVE")));

            // Verify the change in database
            Inmobiliaria updated = inmobiliariaRepository.findById(testInmobiliaria.getIdInmobiliaria()).orElse(null);
            assertThat(updated).isNotNull();
            assertThat(updated.getEstatus()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("Should accept any status value (no validation)")
        void shouldAcceptAnyStatusValue() throws Exception {
            mockMvc.perform(patch("/api/v1/inmobiliarias/{id}/status", testInmobiliaria.getIdInmobiliaria())
                            .header("Authorization", "Bearer " + adminToken)
                            .param("status", "INVALID_STATUS"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.estatus", is("INVALID_STATUS")));
        }
    }

    @Nested
    @DisplayName("Error Handling Integration")
    class ErrorHandlingIntegration {

        @Test
        @DisplayName("Should handle not found errors")
        void shouldHandleNotFoundErrors() throws Exception {
            mockMvc.perform(get("/api/v1/inmobiliarias/999999")
                            .header("Authorization", "Bearer " + userToken))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should handle RFC/NIT not found")
        void shouldHandleRfcNitNotFound() throws Exception {
            mockMvc.perform(get("/api/v1/inmobiliarias/rfc/NOTFOUND")
                            .header("Authorization", "Bearer " + userToken))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should handle malformed JSON")
        void shouldHandleMalformedJson() throws Exception {
            mockMvc.perform(post("/api/v1/inmobiliarias")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ invalid json }"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}