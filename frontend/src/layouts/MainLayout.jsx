import { useCallback, useEffect, useMemo, useState } from "react";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import AuthModal from "../member4-notifications-oauth/components/AuthModal";
import TopNavHeader from "../member4-notifications-oauth/components/TopNavHeader";
import {
  assignLecturerWork,
  createStaffLogin,
  deleteUser,
  getAdminUsers,
  getCurrentUser,
  getLecturerAssignments,
  getSuspiciousUsers,
  logout
} from "../services/authService";

const VALID_TABS = new Set(["home", "resource", "timetable", "jobs", "ticket"]);

const DEFAULT_TAB_BY_PATH = {
  "/": "home",
  "/admin": "timetable",
  "/lecturer": "resource",
  "/student": "home"
};

function getDefaultTab(pathname) {
  return DEFAULT_TAB_BY_PATH[pathname] || "home";
}

function resolveActiveTab(pathname, search) {
  const params = new URLSearchParams(search);
  const fromQuery = params.get("tab");
  if (fromQuery && VALID_TABS.has(fromQuery)) {
    return fromQuery;
  }
  return getDefaultTab(pathname);
}

function targetPathForUser(user) {
  const role = user?.role?.replace("ROLE_", "").toLowerCase();
  if (role === "admin") {
    return "/admin";
  }
  if (role === "lecturer") {
    return "/lecturer";
  }
  if (role === "student") {
    return "/student";
  }
  return "/";
}

export default function MainLayout() {
  const [user, setUser] = useState(null);
  const [authModal, setAuthModal] = useState({ open: false, mode: "login" });
  const [adminUsers, setAdminUsers] = useState([]);
  const [suspiciousUsers, setSuspiciousUsers] = useState([]);
  const [lecturerAssignments, setLecturerAssignments] = useState([]);
  const [loadingAdminData, setLoadingAdminData] = useState(false);
  const [appError, setAppError] = useState("");

  const location = useLocation();
  const navigate = useNavigate();

  const activeTab = useMemo(
    () => resolveActiveTab(location.pathname, location.search),
    [location.pathname, location.search]
  );

  const role = useMemo(() => user?.role?.replace("ROLE_", "").toLowerCase() || "", [user]);

  const fetchAdminData = useCallback(async () => {
    if (role !== "admin") {
      return;
    }

    try {
      setLoadingAdminData(true);
      const [users, suspicious, assignments] = await Promise.all([
        getAdminUsers(),
        getSuspiciousUsers(),
        getLecturerAssignments()
      ]);
      setAdminUsers(users);
      setSuspiciousUsers(suspicious);
      setLecturerAssignments(assignments);
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
    const params = new URLSearchParams(window.location.search);
    const authResult = params.get("auth");
    if (!authResult) {
      return;
    }

    const clearAuthParams = () => {
      params.delete("auth");
      params.delete("reason");
      const nextQuery = params.toString();
      const nextUrl = `${window.location.pathname}${nextQuery ? `?${nextQuery}` : ""}${window.location.hash}`;
      window.history.replaceState({}, "", nextUrl);
    };

    const applyOAuthSession = async () => {
      if (authResult === "google-failed") {
        const reason = params.get("reason");
        setAppError(reason || "Google sign-in failed. Please try again.");
        clearAuthParams();
        return;
      }

      let lastError = null;
      for (let attempt = 0; attempt < 3; attempt += 1) {
        try {
          const me = await getCurrentUser();
          setUser(me);
          setAppError("");
          navigate(targetPathForUser(me), { replace: true });
          clearAuthParams();
          return;
        } catch (error) {
          lastError = error;
          await new Promise((resolve) => setTimeout(resolve, 250));
        }
      }

      setAppError(lastError?.message || "Google sign-in completed, but session could not be restored.");
      clearAuthParams();
    };

    applyOAuthSession();
  }, [navigate]);

  useEffect(() => {
    if (role === "admin") {
      fetchAdminData();
    }
  }, [role, fetchAdminData]);

  const openLogin = (mode) => {
    setAuthModal({ open: true, mode });
  };

  const handleAuthenticated = (authUser) => {
    setUser(authUser);
    setAppError("");
    navigate(targetPathForUser(authUser));
  };

  const handleLogout = async () => {
    try {
      await logout();
    } catch {
      // Ignore logout request errors and clear local state regardless.
    }

    setUser(null);
    setAdminUsers([]);
    setSuspiciousUsers([]);
    setLecturerAssignments([]);
    navigate("/");
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

  const handleTabChange = (tab) => {
    if (!VALID_TABS.has(tab)) {
      return;
    }

    const params = new URLSearchParams(location.search);
    params.set("tab", tab);
    navigate(`${location.pathname}?${params.toString()}`);
  };

  return (
    <div className="min-h-screen">
      <TopNavHeader
        activeTab={activeTab}
        onTabClick={handleTabChange}
        user={user}
        onLogin={openLogin}
        onLogout={handleLogout}
      />

      <main className="mx-auto w-full max-w-7xl px-4 py-6 sm:px-6 sm:py-8">
        {appError ? (
          <div className="mb-4 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">{appError}</div>
        ) : null}

        <Outlet
          context={{
            user,
            activeTab,
            onLogin: openLogin,
            onNavigate: handleTabChange,
            adminUsers,
            suspiciousUsers,
            lecturerAssignments,
            loadingAdminData,
            reloadAdminData: fetchAdminData,
            onAssignLecturerWork: handleAssignLecturerWork,
            onCreateStaffLogin: handleCreateStaffLogin,
            onDeleteUser: handleDeleteUser
          }}
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
