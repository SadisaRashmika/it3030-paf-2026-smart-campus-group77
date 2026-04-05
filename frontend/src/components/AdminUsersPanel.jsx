import { AlertTriangle, RefreshCcw, Users } from "lucide-react";

export default function AdminUsersPanel({ users, suspiciousUsers, loading, onReload }) {
  return (
    <section className="space-y-4">
      <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div>
            <p className="text-xs font-bold uppercase tracking-widest text-slate-500">Admin Controls</p>
            <h2 className="mt-1 text-2xl font-bold text-slate-900">User Management Snapshot</h2>
          </div>
          <button
            onClick={onReload}
            disabled={loading}
            className="inline-flex items-center gap-2 rounded-lg border border-blue-200 px-3 py-2 text-sm font-semibold text-blue-700 transition hover:bg-blue-50 disabled:opacity-60"
          >
            <RefreshCcw size={14} className={loading ? "animate-spin" : ""} /> Refresh
          </button>
        </div>

        <div className="mt-4 grid grid-cols-1 gap-3 sm:grid-cols-2">
          <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
            <p className="text-xs uppercase tracking-widest text-slate-500">Total Users</p>
            <p className="mt-1 text-3xl font-bold text-slate-900">{users.length}</p>
          </div>
          <div className="rounded-xl border border-rose-200 bg-rose-50 p-4">
            <p className="text-xs uppercase tracking-widest text-rose-600">Suspicious Accounts</p>
            <p className="mt-1 text-3xl font-bold text-rose-700">{suspiciousUsers.length}</p>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 gap-4 xl:grid-cols-2">
        <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <p className="mb-3 inline-flex items-center gap-2 text-sm font-bold text-slate-800">
            <Users size={16} /> All Accounts
          </p>
          <div className="space-y-2">
            {users.length === 0 ? (
              <p className="rounded-lg bg-slate-50 px-3 py-2 text-sm text-slate-600">No users returned by API.</p>
            ) : (
              users.map((user) => (
                <div key={user.id} className="rounded-lg border border-slate-200 bg-slate-50 px-3 py-2 text-xs">
                  <p className="font-bold text-slate-800">{user.userId} - {user.email}</p>
                  <p className="mt-1 text-slate-600">{user.role} | {user.status}</p>
                </div>
              ))
            )}
          </div>
        </article>

        <article className="rounded-2xl border border-rose-200 bg-white p-5 shadow-sm">
          <p className="mb-3 inline-flex items-center gap-2 text-sm font-bold text-rose-700">
            <AlertTriangle size={16} /> Suspicious Activity
          </p>
          <div className="space-y-2">
            {suspiciousUsers.length === 0 ? (
              <p className="rounded-lg bg-emerald-50 px-3 py-2 text-sm text-emerald-700">No suspicious accounts currently flagged.</p>
            ) : (
              suspiciousUsers.map((user) => (
                <div key={user.id} className="rounded-lg border border-rose-200 bg-rose-50 px-3 py-2 text-xs">
                  <p className="font-bold text-rose-800">{user.userId} - {user.email}</p>
                  <p className="mt-1 text-rose-700">OTP Requests: {user.otpRequestCount} | Failed OTP: {user.failedOtpAttempts}</p>
                </div>
              ))
            )}
          </div>
        </article>
      </div>
    </section>
  );
}
