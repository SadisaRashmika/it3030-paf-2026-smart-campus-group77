import React, { useState } from 'react';
import { useOutletContext, useNavigate } from 'react-router-dom';
import { createBooking } from '../../services/bookingService';


export default function BookingForm({ resources, userId: propUserId, onSuccess }) {
  const { user } = useOutletContext();
  const navigate = useNavigate();
  const userId = propUserId || user?.id;

  const [resourceId, setResourceId] = useState('');
  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');
    try {
      await createBooking({
        resourceId: parseInt(resourceId),
        userId,
        startTime: new Date(startTime).toISOString(),
        endTime: new Date(endTime).toISOString()
      });
      if (onSuccess) {
        onSuccess();
      } else {
        navigate('/student/bookings');
      }
    } catch (err) {
      setError('Failed to create booking. Please check your inputs.');
      console.error(err);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="max-w-md mx-auto p-6 bg-white rounded-2xl shadow-sm border border-slate-100">
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-bold text-slate-700">Resource ID</label>
          <input 
            type="number" 
            value={resourceId} 
            onChange={(e) => setResourceId(e.target.value)}
            className="mt-1 block w-full rounded-xl border-slate-200 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            required
          />
        </div>
        <div>
          <label className="block text-sm font-bold text-slate-700">Start Time</label>
          <input 
            type="datetime-local" 
            value={startTime} 
            onChange={(e) => setStartTime(e.target.value)}
            className="mt-1 block w-full rounded-xl border-slate-200 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            required
          />
        </div>
        <div>
          <label className="block text-sm font-bold text-slate-700">End Time</label>
          <input 
            type="datetime-local" 
            value={endTime} 
            onChange={(e) => setEndTime(e.target.value)}
            className="mt-1 block w-full rounded-xl border-slate-200 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            required
          />
        </div>
        {error && <p className="text-rose-500 text-xs font-medium">{error}</p>}
        <button 
          type="submit" 
          disabled={submitting}
          className="w-full rounded-xl bg-blue-600 py-3 text-sm font-bold text-white hover:bg-blue-700 transition-all disabled:opacity-50"
        >
          {submitting ? 'Creating...' : 'Submit Booking'}
        </button>
      </form>
    </div>
  );
}
