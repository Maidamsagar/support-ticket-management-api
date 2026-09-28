package com.sagar.ticketing.repository;

import com.sagar.ticketing.model.Ticket;
import com.sagar.ticketing.model.TicketPriority;
import com.sagar.ticketing.model.TicketStatus;
import com.sagar.ticketing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
public interface TicketRepository
        extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByStatus(
            TicketStatus status,
            Pageable pageable
    );

    Page<Ticket> findByPriority(
            TicketPriority priority,
            Pageable pageable
    );

    Page<Ticket> findByStatusAndPriority(
            TicketStatus status,
            TicketPriority priority,
            Pageable pageable
    );

    Page<Ticket> findByCreatedBy(
            User user,
            Pageable pageable
    );

    Page<Ticket> findByCreatedByAndStatus(
            User user,
            TicketStatus status,
            Pageable pageable
    );

    Page<Ticket> findByCreatedByAndPriority(
            User user,
            TicketPriority priority,
            Pageable pageable
    );

    Page<Ticket> findByCreatedByAndStatusAndPriority(
            User user,
            TicketStatus status,
            TicketPriority priority,
            Pageable pageable
    );
}