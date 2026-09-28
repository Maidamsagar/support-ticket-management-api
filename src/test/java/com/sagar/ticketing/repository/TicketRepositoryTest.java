package com.sagar.ticketing.repository;

import com.sagar.ticketing.model.Ticket;
import com.sagar.ticketing.model.TicketPriority;
import com.sagar.ticketing.model.TicketStatus;
import com.sagar.ticketing.model.User;
import com.sagar.ticketing.model.UserRole;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindTicketWithOwner() {

        User owner = new User(
                "Sagar",
                "sagar@example.com",
                "hashed-password",
                UserRole.USER
        );

        userRepository.save(owner);

        Ticket ticket = new Ticket(
                "VPN issue",
                "Unable to connect to VPN",
                TicketStatus.OPEN,
                TicketPriority.HIGH,
                owner
        );

        Ticket savedTicket =
                ticketRepository.save(ticket);

        Optional<Ticket> result =
                ticketRepository.findById(
                        savedTicket.getId()
                );

        assertTrue(result.isPresent());

        Ticket foundTicket = result.get();

        assertEquals(
                "VPN issue",
                foundTicket.getTitle()
        );

        assertEquals(
                "Unable to connect to VPN",
                foundTicket.getDescription()
        );

        assertEquals(
                TicketStatus.OPEN,
                foundTicket.getStatus()
        );

        assertEquals(
                TicketPriority.HIGH,
                foundTicket.getPriority()
        );

        assertEquals(
                "sagar@example.com",
                foundTicket.getCreatedBy().getEmail()
        );

        assertNotNull(foundTicket.getCreatedAt());
        assertNotNull(foundTicket.getUpdatedAt());
    }
    @Test
    void shouldPersistTicketUpdate() {

        User owner = new User(
                "Sagar",
                "sagar@example.com",
                "hashed-password",
                UserRole.USER
        );

        userRepository.save(owner);

        Ticket ticket = new Ticket(
                "VPN issue",
                "Unable to connect to VPN",
                TicketStatus.OPEN,
                TicketPriority.HIGH,
                owner
        );

        Ticket savedTicket =
                ticketRepository.save(ticket);

        savedTicket.update(
                "VPN issue resolved",
                "VPN connection is working now",
                TicketStatus.RESOLVED,
                TicketPriority.LOW
        );

        ticketRepository.flush();

        Ticket updatedTicket =
                ticketRepository.findById(
                        savedTicket.getId()
                ).orElseThrow();

        assertEquals(
                "VPN issue resolved",
                updatedTicket.getTitle()
        );

        assertEquals(
                "VPN connection is working now",
                updatedTicket.getDescription()
        );

        assertEquals(
                TicketStatus.RESOLVED,
                updatedTicket.getStatus()
        );

        assertEquals(
                TicketPriority.LOW,
                updatedTicket.getPriority()
        );

        assertEquals(
                "sagar@example.com",
                updatedTicket.getCreatedBy().getEmail()
        );
    }
    @Test
    void shouldReturnOnlyTicketsCreatedByGivenUser() {

        User sagar = new User(
                "Sagar",
                "sagar@example.com",
                "hashed-password",
                UserRole.USER
        );

        User john = new User(
                "John",
                "john@example.com",
                "hashed-password",
                UserRole.USER
        );

        userRepository.save(sagar);
        userRepository.save(john);

        Ticket sagarTicket1 = new Ticket(
                "VPN issue",
                "Unable to connect to VPN",
                TicketStatus.OPEN,
                TicketPriority.HIGH,
                sagar
        );

        Ticket sagarTicket2 = new Ticket(
                "Laptop issue",
                "Laptop is running slowly",
                TicketStatus.IN_PROGRESS,
                TicketPriority.MEDIUM,
                sagar
        );

        Ticket johnTicket = new Ticket(
                "Email issue",
                "Unable to send emails",
                TicketStatus.OPEN,
                TicketPriority.LOW,
                john
        );

        ticketRepository.saveAll(
                List.of(
                        sagarTicket1,
                        sagarTicket2,
                        johnTicket
                )
        );

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Ticket> result =
                ticketRepository.findByCreatedBy(
                        sagar,
                        pageable
                );

        assertEquals(
                2,
                result.getTotalElements()
        );

        assertEquals(
                2,
                result.getContent().size()
        );

        assertTrue(
                result.getContent()
                        .stream()
                        .allMatch(
                                ticket ->
                                        ticket.getCreatedBy()
                                                .getEmail()
                                                .equals("sagar@example.com")
                        )
        );
    }
    @Test
    void shouldFilterUsersTicketsByStatusAndPriority() {

        User sagar = new User(
                "Sagar",
                "sagar@example.com",
                "hashed-password",
                UserRole.USER
        );

        User john = new User(
                "John",
                "john@example.com",
                "hashed-password",
                UserRole.USER
        );

        userRepository.save(sagar);
        userRepository.save(john);

        Ticket matchingTicket = new Ticket(
                "VPN issue",
                "Unable to connect to VPN",
                TicketStatus.OPEN,
                TicketPriority.HIGH,
                sagar
        );

        Ticket wrongStatus = new Ticket(
                "Laptop issue",
                "Laptop is slow",
                TicketStatus.IN_PROGRESS,
                TicketPriority.HIGH,
                sagar
        );

        Ticket wrongPriority = new Ticket(
                "Email issue",
                "Unable to send emails",
                TicketStatus.OPEN,
                TicketPriority.LOW,
                sagar
        );

        Ticket otherUsersTicket = new Ticket(
                "Network issue",
                "Network is unavailable",
                TicketStatus.OPEN,
                TicketPriority.HIGH,
                john
        );

        ticketRepository.saveAll(
                List.of(
                        matchingTicket,
                        wrongStatus,
                        wrongPriority,
                        otherUsersTicket
                )
        );

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Ticket> result =
                ticketRepository
                        .findByCreatedByAndStatusAndPriority(
                                sagar,
                                TicketStatus.OPEN,
                                TicketPriority.HIGH,
                                pageable
                        );

        assertEquals(
                1,
                result.getTotalElements()
        );

        Ticket resultTicket =
                result.getContent().get(0);

        assertEquals(
                "VPN issue",
                resultTicket.getTitle()
        );

        assertEquals(
                "sagar@example.com",
                resultTicket.getCreatedBy().getEmail()
        );

        assertEquals(
                TicketStatus.OPEN,
                resultTicket.getStatus()
        );

        assertEquals(
                TicketPriority.HIGH,
                resultTicket.getPriority()
        );
    }
    @Test
    void shouldPaginateUsersTickets() {

        User sagar = new User(
                "Sagar",
                "sagar@example.com",
                "hashed-password",
                UserRole.USER
        );

        userRepository.save(sagar);

        ticketRepository.saveAll(
                List.of(
                        new Ticket(
                                "Ticket 1",
                                "Description 1",
                                TicketStatus.OPEN,
                                TicketPriority.LOW,
                                sagar
                        ),
                        new Ticket(
                                "Ticket 2",
                                "Description 2",
                                TicketStatus.OPEN,
                                TicketPriority.MEDIUM,
                                sagar
                        ),
                        new Ticket(
                                "Ticket 3",
                                "Description 3",
                                TicketStatus.IN_PROGRESS,
                                TicketPriority.HIGH,
                                sagar
                        ),
                        new Ticket(
                                "Ticket 4",
                                "Description 4",
                                TicketStatus.RESOLVED,
                                TicketPriority.HIGH,
                                sagar
                        ),
                        new Ticket(
                                "Ticket 5",
                                "Description 5",
                                TicketStatus.CLOSED,
                                TicketPriority.CRITICAL,
                                sagar
                        )
                )
        );

        Pageable firstPageRequest =
                PageRequest.of(0, 2);

        Page<Ticket> firstPage =
                ticketRepository.findByCreatedBy(
                        sagar,
                        firstPageRequest
                );

        assertEquals(
                2,
                firstPage.getContent().size()
        );

        assertEquals(
                5,
                firstPage.getTotalElements()
        );

        assertEquals(
                3,
                firstPage.getTotalPages()
        );

        assertEquals(
                0,
                firstPage.getNumber()
        );

        assertFalse(
                firstPage.isLast()
        );
    }
}