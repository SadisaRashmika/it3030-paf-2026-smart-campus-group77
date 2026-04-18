package com.it3030.smartcampus.member4.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.it3030.smartcampus.member4.dto.NotificationResponse;
import com.it3030.smartcampus.member4.dto.ReadAllNotificationsResponse;
import com.it3030.smartcampus.member4.service.NotificationService;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Test
    void myNotificationsReturnsListForAuthenticatedPrincipal() {
        NotificationController controller = new NotificationController(notificationService);
        Authentication authentication = new UsernamePasswordAuthenticationToken("student@campus.edu", "ignored");

        List<NotificationResponse> notifications = List.of(
                new NotificationResponse(1L, "STU001", "Login successful", false, Instant.now()));
        when(notificationService.myNotifications("student@campus.edu")).thenReturn(notifications);

        ResponseEntity<List<NotificationResponse>> response = controller.myNotifications(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("STU001", response.getBody().get(0).userId());
    }

    @Test
    void markAllReadReturnsUpdatedCount() {
        NotificationController controller = new NotificationController(notificationService);
        Authentication authentication = new UsernamePasswordAuthenticationToken("student@campus.edu", "ignored");
        when(notificationService.markAllRead("student@campus.edu")).thenReturn(3);

        ResponseEntity<ReadAllNotificationsResponse> response = controller.markAllRead(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().updatedCount());
    }
}
