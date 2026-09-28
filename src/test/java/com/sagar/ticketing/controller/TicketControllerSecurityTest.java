package com.sagar.ticketing.controller;

import com.sagar.ticketing.config.SecurityConfig;
import com.sagar.ticketing.repository.UserRepository;
import com.sagar.ticketing.security.JwtAuthenticationFilter;
import com.sagar.ticketing.service.JwtService;
import com.sagar.ticketing.service.TicketService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class
})
@ImportAutoConfiguration({
        SecurityAutoConfiguration.class,
        ServletWebSecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
})
class TicketControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService ticketService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void shouldReturn401WhenUserIsNotAuthenticated()
            throws Exception {

        mockMvc.perform(
                        get("/api/tickets/10")
                )
                .andExpect(
                        status().isUnauthorized()
                );

        verifyNoInteractions(ticketService);
    }

    @Test
    void shouldAllowAuthenticatedUser()
            throws Exception {

        mockMvc.perform(
                        get("/api/tickets/10")
                                .with(
                                        user("sagar@example.com")
                                                .authorities(
                                                        () -> "USER"
                                                )
                                )
                )
                .andExpect(
                        status().isOk()
                );

        verify(ticketService)
                .getTicketById(
                        10L,
                        "sagar@example.com"
                );
    }
}