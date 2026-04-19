import React, { useState, useEffect } from "react";
import { bookingApi } from "../services/bookingApi";

const TimetableWeeklyGrid = () => {
  const [weeklyBookings, setWeeklyBookings] = useState([]);
  const [resources, setResources] = useState([]);
  const [selectedResourceId, setSelectedResourceId] = useState("all");
  const [loading, setLoading] = useState(true);

  // Hardcoded for visibility - the current week (example)
  const [weekStart] = useState(() => {
    const d = new Date();
    const day = d.getDay(); // 0 is Sun, 1 is Mon...
    const diff = d.getDate() - day + (day === 0 ? -6 : 1);
    const start = new Date(d.setDate(diff));
    start.setHours(0, 0, 0, 0);
    return start;
  });

  useEffect(() => {
    fetchData();
  }, [weekStart]);

  const fetchData = async () => {
    try {
      setLoading(true);
      const weekEnd = new Date(weekStart);
      weekEnd.setDate(weekEnd.getDate() + 7);

      // Helper to get ISO string without the 'Z' (Local Time)
      const toLocalISO = (date) => {
        const offset = date.getTimezoneOffset();
        const localDate = new Date(date.getTime() - (offset * 60 * 1000));
        return localDate.toISOString().split('Z')[0];
      };

      const [resData, bookingData] = await Promise.all([
        bookingApi.getResources(),
        bookingApi.getWeeklyBookings(toLocalISO(weekStart), toLocalISO(weekEnd))
      ]);
      setResources(resData);
      setWeeklyBookings(bookingData);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const days = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];
  const hours = Array.from({ length: 14 }, (_, i) => i + 8); // 8 AM to 9 PM

  const filteredBookings = selectedResourceId === "all" 
    ? weeklyBookings 
    : weeklyBookings.filter(b => b.resourceId === parseInt(selectedResourceId));

  const getBookingsForSlot = (dayIndex, hour) => {
    return filteredBookings.filter(b => {
      const d = new Date(b.startTime);
      const dayOfBooking = (d.getDay() + 6) % 7; // Convert Sun=0..Sat=6 to Mon=0..Sun=6
      const startHour = d.getHours();
      return dayOfBooking === dayIndex && startHour === hour;
    });
  };

  if (loading) return <div className="p-10 text-center text-slate-400">Rendering campus schedule...</div>;

  return (
    <div className="max-w-7xl mx-auto p-4 md:p-8 space-y-6 animate-in fade-in duration-500">
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6">
        <div>
          <h1 className="text-3xl font-black text-slate-900 tracking-tight flex items-center">
            <span className="mr-3">🗓️</span> Weekly Master Timetable
          </h1>
          <p className="text-slate-500">Global overview of all approved resource allocations.</p>
        </div>

        <div className="flex flex-col gap-2">
          <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest pl-1">Filter by Resource</label>
          <select 
            className="bg-white border border-slate-200 px-4 py-2 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm"
            value={selectedResourceId}
            onChange={(e) => setSelectedResourceId(e.target.value)}
          >
            <option value="all">Global View (All Resources)</option>
            {resources.map(res => (
                <option key={res.id} value={res.id}>{res.name}</option>
            ))}
          </select>
        </div>
      </header>

      <div className="bg-white rounded-[2rem] shadow-2xl border border-slate-100 overflow-hidden flex flex-col">
          {/* Header row */}
          <div className="grid grid-cols-8 border-b border-slate-100 bg-slate-50/50">
              <div className="p-4 border-r border-slate-100"></div>
              {days.map((day, i) => (
                  <div key={day} className="p-4 text-center border-r border-slate-100 last:border-r-0">
                      <span className="block text-xs font-black text-slate-400 uppercase tracking-tighter">{day}</span>
                      <span className="text-sm font-bold text-slate-700">
                        {new Date(new Date(weekStart).setDate(weekStart.getDate() + i)).getDate()}
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
                    {days.map((_, dayIndex) => {
                        const slots = getBookingsForSlot(dayIndex, hour);
                        return (
                            <div key={dayIndex} className="p-1 border-r border-slate-50 last:border-r-0 relative group hover:bg-slate-50/30 transition-colors">
                                {slots.map(s => (
                                    <div 
                                      key={s.id} 
                                      className="absolute inset-x-1 top-1 bottom-1 bg-indigo-500 text-white rounded-lg p-2 shadow-sm shadow-indigo-200 overflow-hidden z-10 transition-transform hover:scale-105 hover:z-20 border border-indigo-400"
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
              <div className="w-3 h-3 bg-indigo-500 rounded-full"></div>
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
