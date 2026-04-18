import { useMemo, useState } from "react";
import { CheckCircle2, Clock3, Image as ImageIcon, Search, ShieldAlert, XCircle } from "lucide-react";

export default function RecoveryRequestsPanel({ requests = [], loading, onApproveRecoveryRequest, onRejectRecoveryRequest }) {
	const [searchTerm, setSearchTerm] = useState("");
	const [busyRequestId, setBusyRequestId] = useState(null);
	const [actionMessage, setActionMessage] = useState("");
	const [actionError, setActionError] = useState("");

	const filteredRequests = useMemo(() => {
		const query = searchTerm.trim().toLowerCase();
		if (!query) {
			return requests;
		}

		return requests.filter((request) => {
			const haystack = [
				request.userId,
				request.studentEmail,
				request.contactEmail,
				request.issueSummary,
				request.status,
				request.matchedName,
				request.matchedUserId
			].filter(Boolean).join(" ").toLowerCase();
			return haystack.includes(query);
		});
	}, [requests, searchTerm]);

	const handleDecision = async (request, approve) => {
		const action = approve ? onApproveRecoveryRequest : onRejectRecoveryRequest;
		if (!action) {
			setActionError("Recovery request actions are not available.");
			return;
		}

		setActionError("");
		setActionMessage("");
		setBusyRequestId(request.id);
		try {
			await action(request.id);
			setActionMessage(
				approve
					? `Approved recovery request #${request.id}. A temporary password was emailed to the contact address and expires in 1 day.`
					: `Rejected recovery request #${request.id}. A rejection email was sent to the contact address.`
			);
		} catch (error) {
			setActionError(error.message || "Unable to process the request.");
		} finally {
			setBusyRequestId(null);
		}
	};

	return (
		<section className="space-y-4 rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
			<div className="flex flex-wrap items-start justify-between gap-4">
				<div>
					<p className="text-xs font-bold uppercase tracking-[0.2em] text-slate-500">Admin Tickets</p>
					<h2 className="mt-1 text-2xl font-bold text-slate-900">Account Recovery Requests</h2>
					<p className="mt-2 max-w-2xl text-sm leading-6 text-slate-600">
						Review uploaded ID photos, verify the student or lecturer details, then approve or reject each request. Approved requests will receive an email on their contact address.
					</p>
				</div>
				<div className="rounded-2xl bg-slate-50 px-4 py-3 text-sm text-slate-600">
					<div className="flex items-center gap-2 font-bold text-slate-900"><ShieldAlert size={15} />{requests.length} total</div>
					<p className="mt-1 text-xs">Pending, approved, and rejected requests are shown together for audit history.</p>
				</div>
			</div>

			<div className="relative max-w-md">
				<Search size={16} className="pointer-events-none absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
				<input
					type="search"
					value={searchTerm}
					onChange={(event) => setSearchTerm(event.target.value)}
					placeholder="Search by ID, email, or status"
					className="w-full rounded-2xl border border-slate-200 bg-white py-3 pl-10 pr-4 text-sm font-medium text-slate-900 outline-none transition focus:border-[#ffc111] focus:ring-2 focus:ring-amber-100"
				/>
			</div>

			{actionError ? <div className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">{actionError}</div> : null}
			{actionMessage ? <div className="rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">{actionMessage}</div> : null}

			{loading ? (
				<div className="rounded-2xl border border-slate-200 bg-slate-50 px-4 py-10 text-center text-sm text-slate-500">Loading recovery requests...</div>
			) : filteredRequests.length === 0 ? (
				<div className="rounded-2xl border border-dashed border-slate-300 bg-slate-50 px-4 py-10 text-center text-sm text-slate-500">No recovery requests found.</div>
			) : (
				<div className="grid gap-4 xl:grid-cols-2">
					{filteredRequests.map((request) => {
						const isBusy = busyRequestId === request.id;
						const isResolved = request.status !== "PENDING";
						return (
							<article key={request.id} className="overflow-hidden rounded-3xl border border-slate-200 bg-slate-50">
								<div className="flex items-start justify-between gap-4 border-b border-slate-200 bg-white px-4 py-4">
									<div>
										<p className="text-sm font-bold text-slate-900">Request #{request.id}</p>
										<p className="mt-1 text-xs text-slate-500">Submitted by {request.userId} from {request.studentEmail}</p>
									</div>
									<StatusPill status={request.status} />
								</div>

								<div className="grid gap-4 p-4 lg:grid-cols-[1fr_180px]">
									<div className="space-y-4">
										<div className="grid gap-3 sm:grid-cols-2">
											<Detail label="Contact email" value={request.contactEmail} />
											<Detail label="Linked account" value={request.matchedUserId ? `${request.matchedName || "Unknown"} (${request.matchedUserId})` : "No linked account found"} />
											<Detail label="Linked role" value={request.matchedRole || "Unknown"} />
											<Detail label="Account status" value={request.matchedAccountActive == null ? "Unknown" : request.matchedAccountActive ? "Active" : "Inactive"} />
										</div>

										<div className="rounded-2xl bg-white p-4 text-sm text-slate-700">
											<p className="text-xs font-bold uppercase tracking-[0.18em] text-slate-500">Problem summary</p>
											<p className="mt-2 leading-6">{request.issueSummary}</p>
										</div>

										<div className="flex flex-wrap items-center gap-2 text-xs text-slate-500">
											<span className="inline-flex items-center gap-1 rounded-full bg-white px-3 py-1 font-semibold text-slate-600"><Clock3 size={12} /> {formatDate(request.createdAt)}</span>
											{request.reviewedAt ? <span className="inline-flex items-center gap-1 rounded-full bg-white px-3 py-1 font-semibold text-slate-600"><CheckCircle2 size={12} /> Reviewed {formatDate(request.reviewedAt)}</span> : null}
										</div>

										<div className="flex flex-wrap gap-2">
											<button
												type="button"
												onClick={() => handleDecision(request, true)}
												disabled={isBusy || isResolved}
												className="inline-flex items-center gap-2 rounded-2xl bg-emerald-600 px-4 py-2.5 text-sm font-bold text-white transition hover:bg-emerald-700 disabled:cursor-not-allowed disabled:opacity-50"
											>
												<CheckCircle2 size={14} /> {isBusy ? "Processing..." : "Approve"}
											</button>
											<button
												type="button"
												onClick={() => handleDecision(request, false)}
												disabled={isBusy || isResolved}
												className="inline-flex items-center gap-2 rounded-2xl border border-rose-300 bg-white px-4 py-2.5 text-sm font-bold text-rose-700 transition hover:bg-rose-50 disabled:cursor-not-allowed disabled:opacity-50"
											>
												<XCircle size={14} /> Reject
											</button>
										</div>
									</div>

									<div className="space-y-3">
										<div className="rounded-2xl border border-slate-200 bg-white p-3">
											<div className="mb-2 flex items-center gap-2 text-xs font-bold uppercase tracking-[0.18em] text-slate-500"><ImageIcon size={13} /> ID photo</div>
											{request.idPhotoDataUrl ? <img src={request.idPhotoDataUrl} alt={`ID photo for request ${request.id}`} className="h-44 w-full rounded-xl object-cover" /> : <div className="flex h-44 items-center justify-center rounded-xl bg-slate-100 text-sm text-slate-500">No photo available</div>}
										</div>
										<div className="rounded-2xl bg-white p-3 text-xs text-slate-500">
											<p className="font-bold uppercase tracking-[0.18em] text-slate-500">File</p>
											<p className="mt-1 text-slate-700">{request.idPhotoFileName}</p>
											<p className="mt-1 text-slate-500">{request.idPhotoContentType}</p>
										</div>
									</div>
								</div>
							</article>
						);
					})}
				</div>
			)}
		</section>
	);
}

function Detail({ label, value }) {
	return (
		<div className="rounded-2xl bg-slate-50 px-4 py-3">
			<p className="text-[11px] font-bold uppercase tracking-[0.18em] text-slate-500">{label}</p>
			<p className="mt-1 break-words text-sm font-semibold text-slate-900">{value}</p>
		</div>
	);
}

function StatusPill({ status }) {
	const palette = {
		PENDING: "bg-amber-100 text-amber-800",
		APPROVED: "bg-emerald-100 text-emerald-700",
		REJECTED: "bg-rose-100 text-rose-700"
	};

	return <span className={`rounded-full px-3 py-1 text-xs font-bold uppercase tracking-[0.18em] ${palette[status] || "bg-slate-100 text-slate-600"}`}>{status}</span>;
}

function formatDate(value) {
	if (!value) {
		return "Unknown";
	}

	const date = new Date(value);
	if (Number.isNaN(date.getTime())) {
		return value;
	}

	return new Intl.DateTimeFormat("en", {
		month: "short",
		day: "numeric",
		year: "numeric",
		hour: "2-digit",
		minute: "2-digit"
	}).format(date);
}
