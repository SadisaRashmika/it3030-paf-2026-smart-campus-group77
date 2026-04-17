package com.it3030.smartcampus.member4.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import com.it3030.smartcampus.member4.dto.AuthUserResponse;
import com.it3030.smartcampus.member4.dto.ProfilePictureUpdateRequest;
import com.it3030.smartcampus.member4.model.Role;
import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.UserRepository;
import com.it3030.smartcampus.member4.service.NotificationService;
import com.it3030.smartcampus.member4.service.PasswordResetService;

import org.mockito.Mock;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private NotificationService notificationService;

    @Test
    void meReturnsUnauthorizedWhenNotAuthenticated() {
        AuthController controller = new AuthController(
                authenticationManager,
                userRepository,
                passwordResetService,
                notificationService);

        ResponseEntity<AuthUserResponse> response = controller.me(null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void updateProfilePictureTrimsAndPersists() {
        AuthController controller = new AuthController(
                authenticationManager,
                userRepository,
                passwordResetService,
                notificationService);

        UserAccount user = UserAccount.activeUser("STU001", "student@campus.edu", "hash", Role.STUDENT);
        user.setName("Student One");

        when(userRepository.findByEmail("student@campus.edu")).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "student@campus.edu",
                "ignored",
                List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));

        ResponseEntity<AuthUserResponse> response = controller.updateProfilePicture(
                authentication,
                new ProfilePictureUpdateRequest("  data:image/png;base64,abc123  "));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("data:image/png;base64,abc123", response.getBody().profilePictureDataUrl());
        verify(userRepository).save(user);
    }

    @Test
    void updateProfilePictureRejectsUnauthenticated() {
        AuthController controller = new AuthController(
                authenticationManager,
                userRepository,
                passwordResetService,
                notificationService);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> controller.updateProfilePicture(null, new ProfilePictureUpdateRequest("data:image/png;base64,abc")));

        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
    }
}
