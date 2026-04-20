const DEFAULT_API_URL = "http://localhost:8081";

function resolveApiUrl() {
  if (import.meta.env?.VITE_API_URL) {
    return import.meta.env.VITE_API_URL;
  }
  return DEFAULT_API_URL;
}

export async function requestJson(path, options = {}) {
  const apiBase = resolveApiUrl();
  const url = apiBase ? `${apiBase}${path}` : path;
  console.log("FRONTEND API CALL:", url);

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
    let message = "";
    try {
      if (contentType.includes("application/json")) {
        const errorData = await response.json();
        message = errorData.message || errorData.error || response.statusText;
      } else {
        message = await response.text();
      }
    } catch (e) {
      message = response.statusText;
    }
    throw new Error(message || `Request failed with status ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  // Handle cases where the response might be empty despite a successful status (like 200 OK with no body)
  const contentType = response.headers.get("content-type") || "";
  if (!contentType.includes("application/json")) {
    const text = await response.text();
    return text || null;
  }

  try {
    const text = await response.text();
    return text ? JSON.parse(text) : null;
  } catch (error) {
    console.warn("Server returned success but body was not valid JSON", error);
    return null;
  }
}
