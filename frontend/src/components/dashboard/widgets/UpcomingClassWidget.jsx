import React, { useMemo } from "react";
import { CheckCircle2, Clock, MapPin, UserPlus } from "lucide-react";

function parseLocalTime(timeStr) {
  const [h, m] = (timeStr || "00:00").split(":");
  const d = new Date();
  d.setHours(+h, +m, 0, 0);
  return d;
}

export default function UpcomingClassWidget({ nextClass, hasMarked, onMarkAttendance, isProcessing }) {
  const now = new Date();

  const { windowState, minutesUntil } = useMemo(() => {
    if (!nextClass) return { windowState: "none", minutesUntil: null };
    const classStart = parseLocalTime(nextClass.startTime);
    const diff = (classStart - now) / 60000; // minutes
    let windowState;
    if (diff > 15)       windowState = "early";
    else if (diff >= -15) windowState = "open";
    else                  windowState = "late";
    return { windowState, minutesUntil: Math.round(diff) };
  }, [nextClass, now]);

  if (!nextClass) {
    return (
      <div className="rounded-2xl border-2 border-dashed border-slate-200 p-8 text-center text-sm text-slate-400 font-medium">
        No upcoming classes today.
      </div>
    );
  }

  return (
    <div className="rounded-2xl border border-blue-200 bg-gradient-to-br from-blue-50 to-indigo-50 p-6 shadow-sm">
      <div className="flex items-start justify-between gap-4">
        <div className="flex-1 space-y-2">
          <p className="text-xs font-black uppercase tracking-widest text-blue-500">Next Class</p>
          <h3 className="text-xl font-extrabold text-slate-900 leading-tight">{nextClass.title}</h3>
          <div className="flex flex-wrap gap-4 text-sm font-medium text-slate-600">
            <span className="flex items-center gap-1.5"><Clock size={14} />{nextClass.startTime} – {nextClass.endTime}</span>
            <span className="flex items-center gap-1.5"><MapPin size={14} />{nextClass.resource?.name || nextClass.resourceName || "—"}</span>
          </div>
          {minutesUntil !== null && windowState === "early" && (
            <p className="text-xs text-slate-400 font-medium">Starts in {minutesUntil} min</p>
          )}
        </div>

        {hasMarked ? (
          <div className="flex flex-col items-center gap-1 shrink-0">
            <div className="flex items-center gap-2 rounded-xl bg-emerald-50 border border-emerald-200 px-4 py-2.5">
              <CheckCircle2 size={20} className="text-emerald-500" />
              <span className="text-sm font-bold text-emerald-700">Marked ✓</span>
            </div>
          </div>
        ) : (
          <button
            disabled={windowState !== "open" || isProcessing}
            onClick={onMarkAttendance}
            className={`flex items-center gap-2 rounded-xl px-5 py-3 text-sm font-bold transition-all active:scale-95 shrink-0 ${
              windowState === "open"
                ? "bg-blue-600 text-white shadow-lg shadow-blue-100 hover:bg-blue-700"
                : "bg-slate-100 text-slate-400 cursor-not-allowed"
            }`}
          >
            <UserPlus size={18} />
            {windowState === "early"
              ? `Too Early (${minutesUntil}m)`
              : windowState === "late"
              ? "Window Closed"
              : isProcessing ? "Marking..." : "Mark Attendance"}
          </button>
        )}
      </div>
    </div>
  );
}
