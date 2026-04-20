package com.it3030.smartcampus.member3_ticketing.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ticket_comments")
public class TicketComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "commenter_email", nullable = false)
    private String commenterEmail;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    protected TicketComment() {
    }

    public TicketComment(Long ticketId, String commenterEmail, String content) {
        this.ticketId = ticketId;
        this.commenterEmail = commenterEmail;
        this.content = content;
    }

    public Long getId() { return id; }
    public Long getTicketId() { return ticketId; }
    public String getCommenterEmail() { return commenterEmail; }
    public String getContent() { return content; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setContent(String content) { this.content = content; }
}
