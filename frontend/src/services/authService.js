import { requestJson } from "./apiClient";

function deriveName(user) {
  const fromName = typeof user?.name === "string" ? user.name.trim() : "";
  if (fromName) {
    return fromName;
  }

  if (user?.userId) {
    return user.userId;
  }

  if (typeof user?.email === "string" && user.email.includes("@")) {
    return user.email.split("@")[0];
  }

  return "SmartCampus User";
}

function normalizeUser(user) {
  if (!user || typeof user !== "object") {
    return user;
  }

  return {
    ...user,
    name: deriveName(user)
  };
}

export function login(payload) {
  return requestJson("/api/public/auth/login", {
    method: "POST",
    body: JSON.stringify(payload)
  }).then(normalizeUser);
}

export function getCurrentUser() {
  return requestJson("/api/public/auth/me").then(normalizeUser);
}

export function logout() {
  return requestJson("/api/public/auth/logout", { method: "POST" });
}

export function requestActivationOtp(payload) {
  return requestJson("/api/public/activation/send-otp", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function verifyActivationOtp(payload) {
  return requestJson("/api/public/activation/verify", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function requestForgotPasswordOtp(payload) {
  return requestJson("/api/public/auth/forgot-password/send-otp", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function resetForgotPassword(payload) {
  return requestJson("/api/public/auth/forgot-password/reset", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function getAdminUsers() {
  return requestJson("/api/admin/users").then((users) => users.map(normalizeUser));
}

export function getSuspiciousUsers() {
  return requestJson("/api/admin/users/suspicious").then((users) => users.map(normalizeUser));
}

export function createStaffLogin(payload) {
  return requestJson("/api/admin/users/staff-login", {
    method: "POST",
    body: JSON.stringify(payload)
  }).then(normalizeUser);
}

export function assignLecturerWork(payload) {
  return requestJson("/api/admin/lecturers/assign-work", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function getLecturerAssignments() {
  return requestJson("/api/admin/lecturers/assignments");
}

export function deleteUser(userId) {
  return requestJson(`/api/admin/users/${userId}`, {
    method: "DELETE"
  });
}
