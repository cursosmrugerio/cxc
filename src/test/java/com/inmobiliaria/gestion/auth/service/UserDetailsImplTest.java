package com.inmobiliaria.gestion.auth.service;

import com.inmobiliaria.gestion.auth.model.Role;
import com.inmobiliaria.gestion.auth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    private User testUser;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        Role userRole = Role.builder()
                .id(1)
                .name(Role.ERole.ROLE_USER)
                .build();

        Role adminRole = Role.builder()
                .id(2)
                .name(Role.ERole.ROLE_ADMIN)
                .build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .enabled(true)
                .roles(Set.of(userRole, adminRole))
                .build();

        userDetails = UserDetailsImpl.build(testUser);
    }

    @Test
    void build_ShouldCreateUserDetailsWithCorrectProperties() {
        // Then
        assertNotNull(userDetails);
        assertEquals(1L, userDetails.getId());
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("test@example.com", userDetails.getEmail());
        assertEquals("encodedPassword", userDetails.getPassword());
    }

    @Test
    void build_ShouldMapRolesToAuthorities() {
        // When
        var authorities = userDetails.getAuthorities();

        // Then
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void build_WithUserHavingNoRoles_ShouldCreateEmptyAuthorities() {
        // Given
        User userWithoutRoles = User.builder()
                .id(2L)
                .username("noroles")
                .email("noroles@example.com")
                .password("password")
                .roles(Set.of())
                .build();

        // When
        UserDetailsImpl userDetailsWithoutRoles = UserDetailsImpl.build(userWithoutRoles);

        // Then
        assertTrue(userDetailsWithoutRoles.getAuthorities().isEmpty());
    }

    @Test
    void isAccountNonExpired_ShouldReturnTrue() {
        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_ShouldReturnTrue() {
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_ShouldReturnTrue() {
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_ShouldReturnTrue() {
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void equals_WithSameId_ShouldReturnTrue() {
        // Given
        UserDetailsImpl anotherUserDetails = new UserDetailsImpl(
                1L, "different", "different@email.com", "different", Set.of());

        // When & Then
        assertEquals(userDetails, anotherUserDetails);
    }

    @Test
    void equals_WithDifferentId_ShouldReturnFalse() {
        // Given
        UserDetailsImpl anotherUserDetails = new UserDetailsImpl(
                2L, "testuser", "test@example.com", "encodedPassword", Set.of());

        // When & Then
        assertNotEquals(userDetails, anotherUserDetails);
    }

    @Test
    void equals_WithNull_ShouldReturnFalse() {
        assertNotEquals(userDetails, null);
    }

    @Test
    void equals_WithDifferentClass_ShouldReturnFalse() {
        assertNotEquals(userDetails, "not a UserDetailsImpl");
    }

    @Test
    void equals_WithSameObject_ShouldReturnTrue() {
        assertEquals(userDetails, userDetails);
    }

    @Test
    void hashCode_WithSameId_ShouldBeEqual() {
        // Given
        UserDetailsImpl anotherUserDetails = new UserDetailsImpl(
                1L, "different", "different@email.com", "different", Set.of());

        // When & Then
        assertEquals(userDetails.hashCode(), anotherUserDetails.hashCode());
    }

    @Test
    void getAuthorities_ShouldReturnImmutableCollection() {
        // When
        var authorities = userDetails.getAuthorities();

        // Then
        assertNotNull(authorities);
        assertThrows(UnsupportedOperationException.class, () -> {
            if (authorities instanceof java.util.List) {
                ((java.util.List<GrantedAuthority>) authorities).add(null);
            }
        });
    }
}