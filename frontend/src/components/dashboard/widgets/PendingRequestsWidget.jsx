import React from "react";
import { MapPin, X } from "lucide-react";

export default function PendingRequestsWidget({ bookings = [], onCancel }) {
  if (bookings.length === 0) {
    return (
      <div className="rounded-2xl border-2 border-dashed border-slate-200 p-6 text-center text-sm text-slate-400 font-medium">
        No pending requests.
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {bookings.map((b) => (
        <div key={b.id} className="flex items-center gap-3 rounded-2xl bg-white border border-amber-200 p-4 shadow-sm">
          <div className="rounded-xl bg-amber-50 p-2.5 text-amber-600 shrink-0">
            <MapPin size={16} />
          </div>
          <div className="flex-1 min-w-0">
            <p className="font-bold text-slate-900 text-sm truncate">{b.resource?.name}</p>
            <p className="text-[11px] text-slate-500 font-mono">
              {new Date(b.startTime).toLocaleDateString()} · {new Date(b.startTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })} – {new Date(b.endTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
            </p>
          </div>
          <span className="text-[9px] font-black uppercase tracking-widest px-2 py-1 rounded-lg bg-amber-50 text-amber-700 border border-amber-200">
            Pending
          </span>
          {onCancel && (
            <button
              onClick={() => onCancel(b.id)}
              className="rounded-lg p-1.5 hover:bg-rose-50 hover:text-rose-600 text-slate-400 transition-all"
              title="Cancel request"
            >
              <X size={16} />
            </button>
          )}
        </div>
      ))}
    </div>
  );
}
