import { Bell, Camera, ChevronDown, LogOut, Menu, ShieldAlert, UserCircle2, X } from "lucide-react";
import { useEffect, useRef, useState } from "react";

const tabs = [
	{ key: "home", label: "Home", public: true },
	{ key: "timetable", label: "Timetable", public: false },
	{ key: "resource", label: "Resource", public: false },
	{ key: "jobs", label: "Jobs", public: false },
	{ key: "ticket", label: "Ticket", public: false }
];

export default function TopNavHeader({
	activeTab,
	onTabClick,
	user,
	onLogin,
	onLogout,
	notifications = [],
	loadingNotifications = false,
	onOpenNotifications,
	onMarkNotificationRead,
	onMarkAllNotificationsRead,
	onReportSuspicious,
	onOpenProfile,
	lastSeenNotificationAt = "",
	reportingSuspicious = false
}) {
	const [mobileOpen, setMobileOpen] = useState(false);
	const [menuOpen, setMenuOpen] = useState(false);
	const [notificationsOpen, setNotificationsOpen] = useState(false);
	const userMenuRef = useRef(null);
	const notificationsRef = useRef(null);

	const formatNotificationTime = (value) => {
		if (!value) {
			return "";
		}
		try {
			return new Date(value).toLocaleString();
		} catch {
			return "";
		}
	};

	const isLoginAlert = (message) => String(message || "").toLowerCase().includes("logged in successfully");

	const isSeenBeforeLastRefresh = (item) => {
		if (!lastSeenNotificationAt || !item?.createdAt) {
			return false;
		}

		const createdAt = new Date(item.createdAt).getTime();
		const lastSeenAt = new Date(lastSeenNotificationAt).getTime();
		if (Number.isNaN(createdAt) || Number.isNaN(lastSeenAt)) {
			return false;
		}

		return createdAt <= lastSeenAt;
	};

	useEffect(() => {
		const onDown = (event) => {
			if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
				setMenuOpen(false);
			}
			if (notificationsRef.current && !notificationsRef.current.contains(event.target)) {
				setNotificationsOpen(false);
			}
		};

		window.addEventListener("mousedown", onDown);
		return () => window.removeEventListener("mousedown", onDown);
	}, []);

	const visibleTabs = tabs.filter((tab) => user || tab.public);

	const roleLabel = user?.role?.replace("ROLE_", "") || "";
	const roleKey = roleLabel.toLowerCase();
	const canSeeNotifications = Boolean(user);
	const canReportSuspicious = roleKey === "student" || roleKey === "lecturer";
	const unreadCount = notifications.filter((item) => item?.read === false && !isSeenBeforeLastRefresh(item)).length;
	const displayName = user?.name?.trim() || user?.userId || "SmartCampus User";
	const tabLabelForRole = (tab) => {
		if (roleKey === "admin") {
			if (tab.key === "timetable") {
				return "Activity";
			}
			if (tab.key === "resource") {
				return "User Management";
			}
			if (tab.key === "jobs") {
				return "Role Management";
			}
			return tab.label;
		}

		if (roleKey === "student") {
			if (tab.key === "home") {
				return "Home";
			}
			if (tab.key === "timetable") {
				return "Member 1";
			}
			if (tab.key === "resource") {
				return "Member 2";
			}
			if (tab.key === "jobs") {
				return "Member 3";
			}
			if (tab.key === "ticket") {
				return "Member 4";
			}
		}

		return tab.label;
	};
	const initials = displayName
		.split(" ")
		.filter(Boolean)
		.slice(0, 2)
		.map((part) => part[0]?.toUpperCase() || "")
		.join("") || "SC";
	const profilePhotoUrl = user?.profilePictureDataUrl?.trim() || "";

	return (
		<header className="sticky top-0 z-30 border-b border-slate-200 bg-white/95 backdrop-blur-xl">
			<div className="mx-auto flex h-20 w-full max-w-7xl items-center justify-between gap-3 px-4 sm:px-6">
				<div className="flex items-center gap-3 sm:gap-6">
					<div className="flex items-center gap-3">
						<img src="/assets/logoPAF.png" alt="SmartCampus logo" className="h-11 w-auto max-w-[120px] object-contain" />
						<div>
							<p className="font-display text-base font-extrabold text-slate-900 sm:text-lg">SmartCampus</p>
							<p className="text-[11px] font-bold uppercase tracking-[0.2em] text-slate-500">Uni Portal</p>
						</div>
					</div>

					<nav className="hidden items-center gap-2 xl:flex">
						{visibleTabs.map((tab) => {
							const active = activeTab === tab.key;
							const tabLabel = tabLabelForRole(tab);
							return (
								<button
									key={tab.key}
									onClick={() => onTabClick(tab.key)}
									className={`rounded-xl border px-4 py-2.5 text-sm font-bold transition ${
										active
											? "border-amber-500 bg-gradient-to-b from-amber-400 to-amber-500 text-amber-950 shadow-[0_6px_14px_rgba(245,158,11,0.35)]"
											: "border-slate-200 bg-white text-slate-800 hover:border-amber-300 hover:bg-amber-50"
									}`}
								>
									{tabLabel}
								</button>
							);
						})}
					</nav>
				</div>

				<div className="flex items-center gap-2">
					{user ? (
						<>
							{canSeeNotifications ? (
								<div ref={notificationsRef} className="relative hidden sm:block">
									<button
										type="button"
										onClick={() => {
											setNotificationsOpen((prev) => {
												const next = !prev;
												if (next) {
													onOpenNotifications?.();
												}
												return next;
											});
										}}
										title="Notifications"
										className="relative inline-flex h-10 w-10 items-center justify-center rounded-lg border border-slate-200 text-slate-700 transition hover:bg-slate-100"
									>
										<Bell size={16} />
										{unreadCount > 0 ? (
											<span className="absolute -right-1 -top-1 inline-flex min-h-5 min-w-5 items-center justify-center rounded-full bg-rose-500 px-1 text-[10px] font-bold text-white">
												{unreadCount > 99 ? "99+" : unreadCount}
											</span>
										) : null}
									</button>

									{notificationsOpen ? (
										<div className="absolute right-0 mt-2 w-[360px] overflow-hidden rounded-xl border border-slate-200 bg-white shadow-xl">
											<div className="flex items-center justify-between border-b border-slate-100 px-3 py-2.5">
												<p className="text-sm font-bold text-slate-800">Notifications</p>
												<button
													type="button"
													onClick={() => onMarkAllNotificationsRead?.()}
													className="text-xs font-semibold text-slate-500 transition hover:text-slate-700"
												>
													Mark all read
												</button>
											</div>

											<div className="max-h-96 overflow-y-auto">
												{loadingNotifications ? (
													<p className="px-3 py-3 text-sm text-slate-500">Loading notifications...</p>
												) : notifications.length === 0 ? (
													<p className="px-3 py-3 text-sm text-slate-500">No notifications yet.</p>
												) : (
													notifications.map((item) => {
														const loginAlert = isLoginAlert(item.message);
														const itemRead = item?.read === true || isSeenBeforeLastRefresh(item);
														return (
															<div key={item.id} className={`border-b border-slate-100 px-3 py-2.5 ${itemRead ? "bg-white" : "bg-amber-50/50"}`}>
																<button
																	type="button"
																	onClick={() => onMarkNotificationRead?.(item.id)}
																	className="w-full text-left"
																>
																	<p className={`text-sm ${itemRead ? "text-slate-700" : "font-semibold text-slate-900"}`}>{item.message}</p>
																</button>

																{item.createdAt || loginAlert ? (
																	<div className="mt-1 flex items-center justify-between gap-2">
																		{item.createdAt ? <p className="text-[11px] text-slate-500">{formatNotificationTime(item.createdAt)}</p> : <span />}
																		{loginAlert ? (
																			<button
																				type="button"
																				onClick={() => onReportSuspicious?.()}
																				disabled={reportingSuspicious}
																				className="inline-flex items-center gap-1 rounded-md border border-rose-200 px-2.5 py-1 text-xs font-semibold text-rose-700 transition hover:bg-rose-50 disabled:cursor-not-allowed disabled:opacity-60"
																			>
																				<ShieldAlert size={12} /> {reportingSuspicious ? "Reporting..." : "Not you? Report"}
																			</button>
																		) : null}
																	</div>
																) : null}
															</div>
														);
													})
												)}
											</div>
										</div>
									) : null}
								</div>
							) : null}
							<div ref={userMenuRef} className="relative hidden sm:block">
								<button
									onClick={() => setMenuOpen((prev) => !prev)}
									className="flex items-center gap-2 rounded-xl border border-slate-200 bg-white py-1.5 pl-1.5 pr-2.5 transition hover:bg-slate-50"
								>
									<span className="inline-flex h-9 w-9 items-center justify-center overflow-hidden rounded-full bg-gradient-to-br from-amber-500 to-amber-300 text-xs font-bold text-amber-950">
										{profilePhotoUrl ? (
											<img src={profilePhotoUrl} alt={`${displayName} profile`} className="h-full w-full object-cover" />
										) : (
											initials
										)}
									</span>
									<span className="text-left">
										<span className="block text-sm font-bold text-slate-900">{displayName}</span>
										<span className="block text-[11px] font-semibold uppercase tracking-wide text-slate-500">{roleLabel}</span>
									</span>
									<ChevronDown size={14} className={`text-slate-500 transition ${menuOpen ? "rotate-180" : ""}`} />
								</button>
								{menuOpen ? (
									<div className="absolute right-0 mt-2 w-40 overflow-hidden rounded-xl border border-slate-200 bg-white shadow-xl">
										<button
											onClick={() => {
											setMenuOpen(false);
											onOpenProfile?.();
										}}
											className="flex w-full items-center gap-2 px-3 py-2.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50 hover:text-slate-900"
										>
											<Camera size={14} /> My Profile
										</button>
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
								onClick={() => onLogin("activate")}
								className="rounded-xl border border-slate-300 bg-white px-4 py-2.5 text-sm font-bold text-slate-700 transition hover:border-slate-400 hover:bg-slate-50"
							>
								Activate
							</button>
							<button
								onClick={() => onLogin("login")}
								className="rounded-xl bg-gradient-to-b from-amber-400 to-amber-500 px-4 py-2.5 text-sm font-bold text-amber-950 shadow-[0_6px_14px_rgba(245,158,11,0.35)] transition hover:brightness-105"
							>
								Login
							</button>
						</div>
					)}
				</div>
			</div>

			{mobileOpen && user ? (
				<div className="border-t border-slate-200 bg-white px-4 py-3 xl:hidden">
					<div className="mb-2 rounded-xl bg-white px-3 py-2">
						<p className="text-sm font-bold text-slate-800">{displayName}</p>
						<p className="text-[11px] font-semibold uppercase tracking-wide text-slate-500">{roleLabel}</p>
					</div>
					<button
						onClick={() => {
							setMobileOpen(false);
							onOpenProfile?.();
						}}
						className="mb-2 flex w-full items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2.5 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
					>
						<UserCircle2 size={14} /> My Profile
					</button>
					<div className="space-y-1">
						{canSeeNotifications ? (
							<div className="mb-2 rounded-xl border border-slate-200 bg-slate-50 p-2.5">
								<div className="mb-2 flex items-center justify-between">
									<p className="text-xs font-bold uppercase tracking-wide text-slate-600">Notifications</p>
									<button
										type="button"
										onClick={() => onMarkAllNotificationsRead?.()}
										className="text-[11px] font-semibold text-slate-500"
									>
										Mark all read
									</button>
								</div>
								<div className="max-h-48 space-y-1 overflow-y-auto">
									{notifications.length === 0 ? (
										<p className="rounded-lg bg-white px-2 py-2 text-xs text-slate-500">No notifications yet.</p>
									) : (
										notifications.slice(0, 8).map((item) => (
											<div key={`mobile-notice-${item.id}`} className={`rounded-lg bg-white px-2 py-2 text-xs ${item?.read === true || isSeenBeforeLastRefresh(item) ? "text-slate-600" : "font-semibold text-slate-800"}`}>
												<button type="button" onClick={() => onMarkNotificationRead?.(item.id)} className="w-full text-left">
													<p>{item.message}</p>
												</button>
												{item.createdAt || isLoginAlert(item.message) ? (
													<div className="mt-1 flex items-center justify-between gap-2">
														{item.createdAt ? <p className="text-[10px] text-slate-500">{formatNotificationTime(item.createdAt)}</p> : <span />}
														{isLoginAlert(item.message) ? (
															<button
																type="button"
																onClick={() => onReportSuspicious?.()}
																disabled={reportingSuspicious}
																className="inline-flex items-center gap-1 rounded-md border border-rose-200 px-2 py-1 text-[10px] font-semibold text-rose-700"
															>
																<ShieldAlert size={10} /> {reportingSuspicious ? "Reporting..." : "Not you? Report"}
															</button>
														) : null}
													</div>
												) : null}
											</div>
										))
									)}
								</div>
							</div>
						) : null}
						{visibleTabs.map((tab) => {
							const active = activeTab === tab.key;
							const tabLabel = tabLabelForRole(tab);
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
											: "border-slate-200 bg-white text-slate-700 hover:bg-amber-50"
									}`}
								>
									{tabLabel}
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
