package com.it3030.smartcampus.member3_ticketing.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.it3030.smartcampus.member3_ticketing.dto.*;
import com.it3030.smartcampus.member3_ticketing.model.*;
import com.it3030.smartcampus.member3_ticketing.repository.*;
import com.it3030.smartcampus.member4.service.NotificationService;

@Service
public class TicketService {

    private final IncidentTicketRepository ticketRepository;
    private final TicketCommentRepository commentRepository;
    private final TicketAttachmentRepository attachmentRepository;
    private final NotificationService notificationService;

    public TicketService(IncidentTicketRepository ticketRepository,
                         TicketCommentRepository commentRepository,
                         TicketAttachmentRepository attachmentRepository,
                         NotificationService notificationService) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
        this.attachmentRepository = attachmentRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request, String reporterEmail) {
        TicketCategory category;
        try {
            category = TicketCategory.valueOf(request.category().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category: " + request.category());
        }

        TicketPriority priority;
        try {
            priority = TicketPriority.valueOf(request.priority().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid priority: " + request.priority());
        }

        IncidentTicket ticket = new IncidentTicket(
            request.title(),
            request.description(),
            category,
            priority,
            request.resourceLocation(),
            request.contactEmail(),
            request.contactPhone(),
            reporterEmail
        );

        ticket = ticketRepository.save(ticket);

        if (request.attachments() != null && !request.attachments().isEmpty()) {
            if (request.attachments().size() > 3) {
                throw new IllegalArgumentException("Maximum 3 attachments allowed");
            }
            for (CreateTicketRequest.AttachmentData att : request.attachments()) {
                if (att.dataUrl() != null && !att.dataUrl().isBlank()) {
                    attachmentRepository.save(new TicketAttachment(ticket.getId(), att.dataUrl(), att.fileName()));
                }
            }
        }

        notificationService.createSystemNotification(
            reporterEmail,
            "Ticket #" + ticket.getId() + " created successfully and is now OPEN.");

        return getTicketById(ticket.getId(), reporterEmail, false);
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Long ticketId, String currentUserEmail, boolean isAdmin) {
        IncidentTicket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        if (!isAdmin && !ticket.getReporterEmail().equalsIgnoreCase(currentUserEmail)
            && (ticket.getAssignedTechnicianEmail() == null
                || !ticket.getAssignedTechnicianEmail().equalsIgnoreCase(currentUserEmail))) {
            throw new IllegalArgumentException("You do not have access to this ticket");
        }

        List<TicketAttachment> attachments = attachmentRepository.findByTicketId(ticketId);
        List<TicketComment> comments = commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);

        return mapToTicketResponse(ticket, attachments, comments, currentUserEmail);
    }

    @Transactional(readOnly = true)
    public List<TicketSummaryResponse> getMyTickets(String reporterEmail) {
        return ticketRepository.findByReporterEmailOrderByCreatedAtDesc(reporterEmail)
            .stream()
            .map(this::mapToSummary)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketSummaryResponse> getAllTickets() {
        return ticketRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(this::mapToSummary)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketSummaryResponse> getAssignedTickets(String technicianEmail) {
        return ticketRepository.findByAssignedTechnicianEmailOrderByCreatedAtDesc(technicianEmail)
            .stream()
            .map(this::mapToSummary)
            .toList();
    }

    @Transactional
    public TicketResponse updateTicketStatus(Long ticketId, UpdateTicketStatusRequest request,
                                             String currentUserEmail, boolean isAdmin) {
        IncidentTicket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        TicketStatus newStatus;
        try {
            newStatus = TicketStatus.valueOf(request.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + request.status());
        }

        validateStatusTransition(ticket.getStatus(), newStatus, isAdmin);

        if (newStatus == TicketStatus.REJECTED) {
            if (!isAdmin) {
                throw new IllegalArgumentException("Only ticket administrators can reject tickets");
            }
            if (request.rejectionReason() == null || request.rejectionReason().isBlank()) {
                throw new IllegalArgumentException("Rejection reason is required");
            }
            ticket.setRejectionReason(request.rejectionReason());
        }

        if (newStatus == TicketStatus.RESOLVED && request.resolutionNotes() != null) {
            ticket.setResolutionNotes(request.resolutionNotes());
        }

        ticket.setStatus(newStatus);
        ticketRepository.save(ticket);

        notificationService.createSystemNotification(
            ticket.getReporterEmail(),
            "Ticket #" + ticket.getId() + " status updated to " + newStatus + ".");

        if (ticket.getAssignedTechnicianEmail() != null && !ticket.getAssignedTechnicianEmail().isBlank()) {
            notificationService.createSystemNotification(
                ticket.getAssignedTechnicianEmail(),
                "Ticket #" + ticket.getId() + " status is now " + newStatus + ".");
        }

        return getTicketById(ticketId, currentUserEmail, isAdmin);
    }

    @Transactional
    public TicketResponse assignTechnician(Long ticketId, AssignTechnicianRequest request, String currentUserEmail) {
        IncidentTicket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        ticket.setAssignedTechnicianEmail(request.technicianEmail().trim().toLowerCase());
        ticketRepository.save(ticket);

        notificationService.createSystemNotification(
            ticket.getAssignedTechnicianEmail(),
            "You have been assigned to Ticket #" + ticket.getId() + ": " + ticket.getTitle());
        notificationService.createSystemNotification(
            ticket.getReporterEmail(),
            "A technician has been assigned to your Ticket #" + ticket.getId() + ".");

        return getTicketById(ticketId, currentUserEmail, true);
    }

    @Transactional
    public TicketResponse updateTicket(Long ticketId, CreateTicketRequest request, String currentUserEmail, boolean isAdmin) {
        IncidentTicket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        if (!isAdmin && !ticket.getReporterEmail().equalsIgnoreCase(currentUserEmail)) {
            throw new IllegalArgumentException("You can only edit your own tickets");
        }

        if (!isAdmin && ticket.getStatus() != TicketStatus.OPEN) {
            throw new IllegalArgumentException("You can only edit tickets that are in OPEN status");
        }

        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setCategory(TicketCategory.valueOf(request.category().toUpperCase()));
        ticket.setPriority(TicketPriority.valueOf(request.priority().toUpperCase()));
        ticket.setResourceLocation(request.resourceLocation());
        ticket.setContactEmail(request.contactEmail());
        ticket.setContactPhone(request.contactPhone());

        ticketRepository.save(ticket);
        
        // Handle attachments: If new ones are provided, we could append or replace.
        // For now, let's allow adding new ones if under the limit of 3.
        if (request.attachments() != null && !request.attachments().isEmpty()) {
            List<TicketAttachment> existing = attachmentRepository.findByTicketId(ticketId);
            if (existing.size() + request.attachments().size() > 3) {
                throw new IllegalArgumentException("Total attachments cannot exceed 3");
            }
            for (CreateTicketRequest.AttachmentData att : request.attachments()) {
                if (att.dataUrl() != null && !att.dataUrl().isBlank()) {
                    attachmentRepository.save(new TicketAttachment(ticketId, att.dataUrl(), att.fileName()));
                }
            }
        }

        return getTicketById(ticketId, currentUserEmail, isAdmin);
    }

    @Transactional
    public void deleteTicket(Long ticketId, String currentUserEmail, boolean isAdmin) {
        IncidentTicket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        if (!isAdmin) {
            if (!ticket.getReporterEmail().equalsIgnoreCase(currentUserEmail)) {
                throw new IllegalArgumentException("You can only delete your own tickets");
            }
            if (ticket.getStatus() != TicketStatus.OPEN) {
                throw new IllegalArgumentException("You can only delete tickets that are in OPEN status");
            }
        }

        attachmentRepository.deleteByTicketId(ticketId);
        commentRepository.deleteByTicketId(ticketId);
        ticketRepository.deleteById(ticketId);
    }

    @Transactional
    public CommentResponse addComment(Long ticketId, AddCommentRequest request, String commenterEmail) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new IllegalArgumentException("Ticket not found");
        }

        TicketComment comment = new TicketComment(ticketId, commenterEmail, request.content());
        comment = commentRepository.save(comment);

        IncidentTicket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        if (!ticket.getReporterEmail().equalsIgnoreCase(commenterEmail)) {
            notificationService.createSystemNotification(
                ticket.getReporterEmail(),
                "New comment added on your Ticket #" + ticketId + ".");
        }

        if (ticket.getAssignedTechnicianEmail() != null
            && !ticket.getAssignedTechnicianEmail().isBlank()
            && !ticket.getAssignedTechnicianEmail().equalsIgnoreCase(commenterEmail)) {
            notificationService.createSystemNotification(
                ticket.getAssignedTechnicianEmail(),
                "New comment added on assigned Ticket #" + ticketId + ".");
        }

        return new CommentResponse(
            comment.getId(),
            comment.getTicketId(),
            comment.getCommenterEmail(),
            comment.getContent(),
            comment.getCreatedAt(),
            comment.getUpdatedAt(),
            true
        );
    }

    @Transactional
    public CommentResponse updateComment(Long ticketId, Long commentId,
                                         UpdateCommentRequest request, String currentUserEmail) {
        TicketComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getTicketId().equals(ticketId)) {
            throw new IllegalArgumentException("Comment does not belong to this ticket");
        }

        if (!comment.getCommenterEmail().equalsIgnoreCase(currentUserEmail)) {
            throw new IllegalArgumentException("You can only edit your own comments");
        }

        comment.setContent(request.content());
        comment = commentRepository.save(comment);

        return new CommentResponse(
            comment.getId(),
            comment.getTicketId(),
            comment.getCommenterEmail(),
            comment.getContent(),
            comment.getCreatedAt(),
            comment.getUpdatedAt(),
            true
        );
    }

    @Transactional
    public void deleteComment(Long ticketId, Long commentId, String currentUserEmail, boolean isAdmin) {
        TicketComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getTicketId().equals(ticketId)) {
            throw new IllegalArgumentException("Comment does not belong to this ticket");
        }

        if (!isAdmin && !comment.getCommenterEmail().equalsIgnoreCase(currentUserEmail)) {
            throw new IllegalArgumentException("You can only delete your own comments");
        }

        commentRepository.deleteById(commentId);
    }

    private void validateStatusTransition(TicketStatus current, TicketStatus next, boolean isAdmin) {
        if (current == next) {
            return;
        }

        boolean valid = switch (current) {
            case OPEN -> next == TicketStatus.IN_PROGRESS || (isAdmin && next == TicketStatus.REJECTED);
            case IN_PROGRESS -> next == TicketStatus.RESOLVED || (isAdmin && next == TicketStatus.REJECTED);
            case RESOLVED -> next == TicketStatus.CLOSED || next == TicketStatus.IN_PROGRESS;
            case CLOSED, REJECTED -> false;
        };

        if (!valid) {
            throw new IllegalArgumentException(
                "Cannot transition from " + current + " to " + next);
        }
    }

    private TicketResponse mapToTicketResponse(IncidentTicket ticket,
                                                List<TicketAttachment> attachments,
                                                List<TicketComment> comments,
                                                String currentUserEmail) {
        List<TicketResponse.AttachmentResponse> attResponses = attachments.stream()
            .map(a -> new TicketResponse.AttachmentResponse(a.getId(), a.getDataUrl(), a.getFileName(), a.getCreatedAt()))
            .toList();

        List<CommentResponse> commentResponses = comments.stream()
            .map(c -> new CommentResponse(
                c.getId(),
                c.getTicketId(),
                c.getCommenterEmail(),
                c.getContent(),
                c.getCreatedAt(),
                c.getUpdatedAt(),
                c.getCommenterEmail().equalsIgnoreCase(currentUserEmail)
            ))
            .toList();

        return new TicketResponse(
            ticket.getId(),
            ticket.getTitle(),
            ticket.getDescription(),
            ticket.getCategory().name(),
            ticket.getPriority().name(),
            ticket.getStatus().name(),
            ticket.getResourceLocation(),
            ticket.getContactEmail(),
            ticket.getContactPhone(),
            ticket.getReporterEmail(),
            ticket.getAssignedTechnicianEmail(),
            ticket.getRejectionReason(),
            ticket.getResolutionNotes(),
            ticket.getCreatedAt(),
            ticket.getUpdatedAt(),
            attResponses,
            commentResponses
        );
    }

    private TicketSummaryResponse mapToSummary(IncidentTicket ticket) {
        int commentCount = commentRepository.countByTicketId(ticket.getId());
        int attachmentCount = attachmentRepository.countByTicketId(ticket.getId());

        return new TicketSummaryResponse(
            ticket.getId(),
            ticket.getTitle(),
            ticket.getCategory().name(),
            ticket.getPriority().name(),
            ticket.getStatus().name(),
            ticket.getResourceLocation(),
            ticket.getReporterEmail(),
            ticket.getAssignedTechnicianEmail(),
            commentCount,
            attachmentCount,
            ticket.getCreatedAt(),
            ticket.getUpdatedAt()
        );
    }
}
