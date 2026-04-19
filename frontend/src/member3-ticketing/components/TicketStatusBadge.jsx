const STATUS_STYLES = {
  OPEN: "border-blue-300 bg-blue-50 text-blue-700",
  IN_PROGRESS: "border-amber-300 bg-amber-50 text-amber-700",
  RESOLVED: "border-emerald-300 bg-emerald-50 text-emerald-700",
  CLOSED: "border-slate-300 bg-slate-100 text-slate-600",
  REJECTED: "border-rose-300 bg-rose-50 text-rose-700"
};

const STATUS_LABELS = {
  OPEN: "Open",
  IN_PROGRESS: "In Progress",
  RESOLVED: "Resolved",
  CLOSED: "Closed",
  REJECTED: "Rejected"
};

export default function TicketStatusBadge({ status }) {
  const style = STATUS_STYLES[status] || STATUS_STYLES.OPEN;
  const label = STATUS_LABELS[status] || status;

  return (
    <span className={`inline-flex items-center gap-1 rounded-full border px-2.5 py-1 text-xs font-bold ${style}`}>
      <span className={`inline-block h-1.5 w-1.5 rounded-full ${
        status === "OPEN" ? "bg-blue-500" :
        status === "IN_PROGRESS" ? "bg-amber-500 animate-pulse" :
        status === "RESOLVED" ? "bg-emerald-500" :
        status === "CLOSED" ? "bg-slate-400" :
        "bg-rose-500"
      }`} />
      {label}
    </span>
  );
}
