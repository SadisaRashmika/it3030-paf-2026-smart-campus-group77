import { useCallback, useEffect, useMemo, useState } from "react";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import AuthModal from "../member4-notifications-oauth/components/AuthModal";
import TopNavHeader from "../member4-notifications-oauth/components/TopNavHeader";
import {
  assignLecturerWork,
  approveRecoveryRequest,
  createStaffLogin,
  deactivateUser,
  deleteUser,
  getAdminUsers,
  getCurrentUser,
  getLecturerAssignments,
  getMyNotifications,
  getRecoveryRequests,
  getSuspiciousUsers,
  markAllNotificationsRead,
  markNotificationRead,
  rejectRecoveryRequest,
  reportSuspiciousLogin,
  updateProfilePicture,
  logout
} from "../services/authService";
import ProfileModal from "../member4-notifications-oauth/components/ProfileModal";

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

function normalizeNotification(item) {
  const resolvedRead =
    typeof item?.read === "boolean"
      ? item.read
      : typeof item?.isRead === "boolean"
        ? item.isRead
        : true;

  return {
    ...item,
    read: resolvedRead
  };
}

function getNotificationStorageKey(user) {
  const userKey = user?.userId || user?.email || "anonymous";
  return `smartcampus:last-notification-seen-at:${String(userKey).trim().toLowerCase()}`;
}

