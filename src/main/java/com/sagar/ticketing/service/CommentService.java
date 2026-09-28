package com.sagar.ticketing.service;

import com.sagar.ticketing.dto.CommentResponse;
import com.sagar.ticketing.dto.CreateCommentRequest;
import com.sagar.ticketing.exception.TicketAccessDeniedException;
import com.sagar.ticketing.exception.TicketNotFoundException;
import com.sagar.ticketing.model.*;
import com.sagar.ticketing.repository.TicketCommentRepository;
import com.sagar.ticketing.repository.TicketRepository;
import com.sagar.ticketing.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final TicketCommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketAccessService ticketAccessService;

    public CommentService(
            TicketCommentRepository commentRepository,
            TicketRepository ticketRepository,
            UserRepository userRepository,
            TicketAccessService ticketAccessService) {

        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.ticketAccessService = ticketAccessService;
    }

    public CommentResponse addComment(
            Long ticketId,
            CreateCommentRequest request,
            String userEmail) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new TicketNotFoundException(ticketId)
                );

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"
                        )
                );

        ticketAccessService.checkAccess(ticket, user);

        TicketComment comment = new TicketComment(
                request.getContent(),
                ticket,
                user
        );

        TicketComment savedComment =
                commentRepository.save(comment);

        return toResponse(savedComment);
    }

    public List<CommentResponse> getComments(
            Long ticketId,
            String userEmail) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new TicketNotFoundException(ticketId)
                );

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"
                        )
                );

        ticketAccessService.checkAccess(ticket, user);

        return commentRepository
                .findByTicketOrderByCreatedAtAsc(ticket)
                .stream()
                .map(this::toResponse)
                .toList();
    }



    private CommentResponse toResponse(
            TicketComment comment) {

        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getCreatedAt()
        );
    }
}