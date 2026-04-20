import {
	BellRing,
	BookOpenText,
	CalendarClock,
	MessageSquareText,
	Sparkles,
	UserCheck
} from "lucide-react";

const advantages = [
	{
		title: "Make Work Easy",
		description: "Lecturers and students can handle day-to-day academic tasks quickly in one place.",
		icon: CalendarClock,
		iconClass: "text-blue-600 bg-blue-100"
	},
	{
		title: "Real-Time Notifications",
		description: "Instant updates for approvals, ticket changes, and important campus actions.",
		icon: BellRing,
		iconClass: "text-emerald-600 bg-emerald-100"
	},
	{
		title: "Ticket and Report System",
		description: "Create, track, and resolve incidents with clear status flow and communication.",
		icon: MessageSquareText,
		iconClass: "text-indigo-600 bg-indigo-100"
	},
	{
		title: "Easy Resource Booking",
		description: "Book rooms and assets with less effort while avoiding schedule conflicts.",
		icon: BookOpenText,
		iconClass: "text-fuchsia-600 bg-fuchsia-100"
	}
];

export default function PortalHomeContent({ user, onLogin, onNavigate }) {
	const role = (user?.role || "").replace("ROLE_", "").toLowerCase();
	const welcomeRoleLabel = role === "lecturer"
		? "Lecturer"
		: role === "admin"
			? "Admin"
			: role === "timetable_manager" || role === "timetablemanager"
				? "Timetable Manager"
				: role === "resource_administator" || role === "resourceadministator"
					? "Resource Administrator"
					: role === "ticket_administrator" || role === "ticketadministrator"
						? "Ticket Administrator"
					: role === "student"
						? "Student"
						: "User";
	const heroImage = !user
		? { src: "/assets/home.png", alt: "SmartCampus home illustration" }
		: role === "admin"
			? { src: "/assets/admin.png", alt: "Admin dashboard illustration" }
			: role === "lecturer"
				? { src: "/assets/lecturer.png", alt: "Lecturer dashboard illustration" }
				: role === "student"
					? { src: "/assets/student.png", alt: "Student dashboard illustration" }
					: null;

	return (
		<section className="space-y-6">
			<div className="glass relative overflow-hidden rounded-3xl border border-slate-200 p-7 shadow-glass sm:p-10">
				<div className="pointer-events-none absolute -right-20 -top-20 h-56 w-56 rounded-full bg-amber-100/70 blur-3xl" />
				<div className="pointer-events-none absolute -bottom-20 -left-20 h-56 w-56 rounded-full bg-orange-100/60 blur-3xl" />

				<div className="relative flex flex-col gap-6 md:flex-row md:items-center md:justify-between">
					<div className="max-w-3xl space-y-4">
						<span className="inline-flex items-center gap-2 rounded-full border border-amber-200 bg-amber-50 px-3 py-1 text-xs font-bold uppercase tracking-wide text-amber-700">
							<Sparkles size={13} /> University Support Platform
						</span>

						{user ? (
							<>
								<h1 className="font-display text-3xl font-bold leading-tight text-slate-900 sm:text-5xl">
									Welcome back, {welcomeRoleLabel}
								</h1>
								<p className="max-w-xl text-sm leading-relaxed text-slate-600 sm:text-base">
									Your personalized SmartCampus dashboard is ready. Open the modules below to continue your work.
								</p>
							</>
						) : (
							<>
								<h1 className="font-display text-4xl font-bold leading-tight text-slate-900 sm:text-6xl">
									One portal for students, lecturers, and admins.
								</h1>
								<p className="max-w-xl text-sm leading-relaxed text-slate-600 sm:text-base">
									Manage timetable, resources, internships, and academic support in one polished experience.
								</p>
							</>
						)}

						{user ? (
							<span className="inline-flex items-center gap-2 rounded-lg border border-amber-300 bg-amber-100 px-3 py-2 text-xs font-semibold text-amber-900">
								<UserCheck size={14} /> Logged in as {role}
							</span>
						) : (
							<p className="pt-1 text-sm font-medium text-slate-500">
								Use the login button in the header to sign in or activate your account.
							</p>
						)}
					</div>

					{heroImage ? (
						<div className="mx-auto w-full max-w-[260px] md:mx-0 md:w-[260px] lg:w-[300px]">
							<img
								src={heroImage.src}
								alt={heroImage.alt}
								className="h-auto w-full object-contain"
							/>
						</div>
					) : null}
				</div>
			</div>

			<div className="space-y-3">
				<div className="flex items-center justify-between">
					<h2 className="text-sm font-extrabold uppercase tracking-[0.14em] text-slate-700">Main Advantages</h2>
					<p className="text-xs font-medium text-slate-500">For lecturers and students</p>
				</div>

				<div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4">
					{advantages.map((item) => {
						const Icon = item.icon;

						return (
							<div
								key={item.title}
								className="rounded-2xl border border-slate-200 bg-white p-5 text-left shadow-sm transition hover:-translate-y-0.5 hover:border-blue-200 hover:shadow-md"
							>
								<span className={`mb-4 inline-flex h-10 w-10 items-center justify-center rounded-xl ${item.iconClass}`}>
									<Icon size={18} />
								</span>
								<h3 className="text-sm font-bold text-slate-900">{item.title}</h3>
								<p className="mt-2 text-xs leading-relaxed text-slate-600">{item.description}</p>
							</div>
						);
					})}
				</div>
			</div>
		</section>
	);
}
