import { useCallback, useEffect, useMemo, useState } from "react";
import AuthModal from "./member4-notifications-oauth/components/AuthModal";
import PortalTabContent from "./member4-notifications-oauth/components/PortalTabContent";
import TopNavHeader from "./member4-notifications-oauth/components/TopNavHeader";
import {
  assignLecturerWork,
  createStaffLogin,
  deleteUser,
  getAdminUsers,
  getCurrentUser,
  getSuspiciousUsers,
  logout
} from "./services/authService";

export default function App() {
  const [user, setUser] = useState(null);
  const [activeTab, setActiveTab] = useState("home");
  const [authModal, setAuthModal] = useState({ open: false, mode: "login" });
  const [adminUsers, setAdminUsers] = useState([]);
  const [suspiciousUsers, setSuspiciousUsers] = useState([]);
  const [loadingAdminData, setLoadingAdminData] = useState(false);
  const [appError, setAppError] = useState("");

  const role = useMemo(() => user?.role?.replace("ROLE_", "").toLowerCase() || "", [user]);

  const fetchAdminData = useCallback(async () => {
    if (role !== "admin") {
      return;
    }

    try {
      setLoadingAdminData(true);
      const [users, suspicious] = await Promise.all([getAdminUsers(), getSuspiciousUsers()]);
      setAdminUsers(users);
      setSuspiciousUsers(suspicious);
    } catch (error) {
      setAppError(error.message);
    } finally {
      setLoadingAdminData(false);
    }
  }, [role]);

  useEffect(() => {
    const restoreSession = async () => {
      try {
        const me = await getCurrentUser();
        setUser(me);
      } catch {
        setUser(null);
      }
    };

    restoreSession();
  }, []);

  useEffect(() => {
    if (role === "admin") {
      fetchAdminData();
    }
  }, [role, fetchAdminData]);

  const handleAuthenticated = (authUser) => {
    setUser(authUser);
    setAppError("");
    setActiveTab("home");
  };

  const handleLogout = async () => {
    try {
      await logout();
    } catch {
      // Ignore logout request errors and clear local state regardless.
    }

    setUser(null);
    setActiveTab("home");
    setAdminUsers([]);
    setSuspiciousUsers([]);
  };

  const handleAssignLecturerWork = async (payload) => {
    const result = await assignLecturerWork(payload);
    await fetchAdminData();
    return result;
  };

  const handleCreateStaffLogin = async (payload) => {
    const result = await createStaffLogin(payload);
    await fetchAdminData();
    return result;
  };

  const handleDeleteUser = async (userId) => {
    await deleteUser(userId);
    await fetchAdminData();
  };

  return (
    <div className="min-h-screen">
      <TopNavHeader
        activeTab={activeTab}
        onTabClick={setActiveTab}
        user={user}
        onLogin={(mode) => setAuthModal({ open: true, mode })}
        onLogout={handleLogout}
      />

      <main className="mx-auto w-full max-w-7xl px-4 py-6 sm:px-6 sm:py-8">
        {appError ? (
          <div className="mb-4 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">{appError}</div>
        ) : null}

        <PortalTabContent
          tab={activeTab}
          user={user}
          onLogin={(mode) => setAuthModal({ open: true, mode })}
          onNavigate={setActiveTab}
          adminUsers={adminUsers}
          suspiciousUsers={suspiciousUsers}
          loadingAdminData={loadingAdminData}
          reloadAdminData={fetchAdminData}
          onAssignLecturerWork={handleAssignLecturerWork}
          onCreateStaffLogin={handleCreateStaffLogin}
          onDeleteUser={handleDeleteUser}
        />
      </main>

      <AuthModal
        isOpen={authModal.open}
        initialMode={authModal.mode}
        onClose={() => setAuthModal((prev) => ({ ...prev, open: false }))}
        onAuthenticated={handleAuthenticated}
      />
    </div>
  );
}
