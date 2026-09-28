package com.sagar.ticketing.controller;

import com.sagar.ticketing.dto.LoginResponse;
import com.sagar.ticketing.exception.EmailAlreadyExistsException;
import com.sagar.ticketing.exception.GlobalExceptionHandler;
import com.sagar.ticketing.exception.InvalidCredentialsException;
import com.sagar.ticketing.model.UserRole;
import com.sagar.ticketing.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mockMvc;

    private UserService userService;

    @BeforeEach
    void setUp() {

        userService = mock(UserService.class);

        AuthController authController =
                new AuthController(userService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();
    }
    @Test
    void shouldReturn201WhenRegistrationIsValid() throws Exception {

        String requestBody = """
            {
              "name": "Sagar",
              "email": "sagar@example.com",
              "password": "hello123"
            }
            """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(
                        status().isCreated()
                );

        verify(userService)
                .register(any());
    }
    @Test
    void shouldReturn400WhenRegistrationDataIsInvalid()
            throws Exception {

        String requestBody = """
            {
              "name": "",
              "email": "not-an-email",
              "password": "123"
            }
            """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(userService);
    }
    @Test
    void shouldReturn409WhenEmailAlreadyExists()
            throws Exception {

        String requestBody = """
            {
              "name": "Sagar",
              "email": "sagar@example.com",
              "password": "hello123"
            }
            """;

        when(userService.register(any()))
                .thenThrow(
                        new EmailAlreadyExistsException(
                                "sagar@example.com"
                        )
                );

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isConflict()
                );
    }
    @Test
    void shouldReturn400WhenLoginDataIsInvalid()
            throws Exception {

        String requestBody = """
            {
              "email": "",
              "password": ""
            }
            """;

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(userService);
    }
    @Test
    void shouldReturn200WhenLoginIsSuccessful()
            throws Exception {

        String requestBody = """
            {
              "email": "sagar@example.com",
              "password": "hello123"
            }
            """;

        LoginResponse loginResponse =
                new LoginResponse(
                        "fake-jwt-token",
                        1L,
                        "Sagar",
                        "sagar@example.com",
                        UserRole.USER
                );

        when(userService.login(any()))
                .thenReturn(loginResponse);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.token")
                                .value("fake-jwt-token")
                )
                .andExpect(
                        jsonPath("$.email")
                                .value("sagar@example.com")
                );
    }
    @Test
    void shouldReturn401WhenCredentialsAreInvalid()
            throws Exception {

        String requestBody = """
            {
              "email": "sagar@example.com",
              "password": "wrong-password"
            }
            """;

        when(userService.login(any()))
                .thenThrow(
                        new InvalidCredentialsException()
                );

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }
}