package com.it3030.smartcampus.member2.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.it3030.smartcampus.member1.model.Resource;
import com.it3030.smartcampus.member1.service.ResourceService;
import com.it3030.smartcampus.member2.dto.TimetableRequest;
import com.it3030.smartcampus.member2.exception.BookingConflictException;
import com.it3030.smartcampus.member2.model.TimetableEntry;
import com.it3030.smartcampus.member2.repository.BookingRepository;
import com.it3030.smartcampus.member2.repository.TimetableRepository;
import com.it3030.smartcampus.member4.model.UserAccount;

@Service
@Transactional
public class TimetableService {

	private final TimetableRepository timetableRepository;
	private final ResourceService resourceService;
	private final BookingRepository bookingRepository;

	public TimetableService(TimetableRepository timetableRepository,
							ResourceService resourceService,
							BookingRepository bookingRepository) {
		this.timetableRepository = timetableRepository;
		this.resourceService = resourceService;
		this.bookingRepository = bookingRepository;
	}

	public List<TimetableEntry> getAllEntries() {
		return timetableRepository.findAll();
	}

	public List<TimetableEntry> getTodayEntries() {
		DayOfWeek today = java.time.LocalDate.now().getDayOfWeek();
		return timetableRepository.findByDayOfWeek(today);
	}

	public List<TimetableEntry> getEntriesForDay(DayOfWeek day) {
		return timetableRepository.findByDayOfWeek(day);
	}

	public TimetableEntry createEntry(TimetableRequest request, UserAccount createdBy) {
		Resource resource = resourceService.getResourceById(request.resourceId());

		// Check for timetable-vs-timetable conflicts
		List<TimetableEntry> conflicts = timetableRepository.findOverlapping(
				request.resourceId(), request.dayOfWeek(),
				request.startTime(), request.endTime());

		if (!conflicts.isEmpty()) {
			throw new BookingConflictException(
					"This resource already has a recurring class scheduled during that time on " + request.dayOfWeek() + ".");
		}

		TimetableEntry entry = new TimetableEntry();
		entry.setResource(resource);
		entry.setDayOfWeek(request.dayOfWeek());
		entry.setStartTime(request.startTime());
		entry.setEndTime(request.endTime());
		entry.setTitle(request.title());
		entry.setDescription(request.description());
		entry.setCreatedBy(createdBy);
		return timetableRepository.save(entry);
	}

	public void deleteEntry(Long id, UserAccount user) {
		TimetableEntry entry = timetableRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Timetable entry not found"));
		boolean isAdmin = user.getRole().name().equals("ADMIN");
		boolean isCreator = entry.getCreatedBy() != null && entry.getCreatedBy().getId().equals(user.getId());
		if (!isAdmin && !isCreator) {
			throw new IllegalArgumentException("Not authorized to delete this entry");
		}
		timetableRepository.delete(entry);
	}

	/**
	 * Checks if a booking overlaps any timetable entry on the same resource and day.
	 * Called by BookingService before saving a booking.
	 */
	public boolean hasConflictWithTimetable(Long resourceId, java.time.Instant startInstant, java.time.Instant endInstant) {
		java.time.ZoneId zone = java.time.ZoneId.systemDefault();
		java.time.LocalDateTime start = startInstant.atZone(zone).toLocalDateTime();
		java.time.LocalDateTime end   = endInstant.atZone(zone).toLocalDateTime();
		DayOfWeek day = start.getDayOfWeek();
		LocalTime startTime = start.toLocalTime();
		LocalTime endTime   = end.toLocalTime();

		return !timetableRepository.findOverlapping(resourceId, day, startTime, endTime).isEmpty();
	}

	public Map<String, Object> getStats() {
		long totalEntries = timetableRepository.count();
		long totalResources = resourceService.getAllResources().size();
		long pendingBookings = bookingRepository.findAll().stream()
				.filter(b -> b.getStatus().name().equals("PENDING")).count();

		String mostBooked = "N/A";
		List<Object[]> results = timetableRepository.findMostBookedResource();
		if (!results.isEmpty()) {
			mostBooked = (String) results.get(0)[0];
		}

		Map<String, Object> stats = new LinkedHashMap<>();
		stats.put("totalResources", totalResources);
		stats.put("pendingBookings", pendingBookings);
		stats.put("totalWeeklyClasses", totalEntries);
		stats.put("mostBookedResource", mostBooked);
		return stats;
	}
}
