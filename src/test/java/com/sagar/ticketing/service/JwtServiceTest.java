package com.sagar.ticketing.service;

import com.sagar.ticketing.model.User;
import com.sagar.ticketing.model.UserRole;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.ExpiredJwtException;

class JwtServiceTest {

    @Test
    void shouldGenerateTokenAndExtractSameEmail() {

        String secret =
                Base64.getEncoder()
                        .encodeToString(
                                "01234567890123456789012345678901"
                                        .getBytes(StandardCharsets.UTF_8)
                        );

        JwtService jwtService =
                new JwtService(
                        secret,
                        3600000
                );

        User user = mock(User.class);

        when(user.getEmail())
                .thenReturn("sagar@example.com");

        when(user.getRole())
                .thenReturn(UserRole.USER);

        String token =
                jwtService.generateToken(user);

        String extractedEmail =
                jwtService.extractEmail(token);

        assertEquals(
                "sagar@example.com",
                extractedEmail
        );
    }
    @Test
    void shouldRejectTamperedToken() {

        String secret =
                Base64.getEncoder()
                        .encodeToString(
                                "01234567890123456789012345678901"
                                        .getBytes(StandardCharsets.UTF_8)
                        );

        JwtService jwtService =
                new JwtService(
                        secret,
                        3600000
                );

        User user = mock(User.class);

        when(user.getEmail())
                .thenReturn("sagar@example.com");

        when(user.getRole())
                .thenReturn(UserRole.USER);

        String token =
                jwtService.generateToken(user);

        String tamperedToken =
                token.substring(0, token.length() - 1)
                        + "X";

        assertThrows(
                JwtException.class,
                () -> jwtService.extractEmail(tamperedToken)
        );
    }
    @Test
    void shouldRejectExpiredToken() {

        String secret =
                Base64.getEncoder()
                        .encodeToString(
                                "01234567890123456789012345678901"
                                        .getBytes(StandardCharsets.UTF_8)
                        );

        JwtService jwtService =
                new JwtService(
                        secret,
                        -1000
                );

        User user = mock(User.class);

        when(user.getEmail())
                .thenReturn("sagar@example.com");

        when(user.getRole())
                .thenReturn(UserRole.USER);

        String token =
                jwtService.generateToken(user);

        assertThrows(
                ExpiredJwtException.class,
                () -> jwtService.extractEmail(token)
        );
    }
}