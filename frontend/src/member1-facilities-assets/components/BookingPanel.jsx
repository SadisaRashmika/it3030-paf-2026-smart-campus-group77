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
    <div className="max-w-6xl mx-auto p-4 md:p-8 space-y-12 animate-in fade-in slide-in-from-bottom-4 duration-700">
      {/* Welcome Header */}
      <section className="space-y-2">
        <h1 className="font-display text-3xl md:text-4xl font-extrabold text-slate-900 tracking-tight">
          Welcome, <span className="text-amber-700 underline decoration-amber-300 underline-offset-8">{user?.name || "Member"}</span>
        </h1>
        <p className="text-slate-600 text-lg">
          Manage your campus resource bookings from one central location.
        </p>
      </section>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-10 items-start">
        {/* Left: Request Form */}
        <div className="lg:col-span-5 sticky top-8">
          <BookingRequestForm onBookingCreated={handleBookingCreated} />
          
          <div className="mt-6 p-4 bg-amber-50 rounded-xl border border-amber-200 flex items-start gap-3">
            <span className="text-xl text-amber-700">Info</span>
            <p className="text-xs text-amber-800 font-semibold leading-relaxed">
              Your requests will stay <strong>PENDING</strong> until a Timetable Manager reviews them. 
              Once approved, you'll see a badge update in your history list.
            </p>
          </div>
        </div>

        {/* Right: History List */}
        <div className="lg:col-span-7">
          <MyBookingsList refreshTrigger={refreshTrigger} />
        </div>
      </div>
    </div>
  );
};

export default BookingPanel;
