package com.example.javasbtemp1.user.controller;

import com.example.javasbtemp1.user.dto.PatchUserDto;
import com.example.javasbtemp1.user.dto.AddressDto;
import com.example.javasbtemp1.user.entity.Address;
import com.example.javasbtemp1.user.entity.User;
import com.example.javasbtemp1.user.exception.DuplicateEmailException;
import com.example.javasbtemp1.user.exception.UserNotFoundException;
import com.example.javasbtemp1.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@DisplayName("PATCH /users/{id}")
class PatchUserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@Nested
	@DisplayName("should update user")
	class ShouldUpdateUser {

		@Test
		@DisplayName("when payload is valid")
		void whenPayloadIsValid() throws Exception {
			PatchUserDto expectedPayload = new PatchUserDto(
					"Alice Updated",
					"alice.updated@example.com",
					new AddressDto("Pine", "Canada"));

			User updatedUser = new User(
					1L,
					"Alice Updated",
					"alice.updated@example.com",
					new Address(2L, "Pine", "Canada"));
			when(userService.patch(eq(1L), eq(expectedPayload))).thenReturn(updatedUser);

			mockMvc.perform(patch("/users/1")
							.contentType("application/json")
							.content("""
									{
									  "name": "Alice Updated",
									  "email": "alice.updated@example.com",
									  "address": {
									    "city": "Pine",
									    "country": "Canada"
									  }
									}
									"""))
					.andExpect(status().isOk())
					.andExpect(content().json("""
							{
							  "id": 1,
							  "name": "Alice Updated",
							  "email": "alice.updated@example.com",
							  "address": {
							    "id": 2,
							    "city": "Pine",
							    "country": "Canada"
							  }
							}
							"""));

			verify(userService).patch(1L, expectedPayload);
		}

		@Test
		@DisplayName("when only name is provided")
		void whenOnlyNameIsProvided() throws Exception {
			PatchUserDto expectedPayload = new PatchUserDto();
			expectedPayload.setName("Alice Updated");

			User updatedUser = new User(1L, "Alice Updated", "alice@example.com", new Address(1L, "Toronto", "Canada"));
			when(userService.patch(eq(1L), eq(expectedPayload))).thenReturn(updatedUser);

			mockMvc.perform(patch("/users/1")
							.contentType("application/json")
							.content("""
									{
									  "name": "Alice Updated"
									}
									"""))
					.andExpect(status().isOk())
					.andExpect(content().json("""
							{
							  "id": 1,
							  "name": "Alice Updated",
							  "email": "alice@example.com",
							  "address": {
							    "id": 1,
							    "city": "Toronto",
							    "country": "Canada"
							  }
							}
							"""));

			verify(userService).patch(1L, expectedPayload);
		}

		@Test
		@DisplayName("when address is explicitly cleared")
		void whenAddressIsExplicitlyCleared() throws Exception {
			PatchUserDto expectedPayload = new PatchUserDto();
			expectedPayload.setAddress(null);

			User updatedUser = new User(1L, "Alice", "alice@example.com", null);
			when(userService.patch(eq(1L), eq(expectedPayload))).thenReturn(updatedUser);

			mockMvc.perform(patch("/users/1")
							.contentType("application/json")
							.content("""
									{
									  "address": null
									}
									"""))
					.andExpect(status().isOk())
					.andExpect(content().json("""
							{
							  "id": 1,
							  "name": "Alice",
							  "email": "alice@example.com",
							  "address": null
							}
							"""));

			verify(userService).patch(1L, expectedPayload);
		}
	}

	@Nested
	@DisplayName("should fail")
	class ShouldFail {

		@Test
		@DisplayName("when email is invalid")
		void whenEmailIsInvalid() throws Exception {
			mockMvc.perform(patch("/users/1")
							.contentType("application/json")
							.content("""
									{
									  "email": "not-an-email"
									}
									"""))
					.andExpect(status().isBadRequest())
					.andExpect(content().json("""
							{
							  "status": 400,
							  "message": "Request validation failed",
							  "fieldErrors": {
							    "email": "must be a well-formed email address"
							  }
							}
							"""));

			verifyNoInteractions(userService);
		}

		@Test
		@DisplayName("when name is blank")
		void whenNameIsBlank() throws Exception {
			PatchUserDto expectedPayload = new PatchUserDto();
			expectedPayload.setName("   ");
			doThrow(new IllegalArgumentException("name must not be blank"))
					.when(userService)
					.patch(eq(1L), eq(expectedPayload));

			mockMvc.perform(patch("/users/1")
							.contentType("application/json")
							.content("""
									{
									  "name": "   "
									}
									"""))
					.andExpect(status().isBadRequest())
					.andExpect(content().json("""
							{
							  "status": 400,
							  "message": "name must not be blank",
							  "fieldErrors": {}
							}
							"""));

			verify(userService).patch(1L, expectedPayload);
		}

		@Test
		@DisplayName("when address.city is missing")
		void whenAddressCityIsMissing() throws Exception {
			mockMvc.perform(patch("/users/1")
							.contentType("application/json")
							.content("""
									{
									  "address": {
									    "country": "Canada"
									  }
									}
									"""))
					.andExpect(status().isBadRequest())
					.andExpect(content().json("""
							{
							  "status": 400,
							  "message": "Request validation failed",
							  "fieldErrors": {
							    "address.city": "must not be blank"
							  }
							}
							"""));

			verifyNoInteractions(userService);
		}

		@Test
		@DisplayName("when address.country is missing")
		void whenAddressCountryIsMissing() throws Exception {
			mockMvc.perform(patch("/users/1")
							.contentType("application/json")
							.content("""
									{
									  "address": {
									    "city": "Toronto"
									  }
									}
									"""))
					.andExpect(status().isBadRequest())
					.andExpect(content().json("""
							{
							  "status": 400,
							  "message": "Request validation failed",
							  "fieldErrors": {
							    "address.country": "must not be blank"
							  }
							}
							"""));

			verifyNoInteractions(userService);
		}

		@Test
		@DisplayName("when user does not exist")
		void whenUserDoesNotExist() throws Exception {
			PatchUserDto expectedPayload = new PatchUserDto();
			expectedPayload.setName("Alice Updated");
			doThrow(new UserNotFoundException(1L))
					.when(userService)
					.patch(eq(1L), eq(expectedPayload));

			mockMvc.perform(patch("/users/1")
							.contentType("application/json")
							.content("""
									{
									  "name": "Alice Updated"
									}
									"""))
					.andExpect(status().isNotFound())
					.andExpect(content().json("""
							{
							  "status": 404,
							  "message": "User with id 1 was not found",
							  "fieldErrors": {}
							}
							"""));

			verify(userService).patch(1L, expectedPayload);
		}

		@Test
		@DisplayName("when email already exists")
		void whenEmailAlreadyExists() throws Exception {
			PatchUserDto expectedPayload = new PatchUserDto();
			expectedPayload.setEmail("existing@example.com");
			doThrow(new DuplicateEmailException("existing@example.com"))
					.when(userService)
					.patch(eq(1L), eq(expectedPayload));

			mockMvc.perform(patch("/users/1")
							.contentType("application/json")
							.content("""
									{
									  "email": "existing@example.com"
									}
									"""))
					.andExpect(status().isConflict())
					.andExpect(content().json("""
							{
							  "status": 409,
							  "message": "A user with email existing@example.com already exists",
							  "fieldErrors": {}
							}
							"""));

			verify(userService).patch(1L, expectedPayload);
		}

		@Test
		@DisplayName("when request body is malformed")
		void whenRequestBodyIsMalformed() throws Exception {
			mockMvc.perform(patch("/users/1")
							.contentType("application/json")
							.content("{\"name\":\"Alice\""))
					.andExpect(status().isBadRequest())
					.andExpect(content().json("""
							{
							  "status": 400,
							  "message": "Request body is invalid or contains unknown fields",
							  "fieldErrors": {}
							}
							"""));

			verifyNoInteractions(userService);
		}

		@Test
		@DisplayName("when id is not numeric")
		void whenIdIsNotNumeric() throws Exception {
			mockMvc.perform(patch("/users/not-a-number")
							.contentType("application/json")
							.content("""
									{
									  "name": "Alice Updated"
									}
									"""))
					.andExpect(status().isBadRequest());

			verifyNoInteractions(userService);
		}
	}
}
