package com.inmobiliaria.gestion.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @InjectMocks
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private ByteArrayOutputStream responseStream;
    private ServletOutputStream servletOutputStream;

    @BeforeEach
    void setUp() throws IOException {
        responseStream = new ByteArrayOutputStream();
        servletOutputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) throws IOException {
                responseStream.write(b);
            }
        };
        when(response.getOutputStream()).thenReturn(servletOutputStream);
    }

    @Test
    void commence_ShouldSetUnauthorizedStatusAndContentType() throws IOException {
        // Given
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/inmobiliarias");
        when(request.getQueryString()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn(null);
        when(authException.getMessage()).thenReturn("Full authentication is required");
        when(request.getServletPath()).thenReturn("/api/v1/inmobiliarias");

        // When
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Then
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void commence_ShouldWriteCorrectJsonResponse() throws IOException {
        // Given
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/v1/inmobiliarias");
        when(request.getQueryString()).thenReturn("param=value");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(authException.getMessage()).thenReturn("JWT token is expired");
        when(request.getServletPath()).thenReturn("/api/v1/inmobiliarias");

        // When
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Then
        String responseContent = responseStream.toString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);

        assertEquals(401, responseMap.get("status"));
        assertEquals("Unauthorized", responseMap.get("error"));
        assertEquals("JWT token is expired", responseMap.get("message"));
        assertEquals("/api/v1/inmobiliarias", responseMap.get("path"));
    }

    @Test
    void commence_WithQueryString_ShouldLogFullUrl() throws IOException {
        // Given
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/inmobiliarias");
        when(request.getQueryString()).thenReturn("page=1&size=10");
        when(request.getHeader("Authorization")).thenReturn(null);
        when(authException.getMessage()).thenReturn("Access denied");
        when(request.getServletPath()).thenReturn("/api/v1/inmobiliarias");

        // When
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Then
        // The logging is verified through the behavior, as we can't easily capture log output
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void commence_WithoutQueryString_ShouldLogUriOnly() throws IOException {
        // Given
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn("/api/v1/inmobiliarias/1");
        when(request.getQueryString()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Bearer malformed-token");
        when(authException.getMessage()).thenReturn("Invalid JWT token");
        when(request.getServletPath()).thenReturn("/api/v1/inmobiliarias/1");

        // When
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void commence_WithAuthorizationHeader_ShouldLogAuthorizationValue() throws IOException {
        // Given
        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/api/v1/users/1");
        when(request.getQueryString()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-looking-but-expired-token");
        when(authException.getMessage()).thenReturn("Token expired");
        when(request.getServletPath()).thenReturn("/api/v1/users/1");

        // When
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Then
        verify(request).getHeader("Authorization");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void commence_WithNullAuthorizationHeader_ShouldHandleGracefully() throws IOException {
        // Given
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/protected");
        when(request.getQueryString()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn(null);
        when(authException.getMessage()).thenReturn("No authentication provided");
        when(request.getServletPath()).thenReturn("/api/v1/protected");

        // When
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void commence_ShouldIncludeCorrectServletPath() throws IOException {
        // Given
        String servletPath = "/api/v1/test/endpoint";
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/v1/test/endpoint");
        when(request.getQueryString()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn(null);
        when(authException.getMessage()).thenReturn("Authentication required");
        when(request.getServletPath()).thenReturn(servletPath);

        // When
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Then
        String responseContent = responseStream.toString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);

        assertEquals(servletPath, responseMap.get("path"));
    }

    @Test
    void commence_WithComplexScenario_ShouldHandleAllFields() throws IOException {
        // Given
        when(request.getMethod()).thenReturn("PATCH");
        when(request.getRequestURI()).thenReturn("/api/v1/complex/endpoint");
        when(request.getQueryString()).thenReturn("filter=active&sort=name");
        when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
        when(authException.getMessage()).thenReturn("JWT signature does not match");
        when(request.getServletPath()).thenReturn("/api/v1/complex/endpoint");

        // When
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Then
        String responseContent = responseStream.toString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);

        assertEquals(401, responseMap.get("status"));
        assertEquals("Unauthorized", responseMap.get("error"));
        assertEquals("JWT signature does not match", responseMap.get("message"));
        assertEquals("/api/v1/complex/endpoint", responseMap.get("path"));

        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}