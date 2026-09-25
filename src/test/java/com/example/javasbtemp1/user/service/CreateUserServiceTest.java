package com.example.javasbtemp1.user.service;

import com.example.javasbtemp1.user.dto.CreateUserDto;
import com.example.javasbtemp1.user.entity.User;
import com.example.javasbtemp1.user.exception.DuplicateEmailException;
import com.example.javasbtemp1.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService.create()")
class CreateUserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@Nested
	@DisplayName("should create user")
	class ShouldCreateUser {

		@Test
		@DisplayName("with normalized and trimmed fields")
		void withNormalizedAndTrimmedFields() {
			CreateUserDto payload = new CreateUserDto(
					"  Alice  ",
					" Alice@Example.com ",
					" 123 Main Street ");
			User userToSave = new User(null, "Alice", "alice@example.com", "123 Main Street");
			User expectedSavedUser = new User(1L, "Alice", "alice@example.com", "123 Main Street");
			when(userRepository.existsByEmailIgnoreCase("alice@example.com")).thenReturn(false);
			when(userRepository.save(eq(userToSave))).thenReturn(expectedSavedUser);

			User actualSavedUser = userService.create(payload);

			assertEquals(expectedSavedUser, actualSavedUser);
			verify(userRepository).save(eq(userToSave));
			verify(userRepository).existsByEmailIgnoreCase("alice@example.com");
		}

		@Test
		@DisplayName("with a null address when address is blank")
		void withNullAddressWhenAddressIsBlank() {
			CreateUserDto payload = new CreateUserDto("Alice", "alice@example.com", "   ");
			User userToSave = new User(null, "Alice", "alice@example.com", null);
			when(userRepository.existsByEmailIgnoreCase("alice@example.com")).thenReturn(false);
			when(userRepository.save(eq(userToSave))).thenReturn(userToSave);

			User actualSavedUser = userService.create(payload);

			assertNull(actualSavedUser.getAddress());
			verify(userRepository).save(eq(userToSave));
		}
	}

	@Nested
	@DisplayName("should fail")
	class ShouldFail {

		@Test
		@DisplayName("when email already exists")
		void whenEmailAlreadyExists() {
			CreateUserDto payload = new CreateUserDto(
					"Alice",
					" Alice@Example.com ",
					"123 Main Street");
			when(userRepository.existsByEmailIgnoreCase("alice@example.com")).thenReturn(true);

			DuplicateEmailException actualException = assertThrows(
					DuplicateEmailException.class,
					() -> userService.create(payload));

			assertEquals("A user with email alice@example.com already exists", actualException.getMessage());
			verify(userRepository).existsByEmailIgnoreCase("alice@example.com");
			verify(userRepository, never()).save(any(User.class));
		}

	}
}
