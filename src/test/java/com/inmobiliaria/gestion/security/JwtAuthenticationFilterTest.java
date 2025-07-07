package com.inmobiliaria.gestion.security;

import com.inmobiliaria.gestion.auth.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldSetAuthentication() throws ServletException, IOException {
        // Given
        String token = "valid-jwt-token";
        String username = "testuser";

        when(request.getRequestURI()).thenReturn("/api/v1/inmobiliarias");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateJwtToken(token)).thenReturn(true);
        when(jwtUtil.getUserNameFromJwtToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(securityContext).setAuthentication(any(Authentication.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Given
        String token = "invalid-jwt-token";

        when(request.getRequestURI()).thenReturn("/api/v1/inmobiliarias");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateJwtToken(token)).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(securityContext, never()).setAuthentication(any(Authentication.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNoToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/v1/inmobiliarias");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtil, never()).validateJwtToken(anyString());
        verify(securityContext, never()).setAuthentication(any(Authentication.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithMalformedAuthorizationHeader_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/v1/inmobiliarias");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtil, never()).validateJwtToken(anyString());
        verify(securityContext, never()).setAuthentication(any(Authentication.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithException_ShouldContinueFilterChain() throws ServletException, IOException {
        // Given
        String token = "valid-jwt-token";
        String username = "testuser";

        when(request.getRequestURI()).thenReturn("/api/v1/inmobiliarias");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateJwtToken(token)).thenReturn(true);
        when(jwtUtil.getUserNameFromJwtToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username))
                .thenThrow(new RuntimeException("User service error"));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(securityContext, never()).setAuthentication(any(Authentication.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotFilter_WithAuthEndpoint_ShouldReturnTrue() {
        // Given
        when(request.getRequestURI()).thenReturn("/api/v1/auth/signin");

        // When
        boolean shouldNotFilter = jwtAuthenticationFilter.shouldNotFilter(request);

        // Then
        assertTrue(shouldNotFilter);
    }

    @Test
    void shouldNotFilter_WithSwaggerEndpoint_ShouldReturnTrue() {
        // Given
        when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");

        // When
        boolean shouldNotFilter = jwtAuthenticationFilter.shouldNotFilter(request);

        // Then
        assertTrue(shouldNotFilter);
    }

    @Test
    void shouldNotFilter_WithStaticResource_ShouldReturnTrue() {
        // Given
        when(request.getRequestURI()).thenReturn("/css/style.css");

        // When
        boolean shouldNotFilter = jwtAuthenticationFilter.shouldNotFilter(request);

        // Then
        assertTrue(shouldNotFilter);
    }

    @Test
    void shouldNotFilter_WithRootPath_ShouldReturnTrue() {
        // Given
        when(request.getRequestURI()).thenReturn("/");

        // When
        boolean shouldNotFilter = jwtAuthenticationFilter.shouldNotFilter(request);

        // Then
        assertTrue(shouldNotFilter);
    }

    @Test
    void shouldNotFilter_WithHtmlFile_ShouldReturnTrue() {
        // Given
        when(request.getRequestURI()).thenReturn("/login.html");

        // When
        boolean shouldNotFilter = jwtAuthenticationFilter.shouldNotFilter(request);

        // Then
        assertTrue(shouldNotFilter);
    }

    @Test
    void shouldNotFilter_WithErrorEndpoint_ShouldReturnTrue() {
        // Given
        when(request.getRequestURI()).thenReturn("/error");

        // When
        boolean shouldNotFilter = jwtAuthenticationFilter.shouldNotFilter(request);

        // Then
        assertTrue(shouldNotFilter);
    }

    @Test
    void shouldNotFilter_WithWellKnownEndpoint_ShouldReturnTrue() {
        // Given
        when(request.getRequestURI()).thenReturn("/.well-known/appspecific/com.chrome.devtools.json");

        // When
        boolean shouldNotFilter = jwtAuthenticationFilter.shouldNotFilter(request);

        // Then
        assertTrue(shouldNotFilter);
    }

    @Test
    void shouldNotFilter_WithProtectedEndpoint_ShouldReturnFalse() {
        // Given
        when(request.getRequestURI()).thenReturn("/api/v1/inmobiliarias");

        // When
        boolean shouldNotFilter = jwtAuthenticationFilter.shouldNotFilter(request);

        // Then
        assertFalse(shouldNotFilter);
    }

    @Test
    void parseJwt_WithValidBearerToken_ShouldReturnToken() throws ServletException, IOException {
        // Given
        String token = "valid-jwt-token";
        when(request.getRequestURI()).thenReturn("/api/v1/inmobiliarias");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateJwtToken(token)).thenReturn(true);
        when(jwtUtil.getUserNameFromJwtToken(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtil).validateJwtToken(token);
    }

    @Test
    void parseJwt_WithTokenWithoutBearerPrefix_ShouldNotProcessToken() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/v1/inmobiliarias");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("just-token-without-bearer");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtil, never()).validateJwtToken(anyString());
        verify(filterChain).doFilter(request, response);
    }
}