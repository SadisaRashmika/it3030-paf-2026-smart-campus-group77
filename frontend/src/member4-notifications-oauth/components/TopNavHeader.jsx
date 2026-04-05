import { ChevronDown, LogOut, Menu, X } from "lucide-react";
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
		<header className="sticky top-0 z-30 border-b border-amber-200/70 bg-amber-50/90 backdrop-blur-xl">
			<div className="mx-auto flex h-20 w-full max-w-7xl items-center justify-between gap-3 px-4 sm:px-6">
				<div className="flex items-center gap-3 sm:gap-6">
					<div className="flex items-center gap-3">
						<img src="/assets/logoPAF.png" alt="SmartCampus logo" className="h-11 w-auto max-w-[120px] object-contain" />
						<div>
							<p className="font-display text-base font-extrabold text-slate-900 sm:text-lg">SmartCampus</p>
							<p className="text-[11px] font-bold uppercase tracking-[0.2em] text-amber-700/80">Uni Portal</p>
						</div>
					</div>

					<nav className="hidden items-center gap-2 xl:flex">
						{visibleTabs.map((tab) => {
							const active = activeTab === tab.key;
							return (
								<button
									key={tab.key}
									onClick={() => onTabClick(tab.key)}
									className={`rounded-xl border px-4 py-2.5 text-sm font-bold transition ${
										active
											? "border-amber-500 bg-gradient-to-b from-amber-400 to-amber-500 text-amber-950 shadow-[0_6px_14px_rgba(245,158,11,0.35)]"
											: "border-amber-200 bg-white/85 text-slate-800 hover:border-amber-300 hover:bg-amber-100/60"
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
									className="flex items-center gap-2 rounded-xl border border-amber-200 bg-white py-1.5 pl-1.5 pr-2.5 transition hover:bg-amber-50"
								>
									<span className="inline-flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-br from-amber-500 to-amber-300 text-xs font-bold text-amber-950">
										{initials}
									</span>
									<span className="text-left">
										<span className="block text-sm font-bold text-slate-900">{user.email}</span>
										<span className="block text-[11px] font-semibold uppercase tracking-wide text-slate-500">{roleLabel}</span>
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
								className="inline-flex h-10 w-10 items-center justify-center rounded-lg border border-amber-200 text-slate-700 transition hover:bg-amber-100 xl:hidden"
							>
								{mobileOpen ? <X size={18} /> : <Menu size={18} />}
							</button>
						</>
					) : (
						<div className="flex items-center gap-2">
							<button
								onClick={() => onLogin("login")}
								className="rounded-xl border border-amber-300 bg-white px-4 py-2.5 text-sm font-bold text-amber-800 transition hover:bg-amber-100"
							>
								Login
							</button>
							<button
								onClick={() => onLogin("activate")}
								className="rounded-xl bg-gradient-to-b from-amber-400 to-amber-500 px-4 py-2.5 text-sm font-bold text-amber-950 shadow-[0_6px_14px_rgba(245,158,11,0.35)] transition hover:brightness-105"
							>
								Activate
							</button>
						</div>
					)}
				</div>
			</div>

			{mobileOpen && user ? (
				<div className="border-t border-amber-200 bg-amber-50/90 px-4 py-3 xl:hidden">
					<div className="mb-2 rounded-xl bg-white px-3 py-2">
						<p className="text-sm font-bold text-slate-800">{user.email}</p>
						<p className="text-[11px] font-semibold uppercase tracking-wide text-slate-500">{roleLabel}</p>
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
									className={`w-full rounded-xl border px-3 py-2.5 text-left text-sm font-bold transition ${
										active
											? "border-amber-500 bg-amber-400 text-amber-950"
											: "border-amber-200 bg-white text-slate-700 hover:bg-amber-100"
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
