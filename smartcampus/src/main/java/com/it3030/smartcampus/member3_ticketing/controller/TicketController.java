package com.it3030.smartcampus.member3_ticketing.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import com.it3030.smartcampus.member3_ticketing.dto.*;
import com.it3030.smartcampus.member3_ticketing.service.TicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/member3/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest request,
                                                        Principal principal) {
        TicketResponse ticket = ticketService.createTicket(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }

    @GetMapping("/my")
    public ResponseEntity<List<TicketSummaryResponse>> getMyTickets(Principal principal) {
        return ResponseEntity.ok(ticketService.getMyTickets(principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id,
                                                         Authentication authentication) {
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        return ResponseEntity.ok(ticketService.getTicketById(id, authentication.getName(), isAdmin));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TicketSummaryResponse>> getAllTickets(Authentication authentication) {
        if (!hasRole(authentication, "ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<TicketSummaryResponse>> getAssignedTickets(Principal principal) {
        return ResponseEntity.ok(ticketService.getAssignedTickets(principal.getName()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateTicketStatus(@PathVariable Long id,
                                                              @Valid @RequestBody UpdateTicketStatusRequest request,
                                                              Authentication authentication) {
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        return ResponseEntity.ok(ticketService.updateTicketStatus(id, request, authentication.getName(), isAdmin));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<TicketResponse> assignTechnician(@PathVariable Long id,
                                                            @Valid @RequestBody AssignTechnicianRequest request,
                                                            Authentication authentication) {
        if (!hasRole(authentication, "ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(ticketService.assignTechnician(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TicketMessageResponse> deleteTicket(@PathVariable Long id,
                                                               Authentication authentication) {
        if (!hasRole(authentication, "ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ticketService.deleteTicket(id);
        return ResponseEntity.ok(new TicketMessageResponse("Ticket deleted successfully"));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long id,
                                                       @Valid @RequestBody AddCommentRequest request,
                                                       Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ticketService.addComment(id, request, principal.getName()));
    }

    @PutMapping("/{id}/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id,
                                                          @PathVariable Long commentId,
                                                          @Valid @RequestBody UpdateCommentRequest request,
                                                          Principal principal) {
        return ResponseEntity.ok(ticketService.updateComment(id, commentId, request, principal.getName()));
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<TicketMessageResponse> deleteComment(@PathVariable Long id,
                                                                @PathVariable Long commentId,
                                                                Authentication authentication) {
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        ticketService.deleteComment(id, commentId, authentication.getName(), isAdmin);
        return ResponseEntity.ok(new TicketMessageResponse("Comment deleted successfully"));
    }

    private boolean hasRole(Authentication authentication, String role) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(a -> a.equals(role));
    }
}
