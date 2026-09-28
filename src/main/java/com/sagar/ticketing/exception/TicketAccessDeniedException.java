package com.sagar.ticketing.exception;

public class TicketAccessDeniedException
        extends RuntimeException {

    public TicketAccessDeniedException() {
        super("You are not allowed to access this ticket");
    }
}