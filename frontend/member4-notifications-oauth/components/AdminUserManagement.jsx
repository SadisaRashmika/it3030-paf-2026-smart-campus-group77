import React, { useState } from "react";
import { member4Api } from "../services/member4Api";

export default function AdminUserManagement() {
	const [authHeader, setAuthHeader] = useState("Basic ");
	const [users, setUsers] = useState([]);
	const [message, setMessage] = useState("");

	async function loadUsers() {
		try {
			const result = await member4Api.getUsers(authHeader);
			setUsers(result);
			setMessage("");
		} catch (error) {
			setMessage(error.message);
		}
	}

	return (
		<div className="admin-panel">
			<h2>Admin User Management</h2>
			<p>Use Basic Auth with the seeded admin account to inspect activation status and suspicious activity.</p>
			<input value={authHeader} onChange={(event) => setAuthHeader(event.target.value)} placeholder="Authorization header" />
			<button type="button" onClick={loadUsers}>Load Users</button>
			{message ? <div className="admin-message">{message}</div> : null}
			<ul>
				{users.map((user) => (
					<li key={user.id}>
						<strong>{user.name || user.userId}</strong> - {user.role} - {user.status}
					</li>
				))}
			</ul>
		</div>
	);
}