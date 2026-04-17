# SmartCampus API Endpoints

## Member 4 Scope (Completed)

This section documents the endpoints implemented and maintained under Member 4 responsibilities:
- Notifications
- OAuth and authentication improvements
- Admin user management support
- Account recovery workflow
- Profile picture support

### Authentication and Session

| Method | Path | Access | Description | Typical Responses |
|---|---|---|---|---|
| POST | /api/public/auth/login | Public | Login using email/userId/identifier + password | 200, 400, 401, 403 |
| POST | /api/public/auth/logout | Authenticated | Invalidate session | 204 |
| GET | /api/public/auth/me | Authenticated | Return current user profile and role | 200, 401 |
| PATCH | /api/public/auth/profile-picture | Authenticated | Update current user profile picture (data URL) | 200, 400, 401 |

Sample request (login):

```json
{
	"identifier": "admin001",
	"password": "12345"
}
```

Sample response (me):

```json
{
	"name": "Koffy Doggy",
	"email": "koffy.doggy@gmail.com",
	"userId": "ADMIN001",
	"role": "ROLE_ADMIN",
	"authenticated": true,
	"profilePictureDataUrl": "data:image/png;base64,..."
}
```

### Forgot Password (OTP)

| Method | Path | Access | Description | Typical Responses |
|---|---|---|---|---|
| POST | /api/public/auth/forgot-password/send-otp | Public | Send OTP to account email | 200, 400 |
| POST | /api/public/auth/forgot-password/reset | Public | Reset password using OTP | 200, 400 |

### Activation and Suspicious Report

| Method | Path | Access | Description | Typical Responses |
|---|---|---|---|---|
| POST | /api/public/activation/send-otp | Public | Send activation OTP | 202, 400 |
| POST | /api/public/activation/verify | Public | Verify activation OTP and set password | 200, 400 |
| GET | /api/public/activation/status?userId={id} | Public | Check account activation status | 200, 400 |
| GET | /api/public/activation/report-suspicious?userId={id}&email={email} | Public | Report suspicious login event | 200, 400 |

### Notifications

| Method | Path | Access | Description | Typical Responses |
|---|---|---|---|---|
| GET | /api/member4/notifications/me | Authenticated | List current user notifications | 200, 401 |
| PATCH | /api/member4/notifications/{id}/read | Authenticated | Mark single notification as read | 200, 404 |
| PATCH | /api/member4/notifications/read-all | Authenticated | Mark all current user notifications as read | 200 |
| POST | /api/member4/notifications/login-alert | Authenticated | Create login-success notification for current user | 201, 401 |
| DELETE | /api/member4/notifications/{id} | Authenticated | Delete single notification | 204, 404 |

### Recovery Requests

| Method | Path | Access | Description | Typical Responses |
|---|---|---|---|---|
| POST | /api/public/recovery-requests | Public | Submit account recovery request with ID photo | 201, 400 |
| GET | /api/admin/recovery-requests | Admin | List all recovery requests | 200, 403 |
| PATCH | /api/admin/recovery-requests/{requestId}/approve | Admin | Approve recovery and issue temporary password | 200, 400, 403 |
| PATCH | /api/admin/recovery-requests/{requestId}/reject | Admin | Reject recovery request | 200, 403 |

### Admin Controls

| Method | Path | Access | Description | Typical Responses |
|---|---|---|---|---|
| GET | /api/admin/users | Admin | List all users with profile picture field | 200, 403 |
| GET | /api/admin/users/suspicious | Admin | List suspicious accounts | 200, 403 |
| POST | /api/admin/users/staff-login | Admin | Create lecturer/staff login | 201, 400, 403 |
| POST | /api/admin/lecturers/assign-work | Admin | Create lecturer assignment | 200, 400, 403 |
| GET | /api/admin/lecturers/assignments | Admin | List lecturer assignments | 200, 403 |
| PATCH | /api/admin/users/{userId}/role | Admin | Update role | 200, 400, 403 |
| PATCH | /api/admin/users/{userId}/deactivate | Admin | Deactivate account | 200, 403 |
| PATCH | /api/admin/users/{userId}/clear-suspicious | Admin | Clear suspicious flag | 200, 403 |
| DELETE | /api/admin/users/{userId} | Admin | Delete account | 204, 403 |

---

## Notes for Remaining Team Sections

Member 1, Member 2, and Member 3 endpoint documentation can be appended below this section using the same table format.

