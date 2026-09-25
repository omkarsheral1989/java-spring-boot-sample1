package com.example.javasbtemp1.user.service;

import com.example.javasbtemp1.user.dto.PatchUserDto;
import com.example.javasbtemp1.user.entity.User;
import com.example.javasbtemp1.user.exception.DuplicateEmailException;
import com.example.javasbtemp1.user.exception.UserNotFoundException;
import com.example.javasbtemp1.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService.patch()")
class PatchUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("should update user")
    class ShouldUpdateUser {

        @Test
        @DisplayName("when all fields are provided")
        void whenAllFieldsAreProvided() {
            User existingUser = new User(1L, "Alice", "alice@example.com", "123 Main Street");
            User updatedUser = new User(1L, "Alice Updated", "alice.updated@example.com", "789 Pine Road");
            PatchUserDto payload = new PatchUserDto();
            payload.setName("  Alice Updated  ");
            payload.setEmail(" ALICE.UPDATED@EXAMPLE.COM ");
            payload.setAddress(" 789 Pine Road ");
            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmailIgnoreCase("alice.updated@example.com")).thenReturn(false);
            when(userRepository.save(updatedUser)).thenReturn(updatedUser);

            User actualUser = userService.patch(1L, payload);

            assertEquals(updatedUser, actualUser);
            verify(userRepository).findById(1L);
            verify(userRepository).existsByEmailIgnoreCase("alice.updated@example.com");
            verify(userRepository).save(updatedUser);
        }

        @Test
        @DisplayName("when only name is provided")
        void whenOnlyNameIsProvided() {
            User existingUser = new User(1L, "Alice", "alice@example.com", "123 Main Street");
            User updatedUser = new User(1L, "Alice Updated", "alice@example.com", "123 Main Street");
            PatchUserDto payload = new PatchUserDto();
            payload.setName("  Alice Updated  ");
            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(userRepository.save(updatedUser)).thenReturn(updatedUser);

            User actualUser = userService.patch(1L, payload);

            assertEquals(updatedUser, actualUser);
            verify(userRepository).save(updatedUser);
            verify(userRepository, never()).existsByEmailIgnoreCase(anyString());
        }

        @Test
        @DisplayName("when only email is provided")
        void whenOnlyEmailIsProvided() {
            User existingUser = new User(1L, "Alice", "alice@example.com", "123 Main Street");
            User updatedUser = new User(1L, "Alice", "alice.new@example.com", "123 Main Street");
            PatchUserDto payload = new PatchUserDto();
            payload.setEmail(" ALICE.NEW@EXAMPLE.COM ");
            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmailIgnoreCase("alice.new@example.com")).thenReturn(false);
            when(userRepository.save(updatedUser)).thenReturn(updatedUser);

            User actualUser = userService.patch(1L, payload);

            assertEquals(updatedUser, actualUser);
            verify(userRepository).existsByEmailIgnoreCase("alice.new@example.com");
            verify(userRepository).save(updatedUser);
        }

        @Test
        @DisplayName("when address is explicitly cleared")
        void whenAddressIsExplicitlyCleared() {
            User existingUser = new User(1L, "Alice", "alice@example.com", "123 Main Street");
            User updatedUser = new User(1L, "Alice", "alice@example.com", null);
            PatchUserDto payload = new PatchUserDto();
            payload.setAddress(null);
            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(userRepository.save(updatedUser)).thenReturn(updatedUser);

            User actualUser = userService.patch(1L, payload);

            assertEquals(updatedUser, actualUser);
            verify(userRepository).save(updatedUser);
        }

        @Test
        @DisplayName("when address is provided")
        void whenAddressIsProvided() {
            User existingUser = new User(1L, "Alice", "alice@example.com", null);
            User updatedUser = new User(1L, "Alice", "alice@example.com", "789 Pine Road");
            PatchUserDto payload = new PatchUserDto();
            payload.setAddress(" 789 Pine Road ");
            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(userRepository.save(updatedUser)).thenReturn(updatedUser);

            User actualUser = userService.patch(1L, payload);

            assertEquals(updatedUser, actualUser);
            verify(userRepository).save(updatedUser);
        }

        @Test
        @DisplayName("when email is same with different casing")
        void whenEmailIsSameWithDifferentCasing() {
            User existingUser = new User(1L, "Alice", "alice@example.com", "123 Main Street");
            User updatedUser = new User(1L, "Alice", "alice@example.com", "123 Main Street");
            PatchUserDto payload = new PatchUserDto();
            payload.setEmail(" ALICE@EXAMPLE.COM ");
            when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
            when(userRepository.save(updatedUser)).thenReturn(updatedUser);

            User actualUser = userService.patch(existingUser.getId(), payload);

            assertEquals(updatedUser, actualUser);
            verify(userRepository, never()).existsByEmailIgnoreCase(anyString());
            verify(userRepository).save(updatedUser);
        }
    }

    @Nested
    @DisplayName("should fail")
    class ShouldFail {

        @Test
        @DisplayName("when user does not exist")
        void whenUserDoesNotExist() {
            PatchUserDto payload = new PatchUserDto();
            payload.setName("Alice Updated");
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            UserNotFoundException actualException = assertThrows(
                    UserNotFoundException.class,
                    () -> userService.patch(1L, payload));

            assertEquals("User with id 1 was not found", actualException.getMessage());
            verify(userRepository).findById(1L);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("when name is blank")
        void whenNameIsBlank() {
            User userToUpdate = new User(1L, "Alice", "alice@example.com", "123 Main Street");
            PatchUserDto payload = new PatchUserDto();
            payload.setName("   ");
            when(userRepository.findById(1L)).thenReturn(Optional.of(userToUpdate));

            IllegalArgumentException actualException = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.patch(1L, payload));

            assertEquals("name must not be blank", actualException.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("when email is blank")
        void whenEmailIsBlank() {
            User userToUpdate = new User(1L, "Alice", "alice@example.com", "123 Main Street");
            PatchUserDto payload = new PatchUserDto();
            payload.setEmail("   ");
            when(userRepository.findById(userToUpdate.getId())).thenReturn(Optional.of(userToUpdate));

            IllegalArgumentException actualException = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.patch(userToUpdate.getId(), payload));

            assertEquals("email must not be blank", actualException.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("when email already exists")
        void whenEmailAlreadyExists() {
            User userToUpdate = new User(1L, "Alice", "alice@example.com", "123 Main Street");
            User userWithExistingEmail = new User(2L, "Bob", "bob@example.com", "456 Oak Avenue");
            PatchUserDto payload = new PatchUserDto();
            payload.setEmail(userWithExistingEmail.getEmail());
            when(userRepository.findById(userToUpdate.getId())).thenReturn(Optional.of(userToUpdate));
            when(userRepository.existsByEmailIgnoreCase(userWithExistingEmail.getEmail())).thenReturn(true);

            DuplicateEmailException actualException = assertThrows(
                    DuplicateEmailException.class,
                    () -> userService.patch(userToUpdate.getId(), payload));

            assertEquals("A user with email " + userWithExistingEmail.getEmail() + " already exists",
                    actualException.getMessage());
            verify(userRepository).existsByEmailIgnoreCase(userWithExistingEmail.getEmail());
            verify(userRepository, never()).save(any(User.class));
        }

    }
}
