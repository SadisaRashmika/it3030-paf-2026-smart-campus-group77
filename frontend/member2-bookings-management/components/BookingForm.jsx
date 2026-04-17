import React, { useState } from 'react';
import { createBooking } from '../services/bookingService';
import './BookingForm.css';

/**
 * BookingForm
 * ────────────────────────────────────────────────────────────────
 * Lets a user request a resource booking.
 *
 * Props:
 *  - resourceId  (number)  – pre-fill the resource field (optional)
 *  - resources   (Array)   – list of { id, name, location } for the dropdown
 *  - userId      (number)  – current user id (hardcoded to 1 until Auth is ready)
 *  - onSuccess   (fn)      – callback fired after a successful submission
 */
export default function BookingForm({
  resourceId: defaultResourceId = '',
  resources = [],
  userId = 1,
  onSuccess,
}) {
  const [form, setForm] = useState({
    resourceId: defaultResourceId,
    startDate: '',
    startTime: '',
    endDate: '',
    endTime: '',
    purpose: '',
    expectedAttendees: '',
  });

  const [status, setStatus] = useState(null); // 'success' | 'conflict' | 'error'
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setStatus(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setStatus(null);

    const startTime = `${form.startDate}T${form.startTime}:00`;
    const endTime   = `${form.endDate}T${form.endTime}:00`;

    try {
      await createBooking({
        resourceId: Number(form.resourceId),
        userId,
        startTime,
        endTime,
        purpose: form.purpose,
        expectedAttendees: form.expectedAttendees ? Number(form.expectedAttendees) : null,
      });

      setStatus('success');
      setForm({ resourceId: defaultResourceId, startDate: '', startTime: '', endDate: '', endTime: '', purpose: '', expectedAttendees: '' });
      if (onSuccess) onSuccess();
    } catch (err) {
      if (err.response?.status === 409) {
        setStatus('conflict');
      } else {
        setStatus('error');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bf-card">
      <div className="bf-header">
        <span className="bf-icon">🗓️</span>
        <div>
          <h2 className="bf-title">Request a Booking</h2>
          <p className="bf-subtitle">Fill in the details to reserve a campus resource.</p>
        </div>
      </div>

      {status === 'success' && (
        <div className="bf-alert bf-alert--success">
          ✅ Booking request submitted! It is now <strong>PENDING</strong> admin review.
        </div>
      )}
      {status === 'conflict' && (
        <div className="bf-alert bf-alert--conflict">
          ⚠️ This time slot is already booked for the selected resource. Please choose a different time.
        </div>
      )}
      {status === 'error' && (
        <div className="bf-alert bf-alert--error">
          ❌ Something went wrong. Please try again or contact support.
        </div>
      )}

      <form className="bf-form" onSubmit={handleSubmit} noValidate>
        {/* Resource selector */}
        <div className="bf-field">
          <label htmlFor="bf-resource" className="bf-label">Resource</label>
          {resources.length > 0 ? (
            <select
              id="bf-resource"
              name="resourceId"
              className="bf-input"
              value={form.resourceId}
              onChange={handleChange}
              required
            >
              <option value="">— Select a resource —</option>
              {resources.map((r) => (
                <option key={r.id} value={r.id}>
                  {r.name}{r.location ? ` (${r.location})` : ''}
                </option>
              ))}
            </select>
          ) : (
            <input
              id="bf-resource"
              name="resourceId"
              type="number"
              className="bf-input"
              placeholder="Resource ID"
              value={form.resourceId}
              onChange={handleChange}
              required
              min={1}
            />
          )}
        </div>

        {/* Start date & time */}
        <div className="bf-field-row">
          <div className="bf-field">
            <label htmlFor="bf-start-date" className="bf-label">Start Date</label>
            <input
              id="bf-start-date"
              name="startDate"
              type="date"
              className="bf-input"
              value={form.startDate}
              onChange={handleChange}
              required
            />
          </div>
          <div className="bf-field">
            <label htmlFor="bf-start-time" className="bf-label">Start Time</label>
            <input
              id="bf-start-time"
              name="startTime"
              type="time"
              className="bf-input"
              value={form.startTime}
              onChange={handleChange}
              required
            />
          </div>
        </div>

        {/* End date & time */}
        <div className="bf-field-row">
          <div className="bf-field">
            <label htmlFor="bf-end-date" className="bf-label">End Date</label>
            <input
              id="bf-end-date"
              name="endDate"
              type="date"
              className="bf-input"
              value={form.endDate}
              onChange={handleChange}
              required
            />
          </div>
          <div className="bf-field">
            <label htmlFor="bf-end-time" className="bf-label">End Time</label>
            <input
              id="bf-end-time"
              name="endTime"
              type="time"
              className="bf-input"
              value={form.endTime}
              onChange={handleChange}
              required
            />
          </div>
        </div>

        {/* Purpose and Expected Attendees */}
        <div className="bf-field-row">
          <div className="bf-field">
            <label htmlFor="bf-purpose" className="bf-label">Purpose</label>
            <textarea
              id="bf-purpose"
              name="purpose"
              className="bf-input bf-textarea"
              placeholder="e.g. Lecture, Study Group…"
              value={form.purpose}
              onChange={handleChange}
              required
              rows={2}
            />
          </div>
          <div className="bf-field">
            <label htmlFor="bf-attendees" className="bf-label">Expected Attendees (Optional)</label>
            <input
              id="bf-attendees"
              name="expectedAttendees"
              type="number"
              className="bf-input"
              placeholder="e.g. 10"
              value={form.expectedAttendees}
              onChange={handleChange}
              min={1}
            />
          </div>
        </div>

        <button
          id="bf-submit"
          type="submit"
          className="bf-btn"
          disabled={loading}
        >
          {loading ? (
            <><span className="bf-spinner" /> Submitting…</>
          ) : (
            'Submit Booking Request'
          )}
        </button>
      </form>
    </div>
  );
}
