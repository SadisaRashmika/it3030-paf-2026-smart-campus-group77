import React from "react";
import { renderToStaticMarkup } from "react-dom/server";
import { describe, expect, it, vi } from "vitest";

import AdminUsersPanel from "./AdminUsersPanel";

describe("AdminUsersPanel", () => {
  const users = [
    {
      id: 1,
      name: "Student One",
      userId: "STU001",
      email: "student@campus.edu",
      role: "ROLE_STUDENT",
      status: "ACTIVE",
      active: true,
      otpRequestCount: 0,
      failedOtpAttempts: 0
    }
  ];

  it("renders Active filter option", () => {
    const html = renderToStaticMarkup(
      <AdminUsersPanel
        users={users}
        suspiciousUsers={[]}
        loading={false}
        onDeleteUser={vi.fn()}
        onDeactivateUser={vi.fn()}
      />
    );

    expect(html).toContain("Active");
  });

  it("renders user ID in account rows", () => {
    const html = renderToStaticMarkup(
      <AdminUsersPanel
        users={users}
        suspiciousUsers={[]}
        loading={false}
        onDeleteUser={vi.fn()}
        onDeactivateUser={vi.fn()}
      />
    );

    expect(html).toContain("User ID: STU001");
  });
});
