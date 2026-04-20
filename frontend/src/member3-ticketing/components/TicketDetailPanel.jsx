import { useEffect, useState } from "react";
import { ArrowLeft, MapPin, Mail, Phone, User2, Calendar, AlertTriangle, Edit3, Trash2 } from "lucide-react";
import TicketStatusBadge from "./TicketStatusBadge";
import TicketPriorityBadge from "./TicketPriorityBadge";
import TicketCommentSection from "./TicketCommentSection";
import CreateTicketModal from "./CreateTicketModal";
import { getTicketById, updateTicketStatus, addComment, updateComment, deleteComment, updateTicket, deleteTicket } from "../services/ticketService";

const CATEGORY_LABELS = {
  HARDWARE: "Hardware", SOFTWARE: "Software", NETWORK: "Network",
  ELECTRICAL: "Electrical", PLUMBING: "Plumbing", GENERAL: "General", OTHER: "Other"
};

const STATUS_TRANSITIONS = {
  OPEN: [{ value: "IN_PROGRESS", label: "Start Work" }],
  IN_PROGRESS: [{ value: "RESOLVED", label: "Mark Resolved" }],
  RESOLVED: [{ value: "CLOSED", label: "Close Ticket" }, { value: "IN_PROGRESS", label: "Reopen" }]
};

const ADMIN_EXTRA_TRANSITIONS = {
  OPEN: [{ value: "REJECTED", label: "Reject", danger: true }],
  IN_PROGRESS: [{ value: "REJECTED", label: "Reject", danger: true }]
};

