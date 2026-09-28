package com.sagar.ticketing.service;

import com.sagar.ticketing.exception.TicketAccessDeniedException;
import com.sagar.ticketing.model.Ticket;
import com.sagar.ticketing.model.User;
import com.sagar.ticketing.model.UserRole;
import org.springframework.stereotype.Service;

@Service
public class TicketAccessService {

    public void checkAccess(Ticket ticket, User user) {

        boolean isAdmin =
                user.getRole() == UserRole.ADMIN;

        boolean isOwner =
                ticket.getCreatedBy()
                        .getEmail()
                        .equals(user.getEmail());

        if (!isAdmin && !isOwner) {
            throw new TicketAccessDeniedException();
        }
    }
}