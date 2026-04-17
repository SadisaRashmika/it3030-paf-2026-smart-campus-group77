package com.it3030.smartcampus.member2.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.it3030.smartcampus.member2.dto.TimetableRequest;
import com.it3030.smartcampus.member2.model.TimetableEntry;
import com.it3030.smartcampus.member2.service.TimetableService;
import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

	private final TimetableService timetableService;
	private final UserRepository userRepository;

	public TimetableController(TimetableService timetableService, UserRepository userRepository) {
		this.timetableService = timetableService;
		this.userRepository = userRepository;
	}

	/** GET all entries (full weekly grid) — any authenticated user */
	@GetMapping
	public List<TimetableEntry> getAllEntries() {
		return timetableService.getAllEntries();
	}

	/** GET today's entries — any authenticated user */
	@GetMapping("/today")
	public List<TimetableEntry> getTodayEntries() {
		return timetableService.getTodayEntries();
	}

	/** GET stats — TIMETABLE_MANAGER or ADMIN */
	@GetMapping("/stats")
	@PreAuthorize("hasAnyRole('ADMIN', 'TIMETABLE_MANAGER')")
	public Map<String, Object> getStats() {
		return timetableService.getStats();
	}

	/** POST create entry — TIMETABLE_MANAGER only */
	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'TIMETABLE_MANAGER')")
	public ResponseEntity<TimetableEntry> createEntry(
			@Valid @RequestBody TimetableRequest request,
			Authentication authentication) {
		UserAccount user = getCurrentUser(authentication);
		TimetableEntry entry = timetableService.createEntry(request, user);
		return ResponseEntity.status(HttpStatus.CREATED).body(entry);
	}

	/** DELETE entry — TIMETABLE_MANAGER or ADMIN */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'TIMETABLE_MANAGER')")
	public ResponseEntity<Void> deleteEntry(@PathVariable Long id, Authentication authentication) {
		UserAccount user = getCurrentUser(authentication);
		timetableService.deleteEntry(id, user);
		return ResponseEntity.noContent().build();
	}

	private UserAccount getCurrentUser(Authentication authentication) {
		if (authentication == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
		}
		return userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
	}
}
