package com.sagar.ticketing.repository;

import com.sagar.ticketing.model.User;
import com.sagar.ticketing.model.UserRole;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {

        User user = new User(
                "Sagar",
                "sagar@example.com",
                "hashed-password",
                UserRole.USER
        );

        userRepository.save(user);

        Optional<User> result =
                userRepository.findByEmail(
                        "sagar@example.com"
                );

        assertTrue(result.isPresent());

        assertEquals(
                "Sagar",
                result.get().getName()
        );

        assertEquals(
                "sagar@example.com",
                result.get().getEmail()
        );

        assertEquals(
                UserRole.USER,
                result.get().getRole()
        );
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {

        User user = new User(
                "Sagar",
                "sagar@example.com",
                "hashed-password",
                UserRole.USER
        );

        userRepository.save(user);

        boolean exists =
                userRepository.existsByEmail(
                        "sagar@example.com"
                );

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {

        boolean exists =
                userRepository.existsByEmail(
                        "missing@example.com"
                );

        assertFalse(exists);
    }
}