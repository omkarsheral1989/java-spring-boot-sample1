package com.example.javasbtemp1.user.controller;

import com.example.javasbtemp1.user.entity.User;
import com.example.javasbtemp1.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@DisplayName("GET /users")
class GetUsersControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@Nested
	@DisplayName("should return users")
	class ShouldReturnUsers {

		@Test
		@DisplayName("when users exist")
		void whenUsersExist() throws Exception {
			when(userService.findAll()).thenReturn(List.of(
					new User(1L, "Alice", "alice@example.com", "123 Main Street"),
					new User(2L, "Bob", "bob@example.com", "456 Oak Avenue")
			));

			mockMvc.perform(get("/users"))
					.andExpect(status().isOk())
					.andExpect(content().json("""
							[
							  {
							    "id": 1,
							    "name": "Alice",
							    "email": "alice@example.com",
							    "address": "123 Main Street"
							  },
							  {
							    "id": 2,
							    "name": "Bob",
							    "email": "bob@example.com",
							    "address": "456 Oak Avenue"
							  }
							]
							""", JsonCompareMode.STRICT));

			verify(userService).findAll();
		}

		@Test
		@DisplayName("when users are returned in a specific order")
		void whenUsersAreReturnedInSpecificOrder() throws Exception {
			when(userService.findAll()).thenReturn(List.of(
					new User(2L, "Bob", "bob@example.com", "456 Oak Avenue"),
					new User(1L, "Alice", "alice@example.com", "123 Main Street")
			));

			mockMvc.perform(get("/users"))
					.andExpect(status().isOk())
					.andExpect(content().json("""
							[
							  {
							    "id": 2,
							    "name": "Bob",
							    "email": "bob@example.com",
							    "address": "456 Oak Avenue"
							  },
							  {
							    "id": 1,
							    "name": "Alice",
							    "email": "alice@example.com",
							    "address": "123 Main Street"
							  }
							]
							""", JsonCompareMode.STRICT));

			verify(userService).findAll();
		}
	}

	@Nested
	@DisplayName("should return an empty list")
	class ShouldReturnAnEmptyList {

		@Test
		@DisplayName("when no users exist")
		void whenNoUsersExist() throws Exception {
			when(userService.findAll()).thenReturn(List.of());

			mockMvc.perform(get("/users"))
					.andExpect(status().isOk())
					.andExpect(content().json("[]", JsonCompareMode.STRICT));

			verify(userService).findAll();
		}
	}
}
