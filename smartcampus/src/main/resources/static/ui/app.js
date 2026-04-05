const jsonHeaders = { "Content-Type": "application/json" };

async function request(path, options = {}) {
  const response = await fetch(path, {
    credentials: "same-origin",
    headers: { ...jsonHeaders, ...(options.headers || {}) },
    ...options
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed: ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

const loginForm = document.getElementById("login-form");
const authMessage = document.getElementById("auth-message");
const activationCard = document.getElementById("activation-card");
const adminCard = document.getElementById("admin-card");

const sendOtpForm = document.getElementById("send-otp-form");
const verifyForm = document.getElementById("verify-form");
const activationMessage = document.getElementById("activation-message");

const refreshUsersBtn = document.getElementById("refresh-users");
const adminMessage = document.getElementById("admin-message");
const usersTable = document.getElementById("users-table");

function showCard(element, visible) {
  element.classList.toggle("hidden", !visible);
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
    showCard(activationCard, true);
    if (me.role === "ROLE_ADMIN") {
      showCard(adminCard, true);
      await loadUsers();
    }
    authMessage.textContent = `Logged in as ${me.email} (${me.role})`;
  } catch {
    showCard(activationCard, true);
    showCard(adminCard, false);
  }
}

loginForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const email = document.getElementById("login-email").value;
  const password = document.getElementById("login-password").value;

  try {
    const result = await request("/api/public/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, password })
    });

    authMessage.textContent = `Welcome ${result.email}`;
    showCard(activationCard, true);
    if (result.role === "ROLE_ADMIN") {
      showCard(adminCard, true);
      await loadUsers();
    } else {
      showCard(adminCard, false);
    }
  } catch (error) {
    authMessage.textContent = error.message;
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
    activationMessage.textContent = result.message;
  } catch (error) {
    activationMessage.textContent = error.message;
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
    activationMessage.textContent = result.message;
  } catch (error) {
    activationMessage.textContent = error.message;
  }
});

refreshUsersBtn.addEventListener("click", async () => {
  await loadUsers();
});

checkSession();
