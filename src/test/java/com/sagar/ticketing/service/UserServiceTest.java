package com.sagar.ticketing.service;

import com.sagar.ticketing.dto.LoginRequest;
import com.sagar.ticketing.dto.LoginResponse;
import com.sagar.ticketing.dto.RegisterRequest;
import com.sagar.ticketing.dto.UserResponse;
import com.sagar.ticketing.exception.EmailAlreadyExistsException;
import com.sagar.ticketing.exception.InvalidCredentialsException;
import com.sagar.ticketing.model.User;
import com.sagar.ticketing.model.UserRole;
import com.sagar.ticketing.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUserSuccessfully() {

        RegisterRequest request = new RegisterRequest();
        request.setName("Sagar");
        request.setEmail("sagar@example.com");
        request.setPassword("hello123");

        when(userRepository.existsByEmail("sagar@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("hello123"))
                .thenReturn("hashed-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        UserResponse response =
                userService.register(request);
        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository)
                .save(userCaptor.capture());

        User savedUser =
                userCaptor.getValue();
        assertEquals(
                "hashed-password",
                savedUser.getPassword()
        );
        assertNotEquals(
                "hello123",
                savedUser.getPassword()
        );
        assertEquals(
                UserRole.USER,
                savedUser.getRole()
        );

        assertEquals("Sagar", response.getName());
        assertEquals(
                "sagar@example.com",
                response.getEmail()
        );

        assertEquals(
                UserRole.USER,
                response.getRole()
        );

        verify(passwordEncoder)
                .encode("hello123");

        verify(userRepository)
                .save(any(User.class));
    }
    @Test
    void shouldRejectRegistrationWhenEmailAlreadyExists() {

        RegisterRequest request = new RegisterRequest();
        request.setName("Sagar");
        request.setEmail("sagar@example.com");
        request.setPassword("hello123");

        when(userRepository.existsByEmail("sagar@example.com"))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.register(request)
        );

        verify(userRepository)
                .existsByEmail("sagar@example.com");

        verifyNoInteractions(passwordEncoder);

        verify(userRepository, never())
                .save(any(User.class));
    }
    @Test
    void shouldLoginSuccessfullyWithCorrectCredentials() {

        User user = new User(
                "Sagar",
                "sagar@example.com",
                "stored-hash",
                UserRole.USER
        );

        LoginRequest request = new LoginRequest();
        request.setEmail("sagar@example.com");
        request.setPassword("hello123");

        when(userRepository.findByEmail("sagar@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "hello123",
                "stored-hash"
        )).thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("fake-jwt-token");

        LoginResponse response =
                userService.login(request);

        assertEquals(
                "fake-jwt-token",
                response.getToken()
        );

        assertEquals(
                "sagar@example.com",
                response.getEmail()
        );

        assertEquals(
                UserRole.USER,
                response.getRole()
        );

        verify(passwordEncoder).matches(
                "hello123",
                "stored-hash"
        );

        verify(jwtService)
                .generateToken(user);
    }
    @Test
    void shouldRejectLoginWhenPasswordIsWrong() {

        User user = new User(
                "Sagar",
                "sagar@example.com",
                "stored-hash",
                UserRole.USER
        );

        LoginRequest request = new LoginRequest();
        request.setEmail("sagar@example.com");
        request.setPassword("wrong-password");

        when(userRepository.findByEmail("sagar@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrong-password",
                "stored-hash"
        )).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );

        verify(passwordEncoder).matches(
                "wrong-password",
                "stored-hash"
        );

        verifyNoInteractions(jwtService);
    }
    @Test
    void shouldRejectLoginWhenEmailDoesNotExist() {

        LoginRequest request = new LoginRequest();
        request.setEmail("nobody@example.com");
        request.setPassword("hello123");

        when(userRepository.findByEmail("nobody@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );

        verify(userRepository)
                .findByEmail("nobody@example.com");

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }
}