import { useOutletContext } from "react-router-dom";
import PortalTabContent from "../member4-notifications-oauth/components/PortalTabContent";

export default function LecturerPage() {
  const {
    user,
    activeTab,
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
      recoveryRequests={recoveryRequests}
      loadingAdminData={loadingAdminData}
      reloadAdminData={reloadAdminData}
      onAssignLecturerWork={onAssignLecturerWork}
      onCreateStaffLogin={onCreateStaffLogin}
      onDeleteUser={onDeleteUser}
      onDeactivateUser={onDeactivateUser}
      onApproveRecoveryRequest={onApproveRecoveryRequest}
      onRejectRecoveryRequest={onRejectRecoveryRequest}
    />
  );
}
