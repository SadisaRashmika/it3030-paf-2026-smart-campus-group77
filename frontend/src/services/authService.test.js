import { beforeEach, describe, expect, it, vi } from "vitest";

vi.mock("./apiClient", () => ({
  requestJson: vi.fn()
}));

import { requestJson } from "./apiClient";
import { reportSuspiciousLogin } from "./authService";

describe("reportSuspiciousLogin", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("throws when user details are missing", () => {
    expect(() => reportSuspiciousLogin({ userId: "", email: "" })).toThrow(
      "Unable to report suspicious login. Missing user details."
    );
  });

  it("calls API with encoded query params", async () => {
    requestJson.mockResolvedValue({ message: "ok" });

    const response = await reportSuspiciousLogin({
      userId: "STU001",
      email: "user+test@campus.edu"
    });

    expect(requestJson).toHaveBeenCalledWith(
      "/api/public/activation/report-suspicious?userId=STU001&email=user%2Btest%40campus.edu"
    );
    expect(response).toEqual({ message: "ok" });
  });
});
