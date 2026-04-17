import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft, BadgeHelp } from "lucide-react";
import { member4Api } from "../services/member4Api";

export default function AccountActivationPage() {
	const [email, setEmail] = useState("");
	const [role, setRole] = useState("STUDENT");
	const [otp, setOtp] = useState("");
	const [newPassword, setNewPassword] = useState("");
	const [message, setMessage] = useState("");
	const [loading, setLoading] = useState(false);
	const navigate = useNavigate();

	async function handleSendOtp(event) {
		event.preventDefault();
		setLoading(true);
		setMessage("");
		try {
			const result = await member4Api.requestOtp({ email, role });
			setMessage(result.message);
		} catch (error) {
			setMessage(error.message);
		} finally {
			setLoading(false);
		}
	}

	async function handleVerify(event) {
		event.preventDefault();
		setLoading(true);
		setMessage("");
		try {
			const result = await member4Api.verifyOtp({ email, otp, newPassword });
			setMessage(result.message);
		} catch (error) {
			setMessage(error.message);
		} finally {
			setLoading(false);
		}
	}

	return (
		<main className="page-shell">
			<div className="activation-card space-y-4 rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
				<div className="flex flex-wrap items-start justify-between gap-3">
					<div>
						<p className="text-xs font-bold uppercase tracking-[0.22em] text-slate-500">Account activation</p>
						<h2 className="mt-1 text-2xl font-bold text-slate-900">Activate Your Account</h2>
						<p className="mt-1 text-sm text-slate-600">Request an OTP, verify it, then set your password.</p>
					</div>
					<button
						type="button"
						onClick={() => navigate("/help")}
						className="inline-flex items-center gap-2 rounded-2xl border border-slate-200 bg-slate-50 px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-100"
					>
						<BadgeHelp size={15} /> Need help?
					</button>
				</div>
				<form onSubmit={handleSendOtp} className="activation-form">
					<input type="email" value={email} onChange={(event) => setEmail(event.target.value)} placeholder="School email" />
					<select value={role} onChange={(event) => setRole(event.target.value)}>
						<option value="STUDENT">Student</option>
						<option value="LECTURER">Lecturer</option>
					</select>
					<button type="submit" disabled={loading}>Send OTP</button>
				</form>
				<form onSubmit={handleVerify} className="activation-form">
					<input type="text" value={otp} onChange={(event) => setOtp(event.target.value)} placeholder="OTP" />
					<input type="password" value={newPassword} onChange={(event) => setNewPassword(event.target.value)} placeholder="New password" />
					<button type="submit" disabled={loading}>Verify & Activate</button>
				</form>
				<button
					type="button"
					onClick={() => navigate(-1)}
					className="inline-flex items-center gap-2 rounded-2xl border border-slate-200 px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
				>
					<ArrowLeft size={15} /> Back
				</button>
				{message ? <div className="activation-message">{message}</div> : null}
			</div>
		</main>
	);
}