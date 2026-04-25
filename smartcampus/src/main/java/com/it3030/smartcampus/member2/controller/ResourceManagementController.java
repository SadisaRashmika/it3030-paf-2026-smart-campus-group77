package com.it3030.smartcampus.member2.controller;

import com.it3030.smartcampus.member2.dto.ApproveRejectRequest;
import com.it3030.smartcampus.member2.dto.ResourceManagementResponse;
import com.it3030.smartcampus.member2.dto.CreateResourceRequest;
import com.it3030.smartcampus.member2.exception.ResourceConflictException;
import com.it3030.smartcampus.member2.service.ResourceManagementService;
import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/member2/resources")
public class ResourceManagementController {

    @Autowired
    private ResourceManagementService resourceManagementService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'LECTURER', 'ADMIN')")
    public ResponseEntity<?> createResource(@RequestBody CreateResourceRequest request) {
        try {
            UserAccount currentUser = getCurrentUser();
            ResourceManagementResponse response = resourceManagementService.createResource(request, currentUser);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (ResourceConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mine")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceManagementResponse> getMyResources() {
        return resourceManagementService.getMyResources(getCurrentUser());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('TIMETABLE_MANAGER', 'ADMIN')")
    public List<ResourceManagementResponse> getPendingResources() {
        return resourceManagementService.getPendingResources();
    }

    @GetMapping("/weekly")
    @PreAuthorize("hasAnyRole('STUDENT', 'LECTURER', 'TIMETABLE_MANAGER', 'ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceManagementResponse> getWeeklyResources(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return resourceManagementService.getWeeklyApprovedResources(start, end);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('TIMETABLE_MANAGER', 'ADMIN')")
    public ResponseEntity<?> approveResource(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(resourceManagementService.approveResource(id));
        } catch (ResourceConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('TIMETABLE_MANAGER', 'ADMIN')")
    public ResponseEntity<?> rejectResource(@PathVariable Long id, @RequestBody ApproveRejectRequest request) {
        return ResponseEntity.ok(resourceManagementService.rejectResource(id, request));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancelResource(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(resourceManagementService.cancelResource(id, getCurrentUser()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    private UserAccount getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            throw new IllegalStateException("Not authenticated");
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Current user not found in database"));
    }
}
