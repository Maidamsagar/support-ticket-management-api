package com.sagar.ticketing.repository;

import com.sagar.ticketing.model.Ticket;
import com.sagar.ticketing.model.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketCommentRepository
        extends JpaRepository<TicketComment, Long> {

    List<TicketComment> findByTicketOrderByCreatedAtAsc(
            Ticket ticket
    );
}