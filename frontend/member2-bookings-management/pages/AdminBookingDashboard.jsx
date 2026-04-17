import React, { useEffect, useState, useCallback } from 'react';
import { getAllBookings, approveBooking, rejectBooking } from '../services/bookingService';
import './AdminBookingDashboard.css';

/**
 * AdminBookingDashboard
 * ─────────────────────────────────────────────────────────────────
 * Central dashboard for admins to manage all booking requests.
 *
 * Features:
 *  • Summary stat cards (Total, Pending, Approved, Rejected)
 *  • Filter tabs: All | Pending | Approved | Rejected | Cancelled
 *  • Pending bookings always surface at the top of the list
 *  • Approve / Reject buttons on every PENDING row
 *  • Conflict error shown inline when approval is blocked
 */
export default function AdminBookingDashboard() {
  const [bookings, setBookings]         = useState([]);
  const [loading, setLoading]           = useState(true);
  const [error, setError]               = useState(null);
  const [filter, setFilter]             = useState('ALL');  // ALL | PENDING | APPROVED | REJECTED | CANCELLED
  const [actionState, setActionState]   = useState({});    // { [id]: 'approving'|'rejecting'|'conflict' }

  const fetchBookings = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await getAllBookings();
      // Sort: PENDING first, then by startTime ascending
      const sorted = [...res.data].sort((a, b) => {
        if (a.status === 'PENDING' && b.status !== 'PENDING') return -1;
        if (a.status !== 'PENDING' && b.status === 'PENDING') return  1;
        return new Date(a.startTime) - new Date(b.startTime);
      });
      setBookings(sorted);
    } catch {
      setError('Failed to load bookings. Check your connection and refresh.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchBookings(); }, [fetchBookings]);

  // ── Actions ──────────────────────────────────────────────────────────────
  const handleApprove = async (id) => {
    setActionState((s) => ({ ...s, [id]: 'approving' }));
    try {
      await approveBooking(id);
      await fetchBookings();
    } catch (err) {
      const isConflict = err.response?.status === 409;
      setActionState((s) => ({ ...s, [id]: isConflict ? 'conflict' : 'error' }));
      // clear the error indicator after 4 s
      setTimeout(() => setActionState((s) => { const c = { ...s }; delete c[id]; return c; }), 4000);
    }
  };

  const handleReject = async (id) => {
    const reason = window.prompt('Provide a reason for rejection (optional):');
    if (reason === null) return; // User cancelled the prompt
    
    setActionState((s) => ({ ...s, [id]: 'rejecting' }));
    try {
      await rejectBooking(id, reason);
      await fetchBookings();
    } catch {
      setActionState((s) => { const c = { ...s }; delete c[id]; return c; });
      alert('Could not reject the booking. Please try again.');
    }
  };

  // ── Derived data ─────────────────────────────────────────────────────────
  const stats = {
    total:     bookings.length,
    pending:   bookings.filter((b) => b.status === 'PENDING').length,
    approved:  bookings.filter((b) => b.status === 'APPROVED').length,
    rejected:  bookings.filter((b) => b.status === 'REJECTED').length,
    cancelled: bookings.filter((b) => b.status === 'CANCELLED').length,
  };

  const displayed =
    filter === 'ALL' ? bookings : bookings.filter((b) => b.status === filter);

  // ── Helpers ───────────────────────────────────────────────────────────────
  const fmt = (iso) =>
    new Date(iso).toLocaleString('en-LK', {
      weekday: 'short', month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit',
    });

  const TABS = ['ALL', 'PENDING', 'APPROVED', 'REJECTED', 'CANCELLED'];

  const statusMeta = {
    PENDING:   { label: 'Pending',   cls: 'ad-badge--pending'   },
    APPROVED:  { label: 'Approved',  cls: 'ad-badge--approved'  },
    REJECTED:  { label: 'Rejected',  cls: 'ad-badge--rejected'  },
    CANCELLED: { label: 'Cancelled', cls: 'ad-badge--cancelled' },
  };

  // ── Render ────────────────────────────────────────────────────────────────
  return (
    <div className="ad-page">
      {/* ── Page header ── */}
      <header className="ad-header">
        <div className="ad-header-left">
          <span className="ad-icon">🛡️</span>
          <div>
            <h1 className="ad-title">Booking Management</h1>
            <p className="ad-subtitle">Review, approve, and reject campus resource requests.</p>
          </div>
        </div>
        <button id="ad-refresh" className="ad-refresh-btn" onClick={fetchBookings} disabled={loading}>
          {loading ? <span className="ad-spinner ad-spinner--sm" /> : '↻'} Refresh
        </button>
      </header>

      {/* ── Stat cards ── */}
      <div className="ad-stats">
        {[
          { key: 'total',     label: 'Total',     value: stats.total,     emoji: '📊' },
          { key: 'pending',   label: 'Pending',   value: stats.pending,   emoji: '⏳' },
          { key: 'approved',  label: 'Approved',  value: stats.approved,  emoji: '✅' },
          { key: 'rejected',  label: 'Rejected',  value: stats.rejected,  emoji: '❌' },
          { key: 'cancelled', label: 'Cancelled', value: stats.cancelled, emoji: '🚫' },
        ].map(({ key, label, value, emoji }) => (
          <div
            key={key}
            id={`ad-stat-${key}`}
            className={`ad-stat-card ad-stat-card--${key}`}
            onClick={() => setFilter(key === 'total' ? 'ALL' : key.toUpperCase())}
            role="button"
            tabIndex={0}
          >
            <span className="ad-stat-emoji">{emoji}</span>
            <span className="ad-stat-value">{value}</span>
            <span className="ad-stat-label">{label}</span>
          </div>
        ))}
      </div>

      {/* ── Error ── */}
      {error && <div className="ad-alert ad-alert--error">{error}</div>}

      {/* ── Filter tabs ── */}
      <div className="ad-tabs" role="tablist">
        {TABS.map((tab) => (
          <button
            key={tab}
            id={`ad-tab-${tab.toLowerCase()}`}
            role="tab"
            aria-selected={filter === tab}
            className={`ad-tab ${filter === tab ? 'ad-tab--active' : ''}`}
            onClick={() => setFilter(tab)}
          >
            {tab === 'ALL' ? 'All' : tab.charAt(0) + tab.slice(1).toLowerCase()}
            {tab !== 'ALL' && (
              <span className="ad-tab-count">
                {bookings.filter((b) => b.status === tab).length}
              </span>
            )}
          </button>
        ))}
      </div>

      {/* ── Loading skeleton ── */}
      {loading && !error && (
        <div className="ad-loading">
          <span className="ad-spinner ad-spinner--lg" />
          Loading bookings…
        </div>
      )}

      {/* ── Empty state ── */}
      {!loading && !error && displayed.length === 0 && (
        <div className="ad-empty">
          <span className="ad-empty-icon">📭</span>
          <p>No {filter === 'ALL' ? '' : filter.toLowerCase() + ' '}bookings found.</p>
        </div>
      )}

      {/* ── Table ── */}
      {!loading && !error && displayed.length > 0 && (
        <div className="ad-table-wrapper">
          <table className="ad-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>User</th>
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
              {displayed.map((b) => {
                const meta   = statusMeta[b.status] ?? { label: b.status, cls: '' };
                const action = actionState[b.id];
                const isPending = b.status === 'PENDING';

                return (
                  <tr key={b.id} className={`ad-row ${isPending ? 'ad-row--pending' : ''}`}>
                    <td className="ad-cell ad-cell--id">{b.id}</td>

                    <td className="ad-cell">
                      <span className="ad-user-name">{b.userName ?? `User #${b.userId}`}</span>
                      {b.userEmail && <span className="ad-user-email">{b.userEmail}</span>}
                    </td>

                    <td className="ad-cell">
                      <span className="ad-resource-name">{b.resourceName ?? `Resource #${b.resourceId}`}</span>
                      {b.resourceType && <span className="ad-resource-type">{b.resourceType}</span>}
                    </td>

                    <td className="ad-cell">{b.resourceLocation ?? '—'}</td>

                    <td className="ad-cell ad-cell--time">{fmt(b.startTime)}</td>
                    <td className="ad-cell ad-cell--time">{fmt(b.endTime)}</td>

                    <td className="ad-cell ad-cell--purpose">
                      {b.purpose}
                      {b.adminNotes && (
                        <div style={{ marginTop: '4px', fontSize: '0.75rem', color: '#fca5a5' }}>
                          <strong>Note:</strong> {b.adminNotes}
                        </div>
                      )}
                    </td>

                    <td className="ad-cell">{b.expectedAttendees || '—'}</td>

                    <td className="ad-cell">
                      <span className={`ad-badge ${meta.cls}`}>{meta.label}</span>
                    </td>

                    <td className="ad-cell ad-cell--actions">
                      {/* Conflict / error message */}
                      {action === 'conflict' && (
                        <span className="ad-inline-error">⚠️ Slot taken!</span>
                      )}
                      {action === 'error' && (
                        <span className="ad-inline-error">❌ Error</span>
                      )}

                      {isPending && action !== 'conflict' && action !== 'error' && (
                        <div className="ad-action-btns">
                          <button
                            id={`ad-approve-${b.id}`}
                            className="ad-btn ad-btn--approve"
                            onClick={() => handleApprove(b.id)}
                            disabled={!!action}
                            title="Approve booking"
                          >
                            {action === 'approving'
                              ? <span className="ad-spinner ad-spinner--sm" />
                              : '✓ Approve'}
                          </button>
                          <button
                            id={`ad-reject-${b.id}`}
                            className="ad-btn ad-btn--reject"
                            onClick={() => handleReject(b.id)}
                            disabled={!!action}
                            title="Reject booking"
                          >
                            {action === 'rejecting'
                              ? <span className="ad-spinner ad-spinner--sm" />
                              : '✕ Reject'}
                          </button>
                        </div>
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
