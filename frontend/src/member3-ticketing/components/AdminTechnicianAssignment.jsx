import { useCallback, useEffect, useState } from "react";
import { UserCheck, Users, TicketCheck, Search, Eye } from "lucide-react";
import TicketStatusBadge from "./TicketStatusBadge";
import TicketPriorityBadge from "./TicketPriorityBadge";
import TicketDetailPanel from "./TicketDetailPanel";
import { getAllTickets, assignTechnician } from "../services/ticketService";

export default function AdminTechnicianAssignment({ user }) {
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [viewFilter, setViewFilter] = useState("UNASSIGNED");
  const [selectedTicketId, setSelectedTicketId] = useState(null);
  const [editingTicketId, setEditingTicketId] = useState(null);
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

  const handleAssign = async (ticketId) => {
    if (!techEmail.trim()) return;
    setAssigning(true);
    try {
      await assignTechnician(ticketId, { technicianEmail: techEmail.trim() });
      setEditingTicketId(null);
      setTechEmail("");
      await loadTickets();
    } catch (err) {
      setError(err.message);
    } finally {
      setAssigning(false);
    }
  };

  const unassigned = tickets.filter(t => !t.assignedTechnicianEmail && t.status !== "CLOSED" && t.status !== "REJECTED");
  const assigned = tickets.filter(t => t.assignedTechnicianEmail);

  // Build technician workload
  const technicianMap = {};
  assigned.forEach(t => {
    const email = t.assignedTechnicianEmail;
    if (!technicianMap[email]) {
      technicianMap[email] = { email, total: 0, open: 0, inProgress: 0, resolved: 0 };
    }
    technicianMap[email].total++;
    if (t.status === "OPEN") technicianMap[email].open++;
    if (t.status === "IN_PROGRESS") technicianMap[email].inProgress++;
    if (t.status === "RESOLVED") technicianMap[email].resolved++;
  });
  const technicianList = Object.values(technicianMap).sort((a, b) => b.total - a.total);

  const displayTickets = (viewFilter === "UNASSIGNED" ? unassigned : assigned)
    .filter(t => {
      if (!searchTerm) return true;
      const q = searchTerm.toLowerCase();
      return t.title?.toLowerCase().includes(q) || t.reporterEmail?.toLowerCase().includes(q) || t.assignedTechnicianEmail?.toLowerCase().includes(q);
    });

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
      <div className="rounded-2xl border border-slate-200 bg-gradient-to-r from-white via-emerald-50/20 to-white p-6 shadow-sm">
        <div className="inline-flex items-center gap-2 rounded-xl bg-emerald-50 px-3 py-1.5 text-sm font-bold text-emerald-700 mb-2">
          <Users size={16} /> Technician Management
        </div>
        <h1 className="text-2xl font-bold text-slate-900">Assignment Dashboard</h1>
        <p className="mt-1 text-sm text-slate-500">Assign technicians and monitor workload distribution</p>

        {/* Quick Stats */}
        <div className="mt-4 grid grid-cols-2 gap-3 sm:grid-cols-3">
          <div className="rounded-xl border border-amber-100 bg-amber-50/50 px-4 py-3 text-center">
            <p className="text-2xl font-bold text-amber-700">{unassigned.length}</p>
            <p className="text-[10px] font-bold uppercase text-amber-500">Unassigned</p>
          </div>
          <div className="rounded-xl border border-emerald-100 bg-emerald-50/50 px-4 py-3 text-center">
            <p className="text-2xl font-bold text-emerald-700">{assigned.length}</p>
            <p className="text-[10px] font-bold uppercase text-emerald-500">Assigned</p>
          </div>
          <div className="rounded-xl border border-blue-100 bg-blue-50/50 px-4 py-3 text-center">
            <p className="text-2xl font-bold text-blue-700">{technicianList.length}</p>
            <p className="text-[10px] font-bold uppercase text-blue-500">Technicians</p>
          </div>
        </div>
      </div>

      {/* Technician Workload */}
      {technicianList.length > 0 && (
        <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <h3 className="mb-3 text-xs font-bold uppercase tracking-wide text-slate-500">Technician Workload</h3>
          <div className="grid gap-2 sm:grid-cols-2 lg:grid-cols-3">
            {technicianList.map((tech) => (
              <div key={tech.email} className="flex items-center justify-between rounded-xl border border-slate-100 bg-slate-50/40 px-4 py-3">
                <div className="flex items-center gap-2">
                  <span className="flex h-8 w-8 items-center justify-center rounded-full bg-gradient-to-br from-emerald-400 to-emerald-300 text-xs font-bold text-emerald-950">
                    {tech.email.substring(0, 2).toUpperCase()}
                  </span>
                  <div>
                    <p className="text-sm font-bold text-slate-800">{tech.email.split("@")[0]}</p>
                    <p className="text-[10px] text-slate-400">{tech.email}</p>
                  </div>
                </div>
                <div className="flex items-center gap-2 text-[10px] font-bold">
                  <span className="rounded bg-blue-100 px-1.5 py-0.5 text-blue-700">{tech.open} open</span>
                  <span className="rounded bg-amber-100 px-1.5 py-0.5 text-amber-700">{tech.inProgress} active</span>
                  <span className="rounded bg-emerald-100 px-1.5 py-0.5 text-emerald-700">{tech.resolved} done</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Toggle & Search */}
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
        <div className="flex gap-1.5">
          <button
            onClick={() => setViewFilter("UNASSIGNED")}
            className={`flex items-center gap-1.5 rounded-xl border px-4 py-2 text-xs font-bold transition ${
              viewFilter === "UNASSIGNED"
                ? "border-amber-400 bg-amber-50 text-amber-800"
                : "border-slate-200 bg-white text-slate-600 hover:bg-amber-50/30"
            }`}
          >
            <TicketCheck size={14} /> Unassigned ({unassigned.length})
          </button>
          <button
            onClick={() => setViewFilter("ASSIGNED")}
            className={`flex items-center gap-1.5 rounded-xl border px-4 py-2 text-xs font-bold transition ${
              viewFilter === "ASSIGNED"
                ? "border-emerald-400 bg-emerald-50 text-emerald-800"
                : "border-slate-200 bg-white text-slate-600 hover:bg-emerald-50/30"
            }`}
          >
            <UserCheck size={14} /> Assigned ({assigned.length})
          </button>
        </div>
        <div className="relative flex-1">
          <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search tickets..."
            className="w-full rounded-lg border border-slate-200 bg-white py-2 pl-9 pr-3 text-sm focus:border-amber-400 focus:outline-none focus:ring-2 focus:ring-amber-100"
          />
        </div>
      </div>

      {error && (
        <div className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-2.5 text-sm text-rose-700">{error}</div>
      )}

      {/* Ticket List */}
      {loading ? (
        <div className="flex h-40 items-center justify-center">
          <div className="h-8 w-8 animate-spin rounded-full border-3 border-amber-400 border-t-transparent" />
        </div>
      ) : displayTickets.length === 0 ? (
        <div className="flex flex-col items-center justify-center rounded-2xl border border-dashed border-slate-200 bg-white py-12">
          <Users size={36} className="mb-2 text-slate-300" />
          <p className="text-sm font-semibold text-slate-500">
            {viewFilter === "UNASSIGNED" ? "No unassigned tickets" : "No assigned tickets"}
          </p>
        </div>
      ) : (
        <div className="space-y-2">
          {displayTickets.map((t) => (
            <div key={t.id} className="flex items-center justify-between rounded-xl border border-slate-200 bg-white px-4 py-3 transition hover:border-amber-200 hover:shadow-sm">
              <div className="flex items-center gap-3 flex-1 min-w-0">
                <span className="text-xs font-semibold text-slate-400 w-8">#{t.id}</span>
                <div className="flex-1 min-w-0">
                  <button onClick={() => setSelectedTicketId(t.id)} className="text-sm font-semibold text-slate-800 hover:text-amber-700 transition truncate block">
                    {t.title}
                  </button>
                  <div className="flex items-center gap-2 mt-0.5">
                    <span className="text-[11px] text-slate-400">{t.reporterEmail?.split("@")[0]}</span>
                    {t.resourceLocation && <span className="text-[11px] text-slate-400">📍 {t.resourceLocation}</span>}
                  </div>
                </div>
                <TicketStatusBadge status={t.status} />
                <TicketPriorityBadge priority={t.priority} />
              </div>

              <div className="flex items-center gap-2 ml-3">
                {editingTicketId === t.id ? (
                  <div className="flex items-center gap-2">
                    <input
                      type="email"
                      value={techEmail}
                      onChange={(e) => setTechEmail(e.target.value)}
                      placeholder="tech@email.com"
                      className="w-48 rounded-lg border border-slate-200 px-3 py-1.5 text-xs focus:border-amber-400 focus:outline-none"
                      autoFocus
                    />
                    <button
                      onClick={() => handleAssign(t.id)}
                      disabled={assigning || !techEmail.trim()}
                      className="rounded-lg bg-amber-400 px-3 py-1.5 text-xs font-bold text-amber-950 hover:bg-amber-500 disabled:opacity-50"
                    >
                      {assigning ? "..." : "Assign"}
                    </button>
                    <button
                      onClick={() => { setEditingTicketId(null); setTechEmail(""); }}
                      className="rounded-lg border border-slate-200 px-2 py-1.5 text-xs text-slate-500 hover:bg-slate-50"
                    >
                      ✕
                    </button>
                  </div>
                ) : (
                  <>
                    {t.assignedTechnicianEmail ? (
                      <span className="flex items-center gap-1 text-xs font-semibold text-emerald-600">
                        <UserCheck size={12} /> {t.assignedTechnicianEmail.split("@")[0]}
                      </span>
                    ) : (
                      <button
                        onClick={() => { setEditingTicketId(t.id); setTechEmail(""); }}
                        className="flex items-center gap-1 rounded-lg border border-dashed border-amber-300 bg-amber-50/50 px-3 py-1.5 text-xs font-bold text-amber-700 transition hover:bg-amber-100"
                      >
                        <Users size={12} /> Assign
                      </button>
                    )}
                    <button onClick={() => setSelectedTicketId(t.id)} className="flex h-7 w-7 items-center justify-center rounded-lg text-slate-400 hover:bg-slate-100 hover:text-slate-700">
                      <Eye size={14} />
                    </button>
                  </>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
