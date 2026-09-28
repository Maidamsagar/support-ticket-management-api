package com.sagar.ticketing.dto;

import java.time.LocalDateTime;

public class CommentResponse {

    private Long id;
    private String content;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;

    public CommentResponse(
            Long id,
            String content,
            Long authorId,
            String authorName,
            LocalDateTime createdAt) {

        this.id = id;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}