import React, { useState, useEffect } from "react";
import { CalendarDays, CheckSquare } from "lucide-react";
import WelcomeWidget from "./widgets/WelcomeWidget";
import TodayScheduleWidget from "./widgets/TodayScheduleWidget";
import UpcomingClassWidget from "./widgets/UpcomingClassWidget";
import QuickActionsWidget from "./widgets/QuickActionsWidget";
import { getTodayTimetable, joinSession } from "../../services/bookingService";

const QUICK_ACTIONS = [
  { label: "View Full Timetable", icon: CalendarDays, href: "/student/timetable" },
  { label: "My Sessions", icon: CheckSquare, href: "/student/bookings" },
];

export default function StudentDashboard({ user }) {
  const [todayClasses, setTodayClasses] = useState([]);
  const [nextClass, setNextClass] = useState(null);
  const [hasMarked, setHasMarked] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const entries = await getTodayTimetable().catch(() => []);
        const now = new Date();

        const sorted = (entries || []).sort((a, b) => a.startTime.localeCompare(b.startTime));
        setTodayClasses(sorted.map((e) => ({
          title: e.title,
          startTime: e.startTime?.slice(0, 5),
          endTime: e.endTime?.slice(0, 5),
          resourceName: e.resource?.name,
        })));

        // Find the next upcoming class (most recent start time >= now - 1 hr)
        const nowMins = now.getHours() * 60 + now.getMinutes();
        const upcoming = sorted.find((e) => {
          const [h, m] = (e.startTime || "").split(":").map(Number);
          return h * 60 + m + 60 >= nowMins; // within past 1 hour or future
        });
        if (upcoming) setNextClass({ ...upcoming, startTime: upcoming.startTime?.slice(0, 5), endTime: upcoming.endTime?.slice(0, 5) });
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const handleMarkAttendance = async () => {
    if (!nextClass?.id) {
      // Timetable entries don't have booking IDs—demo the state change
      setHasMarked(true);
      return;
    }
    try {
      setIsProcessing(true);
      await joinSession(nextClass.id);
      setHasMarked(true);
    } catch (err) {
      alert(err.message || "Could not mark attendance.");
    } finally {
      setIsProcessing(false);
    }
  };

  if (loading) {
    return <div className="flex h-64 items-center justify-center text-slate-400 font-medium animate-pulse">Loading dashboard...</div>;
  }

  return (
    <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
      <WelcomeWidget name={user?.name} role={user?.role} />

      {/* Upcoming Class (hero widget) */}
      <section className="space-y-3">
        <div className="flex items-center gap-2">
          <div className="h-1.5 w-1.5 rounded-full bg-blue-500 animate-pulse" />
          <h2 className="text-xs font-black uppercase tracking-widest text-slate-400">Upcoming Class</h2>
        </div>
        <UpcomingClassWidget
          nextClass={nextClass}
          hasMarked={hasMarked}
          onMarkAttendance={handleMarkAttendance}
          isProcessing={isProcessing}
        />
      </section>

      {/* Full today's schedule */}
      <section className="space-y-3">
        <div className="flex items-center gap-2">
          <div className="h-1.5 w-1.5 rounded-full bg-emerald-500" />
          <h2 className="text-xs font-black uppercase tracking-widest text-slate-400">
            Today's Classes ({todayClasses.length})
          </h2>
        </div>
        <TodayScheduleWidget entries={todayClasses} emptyMessage="No classes scheduled for today." />
      </section>

      {/* Quick Actions */}
      <section className="space-y-3">
        <h2 className="text-xs font-black uppercase tracking-widest text-slate-400">Quick Access</h2>
        <QuickActionsWidget actions={QUICK_ACTIONS} />
      </section>
    </div>
  );
}
