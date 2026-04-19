import React, { useState, useEffect, useCallback } from "react";
import { bookingApi } from "../services/bookingApi";
import BookingStatusBadge from "./BookingStatusBadge";

const MyBookingsList = ({ refreshTrigger }) => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchMyBookings = useCallback(async () => {
    try {
      setLoading(true);
      const data = await bookingApi.getMyBookings();
      setBookings(data);
    } catch (err) {
      setError("Failed to load your bookings.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchMyBookings();
  }, [fetchMyBookings, refreshTrigger]);

  const handleCancel = async (id) => {
    if (!window.confirm("Are you sure you want to cancel this booking?")) return;
    
    try {
      await bookingApi.cancelBooking(id);
      fetchMyBookings(); // Refresh list
    } catch (err) {
      alert("Failed to cancel booking: " + err.message);
    }
  };

  const formatDateTime = (isoString) => {
    const date = new Date(isoString);
    return date.toLocaleString([], { 
        weekday: 'short', 
        month: 'short', 
        day: 'numeric', 
        hour: '2-digit', 
        minute: '2-digit' 
    });
  };

  if (loading) return <div className="text-center py-10 text-slate-400 italic">Loading your bookings...</div>;

  return (
    <div className="space-y-4">
      <h3 className="text-slate-800 font-bold text-xl flex items-center px-2">
        <span className="mr-2">📋</span> My Booking History
      </h3>

      {bookings.length === 0 ? (
        <div className="bg-slate-50 border-2 border-dashed border-slate-200 rounded-2xl p-10 text-center">
            <p className="text-slate-400">You haven't made any bookings yet.</p>
        </div>
      ) : (
        <div className="grid gap-4">
          {bookings.map((booking) => (
            <div 
              key={booking.id} 
              className="bg-white border border-slate-100 rounded-2xl p-5 shadow-sm hover:shadow-md transition-all group flex flex-col md:flex-row md:items-center justify-between gap-4"
            >
              <div className="space-y-1">
                <div className="flex items-center gap-3">
                  <span className="font-bold text-slate-800 text-lg">{booking.resourceName}</span>
                  <BookingStatusBadge status={booking.status} />
                </div>
                <div className="text-sm text-slate-500 flex items-center gap-2">
                  <span>⏰ {formatDateTime(booking.startTime)}</span>
                  <span>→</span>
                  <span>{formatDateTime(booking.endTime).split(',')[1]}</span>
                </div>
                {booking.purpose && (
                  <p className="text-sm text-slate-600 italic bg-slate-50 px-3 py-1 rounded-lg inline-block">
                    "{booking.purpose}"
                  </p>
                )}
                {booking.rejectionReason && (
                    <div className="mt-2 text-xs text-rose-500 font-medium bg-rose-50 px-2 py-1 rounded border border-rose-100 italic">
                        Reason: {booking.rejectionReason}
                    </div>
                )}
              </div>

              <div className="flex items-center gap-3">
                {booking.status === "APPROVED" && (
                  <button
                    onClick={() => handleCancel(booking.id)}
                    className="px-4 py-2 bg-slate-50 hover:bg-rose-50 text-slate-600 hover:text-rose-600 text-sm font-bold border border-slate-200 hover:border-rose-200 rounded-xl transition-all"
                  >
                    Cancel Booking
                  </button>
                )}
                
                {/* Visual indicator for IDs */}
                <span className="text-[10px] text-slate-300 font-mono">#{booking.id}</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MyBookingsList;
