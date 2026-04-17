const DEFAULT_API_URL = "";

function resolveApiUrl() {
  if (import.meta.env?.VITE_API_URL) {
    return import.meta.env.VITE_API_URL;
  }
  return DEFAULT_API_URL;
}

export async function requestJson(path, options = {}) {
  const apiBase = resolveApiUrl();
  const url = apiBase ? `${apiBase}${path}` : path;

  const response = await fetch(url, {
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {})
    },
    ...options
  });

  if (!response.ok) {
    const contentType = response.headers.get("content-type") || "";
    const message = contentType.includes("application/json")
      ? (await response.json()).message || response.statusText
      : await response.text();
    throw new Error(message || `Request failed with status ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}
