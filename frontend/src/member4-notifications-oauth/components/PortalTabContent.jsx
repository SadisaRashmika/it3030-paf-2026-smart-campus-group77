import { CalendarClock, Briefcase, BookOpenText, MessageSquareText } from "lucide-react";
import AdminRoleManagementPanel from "./AdminRoleManagementPanel";
import AdminUserManagementPanel from "./AdminUserManagementPanel";
import AdminUsersPanel from "./AdminUsersPanel";
import PortalHomeContent from "./PortalHomeContent";

export default function PortalTabContent({
	tab,
	user,
	onLogin,
	onNavigate,
	adminUsers,
	suspiciousUsers,
	lecturerAssignments,
	loadingAdminData,
	reloadAdminData,
	onAssignLecturerWork,
	onCreateStaffLogin,
	onDeleteUser
}) {
	if (tab === "home") {
		return <PortalHomeContent user={user} onLogin={onLogin} onNavigate={onNavigate} />;
	}

	if (!user) {
		return (
			<section className="rounded-2xl border border-slate-200 bg-white p-8 shadow-sm">
				<h2 className="text-2xl font-bold text-slate-900">Login Required</h2>
				<p className="mt-2 text-sm text-slate-600">Please login to access this area.</p>
			</section>
		);
	}

	const role = user.role?.replace("ROLE_", "").toLowerCase();

	if (role === "admin" && tab === "timetable") {
		return (
			<AdminUsersPanel
				users={adminUsers}
				suspiciousUsers={suspiciousUsers}
				loading={loadingAdminData}
				onDeleteUser={onDeleteUser}
			/>
		);
	}

	if (role === "admin" && tab === "resource") {
		return (
			<AdminUserManagementPanel
				users={adminUsers}
				assignments={lecturerAssignments}
				onAssignLecturerWork={onAssignLecturerWork}
			/>
		);
	}

	if (role === "admin" && tab === "jobs") {
		return <AdminRoleManagementPanel onCreateStaffLogin={onCreateStaffLogin} />;
	}

	if (tab === "resource") {
		return (
			<RolePanel
				icon={BookOpenText}
				title="Resource Management"
				body={
					role === "lecturer"
						? "Upload and update module content, notes, and announcements for your students."
						: "Browse course material, lecture notes, and subject-specific resources."
				}
			/>
		);
	}

	if (tab === "timetable") {
		return (
			<RolePanel
				icon={CalendarClock}
				title="Timetable"
				body={
					role === "lecturer"
						? "View your teaching sessions and manage your weekly schedule."
						: "Track classes and upcoming session changes in one place."
				}
			/>
		);
	}

	if (tab === "jobs") {
		return (
			<RolePanel
				icon={Briefcase}
				title="Jobs and Internships"
				body={
					role === "lecturer"
						? "Review opportunities and post role-specific industry pathways."
						: "Find internships and jobs aligned with your profile and program."
				}
			/>
		);
	}

	return (
		<RolePanel
			icon={MessageSquareText}
			title="Academic Tickets"
			body={
				role === "lecturer"
					? "Review student issues, respond with clarity, and close support requests."
					: "Raise academic concerns and chat with lecturers on active requests."
			}
		/>
	);
}

function RolePanel({ icon: Icon, title, body }) {
	return (
		<section className="rounded-2xl border border-slate-200 bg-white p-8 shadow-sm">
			<div className="inline-flex items-center gap-2 rounded-xl bg-blue-50 px-3 py-1.5 text-sm font-bold text-blue-700">
				<Icon size={16} /> {title}
			</div>
			<h2 className="mt-4 text-2xl font-bold text-slate-900">{title} Workspace</h2>
			<p className="mt-2 max-w-2xl text-sm leading-relaxed text-slate-600">{body}</p>
		</section>
	);
}
