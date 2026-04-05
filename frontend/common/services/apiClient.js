const DEFAULT_BASE_URL = "http://localhost:8081";

function getBaseUrl() {
	if (typeof import.meta !== "undefined" && import.meta.env && import.meta.env.VITE_API_URL) {
		return import.meta.env.VITE_API_URL;
	}

	return DEFAULT_BASE_URL;
}

export async function requestJson(path, options = {}) {
	const response = await fetch(`${getBaseUrl()}${path}`, {
		headers: {
			"Content-Type": "application/json",
			...(options.headers || {})
		},
		...options
	});

	if (!response.ok) {
		const message = await response.text();
		throw new Error(message || `Request failed with ${response.status}`);
	}

	if (response.status === 204) {
		return null;
	}

	return response.json();
}