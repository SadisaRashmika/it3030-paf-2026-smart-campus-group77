import React, { useCallback, useEffect, useMemo, useState } from "react";
import { bookingApi } from "../services/bookingApi";

const TimetableWeeklyGrid = () => {
  const [weeklyBookings, setWeeklyBookings] = useState([]);
  const [resources, setResources] = useState([]);
  const [selectedResourceId, setSelectedResourceId] = useState("all");
  const [loading, setLoading] = useState(true);
  const [visibleStart, setVisibleStart] = useState(() => {
    const start = new Date();
    start.setHours(0, 0, 0, 0);
    return start;
  });

  // 7-day window starting from selected day.
  const rangeStart = useMemo(() => {
    const start = new Date(visibleStart);
    start.setHours(0, 0, 0, 0);
    return start;
  }, [visibleStart]);

  const rangeEnd = useMemo(() => {
    const end = new Date(rangeStart);
    end.setDate(end.getDate() + 6);
    return end;
  }, [rangeStart]);

  const isCurrentWindow = useMemo(() => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return rangeStart.getTime() === today.getTime();
  }, [rangeStart]);

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      const weekEnd = new Date(rangeStart);
      weekEnd.setDate(weekEnd.getDate() + 7);

      // Helper to get ISO string without the 'Z' (Local Time)
      const toLocalISO = (date) => {
        const offset = date.getTimezoneOffset();
        const localDate = new Date(date.getTime() - (offset * 60 * 1000));
        return localDate.toISOString().split('Z')[0];
      };

      const [resData, bookingData] = await Promise.all([
        bookingApi.getResources(),
        bookingApi.getWeeklyBookings(toLocalISO(rangeStart), toLocalISO(weekEnd))
      ]);
      setResources(resData);
      setWeeklyBookings(bookingData);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [rangeStart]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  useEffect(() => {
    // Keep timetable live while page stays open.
    const refreshInterval = window.setInterval(() => {
      fetchData();
    }, 30000);

    const onFocus = () => {
      fetchData();
    };

    window.addEventListener("focus", onFocus);

    return () => {
      window.clearInterval(refreshInterval);
      window.removeEventListener("focus", onFocus);
    };
  }, [fetchData]);

  const shiftWeek = (days) => {
    setVisibleStart((previous) => {
      const next = new Date(previous);
      next.setDate(next.getDate() + days);
      next.setHours(0, 0, 0, 0);
      return next;
    });
  };

  const goToToday = () => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    setVisibleStart(today);
  };

  const days = Array.from({ length: 7 }, (_, offset) => {
    const date = new Date(rangeStart);
    date.setDate(rangeStart.getDate() + offset);
    return {
      key: date.toISOString(),
      label: date.toLocaleDateString([], { weekday: "short" }),
      date
    };
  });
  const hours = Array.from({ length: 14 }, (_, i) => i + 8); // 8 AM to 9 PM

  const filteredBookings = selectedResourceId === "all" 
    ? weeklyBookings 
    : weeklyBookings.filter(b => b.resourceId === parseInt(selectedResourceId));

  const getBookingsForSlot = (dayDate, hour) => {
    const slotStart = new Date(dayDate);
    slotStart.setHours(hour, 0, 0, 0);
    const slotEnd = new Date(slotStart);
    slotEnd.setHours(slotEnd.getHours() + 1);

    return filteredBookings.filter(b => {
      const d = new Date(b.startTime);
      const end = new Date(b.endTime);
      return (
        d.getFullYear() === dayDate.getFullYear() &&
        d.getMonth() === dayDate.getMonth() &&
        d.getDate() === dayDate.getDate() &&
        d < slotEnd &&
        end > slotStart
      );
    });
  };

  if (loading) return <div className="p-10 text-center text-slate-400">Rendering campus schedule...</div>;

  return (
    <div className="max-w-7xl mx-auto p-4 md:p-8 space-y-6 animate-in fade-in duration-500">
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6">
        <div>
          <h1 className="font-display text-3xl font-extrabold text-slate-900 tracking-tight flex items-center">
            Weekly Master Timetable
          </h1>
          <p className="text-slate-500">Global overview of all approved resource allocations.</p>
          <p className="mt-1 text-xs font-semibold uppercase tracking-wide text-slate-500">
            {rangeStart.toLocaleDateString([], { month: "short", day: "numeric" })} to {rangeEnd.toLocaleDateString([], { month: "short", day: "numeric", year: "numeric" })}
          </p>
        </div>

        <div className="flex flex-col gap-3">
          <div className="flex items-center gap-2">
            <button
              type="button"
              onClick={() => shiftWeek(-7)}
              className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs font-bold text-slate-700 transition hover:border-amber-300 hover:bg-amber-50"
            >
              Previous Week
            </button>
            <button
              type="button"
              onClick={goToToday}
              disabled={isCurrentWindow}
              className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs font-bold text-slate-700 transition hover:border-amber-300 hover:bg-amber-50 disabled:cursor-not-allowed disabled:opacity-60"
            >
              Today
            </button>
            <button
              type="button"
              onClick={() => shiftWeek(7)}
              className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs font-bold text-slate-700 transition hover:border-amber-300 hover:bg-amber-50"
            >
              Next Week
            </button>
          </div>

          <div className="flex flex-col gap-2">
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest pl-1">Filter by Resource</label>
            <select
              className="bg-white border border-slate-200 px-4 py-2 rounded-xl outline-none focus:ring-2 focus:ring-amber-500 shadow-sm"
              value={selectedResourceId}
              onChange={(e) => setSelectedResourceId(e.target.value)}
            >
              <option value="all">Global View (All Resources)</option>
              {resources.map(res => (
                  <option key={res.id} value={res.id}>{res.name}</option>
              ))}
            </select>
          </div>
        </div>
      </header>

      <div className="bg-white rounded-[2rem] shadow-2xl border border-slate-200 overflow-hidden flex flex-col">
          {/* Header row */}
          <div className="grid grid-cols-8 border-b border-slate-100 bg-slate-50/50">
              <div className="p-4 border-r border-slate-100"></div>
              {days.map((day) => (
                  <div key={day.key} className="p-4 text-center border-r border-slate-100 last:border-r-0">
                      <span className="block text-xs font-black text-slate-400 uppercase tracking-tighter">{day.label}</span>
                      <span className="text-sm font-bold text-slate-700">
                        {day.date.getDate()}
                      </span>
                  </div>
              ))}
          </div>

          {/* Grid body */}
          <div className="h-[600px] overflow-y-auto">
            {hours.map(hour => (
                <div key={hour} className="grid grid-cols-8 border-b border-slate-50 last:border-b-0 min-h-[60px]">
                    <div className="p-3 border-r border-slate-50 text-right bg-slate-50/30">
                        <span className="text-[10px] font-bold text-slate-400">{hour}:00</span>
                    </div>
                    {days.map((day) => {
                      const slots = getBookingsForSlot(day.date, hour);
                        return (
                        <div key={day.key} className="p-1 border-r border-slate-50 last:border-r-0 relative group hover:bg-slate-50/30 transition-colors">
                                {slots.map(s => (
                                    <div 
                                      key={s.id} 
                                      className="absolute inset-x-1 top-1 bottom-1 bg-amber-500 text-amber-950 rounded-lg p-2 shadow-sm shadow-amber-200 overflow-hidden z-10 transition-transform hover:scale-105 hover:z-20 border border-amber-400"
                                      title={`${s.resourceName}: ${s.purpose}`}
                                    >
                                        <div className="text-[9px] font-black uppercase tracking-tighter opacity-70 truncate">{s.resourceName}</div>
                                        <div className="text-[10px] font-bold leading-tight line-clamp-2">{s.purpose}</div>
                                    </div>
                                ))}
                            </div>
                        );
                    })}
                </div>
            ))}
          </div>
      </div>

      <footer className="flex justify-center gap-6 py-4">
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 bg-amber-500 rounded-full"></div>
              <span className="text-xs text-slate-500 font-medium italic">Approved Booking</span>
          </div>
          <div className="flex items-center gap-2">
              <div className="w-3 h-3 bg-slate-100 rounded-full border border-slate-200"></div>
              <span className="text-xs text-slate-500 font-medium italic">Available Slot</span>
          </div>
      </footer>
    </div>
  );
};

export default TimetableWeeklyGrid;
