package com.it3030.smartcampus.member3_ticketing.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ticket_attachments")
public class TicketAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "data_url", nullable = false, columnDefinition = "TEXT")
    private String dataUrl;

    @Column(name = "file_name")
    private String fileName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    protected TicketAttachment() {
    }

    public TicketAttachment(Long ticketId, String dataUrl, String fileName) {
        this.ticketId = ticketId;
        this.dataUrl = dataUrl;
        this.fileName = fileName;
    }

    public Long getId() { return id; }
    public Long getTicketId() { return ticketId; }
    public String getDataUrl() { return dataUrl; }
    public String getFileName() { return fileName; }
    public Instant getCreatedAt() { return createdAt; }
}
