import React, { useState, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import { getMyBookings, joinSession, cancelBooking } from '../../services/bookingService';


export default function MyBookingsPage({ userId: propUserId }) {
  const { user } = useOutletContext();
  const userId = propUserId || user?.id;

  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchBookings = async () => {
      try {
        const data = await getMyBookings();
        setBookings(data);
      } catch (err) {
        console.error('Failed to fetch bookings', err);
      } finally {
        setLoading(false);
      }
    };
    fetchBookings();
  }, []);

  const handleJoin = async (id) => {
    try {
      await joinSession(id);
      alert('Joined session successfully!');
    } catch (err) {
      alert('Failed to join session.');
    }
  };

  const handleCancel = async (id) => {
    if (!window.confirm('Cancel this booking?')) return;
    try {
      await cancelBooking(id);
      setBookings(prev => prev.map(b => b.id === id ? { ...b, status: 'CANCELLED' } : b));
    } catch (err) {
      alert('Failed to cancel booking.');
    }
  };

  if (loading) return <div className="p-8 text-center text-slate-500">Loading your bookings...</div>;

  return (
    <div className="p-8 max-w-4xl mx-auto">
      <h2 className="text-2xl font-bold text-slate-900 mb-6">My Bookings</h2>
      {bookings.length === 0 ? (
        <div className="rounded-2xl border-2 border-dashed border-slate-200 p-12 text-center text-slate-400">
          No bookings found.
        </div>
      ) : (
        <div className="grid gap-4">
          {bookings.map((booking) => (
            <div key={booking.id} className="p-6 bg-white rounded-2xl border border-slate-200 shadow-sm flex justify-between items-center">
              <div>
                <h4 className="font-bold text-slate-900">{booking.resource.name}</h4>
                <p className="text-sm text-slate-500">
                  {new Date(booking.startTime).toLocaleString()} - {new Date(booking.endTime).toLocaleTimeString()}
                </p>
                <span className={`inline-block mt-2 px-2 py-1 rounded-lg text-[10px] font-black uppercase ${
                  booking.status === 'APPROVED' ? 'bg-emerald-50 text-emerald-700' : 
                  booking.status === 'REJECTED' ? 'bg-rose-50 text-rose-700' : 'bg-amber-50 text-amber-700'
                }`}>
                  {booking.status}
                </span>
              </div>
              <div className="flex gap-2">
                {booking.status === 'APPROVED' && (
                  <button 
                    onClick={() => handleJoin(booking.id)}
                    className="rounded-xl bg-blue-600 px-4 py-2 text-sm font-bold text-white hover:bg-blue-700"
                  >
                    Join
                  </button>
                )}
                {booking.status === 'PENDING' && (
                  <button
                    onClick={() => handleCancel(booking.id)}
                    className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-2 text-sm font-bold text-rose-600 hover:bg-rose-100"
                  >
                    Cancel
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
