# Architecture Diagrams

## Member 4 Architecture (Completed)

This section captures the architecture implemented for Member 4 features:
- OAuth authentication flow integration
- Notifications module
- Admin user/suspicious management support
- Account recovery workflow
- Profile picture update and rendering pipeline

## 1) Backend (Spring Boot) - Layered Architecture

```mermaid
flowchart TD
		A[React Client] --> B[Controller Layer]
		B --> C[Service Layer]
		C --> D[Repository Layer]
		D --> E[(PostgreSQL)]

		B --> F[DTO Validation]
		B --> G[ApiExceptionHandler]
		H[Spring Security + OAuth2] --> B
```

### Member 4 backend components

- Controllers
	- AuthController
	- ActivationController
	- AdminController
	- NotificationController
	- RecoveryRequestController
- Services
	- ActivationService
	- NotificationService
	- RecoveryRequestService
	- PasswordResetService
	- ConsoleMailService / MailService
- Security
	- SmartCampusSecurityConfig
	- DatabaseBackedOAuth2UserService
	- DatabaseBackedOidcUserService
	- TemporaryPasswordExpiryFilter

## 2) Frontend (React) - Component Architecture

```mermaid
flowchart TD
		M[MainLayout] --> N[TopNavHeader]
		M --> P[PortalTabContent]
		M --> Q[AuthModal]
		M --> R[ProfileModal]

		P --> U[AdminUsersPanel]
		P --> V[AdminRoleManagementPanel]
		P --> W[RecoveryRequestsPanel]

		N --> X[Notifications Dropdown]
		R --> Y[Profile Picture Update]
		Q --> Z[Forgot Password OTP Flow]
```

### Member 4 frontend responsibilities

- Navbar role-aware behavior and notification panel
- Auth modal with activation/forgot-password workflows
- Recovery request UI and admin actions
- Profile modal with photo upload and change-password entrypoint
- Admin activity list enhancements (avatar with fallback initials)

## 3) Data Model Additions (Member 4)

- `users.profile_picture_data_url` (V8)
- `users.temporary_password_hash` (V7)
- `users.temp_password_expires_at` (V7)
- `account_recovery_requests` table and indexes (V6)
- `notifications` table already used by Member 4 flows

## 4) Security and Access Notes

- Public endpoints: activation, login, forgot-password, recovery submission
- Protected endpoints: notifications, profile update
- Admin endpoints: user management, recovery approvals/rejections
- OAuth2 login configured with role-mapped access in security config

---

## Remaining Team Architecture Sections

Member 1, Member 2, and Member 3 architecture diagrams can be appended after this section.

