import { requestJson } from "../../../common/services/apiClient";

export const member4Api = {
	requestOtp(payload) {
		return requestJson("/api/public/activation/send-otp", {
			method: "POST",
			body: JSON.stringify(payload)
		});
	},
	verifyOtp(payload) {
		return requestJson("/api/public/activation/verify", {
			method: "POST",
			body: JSON.stringify(payload)
		});
	},
	getStatus(email) {
		return requestJson(`/api/public/activation/status?email=${encodeURIComponent(email)}`);
	},
	getUsers(authHeader) {
		return requestJson("/api/admin/users", {
			headers: { Authorization: authHeader }
		});
	},
	getSuspiciousUsers(authHeader) {
		return requestJson("/api/admin/users/suspicious", {
			headers: { Authorization: authHeader }
		});
	},
	updateUserRole(userId, role, authHeader) {
		return requestJson(`/api/admin/users/${userId}/role`, {
			method: "PATCH",
			headers: { Authorization: authHeader },
			body: JSON.stringify({ role })
		});
	},
	deactivateUser(userId, authHeader) {
		return requestJson(`/api/admin/users/${userId}/deactivate`, {
			method: "PATCH",
			headers: { Authorization: authHeader }
		});
	},
	deleteUser(userId, authHeader) {
		return requestJson(`/api/admin/users/${userId}`, {
			method: "DELETE",
			headers: { Authorization: authHeader }
		});
	}
};