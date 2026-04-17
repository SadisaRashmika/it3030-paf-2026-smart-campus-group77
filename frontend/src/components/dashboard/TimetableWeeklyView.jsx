import React, { useState, useEffect } from "react";
import { Plus, Trash2, X, CalendarDays, RefreshCw } from "lucide-react";
import {
  getTimetable,
  createTimetableEntry,
  deleteTimetableEntry,
  getAllResources,
  joinSession,
} from "../../services/bookingService";

const DAYS = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"];
const HOURS = Array.from({ length: 13 }, (_, i) => i + 8); // 8 AM – 8 PM

const RESOURCE_COLORS = {
  HALL: "bg-blue-100 border-blue-300 text-blue-800",
  LAB: "bg-emerald-100 border-emerald-300 text-emerald-800",
  ROOM: "bg-amber-100 border-amber-300 text-amber-800",
  AUDITORIUM: "bg-violet-100 border-violet-300 text-violet-800",
};

function getDayColor(type) {
  return RESOURCE_COLORS[type] || "bg-slate-100 border-slate-300 text-slate-700";
}

function fmtHour(h) {
  const d = new Date();
  d.setHours(h, 0, 0);
  return d.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
}

function timeToFrac(timeStr) {
  const [h, m] = (timeStr || "00:00").split(":").map(Number);
  return (h - 8) + m / 60;
}

function slotHeight(startStr, endStr) {
  return Math.max(timeToFrac(endStr) - timeToFrac(startStr), 0.5);
}

const EMPTY_FORM = { resourceId: "", dayOfWeek: "MONDAY", startTime: "09:00", endTime: "10:00", title: "", description: "" };

