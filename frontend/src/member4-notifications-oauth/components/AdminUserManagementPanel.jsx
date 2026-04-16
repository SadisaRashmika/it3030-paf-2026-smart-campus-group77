import { BellRing, Clock3, Mail, Search, X } from "lucide-react";
import { useMemo, useState } from "react";

function getTodayValue() {
	const now = new Date();
	const year = now.getFullYear();
	const month = String(now.getMonth() + 1).padStart(2, "0");
	const day = String(now.getDate()).padStart(2, "0");
	return `${year}-${month}-${day}`;
}

export default function AdminUserManagementPanel({ users, assignments = [], onAssignLecturerWork }) {
	const todayValue = getTodayValue();
	const [workForm, setWorkForm] = useState({
		lecturerIds: [],
		workTitle: "",
		description: "",
		location: "",
		startDate: "",
		endDate: "",
		startTime: "",
		endTime: "",
		sendEmail: true
	});
	const [searchTerm, setSearchTerm] = useState("");
	const [workActionMessage, setWorkActionMessage] = useState("");
	const [workFormError, setWorkFormError] = useState("");
	const [submittingWork, setSubmittingWork] = useState(false);

	const getLecturerName = (lecturer) => {
		const explicitName = lecturer?.name?.trim();
		if (explicitName) {
			return explicitName;
		}

		if (lecturer?.userId) {
			return lecturer.userId;
		}

		if (lecturer?.email) {
			return lecturer.email.split("@")[0];
		}

		return "Lecturer";
	};

	const normalizeSearchValue = (value) =>
		(value || "")
			.toLowerCase()
			.replace(/[^a-z0-9\s]/g, " ")
			.replace(/\s+/g, " ")
			.trim();

	const lecturerUsers = useMemo(
		() => users.filter((user) => (user.role || "").toLowerCase().includes("lecturer")),
		[users]
	);

	const selectedLecturers = useMemo(
		() => lecturerUsers.filter((lecturer) => workForm.lecturerIds.includes(lecturer.id)),
		[lecturerUsers, workForm.lecturerIds]
	);

	const matchingLecturers = useMemo(() => {
		const term = normalizeSearchValue(searchTerm);
		if (!term) {
			return [];
		}

		const tokens = term.split(" ").filter(Boolean);

		const candidates = lecturerUsers.filter((lecturer) => {
			const haystack = normalizeSearchValue(
				`${getLecturerName(lecturer)} ${lecturer.userId || ""} ${lecturer.email || ""}`
			);

			return tokens.every((token) => haystack.includes(token));
		});

		const sorted = candidates.sort((left, right) => {
			const leftSelected = workForm.lecturerIds.includes(left.id);
			const rightSelected = workForm.lecturerIds.includes(right.id);
			if (leftSelected === rightSelected) {
				return getLecturerName(left).localeCompare(getLecturerName(right));
			}
			return leftSelected ? -1 : 1;
		});

		return sorted;
	}, [lecturerUsers, searchTerm, workForm.lecturerIds]);

	const toggleLecturer = (lecturerId) => {
		setWorkForm((prev) => {
			const exists = prev.lecturerIds.includes(lecturerId);
			return {
				...prev,
				lecturerIds: exists ? prev.lecturerIds.filter((id) => id !== lecturerId) : [...prev.lecturerIds, lecturerId]
			};
		});
	};

	const onAssignWorkSubmit = async (event) => {
		event.preventDefault();
		setWorkFormError("");

		if (!onAssignLecturerWork) {
			setWorkFormError("Assign work action is not available.");
			return;
		}

		if (workForm.lecturerIds.length === 0) {
			setWorkFormError("Select at least one lecturer.");
			return;
		}

		if (!workForm.workTitle.trim()) {
			setWorkFormError("Work Title is required.");
			return;
		}

		if (!workForm.description.trim()) {
			setWorkFormError("Description is required.");
			return;
		}

		if (!workForm.location.trim()) {
			setWorkFormError("Location / Lecture Hall is required.");
			return;
		}

		if (workForm.startDate && workForm.startDate < todayValue) {
			setWorkFormError("Start Date cannot be in the past.");
			return;
		}

		if (workForm.endDate && workForm.endDate < todayValue) {
			setWorkFormError("End Date cannot be in the past.");
			return;
		}

		if (workForm.startDate && workForm.endDate && workForm.endDate < workForm.startDate) {
			setWorkFormError("End Date cannot be earlier than Start Date.");
			return;
		}

		setSubmittingWork(true);
		setWorkActionMessage("");
		try {
			const assignment = await onAssignLecturerWork({
				lecturerIds: workForm.lecturerIds,
				workTitle: workForm.workTitle,
				description: workForm.description,
				location: workForm.location,
				startDate: workForm.startDate || null,
				endDate: workForm.endDate || null,
				startTime: workForm.startTime || null,
				endTime: workForm.endTime || null,
				sendEmail: workForm.sendEmail
			});
			setWorkActionMessage(
				`${assignment.message} Notifications: ${assignment.notificationCount}. Emails sent: ${assignment.emailSentCount}.`
			);
			setWorkFormError("");
			setWorkForm({
				lecturerIds: [],
				workTitle: "",
				description: "",
				location: "",
				startDate: "",
				endDate: "",
				startTime: "",
				endTime: "",
				sendEmail: true
			});
			setSearchTerm("");
		} catch (error) {
			setWorkActionMessage(error.message || "Unable to assign lecturer work.");
		} finally {
			setSubmittingWork(false);
		}
	};

	return (
		<section className="space-y-4">
			<div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
				<p className="text-xs font-bold uppercase tracking-widest text-slate-500">Admin Controls</p>
				<h2 className="mt-1 text-2xl font-bold text-slate-900">User Management</h2>
				<p className="mt-1 text-sm text-slate-600">
					Search lecturers by name, ID, or email, then assign work with notifications and optional email.
				</p>
			</div>

			<div className="grid grid-cols-1 gap-4">
				<article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
					<p className="mb-3 inline-flex items-center gap-2 text-sm font-bold text-slate-800">
						<BellRing size={16} /> Assign Work to Lecturer
					</p>
					<p className="mb-4 text-xs text-slate-500">
						Select one or more lecturers by searching their name, email, or ID. End date and end time are optional, but past dates are not allowed.
					</p>

					<div className="mb-4 space-y-3 rounded-2xl border border-slate-200 bg-slate-50 p-4">
						<label className="block text-xs font-semibold uppercase tracking-wider text-slate-500">Search Lecturers</label>
						<div className="relative">
							<Search size={14} className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
							<input
								value={searchTerm}
								onChange={(event) => setSearchTerm(event.target.value)}
								className="w-full rounded-xl border border-slate-200 bg-white py-2.5 pl-9 pr-3 text-sm text-slate-700 outline-none ring-amber-200 focus:ring"
								placeholder="Type lecturer name, ID, or email"
							/>
						</div>

						<div>
							<p className="mb-2 text-xs font-semibold uppercase tracking-wider text-slate-500">Selected Lecturers</p>
							<div className="flex flex-wrap gap-2">
								{selectedLecturers.length === 0 ? (
									<span className="rounded-full border border-dashed border-slate-300 bg-white px-3 py-1.5 text-xs text-slate-500">
										No lecturers selected yet
									</span>
								) : (
									selectedLecturers.map((lecturer) => (
										<span
											key={lecturer.id}
											className="inline-flex items-center gap-2 rounded-full border border-amber-200 bg-amber-50 px-3 py-1.5 text-xs font-semibold text-amber-900"
										>
											{getLecturerName(lecturer)} · {lecturer.email}
											<button
												type="button"
												onClick={() => toggleLecturer(lecturer.id)}
												className="rounded-full p-0.5 transition hover:bg-amber-100"
												aria-label={`Remove ${lecturer.userId}`}
											>
												<X size={12} />
											</button>
										</span>
									))
								)}
							</div>
						</div>

						<div className="max-h-72 overflow-auto rounded-2xl border border-slate-200 bg-white p-2">
							{searchTerm.trim().length === 0 ? (
								<p className="px-3 py-2 text-sm text-slate-500">Start typing to search lecturers by name, ID, or email.</p>
							) : matchingLecturers.length === 0 ? (
								<p className="px-3 py-2 text-sm text-slate-500">No matching lecturers found.</p>
							) : (
								matchingLecturers.map((lecturer) => {
									const selected = workForm.lecturerIds.includes(lecturer.id);
									return (
										<button
											key={lecturer.id}
											type="button"
											onClick={() => toggleLecturer(lecturer.id)}
											className={`flex w-full items-center justify-between gap-3 rounded-xl px-3 py-2.5 text-left text-sm transition ${selected ? "bg-amber-50" : "hover:bg-slate-50"}`}
										>
											<span>
												<span className="block font-semibold text-slate-800">{getLecturerName(lecturer)}</span>
												<span className="block text-xs text-slate-500">{lecturer.email}</span>
											</span>
											<span className={`rounded-full px-2 py-1 text-[11px] font-bold ${selected ? "bg-amber-200 text-amber-950" : "bg-slate-100 text-slate-600"}`}>
												{selected ? "Selected" : "Add"}
											</span>
										</button>
									);
								})
							)}
						</div>
					</div>

					<form className="space-y-3" onSubmit={onAssignWorkSubmit}>
						<div>
							<label className="mb-1 block text-xs font-semibold uppercase tracking-wider text-slate-500">Work Title</label>
							<input
								required
								value={workForm.workTitle}
								onChange={(event) => setWorkForm((prev) => ({ ...prev, workTitle: event.target.value }))}
								className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 outline-none ring-amber-200 focus:ring"
								placeholder="Paper Marking"
							/>
						</div>
						<div>
							<label className="mb-1 block text-xs font-semibold uppercase tracking-wider text-slate-500">Description</label>
							<textarea
								required
								value={workForm.description}
								onChange={(event) => setWorkForm((prev) => ({ ...prev, description: event.target.value }))}
								rows={3}
								className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 outline-none ring-amber-200 focus:ring"
								placeholder="Include marking instructions, rubric, and submission notes."
							/>
						</div>
						<div>
							<label className="mb-1 block text-xs font-semibold uppercase tracking-wider text-slate-500">Location / Lecture Hall</label>
							<input
								required
								value={workForm.location}
								onChange={(event) => setWorkForm((prev) => ({ ...prev, location: event.target.value }))}
								className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 outline-none ring-amber-200 focus:ring"
								placeholder="Lecture Hall A3"
							/>
						</div>
						<div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
							<div>
								<label className="mb-1 block text-xs font-semibold uppercase tracking-wider text-slate-500">Start Date</label>
								<input
									type="date"
									value={workForm.startDate}
									onChange={(event) => setWorkForm((prev) => ({ ...prev, startDate: event.target.value }))}
									min={todayValue}
									className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 outline-none ring-amber-200 focus:ring"
								/>
							</div>
							<div>
								<label className="mb-1 block text-xs font-semibold uppercase tracking-wider text-slate-500">End Date</label>
								<input
									type="date"
									value={workForm.endDate}
									onChange={(event) => setWorkForm((prev) => ({ ...prev, endDate: event.target.value }))}
									min={todayValue}
									className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 outline-none ring-amber-200 focus:ring"
								/>
							</div>
						</div>
						<div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
							<div>
								<label className="mb-1 block text-xs font-semibold uppercase tracking-wider text-slate-500">Start Time</label>
								<input
									type="time"
									value={workForm.startTime}
									onChange={(event) => setWorkForm((prev) => ({ ...prev, startTime: event.target.value }))}
									className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 outline-none ring-amber-200 focus:ring"
								/>
							</div>
							<div>
								<label className="mb-1 block text-xs font-semibold uppercase tracking-wider text-slate-500">End Time</label>
								<input
									type="time"
									value={workForm.endTime}
									onChange={(event) => setWorkForm((prev) => ({ ...prev, endTime: event.target.value }))}
									className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 outline-none ring-amber-200 focus:ring"
								/>
							</div>
						</div>
						<label className="inline-flex items-center gap-2 text-xs font-semibold text-slate-600">
							<input
								type="checkbox"
								checked={workForm.sendEmail}
								onChange={(event) => setWorkForm((prev) => ({ ...prev, sendEmail: event.target.checked }))}
								className="h-4 w-4 rounded border-slate-300 text-amber-500 focus:ring-amber-300"
							/>
							<Mail size={14} /> Send email in addition to in-app notification
						</label>
						<div className="flex justify-end">
							<button
								type="submit"
								disabled={submittingWork}
								className="inline-flex items-center rounded-lg bg-gradient-to-b from-amber-400 to-amber-500 px-4 py-2 text-sm font-bold text-amber-950 shadow-[0_6px_14px_rgba(245,158,11,0.35)] transition hover:brightness-105 disabled:cursor-not-allowed disabled:opacity-60"
							>
								{submittingWork ? "Assigning..." : "Assign Work"}
							</button>
						</div>
					</form>
					{workFormError ? <p className="mt-3 rounded-lg bg-rose-50 px-3 py-2 text-xs font-medium text-rose-800">{workFormError}</p> : null}
					{workActionMessage ? (
						<p className="mt-3 rounded-lg bg-slate-50 px-3 py-2 text-xs font-medium text-slate-700">{workActionMessage}</p>
					) : null}
				</article>

				<article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
					<div className="mb-3 flex items-center justify-between gap-3">
						<p className="inline-flex items-center gap-2 text-sm font-bold text-slate-800">
							<Clock3 size={16} /> Assigned Work View
						</p>
						<span className="rounded-full border border-slate-200 bg-slate-50 px-2.5 py-1 text-xs font-semibold text-slate-600">
							{assignments.length} Records
						</span>
					</div>

					<div className="space-y-2">
						{assignments.length === 0 ? (
							<p className="rounded-lg bg-slate-50 px-3 py-2 text-sm text-slate-600">
								No work assignments yet. Submit the form above to see records here.
							</p>
						) : (
							assignments.map((assignment) => (
								<div key={assignment.assignmentId} className="rounded-xl border border-slate-200 bg-slate-50 px-3 py-2.5 text-xs">
									<div className="flex flex-wrap items-start justify-between gap-3">
										<div>
											<p className="font-bold text-slate-800">{assignment.workTitle}</p>
											<p className="mt-1 text-slate-600">{assignment.description}</p>
											<p className="mt-1 text-slate-700">Location: {assignment.location}</p>
											<p className="mt-1 text-slate-700">
												Schedule: {assignment.startDate || "-"}{assignment.endDate ? ` to ${assignment.endDate}` : ""}
												{assignment.startTime ? ` | ${assignment.startTime}` : ""}
												{assignment.endTime ? ` to ${assignment.endTime}` : ""}
											</p>
											<p className="mt-1 text-slate-700">
												Recipients: {assignment.recipientNames?.length ? assignment.recipientNames.join(", ") : "-"}
											</p>
										</div>
										<div className="text-right text-[11px] font-medium text-slate-500">
											<p>ID: #{assignment.assignmentId}</p>
											<p className="mt-1">Email: {assignment.sendEmail ? "Yes" : "No"}</p>
											<p className="mt-1">Created: {assignment.createdAt ? new Date(assignment.createdAt).toLocaleString() : "-"}</p>
										</div>
									</div>
								</div>
							))
						)}
					</div>
				</article>
			</div>
		</section>
	);
}
