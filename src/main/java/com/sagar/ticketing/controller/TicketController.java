package com.sagar.ticketing.controller;

import com.sagar.ticketing.dto.*;
import com.sagar.ticketing.model.Ticket;
import com.sagar.ticketing.model.TicketPriority;
import com.sagar.ticketing.model.TicketStatus;
import com.sagar.ticketing.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public PagedResponse<TicketResponse> getTickets(
            @RequestParam(required = false)
            TicketStatus status,

            @RequestParam(required = false)
            TicketPriority priority,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            Authentication authentication) {

        return ticketService.getTickets(
                status,
                priority,
                authentication.getName(),
                page,
                size
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(
            @PathVariable Long id,
            Authentication authentication) {

        TicketResponse ticket =
                ticketService.getTicketById(
                        id,
                        authentication.getName()
                );

        return ResponseEntity.ok(ticket);
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            Authentication authentication) {

        TicketResponse ticket =
                ticketService.createTicket(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ticket);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketRequest request,
            Authentication authentication) {

        TicketResponse ticket =
                ticketService.updateTicket(
                        id,
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(ticket);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(
            @PathVariable Long id,
            Authentication authentication) {

        ticketService.deleteTicket(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

}