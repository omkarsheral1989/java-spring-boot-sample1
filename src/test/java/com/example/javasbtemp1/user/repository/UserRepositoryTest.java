package com.example.javasbtemp1.user.repository;

import com.example.javasbtemp1.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DisplayName("UserRepository")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Nested
    @DisplayName("existsByEmailIgnoreCase()")
    class ExistsByEmailIgnoreCase {

        @Test
        @DisplayName("returns true when email matches ignoring case")
        void returnsTrueWhenEmailMatchesIgnoringCase() {
            userRepository.save(new User(null, "Alice", "alice@example.com", null));

            assertTrue(userRepository.existsByEmailIgnoreCase("ALICE@EXAMPLE.COM"));
        }

        @Test
        @DisplayName("returns false when email does not exist")
        void returnsFalseWhenEmailDoesNotExist() {
            assertFalse(userRepository.existsByEmailIgnoreCase("unknown@example.com"));
        }
    }

    @Nested
    @DisplayName("findByEmailIgnoreCase()")
    class FindByEmailIgnoreCase {

        @Test
        @DisplayName("returns the matching user when email differs by case")
        void returnsMatchingUserWhenEmailDiffersByCase() {
            User savedUser = userRepository.save(new User(null, "Alice", "alice@example.com", null));

            Optional<User> result = userRepository.findByEmailIgnoreCase("ALICE@EXAMPLE.COM");

            assertTrue(result.isPresent());
            assertEquals(savedUser.getId(), result.get().getId());
            assertEquals("Alice", result.get().getName());
            assertEquals("alice@example.com", result.get().getEmail());
        }

        @Test
        @DisplayName("returns empty when email does not exist")
        void returnsEmptyWhenEmailDoesNotExist() {
            assertFalse(userRepository.findByEmailIgnoreCase("unknown@example.com").isPresent());
        }
    }
}
