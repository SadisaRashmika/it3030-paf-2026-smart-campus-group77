import axios from 'axios';

const API_URL = 'http://localhost:8081/api/bookings';

/**
 * Creates a new booking request.
 * @param {{resourceId: number, userId: number, startTime: string, endTime: string, purpose: string}} bookingData
 */
export const createBooking = (bookingData) => axios.post(API_URL, bookingData);

/** Fetch the current user's bookings. Pass userId until Auth is wired up. */
export const getMyBookings = (userId = 1) =>
  axios.get(`${API_URL}/my-bookings`, { params: { userId } });

/** Admin: fetch every booking in the system. */
export const getAllBookings = () => axios.get(API_URL);

/** Admin: fetch only PENDING bookings. */
export const getPendingBookings = () => axios.get(`${API_URL}/pending`);

/** Fetch a single booking by ID. */
export const getBookingById = (id) => axios.get(`${API_URL}/${id}`);

/** Admin: approve a booking. */
export const approveBooking = (id) => axios.put(`${API_URL}/${id}/approve`);

/** Admin: reject a booking. */
export const rejectBooking = (id, reason) => axios.put(`${API_URL}/${id}/reject`, null, { params: { reason } });

/** User or admin: cancel a booking. */
export const cancelBooking = (id) => axios.put(`${API_URL}/${id}/cancel`);
