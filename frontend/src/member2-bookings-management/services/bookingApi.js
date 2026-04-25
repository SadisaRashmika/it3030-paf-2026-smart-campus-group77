import { requestJson } from "../../services/apiClient";

const BASE_URL = "/api/member2";

export const bookingApi = {
    /** Booking Core */
    createBooking: (data) => requestJson(`${BASE_URL}/bookings`, {
        method: "POST",
        body: JSON.stringify(data)
    }),

    getMyBookings: () => requestJson(`${BASE_URL}/bookings/mine`),

    cancelBooking: (id) => requestJson(`${BASE_URL}/bookings/${id}/cancel`, {
        method: "PATCH"
    }),

    /** Management & Timetable */
    getPendingBookings: () => requestJson(`${BASE_URL}/bookings/pending`),

    getWeeklyBookings: (start, end) => requestJson(`${BASE_URL}/bookings/weekly?start=${start}&end=${end}`),

    approveBooking: (id) => requestJson(`${BASE_URL}/bookings/${id}/approve`, {
        method: "PATCH"
    }),

    rejectBooking: (id, reason) => requestJson(`${BASE_URL}/bookings/${id}/reject`, {
        method: "PATCH",
        body: JSON.stringify({ rejectionReason: reason })
    })
};
