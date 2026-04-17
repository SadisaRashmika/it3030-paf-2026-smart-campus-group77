import React from "react";

const ROLE_LABELS = {
  admin: "Administrator",
  timetable_manager: "Timetable Manager",
  lecturer: "Lecturer",
  student: "Student",
};

const ROLE_COLORS = {
  admin: "bg-rose-100 text-rose-700",
  timetable_manager: "bg-violet-100 text-violet-700",
  lecturer: "bg-blue-100 text-blue-700",
  student: "bg-emerald-100 text-emerald-700",
};

export default function WelcomeWidget({ name, role }) {
  const roleKey = (role || "").replace("ROLE_", "").toLowerCase();
  const label = ROLE_LABELS[roleKey] || roleKey;
  const color = ROLE_COLORS[roleKey] || "bg-slate-100 text-slate-600";
  const hour = new Date().getHours();
  const greeting = hour < 12 ? "Good morning" : hour < 17 ? "Good afternoon" : "Good evening";

  return (
    <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
      <div>
        <h1 className="text-3xl font-extrabold tracking-tight text-slate-900">
          {greeting}, <span className="text-blue-600">{name || "User"}</span> 👋
        </h1>
        <p className="mt-1 text-sm text-slate-500 font-medium">
          {new Date().toLocaleDateString("en-US", { weekday: "long", year: "numeric", month: "long", day: "numeric" })}
        </p>
      </div>
      <span className={`self-start sm:self-auto px-3 py-1.5 rounded-xl text-xs font-black uppercase tracking-widest ${color}`}>
        {label}
      </span>
    </div>
  );
}
