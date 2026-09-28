package com.sagar.ticketing.service;

import com.sagar.ticketing.exception.TicketAccessDeniedException;
import com.sagar.ticketing.model.Ticket;
import com.sagar.ticketing.model.TicketPriority;
import com.sagar.ticketing.model.TicketStatus;
import com.sagar.ticketing.model.User;
import com.sagar.ticketing.model.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketAccessServiceTest {

    private final TicketAccessService ticketAccessService =
            new TicketAccessService();

    @Test
    void shouldAllowAdminToAccessTicket() {

        User owner = new User(
                "Sagar",
                "sagar@example.com",
                "hashed-password",
                UserRole.USER
        );

        User admin = new User(
                "Admin",
                "admin@example.com",
                "hashed-password",
                UserRole.ADMIN
        );

        Ticket ticket = new Ticket(
                "VPN issue",
                "VPN keeps disconnecting",
                TicketStatus.OPEN,
                TicketPriority.HIGH,
                owner
        );

        assertDoesNotThrow(() ->
                ticketAccessService.checkAccess(ticket, admin)
        );
    }

    @Test
    void shouldDenyNormalUserWhoDoesNotOwnTicket() {

        User owner = new User(
                "Sagar",
                "sagar@example.com",
                "hashed-password",
                UserRole.USER
        );

        User anotherUser = new User(
                "John",
                "john@example.com",
                "hashed-password",
                UserRole.USER
        );

        Ticket ticket = new Ticket(
                "VPN issue",
                "VPN keeps disconnecting",
                TicketStatus.OPEN,
                TicketPriority.HIGH,
                owner
        );

        assertThrows(
                TicketAccessDeniedException.class,
                () -> ticketAccessService.checkAccess(
                        ticket,
                        anotherUser
                )
        );
    }

    @Test
    void shouldAllowOwnerToAccessTicket() {

        User owner = new User(
                "Sagar",
                "sagar@example.com",
                "hashed-password",
                UserRole.USER
        );

        Ticket ticket = new Ticket(
                "VPN issue",
                "VPN keeps disconnecting",
                TicketStatus.OPEN,
                TicketPriority.HIGH,
                owner
        );

        assertDoesNotThrow(() ->
                ticketAccessService.checkAccess(
                        ticket,
                        owner
                )
        );
    }
}