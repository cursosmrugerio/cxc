package com.inmobiliaria.gestion.auth.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Role Entity Tests")
class RoleEntityTest {

    @Nested
    @DisplayName("Constructor and Builder Tests")
    class ConstructorAndBuilderTests {

        @Test
        @DisplayName("Should create role with no args constructor")
        void shouldCreateRoleWithNoArgsConstructor() {
            Role role = new Role();
            assertNotNull(role);
            assertNull(role.getId());
            assertNull(role.getName());
        }

        @Test
        @DisplayName("Should create role with builder")
        void shouldCreateRoleWithBuilder() {
            Role role = Role.builder()
                    .id(1)
                    .name(Role.ERole.ROLE_USER)
                    .build();
            
            assertEquals(1, role.getId());
            assertEquals(Role.ERole.ROLE_USER, role.getName());
        }

        @Test
        @DisplayName("Should create role with all args constructor")
        void shouldCreateRoleWithAllArgsConstructor() {
            Role role = new Role(1, Role.ERole.ROLE_ADMIN);
            
            assertEquals(1, role.getId());
            assertEquals(Role.ERole.ROLE_ADMIN, role.getName());
        }
    }

    @Nested
    @DisplayName("ERole Enum Tests")
    class ERoleEnumTests {

        @Test
        @DisplayName("Should have ROLE_USER enum value")
        void shouldHaveRoleUserEnumValue() {
            Role.ERole role = Role.ERole.ROLE_USER;
            assertEquals("ROLE_USER", role.name());
        }

        @Test
        @DisplayName("Should have ROLE_MODERATOR enum value")
        void shouldHaveRoleModeratorEnumValue() {
            Role.ERole role = Role.ERole.ROLE_MODERATOR;
            assertEquals("ROLE_MODERATOR", role.name());
        }

        @Test
        @DisplayName("Should have ROLE_ADMIN enum value")
        void shouldHaveRoleAdminEnumValue() {
            Role.ERole role = Role.ERole.ROLE_ADMIN;
            assertEquals("ROLE_ADMIN", role.name());
        }

        @Test
        @DisplayName("Should have exactly 3 enum values")
        void shouldHaveExactly3EnumValues() {
            Role.ERole[] values = Role.ERole.values();
            assertEquals(3, values.length);
        }

        @Test
        @DisplayName("Should parse enum from string")
        void shouldParseEnumFromString() {
            Role.ERole userRole = Role.ERole.valueOf("ROLE_USER");
            Role.ERole moderatorRole = Role.ERole.valueOf("ROLE_MODERATOR");
            Role.ERole adminRole = Role.ERole.valueOf("ROLE_ADMIN");
            
            assertEquals(Role.ERole.ROLE_USER, userRole);
            assertEquals(Role.ERole.ROLE_MODERATOR, moderatorRole);
            assertEquals(Role.ERole.ROLE_ADMIN, adminRole);
        }

        @Test
        @DisplayName("Should throw exception for invalid enum value")
        void shouldThrowExceptionForInvalidEnumValue() {
            assertThrows(IllegalArgumentException.class, () -> {
                Role.ERole.valueOf("INVALID_ROLE");
            });
        }
    }

    @Nested
    @DisplayName("Equality and HashCode Tests")
    class EqualityAndHashCodeTests {

        @Test
        @DisplayName("Should be equal when same object")
        void shouldBeEqualWhenSameObject() {
            Role role = Role.builder()
                    .id(1)
                    .name(Role.ERole.ROLE_USER)
                    .build();
            
            assertEquals(role, role);
            assertEquals(role.hashCode(), role.hashCode());
        }

