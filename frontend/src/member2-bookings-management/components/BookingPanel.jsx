import React, { useState } from "react";
import BookingRequestForm from "./BookingRequestForm";
import MyBookingsList from "./MyBookingsList";

/**
 * Main dashboard for Students and Lecturers.
 * Combines the booking request form and the history list.
 */
const BookingPanel = ({ user }) => {
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const handleBookingCreated = () => {
    // Increment trigger to force MyBookingsList to re-fetch
    setRefreshTrigger(prev => prev + 1);
  };

  return (
    <div className="space-y-4">
      <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <p className="text-xs font-bold uppercase tracking-widest text-slate-500">Bookings Workspace</p>
        <h2 className="mt-1 text-2xl font-bold text-slate-900">Manage Your Booking Requests</h2>
        <p className="mt-1 text-sm text-slate-600">
          Welcome, {user?.name || "Member"}. Submit a booking request and monitor your status updates in one place.
        </p>
      </div>

      <div className="grid grid-cols-1 gap-4 xl:grid-cols-12 xl:items-start">
        <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm xl:col-span-5">
          <BookingRequestForm onBookingCreated={handleBookingCreated} />
          <div className="mt-4 rounded-xl border border-amber-200 bg-amber-50 px-3 py-2 text-xs text-amber-800">
            Requests remain PENDING until reviewed by Timetable Manager or Admin. Approved requests can later be cancelled.
          </div>
        </article>

        <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm xl:col-span-7">
          <MyBookingsList refreshTrigger={refreshTrigger} />
        </article>
      </div>
    </div>
  );
};

export default BookingPanel;
