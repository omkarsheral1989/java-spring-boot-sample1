package com.example.javasbtemp1.user.service;

import com.example.javasbtemp1.user.entity.User;
import com.example.javasbtemp1.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService.findAll()")
class FindAllUsersServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("should return users")
    class ShouldReturnUsers {

        @Test
        @DisplayName("when users exist")
        void whenUsersExist() {
            List<User> expectedUsers = List.of(
                    new User(1L, "Alice", "alice@example.com", "123 Main Street"),
                    new User(2L, "Bob", "bob@example.com", "456 Oak Avenue"));
            when(userRepository.findAll()).thenReturn(expectedUsers);

            List<User> actualUsers = userService.findAll();

            assertEquals(expectedUsers, actualUsers);
            verify(userRepository).findAll();
        }
    }

    @Nested
    @DisplayName("should return an empty list")
    class ShouldReturnAnEmptyList {

        @Test
        @DisplayName("when no users exist")
        void whenNoUsersExist() {
            when(userRepository.findAll()).thenReturn(List.of());

            List<User> actualUsers = userService.findAll();

            assertTrue(actualUsers.isEmpty());
            verify(userRepository).findAll();
        }
    }
}
