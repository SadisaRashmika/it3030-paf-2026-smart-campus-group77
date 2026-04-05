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
const authCard = document.getElementById("auth-card");
const activationStep1 = document.getElementById("activation-step-1");
const activationStep2 = document.getElementById("activation-step-2");
const adminCard = document.getElementById("admin-card");
const lecturerCard = document.getElementById("lecturer-card");
const studentCard = document.getElementById("student-card");

const openActivationBtn = document.getElementById("open-activation");
const backToLogin1Btn = document.getElementById("back-to-login-1");
const backToStep1Btn = document.getElementById("back-to-step-1");

const sendOtpForm = document.getElementById("send-otp-form");
const verifyForm = document.getElementById("verify-form");
const activationMessage = document.getElementById("activation-message");
const verifyMessage = document.getElementById("verify-message");

const refreshUsersBtn = document.getElementById("refresh-users");
const adminMessage = document.getElementById("admin-message");
const usersTable = document.getElementById("users-table");
const lecturerMessage = document.getElementById("lecturer-message");
const studentMessage = document.getElementById("student-message");

const logoutAdminBtn = document.getElementById("logout-admin");
const logoutLecturerBtn = document.getElementById("logout-lecturer");
const logoutStudentBtn = document.getElementById("logout-student");

function showCard(element, visible) {
  element.classList.toggle("hidden", !visible);
}

function setMessage(element, text, isSuccess = false) {
  element.textContent = text;
  element.classList.toggle("success", isSuccess);
}

function showLoginOnly() {
  showCard(authCard, true);
  showCard(activationStep1, false);
  showCard(activationStep2, false);
}

function hideDashboards() {
  showCard(adminCard, false);
  showCard(lecturerCard, false);
  showCard(studentCard, false);
}

function renderUsers(users) {
  usersTable.innerHTML = users
    .map((user) => `
      <tr>
        <td>${user.id}</td>
        <td>${user.userId}</td>
        <td>${user.email}</td>
        <td>${user.role}</td>
        <td>${user.status}</td>
        <td>${user.otpRequestCount}</td>
        <td>${user.failedOtpAttempts}</td>
      </tr>
    `)
    .join("");
}

async function loadUsers() {
  try {
    const users = await request("/api/admin/users");
    renderUsers(users);
    adminMessage.textContent = "";
  } catch (error) {
    adminMessage.textContent = error.message;
  }
}

async function checkSession() {
  try {
    const me = await request("/api/public/auth/me", { method: "GET" });
    showCard(authCard, false);
    showCard(activationStep1, false);
    showCard(activationStep2, false);
    hideDashboards();
    await openRoleDashboard(me.role, me.email);
  } catch {
    showLoginOnly();
    hideDashboards();
  }
}

async function openRoleDashboard(role, email) {
  if (role === "ROLE_ADMIN") {
    showCard(adminCard, true);
    await loadUsers();
    setMessage(adminMessage, `Logged in as ${email} (ADMIN)`, true);
    return;
  }

  if (role === "ROLE_LECTURER") {
    showCard(lecturerCard, true);
    try {
      const result = await request("/api/lecturer/dashboard", { method: "GET" });
      setMessage(lecturerMessage, result.message || `Logged in as ${email} (LECTURER)`, true);
    } catch (error) {
      setMessage(lecturerMessage, error.message, false);
    }
    return;
  }

  if (role === "ROLE_STUDENT") {
    showCard(studentCard, true);
    try {
      const result = await request("/api/student/dashboard", { method: "GET" });
      setMessage(studentMessage, result.message || `Logged in as ${email} (STUDENT)`, true);
    } catch (error) {
      setMessage(studentMessage, error.message, false);
    }
    return;
  }

  setMessage(authMessage, "Unknown role", false);
}

async function logout() {
  try {
    await request("/api/public/auth/logout", { method: "POST" });
  } catch {
    // no-op
  }
  setMessage(authMessage, "Logged out", true);
  showLoginOnly();
  hideDashboards();
}

openActivationBtn.addEventListener("click", () => {
  showCard(activationStep1, true);
  showCard(activationStep2, false);
  setMessage(activationMessage, "", false);
  setMessage(verifyMessage, "", false);
});

backToLogin1Btn.addEventListener("click", () => {
  showLoginOnly();
});

backToStep1Btn.addEventListener("click", () => {
  showCard(activationStep2, false);
  showCard(activationStep1, true);
  setMessage(verifyMessage, "", false);
});

loginForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const email = document.getElementById("login-email").value;
  const password = document.getElementById("login-password").value;

  try {
    const result = await request("/api/public/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, password })
    });

    showCard(authCard, false);
    showCard(activationStep1, false);
    showCard(activationStep2, false);
    hideDashboards();
    await openRoleDashboard(result.role, result.email);
  } catch (error) {
    setMessage(authMessage, error.message, false);
  }
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
    showCard(activationStep1, false);
    showCard(activationStep2, true);
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
  } catch (error) {
    setMessage(verifyMessage, error.message, false);
  }
});

refreshUsersBtn.addEventListener("click", async () => {
  await loadUsers();
});

logoutAdminBtn.addEventListener("click", logout);
logoutLecturerBtn.addEventListener("click", logout);
logoutStudentBtn.addEventListener("click", logout);

showLoginOnly();
hideDashboards();
checkSession();
