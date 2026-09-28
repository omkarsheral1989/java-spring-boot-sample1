package com.example.javasbtemp1.user.service;

import com.example.javasbtemp1.user.entity.User;
import com.example.javasbtemp1.user.entity.Address;
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
@DisplayName("UserService.delete()")
class DeleteUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("should delete user")
    class ShouldDeleteUser {

        @Test
        @DisplayName("when user exists")
        void whenUserExists() {
            User expectedUser = new User(1L, "Alice", "alice@example.com", new Address(1L, "Toronto", "Canada"));
            when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));

            userService.delete(1L);

            verify(userRepository).findById(1L);
            verify(userRepository).delete(expectedUser);
        }
    }

    @Nested
    @DisplayName("should fail")
    class ShouldFail {

        @Test
        @DisplayName("when user does not exist")
        void whenUserDoesNotExist() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            UserNotFoundException actualException = assertThrows(
                    UserNotFoundException.class,
                    () -> userService.delete(1L));

            assertEquals("User with id 1 was not found", actualException.getMessage());
            verify(userRepository).findById(1L);
            verify(userRepository, never()).delete(any(User.class));
        }
    }
}
