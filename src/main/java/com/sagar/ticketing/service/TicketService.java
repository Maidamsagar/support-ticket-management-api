package com.sagar.ticketing.service;

import com.sagar.ticketing.dto.CreateTicketRequest;
import com.sagar.ticketing.dto.PagedResponse;
import com.sagar.ticketing.dto.TicketResponse;
import com.sagar.ticketing.dto.UpdateTicketRequest;
import com.sagar.ticketing.exception.TicketAccessDeniedException;
import com.sagar.ticketing.exception.TicketNotFoundException;
import com.sagar.ticketing.model.*;
import com.sagar.ticketing.repository.TicketRepository;
import com.sagar.ticketing.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketAccessService ticketAccessService;

    public TicketService(
            TicketRepository ticketRepository,
            UserRepository userRepository,
            TicketAccessService ticketAccessService) {

        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.ticketAccessService = ticketAccessService;
    }

    public PagedResponse<TicketResponse> getTickets(
            TicketStatus status,
            TicketPriority priority,
            String userEmail,
            int page,
            int size) {
        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page must be 0 or greater"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Size must be between 1 and 100"
            );
        }

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"
                        )
                );

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Ticket> ticketPage;

        if (user.getRole() == UserRole.ADMIN) {

            if (status != null && priority != null) {

                ticketPage =
                        ticketRepository.findByStatusAndPriority(
                                status,
                                priority,
                                pageable
                        );

            } else if (status != null) {

                ticketPage =
                        ticketRepository.findByStatus(
                                status,
                                pageable
                        );

            } else if (priority != null) {

                ticketPage =
                        ticketRepository.findByPriority(
                                priority,
                                pageable
                        );

            } else {

                ticketPage =
                        ticketRepository.findAll(pageable);
            }

        } else {

            if (status != null && priority != null) {

                ticketPage =
                        ticketRepository
                                .findByCreatedByAndStatusAndPriority(
                                        user,
                                        status,
                                        priority,
                                        pageable
                                );

            } else if (status != null) {

                ticketPage =
                        ticketRepository.findByCreatedByAndStatus(
                                user,
                                status,
                                pageable
                        );

            } else if (priority != null) {

                ticketPage =
                        ticketRepository.findByCreatedByAndPriority(
                                user,
                                priority,
                                pageable
                        );

            } else {

                ticketPage =
                        ticketRepository.findByCreatedBy(
                                user,
                                pageable
                        );
            }
        }

        List<TicketResponse> content =
                ticketPage.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return new PagedResponse<>(
                content,
                ticketPage.getNumber(),
                ticketPage.getSize(),
                ticketPage.getTotalElements(),
                ticketPage.getTotalPages(),
                ticketPage.isLast()
        );
    }

    public TicketResponse getTicketById(
            Long id,
            String userEmail) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new TicketNotFoundException(id)
                );

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"
                        )
                );

        ticketAccessService.checkAccess(ticket, user);

        return toResponse(ticket);
    }

    public TicketResponse createTicket(
            CreateTicketRequest request,
            String userEmail) {

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"
                        )
                );

        Ticket ticket = new Ticket(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getPriority(),
                user
        );

        Ticket savedTicket =
                ticketRepository.save(ticket);

        return toResponse(savedTicket);
    }
    private TicketResponse toResponse(Ticket ticket) {

        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCreatedBy().getId(),
                ticket.getCreatedBy().getName(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
    public TicketResponse updateTicket(
            Long id,
            UpdateTicketRequest request,
            String userEmail) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new TicketNotFoundException(id)
                );

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"
                        )
                );

        ticketAccessService.checkAccess(ticket, user);

        ticket.update(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getPriority()
        );

        Ticket savedTicket =
                ticketRepository.save(ticket);

        return toResponse(savedTicket);
    }
    public void deleteTicket(
            Long id,
            String userEmail) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new TicketNotFoundException(id)
                );

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"
                        )
                );

        ticketAccessService.checkAccess(ticket, user);

        ticketRepository.delete(ticket);
    }

}