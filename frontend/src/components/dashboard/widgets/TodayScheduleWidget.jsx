import React from "react";
import { Clock, MapPin } from "lucide-react";

function fmtTime(t) {
  if (!t) return "";
  // t can be "HH:mm:ss" (timetable) or ISO string (booking)
  if (t.includes("T")) {
    return new Date(t).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
  }
  const [h, m] = t.split(":");
  const d = new Date();
  d.setHours(+h, +m, 0);
  return d.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
}

export default function TodayScheduleWidget({ entries = [], emptyMessage = "No events today." }) {
  if (entries.length === 0) {
    return (
      <div className="rounded-2xl border-2 border-dashed border-slate-200 p-8 text-center text-sm text-slate-400 font-medium">
        {emptyMessage}
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {entries.map((ev, i) => (
        <div key={i} className="flex items-start gap-4 rounded-2xl bg-white border border-slate-200 p-4 shadow-sm hover:border-blue-200 transition-all">
          <div className="rounded-xl bg-blue-50 p-3 text-blue-600 shrink-0">
            <Clock size={18} />
          </div>
          <div className="flex-1 min-w-0">
            <p className="font-bold text-slate-900 truncate">{ev.title}</p>
            <p className="text-xs text-slate-500 flex items-center gap-1 mt-0.5">
              <MapPin size={11} />
              {ev.resourceName || ev.resource?.name || "—"}
            </p>
          </div>
          <div className="text-right shrink-0">
            <p className="text-xs font-mono font-bold text-slate-700">
              {fmtTime(ev.startTime)} – {fmtTime(ev.endTime)}
            </p>
            {ev.type && (
              <span className={`text-[9px] font-black uppercase tracking-widest px-1.5 py-0.5 rounded-md mt-1 inline-block ${ev.type === "BOOKING" ? "bg-amber-50 text-amber-600" : "bg-blue-50 text-blue-600"}`}>
                {ev.type}
              </span>
            )}
          </div>
        </div>
      ))}
    </div>
  );
}
