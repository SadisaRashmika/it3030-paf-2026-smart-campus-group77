import { useCallback, useEffect, useState } from "react";
import { Plus, Search, Filter, TicketCheck, Clock, CheckCircle2, XCircle, AlertCircle } from "lucide-react";
import TicketCard from "./TicketCard";
import CreateTicketModal from "./CreateTicketModal";
import TicketDetailPanel from "./TicketDetailPanel";
import { getMyTickets, createTicket, updateTicket, deleteTicket } from "../services/ticketService";

const STATUS_FILTERS = [
  { key: "ALL", label: "All", icon: Filter },
  { key: "OPEN", label: "Open", icon: AlertCircle },
  { key: "IN_PROGRESS", label: "In Progress", icon: Clock },
  { key: "RESOLVED", label: "Resolved", icon: CheckCircle2 },
  { key: "CLOSED", label: "Closed", icon: TicketCheck },
  { key: "REJECTED", label: "Rejected", icon: XCircle }
];

export default function StudentTicketDashboard({ user }) {
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [selectedTicketId, setSelectedTicketId] = useState(null);
  const [ticketToEdit, setTicketToEdit] = useState(null);
  const [error, setError] = useState("");

  const loadTickets = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getMyTickets();
      setTickets(Array.isArray(data) ? data : []);
      setError("");
    } catch (err) {
      setError(err.message || "Failed to load tickets");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadTickets(); }, [loadTickets]);

  const handleCreateOrUpdate = async (payload) => {
    if (ticketToEdit) {
      await updateTicket(ticketToEdit.id, payload);
    } else {
      await createTicket(payload);
    }
    setTicketToEdit(null);
    await loadTickets();
  };

  const handleEditClick = (ticket) => {
    setTicketToEdit(ticket);
    setCreateModalOpen(true);
  };

  const handleDeleteClick = async (id) => {
    if (!window.confirm("Are you sure you want to delete this ticket?")) return;
    try {
      await deleteTicket(id);
      await loadTickets();
    } catch (err) {
      setError(err.message);
    }
  };

  const filteredTickets = tickets.filter(t => {
    if (statusFilter !== "ALL" && t.status !== statusFilter) return false;
    if (searchTerm) {
      const q = searchTerm.toLowerCase();
      return (
        t.title?.toLowerCase().includes(q) ||
        t.category?.toLowerCase().includes(q) ||
        t.resourceLocation?.toLowerCase().includes(q)
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

  if (selectedTicketId) {
    return (
      <TicketDetailPanel
        ticketId={selectedTicketId}
        onClose={() => { setSelectedTicketId(null); loadTickets(); }}
        user={user}
        isAdmin={false}
      />
    );
  }

  return (
    <div className="space-y-5">
      {/* Hero Header */}
      <div className="rounded-2xl border border-slate-200 bg-gradient-to-r from-white via-amber-50/30 to-white p-6 shadow-sm">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <div className="inline-flex items-center gap-2 rounded-xl bg-amber-50 px-3 py-1.5 text-sm font-bold text-amber-700 mb-2">
              <TicketCheck size={16} /> Maintenance Tickets
            </div>
            <h1 className="text-2xl font-bold text-slate-900">My Tickets</h1>
            <p className="mt-1 text-sm text-slate-500">Report campus incidents and track resolution progress</p>
          </div>
          <button
            onClick={() => setCreateModalOpen(true)}
            className="flex items-center gap-2 rounded-xl bg-gradient-to-b from-amber-400 to-amber-500 px-5 py-3 text-sm font-bold text-amber-950 shadow-[0_6px_14px_rgba(245,158,11,0.35)] transition hover:brightness-105 active:scale-95"
          >
            <Plus size={18} /> New Ticket
          </button>
        </div>
      </div>

      {/* Status Filter Chips */}
      <div className="flex flex-wrap gap-2">
        {STATUS_FILTERS.map((sf) => {
          const Icon = sf.icon;
          const count = statusCounts[sf.key];
          const active = statusFilter === sf.key;
          return (
            <button
              key={sf.key}
              onClick={() => setStatusFilter(sf.key)}
              className={`flex items-center gap-1.5 rounded-xl border px-3.5 py-2 text-xs font-bold transition ${
                active
                  ? "border-amber-400 bg-amber-50 text-amber-800 shadow-sm"
                  : "border-slate-200 bg-white text-slate-600 hover:border-amber-200 hover:bg-amber-50/30"
              }`}
            >
              <Icon size={14} />
              {sf.label}
              {count > 0 && (
                <span className={`ml-0.5 rounded-full px-1.5 py-0.5 text-[10px] font-bold ${
                  active ? "bg-amber-200 text-amber-900" : "bg-slate-100 text-slate-500"
                }`}>
                  {count}
                </span>
              )}
            </button>
          );
        })}
      </div>

      {/* Search Bar */}
      <div className="relative">
        <Search size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Search tickets by title, category, or location..."
          className="w-full rounded-xl border border-slate-200 bg-white py-3 pl-10 pr-4 text-sm text-slate-900 transition focus:border-amber-400 focus:outline-none focus:ring-2 focus:ring-amber-100"
        />
      </div>

      {error && (
        <div className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-2.5 text-sm text-rose-700">{error}</div>
      )}

      {/* Ticket Grid */}
      {loading ? (
        <div className="flex h-40 items-center justify-center">
          <div className="h-8 w-8 animate-spin rounded-full border-3 border-amber-400 border-t-transparent" />
        </div>
      ) : filteredTickets.length === 0 ? (
        <div className="flex flex-col items-center justify-center rounded-2xl border border-dashed border-slate-200 bg-white py-16">
          <TicketCheck size={40} className="mb-3 text-slate-300" />
          <p className="text-sm font-semibold text-slate-500">
            {tickets.length === 0 ? "No tickets yet" : "No matching tickets"}
          </p>
          <p className="mt-1 text-xs text-slate-400">
            {tickets.length === 0 ? "Create your first ticket to get started" : "Try adjusting your filters"}
          </p>
          {tickets.length === 0 && (
            <button
              onClick={() => setCreateModalOpen(true)}
              className="mt-4 rounded-xl bg-amber-400 px-4 py-2 text-sm font-bold text-amber-950 transition hover:bg-amber-500"
            >
              Create Ticket
            </button>
          )}
        </div>
      ) : (
        <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
          {filteredTickets.map((ticket) => (
            <TicketCard
              key={ticket.id}
              ticket={ticket}
              onClick={() => setSelectedTicketId(ticket.id)}
              onEdit={() => handleEditClick(ticket)}
              onDelete={() => handleDeleteClick(ticket.id)}
            />
          ))}
        </div>
      )}

      {/* Create Modal */}
      <CreateTicketModal
        isOpen={createModalOpen}
        onClose={() => { setCreateModalOpen(false); setTicketToEdit(null); }}
        onSubmit={handleCreateOrUpdate}
        userEmail={user?.email}
        initialData={ticketToEdit}
      />
    </div>
  );
}
