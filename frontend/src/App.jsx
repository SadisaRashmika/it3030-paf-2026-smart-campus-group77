import { Navigate, Route, Routes, useOutletContext } from "react-router-dom";
import MainLayout from "./layouts/MainLayout";
import AdminPage from "./pages/AdminPage";
import HomePage from "./pages/HomePage";
import LecturerPage from "./pages/LecturerPage";
import HelpPage from "./pages/HelpPage";
import NotFoundPage from "./pages/NotFoundPage";
import StudentPage from "./pages/StudentPage";
import ResourcePanel from "./member2-bookings-management/components/ResourcePanel";
import MyBookingsPage from "./member2-bookings-management/pages/MyBookingsPage";
import AdminBookingDashboard from "./member2-bookings-management/components/AdminBookingDashboard";
import TimetableWeeklyView from "./components/dashboard/TimetableWeeklyView";

// Wrapper to pass user from outlet context into ResourcePanel
function ResourcePanelRoute() {
  const { user } = useOutletContext();
  return <ResourcePanel user={user} />;
}

// Wrapper to pass user into TimetableWeeklyView
function TimetableRoute() {
  const { user } = useOutletContext();
  const role = (user?.role || "").replace("ROLE_", "").toLowerCase();
  return <TimetableWeeklyView user={user} readOnly={role === "student"} />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<HomePage />} />
        <Route path="admin" element={<AdminPage />} />
        <Route path="lecturer" element={<LecturerPage />} />
        <Route path="student" element={<StudentPage />} />

        {/* Member 2: Booking Management */}
        <Route path="admin/bookings" element={<AdminBookingDashboard />} />
        <Route path="student/bookings" element={<MyBookingsPage />} />
        <Route path="student/book" element={<ResourcePanelRoute />} />
        <Route path="lecturer/book" element={<ResourcePanelRoute />} />
        <Route path="lecturer/bookings" element={<MyBookingsPage />} />

        {/* Timetable — accessible to all roles */}
        <Route path="timetable" element={<TimetableRoute />} />
        <Route path="student/timetable" element={<TimetableRoute />} />
        <Route path="lecturer/timetable" element={<TimetableRoute />} />
        <Route path="admin/timetable" element={<TimetableRoute />} />

        <Route path="help" element={<HelpPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
