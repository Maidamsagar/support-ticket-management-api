package com.sagar.ticketing.service;

import com.sagar.ticketing.dto.CreateTicketRequest;
import com.sagar.ticketing.dto.TicketResponse;
import com.sagar.ticketing.dto.UpdateTicketRequest;
import com.sagar.ticketing.exception.TicketAccessDeniedException;
import com.sagar.ticketing.exception.TicketNotFoundException;
import com.sagar.ticketing.model.*;
import com.sagar.ticketing.repository.TicketRepository;
import com.sagar.ticketing.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketAccessService ticketAccessService;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void shouldThrowExceptionWhenTicketDoesNotExist() {

        when(ticketRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                TicketNotFoundException.class,
                () -> ticketService.getTicketById(
                        999L,
                        "sagar@example.com"
                )
        );

        verify(ticketRepository).findById(999L);

        verifyNoInteractions(userRepository);

        verifyNoInteractions(ticketAccessService);
    }
    @Test
    void shouldReturnTicketWhenTicketExists() {

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

        when(ticketRepository.findById(10L))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findByEmail("sagar@example.com"))
                .thenReturn(Optional.of(owner));

        TicketResponse response =
                ticketService.getTicketById(
                        10L,
                        "sagar@example.com"
                );

        assertEquals(
                "VPN issue",
                response.getTitle()
        );

        assertEquals(
                TicketStatus.OPEN,
                response.getStatus()
        );

        assertEquals(
                TicketPriority.HIGH,
                response.getPriority()
        );

        verify(ticketRepository)
                .findById(10L);

        verify(userRepository)
                .findByEmail("sagar@example.com");

        verify(ticketAccessService)
                .checkAccess(ticket, owner);
    }
    @Test
    void shouldThrowExceptionWhenUserCannotAccessTicket() {

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

        when(ticketRepository.findById(10L))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(anotherUser));

        doThrow(new TicketAccessDeniedException())
                .when(ticketAccessService)
                .checkAccess(ticket, anotherUser);

        assertThrows(
                TicketAccessDeniedException.class,
                () -> ticketService.getTicketById(
                        10L,
                        "john@example.com"
                )
        );

        verify(ticketRepository)
                .findById(10L);

        verify(userRepository)
                .findByEmail("john@example.com");

        verify(ticketAccessService)
                .checkAccess(ticket, anotherUser);
    }
    @Test
    void shouldUpdateTicketWhenUserHasAccess() {

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

        UpdateTicketRequest request = new UpdateTicketRequest();
        request.setTitle("VPN issue updated");
        request.setDescription("VPN now disconnects every five minutes");
        request.setStatus(TicketStatus.IN_PROGRESS);
        request.setPriority(TicketPriority.CRITICAL);

        when(ticketRepository.findById(10L))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findByEmail("sagar@example.com"))
                .thenReturn(Optional.of(owner));

        when(ticketRepository.save(ticket))
                .thenReturn(ticket);

        TicketResponse response =
                ticketService.updateTicket(
                        10L,
                        request,
                        "sagar@example.com"
                );

        assertEquals(
                "VPN issue updated",
                response.getTitle()
        );

        assertEquals(
                "VPN now disconnects every five minutes",
                response.getDescription()
        );

        assertEquals(
                TicketStatus.IN_PROGRESS,
                response.getStatus()
        );

        assertEquals(
                TicketPriority.CRITICAL,
                response.getPriority()
        );

        verify(ticketRepository)
                .findById(10L);

        verify(userRepository)
                .findByEmail("sagar@example.com");

        verify(ticketAccessService)
                .checkAccess(ticket, owner);

        verify(ticketRepository)
                .save(ticket);
    }
    @Test
    void shouldNotSaveTicketWhenAccessIsDenied() {

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

        UpdateTicketRequest request = new UpdateTicketRequest();
        request.setTitle("Changed title");
        request.setDescription("Changed description");
        request.setStatus(TicketStatus.CLOSED);
        request.setPriority(TicketPriority.LOW);

        when(ticketRepository.findById(10L))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(anotherUser));

        doThrow(new TicketAccessDeniedException())
                .when(ticketAccessService)
                .checkAccess(ticket, anotherUser);

        assertThrows(
                TicketAccessDeniedException.class,
                () -> ticketService.updateTicket(
                        10L,
                        request,
                        "john@example.com"
                )
        );

        verify(ticketAccessService)
                .checkAccess(ticket, anotherUser);

        verify(ticketRepository, never())
                .save(any(Ticket.class));
    }
    @Test
    void shouldCreateTicketForAuthenticatedUser() {

        User owner = new User(
                "Sagar",
                "sagar@example.com",
                "hashed-password",
                UserRole.USER
        );

        CreateTicketRequest request = new CreateTicketRequest();
        request.setTitle("Laptop issue");
        request.setDescription("Laptop keeps overheating");
        request.setStatus(TicketStatus.OPEN);
        request.setPriority(TicketPriority.HIGH);

        when(userRepository.findByEmail("sagar@example.com"))
                .thenReturn(Optional.of(owner));

        when(ticketRepository.save(any(Ticket.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        TicketResponse response =
                ticketService.createTicket(
                        request,
                        "sagar@example.com"
                );

        assertEquals(
                "Laptop issue",
                response.getTitle()
        );

        assertEquals(
                "Laptop keeps overheating",
                response.getDescription()
        );

        assertEquals(
                TicketStatus.OPEN,
                response.getStatus()
        );

        assertEquals(
                TicketPriority.HIGH,
                response.getPriority()
        );

        assertEquals(
                "Sagar",
                response.getCreatedByName()
        );

        verify(userRepository)
                .findByEmail("sagar@example.com");

        ArgumentCaptor<Ticket> ticketCaptor =
                ArgumentCaptor.forClass(Ticket.class);

        verify(ticketRepository)
                .save(ticketCaptor.capture());

        Ticket savedTicket =
                ticketCaptor.getValue();

        assertEquals(
                "sagar@example.com",
                savedTicket.getCreatedBy().getEmail()
        );
    }
    @Test
    void shouldDeleteTicketWhenUserHasAccess() {

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

        when(ticketRepository.findById(10L))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findByEmail("sagar@example.com"))
                .thenReturn(Optional.of(owner));

        ticketService.deleteTicket(
                10L,
                "sagar@example.com"
        );

        verify(ticketRepository)
                .findById(10L);

        verify(userRepository)
                .findByEmail("sagar@example.com");

        verify(ticketAccessService)
                .checkAccess(ticket, owner);

        verify(ticketRepository)
                .delete(ticket);
    }
    @Test
    void shouldNotDeleteTicketWhenAccessIsDenied() {

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

        when(ticketRepository.findById(10L))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(anotherUser));

        doThrow(new TicketAccessDeniedException())
                .when(ticketAccessService)
                .checkAccess(ticket, anotherUser);

        assertThrows(
                TicketAccessDeniedException.class,
                () -> ticketService.deleteTicket(
                        10L,
                        "john@example.com"
                )
        );

        verify(ticketAccessService)
                .checkAccess(ticket, anotherUser);

        verify(ticketRepository, never())
                .delete(any(Ticket.class));
    }
}