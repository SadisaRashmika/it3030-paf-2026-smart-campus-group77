import React, { useEffect, useState, useCallback } from 'react';
import { getMyBookings, cancelBooking } from '../services/bookingService';
import './MyBookingsPage.css';

/**
 * MyBookingsPage
 * ─────────────────────────────────────────────────────────────────
 * Displays all bookings for the current user with status badges.
 * A "Cancel" button appears only for PENDING or APPROVED bookings.
 *
 * Props:
 *  - userId (number) – current user's id (defaults to 1 until Auth is ready)
 */
export default function MyBookingsPage({ userId = 1 }) {
  const [bookings, setBookings]   = useState([]);
  const [loading, setLoading]     = useState(true);
  const [error, setError]         = useState(null);
  const [cancelling, setCancelling] = useState(null); // id of booking being cancelled

  const fetchBookings = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await getMyBookings(userId);
      setBookings(res.data);
    } catch {
      setError('Failed to load your bookings. Please refresh the page.');
    } finally {
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => { fetchBookings(); }, [fetchBookings]);

  const handleCancel = async (id) => {
    if (!window.confirm('Are you sure you want to cancel this booking?')) return;
    setCancelling(id);
    try {
      await cancelBooking(id);
      await fetchBookings();
    } catch {
      alert('Could not cancel the booking. Please try again.');
    } finally {
      setCancelling(null);
    }
  };

  // ── helpers ──────────────────────────────────────────────────────────────
  const fmt = (iso) =>
    new Date(iso).toLocaleString('en-LK', {
      weekday: 'short', year: 'numeric', month: 'short',
      day: 'numeric', hour: '2-digit', minute: '2-digit',
    });

  const statusMeta = {
    PENDING:   { label: 'Pending',   cls: 'mb-badge--pending'   },
    APPROVED:  { label: 'Approved',  cls: 'mb-badge--approved'  },
    REJECTED:  { label: 'Rejected',  cls: 'mb-badge--rejected'  },
    CANCELLED: { label: 'Cancelled', cls: 'mb-badge--cancelled' },
  };

  const canCancel = (status) => status === 'PENDING' || status === 'APPROVED';

  // ── render ───────────────────────────────────────────────────────────────
  return (
    <div className="mb-page">
      <header className="mb-header">
        <div className="mb-header-left">
          <span className="mb-icon">📋</span>
          <div>
            <h1 className="mb-title">My Bookings</h1>
            <p className="mb-subtitle">Track the status of all your resource requests.</p>
          </div>
        </div>
        <button id="mb-refresh" className="mb-refresh-btn" onClick={fetchBookings} disabled={loading}>
          {loading ? <span className="mb-spinner" /> : '↻'} Refresh
        </button>
      </header>

      {error && <div className="mb-alert mb-alert--error">{error}</div>}

      {loading && !error && (
        <div className="mb-loading">
          <span className="mb-spinner mb-spinner--lg" />
          Loading your bookings…
        </div>
      )}

      {!loading && !error && bookings.length === 0 && (
        <div className="mb-empty">
          <span className="mb-empty-icon">📭</span>
          <p>You have no bookings yet.</p>
          <p className="mb-empty-hint">Use the booking form to request a resource.</p>
        </div>
      )}

      {!loading && !error && bookings.length > 0 && (
        <div className="mb-table-wrapper">
          <table className="mb-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Resource</th>
                <th>Location</th>
                <th>Start</th>
                <th>End</th>
                <th>Purpose</th>
                <th>Attendees</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {bookings.map((b, idx) => {
                const meta = statusMeta[b.status] ?? { label: b.status, cls: '' };
                return (
                  <tr key={b.id} className="mb-row">
                    <td className="mb-cell mb-cell--id">{idx + 1}</td>
                    <td className="mb-cell mb-cell--resource">
                      <span className="mb-resource-name">{b.resourceName ?? `Resource #${b.resourceId}`}</span>
                      {b.resourceType && (
                        <span className="mb-resource-type">{b.resourceType}</span>
                      )}
                    </td>
                    <td className="mb-cell">{b.resourceLocation ?? '—'}</td>
                    <td className="mb-cell mb-cell--time">{fmt(b.startTime)}</td>
                    <td className="mb-cell mb-cell--time">{fmt(b.endTime)}</td>
                    <td className="mb-cell mb-cell--purpose">
                      {b.purpose}
                      {b.adminNotes && (
                        <div style={{ marginTop: '4px', fontSize: '0.8rem', color: '#fca5a5' }}>
                          <strong>Note:</strong> {b.adminNotes}
                        </div>
                      )}
                    </td>
                    <td className="mb-cell">{b.expectedAttendees || '—'}</td>
                    <td className="mb-cell">
                      <span className={`mb-badge ${meta.cls}`}>{meta.label}</span>
                    </td>
                    <td className="mb-cell">
                      {canCancel(b.status) && (
                        <button
                          id={`mb-cancel-${b.id}`}
                          className="mb-btn-cancel"
                          onClick={() => handleCancel(b.id)}
                          disabled={cancelling === b.id}
                        >
                          {cancelling === b.id
                            ? <span className="mb-spinner mb-spinner--sm" />
                            : 'Cancel'}
                        </button>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
