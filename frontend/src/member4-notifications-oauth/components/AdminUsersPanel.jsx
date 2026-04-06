import { AlertTriangle, HelpCircle, Search, Trash2, Users } from "lucide-react";
import { useMemo, useState } from "react";

const ACCOUNT_FILTERS = ["all", "student", "lecturer"];

export default function AdminUsersPanel({
	users,
	suspiciousUsers,
	loading,
	onDeleteUser
}) {
	const [accountFilter, setAccountFilter] = useState("all");
	const [searchTerm, setSearchTerm] = useState("");
	const [deletingUserId, setDeletingUserId] = useState(null);
	const [actionError, setActionError] = useState("");
	const [showWhySuspicious, setShowWhySuspicious] = useState(false);
	const [pendingDeleteUser, setPendingDeleteUser] = useState(null);

	const filteredUsers = useMemo(() => {
		const normalizedSearch = searchTerm.trim().toLowerCase();

		if (accountFilter === "all") {
			return users.filter((user) => {
				if (!normalizedSearch) {
					return true;
				}
				const haystack = `${user.name || ""} ${user.userId || ""} ${user.email || ""}`.toLowerCase();
				return haystack.includes(normalizedSearch);
			});
		}

		return users.filter((user) => {
			const role = (user.role || "").toLowerCase();
			if (!role.includes(accountFilter)) {
				return false;
			}
			if (!normalizedSearch) {
				return true;
			}
			const haystack = `${user.name || ""} ${user.userId || ""} ${user.email || ""}`.toLowerCase();
			return haystack.includes(normalizedSearch);
		});
	}, [users, accountFilter, searchTerm]);

	const handleDeleteUser = async (user) => {
		if (!onDeleteUser) {
			setActionError("Delete action is not available.");
			return;
		}

		setPendingDeleteUser(user);
	};

	const confirmDeleteUser = async () => {
		if (!pendingDeleteUser) {
			return;
		}

		setActionError("");
		setDeletingUserId(pendingDeleteUser.id);
		try {
			await onDeleteUser(pendingDeleteUser.id);
			setPendingDeleteUser(null);
		} catch (error) {
			setActionError(error.message || "Unable to delete user.");
		} finally {
			setDeletingUserId(null);
		}
	};

	return (
		<>
		<section className="space-y-4">
			<div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
				<div className="flex flex-wrap items-center justify-between gap-3">
					<div>
						<p className="text-xs font-bold uppercase tracking-widest text-slate-500">Admin Controls</p>
						<h2 className="mt-1 text-2xl font-bold text-slate-900">Manage Users</h2>
						<p className="mt-1 text-sm text-slate-600">
							Create lecturer staff logins, assign lecturer work, and trigger in-app notifications and emails.
						</p>
					</div>
					{loading ? (
						<span className="rounded-full border border-amber-200 bg-amber-50 px-3 py-1 text-xs font-bold text-amber-700">
							Refreshing
						</span>
					) : null}
				</div>

				<div className="mt-4 grid grid-cols-1 gap-3 sm:grid-cols-2">
					<div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
						<p className="text-xs uppercase tracking-widest text-slate-500">Total Users</p>
						<p className="mt-1 text-3xl font-bold text-slate-900">{users.length}</p>
					</div>
					<div className="rounded-xl border border-rose-200 bg-rose-50 p-4">
						<p className="text-xs uppercase tracking-widest text-rose-600">Suspicious Accounts</p>
						<p className="mt-1 text-3xl font-bold text-rose-700">{suspiciousUsers.length}</p>
					</div>
				</div>
			</div>

			<div className="grid grid-cols-1 gap-4 xl:grid-cols-2">
				<article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
					<div className="mb-3 flex flex-wrap items-center justify-between gap-3">
						<p className="inline-flex items-center gap-2 text-sm font-bold text-slate-800">
							<Users size={16} /> All Accounts
						</p>
						<div className="inline-flex items-center gap-1 rounded-lg border border-slate-200 bg-slate-50 p-1">
							{ACCOUNT_FILTERS.map((filter) => {
								const selected = accountFilter === filter;
								const label = filter === "all" ? "All" : filter === "student" ? "Student" : "Lecturer";
								return (
									<button
										key={filter}
										type="button"
										onClick={() => setAccountFilter(filter)}
										className={`rounded-md px-2.5 py-1 text-xs font-semibold transition ${
											selected
												? "bg-white text-slate-900 shadow-sm"
												: "text-slate-600 hover:text-slate-800"
										}`}
									>
										{label}
									</button>
								);
							})}
						</div>
					</div>
					<div className="mb-3">
						<div className="relative">
							<Search size={14} className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
							<input
								value={searchTerm}
								onChange={(event) => setSearchTerm(event.target.value)}
								placeholder="Search by name, ID, or email"
								className="w-full rounded-xl border border-slate-200 bg-white py-2.5 pl-9 pr-3 text-sm text-slate-700 outline-none ring-amber-200 focus:ring"
							/>
						</div>
					</div>
					<div className="space-y-2">
						{filteredUsers.length === 0 ? (
							<p className="rounded-lg bg-slate-50 px-3 py-2 text-sm text-slate-600">No users returned by API.</p>
						) : (
							filteredUsers.map((user) => (
								<div key={user.id} className="rounded-lg border border-slate-200 bg-slate-50 px-3 py-2 text-xs">
									<div className="flex items-start justify-between gap-3">
										<div>
											<p className="font-bold text-slate-800">{user.name || user.userId} - {user.email}</p>
											<p className="mt-1 text-slate-600">{user.role} | {user.status}</p>
										</div>
										<button
											type="button"
											onClick={() => handleDeleteUser(user)}
											disabled={deletingUserId === user.id}
											className="inline-flex items-center gap-1 rounded-md border border-rose-200 bg-white px-2 py-1 font-semibold text-rose-700 transition hover:bg-rose-50 disabled:cursor-not-allowed disabled:opacity-60"
										>
											<Trash2 size={12} /> {deletingUserId === user.id ? "Deleting" : "Delete"}
										</button>
									</div>
								</div>
							))
						)}
						{actionError ? <p className="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-700">{actionError}</p> : null}
					</div>
				</article>

				<article className="rounded-2xl border border-rose-200 bg-white p-5 shadow-sm">
					<div className="mb-3 flex items-center justify-between gap-3">
						<p className="inline-flex items-center gap-2 text-sm font-bold text-rose-700">
							<AlertTriangle size={16} /> Suspicious Activity
						</p>
						<button
							type="button"
							onClick={() => setShowWhySuspicious((prev) => !prev)}
							className="inline-flex items-center gap-1 rounded-md border border-rose-200 bg-white px-2.5 py-1 text-xs font-semibold text-rose-700 transition hover:bg-rose-50"
						>
							<HelpCircle size={13} /> Why Suspicious?
						</button>
					</div>
					{showWhySuspicious ? (
						<div className="mb-3 rounded-lg border border-rose-200 bg-rose-50 px-3 py-2 text-xs text-rose-800">
							<p className="font-semibold">Accounts are flagged suspicious when:</p>
							<p className="mt-1">1. Too many OTP requests before activation.</p>
							<p>2. Too many failed OTP verification attempts.</p>
							<p>3. User clicks the suspicious-report link in onboarding email.</p>
						</div>
					) : null}
					<div className="space-y-2">
						{suspiciousUsers.length === 0 ? (
							<p className="rounded-lg bg-emerald-50 px-3 py-2 text-sm text-emerald-700">No suspicious accounts currently flagged.</p>
						) : (
							suspiciousUsers.map((user) => (
								<div key={user.id} className="rounded-lg border border-rose-200 bg-rose-50 px-3 py-2 text-xs">
									<div className="flex items-start justify-between gap-3">
										<div>
											<p className="font-bold text-rose-800">{user.name || user.userId} - {user.email}</p>
											<p className="mt-1 text-rose-700">OTP Requests: {user.otpRequestCount} | Failed OTP: {user.failedOtpAttempts}</p>
											{user.suspiciousReason ? <p className="mt-1 font-semibold text-rose-800">Reason: {user.suspiciousReason}</p> : null}
										</div>
										<button
											type="button"
											onClick={() => handleDeleteUser(user)}
											disabled={deletingUserId === user.id}
											className="inline-flex items-center gap-1 rounded-md border border-rose-300 bg-white px-2 py-1 font-semibold text-rose-700 transition hover:bg-rose-100 disabled:cursor-not-allowed disabled:opacity-60"
										>
											<Trash2 size={12} /> {deletingUserId === user.id ? "Deleting" : "Delete"}
										</button>
									</div>
								</div>
							))
						)}
					</div>
				</article>
			</div>
		</section>

		{pendingDeleteUser ? (
			<div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/45 p-4 backdrop-blur-sm">
				<div className="w-full max-w-md rounded-2xl border border-slate-200 bg-white p-5 shadow-2xl">
					<h3 className="text-base font-bold text-slate-900">Confirm Delete</h3>
					<p className="mt-2 text-sm text-slate-700">
						Are you sure you want to delete <span className="font-semibold">{pendingDeleteUser.name || pendingDeleteUser.userId}</span> ({pendingDeleteUser.userId})?
					</p>
					{pendingDeleteUser.suspiciousReason ? (
						<p className="mt-2 rounded-lg border border-rose-200 bg-rose-50 px-3 py-2 text-xs text-rose-700">
							Reason: {pendingDeleteUser.suspiciousReason}
						</p>
					) : null}

					<div className="mt-4 flex justify-end gap-2">
						<button
							type="button"
							onClick={() => setPendingDeleteUser(null)}
							className="rounded-lg border border-slate-200 px-3 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
						>
							Cancel
						</button>
						<button
							type="button"
							onClick={confirmDeleteUser}
							disabled={deletingUserId === pendingDeleteUser.id}
							className="inline-flex items-center rounded-lg border border-rose-200 bg-rose-600 px-3 py-2 text-sm font-bold text-white transition hover:bg-rose-700 disabled:cursor-not-allowed disabled:opacity-60"
						>
							{deletingUserId === pendingDeleteUser.id ? "Deleting..." : "Delete User"}
						</button>
					</div>
				</div>
			</div>
		) : null}
		</>
	);
}
