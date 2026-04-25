import React, { useState, useEffect } from "react";
import { bookingApi } from "../../member2-bookings-management/services/bookingApi";
import { resourceApi } from "../services/resourceApi";

const ResourceAvailabilityView = () => {
  const [resources, setResources] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [resData, bookingData] = await Promise.all([
        resourceApi.getResources(),
        bookingApi.getWeeklyBookings(
            new Date(new Date().setHours(0,0,0,0)).toISOString(),
            new Date(new Date().setHours(23,59,59,999)).toISOString()
        )
      ]);
      setResources(resData);
      setBookings(bookingData);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const isResourceBusyNow = (resourceId) => {
    const now = new Date();
    return bookings.find(b =>
      b.resourceId === resourceId &&
      new Date(b.startTime) <= now &&
      new Date(b.endTime) >= now
    );
  };

  if (loading) return <div className="p-10 text-center text-slate-400">Inventorying resources...</div>;

  return (
    <div className="max-w-6xl mx-auto p-4 md:p-8 space-y-8 animate-in fade-in duration-500">
      <header>
        <h1 className="font-display text-3xl font-extrabold text-slate-900 tracking-tight flex items-center">
          Resource Status Tracker
        </h1>
        <p className="text-slate-500">Real-time availability monitoring for campus assets.</p>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {resources.map((res) => {
          const activeBooking = isResourceBusyNow(res.id);
          const isBusy = !!activeBooking;

          return (
            <div
              key={res.id}
              className={`bg-white border border-slate-200 rounded-3xl p-6 shadow-sm hover:shadow-xl transition-all border-l-8 ${isBusy ? 'border-l-rose-500' : 'border-l-amber-500'}`}
            >
              <div className="flex justify-between items-start mb-4">
                <div>
                  <h3 className="font-bold text-slate-800 text-lg">{res.name}</h3>
                  <span className="text-xs font-bold text-slate-400 uppercase tracking-widest">{res.type}</span>
                </div>
                <div className={`px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-tighter ${isBusy ? 'bg-rose-50 text-rose-600' : 'bg-amber-50 text-amber-700'}`}>
                  {isBusy ? 'Occupied' : 'Free'}
                </div>
              </div>

              {isBusy ? (
                <div className="bg-rose-50/50 p-3 rounded-2xl border border-rose-100">
                  <div className="text-xs text-rose-400 font-bold mb-1">CURRENTLY BOOKED BY</div>
                  <div className="text-sm font-bold text-rose-700">{activeBooking.userName}</div>
                  <div className="text-xs text-rose-500 mt-1 italic">until {new Date(activeBooking.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</div>
                </div>
              ) : (
                <div className="bg-amber-50/50 p-3 rounded-2xl border border-amber-100">
                  <p className="text-xs text-amber-700 font-medium">Available for immediate booking.</p>
                </div>
              )}

              <button className="w-full mt-6 py-2 text-slate-500 hover:text-amber-700 text-xs font-bold transition-colors">
                View Weekly Schedule
              </button>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default ResourceAvailabilityView;
