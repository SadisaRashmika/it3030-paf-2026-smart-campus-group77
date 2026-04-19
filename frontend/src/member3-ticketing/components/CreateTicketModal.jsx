import { useState } from "react";
import { X } from "lucide-react";
import ImageUploader from "./ImageUploader";

const CATEGORIES = [
  { value: "HARDWARE", label: "Hardware", icon: "🖥️" },
  { value: "SOFTWARE", label: "Software", icon: "💿" },
  { value: "NETWORK", label: "Network", icon: "🌐" },
  { value: "ELECTRICAL", label: "Electrical", icon: "⚡" },
  { value: "PLUMBING", label: "Plumbing", icon: "🔧" },
  { value: "GENERAL", label: "General", icon: "📋" },
  { value: "OTHER", label: "Other", icon: "📌" }
];

const PRIORITIES = [
  { value: "LOW", label: "Low", color: "text-slate-600 bg-slate-50 border-slate-200" },
  { value: "MEDIUM", label: "Medium", color: "text-blue-700 bg-blue-50 border-blue-200" },
  { value: "HIGH", label: "High", color: "text-amber-700 bg-amber-50 border-amber-200" },
  { value: "CRITICAL", label: "Critical", color: "text-rose-700 bg-rose-50 border-rose-200" }
];

export default function CreateTicketModal({ isOpen, onClose, onSubmit, userEmail }) {
  const [form, setForm] = useState({
    title: "",
    description: "",
    category: "GENERAL",
    priority: "MEDIUM",
    resourceLocation: "",
    contactEmail: userEmail || "",
    contactPhone: ""
  });
  const [images, setImages] = useState([]);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  const handleChange = (field, value) => {
    setForm(prev => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (!form.title.trim()) { setError("Title is required"); return; }
    if (!form.description.trim()) { setError("Description is required"); return; }

    setSubmitting(true);
    try {
      await onSubmit({
        ...form,
        attachments: images
      });
      setForm({ title: "", description: "", category: "GENERAL", priority: "MEDIUM", resourceLocation: "", contactEmail: userEmail || "", contactPhone: "" });
      setImages([]);
      onClose();
    } catch (err) {
      setError(err.message || "Failed to create ticket");
    } finally {
      setSubmitting(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
      <div className="relative w-full max-w-2xl max-h-[90vh] overflow-y-auto rounded-2xl border border-slate-200 bg-white shadow-2xl">
        {/* Header */}
        <div className="sticky top-0 z-10 flex items-center justify-between border-b border-slate-100 bg-white/95 backdrop-blur px-6 py-4 rounded-t-2xl">
          <div>
            <h2 className="text-lg font-bold text-slate-900">Create New Ticket</h2>
            <p className="text-xs text-slate-500 mt-0.5">Report an incident or maintenance request</p>
          </div>
          <button onClick={onClose} className="flex h-8 w-8 items-center justify-center rounded-lg text-slate-400 transition hover:bg-slate-100 hover:text-slate-700">
            <X size={18} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-5">
          {error && (
            <div className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-2.5 text-sm text-rose-700">{error}</div>
          )}

          {/* Title */}
          <div>
            <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-600">Title *</label>
            <input
              type="text"
              value={form.title}
              onChange={(e) => handleChange("title", e.target.value)}
              placeholder="Brief summary of the issue..."
              className="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-900 transition focus:border-amber-400 focus:outline-none focus:ring-2 focus:ring-amber-100"
            />
          </div>

          {/* Category & Priority Row */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-600">Category *</label>
              <div className="grid grid-cols-4 gap-1.5">
                {CATEGORIES.map((cat) => (
                  <button
                    key={cat.value}
                    type="button"
                    onClick={() => handleChange("category", cat.value)}
                    className={`flex flex-col items-center gap-0.5 rounded-lg border px-2 py-2 text-center transition ${
                      form.category === cat.value
                        ? "border-amber-400 bg-amber-50 shadow-sm"
                        : "border-slate-200 bg-white hover:border-amber-200 hover:bg-amber-50/30"
                    }`}
                  >
                    <span className="text-base">{cat.icon}</span>
                    <span className="text-[10px] font-semibold text-slate-600">{cat.label}</span>
                  </button>
                ))}
              </div>
            </div>

            <div>
              <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-600">Priority *</label>
              <div className="space-y-1.5">
                {PRIORITIES.map((p) => (
                  <button
                    key={p.value}
                    type="button"
                    onClick={() => handleChange("priority", p.value)}
                    className={`flex w-full items-center gap-2 rounded-lg border px-3 py-2 text-sm font-semibold transition ${
                      form.priority === p.value
                        ? p.color + " ring-2 ring-amber-200"
                        : "border-slate-200 bg-white text-slate-600 hover:border-slate-300"
                    }`}
                  >
                    <span className={`h-2 w-2 rounded-full ${
                      p.value === "LOW" ? "bg-slate-400" :
                      p.value === "MEDIUM" ? "bg-blue-500" :
                      p.value === "HIGH" ? "bg-amber-500" : "bg-rose-500"
                    }`} />
                    {p.label}
                  </button>
                ))}
              </div>
            </div>
          </div>

          {/* Description */}
          <div>
            <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-600">Description *</label>
            <textarea
              value={form.description}
              onChange={(e) => handleChange("description", e.target.value)}
              rows={4}
              placeholder="Describe the issue in detail..."
              className="w-full resize-none rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-900 transition focus:border-amber-400 focus:outline-none focus:ring-2 focus:ring-amber-100"
            />
          </div>

          {/* Resource/Location */}
          <div>
            <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-600">Resource / Location</label>
            <input
              type="text"
              value={form.resourceLocation}
              onChange={(e) => handleChange("resourceLocation", e.target.value)}
              placeholder="e.g. Lab 3, Building A, Room 205..."
              className="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-900 transition focus:border-amber-400 focus:outline-none focus:ring-2 focus:ring-amber-100"
            />
          </div>

          {/* Contact Details */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-600">Contact Email</label>
              <input
                type="email"
                value={form.contactEmail}
                onChange={(e) => handleChange("contactEmail", e.target.value)}
                className="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-900 transition focus:border-amber-400 focus:outline-none focus:ring-2 focus:ring-amber-100"
              />
            </div>
            <div>
              <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-600">Contact Phone</label>
              <input
                type="tel"
                value={form.contactPhone}
                onChange={(e) => handleChange("contactPhone", e.target.value)}
                placeholder="+94 77 123 4567"
                className="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-900 transition focus:border-amber-400 focus:outline-none focus:ring-2 focus:ring-amber-100"
              />
            </div>
          </div>

          {/* Image Attachments */}
          <div>
            <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-600">Evidence (Images)</label>
            <ImageUploader images={images} onChange={setImages} max={3} />
          </div>

          {/* Actions */}
          <div className="flex items-center justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="rounded-xl border border-slate-200 bg-white px-5 py-2.5 text-sm font-bold text-slate-700 transition hover:bg-slate-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="rounded-xl bg-gradient-to-b from-amber-400 to-amber-500 px-6 py-2.5 text-sm font-bold text-amber-950 shadow-[0_4px_12px_rgba(245,158,11,0.35)] transition hover:brightness-105 disabled:opacity-60 disabled:cursor-not-allowed"
            >
              {submitting ? "Creating..." : "Create Ticket"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
