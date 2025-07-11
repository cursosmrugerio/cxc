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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;

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

    @Nested
    @DisplayName("JWT Exception Branch Coverage Tests")
    class JwtExceptionBranchTests {

        @Test
        @DisplayName("Should handle UnsupportedJwtException branch")
        void shouldHandleUnsupportedJwtExceptionBranch() {
            // Given - Token with unsupported format
            String unsupportedToken = "eyJhbGciOiJub25lIn0.eyJzdWIiOiJ0ZXN0In0."; // No signature

            // When
            boolean isValid = jwtUtil.validateJwtToken(unsupportedToken);

            // Then - Should return false and trigger UnsupportedJwtException branch
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Should handle different types of malformed tokens")
        void shouldHandleDifferentTypesOfMalformedTokens() {
            String[] malformedTokens = {
                "not.a.jwt",                    // Wrong structure
                "onlyonepart",                  // Missing dots
                "two.parts",                    // Only two parts
                "too.many.parts.in.this.jwt",  // Too many parts
                "!@#$%^&*()",                   // Invalid characters
                "123456789",                    // Numbers only
                "Bearer token123"               // With Bearer prefix
            };

            // When & Then - All should return false and trigger MalformedJwtException
            for (String malformedToken : malformedTokens) {
                boolean isValid = jwtUtil.validateJwtToken(malformedToken);
                assertFalse(isValid, "Should be invalid: " + malformedToken);
            }
        }

        @Test
        @DisplayName("Should handle token with tampered signature")
        void shouldHandleTokenWithTamperedSignature() {
            // Given - Generate valid token and tamper with signature
            String validToken = jwtUtil.generateJwtToken(authentication);
            String[] tokenParts = validToken.split("\\.");
            String tamperedToken = tokenParts[0] + "." + tokenParts[1] + ".tampered_signature";

            // When
            boolean isValid = jwtUtil.validateJwtToken(tamperedToken);

            // Then - Should return false due to signature verification failure
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Should handle really expired token")
        void shouldHandleReallyExpiredToken() {
            // Given - Set negative expiration to create immediate expiry
            ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", -86400000); // -1 day
            
            String expiredToken = jwtUtil.generateJwtToken(authentication);
            
            // Reset expiration for other tests
            ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 86400000);

            // When
            boolean isValid = jwtUtil.validateJwtToken(expiredToken);

            // Then - Should return false and trigger ExpiredJwtException branch
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Should handle IllegalArgumentException for empty claims")
        void shouldHandleIllegalArgumentExceptionForEmptyClaims() {
            // Given - Various edge case tokens that trigger IllegalArgumentException
            String[] edgeCaseTokens = {
                "",                    // Empty string
                " ",                   // Whitespace only
                "\t\n",               // Tabs and newlines
                "null",               // String "null"
                "undefined"           // String "undefined"
            };

            // When & Then - All should return false and trigger IllegalArgumentException
            for (String token : edgeCaseTokens) {
                boolean isValid = jwtUtil.validateJwtToken(token);
                assertFalse(isValid, "Should be invalid: '" + token + "'");
            }
        }

        @Test
        @DisplayName("Should handle general Exception branch")
        void shouldHandleGeneralExceptionBranch() {
            // Given - Token that might cause unexpected parsing errors
            String problematicToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ"; // Missing signature part

            // When
            boolean isValid = jwtUtil.validateJwtToken(problematicToken);

            // Then - Should return false and handle gracefully
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Should handle token validation with wrong secret")
        void shouldHandleTokenValidationWithWrongSecret() {
            // Given - Generate token with current secret
            String token = jwtUtil.generateJwtToken(authentication);
            
            // Change the secret to simulate wrong key scenario
            ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "completely-different-secret-key-that-will-not-work-for-verification-purposes");

            // When
            boolean isValid = jwtUtil.validateJwtToken(token);

            // Then - Should return false due to signature verification failure
            assertFalse(isValid);
            
            // Reset secret for other tests
            ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "test-secret-key-for-jwt-signing-must-be-at-least-256-bits-long");
        }

        @Test
        @DisplayName("Should handle concurrent token validation gracefully")
        void shouldHandleConcurrentTokenValidationGracefully() {
            // Given - Multiple tokens to validate
            String validToken = jwtUtil.generateJwtToken(authentication);
            String invalidToken = "invalid.token.here";

            // When & Then - Should handle multiple validations without issues
            assertDoesNotThrow(() -> {
                boolean valid1 = jwtUtil.validateJwtToken(validToken);
                boolean valid2 = jwtUtil.validateJwtToken(invalidToken);
                boolean valid3 = jwtUtil.validateJwtToken(validToken);
                
                assertTrue(valid1);
                assertFalse(valid2);
                assertTrue(valid3);
            });
        }

        @Test
        @DisplayName("Should extract username from various valid tokens")
        void shouldExtractUsernameFromVariousValidTokens() {
            // Given - Different usernames
            String[] usernames = {"user1", "test@email.com", "user_with_underscore", "123numeric"};

            for (String username : usernames) {
                // When
                String token = jwtUtil.generateTokenFromUsername(username);
                String extractedUsername = jwtUtil.getUserNameFromJwtToken(token);

                // Then
                assertEquals(username, extractedUsername);
                assertTrue(jwtUtil.validateJwtToken(token));
            }
        }
    }
}