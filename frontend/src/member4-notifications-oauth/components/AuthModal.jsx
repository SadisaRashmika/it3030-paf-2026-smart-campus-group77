import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
	AlertCircle,
	CheckCircle2,
	Eye,
	EyeOff,
	Globe,
	Hash,
	Lock,
	Mail,
	ShieldCheck,
	X
} from "lucide-react";
import {
	createLoginAlert,
	login,
	requestActivationOtp,
	requestForgotPasswordOtp,
	resetForgotPassword,
	startGoogleLogin,
	verifyActivationOtp
} from "../../services/authService";

const MODES = {
	LOGIN: "login",
	ACTIVATE: "activate",
	FORGOT: "forgot"
};

const initialForms = {
	login: { identifier: "", password: "" },
	activateRequest: { userId: "", email: "" },
	activateVerify: { otp: "", newPassword: "" },
	forgotRequest: { email: "" },
	forgotVerify: { otp: "", newPassword: "" }
};

const createInitialForms = () => ({
	login: { ...initialForms.login },
	activateRequest: { ...initialForms.activateRequest },
	activateVerify: { ...initialForms.activateVerify },
	forgotRequest: { ...initialForms.forgotRequest },
	forgotVerify: { ...initialForms.forgotVerify }
});

export default function AuthModal({ isOpen, onClose, onAuthenticated, initialMode = MODES.LOGIN }) {
	const [mode, setMode] = useState(initialMode);
	const [forms, setForms] = useState(createInitialForms());
	const [activationStep, setActivationStep] = useState(1);
	const [forgotStep, setForgotStep] = useState(1);
	const [loading, setLoading] = useState(false);
	const [error, setError] = useState("");
	const [message, setMessage] = useState("");
	const modalRef = useRef(null);
	const navigate = useNavigate();

	useEffect(() => {
		if (!isOpen) {
			setMode(MODES.LOGIN);
			setForms(createInitialForms());
			setActivationStep(1);
			setForgotStep(1);
			setLoading(false);
			setError("");
			setMessage("");
			return;
		}

		setMode(initialMode);
		setForms(createInitialForms());
		setActivationStep(1);
		setForgotStep(1);
		setLoading(false);
		setError("");
		setMessage("");
	}, [initialMode, isOpen]);

	useEffect(() => {
		if (!isOpen) {
			return;
		}

		const onKeyDown = (event) => {
			if (event.key === "Escape") {
				onClose();
			}
		};

		const onPointerDown = (event) => {
			if (modalRef.current && !modalRef.current.contains(event.target)) {
				onClose();
			}
		};

		window.addEventListener("keydown", onKeyDown);
		document.addEventListener("pointerdown", onPointerDown, true);
		return () => {
			window.removeEventListener("keydown", onKeyDown);
			document.removeEventListener("pointerdown", onPointerDown, true);
		};
	}, [isOpen, onClose]);

	if (!isOpen) {
		return null;
	}

	const updateForm = (key, field, value) => {
		setForms((prev) => ({
			...prev,
			[key]: {
				...prev[key],
				[field]: value
			}
		}));
	};

	const resetFeedback = () => {
		setError("");
		setMessage("");
	};

	const switchMode = (nextMode) => {
		setMode(nextMode);
		setActivationStep(1);
		setForgotStep(1);
		resetFeedback();
	};

	const onLogin = async (event) => {
		event.preventDefault();
		resetFeedback();

		if (!forms.login.identifier.trim()) {
			setError("Enter email or user ID.");
			return;
		}

		const identifier = forms.login.identifier.trim();
		const isEmailIdentifier = identifier.includes("@");
		const loginPayload = {
			identifier,
			email: isEmailIdentifier ? identifier : "",
			userId: isEmailIdentifier ? "" : identifier,
			password: forms.login.password
		};

		setLoading(true);
		try {
			const user = await login(loginPayload);
			await createLoginAlert("Password Login").catch(() => null);
			onAuthenticated(user);
			onClose();
		} catch (err) {
			setError(err.message);
		} finally {
			setLoading(false);
		}
	};

	const onNeedHelp = () => {
		onClose();
		navigate("/help");
	};

	const onRequestActivationOtp = async (event) => {
		event.preventDefault();
		resetFeedback();
		setLoading(true);
		try {
			await requestActivationOtp(forms.activateRequest);
			setActivationStep(2);
			setMessage("OTP sent to your email. Enter it below to activate your account.");
		} catch (err) {
			setError(err.message);
		} finally {
			setLoading(false);
		}
	};

	const onVerifyActivationOtp = async (event) => {
		event.preventDefault();
		resetFeedback();
		setLoading(true);
		try {
			await verifyActivationOtp({ ...forms.activateRequest, ...forms.activateVerify });
			setMessage("Account activated. Please sign in.");
			setMode(MODES.LOGIN);
			setActivationStep(1);
			setForms((prev) => ({
				...prev,
				login: { email: forms.activateRequest.email, password: "" },
				activateRequest: { ...initialForms.activateRequest },
				activateVerify: { ...initialForms.activateVerify }
			}));
		} catch (err) {
			setError(err.message);
		} finally {
			setLoading(false);
		}
	};

	const onRequestForgotPasswordOtp = async (event) => {
		event.preventDefault();
		resetFeedback();
		setLoading(true);
		try {
			await requestForgotPasswordOtp(forms.forgotRequest);
			setForgotStep(2);
			setMessage("OTP sent to your email. Enter it below to reset your password.");
		} catch (err) {
			setError(err.message);
		} finally {
			setLoading(false);
		}
	};

	const onResetForgotPassword = async (event) => {
		event.preventDefault();
		resetFeedback();
		setLoading(true);
		try {
			await resetForgotPassword({ email: forms.forgotRequest.email, ...forms.forgotVerify });
			setMessage("Password reset successful. Please sign in.");
			setMode(MODES.LOGIN);
			setForgotStep(1);
			setForms((prev) => ({
				...prev,
				login: { identifier: forms.forgotRequest.email, password: "" },
				forgotRequest: { ...initialForms.forgotRequest },
				forgotVerify: { ...initialForms.forgotVerify }
			}));
		} catch (err) {
			setError(err.message);
		} finally {
			setLoading(false);
		}
	};

	const modeMeta = {
		login: {
			title: "Welcome back",
			subtitle: "Sign in to SmartCampus"
		},
		activate: {
			title: "Activate your account",
			subtitle: "Verify your ID and set your password"
		},
		forgot: {
			title: "Reset your password",
			subtitle: "Verify your email and create a new password"
		}
	};

	return (
		<div
			className="fixed inset-0 z-50 flex items-center justify-center p-4"
			style={{ background: "rgba(2,6,23,0.55)", backdropFilter: "blur(7px)" }}
		>
			<div
				ref={modalRef}
				className="relative w-full max-w-md overflow-hidden rounded-3xl bg-white shadow-glass ring-1 ring-slate-200"
			>
				<div className="flex justify-end px-4 pt-4">
					<button
						type="button"
						onClick={onClose}
						onMouseDown={(event) => event.stopPropagation()}
						className="inline-flex h-9 w-9 items-center justify-center rounded-full text-slate-500 transition hover:bg-slate-100 hover:text-slate-700"
						aria-label="Close login modal"
					>
						<X size={18} />
					</button>
				</div>

				<div className="px-7 pb-5 pt-4 text-slate-900">
					<h2 className="font-display text-2xl font-bold leading-tight">{modeMeta[mode].title}</h2>
					<p className="mt-1 text-sm font-medium text-slate-600">{modeMeta[mode].subtitle}</p>
				</div>

				<div className="space-y-4 px-7 pb-7 pt-1">
					{error ? (
						<div className="flex items-start gap-2 rounded-xl border border-rose-200 bg-rose-50 px-3 py-2.5 text-rose-700">
							<AlertCircle size={15} className="mt-0.5 shrink-0" />
							<p className="text-sm font-medium leading-relaxed">{error}</p>
						</div>
					) : null}

					{message ? (
						<div className="flex items-start gap-2 rounded-xl border border-emerald-200 bg-emerald-50 px-3 py-2.5 text-emerald-700">
							<CheckCircle2 size={15} className="mt-0.5 shrink-0" />
							<p className="text-sm font-medium leading-relaxed">{message}</p>
						</div>
					) : null}

					{mode === MODES.LOGIN ? (
						<LoginForm
							forms={forms}
							updateForm={updateForm}
							loading={loading}
							onLogin={onLogin}
							onGoogleLogin={startGoogleLogin}
								onNeedHelp={onNeedHelp}
							onActivate={() => switchMode(MODES.ACTIVATE)}
							onForgotPassword={() => switchMode(MODES.FORGOT)}
						/>
					) : mode === MODES.ACTIVATE ? (
						<ActivateForm
							forms={forms}
							updateForm={updateForm}
							loading={loading}
							step={activationStep}
							onRequestOtp={onRequestActivationOtp}
							onVerifyOtp={onVerifyActivationOtp}
							onBackToLogin={() => switchMode(MODES.LOGIN)}
						/>
					) : (
						<ForgotPasswordForm
							forms={forms}
							updateForm={updateForm}
							loading={loading}
							step={forgotStep}
							onRequestOtp={onRequestForgotPasswordOtp}
							onResetPassword={onResetForgotPassword}
							onBackToLogin={() => switchMode(MODES.LOGIN)}
						/>
					)}
				</div>
			</div>
		</div>
	);
}

