import { useEffect, useState } from "react";
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

export default function CreateTicketModal({ isOpen, onClose, onSubmit, userEmail, initialData }) {
  const [form, setForm] = useState({
    title: "",
    description: "",
    category: "GENERAL",
    priority: "MEDIUM",
    resourceLocation: "",
    contactEmail: userEmail || "",
    contactPhone: ""
  });

  useEffect(() => {
    if (isOpen) {
      setForm({
        title: initialData?.title || "",
        description: initialData?.description || "",
        category: initialData?.category || "GENERAL",
        priority: initialData?.priority || "MEDIUM",
        resourceLocation: initialData?.resourceLocation || "",
        contactEmail: initialData?.contactEmail || userEmail || "",
        contactPhone: initialData?.contactPhone || ""
      });
      setImages([]);
    }
  }, [isOpen, initialData, userEmail]);
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

    // Phone validation: starts with 0, total 10 digits
    if (form.contactPhone && !/^0\d{9}$/.test(form.contactPhone)) {
      setError("Contact phone must start with 0 and be exactly 10 digits (e.g., 0711345678)");
      return;
    }

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
      <div className="relative w-full max-w-5xl rounded-2xl border border-slate-200 bg-white shadow-2xl">
        {/* Header */}
        <div className="flex items-center justify-between border-b border-slate-100 bg-white px-6 py-4 rounded-t-2xl">
          <div>
            <h2 className="text-lg font-bold text-slate-900">{initialData ? "Edit Ticket" : "Create New Ticket"}</h2>
            <p className="text-xs text-slate-500 mt-0.5">{initialData ? "Modify your ticket details" : "Report an incident or maintenance request"}</p>
          </div>
          <button onClick={onClose} className="flex h-8 w-8 items-center justify-center rounded-lg text-slate-400 transition hover:bg-slate-100 hover:text-slate-700">
            <X size={18} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6">
          {error && (
            <div className="mb-5 rounded-xl border border-rose-200 bg-rose-50 px-4 py-2 text-sm text-rose-700">{error}</div>
          )}

          <div className="space-y-6">
            {/* Top Row: Title & Priority */}
            <div className="grid grid-cols-1 gap-5 sm:grid-cols-12">
              <div className="sm:col-span-8">
                <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-600">Title *</label>
                <input
                  type="text"
                  value={form.title}
                  onChange={(e) => handleChange("title", e.target.value)}
                  placeholder="Brief summary of the issue..."
                  className="w-full rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-sm transition focus:border-amber-400 focus:outline-none focus:ring-2 focus:ring-amber-100"
                />
              </div>
              <div className="sm:col-span-4">
                <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-600">Priority *</label>
                <div className="flex gap-1.5">
                  {PRIORITIES.map((p) => (
                    <button
                      key={p.value}
                      type="button"
                      onClick={() => handleChange("priority", p.value)}
                      title={p.label}
                      className={`flex flex-1 items-center justify-center rounded-lg border py-2 transition ${
                        form.priority === p.value
                          ? "border-amber-400 bg-amber-50 ring-2 ring-amber-100"
                          : "border-slate-200 bg-white hover:bg-slate-50"
                      }`}
                    >
                      <span className={`h-2.5 w-2.5 rounded-full ${
                        p.value === "LOW" ? "bg-slate-400" :
                        p.value === "MEDIUM" ? "bg-blue-500" :
                        p.value === "HIGH" ? "bg-amber-500" : "bg-rose-500"
                      }`} />
                    </button>
                  ))}
                </div>
              </div>
            </div>

            {/* Second Row: Category Buttons */}
            <div>
              <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-600">Category *</label>
              <div className="flex flex-wrap gap-2">
                {CATEGORIES.map((cat) => (
                  <button
                    key={cat.value}
                    type="button"
                    onClick={() => handleChange("category", cat.value)}
                    className={`flex items-center gap-2 rounded-xl border px-4 py-2 text-sm font-semibold transition ${
                      form.category === cat.value
                        ? "border-amber-400 bg-amber-50 shadow-sm"
                        : "border-slate-200 bg-white hover:border-amber-200 hover:bg-amber-50/30"
                    }`}
                  >
                    <span>{cat.icon}</span>
                    <span className="text-xs">{cat.label}</span>
                  </button>
                ))}
              </div>
            </div>

            {/* Third Row: Main Content Grid */}
            <div className="grid grid-cols-1 gap-6 sm:grid-cols-12">
              {/* Left Column: Description */}
              <div className="sm:col-span-7 space-y-4">
                <div>
                  <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-600">Description *</label>
                  <textarea
                    value={form.description}
                    onChange={(e) => handleChange("description", e.target.value)}
                    rows={5}
                    placeholder="Describe the issue in detail..."
                    className="w-full resize-none rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm transition focus:border-amber-400 focus:outline-none focus:ring-2 focus:ring-amber-100"
                  />
                </div>
                <div>
                  <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-600">Evidence (Images)</label>
                  <ImageUploader images={images} onChange={setImages} max={3} />
                </div>
              </div>

              {/* Right Column: Details & Contact */}
              <div className="sm:col-span-5 space-y-4">
                <div>
                  <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-600">Resource / Location</label>
                  <input
                    type="text"
                    value={form.resourceLocation}
                    onChange={(e) => handleChange("resourceLocation", e.target.value)}
                    placeholder="e.g. Lab 3, Building A..."
                    className="w-full rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-sm transition focus:border-amber-400 focus:outline-none focus:ring-2 focus:ring-amber-100"
                  />
                </div>
                <div>
                  <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-600">Contact Email</label>
                  <input
                    type="email"
                    value={form.contactEmail}
                    onChange={(e) => handleChange("contactEmail", e.target.value)}
                    className="w-full rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-sm transition focus:border-amber-400 focus:outline-none focus:ring-2 focus:ring-amber-100"
                  />
                </div>
                <div>
                  <label className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-600">Contact Phone</label>
                  <input
                    type="tel"
                    value={form.contactPhone}
                    onChange={(e) => handleChange("contactPhone", e.target.value.replace(/\D/g, ""))}
                    placeholder="0711345678"
                    maxLength={10}
                    className="w-full rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-sm transition focus:border-amber-400 focus:outline-none focus:ring-2 focus:ring-amber-100"
                  />
                  <p className="mt-1 text-[10px] text-slate-400 font-medium">10 digits starting with 0</p>
                </div>
              </div>
            </div>
          </div>

          {/* Footer Actions */}
          <div className="flex items-center justify-end gap-3 pt-6 border-t border-slate-100 mt-6">
            <button
              type="button"
              onClick={onClose}
              className="rounded-xl border border-slate-200 bg-white px-6 py-2.5 text-sm font-bold text-slate-700 hover:bg-slate-50 transition"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="rounded-xl bg-gradient-to-b from-amber-400 to-amber-500 px-8 py-2.5 text-sm font-bold text-amber-950 shadow-md hover:brightness-105 transition disabled:opacity-60"
            >
              {submitting ? (initialData ? "Updating..." : "Creating...") : (initialData ? "Update Ticket" : "Create Ticket")}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
