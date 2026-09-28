package com.example.javasbtemp1.user.controller;

import com.example.javasbtemp1.user.entity.User;
import com.example.javasbtemp1.user.entity.Address;
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
					new User(1L, "Alice", "alice@example.com", new Address(1L, "Toronto", "Canada")),
					new User(2L, "Bob", "bob@example.com", new Address(2L, "Vancouver", "Canada"))
			));

			mockMvc.perform(get("/users"))
					.andExpect(status().isOk())
					.andExpect(content().json("""
							[
							  {
							    "id": 1,
							    "name": "Alice",
							    "email": "alice@example.com",
							    "address": {
							      "id": 1,
							      "city": "Toronto",
							      "country": "Canada"
							    }
							  },
							  {
							    "id": 2,
							    "name": "Bob",
							    "email": "bob@example.com",
							    "address": {
							      "id": 2,
							      "city": "Vancouver",
							      "country": "Canada"
							    }
							  }
							]
							""", JsonCompareMode.STRICT));

			verify(userService).findAll();
		}

		@Test
		@DisplayName("when users are returned in a specific order")
		void whenUsersAreReturnedInSpecificOrder() throws Exception {
			when(userService.findAll()).thenReturn(List.of(
					new User(2L, "Bob", "bob@example.com", new Address(2L, "Vancouver", "Canada")),
					new User(1L, "Alice", "alice@example.com", new Address(1L, "Toronto", "Canada"))
			));

			mockMvc.perform(get("/users"))
					.andExpect(status().isOk())
					.andExpect(content().json("""
							[
							  {
							    "id": 2,
							    "name": "Bob",
							    "email": "bob@example.com",
							    "address": {
							      "id": 2,
							      "city": "Vancouver",
							      "country": "Canada"
							    }
							  },
							  {
							    "id": 1,
							    "name": "Alice",
							    "email": "alice@example.com",
							    "address": {
							      "id": 1,
							      "city": "Toronto",
							      "country": "Canada"
							    }
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