function LoginForm({ forms, updateForm, loading, onLogin, onGoogleLogin, onNeedHelp, onActivate, onForgotPassword }) {
	return (
		<form className="space-y-4" onSubmit={onLogin}>
			<Field
				icon={Hash}
				label="Email or User ID"
				placeholder="you@example.com or ADMIN001"
				value={forms.login.identifier}
				onChange={(value) => updateForm("login", "identifier", value)}
				required
			/>
			<PasswordField
				label="Password"
				placeholder="Enter your password"
				value={forms.login.password}
				onChange={(value) => updateForm("login", "password", value)}
				required
			/>

			<div className="flex items-center justify-between gap-3 pt-0.5">
				<button
					type="button"
					onClick={onActivate}
					className="text-sm font-semibold text-slate-600 transition hover:text-slate-800"
				>
					Need activation?
				</button>
				<button
					type="button"
					onClick={onForgotPassword}
					className="text-sm font-semibold text-slate-600 transition hover:text-slate-800"
				>
					Forgot password?
				</button>
			</div>

			<SubmitButton loading={loading} label="Sign In" loadingLabel="Signing in..." />

			<div className="flex items-center gap-3">
				<span className="h-px flex-1 bg-slate-200" />
				<span className="text-[11px] font-semibold uppercase tracking-wide text-slate-400">or</span>
				<span className="h-px flex-1 bg-slate-200" />
			</div>

			<button
				type="button"
				onClick={onGoogleLogin}
				className="flex w-full items-center justify-center gap-2 rounded-2xl border border-slate-300 bg-white px-4 py-3 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
			>
				<Globe size={16} /> Continue with Google
			</button>

			<button
				type="button"
				onClick={onNeedHelp}
				className="w-full rounded-2xl border border-dashed border-slate-300 px-4 py-2.5 text-sm font-semibold text-slate-600 transition hover:border-slate-400 hover:bg-slate-50"
			>
				Need help?
			</button>
		</form>
	);
}

