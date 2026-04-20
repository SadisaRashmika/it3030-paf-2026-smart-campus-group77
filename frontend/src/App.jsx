import { Navigate, Route, Routes } from "react-router-dom";
import MainLayout from "./layouts/MainLayout";
import AdminPage from "./pages/AdminPage";
import HomePage from "./pages/HomePage";
import LecturerPage from "./pages/LecturerPage";
import HelpPage from "./pages/HelpPage";
import NotFoundPage from "./pages/NotFoundPage";
import ResourceAdministatorPage from "./pages/ResourceAdministatorPage";
import StudentPage from "./pages/StudentPage";
import TicketAdministratorPage from "./pages/TicketAdministratorPage";
import TimetableManagerPage from "./pages/TimetableManagerPage";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<HomePage />} />
        <Route path="admin" element={<AdminPage />} />
        <Route path="lecturer" element={<LecturerPage />} />
        <Route path="student" element={<StudentPage />} />
        <Route path="timetable-manager" element={<TimetableManagerPage />} />
        <Route path="resource-administator" element={<ResourceAdministatorPage />} />
        <Route path="ticket-administrator" element={<TicketAdministratorPage />} />
        <Route path="help" element={<HelpPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
