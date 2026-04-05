import {
	ArrowRight,
	BookOpenText,
	Briefcase,
	CalendarClock,
	MessageSquareText,
	Sparkles,
	UserCheck
} from "lucide-react";

const modules = [
	{
		key: "timetable",
		title: "Timetable Management",
		description: "Plan and track classes, sessions, and schedule updates in one place.",
		icon: CalendarClock,
		iconClass: "text-blue-600 bg-blue-100"
	},
	{
		key: "resource",
		title: "Resource Management",
		description: "Share lecture materials, upload notes, and keep learning organized.",
		icon: BookOpenText,
		iconClass: "text-emerald-600 bg-emerald-100"
	},
	{
		key: "jobs",
		title: "Job and Intern Finder",
		description: "Discover role-matched internships and academic opportunities.",
		icon: Briefcase,
		iconClass: "text-amber-600 bg-amber-100"
	},
	{
		key: "ticket",
		title: "Academic Ticket with Chat",
		description: "Raise support tickets and chat directly between lecturers and students.",
		icon: MessageSquareText,
		iconClass: "text-fuchsia-600 bg-fuchsia-100"
	}
];

export default function PortalHomeContent({ user, onLogin, onNavigate }) {
	const role = (user?.role || "").replace("ROLE_", "").toLowerCase();

	const handleModuleClick = (tabKey) => {
		if (user) {
			onNavigate(tabKey);
			return;
		}

		onLogin("login");
	};

	return (
		<section className="space-y-6">
			<div className="glass relative overflow-hidden rounded-3xl border border-slate-200 p-7 shadow-glass sm:p-10">
				<div className="pointer-events-none absolute -right-20 -top-20 h-56 w-56 rounded-full bg-blue-100/70 blur-3xl" />
				<div className="pointer-events-none absolute -bottom-20 -left-20 h-56 w-56 rounded-full bg-emerald-100/60 blur-3xl" />

				<div className="relative max-w-3xl space-y-4">
					<span className="inline-flex items-center gap-2 rounded-full border border-blue-200 bg-blue-50 px-3 py-1 text-xs font-bold uppercase tracking-wide text-blue-700">
						<Sparkles size={13} /> University Support Platform
					</span>

					{user ? (
						<>
							<h1 className="font-display text-3xl font-bold leading-tight text-slate-900 sm:text-5xl">
								Welcome back, {role === "lecturer" ? "Lecturer" : role === "admin" ? "Admin" : "Student"}
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
						<span className="inline-flex items-center gap-2 rounded-lg bg-slate-900 px-3 py-2 text-xs font-semibold text-white">
							<UserCheck size={14} /> Logged in as {role}
						</span>
					) : (
						<p className="pt-1 text-sm font-medium text-slate-500">
							Use the login button in the header to sign in or activate your account.
						</p>
					)}
				</div>
			</div>

			<div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4">
				{modules.map((module) => {
					const Icon = module.icon;

					return (
						<button
							key={module.key}
							onClick={() => handleModuleClick(module.key)}
							className="group rounded-2xl border border-slate-200 bg-white p-5 text-left shadow-sm transition hover:-translate-y-0.5 hover:border-blue-200 hover:shadow-md"
						>
							<span className={`mb-4 inline-flex h-10 w-10 items-center justify-center rounded-xl ${module.iconClass}`}>
								<Icon size={18} />
							</span>
							<h3 className="text-sm font-bold text-slate-900">{module.title}</h3>
							<p className="mt-2 text-xs leading-relaxed text-slate-600">{module.description}</p>
							<span className="mt-4 inline-flex items-center gap-1 text-xs font-semibold text-blue-700">
								Open module <ArrowRight size={12} className="transition group-hover:translate-x-0.5" />
							</span>
						</button>
					);
				})}
			</div>
		</section>
	);
}
