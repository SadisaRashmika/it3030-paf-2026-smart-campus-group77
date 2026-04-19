import { Clock, MessageSquare, Paperclip, User2 } from "lucide-react";
import TicketStatusBadge from "./TicketStatusBadge";
import TicketPriorityBadge from "./TicketPriorityBadge";

const CATEGORY_ICONS = {
  HARDWARE: "🖥️",
  SOFTWARE: "💿",
  NETWORK: "🌐",
  ELECTRICAL: "⚡",
  PLUMBING: "🔧",
  GENERAL: "📋",
  OTHER: "📌"
};

export default function TicketCard({ ticket, onClick }) {
  const timeAgo = (dateStr) => {
    if (!dateStr) return "";
    const now = new Date();
    const date = new Date(dateStr);
    const diff = Math.floor((now - date) / 1000);
    if (diff < 60) return "just now";
    if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
    return `${Math.floor(diff / 86400)}d ago`;
  };

  return (
    <button
      type="button"
      onClick={onClick}
      className="group w-full rounded-2xl border border-slate-200 bg-white p-5 text-left shadow-sm transition-all hover:border-amber-200 hover:shadow-md hover:shadow-amber-100/40 active:scale-[0.99]"
    >
      <div className="mb-3 flex items-start justify-between gap-3">
        <div className="flex items-center gap-2">
          <span className="text-lg">{CATEGORY_ICONS[ticket.category] || "📌"}</span>
          <h3 className="text-sm font-bold text-slate-900 line-clamp-1 group-hover:text-amber-800 transition">{ticket.title}</h3>
        </div>
        <TicketStatusBadge status={ticket.status} />
      </div>

      <div className="flex flex-wrap items-center gap-3 text-xs text-slate-500">
        <TicketPriorityBadge priority={ticket.priority} />

        {ticket.resourceLocation && (
          <span className="flex items-center gap-1 truncate max-w-[140px]" title={ticket.resourceLocation}>
            📍 {ticket.resourceLocation}
          </span>
        )}

        <span className="flex items-center gap-1">
          <Clock size={12} /> {timeAgo(ticket.createdAt)}
        </span>
      </div>

      <div className="mt-3 flex items-center justify-between border-t border-slate-100 pt-3">
        <div className="flex items-center gap-3 text-xs text-slate-500">
          {ticket.assignedTechnicianEmail && (
            <span className="flex items-center gap-1 text-emerald-600" title={ticket.assignedTechnicianEmail}>
              <User2 size={12} /> Assigned
            </span>
          )}
          {ticket.commentCount > 0 && (
            <span className="flex items-center gap-1">
              <MessageSquare size={12} /> {ticket.commentCount}
            </span>
          )}
          {ticket.attachmentCount > 0 && (
            <span className="flex items-center gap-1">
              <Paperclip size={12} /> {ticket.attachmentCount}
            </span>
          )}
        </div>
        <span className="text-[11px] font-semibold text-slate-400 tracking-wide">#{ticket.id}</span>
      </div>
    </button>
  );
}
