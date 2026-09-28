package com.sagar.ticketing.controller;

import com.sagar.ticketing.dto.CommentResponse;
import com.sagar.ticketing.dto.CreateCommentRequest;
import com.sagar.ticketing.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(
            CommentService commentService) {

        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long ticketId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication) {

        CommentResponse comment =
                commentService.addComment(
                        ticketId,
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(comment);
    }

    @GetMapping
    public List<CommentResponse> getComments(
            @PathVariable Long ticketId,
            Authentication authentication) {

        return commentService.getComments(
                ticketId,
                authentication.getName()
        );
    }
}