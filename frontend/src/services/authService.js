import { requestJson } from "./apiClient";

export function login(payload) {
  return requestJson("/api/public/auth/login", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function getCurrentUser() {
  return requestJson("/api/public/auth/me");
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
  return requestJson("/api/admin/users");
}

export function getSuspiciousUsers() {
  return requestJson("/api/admin/users/suspicious");
}
