export function formatActivationStatus(user) {
	if (user.active && user.suspicious) {
		return "Active, flagged suspicious";
	}

	if (user.active) {
		return "Active";
	}

	if (user.suspicious) {
		return "Pending, flagged suspicious";
	}

	return "Pending activation";
}