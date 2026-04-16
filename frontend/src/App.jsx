import { Navigate, Route, Routes } from "react-router-dom";
import MainLayout from "./layouts/MainLayout";
import AdminPage from "./pages/AdminPage";
import HomePage from "./pages/HomePage";
import LecturerPage from "./pages/LecturerPage";
import NotFoundPage from "./pages/NotFoundPage";
import StudentPage from "./pages/StudentPage";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<HomePage />} />
        <Route path="admin" element={<AdminPage />} />
        <Route path="lecturer" element={<LecturerPage />} />
        <Route path="student" element={<StudentPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