        @Test
        @DisplayName("Should be equal when same data")
        void shouldBeEqualWhenSameData() {
            Role role1 = Role.builder()
                    .id(1)
                    .name(Role.ERole.ROLE_USER)
                    .build();
            
            Role role2 = Role.builder()
                    .id(1)
                    .name(Role.ERole.ROLE_USER)
                    .build();
            
            assertEquals(role1, role2);
            assertEquals(role1.hashCode(), role2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different id")
        void shouldNotBeEqualWhenDifferentId() {
            Role role1 = Role.builder()
                    .id(1)
                    .name(Role.ERole.ROLE_USER)
                    .build();
            
            Role role2 = Role.builder()
                    .id(2)
                    .name(Role.ERole.ROLE_USER)
                    .build();
            
            assertNotEquals(role1, role2);
        }

        @Test
        @DisplayName("Should not be equal when different name")
        void shouldNotBeEqualWhenDifferentName() {
            Role role1 = Role.builder()
                    .id(1)
                    .name(Role.ERole.ROLE_USER)
                    .build();
            
            Role role2 = Role.builder()
                    .id(1)
                    .name(Role.ERole.ROLE_ADMIN)
                    .build();
            
            assertNotEquals(role1, role2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            Role role = Role.builder()
                    .id(1)
                    .name(Role.ERole.ROLE_USER)
                    .build();
            
            assertNotEquals(null, role);
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            Role role = Role.builder()
                    .id(1)
                    .name(Role.ERole.ROLE_USER)
                    .build();
            
            assertNotEquals("string", role);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should contain all fields in toString")
        void shouldContainAllFieldsInToString() {
            Role role = Role.builder()
                    .id(1)
                    .name(Role.ERole.ROLE_USER)
                    .build();
            
            String toString = role.toString();
            
            assertTrue(toString.contains("Role"));
            assertTrue(toString.contains("id=1"));
            assertTrue(toString.contains("name=ROLE_USER"));
        }

        @Test
        @DisplayName("Should handle null fields in toString")
        void shouldHandleNullFieldsInToString() {
            Role role = new Role();
            
            String toString = role.toString();
            
            assertTrue(toString.contains("Role"));
            assertTrue(toString.contains("id=null"));
            assertTrue(toString.contains("name=null"));
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterAndSetterTests {

        @Test
        @DisplayName("Should set and get id correctly")
        void shouldSetAndGetIdCorrectly() {
            Role role = new Role();
            role.setId(1);
            
            assertEquals(1, role.getId());
        }

        @Test
        @DisplayName("Should set and get name correctly")
        void shouldSetAndGetNameCorrectly() {
            Role role = new Role();
            role.setName(Role.ERole.ROLE_ADMIN);
            
            assertEquals(Role.ERole.ROLE_ADMIN, role.getName());
        }

        @Test
        @DisplayName("Should handle null id")
        void shouldHandleNullId() {
            Role role = new Role();
            role.setId(null);
            
            assertNull(role.getId());
        }

        @Test
        @DisplayName("Should handle null name")
        void shouldHandleNullName() {
            Role role = new Role();
            role.setName(null);
            
            assertNull(role.getName());
        }
    }

    @Nested
    @DisplayName("Role Creation Scenarios Tests")
    class RoleCreationScenariosTests {

        @Test
        @DisplayName("Should create user role")
        void shouldCreateUserRole() {
            Role userRole = Role.builder()
                    .id(1)
                    .name(Role.ERole.ROLE_USER)
                    .build();
            
            assertEquals(1, userRole.getId());
            assertEquals(Role.ERole.ROLE_USER, userRole.getName());
            assertEquals("ROLE_USER", userRole.getName().name());
        }

        @Test
        @DisplayName("Should create moderator role")
        void shouldCreateModeratorRole() {
            Role moderatorRole = Role.builder()
                    .id(2)
                    .name(Role.ERole.ROLE_MODERATOR)
                    .build();
            
            assertEquals(2, moderatorRole.getId());
            assertEquals(Role.ERole.ROLE_MODERATOR, moderatorRole.getName());
            assertEquals("ROLE_MODERATOR", moderatorRole.getName().name());
        }

        @Test
        @DisplayName("Should create admin role")
        void shouldCreateAdminRole() {
            Role adminRole = Role.builder()
                    .id(3)
                    .name(Role.ERole.ROLE_ADMIN)
                    .build();
            
            assertEquals(3, adminRole.getId());
            assertEquals(Role.ERole.ROLE_ADMIN, adminRole.getName());
            assertEquals("ROLE_ADMIN", adminRole.getName().name());
        }
    }
}