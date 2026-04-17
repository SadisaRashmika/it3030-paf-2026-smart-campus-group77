# Team Contributions

## Member 4 - Notifications, OAuth/Auth Improvements, Role Management Support

### Summary

Implemented the full Member 4 module scope around authentication experience, notification workflows, account recovery, admin user operations support, and profile picture features across backend + frontend.

### Backend Contributions

1. Authentication and session APIs
- Login/logout/me endpoints
- Forgot-password OTP send/reset endpoints
- Profile picture update endpoint

2. OAuth integration and access control
- OAuth2/OIDC backed user service integration
- Role-protected endpoint access via Spring Security config
- Temporary password expiry enforcement filter integration

3. Notifications module
- List notifications for current user
- Mark single notification as read
- Mark all notifications as read
- Create login alert notification
- Delete notification
- Notification creation on password change and OAuth login events

4. Admin user management support
- User list and suspicious user list
- Deactivate, clear suspicious, delete, role update
- Lecturer assignment support endpoints

5. Recovery workflow
- Public recovery request submission with ID photo evidence
- Admin approval/rejection endpoints
- One-day temporary password issuance flow and email notifications

6. Database migrations and schema support
- Recovery request table migration
- Temporary password fields migration
- Profile picture field migration

### Frontend Contributions

1. Auth and session UX
- Login/activate/forgot-password modal workflows
- Google login trigger integration

2. Top navigation and notifications
- Notification panel in top navigation
- Read/read-all controls
- Admin visibility for notifications

3. Admin activity and user management UI
- Admin users/suspicious account panels
- Avatar rendering in activity lists (profile picture with initials fallback)
- Recovery requests panel with approve/reject handling

4. Profile features
- Profile modal with read-only user details
- Profile picture upload and save flow
- Change-password entrypoint connected to OTP reset flow
- Navbar avatar replacement with profile image fallback behavior

5. Frontend reliability/testing updates
- Standardized Member 4 API wrapper to use the active session-aware API client
- Added frontend unit test coverage for suspicious-login request validation and query encoding

### Member 4 Endpoint Count Evidence

Member 4 implemented more than four endpoints and used multiple HTTP methods:
- GET: examples include `/api/public/auth/me`, `/api/member4/notifications/me`, `/api/admin/users`
- POST: examples include `/api/public/auth/login`, `/api/member4/notifications/login-alert`, `/api/public/recovery-requests`
- PATCH: examples include `/api/public/auth/profile-picture`, `/api/member4/notifications/read-all`, `/api/admin/recovery-requests/{requestId}/approve`
- DELETE: examples include `/api/member4/notifications/{id}`, `/api/admin/users/{userId}`

### Key Files (Member 4)

- Backend controllers/services/security under:
	- `smartcampus/src/main/java/com/it3030/smartcampus/member4/**`
- Frontend module components/pages under:
	- `frontend/src/member4-notifications-oauth/**`
- Shared integration points:
	- `frontend/src/layouts/MainLayout.jsx`
	- `frontend/src/services/authService.js`
	- `frontend/src/pages/HelpPage.jsx`

### Testing Evidence Added (Member 4)

- Frontend test file:
	- `frontend/src/services/authService.test.js`
- Backend test file:
	- `smartcampus/src/test/java/com/it3030/smartcampus/member4/controller/AuthControllerTest.java`

### Command/Execution Clarification Added

- Documented that backend tests should run via:
	- backend folder: `npm test`
	- repository root: `npm run backend:test` or `smartcampus\mvnw.cmd -f smartcampus\pom.xml test`
- Clarified why plain `./mvnw.cmd test` fails at repository root (wrapper is under `smartcampus/`).

---

## Member 1

Pending update by Member 1.

## Member 2

Pending update by Member 2.

## Member 3

Pending update by Member 3.

