import { requestJson } from "./apiClient";

export function getAllResources(type) {
  const url = type ? `/api/member1/resources?type=${type}` : "/api/member1/resources";
  return requestJson(url);
}

export function createBooking(payload) {
  return requestJson("/api/member2/bookings", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function getMyBookings() {
  return requestJson("/api/member2/bookings/my");
}

export function getAllBookings() {
  return requestJson("/api/member2/bookings");
}

export function updateBookingStatus(id, status) {
  return requestJson(`/api/member2/bookings/${id}/status?status=${status}`, {
    method: "PUT"
  });
}

export function joinSession(id) {
  return requestJson(`/api/member2/bookings/${id}/join`, {
    method: "POST"
  });
}

export function cancelBooking(id) {
  return requestJson(`/api/member2/bookings/${id}`, {
    method: "DELETE"
  });
}

export function getTodayMineBookings() {
  return requestJson("/api/member2/bookings/today/mine");
}

export function getBookingStats() {
  return requestJson("/api/member2/bookings/stats");
}

// --- Timetable API ---
export function getTimetable() {
  return requestJson("/api/timetable");
}

export function getTodayTimetable() {
  return requestJson("/api/timetable/today");
}

export function getTimetableStats() {
  return requestJson("/api/timetable/stats");
}

export function createTimetableEntry(payload) {
  return requestJson("/api/timetable", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function deleteTimetableEntry(id) {
  return requestJson(`/api/timetable/${id}`, {
    method: "DELETE"
  });
}
