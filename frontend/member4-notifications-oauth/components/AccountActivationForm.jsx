import React, { useState } from "react";
import { member4Api } from "../services/member4Api";

export default function AccountActivationForm() {
	const [email, setEmail] = useState("");
	const [role, setRole] = useState("STUDENT");
	const [otp, setOtp] = useState("");
	const [newPassword, setNewPassword] = useState("");
	const [message, setMessage] = useState("");
	const [loading, setLoading] = useState(false);

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
		<div className="activation-card">
			<h2>Activate Your Account</h2>
			<p>Request an OTP, verify it, then set your password.</p>
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
			{message ? <div className="activation-message">{message}</div> : null}
		</div>
	);
}