import { useCallback, useEffect, useState } from "react";
import { Search, Filter, Trash2, UserPlus, TicketCheck, Clock, CheckCircle2, XCircle, AlertCircle, Eye } from "lucide-react";
import TicketStatusBadge from "./TicketStatusBadge";
import TicketPriorityBadge from "./TicketPriorityBadge";
import TicketDetailPanel from "./TicketDetailPanel";
import { getAllTickets, deleteTicket, assignTechnician } from "../services/ticketService";

const STATUS_FILTERS = [
  { key: "ALL", label: "All" },
  { key: "OPEN", label: "Open" },
  { key: "IN_PROGRESS", label: "In Progress" },
  { key: "RESOLVED", label: "Resolved" },
  { key: "CLOSED", label: "Closed" },
  { key: "REJECTED", label: "Rejected" }
];

export default function AdminTicketManagement({ user }) {
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [selectedTicketId, setSelectedTicketId] = useState(null);
  const [assignModal, setAssignModal] = useState(null);
  const [techEmail, setTechEmail] = useState("");
  const [assigning, setAssigning] = useState(false);
  const [error, setError] = useState("");

  const loadTickets = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getAllTickets();
      setTickets(Array.isArray(data) ? data : []);
      setError("");
    } catch (err) {
      setError(err.message || "Failed to load tickets");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadTickets(); }, [loadTickets]);

  const handleDelete = async (ticketId) => {
    if (!window.confirm("Are you sure you want to delete this ticket?")) return;
    try {
      await deleteTicket(ticketId);
      await loadTickets();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleAssign = async () => {
    if (!techEmail.trim()) return;
    setAssigning(true);
    try {
      await assignTechnician(assignModal, { technicianEmail: techEmail.trim() });
      setAssignModal(null);
      setTechEmail("");
      await loadTickets();
    } catch (err) {
      setError(err.message);
    } finally {
      setAssigning(false);
    }
  };

  const filteredTickets = tickets.filter(t => {
    if (statusFilter !== "ALL" && t.status !== statusFilter) return false;
    if (searchTerm) {
      const q = searchTerm.toLowerCase();
      return (
        t.title?.toLowerCase().includes(q) ||
        t.reporterEmail?.toLowerCase().includes(q) ||
        t.assignedTechnicianEmail?.toLowerCase().includes(q) ||
        t.resourceLocation?.toLowerCase().includes(q) ||
        String(t.id).includes(q)
      );
    }
    return true;
  });

  const statusCounts = {
    ALL: tickets.length,
    OPEN: tickets.filter(t => t.status === "OPEN").length,
    IN_PROGRESS: tickets.filter(t => t.status === "IN_PROGRESS").length,
    RESOLVED: tickets.filter(t => t.status === "RESOLVED").length,
    CLOSED: tickets.filter(t => t.status === "CLOSED").length,
    REJECTED: tickets.filter(t => t.status === "REJECTED").length
  };

  const timeAgo = (dateStr) => {
    if (!dateStr) return "";
    const diff = Math.floor((new Date() - new Date(dateStr)) / 1000);
    if (diff < 60) return "just now";
    if (diff < 3600) return `${Math.floor(diff / 60)}m`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h`;
    return `${Math.floor(diff / 86400)}d`;
  };

  if (selectedTicketId) {
    return (
      <TicketDetailPanel
        ticketId={selectedTicketId}
        onClose={() => { setSelectedTicketId(null); loadTickets(); }}
        user={user}
        isAdmin={true}
      />
    );
  }

  return (
    <div className="space-y-5">
      {/* Header */}
      <div className="rounded-2xl border border-slate-200 bg-gradient-to-r from-white via-blue-50/30 to-white p-6 shadow-sm">
        <div className="inline-flex items-center gap-2 rounded-xl bg-blue-50 px-3 py-1.5 text-sm font-bold text-blue-700 mb-2">
          <TicketCheck size={16} /> Admin Panel
        </div>
        <h1 className="text-2xl font-bold text-slate-900">Ticket Management</h1>
        <p className="mt-1 text-sm text-slate-500">Manage all campus maintenance and incident tickets</p>

        {/* Summary Cards */}
        <div className="mt-4 grid grid-cols-2 gap-3 sm:grid-cols-5">
          <div className="rounded-xl border border-blue-100 bg-blue-50/50 px-3 py-2.5 text-center">
            <p className="text-lg font-bold text-blue-700">{statusCounts.OPEN}</p>
            <p className="text-[10px] font-bold uppercase text-blue-500">Open</p>
          </div>
          <div className="rounded-xl border border-amber-100 bg-amber-50/50 px-3 py-2.5 text-center">
            <p className="text-lg font-bold text-amber-700">{statusCounts.IN_PROGRESS}</p>
            <p className="text-[10px] font-bold uppercase text-amber-500">In Progress</p>
          </div>
          <div className="rounded-xl border border-emerald-100 bg-emerald-50/50 px-3 py-2.5 text-center">
            <p className="text-lg font-bold text-emerald-700">{statusCounts.RESOLVED}</p>
            <p className="text-[10px] font-bold uppercase text-emerald-500">Resolved</p>
          </div>
          <div className="rounded-xl border border-slate-100 bg-slate-50/50 px-3 py-2.5 text-center">
            <p className="text-lg font-bold text-slate-600">{statusCounts.CLOSED}</p>
            <p className="text-[10px] font-bold uppercase text-slate-400">Closed</p>
          </div>
          <div className="rounded-xl border border-rose-100 bg-rose-50/50 px-3 py-2.5 text-center">
            <p className="text-lg font-bold text-rose-600">{statusCounts.REJECTED}</p>
            <p className="text-[10px] font-bold uppercase text-rose-400">Rejected</p>
          </div>
        </div>
      </div>

      {/* Filters Row */}
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
        <div className="flex flex-wrap gap-1.5">
          {STATUS_FILTERS.map((sf) => (
            <button
              key={sf.key}
              onClick={() => setStatusFilter(sf.key)}
              className={`rounded-lg border px-3 py-1.5 text-xs font-bold transition ${
                statusFilter === sf.key
                  ? "border-amber-400 bg-amber-50 text-amber-800"
                  : "border-slate-200 bg-white text-slate-600 hover:bg-amber-50/30"
              }`}
            >
              {sf.label} ({statusCounts[sf.key]})
            </button>
          ))}
        </div>
        <div className="relative flex-1">
          <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search by title, reporter, technician..."
            className="w-full rounded-lg border border-slate-200 bg-white py-2 pl-9 pr-3 text-sm focus:border-amber-400 focus:outline-none focus:ring-2 focus:ring-amber-100"
          />
        </div>
      </div>

      {error && (
        <div className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-2.5 text-sm text-rose-700">{error}</div>
      )}

      {/* Tickets Table */}
      {loading ? (
        <div className="flex h-40 items-center justify-center">
          <div className="h-8 w-8 animate-spin rounded-full border-3 border-amber-400 border-t-transparent" />
        </div>
      ) : filteredTickets.length === 0 ? (
        <div className="flex flex-col items-center justify-center rounded-2xl border border-dashed border-slate-200 bg-white py-12">
          <TicketCheck size={36} className="mb-2 text-slate-300" />
          <p className="text-sm font-semibold text-slate-500">No tickets found</p>
        </div>
      ) : (
        <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead>
                <tr className="border-b border-slate-100 bg-slate-50/80">
                  <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wide text-slate-500">#</th>
                  <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wide text-slate-500">Title</th>
                  <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wide text-slate-500">Status</th>
                  <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wide text-slate-500">Priority</th>
                  <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wide text-slate-500">Reporter</th>
                  <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wide text-slate-500">Technician</th>
                  <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wide text-slate-500">Age</th>
                  <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wide text-slate-500">Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredTickets.map((t) => (
                  <tr key={t.id} className="border-b border-slate-50 transition hover:bg-amber-50/20">
                    <td className="px-4 py-3 text-xs font-semibold text-slate-400">{t.id}</td>
                    <td className="px-4 py-3">
                      <button onClick={() => setSelectedTicketId(t.id)} className="text-sm font-semibold text-slate-800 hover:text-amber-700 transition text-left line-clamp-1">
                        {t.title}
                      </button>
                      {t.resourceLocation && <p className="text-[11px] text-slate-400 mt-0.5">📍 {t.resourceLocation}</p>}
                    </td>
                    <td className="px-4 py-3"><TicketStatusBadge status={t.status} /></td>
                    <td className="px-4 py-3"><TicketPriorityBadge priority={t.priority} /></td>
                    <td className="px-4 py-3 text-xs text-slate-600 truncate max-w-[120px]">{t.reporterEmail?.split("@")[0]}</td>
                    <td className="px-4 py-3">
                      {t.assignedTechnicianEmail ? (
                        <span className="text-xs font-semibold text-emerald-600">{t.assignedTechnicianEmail.split("@")[0]}</span>
                      ) : (
                        <span className="text-xs text-slate-400">—</span>
                      )}
                    </td>
                    <td className="px-4 py-3 text-xs text-slate-500">{timeAgo(t.createdAt)}</td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-1">
                        <button onClick={() => setSelectedTicketId(t.id)} className="flex h-7 w-7 items-center justify-center rounded-lg text-slate-400 transition hover:bg-slate-100 hover:text-slate-700" title="View">
                          <Eye size={14} />
                        </button>
                        <button onClick={() => { setAssignModal(t.id); setTechEmail(t.assignedTechnicianEmail || ""); }} className="flex h-7 w-7 items-center justify-center rounded-lg text-slate-400 transition hover:bg-blue-50 hover:text-blue-600" title="Assign Technician">
                          <UserPlus size={14} />
                        </button>
                        <button onClick={() => handleDelete(t.id)} className="flex h-7 w-7 items-center justify-center rounded-lg text-slate-400 transition hover:bg-rose-50 hover:text-rose-600" title="Delete">
                          <Trash2 size={14} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Assign Technician Modal */}
      {assignModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="w-full max-w-md rounded-2xl border border-slate-200 bg-white p-6 shadow-2xl">
            <h3 className="text-lg font-bold text-slate-900 mb-1">Assign Technician</h3>
            <p className="text-xs text-slate-500 mb-4">Ticket #{assignModal}</p>
            <input
              type="email"
              value={techEmail}
              onChange={(e) => setTechEmail(e.target.value)}
              placeholder="technician@smartcampus.lk"
              className="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm focus:border-amber-400 focus:outline-none focus:ring-2 focus:ring-amber-100"
            />
            <div className="mt-4 flex justify-end gap-2">
              <button onClick={() => { setAssignModal(null); setTechEmail(""); }} className="rounded-xl border border-slate-200 px-4 py-2 text-sm font-bold text-slate-600 hover:bg-slate-50">Cancel</button>
              <button
                onClick={handleAssign}
                disabled={!techEmail.trim() || assigning}
                className="rounded-xl bg-gradient-to-b from-amber-400 to-amber-500 px-5 py-2 text-sm font-bold text-amber-950 shadow transition hover:brightness-105 disabled:opacity-50"
              >
                {assigning ? "Assigning..." : "Assign"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
