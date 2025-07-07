package com.inmobiliaria.gestion.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmobiliaria.gestion.auth.dto.LoginRequest;
import com.inmobiliaria.gestion.auth.model.Role;
import com.inmobiliaria.gestion.auth.model.User;
import com.inmobiliaria.gestion.auth.repository.RoleRepository;
import com.inmobiliaria.gestion.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
class SecurityConfigIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
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
        when(roleRepository.findAll()).thenReturn(java.util.List.of());

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
        // Test protected API endpoints
        mockMvc.perform(get("/api/v1/inmobiliarias"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/inmobiliarias")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/propiedades"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoints_WithValidJWT_ShouldAllowAccess() throws Exception {
        // Given - Create a test user and JWT token
        Role userRole = Role.builder()
                .id(1)
                .name(Role.ERole.ROLE_USER)
                .build();

        User testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .enabled(true)
                .roles(Set.of(userRole))
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        String token = jwtUtil.generateTokenFromUsername("testuser");

        // When & Then - Access protected endpoint with valid token
        mockMvc.perform(get("/api/v1/inmobiliarias")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()); // Assuming the endpoint exists and returns 200
    }

    @Test
    void protectedEndpoints_WithInvalidJWT_ShouldReturnUnauthorized() throws Exception {
        // Test with invalid token
        mockMvc.perform(get("/api/v1/inmobiliarias")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());

        // Test with malformed authorization header
        mockMvc.perform(get("/api/v1/inmobiliarias")
                .header("Authorization", "Basic sometoken"))
                .andExpect(status().isUnauthorized());

        // Test with expired token (create a token with past expiration)
        // This would require mocking the JwtUtil to create an expired token
        mockMvc.perform(get("/api/v1/inmobiliarias")
                .header("Authorization", "Bearer expired.jwt.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticationFlow_LoginAndAccessProtectedResource() throws Exception {
        // Given - Set up user for login
        Role userRole = Role.builder()
                .id(1)
                .name(Role.ERole.ROLE_USER)
                .build();

        User testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .enabled(true)
                .roles(Set.of(userRole))
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

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
        mockMvc.perform(get("/api/v1/inmobiliarias")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
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
        // Test Swagger UI
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection()); // Redirects to swagger-ui/index.html

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertTrue(status != 401); // Should not require authentication
                });

        // Test API docs
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk()); // Should be accessible

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk()); // Should be accessible
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