import { requestJson } from "../../services/apiClient";

const BASE = "/api/member3/tickets";

export function createTicket(payload) {
  return requestJson(BASE, {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function updateTicket(id, payload) {
  return requestJson(`${BASE}/${id}`, {
    method: "PUT",
    body: JSON.stringify(payload)
  });
}

export function getMyTickets() {
  return requestJson(`${BASE}/my`);
}

export function getTicketById(id) {
  return requestJson(`${BASE}/${id}`);
}

export function getAllTickets() {
  return requestJson(`${BASE}/all`);
}

export function getAssignedTickets() {
  return requestJson(`${BASE}/assigned`);
}

export function updateTicketStatus(id, payload) {
  return requestJson(`${BASE}/${id}/status`, {
    method: "PATCH",
    body: JSON.stringify(payload)
  });
}

export function assignTechnician(id, payload) {
  return requestJson(`${BASE}/${id}/assign`, {
    method: "PATCH",
    body: JSON.stringify(payload)
  });
}

export function deleteTicket(id) {
  return requestJson(`${BASE}/${id}`, {
    method: "DELETE"
  });
}

export function addComment(ticketId, payload) {
  return requestJson(`${BASE}/${ticketId}/comments`, {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function updateComment(ticketId, commentId, payload) {
  return requestJson(`${BASE}/${ticketId}/comments/${commentId}`, {
    method: "PUT",
    body: JSON.stringify(payload)
  });
}

export function deleteComment(ticketId, commentId) {
  return requestJson(`${BASE}/${ticketId}/comments/${commentId}`, {
    method: "DELETE"
  });
}
