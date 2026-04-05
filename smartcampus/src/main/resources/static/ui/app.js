const jsonHeaders = { "Content-Type": "application/json" };

async function request(path, options = {}) {
  const response = await fetch(path, {
    credentials: "same-origin",
    headers: { ...jsonHeaders, ...(options.headers || {}) },
    ...options
  });

  if (!response.ok) {
    let message = "";
    const raw = await response.text();
    try {
      const parsed = JSON.parse(raw);
      message = parsed.message || parsed.error || raw;
    } catch {
      message = raw;
    }
    throw new Error(message || `Request failed: ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

const loginForm = document.getElementById("login-form");
const authMessage = document.getElementById("auth-message");
const authModal = document.getElementById("auth-modal");
const openLoginBtn = document.getElementById("open-login");
const closeLoginBtn = document.getElementById("close-login");
const userChip = document.getElementById("user-chip");
const userEmail = document.getElementById("user-email");
const userMeta = document.getElementById("user-meta");
const logoutBtn = document.getElementById("logout-btn");
const tabButtons = Array.from(document.querySelectorAll(".tab-btn"));
const heroTitle = document.getElementById("hero-title");
const heroSubtitle = document.getElementById("hero-subtitle");

const activationStep1 = document.getElementById("activation-step-1");
const activationStep2 = document.getElementById("activation-step-2");
const panelHome = document.getElementById("panel-home");
const panelTab1 = document.getElementById("panel-tab1");
const panelTab2 = document.getElementById("panel-tab2");
const panelTab3 = document.getElementById("panel-tab3");

const openActivationBtn = document.getElementById("open-activation");

const sendOtpForm = document.getElementById("send-otp-form");
const verifyForm = document.getElementById("verify-form");
const activationMessage = document.getElementById("activation-message");
const verifyMessage = document.getElementById("verify-message");
let currentUser = null;
let pendingTab = "home";

function showCard(element, visible) {
  element.classList.toggle("hidden", !visible);
}

function setMessage(element, text, isSuccess = false) {
  element.textContent = text;
  element.classList.toggle("success", isSuccess);
}

function closeModal() {
  showCard(authModal, false);
}

function openModal() {
  showCard(authModal, true);
}

function resetActivationPanels() {
  showCard(activationStep1, false);
  showCard(activationStep2, false);
}

function showActivationStep(step) {
  showCard(activationStep1, step === 1);
  showCard(activationStep2, step === 2);
}

const panels = {
  home: panelHome,
  tab1: panelTab1,
  tab2: panelTab2,
  tab3: panelTab3
};

function setActiveTab(tabKey) {
  Object.entries(panels).forEach(([key, panel]) => {
    panel.classList.toggle("active", key === tabKey);
  });

  tabButtons.forEach((button) => {
    button.classList.toggle("active", button.dataset.tab === tabKey);
  });
}

function updateHero(user) {
  if (!user) {
    heroTitle.textContent = "Welcome to SmartCampus";
    heroSubtitle.textContent = "Use the top navigation to switch between sections.";
    return;
  }

  const cleanRole = (user.role || "ROLE_UNKNOWN").replace("ROLE_", "");
  heroTitle.textContent = `Welcome ${cleanRole}`;
  heroSubtitle.textContent = `Logged in as ${cleanRole} | ID: ${user.userId || "UNKNOWN"}`;
}

function setAuthState(user) {
  currentUser = user;

  if (!user) {
    showCard(openLoginBtn, true);
    showCard(userChip, false);
    showCard(logoutBtn, false);
    setActiveTab("home");
    updateHero(null);
    return;
  }

  const cleanRole = (user.role || "ROLE_UNKNOWN").replace("ROLE_", "");
  userEmail.textContent = user.email;
  userMeta.textContent = `${cleanRole} | ${user.userId || "UNKNOWN"}`;
  showCard(openLoginBtn, false);
  showCard(userChip, true);
  showCard(logoutBtn, true);
  updateHero(user);
}

async function checkSession() {
  try {
    const me = await request("/api/public/auth/me", { method: "GET" });
    setAuthState(me);
  } catch {
    setAuthState(null);
  }
}

async function logout() {
  try {
    await request("/api/public/auth/logout", { method: "POST" });
  } catch {
    // no-op
  }

  pendingTab = "home";
  setAuthState(null);
  closeModal();
  setMessage(authMessage, "", false);
  resetActivationPanels();
}

openLoginBtn.addEventListener("click", () => {
  pendingTab = "home";
  openModal();
  resetActivationPanels();
});

closeLoginBtn.addEventListener("click", () => {
  closeModal();
  resetActivationPanels();
});

authModal.addEventListener("click", (event) => {
  if (event.target === authModal) {
    closeModal();
  }
});

loginForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const identifier = document.getElementById("login-identifier").value.trim();
  const password = document.getElementById("login-password").value;

  if (!identifier) {
    setMessage(authMessage, "Enter email or user ID.", false);
    return;
  }

  const isEmailIdentifier = identifier.includes("@");
  const email = isEmailIdentifier ? identifier : "";
  const userId = isEmailIdentifier ? "" : identifier;

  try {
    const result = await request("/api/public/auth/login", {
      method: "POST",
      body: JSON.stringify({ identifier, userId, email, password })
    });

    setAuthState(result);
    setActiveTab(pendingTab);
    pendingTab = "home";
    closeModal();
    resetActivationPanels();
    setMessage(authMessage, "", false);
  } catch (error) {
    setMessage(authMessage, error.message, false);
  }
});

openActivationBtn.addEventListener("click", () => {
  showActivationStep(1);
  setMessage(activationMessage, "", false);
  setMessage(verifyMessage, "", false);
});

sendOtpForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const userId = document.getElementById("activation-user-id").value;
  const email = document.getElementById("activation-email").value;

  try {
    const result = await request("/api/public/activation/send-otp", {
      method: "POST",
      body: JSON.stringify({ userId, email })
    });
    setMessage(activationMessage, result.message, true);
    showActivationStep(2);
  } catch (error) {
    setMessage(activationMessage, error.message, false);
  }
});

verifyForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const userId = document.getElementById("activation-user-id").value;
  const email = document.getElementById("activation-email").value;
  const otp = document.getElementById("activation-otp").value;
  const newPassword = document.getElementById("activation-password").value;

  try {
    const result = await request("/api/public/activation/verify", {
      method: "POST",
      body: JSON.stringify({ userId, email, otp, newPassword })
    });
    setMessage(verifyMessage, result.message, true);
    setMessage(authMessage, "Activation successful. You can now login.", true);
    showActivationStep(1);
  } catch (error) {
    setMessage(verifyMessage, error.message, false);
  }
});

tabButtons.forEach((button) => {
  button.addEventListener("click", () => {
    const requestedTab = button.dataset.tab;
    if (requestedTab !== "home" && !currentUser) {
      pendingTab = requestedTab;
      setMessage(authMessage, "Please login to access this tab.", false);
      openModal();
      resetActivationPanels();
      setActiveTab("home");
      return;
    }

    setActiveTab(requestedTab);
  });
});

logoutBtn.addEventListener("click", logout);

setActiveTab("home");
setAuthState(null);
resetActivationPanels();
checkSession();
