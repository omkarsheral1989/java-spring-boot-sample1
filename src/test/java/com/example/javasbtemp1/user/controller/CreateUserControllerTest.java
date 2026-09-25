package com.example.javasbtemp1.user.controller;

import com.example.javasbtemp1.user.dto.CreateUserDto;
import com.example.javasbtemp1.user.entity.User;
import com.example.javasbtemp1.user.exception.DuplicateEmailException;
import com.example.javasbtemp1.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@DisplayName("POST /users")
class CreateUserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@Nested
	@DisplayName("should create user")
	class ShouldCreateUser {

		@Test
		@DisplayName("when payload is valid")
		void whenPayloadIsValid() throws Exception {
			CreateUserDto expectedPayload =
					new CreateUserDto("Alice", "alice@example.com", "123 Main Street");
			User createdUser = new User(1L, "Alice", "alice@example.com", "123 Main Street");
			when(userService.create(eq(expectedPayload))).thenReturn(createdUser);

			mockMvc.perform(post("/users")
							.contentType("application/json")
							.content("""
									{
									  "name": "Alice",
									  "email": "alice@example.com",
									  "address": "123 Main Street"
									}
									"""))
					.andExpect(status().isCreated())
					.andExpect(header().string("Location", "http://localhost/users/1"))
					.andExpect(content().json("""
							{
							  "id": 1,
							  "address": "123 Main Street",
							  "email": "alice@example.com",
							  "name": "Alice"
							}
							"""));

			verify(userService).create(eq(expectedPayload));
		}

		@Test
		@DisplayName("when optional address is omitted")
		void whenOptionalAddressIsOmitted() throws Exception {
			CreateUserDto expectedPayload = new CreateUserDto("Alice", "alice@example.com", null);
			User createdUser = new User(1L, "Alice", "alice@example.com", null);
			when(userService.create(eq(expectedPayload))).thenReturn(createdUser);

			mockMvc.perform(post("/users")
							.contentType("application/json")
							.content("""
									{
									  "name": "Alice",
									  "email": "alice@example.com"
									}
									"""))
					.andExpect(status().isCreated())
					.andExpect(header().string("Location", "http://localhost/users/1"))
					.andExpect(content().json("""
							{
							  "id": 1,
							  "address": null,
							  "email": "alice@example.com",
							  "name": "Alice"
							}
							"""));

			verify(userService).create(eq(expectedPayload));
		}
	}

	@Nested
	@DisplayName("should fail")
	class ShouldFail {
		@Test
		@DisplayName("when email is invalid")
		void whenEmailIsInvalid() throws Exception {
			mockMvc.perform(post("/users")
							.contentType("application/json")
							.content("""
									{
									  "name": "Alice",
									  "email": "not-an-email",
									  "address": "123 Main Street"
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
		@DisplayName("when email is missing")
		void whenEmailIsMissing() throws Exception {
			mockMvc.perform(post("/users")
							.contentType("application/json")
							.content("""
									{
									  "name": "Alice",
									  "address": "123 Main Street"
									}
									"""))
					.andExpect(status().isBadRequest())
					.andExpect(content().json("""
							{
							  "status": 400,
							  "message": "Request validation failed",
							  "fieldErrors": {
							    "email": "must not be blank"
							  }
							}
							"""));

			verifyNoInteractions(userService);
		}

		@Test
		@DisplayName("when name is missing")
		void whenNameIsMissing() throws Exception {
			mockMvc.perform(post("/users")
							.contentType("application/json")
							.content("""
									{
									  "email": "alice@example.com"
									}
									"""))
					.andExpect(status().isBadRequest())
					.andExpect(content().json("""
							{
							  "status": 400,
							  "message": "Request validation failed",
							  "fieldErrors": {
							    "name": "must not be blank"
							  }
							}
							"""));

			verifyNoInteractions(userService);
		}

		@Test
		@DisplayName("when name is blank")
		void whenNameIsBlank() throws Exception {
			mockMvc.perform(post("/users")
							.contentType("application/json")
							.content("""
									{
									  "name": "   ",
									  "email": "alice@example.com"
									}
									"""))
					.andExpect(status().isBadRequest())
					.andExpect(content().json("""
							{
							  "status": 400,
							  "message": "Request validation failed",
							  "fieldErrors": {
							    "name": "must not be blank"
							  }
							}
							"""));

			verifyNoInteractions(userService);
		}

		@Test
		@DisplayName("when request body is malformed")
		void whenRequestBodyIsMalformed() throws Exception {
			mockMvc.perform(post("/users")
							.contentType("application/json")
							.content("{\"name\":\"Alice\",\"email\":"))
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
		@DisplayName("when request body is missing")
		void whenRequestBodyIsMissing() throws Exception {
			mockMvc.perform(post("/users")
							.contentType("application/json"))
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
		@DisplayName("when email already exists")
		void whenEmailAlreadyExists() throws Exception {
			CreateUserDto expectedPayload = new CreateUserDto("Alice", "alice@example.com", null);
			when(userService.create(eq(expectedPayload)))
					.thenThrow(new DuplicateEmailException("alice@example.com"));

			mockMvc.perform(post("/users")
							.contentType("application/json")
							.content("""
									{
									  "name": "Alice",
									  "email": "alice@example.com"
									}
									"""))
					.andExpect(status().isConflict())
					.andExpect(content().json("""
							{
							  "status": 409,
							  "message": "A user with email alice@example.com already exists",
							  "fieldErrors": {}
							}
							"""));

			verify(userService).create(eq(expectedPayload));
		}
	}
}