export default function TicketDetailPanel({ ticketId, onClose, user, isAdmin }) {
  const [ticket, setTicket] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [statusAction, setStatusAction] = useState(null);
  const [rejectionReason, setRejectionReason] = useState("");
  const [resolutionNotes, setResolutionNotes] = useState("");
  const [updatingStatus, setUpdatingStatus] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [imageModal, setImageModal] = useState(null);

  const loadTicket = async () => {
    setLoading(true);
    try {
      const data = await getTicketById(ticketId);
      setTicket(data);
      setError("");
    } catch (err) {
      setError(err.message || "Failed to load ticket");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadTicket(); }, [ticketId]);

  const handleStatusUpdate = async (newStatus) => {
    if (newStatus === "REJECTED") {
      setStatusAction("REJECTED");
      return;
    }
    if (newStatus === "RESOLVED") {
      setStatusAction("RESOLVED");
      return;
    }

    setUpdatingStatus(true);
    try {
      const updated = await updateTicketStatus(ticketId, { status: newStatus });
      setTicket(updated);
    } catch (err) {
      setError(err.message);
    } finally {
      setUpdatingStatus(false);
    }
  };

  const confirmStatusAction = async () => {
    setUpdatingStatus(true);
    try {
      const payload = { status: statusAction };
      if (statusAction === "REJECTED") payload.rejectionReason = rejectionReason;
      if (statusAction === "RESOLVED") payload.resolutionNotes = resolutionNotes;
      const updated = await updateTicketStatus(ticketId, payload);
      setTicket(updated);
      setStatusAction(null);
      setRejectionReason("");
      setResolutionNotes("");
    } catch (err) {
      setError(err.message);
    } finally {
      setUpdatingStatus(false);
    }
  };

  const handleAddComment = async (payload) => {
    const comment = await addComment(ticketId, payload);
    setTicket(prev => ({ ...prev, comments: [...prev.comments, comment] }));
  };

  const handleUpdateComment = async (commentId, payload) => {
    const updated = await updateComment(ticketId, commentId, payload);
    setTicket(prev => ({
      ...prev,
      comments: prev.comments.map(c => c.id === commentId ? updated : c)
    }));
  };

  const handleDeleteComment = async (commentId) => {
    if (!window.confirm("Delete this comment?")) return;
    await deleteComment(ticketId, commentId);
    setTicket(prev => ({
      ...prev,
      comments: prev.comments.filter(c => c.id !== commentId)
    }));
  };

  const handleEditSubmit = async (payload) => {
    const updated = await updateTicket(ticketId, payload);
    setTicket(updated);
    setEditModalOpen(false);
  };

  const handleDeleteTicket = async () => {
    if (!window.confirm("Are you sure you want to delete this ticket? This action cannot be undone.")) return;
    setDeleting(true);
    try {
      await deleteTicket(ticketId);
      onClose();
    } catch (err) {
      setError(err.message);
      setDeleting(false);
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return "";
    try { return new Date(dateStr).toLocaleString(); } catch { return ""; }
  };

  const transitions = STATUS_TRANSITIONS[ticket?.status] || [];
  const adminActions = isAdmin ? (ADMIN_EXTRA_TRANSITIONS[ticket?.status] || []) : [];
  const allActions = [...transitions, ...adminActions];

  const isReporter = ticket?.reporterEmail?.toLowerCase() === user?.email?.toLowerCase();
  const canModify = isReporter && ticket?.status === "OPEN";

  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-3 border-amber-400 border-t-transparent" />
      </div>
    );
  }

  if (error && !ticket) {
    return (
      <div className="p-6">
        <button onClick={onClose} className="mb-4 flex items-center gap-1 text-sm font-semibold text-slate-600 hover:text-slate-900">
          <ArrowLeft size={16} /> Back
        </button>
        <div className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">{error}</div>
      </div>
    );
  }

  return (
    <div className="space-y-0">
      {/* Header */}
      <div className="mb-4 flex items-start justify-between gap-3">
        <button onClick={onClose} className="flex items-center gap-1 text-sm font-semibold text-slate-500 transition hover:text-slate-900">
          <ArrowLeft size={16} /> Back to list
        </button>
        <div className="flex items-center gap-3">
          {canModify && !deleting && (
            <div className="flex items-center gap-2 mr-2">
              <button
                onClick={() => setEditModalOpen(true)}
                className="flex items-center gap-1.5 rounded-lg border border-slate-200 bg-white px-3 py-1.5 text-xs font-bold text-slate-600 transition hover:bg-slate-50 hover:text-slate-900"
              >
                <Edit3 size={14} /> Edit
              </button>
              <button
                onClick={handleDeleteTicket}
                className="flex items-center gap-1.5 rounded-lg border border-rose-100 bg-white px-3 py-1.5 text-xs font-bold text-rose-600 transition hover:bg-rose-50 hover:text-rose-700"
              >
                <Trash2 size={14} /> Delete
              </button>
            </div>
          )}
          {deleting && <span className="text-xs font-bold text-rose-500 animate-pulse">Deleting...</span>}
          <span className="text-xs font-semibold text-slate-400">#{ticket.id}</span>
        </div>
      </div>

      {error && (
        <div className="mb-4 rounded-xl border border-rose-200 bg-rose-50 px-4 py-2.5 text-sm text-rose-700">{error}</div>
      )}

      <div className="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">
        {/* Ticket Head */}
        <div className="border-b border-slate-100 bg-gradient-to-r from-white via-amber-50/30 to-white p-5">
          <div className="flex flex-wrap items-center gap-2 mb-2">
            <TicketStatusBadge status={ticket.status} />
            <TicketPriorityBadge priority={ticket.priority} />
            <span className="rounded-md border border-slate-200 bg-slate-50 px-2 py-0.5 text-[11px] font-semibold text-slate-600">
              {CATEGORY_LABELS[ticket.category] || ticket.category}
            </span>
          </div>
          <h2 className="text-xl font-bold text-slate-900">{ticket.title}</h2>
          <p className="mt-2 text-sm leading-relaxed text-slate-600">{ticket.description}</p>
        </div>

        {/* Details Grid */}
        <div className="grid grid-cols-2 gap-px bg-slate-100 sm:grid-cols-4">
          {ticket.resourceLocation && (
            <div className="flex items-center gap-2 bg-white px-4 py-3">
              <MapPin size={14} className="text-slate-400" />
              <div>
                <p className="text-[10px] font-bold uppercase text-slate-400">Location</p>
                <p className="text-xs font-semibold text-slate-700">{ticket.resourceLocation}</p>
              </div>
            </div>
          )}
          {ticket.contactEmail && (
            <div className="flex items-center gap-2 bg-white px-4 py-3">
              <Mail size={14} className="text-slate-400" />
              <div>
                <p className="text-[10px] font-bold uppercase text-slate-400">Email</p>
                <p className="text-xs font-semibold text-slate-700 truncate">{ticket.contactEmail}</p>
              </div>
            </div>
          )}
          {ticket.contactPhone && (
            <div className="flex items-center gap-2 bg-white px-4 py-3">
              <Phone size={14} className="text-slate-400" />
              <div>
                <p className="text-[10px] font-bold uppercase text-slate-400">Phone</p>
                <p className="text-xs font-semibold text-slate-700">{ticket.contactPhone}</p>
              </div>
            </div>
          )}
          <div className="flex items-center gap-2 bg-white px-4 py-3">
              <User2 size={14} className="text-slate-400" />
            <div>
              <p className="text-[10px] font-bold uppercase text-slate-400">Reporter</p>
              <p className="text-xs font-semibold text-slate-700 truncate">{ticket.reporterEmail?.split("@")[0]}</p>
            </div>
          </div>
          {ticket.assignedTechnicianEmail && (
            <div className="flex items-center gap-2 bg-white px-4 py-3">
              <User2 size={14} className="text-emerald-500" />
              <div>
                <p className="text-[10px] font-bold uppercase text-slate-400">Technician</p>
                <p className="text-xs font-semibold text-emerald-700 truncate">{ticket.assignedTechnicianEmail?.split("@")[0]}</p>
              </div>
            </div>
          )}
          <div className="flex items-center gap-2 bg-white px-4 py-3">
              <Calendar size={14} className="text-slate-400" />
            <div>
              <p className="text-[10px] font-bold uppercase text-slate-400">Created</p>
              <p className="text-xs font-semibold text-slate-700">{formatDate(ticket.createdAt)}</p>
            </div>
          </div>
        </div>

        {/* Rejection / Resolution Notes */}
        {ticket.rejectionReason && (
          <div className="border-t border-rose-100 bg-rose-50/50 px-5 py-3">
            <div className="flex items-center gap-1 text-xs font-bold text-rose-700"><AlertTriangle size={12} /> Rejection Reason</div>
            <p className="mt-1 text-sm text-rose-600">{ticket.rejectionReason}</p>
          </div>
        )}
        {ticket.resolutionNotes && (
          <div className="border-t border-emerald-100 bg-emerald-50/50 px-5 py-3">
            <div className="flex items-center gap-1 text-xs font-bold text-emerald-700">Resolution Notes</div>
            <p className="mt-1 text-sm text-emerald-600">{ticket.resolutionNotes}</p>
          </div>
        )}

        {/* Image Attachments */}
        {ticket.attachments && ticket.attachments.length > 0 && (
          <div className="border-t border-slate-100 p-5">
            <h4 className="mb-2 text-xs font-bold uppercase tracking-wide text-slate-500">Attachments ({ticket.attachments.length})</h4>
            <div className="flex flex-wrap gap-3">
              {ticket.attachments.map((att, i) => (
                <button
                  key={att.id || i}
                  onClick={() => setImageModal(att)}
                  className="h-24 w-24 overflow-hidden rounded-xl border border-slate-200 shadow-sm transition hover:shadow-md hover:border-amber-300"
                >
                  <img src={att.dataUrl} alt={att.fileName || `Attachment ${i + 1}`} className="h-full w-full object-cover" />
                </button>
              ))}
            </div>
          </div>
        )}

        {/* Status Actions */}
        {allActions.length > 0 && (
          <div className="border-t border-slate-100 px-5 py-4">
            <div className="flex flex-wrap gap-2">
              {allActions.map((action) => (
                <button
                  key={action.value}
                  onClick={() => handleStatusUpdate(action.value)}
                  disabled={updatingStatus}
                  className={`rounded-xl px-4 py-2 text-sm font-bold transition disabled:opacity-50 ${
                    action.danger
                      ? "border border-rose-200 bg-white text-rose-700 hover:bg-rose-50"
                      : "bg-gradient-to-b from-amber-400 to-amber-500 text-amber-950 shadow-[0_4px_12px_rgba(245,158,11,0.25)] hover:brightness-105"
                  }`}
                >
                  {action.label}
                </button>
              ))}
            </div>

            {/* Rejection Reason Input */}
            {statusAction === "REJECTED" && (
              <div className="mt-3 space-y-2">
                <textarea
                  value={rejectionReason}
                  onChange={(e) => setRejectionReason(e.target.value)}
                  placeholder="Enter rejection reason (required)..."
                  rows={2}
                  className="w-full resize-none rounded-xl border border-rose-200 bg-white px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-rose-100"
                />
                <div className="flex gap-2">
                  <button onClick={() => setStatusAction(null)} className="rounded-lg border border-slate-200 px-3 py-1.5 text-xs font-semibold text-slate-600 hover:bg-slate-50">Cancel</button>
                  <button
                    onClick={confirmStatusAction}
                    disabled={!rejectionReason.trim() || updatingStatus}
                    className="rounded-lg bg-rose-500 px-3 py-1.5 text-xs font-bold text-white hover:bg-rose-600 disabled:opacity-50"
                  >
                    Confirm Reject
                  </button>
                </div>
              </div>
            )}

            {/* Resolution Notes Input */}
            {statusAction === "RESOLVED" && (
              <div className="mt-3 space-y-2">
                <textarea
                  value={resolutionNotes}
                  onChange={(e) => setResolutionNotes(e.target.value)}
                  placeholder="Add resolution notes (optional)..."
                  rows={2}
                  className="w-full resize-none rounded-xl border border-emerald-200 bg-white px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-100"
                />
                <div className="flex gap-2">
                  <button onClick={() => setStatusAction(null)} className="rounded-lg border border-slate-200 px-3 py-1.5 text-xs font-semibold text-slate-600 hover:bg-slate-50">Cancel</button>
                  <button
                    onClick={confirmStatusAction}
                    disabled={updatingStatus}
                    className="rounded-lg bg-emerald-500 px-3 py-1.5 text-xs font-bold text-white hover:bg-emerald-600 disabled:opacity-50"
                  >
                    Mark Resolved
                  </button>
                </div>
              </div>
            )}
          </div>
        )}

        {/* Comments Section */}
        <div className="border-t border-slate-100 px-5 py-4">
          <TicketCommentSection
            comments={ticket.comments || []}
            onAdd={handleAddComment}
            onUpdate={handleUpdateComment}
            onDelete={handleDeleteComment}
            currentUserEmail={user?.email}
          />
        </div>
      </div>

      {/* Image Lightbox */}
      {imageModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4" onClick={() => setImageModal(null)}>
          <div className="relative max-h-[85vh] max-w-4xl overflow-hidden rounded-2xl bg-white shadow-2xl" onClick={(e) => e.stopPropagation()}>
            <img src={imageModal.dataUrl} alt={imageModal.fileName} className="max-h-[85vh] w-auto object-contain" />
            <button onClick={() => setImageModal(null)} className="absolute right-3 top-3 flex h-8 w-8 items-center justify-center rounded-full bg-black/50 text-white transition hover:bg-black/70">✕</button>
          </div>
        </div>
      )}

      {/* Edit Modal */}
      <CreateTicketModal
        isOpen={editModalOpen}
        onClose={() => setEditModalOpen(false)}
        onSubmit={handleEditSubmit}
        userEmail={user?.email}
        initialData={ticket}
      />
    </div>
  );
}
