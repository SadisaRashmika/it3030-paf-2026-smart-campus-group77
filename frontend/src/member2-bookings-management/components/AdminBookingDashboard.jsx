import React, { useState, useEffect } from "react";
import { Check, X, Clock, User, MapPin, Calendar, RefreshCw, AlertCircle } from "lucide-react";
import { getAllBookings, updateBookingStatus } from "../../services/bookingService";

export default function AdminBookingDashboard() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchBookings = async () => {
    try {
      setLoading(true);
      const data = await getAllBookings();
      setBookings(data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)));
      setError("");
    } catch (err) {
      setError("Failed to fetch pending bookings.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBookings();
  }, []);

  const handleStatusUpdate = async (id, status) => {
    try {
      await updateBookingStatus(id, status);
      fetchBookings();
    } catch (err) {
      alert("Failed to update booking status.");
    }
  };

  if (loading) {
    return (
      <div className="flex h-64 flex-col items-center justify-center space-y-4">
        <RefreshCw className="h-8 w-8 animate-spin text-blue-600" />
        <p className="text-slate-500">Loading manager dashboard...</p>
      </div>
    );
  }

  const pendingBookings = bookings.filter(b => b.status === "PENDING");
  const processedBookings = bookings.filter(b => b.status !== "PENDING");

  return (
    <div className="space-y-8 animate-in fade-in duration-500">
      <div className="flex items-end justify-between">
        <div>
          <h2 className="text-3xl font-extrabold text-slate-900 tracking-tight">Booking Approvals</h2>
          <p className="mt-2 text-slate-600 font-medium">Timetable Manager Portal</p>
        </div>
        <button 
          onClick={fetchBookings}
          className="flex items-center gap-2 rounded-xl bg-white border border-slate-200 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50 transition-all shadow-sm"
        >
          <RefreshCw size={16} /> Sync
        </button>
      </div>

      {error && (
        <div className="rounded-xl bg-rose-50 p-4 flex items-center gap-3 text-rose-700 border border-rose-100 italic font-medium">
          <AlertCircle size={20} /> {error}
        </div>
      )}

      {/* Pending Requests Section */}
      <section>
        <div className="mb-6 flex items-center gap-3">
          <div className="h-2 w-2 rounded-full bg-blue-500 animate-pulse" />
          <h3 className="text-sm font-bold uppercase tracking-widest text-slate-400">Pending Authorization ({pendingBookings.length})</h3>
        </div>
        
        {pendingBookings.length === 0 ? (
          <div className="rounded-2xl border-2 border-dashed border-slate-200 p-12 text-center">
            <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-slate-50 text-slate-300">
              <Check size={24} />
            </div>
            <p className="mt-4 text-sm font-bold text-slate-400">All bookings processed</p>
          </div>
        ) : (
          <div className="grid gap-4">
            {pendingBookings.map((booking) => (
              <div key={booking.id} className="group relative overflow-hidden rounded-2xl border border-slate-200 bg-white p-6 shadow-sm transition-all hover:shadow-md hover:border-blue-200">
                <div className="flex flex-col gap-6 sm:flex-row sm:items-center sm:justify-between">
                  {/* Left Column: Info */}
                  <div className="flex items-start gap-4 flex-1">
                    <div className="rounded-xl bg-blue-50 p-4 text-blue-600">
                      <MapPin size={24} />
                    </div>
                    <div className="space-y-1">
                      <h4 className="text-xl font-bold text-slate-900 leading-tight">{booking.resource.name}</h4>
                      <div className="flex flex-wrap gap-x-6 gap-y-1 text-sm font-medium text-slate-500">
                        <span className="flex items-center gap-1.5"><User size={14} /> {booking.user.email}</span>
                        <span className="flex items-center gap-1.5"><Calendar size={14} /> {new Date(booking.startTime).toLocaleDateString()}</span>
                        <span className="flex items-center gap-1.5"><Clock size={14} /> {new Date(booking.startTime).toLocaleTimeString()} - {new Date(booking.endTime).toLocaleTimeString()}</span>
                      </div>
                    </div>
                  </div>

                  {/* Right Column: Actions */}
                  <div className="flex gap-3">
                    <button
                      onClick={() => handleStatusUpdate(booking.id, "REJECTED")}
                      className="flex items-center gap-2 rounded-xl border border-slate-100 bg-white px-5 py-2.5 text-sm font-bold text-slate-600 hover:bg-rose-50 hover:text-rose-600 hover:border-rose-100 transition-all active:scale-95"
                    >
                      <X size={18} /> Reject
                    </button>
                    <button
                      onClick={() => handleStatusUpdate(booking.id, "APPROVED")}
                      className="flex items-center gap-2 rounded-xl bg-blue-600 px-7 py-2.5 text-sm font-bold text-white shadow-lg shadow-blue-100 hover:bg-blue-700 transition-all active:scale-95"
                    >
                      <Check size={18} /> Approve
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* History Tracking Section */}
      {processedBookings.length > 0 && (
        <section className="pt-4 opacity-75 grayscale hover:grayscale-0 hover:opacity-100 transition-all duration-700">
          <h3 className="mb-6 text-sm font-bold uppercase tracking-widest text-slate-400">Processing History</h3>
          <div className="rounded-2xl border border-slate-200 bg-white shadow-sm overflow-hidden">
             <table className="w-full text-left text-sm">
                <thead className="bg-slate-50 text-slate-500 uppercase tracking-tighter font-bold border-b border-slate-100">
                  <tr>
                    <th className="px-6 py-4">Resource</th>
                    <th className="px-6 py-4">requested by</th>
                    <th className="px-6 py-4">Schedule</th>
                    <th className="px-6 py-4">Verdict</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {processedBookings.slice(0, 10).map((booking) => (
                    <tr key={booking.id} className="hover:bg-slate-50/50 transition-colors">
                      <td className="px-6 py-4 font-bold text-slate-800">{booking.resource.name}</td>
                      <td className="px-6 py-4 text-slate-600">{booking.user.email}</td>
                      <td className="px-6 py-4 text-slate-500 text-xs font-mono">
                        {new Date(booking.startTime).toLocaleString()}
                      </td>
                      <td className="px-6 py-4">
                        <span className={`inline-flex items-center rounded-lg px-2.5 py-1 text-[10px] font-black uppercase tracking-tighter ${
                          booking.status === "APPROVED" ? "bg-emerald-50 text-emerald-700" : "bg-rose-50 text-rose-700"
                        }`}>
                          {booking.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
             </table>
          </div>
        </section>
      )}
    </div>
  );
}
