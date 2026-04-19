import React, { useState, useEffect } from "react";
import { bookingApi } from "../services/bookingApi";

const BookingRequestForm = ({ onBookingCreated }) => {
  const [resources, setResources] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const [formData, setFormData] = useState({
    resourceId: "",
    date: "",
    startTime: "",
    endTime: "",
    purpose: "",
    expectedAttendees: ""
  });

  useEffect(() => {
    fetchResources();
  }, []);

  const fetchResources = async () => {
    try {
      const data = await bookingApi.getResources();
      setResources(data);
    } catch (err) {
      console.error("Failed to fetch resources", err);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(false);

    try {
      // Combine date and time for backend
      const startDateTime = `${formData.date}T${formData.startTime}:00`;
      const endDateTime = `${formData.date}T${formData.endTime}:00`;

      await bookingApi.createBooking({
        resourceId: parseInt(formData.resourceId),
        startTime: startDateTime,
        endTime: endDateTime,
        purpose: formData.purpose,
        expectedAttendees: parseInt(formData.expectedAttendees) || 0
      });

      setSuccess(true);
      setFormData({
        resourceId: "",
        date: "",
        startTime: "",
        endTime: "",
        purpose: "",
        expectedAttendees: ""
      });
      if (onBookingCreated) onBookingCreated();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white rounded-2xl shadow-xl border border-slate-100 overflow-hidden transition-all hover:shadow-2xl">
      <div className="bg-gradient-to-r from-indigo-600 to-violet-600 px-6 py-4">
        <h3 className="text-white font-bold text-lg flex items-center">
          <span className="mr-2">📅</span> New Booking Request
        </h3>
      </div>

      <form onSubmit={handleSubmit} className="p-6 space-y-4">
        {error && (
          <div className="bg-rose-50 border-l-4 border-rose-500 p-4 rounded-md">
            <p className="text-rose-700 text-sm font-medium">{error}</p>
          </div>
        )}

        {success && (
          <div className="bg-emerald-50 border-l-4 border-emerald-500 p-4 rounded-md animate-pulse">
            <p className="text-emerald-700 text-sm font-medium">Request submitted successfully!</p>
          </div>
        )}

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="space-y-1">
            <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider">Resource</label>
            <select
              required
              className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:bg-white outline-none transition-all"
              value={formData.resourceId}
              onChange={(e) => setFormData({ ...formData, resourceId: e.target.value })}
            >
              <option value="">Select a resource...</option>
              {resources.map((res) => (
                <option key={res.id} value={res.id}>
                  {res.name} ({res.type})
                </option>
              ))}
            </select>
          </div>

          <div className="space-y-1">
            <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider">Date</label>
            <input
              type="date"
              required
              className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:bg-white outline-none transition-all"
              value={formData.date}
              onChange={(e) => setFormData({ ...formData, date: e.target.value })}
            />
          </div>

          <div className="space-y-1">
            <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider">Start Time</label>
            <input
              type="time"
              required
              className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:bg-white outline-none transition-all"
              value={formData.startTime}
              onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
            />
          </div>

          <div className="space-y-1">
            <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider">End Time</label>
            <input
              type="time"
              required
              className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:bg-white outline-none transition-all"
              value={formData.endTime}
              onChange={(e) => setFormData({ ...formData, endTime: e.target.value })}
            />
          </div>
        </div>

        <div className="space-y-1">
          <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider">Purpose</label>
          <textarea
            required
            rows="2"
            maxLength="500"
            className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:bg-white outline-none transition-all resize-none"
            placeholder="What is the reason for this booking?"
            value={formData.purpose}
            onChange={(e) => setFormData({ ...formData, purpose: e.target.value })}
          />
        </div>

        <div className="space-y-1">
          <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider">Expected Attendees</label>
          <input
            type="number"
            min="1"
            className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:bg-white outline-none transition-all"
            placeholder="Approximate number of people"
            value={formData.expectedAttendees}
            onChange={(e) => setFormData({ ...formData, expectedAttendees: e.target.value })}
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-3 rounded-xl shadow-lg shadow-indigo-200 transition-all transform hover:-translate-y-0.5 active:translate-y-0 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {loading ? "Submitting..." : "Submit Booking Request"}
        </button>
      </form>
    </div>
  );
};

export default BookingRequestForm;
