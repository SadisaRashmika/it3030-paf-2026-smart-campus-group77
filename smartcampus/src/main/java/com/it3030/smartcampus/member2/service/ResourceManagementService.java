package com.it3030.smartcampus.member2.service;

import com.it3030.smartcampus.member2.dto.ApproveRejectRequest;
import com.it3030.smartcampus.member2.dto.ResourceManagementResponse;
import com.it3030.smartcampus.member2.dto.CreateResourceRequest;
import com.it3030.smartcampus.member2.exception.ResourceConflictException;
import com.it3030.smartcampus.member2.model.ResourceManagement;
import com.it3030.smartcampus.member2.model.ResourceStatus;
import com.it3030.smartcampus.member2.model.Resource;
import com.it3030.smartcampus.member2.repository.ResourceManagementRepository;
import com.it3030.smartcampus.member4.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ResourceManagementService {

    @Autowired
    private ResourceManagementRepository resourceManagementRepository;

    @Autowired
    private ResourceService resourceService;

    @Transactional
    public ResourceManagementResponse createResource(CreateResourceRequest request, UserAccount user) {
        // 1. Validate time range
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot book in the past");
        }

        // 2. Check for conflicts (Only against APPROVED bookings)
        List<Booking> conflicts = bookingRepository.findOverlappingApprovedBookings(
                request.getResourceId(), request.getStartTime(), request.getEndTime());

        if (!conflicts.isEmpty()) {
            throw new ResourceConflictException("Resource is already booked for this time period.");
        }

        // 3. Save booking
        Resource resource = resourceService.getResourceById(request.getResourceId());

        ResourceManagement resourceManagement = new ResourceManagement();
        resourceManagement.setUser(user);
        resourceManagement.setResource(resource);
        resourceManagement.setStartTime(request.getStartTime());
        resourceManagement.setEndTime(request.getEndTime());
        resourceManagement.setPurpose(request.getPurpose());
        resourceManagement.setExpectedAttendees(request.getExpectedAttendees());
        resourceManagement.setStatus(ResourceStatus.PENDING);

        ResourceManagement saved = resourceManagementRepository.save(resourceManagement);
        return mapToResponse(saved);
    }

    public List<ResourceManagementResponse> getMyResources(UserAccount user) {
        return resourceManagementRepository.findByUserOrderByStartTimeDesc(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResourceManagementResponse> getPendingResources() {
        System.out.println("SERVICE: Fetching pending bookings...");
        return resourceManagementRepository.findByStatusOrderByStartTimeAsc(ResourceStatus.PENDING).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ResourceManagementResponse> getWeeklyApprovedResources(LocalDateTime start, LocalDateTime end) {
        return resourceManagementRepository.findAllApprovedInWeek(start, end).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ResourceManagementResponse approveResource(Long id) {
        ResourceManagement resourceManagement = getResourceManagementById(id);

        // Re-check conflict at approval time (in case another was approved in between)
        List<ResourceManagement> conflicts = resourceManagementRepository.findOverlappingApprovedResources(
                resourceManagement.getResource().getId(), resourceManagement.getStartTime(), resourceManagement.getEndTime());

        if (!conflicts.isEmpty()) {
            throw new ResourceConflictException(
                    "Cannot approve: Resource now has a conflict with another approved booking.");
        }

        resourceManagement.setStatus(ResourceStatus.APPROVED);
        resourceManagement.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(resourceManagementRepository.save(resourceManagement));
    }

    @Transactional
    public ResourceManagementResponse rejectResource(Long id, ApproveRejectRequest request) {
        ResourceManagement resourceManagement = getResourceManagementById(id);
        resourceManagement.setStatus(ResourceStatus.REJECTED);
        resourceManagement.setRejectionReason(request.getRejectionReason());
        resourceManagement.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(resourceManagementRepository.save(resourceManagement));
    }

    @Transactional
    public ResourceManagementResponse cancelResource(Long id, UserAccount user) {
        ResourceManagement resourceManagement = getResourceManagementById(id);

        // Security check: Only owner can cancel
        if (!resourceManagement.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You are not authorized to cancel this booking.");
        }

        resourceManagement.setStatus(ResourceStatus.CANCELLED);
        resourceManagement.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(resourceManagementRepository.save(resourceManagement));
    }

    private ResourceManagement getResourceManagementById(@NonNull Long id) {
        Long resourceId = Objects.requireNonNull(id, "id must not be null");
        return resourceManagementRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found with id: " + id));
    }

    private ResourceManagementResponse mapToResponse(ResourceManagement resourceManagement) {
        ResourceManagementResponse res = new ResourceManagementResponse();
        res.setId(resourceManagement.getId());

        if (resourceManagement.getResource() != null) {
            res.setResourceId(resourceManagement.getResource().getId());
            res.setResourceName(resourceManagement.getResource().getName());
            res.setResourceType(resourceManagement.getResource().getType());
        } else {
            res.setResourceName("Unknown Resource");
        }

        if (resourceManagement.getUser() != null) {
            res.setUserName(resourceManagement.getUser().getName());
        } else {
            res.setUserName("Unknown User");
        }

        res.setStartTime(resourceManagement.getStartTime());
        res.setEndTime(resourceManagement.getEndTime());
        res.setStatus(resourceManagement.getStatus());
        res.setPurpose(resourceManagement.getPurpose());
        res.setExpectedAttendees(resourceManagement.getExpectedAttendees());
        res.setRejectionReason(resourceManagement.getRejectionReason());
        return res;
    }
}
