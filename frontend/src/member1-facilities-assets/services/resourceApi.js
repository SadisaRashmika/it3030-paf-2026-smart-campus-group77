import { requestJson } from "../../services/apiClient";

const RESOURCE_BASE_URL = "/api/member1/resources";
const LEGACY_RESOURCE_BASE_URL = "/api/member2/resources";

async function requestResourceWithFallback(path, options) {
  try {
    return await requestJson(`${RESOURCE_BASE_URL}${path}`, options);
  } catch (error) {
    const message = String(error?.message || "").toLowerCase();
    if (message.includes("404") || message.includes("not found")) {
      return requestJson(`${LEGACY_RESOURCE_BASE_URL}${path}`, options);
    }
    throw error;
  }
}

export const resourceApi = {
  getResources: () => requestResourceWithFallback(""),

  createResource: (data) =>
    requestResourceWithFallback("", {
      method: "POST",
      body: JSON.stringify(data)
    }),

  updateResource: (id, data) =>
    requestResourceWithFallback(`/${id}`, {
      method: "PUT",
      body: JSON.stringify(data)
    }),

  deleteResource: (id) =>
    requestResourceWithFallback(`/${id}`, {
      method: "DELETE"
    })
};
