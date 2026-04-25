import { requestJson } from "../../services/apiClient";

const BASE_URL = "/api/member2/resources";

export const resourceApi = {
    /** Resources */
    getResources: () => requestJson(`${BASE_URL}/resources`),

    /** Resource Core */
    createResource: (data) => requestJson(`${BASE_URL}`, {
        method: "POST",
        body: JSON.stringify(data)
    }),

    getMyResources: () => requestJson(`${BASE_URL}/mine`),

    cancelResource: (id) => requestJson(`${BASE_URL}/${id}/cancel`, {
        method: "PATCH"
    }),

    /** Management & Schedule */
    getPendingResources: () => requestJson(`${BASE_URL}/pending`),

    getWeeklyResources: (start, end) => requestJson(`${BASE_URL}/weekly?start=${start}&end=${end}`),

    approveResource: (id) => requestJson(`${BASE_URL}/${id}/approve`, {
        method: "PATCH"
    }),

    rejectResource: (id, reason) => requestJson(`${BASE_URL}/${id}/reject`, {
        method: "PATCH",
        body: JSON.stringify({ rejectionReason: reason })
    })
};