function ForgotPasswordForm({ forms, updateForm, loading, step, onRequestOtp, onResetPassword, onBackToLogin }) {
	return (
		<form className="space-y-4" onSubmit={step === 1 ? onRequestOtp : onResetPassword}>
			<StepChip step={step} />

			{step === 1 ? (
				<>
					<Field
						icon={Mail}
						type="email"
						label="Campus email"
						placeholder="you@campus.edu"
						value={forms.forgotRequest.email}
						onChange={(value) => updateForm("forgotRequest", "email", value)}
						required
					/>
					<SubmitButton loading={loading} label="Send OTP" loadingLabel="Sending OTP..." />
				</>
			) : (
				<>
					<Field
						icon={ShieldCheck}
						label="OTP"
						placeholder="Enter 6-digit OTP"
						value={forms.forgotVerify.otp}
						onChange={(value) => updateForm("forgotVerify", "otp", value)}
						required
					/>
					<PasswordField
						icon={Lock}
						label="New password"
						placeholder="Create a new password"
						value={forms.forgotVerify.newPassword}
						onChange={(value) => updateForm("forgotVerify", "newPassword", value)}
						required
					/>
					<div className="grid grid-cols-2 gap-2">
						<button
							type="button"
							onClick={() => onBackToLogin()}
							className="rounded-2xl border border-slate-300 px-4 py-3 text-base font-semibold text-slate-700 transition hover:bg-slate-50"
						>
							Back
						</button>
						<SubmitButton loading={loading} label="Reset password" loadingLabel="Resetting..." />
					</div>
				</>
			)}
		</form>
	);
}

