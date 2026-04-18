package com.it3030.smartcampus.member4.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.it3030.smartcampus.member4.dto.RecoveryRequestResponse;
import com.it3030.smartcampus.member4.dto.RecoveryRequestSubmissionRequest;
import com.it3030.smartcampus.member4.dto.RecoveryRequestSubmissionResponse;
import com.it3030.smartcampus.member4.model.RecoveryRequestStatus;
import com.it3030.smartcampus.member4.service.RecoveryRequestService;

@ExtendWith(MockitoExtension.class)
class RecoveryRequestControllerTest {

    @Mock
    private RecoveryRequestService recoveryRequestService;

    @Test
    void submitReturnsCreatedStatus() {
        RecoveryRequestController controller = new RecoveryRequestController(recoveryRequestService);
        RecoveryRequestSubmissionRequest request = new RecoveryRequestSubmissionRequest(
                "STU001",
                "student@campus.edu",
                "contact@campus.edu",
                "Cannot access account",
                "id-card.png",
                "image/png",
                "data:image/png;base64,aGVsbG8=");

        when(recoveryRequestService.submit(request)).thenReturn(new RecoveryRequestSubmissionResponse(10L, "Submitted"));

        ResponseEntity<RecoveryRequestSubmissionResponse> response = controller.submit(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(10L, response.getBody().id());
    }

    @Test
    void approveReturnsUpdatedRecoveryRequest() {
        RecoveryRequestController controller = new RecoveryRequestController(recoveryRequestService);
        RecoveryRequestResponse approvedResponse = new RecoveryRequestResponse(
                20L,
                "STU001",
                "student@campus.edu",
                "contact@campus.edu",
                "Cannot access account",
                "id-card.png",
                "image/png",
                "data:image/png;base64,aGVsbG8=",
                RecoveryRequestStatus.APPROVED,
                "Student One",
                "student@campus.edu",
                "STU001",
                "STUDENT",
                true,
                Instant.now(),
                Instant.now());

        when(recoveryRequestService.approve(20L)).thenReturn(approvedResponse);

        ResponseEntity<RecoveryRequestResponse> response = controller.approve(20L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(RecoveryRequestStatus.APPROVED, response.getBody().status());
    }
}
