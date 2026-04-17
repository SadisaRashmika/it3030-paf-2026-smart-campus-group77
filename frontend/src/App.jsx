import { Navigate, Route, Routes } from "react-router-dom";
import MainLayout from "./layouts/MainLayout";
import AdminPage from "./pages/AdminPage";
import HomePage from "./pages/HomePage";
import LecturerPage from "./pages/LecturerPage";
import HelpPage from "./pages/HelpPage";
import NotFoundPage from "./pages/NotFoundPage";
import StudentPage from "./pages/StudentPage";
import BookingForm from "../member2-bookings-management/components/BookingForm";
import MyBookingsPage from "../member2-bookings-management/pages/MyBookingsPage";
import AdminBookingDashboard from "../member2-bookings-management/pages/AdminBookingDashboard";

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
        <Route path="student/bookings" element={<MyBookingsPage userId={1} />} />
        <Route path="student/book" element={
          <div style={{ padding: '2rem' }}>
            <h2 style={{ textAlign: 'center', marginBottom: '1rem', color: '#c084fc' }}>New Booking Request</h2>
            <BookingForm 
              resources={[]} 
              userId={1} 
              onSuccess={() => window.location.href = '/student/bookings'} 
            />
          </div>
        } />

        <Route path="help" element={<HelpPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
