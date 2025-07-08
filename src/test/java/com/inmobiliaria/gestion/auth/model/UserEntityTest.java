package com.inmobiliaria.gestion.auth.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Tests")
class UserEntityTest {

    private Validator validator;
    private User validUser;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        validUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
    }

    @Nested
    @DisplayName("Constructor and Builder Tests")
    class ConstructorAndBuilderTests {

        @Test
        @DisplayName("Should create user with no args constructor")
        void shouldCreateUserWithNoArgsConstructor() {
            User user = new User();
            assertNotNull(user);
            assertNotNull(user.getRoles());
            assertTrue(user.getEnabled());
            assertNotNull(user.getCreatedAt());
        }

        @Test
        @DisplayName("Should create user with builder")
        void shouldCreateUserWithBuilder() {
            Role adminRole = Role.builder()
                    .id(1)
                    .name(Role.ERole.ROLE_ADMIN)
                    .build();
            
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            
            User user = User.builder()
                    .id(1L)
                    .username("admin")
                    .email("admin@example.com")
                    .password("adminpass")
                    .roles(roles)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            assertEquals(1L, user.getId());
            assertEquals("admin", user.getUsername());
            assertEquals("admin@example.com", user.getEmail());
            assertEquals("adminpass", user.getPassword());
            assertEquals(1, user.getRoles().size());
            assertTrue(user.getEnabled());
            assertNotNull(user.getCreatedAt());
            assertNotNull(user.getUpdatedAt());
        }

        @Test
        @DisplayName("Should create user with all args constructor")
        void shouldCreateUserWithAllArgsConstructor() {
            Set<Role> roles = new HashSet<>();
            LocalDateTime now = LocalDateTime.now();
            
            User user = new User(1L, "user", "user@test.com", "pass", roles, true, now, now);
            
            assertEquals(1L, user.getId());
            assertEquals("user", user.getUsername());
            assertEquals("user@test.com", user.getEmail());
            assertEquals("pass", user.getPassword());
            assertSame(roles, user.getRoles());
            assertTrue(user.getEnabled());
            assertEquals(now, user.getCreatedAt());
            assertEquals(now, user.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should have default enabled as true")
        void shouldHaveDefaultEnabledAsTrue() {
            User user = User.builder()
                    .username("test")
                    .email("test@example.com")
                    .password("password")
                    .build();
            
            assertTrue(user.getEnabled());
        }

        @Test
        @DisplayName("Should have default empty roles set")
        void shouldHaveDefaultEmptyRolesSet() {
            User user = User.builder()
                    .username("test")
                    .email("test@example.com")
                    .password("password")
                    .build();
            
            assertNotNull(user.getRoles());
            assertTrue(user.getRoles().isEmpty());
        }

        @Test
        @DisplayName("Should have default createdAt timestamp")
        void shouldHaveDefaultCreatedAtTimestamp() {
            LocalDateTime before = LocalDateTime.now().minusSeconds(1);
            
            User user = User.builder()
                    .username("test")
                    .email("test@example.com")
                    .password("password")
                    .build();
            
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);
            
            assertNotNull(user.getCreatedAt());
            assertTrue(user.getCreatedAt().isAfter(before));
            assertTrue(user.getCreatedAt().isBefore(after));
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should pass validation with valid user")
        void shouldPassValidationWithValidUser() {
            Set<ConstraintViolation<User>> violations = validator.validate(validUser);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when username is blank")
        void shouldFailValidationWhenUsernameIsBlank() {
            validUser.setUsername("");
            
            Set<ConstraintViolation<User>> violations = validator.validate(validUser);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("username")
                            && v.getMessage().contains("must not be blank")));
        }

        @Test
        @DisplayName("Should fail validation when username is null")
        void shouldFailValidationWhenUsernameIsNull() {
            validUser.setUsername(null);
            
            Set<ConstraintViolation<User>> violations = validator.validate(validUser);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
        }

        @Test
        @DisplayName("Should fail validation when username exceeds max size")
        void shouldFailValidationWhenUsernameExceedsMaxSize() {
            validUser.setUsername("a".repeat(21)); // Max is 20
            
            Set<ConstraintViolation<User>> violations = validator.validate(validUser);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("username")
                            && v.getMessage().contains("size must be between")));
        }

        @Test
        @DisplayName("Should fail validation when email is blank")
        void shouldFailValidationWhenEmailIsBlank() {
            validUser.setEmail("");
            
            Set<ConstraintViolation<User>> violations = validator.validate(validUser);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }

        @Test
        @DisplayName("Should fail validation when email is null")
        void shouldFailValidationWhenEmailIsNull() {
            validUser.setEmail(null);
            
            Set<ConstraintViolation<User>> violations = validator.validate(validUser);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }

        @Test
        @DisplayName("Should fail validation when email is invalid format")
        void shouldFailValidationWhenEmailIsInvalidFormat() {
            validUser.setEmail("invalid-email");
            
            Set<ConstraintViolation<User>> violations = validator.validate(validUser);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email")
                            && v.getMessage().contains("must be a well-formed email address")));
        }

        @Test
        @DisplayName("Should fail validation when email exceeds max size")
        void shouldFailValidationWhenEmailExceedsMaxSize() {
            validUser.setEmail("a".repeat(45) + "@test.com"); // Max is 50
            
            Set<ConstraintViolation<User>> violations = validator.validate(validUser);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email")
                            && v.getMessage().contains("size must be between")));
        }

        @Test
        @DisplayName("Should fail validation when password is blank")
        void shouldFailValidationWhenPasswordIsBlank() {
            validUser.setPassword("");
            
            Set<ConstraintViolation<User>> violations = validator.validate(validUser);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        }

        @Test
        @DisplayName("Should fail validation when password is null")
        void shouldFailValidationWhenPasswordIsNull() {
            validUser.setPassword(null);
            
            Set<ConstraintViolation<User>> violations = validator.validate(validUser);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        }

        @Test
        @DisplayName("Should fail validation when password exceeds max size")
        void shouldFailValidationWhenPasswordExceedsMaxSize() {
            validUser.setPassword("a".repeat(121)); // Max is 120
            
            Set<ConstraintViolation<User>> violations = validator.validate(validUser);
            
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("password")
                            && v.getMessage().contains("size must be between")));
        }
    }

    @Nested
    @DisplayName("Lifecycle Methods Tests")
    class LifecycleMethodsTests {

        @Test
        @DisplayName("Should update updatedAt timestamp on preUpdate")
        void shouldUpdateUpdatedAtTimestampOnPreUpdate() {
            User user = User.builder()
                    .username("test")
                    .email("test@example.com")
                    .password("password")
                    .build();
            
            assertNull(user.getUpdatedAt());
            
            user.preUpdate();
            
            assertNotNull(user.getUpdatedAt());
            assertTrue(user.getUpdatedAt().isAfter(user.getCreatedAt()));
        }
    }

    @Nested
    @DisplayName("Roles Management Tests")
    class RolesManagementTests {

        @Test
        @DisplayName("Should add role to user")
        void shouldAddRoleToUser() {
            Role userRole = Role.builder()
                    .id(1)
                    .name(Role.ERole.ROLE_USER)
                    .build();
            
            validUser.getRoles().add(userRole);
            
            assertEquals(1, validUser.getRoles().size());
            assertTrue(validUser.getRoles().contains(userRole));
        }

        @Test
        @DisplayName("Should remove role from user")
        void shouldRemoveRoleFromUser() {
            Role userRole = Role.builder()
                    .id(1)
                    .name(Role.ERole.ROLE_USER)
                    .build();
            
            Role adminRole = Role.builder()
                    .id(2)
                    .name(Role.ERole.ROLE_ADMIN)
                    .build();
            
            validUser.getRoles().add(userRole);
            validUser.getRoles().add(adminRole);
            
            assertEquals(2, validUser.getRoles().size());
            
            validUser.getRoles().remove(userRole);
            
            assertEquals(1, validUser.getRoles().size());
            assertFalse(validUser.getRoles().contains(userRole));
            assertTrue(validUser.getRoles().contains(adminRole));
        }

        @Test
        @DisplayName("Should handle multiple roles")
        void shouldHandleMultipleRoles() {
            Role userRole = Role.builder()
                    .id(1)
                    .name(Role.ERole.ROLE_USER)
                    .build();
            
            Role moderatorRole = Role.builder()
                    .id(2)
                    .name(Role.ERole.ROLE_MODERATOR)
                    .build();
            
            Role adminRole = Role.builder()
                    .id(3)
                    .name(Role.ERole.ROLE_ADMIN)
                    .build();
            
            validUser.getRoles().add(userRole);
            validUser.getRoles().add(moderatorRole);
            validUser.getRoles().add(adminRole);
            
            assertEquals(3, validUser.getRoles().size());
            assertTrue(validUser.getRoles().contains(userRole));
            assertTrue(validUser.getRoles().contains(moderatorRole));
            assertTrue(validUser.getRoles().contains(adminRole));
        }
    }

    @Nested
    @DisplayName("Equality and HashCode Tests")
    class EqualityAndHashCodeTests {

        @Test
        @DisplayName("Should be equal when same object")
        void shouldBeEqualWhenSameObject() {
            assertEquals(validUser, validUser);
            assertEquals(validUser.hashCode(), validUser.hashCode());
        }

        @Test
        @DisplayName("Should be equal when same data")
        void shouldBeEqualWhenSameData() {
            LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 1, 12, 0, 0);
            
            User user1 = User.builder()
                    .id(1L)
                    .username("test")
                    .email("test@example.com")
                    .password("password")
                    .enabled(true)
                    .createdAt(fixedTime)
                    .build();
            
            User user2 = User.builder()
                    .id(1L)
                    .username("test")
                    .email("test@example.com")
                    .password("password")
                    .enabled(true)
                    .createdAt(fixedTime)
                    .build();
            
            assertEquals(user1, user2);
            assertEquals(user1.hashCode(), user2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different data")
        void shouldNotBeEqualWhenDifferentData() {
            User user1 = User.builder()
                    .id(1L)
                    .username("test1")
                    .email("test1@example.com")
                    .password("password1")
                    .build();
            
            User user2 = User.builder()
                    .id(2L)
                    .username("test2")
                    .email("test2@example.com")
                    .password("password2")
                    .build();
            
            assertNotEquals(user1, user2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, validUser);
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            assertNotEquals("string", validUser);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should contain all fields in toString")
        void shouldContainAllFieldsInToString() {
            User user = User.builder()
                    .id(1L)
                    .username("testuser")
                    .email("test@example.com")
                    .password("password")
                    .enabled(true)
                    .build();
            
            String toString = user.toString();
            
            assertTrue(toString.contains("User"));
            assertTrue(toString.contains("id=1"));
            assertTrue(toString.contains("username=testuser"));
            assertTrue(toString.contains("email=test@example.com"));
            assertTrue(toString.contains("password=password"));
            assertTrue(toString.contains("enabled=true"));
        }
    }
}