function ActivateForm({ forms, updateForm, loading, step, onRequestOtp, onVerifyOtp, onBackToLogin }) {
	return (
		<form className="space-y-4" onSubmit={step === 1 ? onRequestOtp : onVerifyOtp}>
			<StepChip step={step} />

			{step === 1 ? (
				<>
					<Field
						icon={Hash}
						label="Student or Lecturer ID"
						placeholder="EG: STU001"
						value={forms.activateRequest.userId}
						onChange={(value) => updateForm("activateRequest", "userId", value)}
						required
					/>
					<Field
						icon={Mail}
						type="email"
						label="Campus email"
						placeholder="you@campus.edu"
						value={forms.activateRequest.email}
						onChange={(value) => updateForm("activateRequest", "email", value)}
						required
					/>
					<SubmitButton loading={loading} label="Send OTP" loadingLabel="Sending OTP..." />
				</>
			) : (
				<>
					<Field
						icon={ShieldCheck}
						label="OTP"
						placeholder="Enter 6-digit OTP"
						value={forms.activateVerify.otp}
						onChange={(value) => updateForm("activateVerify", "otp", value)}
						required
					/>
					<PasswordField
						icon={Lock}
						label="New password"
						placeholder="Create a secure password"
						value={forms.activateVerify.newPassword}
						onChange={(value) => updateForm("activateVerify", "newPassword", value)}
						required
					/>
					<div className="grid grid-cols-2 gap-2">
						<button
							type="button"
							onClick={() => onBackToLogin()}
							className="rounded-2xl border border-slate-300 px-4 py-3 text-base font-semibold text-slate-700 transition hover:bg-slate-50"
						>
							Back
						</button>
						<SubmitButton loading={loading} label="Verify" loadingLabel="Verifying..." />
					</div>
				</>
			)}
		</form>
	);
}

function StepChip({ step }) {
	return (
			<p className="inline-flex items-center gap-2 rounded-lg bg-amber-50 px-3 py-1.5 text-[11px] font-bold uppercase tracking-wide text-amber-800">
				<span className="inline-flex h-5 w-5 items-center justify-center rounded-full bg-[#ffc111] text-[11px] font-bold text-slate-950">{step}</span>
			{step === 1 ? "Step 1: Request OTP" : "Step 2: Verify OTP"}
		</p>
	);
}

function Field({ icon: Icon, label, value, onChange, type = "text", placeholder, required }) {
	return (
		<label className="block">
			<span className="mb-1.5 block text-[11px] font-bold uppercase tracking-wide text-slate-600">{label}</span>
			<span className="relative block">
				<span className="pointer-events-none absolute inset-y-0 left-3.5 inline-flex items-center text-slate-400">
					<Icon size={16} />
				</span>
				<input
					type={type}
					value={value}
					onChange={(event) => onChange(event.target.value)}
					placeholder={placeholder}
					required={required}
					className="w-full rounded-2xl border border-slate-200 bg-white py-2.5 pl-11 pr-4 text-sm font-medium text-slate-900 outline-none transition focus:border-[#ffc111] focus:ring-2 focus:ring-amber-100"
				/>
			</span>
		</label>
	);
}

function PasswordField({ icon = Lock, label, value, onChange, placeholder, required }) {
	const [showPassword, setShowPassword] = useState(false);
	const Icon = icon;

	return (
		<label className="block">
			<span className="mb-1.5 block text-[11px] font-bold uppercase tracking-wide text-slate-600">{label}</span>
			<span className="relative block">
				<span className="pointer-events-none absolute inset-y-0 left-3.5 inline-flex items-center text-slate-400">
					<Icon size={16} />
				</span>
				<input
					type={showPassword ? "text" : "password"}
					value={value}
					onChange={(event) => onChange(event.target.value)}
					placeholder={placeholder}
					required={required}
					className="w-full rounded-2xl border border-slate-200 bg-white py-2.5 pl-11 pr-12 text-sm font-medium text-slate-900 outline-none transition focus:border-[#ffc111] focus:ring-2 focus:ring-amber-100"
				/>
				<button
					type="button"
					onClick={() => setShowPassword((prev) => !prev)}
					className="absolute inset-y-0 right-4 inline-flex items-center text-slate-500 transition hover:text-slate-700"
				>
					{showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
				</button>
			</span>
		</label>
	);
}

function SubmitButton({ loading, label, loadingLabel }) {
	return (
		<button
			type="submit"
			disabled={loading}
			className="inline-flex w-full items-center justify-center rounded-2xl bg-[#ffc111] px-4 py-2.5 text-sm font-bold text-slate-950 transition hover:brightness-95 disabled:cursor-not-allowed disabled:opacity-70"
		>
			{loading ? loadingLabel : label}
		</button>
	);
}
