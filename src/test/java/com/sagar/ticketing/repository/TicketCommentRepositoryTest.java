package com.sagar.ticketing.repository;

import com.sagar.ticketing.model.Ticket;
import com.sagar.ticketing.model.TicketComment;
import com.sagar.ticketing.model.TicketPriority;
import com.sagar.ticketing.model.TicketStatus;
import com.sagar.ticketing.model.User;
import com.sagar.ticketing.model.UserRole;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TicketCommentRepositoryTest {

    @Autowired
    private TicketCommentRepository ticketCommentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturnCommentsForTicketInCreatedAtOrder() {

        User user = new User(
                "Sagar",
                "sagar@example.com",
                "hashed-password",
                UserRole.USER
        );

        userRepository.save(user);

        Ticket ticket = new Ticket(
                "VPN issue",
                "Unable to connect",
                TicketStatus.OPEN,
                TicketPriority.HIGH,
                user
        );

        ticketRepository.save(ticket);

        TicketComment firstComment =
                new TicketComment(
                        "First comment",
                        ticket,
                        user
                );

        TicketComment secondComment =
                new TicketComment(
                        "Second comment",
                        ticket,
                        user
                );

        ticketCommentRepository.save(firstComment);
        ticketCommentRepository.save(secondComment);

        List<TicketComment> comments =
                ticketCommentRepository
                        .findByTicketOrderByCreatedAtAsc(ticket);

        assertEquals(
                2,
                comments.size()
        );

        assertEquals(
                "First comment",
                comments.get(0).getContent()
        );

        assertEquals(
                "Second comment",
                comments.get(1).getContent()
        );

        assertEquals(
                ticket.getId(),
                comments.get(0).getTicket().getId()
        );

        assertEquals(
                "sagar@example.com",
                comments.get(0).getAuthor().getEmail()
        );

        assertNotNull(
                comments.get(0).getCreatedAt()
        );

        assertNotNull(
                comments.get(1).getCreatedAt()
        );
    }
}