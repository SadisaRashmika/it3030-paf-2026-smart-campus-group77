import { CalendarClock, Briefcase, BookOpenText, MessageSquareText } from "lucide-react";
import AdminRoleManagementPanel from "./AdminRoleManagementPanel";
import AdminUserManagementPanel from "./AdminUserManagementPanel";
import AdminUsersPanel from "./AdminUsersPanel";
import RecoveryRequestsPanel from "./RecoveryRequestsPanel";
import PortalHomeContent from "./PortalHomeContent";
import ResourcePanel from "../../member2-bookings-management/components/ResourcePanel";
import AdminBookingDashboard from "../../member2-bookings-management/components/AdminBookingDashboard";
import LecturerDashboard from "../../components/dashboard/LecturerDashboard";
import StudentDashboard from "../../components/dashboard/StudentDashboard";
import TimetableManagerDashboard from "../../components/dashboard/TimetableManagerDashboard";
import TimetableWeeklyView from "../../components/dashboard/TimetableWeeklyView";

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
	if (tab === "home") {
		if (role === "lecturer") return <LecturerDashboard user={user} />;
		if (role === "student") return <StudentDashboard user={user} />;
		if (role === "timetable_manager") return <TimetableManagerDashboard user={user} />;
		return <PortalHomeContent user={user} onLogin={onLogin} onNavigate={onNavigate} />;
	}

	if (!user) {
		return (
			<section className="rounded-2xl border border-amber-300 bg-amber-50 p-8 text-center sm:p-12 sm:m-8">
				<div className="mx-auto h-16 w-16 bg-amber-100 rounded-2xl flex items-center justify-center text-amber-600 mb-4">
					<CalendarClock size={32} />
				</div>
				<h2 className="text-2xl font-black text-amber-900 tracking-tighter uppercase">Authorized Access Only</h2>
				<p className="mt-2 text-sm text-amber-800 font-medium">Please sign in to access your dashboard and campus resources.</p>
				<button 
					onClick={onLogin}
					className="mt-6 rounded-xl bg-amber-900 px-6 py-2.5 text-sm font-bold text-white hover:bg-black transition-all"
				>
					Sign In Now
				</button>
			</section>
		);
	}

	const role = user.role?.replace("ROLE_", "").toLowerCase();
	const isManager = role === "timetable_manager";

	// Special Handling for Admin and Manager Dashboards
	if (tab === "timetable" && isManager) {
		return <TimetableWeeklyView user={user} />;
	}

	if (role === "admin" && tab === "timetable") {
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

	if (role === "admin" && tab === "ticket") {
		return (
			<RecoveryRequestsPanel
				requests={recoveryRequests}
				loading={loadingAdminData}
				onApproveRecoveryRequest={onApproveRecoveryRequest}
				onRejectRecoveryRequest={onRejectRecoveryRequest}
			/>
		);
	}

	if (tab === "resource") {
		return <ResourcePanel user={user} />;
	}

	if (tab === "timetable") {
		// Students and Lecturers get the read-only weekly timetable
		return <TimetableWeeklyView user={user} readOnly={role === "student"} />;
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
