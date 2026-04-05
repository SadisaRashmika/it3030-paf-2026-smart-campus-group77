import { useEffect, useState } from "react";
import {
  AlertCircle,
  CheckCircle2,
  Eye,
  EyeOff,
  Hash,
  Lock,
  Mail,
  ShieldCheck,
  X
} from "lucide-react";
import { login, requestActivationOtp, verifyActivationOtp } from "../services/authService";

const MODES = {
  LOGIN: "login",
  ACTIVATE: "activate"
};

const initialForms = {
  login: { email: "", password: "" },
  activateRequest: { userId: "", email: "" },
  activateVerify: { otp: "", newPassword: "" }
};

const createInitialForms = () => ({
  login: { ...initialForms.login },
  activateRequest: { ...initialForms.activateRequest },
  activateVerify: { ...initialForms.activateVerify }
});

export default function AuthModal({ isOpen, onClose, onAuthenticated, initialMode = MODES.LOGIN }) {
  const [mode, setMode] = useState(initialMode);
  const [forms, setForms] = useState(createInitialForms());
  const [activationStep, setActivationStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    if (!isOpen) {
      setMode(MODES.LOGIN);
      setForms(createInitialForms());
      setActivationStep(1);
      setLoading(false);
      setError("");
      setMessage("");
      return;
    }

    setMode(initialMode);
    setForms(createInitialForms());
    setActivationStep(1);
    setLoading(false);
    setError("");
    setMessage("");
  }, [initialMode, isOpen]);

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
    resetFeedback();
  };

  const onLogin = async (event) => {
    event.preventDefault();
    resetFeedback();
    setLoading(true);
    try {
      const user = await login(forms.login);
      onAuthenticated(user);
      onClose();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
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

  const modeMeta = {
    login: {
      title: "Welcome back",
      subtitle: "Sign in to SmartCampus"
    },
    activate: {
      title: "Activate your account",
      subtitle: "Verify your ID and set your password"
    }
  };

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-4"
      style={{ background: "rgba(2,6,23,0.55)", backdropFilter: "blur(7px)" }}
      onMouseDown={(event) => {
        if (event.target === event.currentTarget) {
          onClose();
        }
      }}
    >
      <div className="relative w-full max-w-md overflow-hidden rounded-3xl bg-white shadow-glass">
        <div className="bg-gradient-to-br from-blue-600 via-blue-500 to-cyan-500 px-7 pb-8 pt-7 text-white">
          <button
            onClick={onClose}
            className="absolute right-4 top-4 inline-flex h-8 w-8 items-center justify-center rounded-lg bg-white/20 text-white transition hover:bg-white/30"
          >
            <X size={16} />
          </button>
          <p className="mb-3 inline-flex items-center gap-2 rounded-full bg-white/20 px-3 py-1 text-[11px] font-semibold uppercase tracking-wide">
            <ShieldCheck size={13} /> Secure Access
          </p>
          <h2 className="font-display text-2xl font-bold leading-tight">{modeMeta[mode].title}</h2>
          <p className="mt-1 text-sm text-blue-50">{modeMeta[mode].subtitle}</p>
        </div>

        <div className="space-y-3 px-7 pb-7 pt-5">
          {error ? (
            <div className="flex items-start gap-2 rounded-xl border border-rose-200 bg-rose-50 px-3 py-2.5 text-rose-700">
              <AlertCircle size={15} className="mt-0.5 shrink-0" />
              <p className="text-xs leading-relaxed">{error}</p>
            </div>
          ) : null}

          {message ? (
            <div className="flex items-start gap-2 rounded-xl border border-emerald-200 bg-emerald-50 px-3 py-2.5 text-emerald-700">
              <CheckCircle2 size={15} className="mt-0.5 shrink-0" />
              <p className="text-xs leading-relaxed">{message}</p>
            </div>
          ) : null}

          {mode === MODES.LOGIN ? (
            <LoginForm
              forms={forms}
              updateForm={updateForm}
              loading={loading}
              onLogin={onLogin}
              onActivate={() => switchMode(MODES.ACTIVATE)}
            />
          ) : (
            <ActivateForm
              forms={forms}
              updateForm={updateForm}
              loading={loading}
              step={activationStep}
              onRequestOtp={onRequestActivationOtp}
              onVerifyOtp={onVerifyActivationOtp}
              onBackToLogin={() => switchMode(MODES.LOGIN)}
            />
          )}
        </div>
      </div>
    </div>
  );
}

function LoginForm({ forms, updateForm, loading, onLogin, onActivate }) {
  return (
    <form className="space-y-3" onSubmit={onLogin}>
      <Field
        icon={Mail}
        type="email"
        label="Campus email"
        placeholder="student@my.sliit.lk"
        value={forms.login.email}
        onChange={(value) => updateForm("login", "email", value)}
        required
      />
      <PasswordField
        label="Password"
        placeholder="Enter your password"
        value={forms.login.password}
        onChange={(value) => updateForm("login", "password", value)}
        required
      />

      <div className="flex items-center justify-between gap-2 pt-1">
        <button
          type="button"
          onClick={onActivate}
          className="text-xs font-semibold text-slate-500 transition hover:text-slate-700"
        >
          Need activation?
        </button>
      </div>

      <SubmitButton loading={loading} label="Sign In" loadingLabel="Signing in..." />
    </form>
  );
}

