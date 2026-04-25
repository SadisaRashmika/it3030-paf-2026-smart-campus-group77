package com.it3030.smartcampus.member2.service;

import com.it3030.smartcampus.member2.dto.ResourceManagementResponse;
import com.it3030.smartcampus.member2.dto.CreateResourceRequest;
import com.it3030.smartcampus.member2.exception.ResourceConflictException;
import com.it3030.smartcampus.member2.model.ResourceManagement;
import com.it3030.smartcampus.member2.model.Resource;
import com.it3030.smartcampus.member2.repository.ResourceManagementRepository;
import com.it3030.smartcampus.member4.model.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ResourceService resourceService;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createBooking_Successful() {
        // Arrange
        CreateBookingRequest request = new CreateBookingRequest();
        request.setResourceId(1L);
        request.setStartTime(LocalDateTime.now().plusHours(1));
        request.setEndTime(LocalDateTime.now().plusHours(2));
        request.setPurpose("Test Booking");

        UserAccount user = mock(UserAccount.class);
        when(user.getName()).thenReturn("Test User");
        when(user.getId()).thenReturn(101);

        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName("Lab A");

        when(bookingRepository.findOverlappingApprovedBookings(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(resourceService.getResourceById(1L)).thenReturn(resource);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0, Booking.class));

        // Act
        BookingResponse response = bookingService.createBooking(request, user);

        // Assert
        assertNotNull(response);
        assertEquals("Lab A", response.getResourceName());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    public void createBooking_ThrowsConflict() {
        // Arrange
        CreateBookingRequest request = new CreateBookingRequest();
        request.setResourceId(1L);
        request.setStartTime(LocalDateTime.now().plusHours(1));
        request.setEndTime(LocalDateTime.now().plusHours(2));

        when(bookingRepository.findOverlappingApprovedBookings(anyLong(), any(), any()))
                .thenReturn(List.of(new Booking()));

        // Act & Assert
        assertThrows(BookingConflictException.class, () -> {
            bookingService.createBooking(request, mock(UserAccount.class));
        });
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void createBooking_InvalidTimeRange() {
        // Arrange
        CreateBookingRequest request = new CreateBookingRequest();
        request.setResourceId(1L);
        request.setStartTime(LocalDateTime.now().plusHours(2));
        request.setEndTime(LocalDateTime.now().plusHours(1)); // Start after end

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(request, mock(UserAccount.class));
        });
    }
}