export default function MainLayout() {
  const [user, setUser] = useState(null);
  const [authModal, setAuthModal] = useState({ open: false, mode: "login", email: "" });
  const [adminUsers, setAdminUsers] = useState([]);
  const [suspiciousUsers, setSuspiciousUsers] = useState([]);
  const [lecturerAssignments, setLecturerAssignments] = useState([]);
  const [recoveryRequests, setRecoveryRequests] = useState([]);
  const [notifications, setNotifications] = useState([]);
  const [loadingNotifications, setLoadingNotifications] = useState(false);
  const [loadingAdminData, setLoadingAdminData] = useState(false);
  const [reportingSuspicious, setReportingSuspicious] = useState(false);
  const [notificationsLastSeenAt, setNotificationsLastSeenAt] = useState("");
  const [appError, setAppError] = useState("");
  const [appNotice, setAppNotice] = useState("");
  const [profileModalOpen, setProfileModalOpen] = useState(false);

  const location = useLocation();
  const navigate = useNavigate();

  const activeTab = useMemo(
    () => resolveActiveTab(location.pathname, location.search),
    [location.pathname, location.search]
  );

  const role = useMemo(() => user?.role?.replace("ROLE_", "").toLowerCase() || "", [user]);

  useEffect(() => {
    const storageKey = getNotificationStorageKey(user);
    const savedValue = window.localStorage.getItem(storageKey) || "";
    setNotificationsLastSeenAt(savedValue);
  }, [user]);

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
      setAppNotice("");
    } finally {
      setLoadingAdminData(false);
    }
  }, [role]);

  const fetchRecoveryRequests = useCallback(async () => {
    if (role !== "admin") {
      return;
    }

    try {
      const recoveryQueue = await getRecoveryRequests();
      setRecoveryRequests(Array.isArray(recoveryQueue) ? recoveryQueue : []);
    } catch (error) {
      setRecoveryRequests([]);
      if (error?.message && !String(error.message).includes("404")) {
        setAppError(error.message);
        setAppNotice("");
      }
    }
  }, [role]);

  const fetchNotifications = useCallback(async () => {
    if (!user) {
      setNotifications([]);
      return [];
    }

    try {
      setLoadingNotifications(true);
      const list = await getMyNotifications();
      const normalizedList = Array.isArray(list) ? list.map(normalizeNotification) : [];
      setNotifications(normalizedList);
      return normalizedList;
    } catch {
      // Keep notification dropdown silent on transient errors.
      return [];
    } finally {
      setLoadingNotifications(false);
    }
  }, [user]);

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
        setAppNotice("");
        clearAuthParams();
        return;
      }

      let lastError = null;
      for (let attempt = 0; attempt < 3; attempt += 1) {
        try {
          const me = await getCurrentUser();
          setUser(me);
          setAppError("");
          setAppNotice("");
          navigate(targetPathForUser(me), { replace: true });
          clearAuthParams();
          return;
        } catch (error) {
          lastError = error;
          await new Promise((resolve) => setTimeout(resolve, 250));
        }
      }

      setAppError(lastError?.message || "Google sign-in completed, but session could not be restored.");
      setAppNotice("");
      clearAuthParams();
    };

    applyOAuthSession();
  }, [navigate]);

  useEffect(() => {
    if (role === "admin") {
      fetchAdminData();
    }
  }, [role, fetchAdminData]);

  useEffect(() => {
    if (role === "admin" && activeTab === "ticket") {
      fetchRecoveryRequests();
    }
  }, [role, activeTab, fetchRecoveryRequests]);

  useEffect(() => {
    if (!user) {
      setNotifications([]);
      return;
    }

    fetchNotifications();
  }, [user, fetchNotifications]);

  const openLogin = (mode) => {
    setAuthModal({ open: true, mode, email: "" });
  };

  const openProfileModal = () => {
    setProfileModalOpen(true);
  };

  const openChangePassword = () => {
    setProfileModalOpen(false);
    setAuthModal({
      open: true,
      mode: "forgot",
      email: user?.email || ""
    });
  };

  const handleAuthenticated = (authUser) => {
    setUser(authUser);
    setAppError("");
    setAppNotice("");
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
    setRecoveryRequests([]);
    setNotifications([]);
    setAppNotice("");
    setProfileModalOpen(false);
    navigate("/");
  };

  const handleProfilePictureUpdate = async (profilePictureDataUrl) => {
    const updatedUser = await updateProfilePicture(profilePictureDataUrl);
    setUser(updatedUser);
    return updatedUser;
  };

  const handleOpenNotifications = async () => {
    const openedAt = new Date().toISOString();
    const storageKey = getNotificationStorageKey(user);
    window.localStorage.setItem(storageKey, openedAt);
    setNotificationsLastSeenAt(openedAt);

    try {
      await markAllNotificationsRead();
    } catch {
      // Keep dropdown usable even if mark-all fails.
    }

    await fetchNotifications();
  };

  const handleMarkNotificationRead = async (notificationId) => {
    try {
      const updated = await markNotificationRead(notificationId);
      const normalizedUpdated = normalizeNotification(updated);
      setNotifications((prev) =>
        prev.map((item) => (item.id === normalizedUpdated.id ? normalizedUpdated : item))
      );
    } catch {
      // Ignore read-state failures in UI.
    }
  };

  const handleMarkAllNotificationsRead = async () => {
    try {
      await markAllNotificationsRead();
      setNotifications((prev) => prev.map((item) => ({ ...item, read: true })));
    } catch {
      // Ignore read-state failures in UI.
    }
  };

  const handleReportSuspiciousLogin = async () => {
    if (!user) {
      return;
    }

    const roleKey = (user.role || "").replace("ROLE_", "").toLowerCase();
    if (roleKey !== "student" && roleKey !== "lecturer") {
      return;
    }

    const shouldReport = window.confirm(
      "Report this account login as suspicious? Admin will be notified and your account will be flagged for review."
    );

    if (!shouldReport) {
      return;
    }

    setReportingSuspicious(true);
    setAppError("");
    setAppNotice("");

    try {
      const response = await reportSuspiciousLogin({
        userId: user.userId,
        email: user.email
      });

      setAppNotice(response?.message || "Suspicious login reported to admin.");
      if (role === "admin") {
        await fetchAdminData();
      }
    } catch (error) {
      setAppError(error.message || "Unable to report suspicious login right now.");
    } finally {
      setReportingSuspicious(false);
    }
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

  const handleDeactivateUser = async (userId) => {
    await deactivateUser(userId);
    await fetchAdminData();
  };

  const handleApproveRecoveryRequest = async (requestId) => {
    await approveRecoveryRequest(requestId);
    await fetchRecoveryRequests();
  };

  const handleRejectRecoveryRequest = async (requestId) => {
    await rejectRecoveryRequest(requestId);
    await fetchRecoveryRequests();
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
        notifications={notifications}
        loadingNotifications={loadingNotifications}
        onOpenNotifications={handleOpenNotifications}
        onMarkNotificationRead={handleMarkNotificationRead}
        onMarkAllNotificationsRead={handleMarkAllNotificationsRead}
        onReportSuspicious={handleReportSuspiciousLogin}
        onOpenProfile={openProfileModal}
        reportingSuspicious={reportingSuspicious}
        lastSeenNotificationAt={notificationsLastSeenAt}
      />

      <main className="mx-auto w-full max-w-7xl px-4 py-6 sm:px-6 sm:py-8">
        {appNotice ? (
          <div className="mb-4 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">{appNotice}</div>
        ) : null}
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
            recoveryRequests,
            loadingAdminData,
            reloadAdminData: fetchAdminData,
            onAssignLecturerWork: handleAssignLecturerWork,
            onCreateStaffLogin: handleCreateStaffLogin,
            onDeleteUser: handleDeleteUser,
            onDeactivateUser: handleDeactivateUser,
            onApproveRecoveryRequest: handleApproveRecoveryRequest,
            onRejectRecoveryRequest: handleRejectRecoveryRequest
          }}
        />
      </main>

      <AuthModal
        isOpen={authModal.open}
        initialMode={authModal.mode}
        initialEmail={authModal.email}
        onClose={() => setAuthModal((prev) => ({ ...prev, open: false }))}
        onAuthenticated={handleAuthenticated}
      />

      <ProfileModal
        isOpen={profileModalOpen}
        user={user}
        onClose={() => setProfileModalOpen(false)}
        onChangePassword={openChangePassword}
        onSaveProfilePicture={handleProfilePictureUpdate}
      />
    </div>
  );
}