export default function TimetableWeeklyView({ user, readOnly = false }) {
  const [entries, setEntries] = useState([]);
  const [resources, setResources] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(EMPTY_FORM);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [joinedIds, setJoinedIds] = useState(new Set());

  const role = (user?.role || "").replace("ROLE_", "").toLowerCase();
  const canManage = role === "timetable_manager" || role === "admin";

  const fetchAll = async () => {
    try {
      setLoading(true);
      const [entries, res] = await Promise.all([getTimetable(), getAllResources()]);
      setEntries(Array.isArray(entries) ? entries : []);
      setResources(Array.isArray(res) ? res : []);
    } catch (e) {
      setError("Failed to load timetable.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchAll(); }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    if (form.startTime >= form.endTime) {
      setError("End time must be after start time.");
      return;
    }
    try {
      setSubmitting(true);
      setError("");
      await createTimetableEntry({
        resourceId: parseInt(form.resourceId),
        dayOfWeek: form.dayOfWeek,
        startTime: form.startTime,
        endTime: form.endTime,
        title: form.title,
        description: form.description || null,
      });
      setShowForm(false);
      setForm(EMPTY_FORM);
      await fetchAll();
    } catch (err) {
      setError(err.message || "Failed to create entry.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Remove this timetable entry?")) return;
    try {
      await deleteTimetableEntry(id);
      setEntries((prev) => prev.filter((e) => e.id !== id));
    } catch (err) {
      alert(err.message || "Failed to delete.");
    }
  };

  const handleJoin = async (entry) => {
    try {
      await joinSession(entry.id);
      setJoinedIds((prev) => new Set([...prev, entry.id]));
      alert("Registered for session!");
    } catch (err) {
      alert(err.message || "Could not register.");
    }
  };

  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center gap-3 text-slate-400">
        <RefreshCw className="animate-spin" size={24} />
        <span className="font-medium">Loading timetable...</span>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2 text-slate-700">
          <CalendarDays size={22} className="text-blue-600" />
          <h2 className="text-xl font-extrabold tracking-tight text-slate-900">Weekly Timetable</h2>
        </div>
        <div className="flex gap-2">
          <button onClick={fetchAll} className="flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs font-bold text-slate-600 hover:bg-slate-50 shadow-sm">
            <RefreshCw size={14} /> Refresh
          </button>
          {canManage && (
            <button
              onClick={() => { setShowForm(true); setError(""); }}
              className="flex items-center gap-1.5 rounded-xl bg-blue-600 px-4 py-2 text-xs font-bold text-white hover:bg-blue-700 shadow-md shadow-blue-100"
            >
              <Plus size={14} /> Add Class
            </button>
          )}
        </div>
      </div>

      {error && (
        <div className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700 font-medium">{error}</div>
      )}

      {/* Legend */}
      <div className="flex flex-wrap gap-2">
        {Object.entries(RESOURCE_COLORS).map(([type, cls]) => (
          <span key={type} className={`px-2.5 py-1 rounded-lg text-[10px] font-black uppercase border ${cls}`}>{type}</span>
        ))}
      </div>

      {/* Grid */}
      <div className="overflow-x-auto rounded-2xl border border-slate-200 bg-white shadow-sm">
        <div className="min-w-[720px]">
          {/* Day Headers */}
          <div className="grid border-b border-slate-100" style={{ gridTemplateColumns: "60px repeat(6, 1fr)" }}>
            <div className="border-r border-slate-100 p-2" />
            {DAYS.map((d) => (
              <div key={d} className="border-r border-slate-100 p-3 text-center text-xs font-black uppercase tracking-widest text-slate-400 last:border-r-0">
                {d.slice(0, 3)}
              </div>
            ))}
          </div>

          {/* Time rows */}
          {HOURS.map((hour, hi) => (
            <div key={hour} className="grid border-b border-slate-100 last:border-b-0" style={{ gridTemplateColumns: "60px repeat(6, 1fr)", minHeight: "56px" }}>
              {/* Time label */}
              <div className="border-r border-slate-100 px-2 py-1 text-[10px] font-mono text-slate-400 text-right">
                {fmtHour(hour)}
              </div>

              {/* Day columns */}
              {DAYS.map((day) => {
                const dayEntries = entries.filter((e) => {
                  const eDay = e.dayOfWeek;
                  const startH = parseInt((e.startTime || "").split(":")[0]);
                  return eDay === day && startH === hour;
                });
                return (
                  <div key={day} className="border-r border-slate-100 last:border-r-0 p-1 relative min-h-[56px]">
                    {dayEntries.map((entry) => {
                      const h = slotHeight(entry.startTime, entry.endTime);
                      const resType = entry.resource?.type || "ROOM";
                      const colorCls = getDayColor(resType);
                      const joined = joinedIds.has(entry.id);
                      return (
                        <div
                          key={entry.id}
                          className={`rounded-xl border px-2 py-1.5 shadow-sm text-[11px] leading-tight overflow-hidden ${colorCls}`}
                          style={{ minHeight: `${h * 56}px` }}
                        >
                          <p className="font-bold truncate">{entry.title}</p>
                          <p className="opacity-70 truncate">{entry.resource?.name}</p>
                          <p className="opacity-60 font-mono text-[10px]">
                            {entry.startTime?.slice(0,5)} – {entry.endTime?.slice(0,5)}
                          </p>
                          {canManage && (
                            <button
                              onClick={() => handleDelete(entry.id)}
                              className="mt-1 flex items-center gap-0.5 text-[10px] opacity-60 hover:opacity-100 hover:text-rose-600"
                            >
                              <Trash2 size={10} /> Remove
                            </button>
                          )}
                          {!canManage && !readOnly && (
                            <button
                              disabled={joined}
                              onClick={() => handleJoin(entry)}
                              className={`mt-1 text-[10px] font-bold rounded px-1.5 py-0.5 transition-all ${joined ? "bg-emerald-200 text-emerald-800" : "bg-white/60 hover:bg-white text-current"}`}
                            >
                              {joined ? "Registered ✓" : "Register"}
                            </button>
                          )}
                        </div>
                      );
                    })}
                  </div>
                );
              })}
            </div>
          ))}
        </div>
      </div>

      {/* Add Entry Modal */}
      {showForm && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm">
          <div className="w-full max-w-lg rounded-3xl bg-white shadow-2xl">
            <div className="bg-blue-600 rounded-t-3xl p-6 text-white">
              <div className="flex items-center justify-between">
                <h3 className="text-xl font-bold">Add Recurring Class</h3>
                <button onClick={() => setShowForm(false)} className="rounded-xl bg-white/10 p-2 hover:bg-white/20">
                  <X size={20} />
                </button>
              </div>
            </div>
            <form onSubmit={handleCreate} className="p-6 space-y-4">
              {error && <p className="text-rose-600 text-sm font-medium">{error}</p>}
              <div>
                <label className="block text-xs font-bold text-slate-600 mb-1 uppercase tracking-wide">Class Title *</label>
                <input required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })}
                  className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm focus:border-blue-500 outline-none"
                  placeholder="e.g. IT3030 Lecture" />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-slate-600 mb-1 uppercase tracking-wide">Resource *</label>
                  <select required value={form.resourceId} onChange={(e) => setForm({ ...form, resourceId: e.target.value })}
                    className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm focus:border-blue-500 outline-none">
                    <option value="">Select...</option>
                    {resources.map((r) => <option key={r.id} value={r.id}>{r.name} ({r.type})</option>)}
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-600 mb-1 uppercase tracking-wide">Day *</label>
                  <select value={form.dayOfWeek} onChange={(e) => setForm({ ...form, dayOfWeek: e.target.value })}
                    className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm focus:border-blue-500 outline-none">
                    {DAYS.map((d) => <option key={d} value={d}>{d}</option>)}
                  </select>
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-slate-600 mb-1 uppercase tracking-wide">Start Time *</label>
                  <input required type="time" value={form.startTime} onChange={(e) => setForm({ ...form, startTime: e.target.value })}
                    className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm focus:border-blue-500 outline-none" />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-600 mb-1 uppercase tracking-wide">End Time *</label>
                  <input required type="time" value={form.endTime} onChange={(e) => setForm({ ...form, endTime: e.target.value })}
                    className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm focus:border-blue-500 outline-none" />
                </div>
              </div>
              <div>
                <label className="block text-xs font-bold text-slate-600 mb-1 uppercase tracking-wide">Description</label>
                <input value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })}
                  className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm focus:border-blue-500 outline-none"
                  placeholder="Optional notes..." />
              </div>
              <button type="submit" disabled={submitting}
                className="w-full rounded-xl bg-blue-600 py-3.5 text-sm font-bold text-white hover:bg-blue-700 transition-all disabled:opacity-50">
                {submitting ? "Saving..." : "Add to Timetable"}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
