import React, { useState, useEffect } from "react";
import { bookingApi } from "../services/bookingApi";

const PendingApprovalsPanel = ({ user }) => {
  const [pending, setPending] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [rejectingId, setRejectingId] = useState(null);
  const [rejectionReason, setRejectionReason] = useState("");

  useEffect(() => {
    fetchPending();
  }, []);

  const fetchPending = async () => {
    console.log("DASHBOARD: Starting fetchPending()...");
    try {
      setLoading(true);
      const data = await bookingApi.getPendingBookings();
      console.log("DASHBOARD: Received data:", data);
      setPending(data);
    } catch (err) {
      setError("Failed to fetch pending requests.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (id) => {
    try {
      await bookingApi.approveBooking(id);
      fetchPending();
    } catch (err) {
      alert("Conflict detected: " + err.message);
    }
  };

  const handleReject = async (id) => {
    if (!rejectionReason.trim()) {
      alert("Please provide a reason for rejection.");
      return;
    }
    try {
      await bookingApi.rejectBooking(id, rejectionReason);
      setRejectingId(null);
      setRejectionReason("");
      fetchPending();
    } catch (err) {
      alert("Failed to reject: " + err.message);
    }
  };

  const formatDateTime = (isoString) => {
    return new Date(isoString).toLocaleString([], {
      weekday: 'short', month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  };

  if (loading) return <div className="p-8 text-center text-slate-400">Scanning pending inbox...</div>;

  return (
    <div className="max-w-6xl mx-auto p-4 md:p-8 space-y-8 animate-in fade-in duration-500">
      <header className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="font-display text-3xl font-extrabold text-slate-900 tracking-tight flex items-center">
            Pending Approvals
          </h1>
          <p className="text-slate-500">Review and manage incoming resource booking requests.</p>
        </div>
        <div className="bg-amber-50 text-amber-700 px-4 py-2 rounded-xl border border-amber-100 font-bold text-sm">
          {pending.length} Requests Awaiting Review
        </div>
      </header>

      {pending.length === 0 ? (
        <div className="bg-white border-2 border-dashed border-slate-100 rounded-3xl p-20 text-center shadow-inner">
          <h3 className="text-xl font-bold text-slate-400">All caught up!</h3>
          <p className="text-slate-300">No pending requests at the moment.</p>
        </div>
      ) : (
        <div className="overflow-x-auto bg-white rounded-3xl shadow-xl border border-slate-200">
          <table className="w-full text-left">
            <thead>
              <tr className="bg-slate-50 border-b border-slate-100">
                <th className="px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-widest">Requester</th>
                <th className="px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-widest">Resource</th>
                <th className="px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-widest">Schedule</th>
                <th className="px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-widest text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50">
              {pending.map((req) => (
                <tr key={req.id} className="hover:bg-slate-50/50 transition-colors">
                  <td className="px-6 py-5">
                    <div className="font-bold text-slate-800">{req.userName}</div>
                    <div className="text-xs text-amber-700 font-semibold">#{req.id}</div>
                  </td>
                  <td className="px-6 py-5">
                    <div className="font-semibold text-slate-700">{req.resourceName}</div>
                    <div className="text-xs text-slate-400">{req.resourceType}</div>
                  </td>
                  <td className="px-6 py-5">
                    <div className="text-sm font-medium text-slate-600">{formatDateTime(req.startTime)}</div>
                    <div className="text-xs text-slate-400">for {req.purpose}</div>
                  </td>
                  <td className="px-6 py-5 text-right">
                    {rejectingId === req.id ? (
                      <div className="flex flex-col gap-2 min-w-[200px] ml-auto">
                        <input
                          autoFocus
                          placeholder="Rejection reason..."
                          className="px-3 py-2 text-sm border border-rose-200 rounded-lg outline-none focus:ring-2 focus:ring-rose-500"
                          value={rejectionReason}
                          onChange={(e) => setRejectionReason(e.target.value)}
                        />
                        <div className="flex gap-2 justify-end">
                          <button onClick={() => setRejectingId(null)} className="text-xs text-slate-400 hover:underline">Cancel</button>
                          <button onClick={() => handleReject(req.id)} className="bg-rose-600 text-white text-xs font-bold px-3 py-1.5 rounded-lg shadow-md hover:bg-rose-700 transition-all">Confirm Reject</button>
                        </div>
                      </div>
                    ) : (
                      <div className="flex items-center justify-end gap-3">
                        <button
                          onClick={() => setRejectingId(req.id)}
                          className="p-2 hover:bg-rose-50 text-rose-400 hover:text-rose-600 rounded-xl transition-all border border-transparent hover:border-rose-100"
                          title="Reject"
                        >
                          Reject
                        </button>
                        <button
                          onClick={() => handleApprove(req.id)}
                          className="bg-amber-500 hover:bg-amber-600 text-amber-950 text-sm font-bold px-6 py-2.5 rounded-xl shadow-lg shadow-amber-100 transition-all transform hover:-translate-y-0.5"
                        >
                          Approve Slot
                        </button>
                      </div>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default PendingApprovalsPanel;
