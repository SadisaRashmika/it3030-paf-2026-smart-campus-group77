import { useOutletContext } from "react-router-dom";
import PortalTabContent from "../member4-notifications-oauth/components/PortalTabContent";

export default function StudentPage() {
  const {
    user,
    activeTab,
    onLogin,
    onNavigate,
    adminUsers,
    suspiciousUsers,
    lecturerAssignments,
    loadingAdminData,
    reloadAdminData,
    onAssignLecturerWork,
    onCreateStaffLogin,
    onDeleteUser,
    onDeactivateUser
  } = useOutletContext();

  return (
    <PortalTabContent
      tab={activeTab}
      user={user}
      onLogin={onLogin}
      onNavigate={onNavigate}
      adminUsers={adminUsers}
      suspiciousUsers={suspiciousUsers}
      lecturerAssignments={lecturerAssignments}
      loadingAdminData={loadingAdminData}
      reloadAdminData={reloadAdminData}
      onAssignLecturerWork={onAssignLecturerWork}
      onCreateStaffLogin={onCreateStaffLogin}
      onDeleteUser={onDeleteUser}
      onDeactivateUser={onDeactivateUser}
    />
  );
}
