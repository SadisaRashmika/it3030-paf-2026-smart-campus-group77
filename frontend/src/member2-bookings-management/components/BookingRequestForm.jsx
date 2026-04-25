import React, { useState, useEffect } from "react";
import { bookingApi } from "../services/bookingApi";
import { resourceApi } from "../../member1-facilities-assets/services/resourceApi";

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
      const data = await resourceApi.getResources();
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
    <div className="space-y-4">
      <div>
        <h3 className="text-lg font-bold text-slate-900">New Booking Request</h3>
        <p className="text-sm text-slate-600">Provide date, time range, purpose, and expected attendees.</p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
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
              className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-amber-500 focus:bg-white outline-none transition-all"
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
              className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-amber-500 focus:bg-white outline-none transition-all"
              value={formData.date}
              onChange={(e) => setFormData({ ...formData, date: e.target.value })}
            />
          </div>

          <div className="space-y-1">
            <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider">Start Time</label>
            <input
              type="time"
              required
              className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-amber-500 focus:bg-white outline-none transition-all"
              value={formData.startTime}
              onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
            />
          </div>

          <div className="space-y-1">
            <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider">End Time</label>
            <input
              type="time"
              required
              className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-amber-500 focus:bg-white outline-none transition-all"
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
            className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-amber-500 focus:bg-white outline-none transition-all resize-none"
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
            className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-amber-500 focus:bg-white outline-none transition-all"
            placeholder="Approximate number of people"
            value={formData.expectedAttendees}
            onChange={(e) => setFormData({ ...formData, expectedAttendees: e.target.value })}
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full rounded-xl bg-amber-500 py-3 text-sm font-bold text-amber-950 transition hover:bg-amber-600 disabled:cursor-not-allowed disabled:opacity-50"
        >
          {loading ? "Submitting..." : "Submit Booking Request"}
        </button>
      </form>
    </div>
  );
};

export default BookingRequestForm;
