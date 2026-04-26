import { useState } from "react";
import { useOutletContext } from "react-router-dom";
import { AlertCircle, ArrowLeft, BadgeHelp, CheckCircle2, FileText, ShieldCheck, Upload } from "lucide-react";
import { submitRecoveryRequest } from "../services/authService";

const initialForm = {
	userId: "",
	studentEmail: "",
	contactEmail: "",
	reason: "",
	idPhoto: null
};

const MAX_PHOTO_SIZE = 2 * 1024 * 1024;

export default function HelpPage() {
	const { onLogin } = useOutletContext();
	const [form, setForm] = useState(initialForm);
	const [loading, setLoading] = useState(false);
	const [error, setError] = useState("");
	const [message, setMessage] = useState("");

	const updateField = (field, value) => {
		setForm((prev) => ({ ...prev, [field]: value }));
	};

	const handleSubmit = async (event) => {
		event.preventDefault();
		setError("");
		setMessage("");

		if (!form.idPhoto) {
			setError("Please upload your student ID photo.");
			return;
		}

		if (form.idPhoto.size > MAX_PHOTO_SIZE) {
			setError("ID photo must be 2MB or smaller.");
			return;
		}

		setLoading(true);
		try {
			const idPhotoDataUrl = await readFileAsDataUrl(form.idPhoto);
			const result = await submitRecoveryRequest({
				userId: form.userId,
				studentEmail: form.studentEmail,
				contactEmail: form.contactEmail,
				issueSummary: form.reason,
				idPhotoFileName: form.idPhoto.name,
				idPhotoContentType: form.idPhoto.type || "image/jpeg",
				idPhotoDataUrl
			});

			setMessage(`${result.message} Reference #${result.id}.`);
			setForm(initialForm);
		} catch (submitError) {
			setError(submitError.message || "Unable to submit your request.");
		} finally {
			setLoading(false);
		}
	};

	return (
		<main className="mx-auto min-h-[calc(100vh-5rem)] w-full max-w-7xl px-4 py-6 sm:px-6 sm:py-8">
			<section className="help-page-shell overflow-hidden rounded-[2rem] border border-slate-200 bg-white shadow-[0_30px_80px_-40px_rgba(15,23,42,0.45)]">
				<div className="grid gap-0 lg:grid-cols-[1.05fr_0.95fr]">
					<div className="help-hero-panel relative overflow-hidden bg-[radial-gradient(circle_at_top_left,_rgba(250,204,21,0.35),_transparent_36%),linear-gradient(145deg,_#fffdf3,_#fff8dc_58%,_#fffef9)] px-6 py-8 text-slate-900 sm:px-8 sm:py-10">
						<div className="help-hero-glow absolute right-0 top-0 h-40 w-40 translate-x-1/2 -translate-y-1/2 rounded-full bg-[#ffc111]/30 blur-3xl" />
						<div className="relative max-w-xl space-y-5">
							<div className="inline-flex items-center gap-2 rounded-full border border-amber-300/60 bg-amber-100 px-4 py-1.5 text-xs font-bold uppercase tracking-[0.22em] text-amber-800">
								<BadgeHelp size={14} /> Need help
							</div>
							<h1 className="font-display text-4xl font-black leading-tight sm:text-5xl">Activate or recover your SmartCampus account</h1>
							<p className="max-w-2xl text-sm leading-7 text-slate-700 sm:text-base">
								If you just received onboarding details, follow the activation steps below. If you lost access to your phone or can no longer sign in, submit a recovery request and the admin team will review it manually.
							</p>
							<div className="flex flex-wrap gap-3 pt-2">
								<button type="button" onClick={() => onLogin("login")} className="inline-flex items-center gap-2 rounded-2xl border border-slate-300 bg-white px-4 py-2.5 text-sm font-bold text-slate-700 transition hover:bg-slate-50">
									<ArrowLeft size={16} /> Open login
								</button>
								<button type="button" onClick={() => onLogin("activate")} className="inline-flex items-center gap-2 rounded-2xl bg-[#ffc111] px-4 py-2.5 text-sm font-bold text-slate-950 transition hover:brightness-95">
									<ShieldCheck size={16} /> Open activation form
								</button>
							</div>

							<section className="rounded-3xl border border-amber-200 bg-white p-5 shadow-sm">
								<div className="flex items-center gap-2 text-sm font-bold uppercase tracking-[0.18em] text-slate-500">
									<FileText size={15} /> Activation steps
								</div>
								<ol className="mt-4 space-y-3 text-sm leading-6 text-slate-600">
									<li className="rounded-2xl bg-slate-50 px-4 py-3"><strong className="text-slate-900">1.</strong> Open the activation form from the login screen.</li>
									<li className="rounded-2xl bg-slate-50 px-4 py-3"><strong className="text-slate-900">2.</strong> Enter your student or lecturer ID and campus email.</li>
									<li className="rounded-2xl bg-slate-50 px-4 py-3"><strong className="text-slate-900">3.</strong> Check your email for the activation OTP and finish the setup.</li>
								</ol>
							</section>
						</div>
					</div>

					<div className="help-recovery-panel space-y-6 bg-slate-50 px-5 py-6 sm:px-8 sm:py-8">
						<section className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
							<div className="flex items-center gap-2 text-sm font-bold uppercase tracking-[0.18em] text-slate-500">
								<Upload size={15} /> Recover your account
							</div>
							<p className="mt-3 text-sm leading-6 text-slate-600">
								Use this form if you lost access to your account. The admin team will review your student ID photo and contact you at the new email address you provide.
							</p>

							<form onSubmit={handleSubmit} className="mt-5 space-y-4">
								<div className="grid gap-4 sm:grid-cols-2">
									<Input label="Student ID number" value={form.userId} onChange={(value) => updateField("userId", value)} placeholder="STU001" required />
									<Input label="Student email" type="email" value={form.studentEmail} onChange={(value) => updateField("studentEmail", value)} placeholder="you@campus.edu" required />
								</div>
								<Input label="New contact email" type="email" value={form.contactEmail} onChange={(value) => updateField("contactEmail", value)} placeholder="new.email@example.com" required />
								<label className="block">
									<span className="mb-1.5 block text-xs font-bold uppercase tracking-[0.18em] text-slate-500">Problem summary</span>
									<textarea
										value={form.reason}
										onChange={(event) => updateField("reason", event.target.value)}
										rows={4}
										placeholder="Tell us what happened, such as lost phone, changed number, or cannot access the old email."
										className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-medium text-slate-900 outline-none transition focus:border-[#ffc111] focus:ring-2 focus:ring-amber-100"
										required
									/>
								</label>
								<label className="block">
									<span className="mb-1.5 block text-xs font-bold uppercase tracking-[0.18em] text-slate-500">Student ID photo</span>
									<input
										type="file"
										accept="image/*"
										onChange={(event) => updateField("idPhoto", event.target.files?.[0] || null)}
										className="block w-full cursor-pointer rounded-2xl border border-dashed border-slate-300 bg-slate-50 px-4 py-3 text-sm text-slate-600 file:mr-4 file:rounded-xl file:border-0 file:bg-slate-900 file:px-4 file:py-2 file:text-sm file:font-bold file:text-white"
										required
									/>
								</label>

								{form.idPhoto ? (
									<div className="rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
										<CheckCircle2 size={14} className="mr-2 inline-block align-text-top" />
										{form.idPhoto.name}
									</div>
								) : null}

								{error ? (
									<div className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
										<AlertCircle size={14} className="mr-2 inline-block align-text-top" />
										{error}
									</div>
								) : null}

								{message ? (
									<div className="rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
										<CheckCircle2 size={14} className="mr-2 inline-block align-text-top" />
										{message}
									</div>
								) : null}

								<button type="submit" disabled={loading} className="inline-flex w-full items-center justify-center rounded-2xl bg-[#ffc111] px-4 py-3 text-sm font-bold text-slate-950 transition hover:brightness-95 disabled:cursor-not-allowed disabled:opacity-70">
									{loading ? "Submitting..." : "Submit recovery request"}
								</button>
							</form>
						</section>
					</div>
				</div>
			</section>
		</main>
	);
}

function Input({ label, value, onChange, type = "text", placeholder, required }) {
	return (
		<label className="block">
			<span className="mb-1.5 block text-xs font-bold uppercase tracking-[0.18em] text-slate-500">{label}</span>
			<input
				type={type}
				value={value}
				onChange={(event) => onChange(event.target.value)}
				placeholder={placeholder}
				required={required}
				className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-medium text-slate-900 outline-none transition focus:border-[#ffc111] focus:ring-2 focus:ring-amber-100"
			/>
		</label>
	);
}

function readFileAsDataUrl(file) {
	return new Promise((resolve, reject) => {
		const reader = new FileReader();
		reader.onload = () => resolve(String(reader.result || ""));
		reader.onerror = () => reject(new Error("Unable to read the uploaded image."));
		reader.readAsDataURL(file);
	});
}
