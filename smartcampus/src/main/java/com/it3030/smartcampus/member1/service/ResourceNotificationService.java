package com.it3030.smartcampus.member1.service;

import com.it3030.smartcampus.member1.model.ResourceNotification;
import com.it3030.smartcampus.member1.model.EnhancedResource;
import com.it3030.smartcampus.member1.repository.ResourceNotificationRepository;
import com.it3030.smartcampus.member1.repository.EnhancedResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ResourceNotificationService {

    @Autowired
    private ResourceNotificationRepository notificationRepository;

    @Autowired
    private EnhancedResourceRepository resourceRepository;

    public List<ResourceNotification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Optional<ResourceNotification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    public List<ResourceNotification> getNotificationsByUser(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<ResourceNotification> getNotificationsByResource(Long resourceId) {
        return notificationRepository.findNotificationsByResourceId(resourceId);
    }

    public ResourceNotification createNotification(ResourceNotification notification) {
        // Validate notification data
        if (notification.getUserId() == null || notification.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (notification.getTitle() == null || notification.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }

        if (notification.getMessage() == null || notification.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Message is required");
        }

        // Validate resource if provided
        if (notification.getResource() != null) {
            EnhancedResource resource = resourceRepository.findById(notification.getResource().getId())
                    .orElseThrow(() -> new RuntimeException("Resource not found with id: " + notification.getResource().getId()));
            notification.setResource(resource);
        }

        // Set default values
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }
        if (notification.getUpdatedAt() == null) {
            notification.setUpdatedAt(LocalDateTime.now());
        }
        if (notification.getStatus() == null) {
            notification.setStatus(ResourceNotification.NotificationStatus.UNREAD);
        }
        if (notification.getDeliveryStatus() == null) {
            notification.setDeliveryStatus(ResourceNotification.DeliveryStatus.PENDING);
        }

        return notificationRepository.save(notification);
    }

    public ResourceNotification updateNotification(Long id, ResourceNotification notificationDetails) {
        ResourceNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        // Validate notification data
        if (notificationDetails.getTitle() != null && notificationDetails.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        if (notificationDetails.getMessage() != null && notificationDetails.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        // Update fields
        if (notificationDetails.getTitle() != null) {
            notification.setTitle(notificationDetails.getTitle());
        }
        if (notificationDetails.getMessage() != null) {
            notification.setMessage(notificationDetails.getMessage());
        }
        if (notificationDetails.getPriorityLevel() != null) {
            notification.setPriorityLevel(notificationDetails.getPriorityLevel());
        }
        if (notificationDetails.getDeliveryMethod() != null) {
            notification.setDeliveryMethod(notificationDetails.getDeliveryMethod());
        }
        if (notificationDetails.getScheduledAt() != null) {
            notification.setScheduledAt(notificationDetails.getScheduledAt());
        }
        if (notificationDetails.getExpiresAt() != null) {
            notification.setExpiresAt(notificationDetails.getExpiresAt());
        }
        if (notificationDetails.getActionRequired() != null) {
            notification.setActionRequired(notificationDetails.getActionRequired());
        }
        if (notificationDetails.getActionUrl() != null) {
            notification.setActionUrl(notificationDetails.getActionUrl());
        }
        if (notificationDetails.getActionButtonText() != null) {
            notification.setActionButtonText(notificationDetails.getActionButtonText());
        }
        if (notificationDetails.getMetadata() != null) {
            notification.setMetadata(notificationDetails.getMetadata());
        }

        notification.setUpdatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    public void deleteNotification(Long id) {
        ResourceNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        notificationRepository.delete(notification);
    }

    public ResourceNotification markAsRead(Long id) {
        ResourceNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        notification.markAsRead();
        return notificationRepository.save(notification);
    }

    public ResourceNotification markAsUnread(Long id) {
        ResourceNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        notification.setStatus(ResourceNotification.NotificationStatus.UNREAD);
        notification.setReadAt(null);
        notification.setUpdatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    public ResourceNotification archiveNotification(Long id) {
        ResourceNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        notification.setStatus(ResourceNotification.NotificationStatus.ARCHIVED);
        notification.setUpdatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    public void markAllAsReadForUser(String userId) {
        List<ResourceNotification> unreadNotifications = notificationRepository.findByUserIdAndStatus(
                userId, ResourceNotification.NotificationStatus.UNREAD);

        for (ResourceNotification notification : unreadNotifications) {
            notification.markAsRead();
            notificationRepository.save(notification);
        }
    }

    public void deleteAllReadNotificationsForUser(String userId) {
        List<ResourceNotification> readNotifications = notificationRepository.findByUserIdAndStatus(
                userId, ResourceNotification.NotificationStatus.READ);

        for (ResourceNotification notification : readNotifications) {
            notificationRepository.delete(notification);
        }
    }

    public List<ResourceNotification> getUnreadNotificationsForUser(String userId) {
        return notificationRepository.findUnreadNotificationsForUser(userId);
    }

    public long countUnreadNotificationsForUser(String userId) {
        return notificationRepository.countUnreadNotificationsForUser(userId);
    }

    public long countHighPriorityUnreadNotificationsForUser(String userId) {
        return notificationRepository.countHighPriorityUnreadNotificationsForUser(userId);
    }

    public long countUnreadActionRequiredNotificationsForUser(String userId) {
        return notificationRepository.countUnreadActionRequiredNotificationsForUser(userId);
    }

    public List<ResourceNotification> getNotificationsByStatus(String userId, ResourceNotification.NotificationStatus status) {
        return notificationRepository.findByUserIdAndStatus(userId, status);
    }

    public List<ResourceNotification> getNotificationsByPriorityLevel(String userId, ResourceNotification.PriorityLevel priorityLevel) {
        return notificationRepository.findByUserIdAndPriorityLevel(userId, priorityLevel);
    }

    public List<ResourceNotification> getNotificationsByType(String userId, ResourceNotification.NotificationType type) {
        return notificationRepository.findByUserIdAndType(userId, type);
    }

    public List<ResourceNotification> getNotificationsByDeliveryMethod(String userId, ResourceNotification.DeliveryMethod method) {
        return notificationRepository.findByUserIdAndDeliveryMethod(userId, method);
    }

    public List<ResourceNotification> getHighPriorityNotifications() {
        return notificationRepository.findByUserIdAndPriorityLevel(null, ResourceNotification.PriorityLevel.HIGH);
    }

    public List<ResourceNotification> getUrgentNotifications() {
        return notificationRepository.findByUserIdAndPriorityLevel(null, ResourceNotification.PriorityLevel.URGENT);
    }

    public List<ResourceNotification> getCriticalNotifications() {
        return notificationRepository.findByUserIdAndPriorityLevel(null, ResourceNotification.PriorityLevel.CRITICAL);
    }

    public ResourceNotification createBookingNotification(String userId, Long resourceId, String bookingTitle, ResourceNotification.NotificationType type) {
        ResourceNotification notification = new ResourceNotification();
        notification.setUserId(userId);
        notification.setNotificationType(type);
        notification.setPriorityLevel(ResourceNotification.PriorityLevel.NORMAL);
        notification.setDeliveryMethod(ResourceNotification.DeliveryMethod.IN_APP);
        notification.setRelatedBookingId(null); // Will be set when booking is created
        // Resource will be set in the controller

        // Set title and message based on type
        switch (type) {
            case BOOKING_CONFIRMED:
                notification.setTitle("Booking Confirmed");
                notification.setMessage("Your booking '" + bookingTitle + "' has been confirmed.");
                break;
            case BOOKING_REMINDER:
                notification.setTitle("Booking Reminder");
                notification.setMessage("Reminder: You have a booking '" + bookingTitle + "' coming up soon.");
                notification.setPriorityLevel(ResourceNotification.PriorityLevel.HIGH);
                break;
            case BOOKING_CANCELLED:
                notification.setTitle("Booking Cancelled");
                notification.setMessage("Your booking '" + bookingTitle + "' has been cancelled.");
                break;
            case BOOKING_APPROVED:
                notification.setTitle("Booking Approved");
                notification.setMessage("Your booking '" + bookingTitle + "' has been approved.");
                break;
            case BOOKING_REJECTED:
                notification.setTitle("Booking Rejected");
                notification.setMessage("Your booking '" + bookingTitle + "' has been rejected.");
                notification.setPriorityLevel(ResourceNotification.PriorityLevel.HIGH);
                break;
            default:
                notification.setTitle("Booking Update");
                notification.setMessage("Your booking '" + bookingTitle + "' has been updated.");
                break;
        }

        return createNotification(notification);
    }

    public ResourceNotification createMaintenanceNotification(String userId, Long resourceId, String resourceName, ResourceNotification.NotificationType type) {
        ResourceNotification notification = new ResourceNotification();
        notification.setUserId(userId);
        notification.setNotificationType(type);
        notification.setPriorityLevel(ResourceNotification.PriorityLevel.NORMAL);
        notification.setDeliveryMethod(ResourceNotification.DeliveryMethod.IN_APP);
        notification.setRelatedMaintenanceId(null); // Will be set when maintenance is created
        // Resource will be set in the controller

        // Set title and message based on type
        switch (type) {
            case MAINTENANCE_SCHEDULED:
                notification.setTitle("Maintenance Scheduled");
                notification.setMessage("Maintenance has been scheduled for '" + resourceName + "'.");
                break;
            case MAINTENANCE_COMPLETED:
                notification.setTitle("Maintenance Completed");
                notification.setMessage("Maintenance has been completed for '" + resourceName + "'.");
                break;
            case MAINTENANCE_OVERDUE:
                notification.setTitle("Maintenance Overdue");
                notification.setMessage("Maintenance is overdue for '" + resourceName + "'.");
                notification.setPriorityLevel(ResourceNotification.PriorityLevel.HIGH);
                break;
            case EQUIPMENT_ISSUE:
                notification.setTitle("Equipment Issue");
                notification.setMessage("There is an equipment issue with '" + resourceName + "'.");
                notification.setPriorityLevel(ResourceNotification.PriorityLevel.URGENT);
                break;
            default:
                notification.setTitle("Maintenance Update");
                notification.setMessage("There is a maintenance update for '" + resourceName + "'.");
                break;
        }

        return createNotification(notification);
    }

    public ResourceNotification createPaymentNotification(String userId, String resourceName, Double amount, ResourceNotification.NotificationType type) {
        ResourceNotification notification = new ResourceNotification();
        notification.setUserId(userId);
        notification.setNotificationType(type);
        notification.setPriorityLevel(ResourceNotification.PriorityLevel.NORMAL);
        notification.setDeliveryMethod(ResourceNotification.DeliveryMethod.IN_APP);

        // Set title and message based on type
        switch (type) {
            case PAYMENT_DUE:
                notification.setTitle("Payment Due");
                notification.setMessage("Payment of $" + amount + " is due for '" + resourceName + "'.");
                notification.setPriorityLevel(ResourceNotification.PriorityLevel.HIGH);
                break;
            case PAYMENT_OVERDUE:
                notification.setTitle("Payment Overdue");
                notification.setMessage("Payment of $" + amount + " is overdue for '" + resourceName + "'.");
                notification.setPriorityLevel(ResourceNotification.PriorityLevel.URGENT);
                break;
            case PAYMENT_CONFIRMED:
                notification.setTitle("Payment Confirmed");
                notification.setMessage("Payment of $" + amount + " has been confirmed for '" + resourceName + "'.");
                break;
            case REFUND_PROCESSED:
                notification.setTitle("Refund Processed");
                notification.setMessage("A refund has been processed for '" + resourceName + "'.");
                break;
            default:
                notification.setTitle("Payment Update");
                notification.setMessage("There is a payment update for '" + resourceName + "'.");
                break;
        }

        return createNotification(notification);
    }

    public ResourceNotification createSystemNotification(String userId, String title, String message, ResourceNotification.PriorityLevel priority) {
        ResourceNotification notification = new ResourceNotification();
        notification.setUserId(userId);
        notification.setNotificationType(ResourceNotification.NotificationType.SYSTEM_ALERT);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setPriorityLevel(priority);
        notification.setDeliveryMethod(ResourceNotification.DeliveryMethod.IN_APP);

        return createNotification(notification);
    }

    public List<ResourceNotification> getScheduledNotificationsToSend() {
        return notificationRepository.findScheduledNotificationsToSend(LocalDateTime.now());
    }

    public List<ResourceNotification> getExpiredNotifications() {
        return notificationRepository.findExpiredNotifications(LocalDateTime.now());
    }

    public List<ResourceNotification> getNotificationsToRetry() {
        return notificationRepository.findNotificationsToRetry();
    }

    public ResourceNotification markAsSent(Long id) {
        ResourceNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        notification.markAsSent();
        return notificationRepository.save(notification);
    }

    public ResourceNotification markAsDelivered(Long id) {
        ResourceNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        notification.markAsDelivered();
        return notificationRepository.save(notification);
    }

    public ResourceNotification markAsFailed(Long id, String errorMessage) {
        ResourceNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        notification.markAsFailed(errorMessage);
        return notificationRepository.save(notification);
    }

    public void processScheduledNotifications() {
        List<ResourceNotification> scheduledNotifications = getScheduledNotificationsToSend();
        
        for (ResourceNotification notification : scheduledNotifications) {
            try {
                // Mark as sent (in a real system, this would trigger actual delivery)
                markAsSent(notification.getId());
                markAsDelivered(notification.getId());
            } catch (Exception e) {
                markAsFailed(notification.getId(), e.getMessage());
            }
        }
    }

    public void processExpiredNotifications() {
        List<ResourceNotification> expiredNotifications = getExpiredNotifications();
        
        for (ResourceNotification notification : expiredNotifications) {
            // Archive expired notifications
            archiveNotification(notification.getId());
        }
    }

    public void retryFailedNotifications() {
        List<ResourceNotification> failedNotifications = getNotificationsToRetry();
        
        for (ResourceNotification notification : failedNotifications) {
            try {
                // Retry delivery (in a real system, this would trigger actual delivery)
                markAsSent(notification.getId());
                markAsDelivered(notification.getId());
            } catch (Exception e) {
                markAsFailed(notification.getId(), e.getMessage());
            }
        }
    }

    public List<ResourceNotification> getUnreadActionRequiredNotifications() {
        return notificationRepository.findUnreadActionRequiredNotifications();
    }

    public List<ResourceNotification> getUnreadActionRequiredNotificationsForUser(String userId) {
        return notificationRepository.findUnreadActionRequiredNotificationsForUser(userId);
    }

    public List<ResourceNotification> searchNotifications(String userId, String searchTerm) {
        return notificationRepository.searchNotificationsForUser(userId, searchTerm);
    }

    public List<ResourceNotification> getNotificationsForUserInDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return notificationRepository.findNotificationsForUserInDateRange(userId, startDate, endDate);
    }

    public List<ResourceNotification> getNotificationsForResourceInDateRange(Long resourceId, LocalDateTime startDate, LocalDateTime endDate) {
        return notificationRepository.findNotificationsForResourceInDateRange(resourceId, startDate, endDate);
    }

    public List<ResourceNotification> getRecentNotificationsForUser(String userId, LocalDateTime date) {
        return notificationRepository.findRecentNotificationsForUser(userId, date);
    }

    public List<ResourceNotification> getActiveNotificationsForUser(String userId) {
        return notificationRepository.findActiveNotificationsForUser(userId, LocalDateTime.now());
    }

    public List<ResourceNotification> getArchivedNotificationsForUser(String userId) {
        return notificationRepository.findArchivedNotificationsForUser(userId);
    }

    public long countNotificationsSince(LocalDateTime date) {
        return notificationRepository.countNotificationsSince(date);
    }

    public long countNotificationsForUserSince(String userId, LocalDateTime date) {
        return notificationRepository.countNotificationsForUserSince(userId, date);
    }

    public List<ResourceNotification> getNotificationsByPriorityLevels(String userId, List<ResourceNotification.PriorityLevel> priorityLevels) {
        return notificationRepository.findByUserIdAndPriorityLevels(userId, priorityLevels);
    }

    public List<ResourceNotification> getNotificationsByTypes(String userId, List<ResourceNotification.NotificationType> types) {
        return notificationRepository.findByUserIdAndTypes(userId, types);
    }

    public List<ResourceNotification> getNotificationsForResourceAndUser(Long resourceId, String userId) {
        return notificationRepository.findNotificationsForResourceAndUser(resourceId, userId);
    }

    public List<ResourceNotification> getUnreadNotificationsForResourceAndUser(Long resourceId, String userId) {
        return notificationRepository.findUnreadNotificationsForResourceAndUser(resourceId, userId);
    }

    public List<ResourceNotification> getDeliveredButUnreadNotifications() {
        return notificationRepository.findDeliveredButUnreadNotifications();
    }

    public ResourceNotification createCustomNotification(ResourceNotification notification) {
        return createNotification(notification);
    }

    public void bulkCreateNotifications(List<ResourceNotification> notifications) {
        for (ResourceNotification notification : notifications) {
            createNotification(notification);
        }
    }

    public void sendBulkNotification(List<String> userIds, String title, String message, ResourceNotification.NotificationType type, ResourceNotification.PriorityLevel priority) {
        for (String userId : userIds) {
            ResourceNotification notification = new ResourceNotification();
            notification.setUserId(userId);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setNotificationType(type);
            notification.setPriorityLevel(priority);
            notification.setDeliveryMethod(ResourceNotification.DeliveryMethod.IN_APP);
            
            createNotification(notification);
        }
    }

    private void setRelatedResourceId(ResourceNotification notification, Long resourceId) {
        if (resourceId != null) {
            EnhancedResource resource = resourceRepository.findById(resourceId)
                    .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));
            notification.setResource(resource);
        }
    }

    public void seedDefaultNotifications() {
        if (notificationRepository.count() == 0) {
            // This would be populated with default notifications for testing
            // Implementation would depend on the existing users and resources
        }
    }
}
