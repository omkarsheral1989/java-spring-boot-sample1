package com.example.javasbtemp1.user.repository;

import com.example.javasbtemp1.user.entity.Address;
import com.example.javasbtemp1.user.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class UserAddressPersistenceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void replacingAddressDeletesOldRowAndPersistsNewRow() {
        User user = userRepository.save(new User(
                null,
                "Alice",
                "alice@example.com",
                new Address(null, "Toronto", "Canada")));
        entityManager.flush();

        user.setAddress(new Address(null, "Vancouver", "Canada"));
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        assertEquals(1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM addresses", Integer.class));
        assertEquals("Vancouver", jdbcTemplate.queryForObject("SELECT city FROM addresses", String.class));
    }
}
