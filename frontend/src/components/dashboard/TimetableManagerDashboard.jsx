import React, { useState, useEffect } from "react";
import { BookOpen, CalendarDays, CheckCircle, Layout, LayoutGrid, PlusCircle, Wrench } from "lucide-react";
import WelcomeWidget from "./widgets/WelcomeWidget";
import StatsCardWidget from "./widgets/StatsCardWidget";
import QuickActionsWidget from "./widgets/QuickActionsWidget";
import AdminBookingDashboard from "../../member2-bookings-management/components/AdminBookingDashboard";
import { getTimetableStats } from "../../services/bookingService";
import { getAllResources } from "../../services/bookingService";

const QUICK_ACTIONS = [
  { label: "Add New Class", icon: PlusCircle, href: "/timetable" },
  { label: "Manage Timetable", icon: CalendarDays, href: "/timetable" },
  { label: "All Ad-Hoc Bookings", icon: Layout, href: "/admin/bookings" },
];

export default function TimetableManagerDashboard({ user }) {
  const [stats, setStats] = useState(null);
  const [resourceCount, setResourceCount] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const [s, res] = await Promise.all([
          getTimetableStats().catch(() => null),
          getAllResources().catch(() => []),
        ]);
        setStats(s);
        setResourceCount(Array.isArray(res) ? res.length : 0);
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  const statCards = [
    {
      label: "Bookable Resources",
      value: resourceCount,
      icon: LayoutGrid,
      iconBg: "bg-blue-100",
      iconColor: "text-blue-600",
    },
    {
      label: "Pending Requests",
      value: loading ? "..." : (stats?.pendingBookings ?? "—"),
      icon: CheckCircle,
      iconBg: "bg-amber-100",
      iconColor: "text-amber-600",
    },
    {
      label: "Weekly Classes",
      value: loading ? "..." : (stats?.totalWeeklyClasses ?? "—"),
      icon: CalendarDays,
      iconBg: "bg-emerald-100",
      iconColor: "text-emerald-600",
    },
    {
      label: "Most Booked",
      value: loading ? "..." : (stats?.mostBookedResource ?? "N/A"),
      icon: BookOpen,
      iconBg: "bg-violet-100",
      iconColor: "text-violet-600",
    },
  ];

  return (
    <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
      {/* Header */}
      <div>
        <div className="flex items-center gap-2 mb-1">
          <Wrench size={20} className="text-violet-600" />
          <span className="text-xs font-black uppercase tracking-widest text-violet-500">Manager View</span>
        </div>
        <h1 className="text-3xl font-extrabold tracking-tight text-slate-900">Timetable Management Dashboard</h1>
        <p className="mt-1 text-sm text-slate-500 font-medium">
          {new Date().toLocaleDateString("en-US", { weekday: "long", year: "numeric", month: "long", day: "numeric" })}
        </p>
      </div>

      {/* Stats */}
      <section className="space-y-4">
        <h2 className="text-xs font-black uppercase tracking-widest text-slate-400">System At-a-Glance</h2>
        <StatsCardWidget cards={statCards} />
      </section>

      {/* Pending Approvals */}
      <section className="space-y-4">
        <div className="flex items-center gap-2">
          <div className="h-1.5 w-1.5 rounded-full bg-amber-500 animate-pulse" />
          <h2 className="text-xs font-black uppercase tracking-widest text-slate-400">Pending Ad-Hoc Booking Requests</h2>
        </div>
        <AdminBookingDashboard />
      </section>

      {/* Quick Actions */}
      <section className="space-y-4">
        <h2 className="text-xs font-black uppercase tracking-widest text-slate-400">Quick Actions</h2>
        <QuickActionsWidget actions={QUICK_ACTIONS} />
      </section>
    </div>
  );
}
