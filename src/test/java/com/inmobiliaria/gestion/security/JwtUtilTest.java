package com.inmobiliaria.gestion.security;

import com.inmobiliaria.gestion.auth.model.Role;
import com.inmobiliaria.gestion.auth.model.User;
import com.inmobiliaria.gestion.auth.service.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private Authentication authentication;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Set test values using reflection
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "test-secret-key-for-jwt-signing-must-be-at-least-256-bits-long");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 86400);

        // Create test user
        Role userRole = Role.builder()
                .id(1)
                .name(Role.ERole.ROLE_USER)
                .build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .roles(Set.of(userRole))
                .build();

        UserDetailsImpl userDetails = UserDetailsImpl.build(testUser);
        authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    @Test
    void generateJwtToken_ShouldCreateValidToken() {
        // When
        String token = jwtUtil.generateJwtToken(authentication);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts separated by dots
    }

    @Test
    void generateTokenFromUsername_ShouldCreateValidToken() {
        // When
        String token = jwtUtil.generateTokenFromUsername("testuser");

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void getUserNameFromJwtToken_ShouldReturnCorrectUsername() {
        // Given
        String token = jwtUtil.generateJwtToken(authentication);

        // When
        String username = jwtUtil.getUserNameFromJwtToken(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    void validateJwtToken_WithValidToken_ShouldReturnTrue() {
        // Given
        String token = jwtUtil.generateJwtToken(authentication);

        // When
        boolean isValid = jwtUtil.validateJwtToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateJwtToken_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        boolean isValid = jwtUtil.validateJwtToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_WithMalformedToken_ShouldReturnFalse() {
        // Given
        String malformedToken = "malformed-token";

        // When
        boolean isValid = jwtUtil.validateJwtToken(malformedToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_WithEmptyToken_ShouldReturnFalse() {
        // Given
        String emptyToken = "";

        // When
        boolean isValid = jwtUtil.validateJwtToken(emptyToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_WithNullToken_ShouldReturnFalse() {
        // When
        boolean isValid = jwtUtil.validateJwtToken(null);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_WithExpiredToken_ShouldReturnFalse() {
        // Given - Create a token with very short expiration
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", -1);
        String expiredToken = jwtUtil.generateJwtToken(authentication);

        // Reset to normal expiration
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 86400);

        // When
        boolean isValid = jwtUtil.validateJwtToken(expiredToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void generateJwtToken_WithDifferentUsers_ShouldCreateDifferentTokens() {
        // Given
        String token1 = jwtUtil.generateTokenFromUsername("user1");
        String token2 = jwtUtil.generateTokenFromUsername("user2");

        // Then
        assertNotEquals(token1, token2);
    }

    @Test
    void getUserNameFromJwtToken_WithTokenFromUsername_ShouldReturnCorrectUsername() {
        // Given
        String username = "testuser123";
        String token = jwtUtil.generateTokenFromUsername(username);

        // When
        String retrievedUsername = jwtUtil.getUserNameFromJwtToken(token);

        // Then
        assertEquals(username, retrievedUsername);
    }
}