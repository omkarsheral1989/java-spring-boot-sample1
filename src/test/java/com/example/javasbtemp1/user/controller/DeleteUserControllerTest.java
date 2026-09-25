package com.example.javasbtemp1.user.controller;

import com.example.javasbtemp1.user.exception.UserNotFoundException;
import com.example.javasbtemp1.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@DisplayName("DELETE /users/{id}")
class DeleteUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Nested
    @DisplayName("should delete user")
    class ShouldDeleteUser {

        @Test
        @DisplayName("when user exists")
        void whenUserExists() throws Exception {
            mockMvc.perform(delete("/users/1"))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(userService).delete(1L);
        }
    }

    @Nested
    @DisplayName("should fail")
    class ShouldFail {

        @Test
        @DisplayName("when user does not exist")
        void whenUserDoesNotExist() throws Exception {
            doThrow(new UserNotFoundException(1L))
                    .when(userService)
                    .delete(1L);

            mockMvc.perform(delete("/users/1"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().json("""
                            {
                              "status": 404,
                              "message": "User with id 1 was not found",
                              "fieldErrors": {}
                            }
                            """));

            verify(userService).delete(1L);
        }

        @Test
        @DisplayName("when id is not numeric")
        void whenIdIsNotNumeric() throws Exception {
            mockMvc.perform(delete("/users/not-a-number"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(userService);
        }
    }
}
