import { ShieldCheck, UserPlus } from "lucide-react";
import { useState } from "react";

const ROLE_OPTIONS = [
	{ value: "LECTURER", label: "Lecturer" },
	{ value: "STUDENT", label: "Student" }
];

export default function AdminRoleManagementPanel({ onCreateStaffLogin }) {
	const [form, setForm] = useState({
		name: "",
		email: "",
		role: "LECTURER"
	});
	const [submitting, setSubmitting] = useState(false);
	const [message, setMessage] = useState("");
	const [error, setError] = useState("");

	const handleSubmit = async (event) => {
		event.preventDefault();
		if (!onCreateStaffLogin) {
			setError("Create user action is not available.");
			return;
		}

		if (!form.name.trim()) {
			setError("Full Name is required.");
			return;
		}

		if (!form.email.trim()) {
			setError("Email is required.");
			return;
		}

		if (!form.email.includes("@")) {
			setError("Enter a valid email address.");
			return;
		}

		setSubmitting(true);
		setMessage("");
		setError("");
		try {
			const createdUser = await onCreateStaffLogin({
				name: form.name.trim(),
				email: form.email.trim(),
				role: form.role
			});

			setMessage(
				`User created successfully. ID: ${createdUser.userId}. An activation email with OTP was sent to ${createdUser.email}.`
			);
			setForm({ name: "", email: "", role: form.role });
		} catch (submitError) {
			setError(submitError.message || "Unable to create user.");
		} finally {
			setSubmitting(false);
		}
	};

	return (
		<section className="space-y-4">
			<div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
				<p className="text-xs font-bold uppercase tracking-widest text-slate-500">Admin Controls</p>
				<h2 className="mt-1 text-2xl font-bold text-slate-900">Role Management</h2>
				<p className="mt-1 text-sm text-slate-600">
					Create new users. The system auto-generates user IDs (like LEC001), sends activation details by email,
					and the user can activate from the Activate page.
				</p>
			</div>

			<article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
				<p className="mb-3 inline-flex items-center gap-2 text-sm font-bold text-slate-800">
					<UserPlus size={16} /> Add New User
				</p>
				<form className="space-y-3" onSubmit={handleSubmit}>
					<div>
						<label className="mb-1 block text-xs font-semibold uppercase tracking-wider text-slate-500">Full Name</label>
						<input
							required
							value={form.name}
							onChange={(event) => setForm((prev) => ({ ...prev, name: event.target.value }))}
							className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 outline-none ring-amber-200 focus:ring"
							placeholder="Dr. Prabash Perera"
						/>
					</div>
					<div>
						<label className="mb-1 block text-xs font-semibold uppercase tracking-wider text-slate-500">Email</label>
						<input
							type="email"
							required
							value={form.email}
							onChange={(event) => setForm((prev) => ({ ...prev, email: event.target.value }))}
							className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 outline-none ring-amber-200 focus:ring"
							placeholder="lecturer@smartcampus.edu"
						/>
					</div>
					<div>
						<label className="mb-1 block text-xs font-semibold uppercase tracking-wider text-slate-500">Role</label>
						<select
							value={form.role}
							onChange={(event) => setForm((prev) => ({ ...prev, role: event.target.value }))}
							className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 outline-none ring-amber-200 focus:ring"
						>
							{ROLE_OPTIONS.map((option) => (
								<option key={option.value} value={option.value}>
									{option.label}
								</option>
							))}
						</select>
					</div>

					<div className="rounded-xl border border-amber-200 bg-amber-50 px-3 py-2 text-xs text-amber-900">
						<ShieldCheck size={14} className="mr-1 inline" />
						User ID is generated automatically and sent with OTP to the entered email.
					</div>

					<div className="flex justify-end">
						<button
							type="submit"
							disabled={submitting}
							className="inline-flex items-center rounded-lg bg-gradient-to-b from-amber-400 to-amber-500 px-4 py-2 text-sm font-bold text-amber-950 shadow-[0_6px_14px_rgba(245,158,11,0.35)] transition hover:brightness-105 disabled:cursor-not-allowed disabled:opacity-60"
						>
							{submitting ? "Creating..." : "Create User"}
						</button>
					</div>
				</form>

				{message ? <p className="mt-3 rounded-lg bg-emerald-50 px-3 py-2 text-xs font-medium text-emerald-800">{message}</p> : null}
				{error ? <p className="mt-3 rounded-lg bg-rose-50 px-3 py-2 text-xs font-medium text-rose-800">{error}</p> : null}
			</article>
		</section>
	);
}