function ActivateForm({ forms, updateForm, loading, step, onRequestOtp, onVerifyOtp, onBackToLogin }) {
  return (
    <form className="space-y-3" onSubmit={step === 1 ? onRequestOtp : onVerifyOtp}>
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
            placeholder="student@my.sliit.lk"
            value={forms.activateRequest.email}
            onChange={(value) => updateForm("activateRequest", "email", value)}
            required
          />
          <SubmitButton loading={loading} label="Send OTP" loadingLabel="Sending..." variant="indigo" />
        </>
      ) : (
        <>
          <OtpField
            value={forms.activateVerify.otp}
            onChange={(value) => updateForm("activateVerify", "otp", value)}
          />
          <PasswordField
            label="Create password"
            placeholder="Minimum 8 characters"
            value={forms.activateVerify.newPassword}
            onChange={(value) => updateForm("activateVerify", "newPassword", value)}
            required
          />
          <SubmitButton loading={loading} label="Activate Account" loadingLabel="Activating..." variant="indigo" />
        </>
      )}

      <div className="pt-1 text-center">
        <button
          type="button"
          onClick={onBackToLogin}
          className="text-xs font-semibold text-slate-500 transition hover:text-slate-700"
        >
          Back to sign in
        </button>
      </div>
    </form>
  );
}

function Field({ icon: Icon, label, type = "text", placeholder, value, onChange, required }) {
  return (
    <label className="block">
      <span className="mb-1.5 block text-xs font-semibold text-slate-700">{label}</span>
      <div className="relative">
        {Icon ? (
          <span className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">
            <Icon size={14} />
          </span>
        ) : null}
        <input
          type={type}
          placeholder={placeholder}
          value={value}
          onChange={(event) => onChange(event.target.value)}
          required={required}
          className={`w-full rounded-xl border border-slate-200 bg-slate-50 py-2.5 text-sm text-slate-900 outline-none transition focus:border-blue-400 focus:bg-white focus:ring-4 focus:ring-blue-100 ${
            Icon ? "pl-9 pr-3" : "px-3"
          }`}
        />
      </div>
    </label>
  );
}

function PasswordField({ label, placeholder, value, onChange, required }) {
  const [show, setShow] = useState(false);

  return (
    <label className="block">
      <span className="mb-1.5 block text-xs font-semibold text-slate-700">{label}</span>
      <div className="relative">
        <span className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">
          <Lock size={14} />
        </span>
        <input
          type={show ? "text" : "password"}
          placeholder={placeholder}
          value={value}
          onChange={(event) => onChange(event.target.value)}
          required={required}
          className="w-full rounded-xl border border-slate-200 bg-slate-50 py-2.5 pl-9 pr-10 text-sm text-slate-900 outline-none transition focus:border-blue-400 focus:bg-white focus:ring-4 focus:ring-blue-100"
        />
        <button
          type="button"
          onClick={() => setShow((prev) => !prev)}
          className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 transition hover:text-slate-600"
        >
          {show ? <EyeOff size={14} /> : <Eye size={14} />}
        </button>
      </div>
    </label>
  );
}

function OtpField({ value, onChange }) {
  return (
    <label className="block">
      <span className="mb-1.5 block text-xs font-semibold text-slate-700">OTP Code</span>
      <input
        type="text"
        maxLength={6}
        inputMode="numeric"
        value={value}
        onChange={(event) => onChange(event.target.value.replace(/\D/g, ""))}
        placeholder="6-digit code"
        className="w-full rounded-xl border border-slate-200 bg-slate-50 px-3 py-2.5 font-mono text-sm tracking-[0.2em] text-slate-900 outline-none transition focus:border-blue-400 focus:bg-white focus:ring-4 focus:ring-blue-100"
      />
    </label>
  );
}

function StepChip({ step }) {
  return (
    <p className="inline-flex items-center gap-2 rounded-full bg-indigo-50 px-3 py-1 text-[11px] font-bold uppercase tracking-wide text-indigo-700">
      <span className="inline-flex h-5 w-5 items-center justify-center rounded-full bg-indigo-600 text-[10px] text-white">{step}</span>
      Step {step} of 2
    </p>
  );
}

function SubmitButton({ loading, label, loadingLabel, variant = "blue" }) {
  const gradients = {
    blue: "linear-gradient(135deg,#2563eb,#1d4ed8)",
    indigo: "linear-gradient(135deg,#4f46e5,#4338ca)"
  };

  return (
    <button
      type="submit"
      disabled={loading}
      className="mt-2 w-full rounded-xl py-2.5 text-sm font-semibold text-white transition disabled:cursor-not-allowed disabled:opacity-60"
      style={{ background: gradients[variant], boxShadow: "0 10px 24px rgba(37,99,235,0.24)" }}
    >
      {loading ? loadingLabel : label}
    </button>
  );
}
