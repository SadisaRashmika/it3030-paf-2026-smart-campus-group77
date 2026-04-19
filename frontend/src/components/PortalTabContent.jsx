import { CalendarClock, Briefcase, BookOpenText, MessageSquareText } from "lucide-react";
import AdminRoleManagementPanel from "../member4-notifications-oauth/components/AdminRoleManagementPanel";
import AdminUserManagementPanel from "../member4-notifications-oauth/components/AdminUserManagementPanel";
import AdminUsersPanel from "../member4-notifications-oauth/components/AdminUsersPanel";
import RecoveryRequestsPanel from "../member4-notifications-oauth/components/RecoveryRequestsPanel";
import PortalHomeContent from "../member4-notifications-oauth/components/PortalHomeContent";
import StudentTicketDashboard from "../member3-ticketing/components/StudentTicketDashboard";
import AdminTicketManagement from "../member3-ticketing/components/AdminTicketManagement";
import AdminTechnicianAssignment from "../member3-ticketing/components/AdminTechnicianAssignment";

export default function PortalTabContent({
	tab,
	user,
	onLogin,
	onNavigate,
	adminUsers,
	suspiciousUsers,
	lecturerAssignments,
	recoveryRequests,
	loadingAdminData,
	reloadAdminData,
	onAssignLecturerWork,
	onCreateStaffLogin,
	onDeleteUser,
	onDeactivateUser,
	onApproveRecoveryRequest,
	onRejectRecoveryRequest
}) {
	if (!user) {
		if (tab === "TAB01") {
			return <PortalHomeContent user={user} onLogin={onLogin} onNavigate={onNavigate} />;
		}

		return (
			<section className="rounded-2xl border border-slate-200 bg-white p-8 shadow-sm">
				<h2 className="text-2xl font-bold text-slate-900">Login Required</h2>
				<p className="mt-2 text-sm text-slate-600">Please login to access this area.</p>
			</section>
		);
	}

	const role = user.role?.replace("ROLE_", "").toLowerCase();

	if (role === "admin" && tab === "TAB01") {
		return <PortalHomeContent user={user} onLogin={onLogin} onNavigate={onNavigate} />;
	}

	if (role === "admin" && tab === "TAB02") {
		return (
			<AdminUsersPanel
				users={adminUsers}
				suspiciousUsers={suspiciousUsers}
				loading={loadingAdminData}
				onDeleteUser={onDeleteUser}
				onDeactivateUser={onDeactivateUser}
			/>
		);
	}

	if (role === "admin" && tab === "TAB03") {
		return (
			<AdminUserManagementPanel
				users={adminUsers}
				assignments={lecturerAssignments}
				onAssignLecturerWork={onAssignLecturerWork}
			/>
		);
	}

	if (role === "admin" && tab === "TAB04") {
		return <AdminRoleManagementPanel onCreateStaffLogin={onCreateStaffLogin} />;
	}

	if (role === "admin" && tab === "TAB05") {
		return (
			<RecoveryRequestsPanel
				requests={recoveryRequests}
				loading={loadingAdminData}
				onApproveRecoveryRequest={onApproveRecoveryRequest}
				onRejectRecoveryRequest={onRejectRecoveryRequest}
			/>
		);
	}

	if (role === "admin" && tab === "TAB06") {
		return <AdminTicketManagement user={user} />;
	}

	if (role === "admin" && tab === "TAB07") {
		return <AdminTechnicianAssignment user={user} />;
	}

	if (role === "timetable_manager") {
		if (tab === "TAB01") {
			return (
				<RolePanel
					icon={CalendarClock}
					title="Timetable"
					body="Manage class schedules, session slots, and timetable updates in one place."
				/>
			);
		}

		if (tab === "TAB02") {
			return (
				<RolePanel
					icon={BookOpenText}
					title="Resource Planning"
					body="Coordinate room and resource allocation details for timetable activities."
				/>
			);
		}

		if (tab === "TAB03") {
			return (
				<RolePanel
					icon={Briefcase}
					title="Operations"
					body="Track operational tasks related to timetable publishing and updates."
				/>
			);
		}
	}

	if (role === "resource_administator") {
		if (tab === "TAB01") {
			return (
				<RolePanel
					icon={BookOpenText}
					title="Resource Overview"
					body="Monitor resource usage and review allocation priorities across the campus."
				/>
			);
		}

		if (tab === "TAB02") {
			return (
				<RolePanel
					icon={CalendarClock}
					title="Allocation Schedule"
					body="Plan and adjust timetable-linked resource allocations with conflict visibility."
				/>
			);
			}

		if (tab === "TAB03") {
			return (
				<RolePanel
					icon={Briefcase}
					title="Operations"
					body="Coordinate operational resource tasks and approvals for upcoming sessions."
				/>
			);
		}
	}

	if (tab === "TAB01") {
		return <PortalHomeContent user={user} onLogin={onLogin} onNavigate={onNavigate} />;
	}

	if (tab === "TAB02") {
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

	if (tab === "TAB03") {
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

	if (tab === "TAB04") {
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

	if (tab === "TAB05") {
		return <StudentTicketDashboard user={user} />;
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
