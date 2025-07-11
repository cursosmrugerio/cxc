package com.inmobiliaria.gestion.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmobiliaria.gestion.auth.dto.LoginRequest;
import com.inmobiliaria.gestion.auth.model.Role;
import com.inmobiliaria.gestion.auth.model.User;
import com.inmobiliaria.gestion.auth.repository.RoleRepository;
import com.inmobiliaria.gestion.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class SecurityConfigIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Clean repositories
        userRepository.deleteAll();
        roleRepository.deleteAll();
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

    @Test
    void publicEndpoints_ShouldBeAccessibleWithoutAuthentication() throws Exception {
        // Test root path
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        // Test login page
        mockMvc.perform(get("/login.html"))
                .andExpect(status().isOk());

        // Test register page
        mockMvc.perform(get("/register.html"))
                .andExpect(status().isOk());

        // Test CSS resources
        mockMvc.perform(get("/css/style.css"))
                .andExpect(status().isOk()); // File exists in static resources

        // Test JS resources
        mockMvc.perform(get("/js/auth.js"))
                .andExpect(status().isOk());

        // Test error endpoint (Spring Boot provides a default error handler)
        mockMvc.perform(get("/error"))
                .andExpect(status().is5xxServerError()); // Error endpoint returns 500 without proper error context

        // Test well-known endpoints
        mockMvc.perform(get("/.well-known/test"))
                .andExpect(status().isNotFound()); // Should not require auth
    }

    @Test
    void authEndpoints_ShouldBeAccessibleWithoutAuthentication() throws Exception {
        // Test roles endpoint
        mockMvc.perform(get("/api/v1/auth/roles"))
                .andExpect(status().isOk());

        // Test signup endpoint (will fail validation but should not require auth)
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest()); // Bad request due to validation, not auth

        // Test signin endpoint (will fail but should not require auth)
        mockMvc.perform(post("/api/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest()); // Bad request due to validation, not auth
    }

    @Test
    void protectedEndpoints_ShouldRequireAuthentication() throws Exception {
        // Test any protected API endpoints (since we removed business logic endpoints,
        // we'll test with a generic protected path that would require authentication)
        mockMvc.perform(get("/api/v1/protected"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/protected")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoints_WithValidJWT_ShouldAllowAccess() throws Exception {
        // Given - Create a test user and JWT token
        Role userRole = createRole("ROLE_USER");
        User testUser = createUser("testuser", "test@example.com", "password", Set.of(userRole));

        String token = jwtUtil.generateTokenFromUsername("testuser");

        // When & Then - Access protected endpoint with valid token
        // Since we removed business endpoints, test with a protected path that would return 404 but not 401
        mockMvc.perform(get("/api/v1/protected")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound()); // 404 means it passed auth but endpoint doesn't exist
    }

    @Test
    void protectedEndpoints_WithInvalidJWT_ShouldReturnUnauthorized() throws Exception {
        // Test with invalid token
        mockMvc.perform(get("/api/v1/protected")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());

        // Test with malformed authorization header
        mockMvc.perform(get("/api/v1/protected")
                .header("Authorization", "Basic sometoken"))
                .andExpect(status().isUnauthorized());

        // Test with expired token (create a token with past expiration)
        // This would require mocking the JwtUtil to create an expired token
        mockMvc.perform(get("/api/v1/protected")
                .header("Authorization", "Bearer expired.jwt.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticationFlow_LoginAndAccessProtectedResource() throws Exception {
        // Given - Set up user for login
        Role userRole = createRole("ROLE_USER");
        User testUser = createUser("testuser", "test@example.com", "password", Set.of(userRole));

        LoginRequest loginRequest = new LoginRequest("testuser", "password");

        // When - Login to get JWT token
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        // Extract token from response
        String responseContent = loginResult.getResponse().getContentAsString();
        var responseMap = objectMapper.readValue(responseContent, java.util.Map.class);
        String token = (String) responseMap.get("token");

        // Then - Use token to access protected resource
        mockMvc.perform(get("/api/v1/protected")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound()); // 404 means auth passed but endpoint doesn't exist
    }

    @Test
    void corsConfiguration_ShouldAllowCrossOriginRequests() throws Exception {
        // Test CORS preflight request
        mockMvc.perform(options("/api/v1/auth/signin")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @Test
    void swaggerEndpoints_ShouldBeAccessibleWithoutAuthentication() throws Exception {
        // Test Swagger UI (may not be available in test environment)
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertTrue(status != 401); // Should not require authentication
                });

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertTrue(status != 401); // Should not require authentication
                });

        // Test API docs (may not be available in test environment)
        mockMvc.perform(get("/api-docs"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertTrue(status != 401); // Should not require authentication
                });

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertTrue(status != 401); // Should not require authentication
                });
    }

    @Test
    void jwtFilter_ShouldNotProcessExcludedPaths() throws Exception {
        // These requests should not be processed by JWT filter
        mockMvc.perform(get("/api/v1/auth/roles"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/login.html"))
                .andExpect(status().isOk());

        // No JWT processing should occur for these endpoints
    }
}