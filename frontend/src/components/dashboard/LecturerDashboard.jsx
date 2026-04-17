import React, { useState, useEffect } from "react";
import { BookOpen, CalendarDays, History, PlusCircle } from "lucide-react";
import WelcomeWidget from "./widgets/WelcomeWidget";
import TodayScheduleWidget from "./widgets/TodayScheduleWidget";
import PendingRequestsWidget from "./widgets/PendingRequestsWidget";
import QuickActionsWidget from "./widgets/QuickActionsWidget";
import { getMyBookings, getTodayMineBookings, getTodayTimetable, cancelBooking } from "../../services/bookingService";

const QUICK_ACTIONS = [
  { label: "Book a Resource", icon: PlusCircle, href: "/lecturer/book" },
  { label: "View Full Timetable", icon: CalendarDays, href: "/lecturer/timetable" },
  { label: "My Booking History", icon: History, href: "/student/bookings" },
];

export default function LecturerDashboard({ user }) {
  const [todayEntries, setTodayEntries] = useState([]);
  const [pendingBookings, setPendingBookings] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [timetableToday, myBookings, todayBookings] = await Promise.all([
        getTodayTimetable().catch(() => []),
        getMyBookings().catch(() => []),
        getTodayMineBookings().catch(() => []),
      ]);

      // Merge timetable + today's approved bookings into a combined today schedule
      const timetableEvents = (timetableToday || []).map((e) => ({
        title: e.title,
        startTime: e.startTime,
        endTime: e.endTime,
        resourceName: e.resource?.name,
        type: "CLASS",
      }));

      const bookingEvents = (todayBookings || []).map((b) => ({
        title: b.resource?.name + " (Booking)",
        startTime: new Date(b.startTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit", hour12: false }),
        endTime: new Date(b.endTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit", hour12: false }),
        resourceName: b.resource?.name,
        type: "BOOKING",
      }));

      const combined = [...timetableEvents, ...bookingEvents].sort(
        (a, b) => a.startTime.localeCompare(b.startTime)
      );
      setTodayEntries(combined);
      setPendingBookings((myBookings || []).filter((b) => b.status === "PENDING"));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchData(); }, []);

  const handleCancel = async (id) => {
    if (!window.confirm("Cancel this booking request?")) return;
    try {
      await cancelBooking(id);
      setPendingBookings((prev) => prev.filter((b) => b.id !== id));
    } catch (err) {
      alert(err.message || "Failed to cancel.");
    }
  };

  if (loading) {
    return <div className="flex h-64 items-center justify-center text-slate-400 font-medium animate-pulse">Loading dashboard...</div>;
  }

  return (
    <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
      <WelcomeWidget name={user?.name} role={user?.role} />

      <div className="grid gap-8 lg:grid-cols-2">
        {/* Today's Schedule */}
        <section className="space-y-4">
          <div className="flex items-center gap-2">
            <div className="h-1.5 w-1.5 rounded-full bg-blue-500 animate-pulse" />
            <h2 className="text-xs font-black uppercase tracking-widest text-slate-400">Today's Schedule</h2>
          </div>
          <TodayScheduleWidget
            entries={todayEntries}
            emptyMessage="No classes or bookings for today."
          />
        </section>

        {/* Pending Requests */}
        <section className="space-y-4">
          <div className="flex items-center gap-2">
            <div className="h-1.5 w-1.5 rounded-full bg-amber-500 animate-pulse" />
            <h2 className="text-xs font-black uppercase tracking-widest text-slate-400">
              Pending Requests ({pendingBookings.length})
            </h2>
          </div>
          <PendingRequestsWidget bookings={pendingBookings} onCancel={handleCancel} />
        </section>
      </div>

      {/* Quick Actions */}
      <section className="space-y-4">
        <h2 className="text-xs font-black uppercase tracking-widest text-slate-400">Quick Actions</h2>
        <QuickActionsWidget actions={QUICK_ACTIONS} />
      </section>
    </div>
  );
}
