import { CheckCircle2, ImagePlus, Upload, X } from "lucide-react";
import { useEffect, useRef, useState } from "react";

const MAX_PROFILE_PHOTO_SIZE = 2 * 1024 * 1024;

function readFileAsDataUrl(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(String(reader.result || ""));
    reader.onerror = () => reject(new Error("Unable to read the selected file."));
    reader.readAsDataURL(file);
  });
}

function getInitials(name, fallback = "SC") {
  return String(name || "")
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase() || "")
    .join("") || fallback;
}

export default function ProfileModal({ isOpen, user, onClose, onSaveProfilePicture, onChangePassword }) {
  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const modalRef = useRef(null);
  const fileInputRef = useRef(null);

  useEffect(() => {
    if (!isOpen) {
      setSelectedFile(null);
      setPreviewUrl("");
      setLoading(false);
      setError("");
      setMessage("");
      return;
    }

    setSelectedFile(null);
    setPreviewUrl("");
    setLoading(false);
    setError("");
    setMessage("");
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen) {
      return;
    }

    const onKeyDown = (event) => {
      if (event.key === "Escape") {
        onClose();
      }
    };

    const onPointerDown = (event) => {
      if (modalRef.current && !modalRef.current.contains(event.target)) {
        onClose();
      }
    };

    window.addEventListener("keydown", onKeyDown);
    document.addEventListener("pointerdown", onPointerDown, true);
    return () => {
      window.removeEventListener("keydown", onKeyDown);
      document.removeEventListener("pointerdown", onPointerDown, true);
    };
  }, [isOpen, onClose]);

  if (!isOpen || !user) {
    return null;
  }

  const displayName = user.name || "SmartCampus User";
  const roleLabel = String(user.role || "").replace("ROLE_", "") || "";
  const activePreview = previewUrl || user.profilePictureDataUrl || "";

  const onFileChange = async (event) => {
    const file = event.target.files?.[0] || null;
    setError("");
    setMessage("");

    if (!file) {
      setSelectedFile(null);
      setPreviewUrl("");
      return;
    }

    if (!String(file.type || "").startsWith("image/")) {
      setSelectedFile(null);
      setPreviewUrl("");
      setError("Please choose an image file.");
      return;
    }

    if (file.size > MAX_PROFILE_PHOTO_SIZE) {
      setSelectedFile(null);
      setPreviewUrl("");
      setError("Profile picture must be 2MB or smaller.");
      return;
    }

    const dataUrl = await readFileAsDataUrl(file);
    setSelectedFile(file);
    setPreviewUrl(dataUrl);
  };

  const onSave = async () => {
    if (!selectedFile || !previewUrl) {
      setError("Select a profile picture first.");
      return;
    }

    setLoading(true);
    setError("");
    setMessage("");

    try {
      await onSaveProfilePicture(previewUrl);
      setMessage("Profile picture updated successfully.");
      setSelectedFile(null);
    } catch (err) {
      setError(err.message || "Unable to update your profile picture right now.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4" style={{ background: "rgba(2,6,23,0.55)", backdropFilter: "blur(7px)" }}>
      <div ref={modalRef} className="relative w-full max-w-2xl overflow-hidden rounded-3xl bg-white shadow-glass ring-1 ring-slate-200">
        <div className="flex items-center justify-between border-b border-slate-100 px-6 py-4">
          <div>
            <p className="text-xs font-bold uppercase tracking-[0.18em] text-slate-500">Profile</p>
            <h2 className="mt-1 text-2xl font-bold text-slate-900">Account details</h2>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="inline-flex h-9 w-9 items-center justify-center rounded-full text-slate-500 transition hover:bg-slate-100 hover:text-slate-700"
            aria-label="Close profile modal"
          >
            <X size={18} />
          </button>
        </div>

          <div className="grid gap-6 px-6 py-6 md:grid-cols-[240px_1fr]">
          <section className="rounded-3xl border border-slate-200 bg-slate-50 p-5">
            <div className="flex flex-col items-center text-center">
                <div className="flex h-36 w-36 items-center justify-center overflow-hidden rounded-full border-4 border-white bg-gradient-to-br from-amber-300 to-amber-100 shadow-sm">
                {activePreview ? (
                  <img src={activePreview} alt="Profile preview" className="h-full w-full object-cover" />
                ) : (
                  <span className="text-4xl font-extrabold text-amber-950">{getInitials(displayName)}</span>
                )}
              </div>

              <p className="mt-4 text-lg font-bold text-slate-900">{displayName}</p>
              <p className="text-sm font-semibold uppercase tracking-wide text-slate-500">{roleLabel}</p>

              <input ref={fileInputRef} type="file" accept="image/*" className="hidden" onChange={onFileChange} />
                <div className="mt-5 w-full space-y-3">
                  <button
                    type="button"
                    onClick={() => fileInputRef.current?.click()}
                    className="inline-flex w-full items-center justify-center gap-2 rounded-2xl border border-slate-300 bg-white px-4 py-3 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
                  >
                    <ImagePlus size={16} /> Choose photo
                  </button>

                  <button
                    type="button"
                    onClick={onSave}
                    disabled={loading || !selectedFile}
                    className="inline-flex w-full items-center justify-center gap-2 rounded-2xl bg-gradient-to-b from-amber-400 to-amber-500 px-4 py-3 text-sm font-bold text-amber-950 shadow-[0_6px_14px_rgba(245,158,11,0.35)] transition hover:brightness-105 disabled:cursor-not-allowed disabled:opacity-60"
                  >
                    <Upload size={16} /> {loading ? "Saving..." : "Save picture"}
                  </button>

                  {selectedFile ? <p className="text-xs font-medium text-slate-500">Selected: {selectedFile.name}</p> : null}
                  <p className="text-xs leading-relaxed text-slate-500">
                    Use a clear image. JPG, PNG, or WebP up to 2MB.
                  </p>
                </div>
            </div>
          </section>

            <section className="space-y-4">
            {error ? (
              <div className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm font-medium text-rose-700">{error}</div>
            ) : null}

            {message ? (
              <div className="flex items-start gap-2 rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-emerald-700">
                <CheckCircle2 size={15} className="mt-0.5 shrink-0" />
                <p className="text-sm font-medium">{message}</p>
              </div>
            ) : null}

              <div className="space-y-3">
                <DetailField label="Name" value={user.name || "-"} />
                <DetailField label="User ID" value={user.userId || "-"} />
                <DetailField label="Email" value={user.email || "-"} />
                <DetailField label="Role" value={roleLabel || "-"} />
              </div>

              <div className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-4">
                <p className="text-sm font-semibold text-slate-800">Change password</p>
                <p className="mt-1 text-sm text-slate-600">
                  Use the OTP flow to request a code and set a new password.
                </p>

                <div className="mt-4 flex flex-wrap items-center gap-3">
                  <button
                    type="button"
                    onClick={onChangePassword}
                    className="inline-flex items-center gap-2 rounded-2xl bg-gradient-to-b from-amber-400 to-amber-500 px-4 py-3 text-sm font-bold text-amber-950 shadow-[0_6px_14px_rgba(245,158,11,0.35)] transition hover:brightness-105"
                  >
                    Change password
                  </button>
                </div>
              </div>
          </section>
        </div>
      </div>
    </div>
  );
}

function DetailField({ label, value }) {
  return (
    <label className="block">
      <span className="mb-1.5 block text-xs font-bold uppercase tracking-[0.18em] text-slate-500">{label}</span>
      <div className="rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-medium text-slate-800 shadow-sm">
        {value}
      </div>
    </label>
  );
}