import { ChevronDown, LogOut, Menu, Shield, X } from "lucide-react";
import { useEffect, useRef, useState } from "react";

const tabs = [
  { key: "home", label: "Home", public: true },
  { key: "timetable", label: "Timetable", public: false },
  { key: "resource", label: "Resource", public: false },
  { key: "jobs", label: "Jobs", public: false },
  { key: "ticket", label: "Ticket", public: false }
];

export default function TopNavHeader({ activeTab, onTabClick, user, onLogin, onLogout }) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);
  const userMenuRef = useRef(null);

  useEffect(() => {
    const onDown = (event) => {
      if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
        setMenuOpen(false);
      }
    };

    window.addEventListener("mousedown", onDown);
    return () => window.removeEventListener("mousedown", onDown);
  }, []);

  const visibleTabs = tabs.filter((tab) => user || tab.public);

  const roleLabel = user?.role?.replace("ROLE_", "") || "";
  const initials = user?.email ? user.email.slice(0, 2).toUpperCase() : "SC";

  return (
    <header className="sticky top-0 z-30 border-b border-slate-200/70 bg-white/80 backdrop-blur-xl">
      <div className="mx-auto flex h-16 w-full max-w-7xl items-center justify-between gap-3 px-4 sm:px-6">
        <div className="flex items-center gap-3 sm:gap-6">
          <div className="flex items-center gap-2">
            <span className="inline-flex h-9 w-9 items-center justify-center rounded-xl bg-gradient-to-br from-blue-600 to-cyan-500 text-white shadow-md">
              <Shield size={17} />
            </span>
            <div>
              <p className="font-display text-sm font-bold text-slate-900 sm:text-base">SmartCampus</p>
              <p className="text-[10px] font-semibold uppercase tracking-[0.2em] text-slate-500">Uni Portal</p>
            </div>
          </div>

          <nav className="hidden items-center gap-1 xl:flex">
            {visibleTabs.map((tab) => {
              const active = activeTab === tab.key;
              return (
                <button
                  key={tab.key}
                  onClick={() => onTabClick(tab.key)}
                  className={`rounded-lg px-3.5 py-2 text-sm font-semibold transition ${
                    active ? "bg-slate-900 text-white" : "text-slate-600 hover:bg-slate-100 hover:text-slate-900"
                  }`}
                >
                  {tab.label}
                </button>
              );
            })}
          </nav>
        </div>

        <div className="flex items-center gap-2">
          {user ? (
            <>
              <div ref={userMenuRef} className="relative hidden sm:block">
                <button
                  onClick={() => setMenuOpen((prev) => !prev)}
                  className="flex items-center gap-2 rounded-xl border border-slate-200 bg-slate-50 py-1.5 pl-1.5 pr-2.5 transition hover:bg-slate-100"
                >
                  <span className="inline-flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-br from-indigo-600 to-violet-500 text-xs font-bold text-white">
                    {initials}
                  </span>
                  <span className="text-left">
                    <span className="block text-xs font-bold text-slate-900">{user.email}</span>
                    <span className="block text-[10px] uppercase tracking-wide text-slate-500">{roleLabel}</span>
                  </span>
                  <ChevronDown size={14} className={`text-slate-500 transition ${menuOpen ? "rotate-180" : ""}`} />
                </button>
                {menuOpen ? (
                  <div className="absolute right-0 mt-2 w-40 overflow-hidden rounded-xl border border-slate-200 bg-white shadow-xl">
                    <button
                      onClick={() => {
                        setMenuOpen(false);
                        onLogout();
                      }}
                      className="flex w-full items-center gap-2 px-3 py-2.5 text-sm font-medium text-slate-700 transition hover:bg-rose-50 hover:text-rose-700"
                    >
                      <LogOut size={14} /> Sign out
                    </button>
                  </div>
                ) : null}
              </div>

              <button
                onClick={() => setMobileOpen((prev) => !prev)}
                className="inline-flex h-10 w-10 items-center justify-center rounded-lg border border-slate-200 text-slate-700 transition hover:bg-slate-100 xl:hidden"
              >
                {mobileOpen ? <X size={18} /> : <Menu size={18} />}
              </button>
            </>
          ) : (
            <div className="flex items-center gap-2">
              <button
                onClick={() => onLogin("login")}
                className="rounded-lg border border-blue-200 px-3.5 py-2 text-sm font-semibold text-blue-700 transition hover:bg-blue-50"
              >
                Login
              </button>
              <button
                onClick={() => onLogin("activate")}
                className="rounded-lg bg-slate-900 px-3.5 py-2 text-sm font-semibold text-white transition hover:bg-slate-800"
              >
                Activate
              </button>
            </div>
          )}
        </div>
      </div>

      {mobileOpen && user ? (
        <div className="border-t border-slate-200 bg-white px-4 py-3 xl:hidden">
          <div className="mb-2 rounded-xl bg-slate-50 px-3 py-2">
            <p className="text-xs font-bold text-slate-800">{user.email}</p>
            <p className="text-[10px] uppercase tracking-wide text-slate-500">{roleLabel}</p>
          </div>
          <div className="space-y-1">
            {visibleTabs.map((tab) => {
              const active = activeTab === tab.key;
              return (
                <button
                  key={`mobile-${tab.key}`}
                  onClick={() => {
                    onTabClick(tab.key);
                    setMobileOpen(false);
                  }}
                  className={`w-full rounded-lg px-3 py-2 text-left text-sm font-semibold transition ${
                    active ? "bg-slate-900 text-white" : "text-slate-700 hover:bg-slate-100"
                  }`}
                >
                  {tab.label}
                </button>
              );
            })}
            <button
              onClick={() => {
                onLogout();
                setMobileOpen(false);
              }}
              className="mt-2 flex w-full items-center justify-center gap-2 rounded-lg border border-rose-200 px-3 py-2 text-sm font-semibold text-rose-700 transition hover:bg-rose-50"
            >
              <LogOut size={14} /> Sign out
            </button>
          </div>
        </div>
      ) : null}
    </header>
  );
}
