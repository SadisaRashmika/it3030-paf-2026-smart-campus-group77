package com.it3030.smartcampus.member1.controller;

import com.it3030.smartcampus.member1.model.ResourceNotification;
import com.it3030.smartcampus.member1.service.ResourceNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/member1/notifications")
public class ResourceNotificationController {

    @Autowired
    private ResourceNotificationService notificationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceNotification> getNotificationById(@PathVariable Long id) {
        Optional<ResourceNotification> notification = notificationService.getNotificationById(id);
        return notification.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getNotificationsByUser(@PathVariable String userId) {
        return notificationService.getNotificationsByUser(userId);
    }

    @GetMapping("/resource/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getNotificationsByResource(@PathVariable Long resourceId) {
        return notificationService.getNotificationsByResource(resourceId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> createNotification(@RequestBody ResourceNotification notification) {
        try {
            ResourceNotification createdNotification = notificationService.createNotification(notification);
            return ResponseEntity.ok(createdNotification);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> updateNotification(@PathVariable Long id, @RequestBody ResourceNotification notificationDetails) {
        try {
            ResourceNotification updatedNotification = notificationService.updateNotification(id, notificationDetails);
            return ResponseEntity.ok(updatedNotification);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/mark-read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceNotification> markAsRead(@PathVariable Long id) {
        try {
            ResourceNotification notification = notificationService.markAsRead(id);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/mark-unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceNotification> markAsUnread(@PathVariable Long id) {
        try {
            ResourceNotification notification = notificationService.markAsUnread(id);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/archive")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceNotification> archiveNotification(@PathVariable Long id) {
        try {
            ResourceNotification notification = notificationService.archiveNotification(id);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/user/{userId}/mark-all-read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> markAllAsReadForUser(@PathVariable String userId) {
        notificationService.markAllAsReadForUser(userId);
        return ResponseEntity.ok("All notifications marked as read for user: " + userId);
    }

    @DeleteMapping("/user/{userId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteAllReadNotificationsForUser(@PathVariable String userId) {
        notificationService.deleteAllReadNotificationsForUser(userId);
        return ResponseEntity.ok("All read notifications deleted for user: " + userId);
    }

    @GetMapping("/user/{userId}/unread")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getUnreadNotificationsForUser(@PathVariable String userId) {
        return notificationService.getUnreadNotificationsForUser(userId);
    }

    @GetMapping("/user/{userId}/unread/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> countUnreadNotificationsForUser(@PathVariable String userId) {
        long count = notificationService.countUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/user/{userId}/unread/high-priority/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> countHighPriorityUnreadNotificationsForUser(@PathVariable String userId) {
        long count = notificationService.countHighPriorityUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/user/{userId}/unread/action-required/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> countUnreadActionRequiredNotificationsForUser(@PathVariable String userId) {
        long count = notificationService.countUnreadActionRequiredNotificationsForUser(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/user/{userId}/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getNotificationsByStatus(@PathVariable String userId, @PathVariable ResourceNotification.NotificationStatus status) {
        return notificationService.getNotificationsByStatus(userId, status);
    }

    @GetMapping("/user/{userId}/priority/{priorityLevel}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getNotificationsByPriorityLevel(@PathVariable String userId, @PathVariable ResourceNotification.PriorityLevel priorityLevel) {
        return notificationService.getNotificationsByPriorityLevel(userId, priorityLevel);
    }

    @GetMapping("/user/{userId}/type/{type}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getNotificationsByType(@PathVariable String userId, @PathVariable ResourceNotification.NotificationType type) {
        return notificationService.getNotificationsByType(userId, type);
    }

    @GetMapping("/user/{userId}/method/{method}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getNotificationsByDeliveryMethod(@PathVariable String userId, @PathVariable ResourceNotification.DeliveryMethod method) {
        return notificationService.getNotificationsByDeliveryMethod(userId, method);
    }

    @GetMapping("/high-priority")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceNotification> getHighPriorityNotifications() {
        return notificationService.getHighPriorityNotifications();
    }

    @GetMapping("/urgent")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceNotification> getUrgentNotifications() {
        return notificationService.getUrgentNotifications();
    }

    @GetMapping("/critical")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceNotification> getCriticalNotifications() {
        return notificationService.getCriticalNotifications();
    }

    @PostMapping("/booking")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> createBookingNotification(@RequestBody java.util.Map<String, Object> request) {
        try {
            String userId = request.get("userId").toString();
            Long resourceId = request.get("resourceId") != null ? Long.valueOf(request.get("resourceId").toString()) : null;
            String bookingTitle = request.get("bookingTitle").toString();
            ResourceNotification.NotificationType type = ResourceNotification.NotificationType.valueOf(request.get("type").toString());
            
            ResourceNotification notification = notificationService.createBookingNotification(userId, resourceId, bookingTitle, type);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/maintenance")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> createMaintenanceNotification(@RequestBody java.util.Map<String, Object> request) {
        try {
            String userId = request.get("userId").toString();
            Long resourceId = request.get("resourceId") != null ? Long.valueOf(request.get("resourceId").toString()) : null;
            String resourceName = request.get("resourceName").toString();
            ResourceNotification.NotificationType type = ResourceNotification.NotificationType.valueOf(request.get("type").toString());
            
            ResourceNotification notification = notificationService.createMaintenanceNotification(userId, resourceId, resourceName, type);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> createPaymentNotification(@RequestBody java.util.Map<String, Object> request) {
        try {
            String userId = request.get("userId").toString();
            String resourceName = request.get("resourceName").toString();
            Double amount = request.get("amount") != null ? Double.valueOf(request.get("amount").toString()) : null;
            ResourceNotification.NotificationType type = ResourceNotification.NotificationType.valueOf(request.get("type").toString());
            
            ResourceNotification notification = notificationService.createPaymentNotification(userId, resourceName, amount, type);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/system")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> createSystemNotification(@RequestBody java.util.Map<String, Object> request) {
        try {
            String userId = request.get("userId").toString();
            String title = request.get("title").toString();
            String message = request.get("message").toString();
            ResourceNotification.PriorityLevel priority = request.get("priority") != null ? 
                ResourceNotification.PriorityLevel.valueOf(request.get("priority").toString()) : ResourceNotification.PriorityLevel.NORMAL;
            
            ResourceNotification notification = notificationService.createSystemNotification(userId, title, message, priority);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/scheduled-to-send")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceNotification> getScheduledNotificationsToSend() {
        return notificationService.getScheduledNotificationsToSend();
    }

    @GetMapping("/expired")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceNotification> getExpiredNotifications() {
        return notificationService.getExpiredNotifications();
    }

    @GetMapping("/to-retry")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceNotification> getNotificationsToRetry() {
        return notificationService.getNotificationsToRetry();
    }

    @PatchMapping("/{id}/mark-sent")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<ResourceNotification> markAsSent(@PathVariable Long id) {
        try {
            ResourceNotification notification = notificationService.markAsSent(id);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/mark-delivered")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<ResourceNotification> markAsDelivered(@PathVariable Long id) {
        try {
            ResourceNotification notification = notificationService.markAsDelivered(id);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/mark-failed")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> markAsFailed(@PathVariable Long id, @RequestBody java.util.Map<String, String> request) {
        try {
            String errorMessage = request.get("errorMessage");
            ResourceNotification notification = notificationService.markAsFailed(id, errorMessage);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/process-scheduled")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> processScheduledNotifications() {
        notificationService.processScheduledNotifications();
        return ResponseEntity.ok("Scheduled notifications processed successfully");
    }

    @PostMapping("/process-expired")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> processExpiredNotifications() {
        notificationService.processExpiredNotifications();
        return ResponseEntity.ok("Expired notifications processed successfully");
    }

    @PostMapping("/retry-failed")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> retryFailedNotifications() {
        notificationService.retryFailedNotifications();
        return ResponseEntity.ok("Failed notifications retried successfully");
    }

    @GetMapping("/action-required")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getUnreadActionRequiredNotifications() {
        return notificationService.getUnreadActionRequiredNotifications();
    }

    @GetMapping("/user/{userId}/action-required")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getUnreadActionRequiredNotificationsForUser(@PathVariable String userId) {
        return notificationService.getUnreadActionRequiredNotificationsForUser(userId);
    }

    @GetMapping("/user/{userId}/search")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> searchNotifications(@PathVariable String userId, @RequestParam String searchTerm) {
        return notificationService.searchNotifications(userId, searchTerm);
    }

    @GetMapping("/user/{userId}/date-range")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getNotificationsForUserInDateRange(
            @PathVariable String userId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return notificationService.getNotificationsForUserInDateRange(userId, start, end);
    }

    @GetMapping("/resource/{resourceId}/date-range")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getNotificationsForResourceInDateRange(
            @PathVariable Long resourceId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return notificationService.getNotificationsForResourceInDateRange(resourceId, start, end);
    }

    @GetMapping("/user/{userId}/recent")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getRecentNotificationsForUser(@PathVariable String userId, @RequestParam String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        return notificationService.getRecentNotificationsForUser(userId, dateTime);
    }

    @GetMapping("/user/{userId}/active")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getActiveNotificationsForUser(@PathVariable String userId) {
        return notificationService.getActiveNotificationsForUser(userId);
    }

    @GetMapping("/user/{userId}/archived")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getArchivedNotificationsForUser(@PathVariable String userId) {
        return notificationService.getArchivedNotificationsForUser(userId);
    }

    @GetMapping("/count/since")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Long> countNotificationsSince(@RequestParam String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        long count = notificationService.countNotificationsSince(dateTime);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/user/{userId}/count/since")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> countNotificationsForUserSince(@PathVariable String userId, @RequestParam String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        long count = notificationService.countNotificationsForUserSince(userId, dateTime);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/user/{userId}/priorities")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getNotificationsByPriorityLevels(@PathVariable String userId, @RequestParam List<String> priorityLevels) {
        List<ResourceNotification.PriorityLevel> levels = priorityLevels.stream()
                .map(ResourceNotification.PriorityLevel::valueOf)
                .toList();
        return notificationService.getNotificationsByPriorityLevels(userId, levels);
    }

    @GetMapping("/user/{userId}/types")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getNotificationsByTypes(@PathVariable String userId, @RequestParam List<String> types) {
        List<ResourceNotification.NotificationType> notificationTypes = types.stream()
                .map(ResourceNotification.NotificationType::valueOf)
                .toList();
        return notificationService.getNotificationsByTypes(userId, notificationTypes);
    }

    @GetMapping("/resource/{resourceId}/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getNotificationsForResourceAndUser(@PathVariable Long resourceId, @PathVariable String userId) {
        return notificationService.getNotificationsForResourceAndUser(resourceId, userId);
    }

    @GetMapping("/resource/{resourceId}/user/{userId}/unread")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceNotification> getUnreadNotificationsForResourceAndUser(@PathVariable Long resourceId, @PathVariable String userId) {
        return notificationService.getUnreadNotificationsForResourceAndUser(resourceId, userId);
    }

    @GetMapping("/delivered-but-unread")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceNotification> getDeliveredButUnreadNotifications() {
        return notificationService.getDeliveredButUnreadNotifications();
    }

    @PostMapping("/custom")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> createCustomNotification(@RequestBody ResourceNotification notification) {
        try {
            ResourceNotification createdNotification = notificationService.createCustomNotification(notification);
            return ResponseEntity.ok(createdNotification);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> bulkCreateNotifications(@RequestBody java.util.Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> userIds = (List<String>) request.get("userIds");
            String title = request.get("title").toString();
            String message = request.get("message").toString();
            ResourceNotification.NotificationType type = ResourceNotification.NotificationType.valueOf(request.get("type").toString());
            ResourceNotification.PriorityLevel priority = request.get("priority") != null ? 
                ResourceNotification.PriorityLevel.valueOf(request.get("priority").toString()) : ResourceNotification.PriorityLevel.NORMAL;
            
            notificationService.sendBulkNotification(userIds, title, message, type, priority);
            return ResponseEntity.ok("Bulk notifications sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Object> getNotificationStatistics() {
        long totalSince = notificationService.countNotificationsSince(LocalDateTime.now().minusDays(30));
        
        return ResponseEntity.ok(java.util.Map.of(
            "totalLast30Days", totalSince
        ));
    }

    @PostMapping("/seed")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> seedDefaultNotifications() {
        notificationService.seedDefaultNotifications();
        return ResponseEntity.ok("Default notifications seeded successfully");
    }
}
