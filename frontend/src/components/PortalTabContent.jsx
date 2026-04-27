import { MessageSquareText } from "lucide-react";
import AdminRoleManagementPanel from "../member4-notifications-oauth/components/AdminRoleManagementPanel";
import AdminUserManagementPanel from "../member4-notifications-oauth/components/AdminUserManagementPanel";
import AdminUsersPanel from "../member4-notifications-oauth/components/AdminUsersPanel";
import RecoveryRequestsPanel from "../member4-notifications-oauth/components/RecoveryRequestsPanel";
import PortalHomeContent from "../member4-notifications-oauth/components/PortalHomeContent";
import StudentTicketDashboard from "../member3-ticketing/components/StudentTicketDashboard";
import AdminTicketManagement from "../member3-ticketing/components/AdminTicketManagement";
import AdminTechnicianAssignment from "../member3-ticketing/components/AdminTechnicianAssignment";

// Member 2 Imports
import BookingsPage from "../member2-bookings-management/pages/BookingsPage";
import TimetablePage from "../member2-bookings-management/pages/TimetablePage";
import ApprovalsPage from "../member2-bookings-management/pages/ApprovalsPage";
import ResourceInventoryPage from "../member1-facilities-assets/pages/ResourceInventoryPage";
import ResourceAvailabilityPage from "../member1-facilities-assets/pages/ResourceAvailabilityPage";

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
	onClearSuspiciousUser,
	onApproveRecoveryRequest,
	onRejectRecoveryRequest
}) {
	if (!user) {
		if (tab === "home") {
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

	if (role === "admin" && tab === "home") {
		return <PortalHomeContent user={user} onLogin={onLogin} onNavigate={onNavigate} />;
	}

	if (role === "admin" && tab === "timetable") {
		return (
			<AdminUsersPanel
				users={adminUsers}
				suspiciousUsers={suspiciousUsers}
				loading={loadingAdminData}
				onDeleteUser={onDeleteUser}
				onDeactivateUser={onDeactivateUser}
				onClearSuspiciousUser={onClearSuspiciousUser}
			/>
		);
	}

	if (role === "admin" && tab === "bookings") {
		return (
			<AdminUserManagementPanel
				users={adminUsers}
				assignments={lecturerAssignments}
				onAssignLecturerWork={onAssignLecturerWork}
			/>
		);
	}

	if (role === "admin" && tab === "role-management") {
		return <AdminRoleManagementPanel onCreateStaffLogin={onCreateStaffLogin} />;
	}

	if (role === "admin" && tab === "tickets") {
		return (
			<RecoveryRequestsPanel
				requests={recoveryRequests}
				loading={loadingAdminData}
				onApproveRecoveryRequest={onApproveRecoveryRequest}
				onRejectRecoveryRequest={onRejectRecoveryRequest}
			/>
		);
	}

	if (role === "ticket_administrator" && tab === "home") {
		return <PortalHomeContent user={user} onLogin={onLogin} onNavigate={onNavigate} />;
	}

	if (role === "ticket_administrator" && tab === "ticket-management") {
		return <AdminTicketManagement user={user} />;
	}

	if (role === "ticket_administrator" && tab === "assignments") {
		return <AdminTechnicianAssignment user={user} />;
	}

	if (role === "timetable_manager") {
		if (tab === "home") {
			return <PortalHomeContent user={user} onLogin={onLogin} onNavigate={onNavigate} />;
		}

		if (tab === "timetable") {
			return <TimetablePage />;
		}

		if (tab === "bookings") {
			return <ApprovalsPage user={user} />;
		}
	}

	if (role === "resource_administator") {
		if (tab === "home") {
			return <PortalHomeContent user={user} onLogin={onLogin} onNavigate={onNavigate} />;
		}

		if (tab === "timetable") {
			return <ResourceInventoryPage />;
		}

		if (tab === "bookings") {
			return <ResourceAvailabilityPage />;
		}
	}

	if (role === "student" && (tab === "ticket-management" || tab === "assignments")) {
		return <PortalHomeContent user={user} onLogin={onLogin} onNavigate={onNavigate} />;
	}

	if (tab === "home") {
		return <PortalHomeContent user={user} onLogin={onLogin} onNavigate={onNavigate} />;
	}

	if (tab === "timetable") {
		return <TimetablePage />;
	}

	if (tab === "bookings") {
		return <BookingsPage user={user} />;
	}

	if (tab === "tickets") {
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
