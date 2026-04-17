import React, { useState, useEffect } from "react";
import { BookOpenText, Calendar, CheckCircle2, Clock, MapPin, Plus, RefreshCw, XCircle, UserPlus } from "lucide-react";
import { getAllResources, createBooking, getMyBookings, getAllBookings, joinSession } from "../../services/bookingService";

const STATUS_COLORS = {
  PENDING: "bg-amber-100 text-amber-700 border-amber-200",
  APPROVED: "bg-emerald-100 text-emerald-700 border-emerald-200",
  REJECTED: "bg-rose-100 text-rose-700 border-rose-200",
  CANCELLED: "bg-slate-100 text-slate-700 border-slate-200",
};

export default function ResourcePanel({ user }) {
  const [resources, setResources] = useState([]);
  const [allBookings, setAllBookings] = useState([]);
  const [myBookings, setMyBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [isProcessing, setIsProcessing] = useState(false);
  const [selectedResource, setSelectedResource] = useState(null);
  const [bookingForm, setBookingForm] = useState({
    startTime: "",
    endTime: "",
  });

  const roleName = user?.role?.replace("ROLE_", "") || "STUDENT";
  const canSeeCatalog = roleName === "LECTURER" || roleName === "TIMETABLE_MANAGER";
  const canBook = roleName === "LECTURER";

  const fetchData = async () => {
    try {
      setLoading(true);
      const [resData, myData, allData] = await Promise.all([
        getAllResources(),
        roleName === "LECTURER" ? getMyBookings() : Promise.resolve([]),
        getAllBookings().catch(() => []) // Some roles might not have access to all bookings
      ]);
      setResources(resData);
      setMyBookings(myData);
      setAllBookings(allData);
      setError("");
    } catch (err) {
      setError("Failed to sync campus resources.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [user]);

  const handleBook = async (e) => {
    e.preventDefault();
    if (!selectedResource) return;

    try {
      setIsProcessing(true);
      await createBooking({
        resourceId: selectedResource.id,
        startTime: new Date(bookingForm.startTime).toISOString(),
        endTime: new Date(bookingForm.endTime).toISOString(),
      });
      setSelectedResource(null);
      setBookingForm({ startTime: "", endTime: "" });
      fetchData();
    } catch (err) {
      alert(err.message || "Failed to create request.");
    } finally {
      setIsProcessing(false);
    }
  };

  const handleJoin = async (id) => {
    try {
      setIsProcessing(true);
      await joinSession(id);
      alert("Successfully registered for the session!");
      fetchData();
    } catch (err) {
      alert(err.message || "Failed to join session. Capacity might be full.");
    } finally {
      setIsProcessing(false);
    }
  };

  if (loading) {
    return (
      <div className="flex h-64 flex-col items-center justify-center space-y-4">
        <RefreshCw className="h-10 w-10 animate-spin text-blue-600" />
      </div>
    );
  }

  // Approved sessions for students to join
  const upcomingSessions = allBookings.filter(b => b.status === "APPROVED");

  return (
    <div className="space-y-10 animate-in fade-in slide-in-from-bottom-4 duration-700">
      {/* Welcome Header */}
      <div>
        <h2 className="text-3xl font-extrabold tracking-tight text-slate-900">
          {roleName === "LECTURER" ? "Session Planner" : "Campus Schedule"}
        </h2>
        <p className="mt-2 text-slate-600 font-medium">
          {canSeeCatalog 
            ? (canBook ? "Manage facility requests for your upcoming lectures and events." : "View the complete facility catalog and monitor their operational status.")
            : "View approved sessions and book your seat in lecture halls and labs."}
        </p>
      </div>

      {/* Main Grid: Resources (Lecturer) or Sessions (Student) */}
      <div className="grid gap-8 lg:grid-cols-12">
        
        {/* Resource Catalog (Fixed side for Lecturers, hidden/small for students) */}
        <div className={canSeeCatalog ? "lg:col-span-8 space-y-4" : "lg:col-span-7 space-y-4"}>
           <div className="flex items-center gap-2 text-sm font-bold uppercase tracking-widest text-slate-400">
             <MapPin size={16} /> {canSeeCatalog ? "Facility Catalog" : "Upcoming Sessions"}
           </div>
           
           {canSeeCatalog ? (
             <div className="grid gap-4 sm:grid-cols-2">
               {resources.map(res => (
                 <div key={res.id} className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm hover:border-blue-300 transition-all">
                   <div className="flex justify-between items-start">
                     <div>
                       <h4 className="font-bold text-slate-900">{res.name}</h4>
                       <p className="text-xs text-slate-400 font-bold uppercase">{res.type} • cap. {res.capacity}</p>
                     </div>
                     <span className={`px-2 py-1 rounded-lg text-[10px] font-black uppercase tracking-tighter border ${res.available ? 'bg-emerald-50 text-emerald-600 border-emerald-100' : 'bg-slate-50 text-slate-400 border-slate-100'}`}>
                       {res.available ? 'Ready' : 'In Use'}
                     </span>
                   </div>
                   {canBook && (
                     <button 
                       disabled={!res.available}
                       onClick={() => setSelectedResource(res)}
                       className="mt-4 w-full rounded-xl bg-slate-900 py-2 text-xs font-bold text-white hover:bg-blue-600 transition-all active:scale-[0.98] disabled:opacity-30"
                     >
                       Book Facility
                     </button>
                   )}
                 </div>
               ))}
             </div>
           ) : (
             <div className="space-y-4">
               {upcomingSessions.length === 0 ? (
                 <div className="p-12 border-2 border-dashed rounded-3xl text-center text-slate-400 font-medium">
                   No approved sessions available for registration yet.
                 </div>
               ) : (
                 upcomingSessions.map(session => (
                   <div key={session.id} className="flex flex-col sm:flex-row items-center justify-between rounded-2xl border border-slate-200 bg-white p-6 shadow-sm hover:shadow-md transition-all">
                     <div className="flex items-start gap-4">
                       <div className="rounded-xl bg-blue-50 p-3 text-blue-600 mt-1">
                         <Calendar size={20} />
                       </div>
                       <div>
                         <h4 className="text-lg font-bold text-slate-900">{session.resource.name}</h4>
                         <p className="text-sm text-slate-500 font-medium italic">Lecturer: {session.user.email}</p>
                         <div className="mt-1 flex gap-4 text-xs font-bold text-slate-400 uppercase tracking-tighter">
                            <span className="flex items-center gap-1"><Clock size={12} /> {new Date(session.startTime).toLocaleString()}</span>
                         </div>
                       </div>
                     </div>
                     <button 
                        onClick={() => handleJoin(session.id)}
                        className="mt-4 sm:mt-0 flex items-center gap-2 rounded-xl bg-blue-600 px-6 py-2.5 text-sm font-bold text-white shadow-lg shadow-blue-100 hover:bg-blue-700 active:scale-95 transition-all"
                     >
                       <UserPlus size={16} /> Join Session
                     </button>
                   </div>
                 ))
               )}
             </div>
           )}
        </div>

        {/* Status / History Panel */}
        <div className={canSeeCatalog ? "lg:col-span-4" : "lg:col-span-5"}>
           <div className="flex items-center gap-2 text-sm font-bold uppercase tracking-widest text-slate-400 mb-4">
             <Calendar size={16} /> {canBook ? "My Requests" : "Role Info"}
           </div>
           
           {canBook ? (
             <div className="space-y-4">
                {myBookings.map(b => (
                  <div key={b.id} className="rounded-2xl bg-white border border-slate-100 p-4 shadow-sm">
                    <div className="flex justify-between items-center mb-1">
                      <span className="text-sm font-bold text-slate-800 truncate pr-2">{b.resource.name}</span>
                      <span className={`px-2 py-0.5 rounded-md text-[9px] font-black uppercase tracking-tighter ${STATUS_COLORS[b.status] || STATUS_COLORS.PENDING}`}>
                        {b.status}
                      </span>
                    </div>
                    <p className="text-[10px] text-slate-400 font-mono tracking-tighter">
                      {new Date(b.startTime).toLocaleString()}
                    </p>
                  </div>
                ))}
                {myBookings.length === 0 && <p className="text-sm text-slate-400 italic">No recent requests.</p>}
             </div>
           ) : (
             <div className="rounded-3xl bg-slate-100 p-8">
               <div className="h-16 w-16 bg-white rounded-2xl flex items-center justify-center text-blue-600 shadow-sm mb-6">
                 <BookOpenText size={32} />
               </div>
               <h3 className="text-xl font-bold text-slate-900 mb-2">{roleName === "TIMETABLE_MANAGER" ? "Manager Access" : "Student Access"}</h3>
               <p className="text-sm text-slate-600 leading-relaxed">
                 {roleName === "TIMETABLE_MANAGER" 
                   ? "You are viewing the read-only catalog. To view and approve pending bookings, navigate to the Timetable tab." 
                   : "As a student, you can view all approved sessions created by lecturers. Use the 'Join Session' button to register your attendance and secure your spot."}
               </p>
               <div className="mt-8 space-y-3">
                 <div className="flex items-center gap-3 text-xs font-bold text-slate-500 uppercase tracking-widest">
                   <div className="h-1.5 w-1.5 rounded-full bg-blue-600" /> {roleName === "TIMETABLE_MANAGER" ? "Review Catalog" : "Mark Attendance"}
                 </div>
                 <div className="flex items-center gap-3 text-xs font-bold text-slate-500 uppercase tracking-widest">
                   <div className="h-1.5 w-1.5 rounded-full bg-blue-600" /> {roleName === "TIMETABLE_MANAGER" ? "Approve Requests (Timetable)" : "View Session Details"}
                 </div>
               </div>
             </div>
           )}
        </div>
      </div>

      {/* Booking Modal (Lecturer only) */}
      {selectedResource && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm animate-in fade-in duration-300">
          <div className="w-full max-w-md overflow-hidden rounded-3xl bg-white shadow-2xl animate-in zoom-in-95 duration-300">
            <div className="bg-blue-600 p-8 text-white">
              <div className="flex items-center justify-between">
                <h3 className="text-2xl font-bold">Booking Request</h3>
                <button onClick={() => setSelectedResource(null)} className="rounded-xl bg-white/10 p-2 hover:bg-white/20">
                  <XCircle size={20} />
                </button>
              </div>
              <p className="mt-2 text-blue-100 flex items-center gap-2"><MapPin size={16}/> {selectedResource.name}</p>
            </div>
            <form onSubmit={handleBook} className="p-8 space-y-6">
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-bold text-slate-700 mb-1.5 uppercase tracking-wide">Session Start</label>
                  <input
                    required
                    type="datetime-local"
                    value={bookingForm.startTime}
                    onChange={(e) => setBookingForm({ ...bookingForm, startTime: e.target.value })}
                    className="w-full rounded-xl border-slate-200 bg-slate-50 px-4 py-3 text-sm focus:border-blue-500 transition-all outline-none border"
                  />
                </div>
                <div>
                  <label className="block text-sm font-bold text-slate-700 mb-1.5 uppercase tracking-wide">Session End</label>
                  <input
                    required
                    type="datetime-local"
                    value={bookingForm.endTime}
                    onChange={(e) => setBookingForm({ ...bookingForm, endTime: e.target.value })}
                    className="w-full rounded-xl border-slate-200 bg-slate-50 px-4 py-3 text-sm focus:border-blue-500 transition-all outline-none border"
                  />
                </div>
              </div>
              <button
                type="submit"
                disabled={isProcessing}
                className="w-full rounded-xl bg-blue-600 py-4 text-sm font-bold text-white shadow-xl shadow-blue-100 hover:bg-blue-700 transition-all active:scale-[0.98] disabled:opacity-50"
              >
                {isProcessing ? 'Submitting...' : 'Send to Timetable Manager'}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
