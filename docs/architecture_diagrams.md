# Architecture Diagrams

> Working draft note: This file is currently Member-4-first and will be expanded with Member 1/2/3 architecture when their modules are merged.

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

## 5) Quality and CI Notes (Member 4)

- Frontend CI now runs tests before build (`npm run test:run` then `npm run build`).
- Member 4 controller tests currently include:
	- `AuthControllerTest`
	- `NotificationControllerTest`
	- `RecoveryRequestControllerTest`
- Member 4 frontend tests currently include:
	- `authService.test.js`
	- `AdminUsersPanel.test.jsx`
- Stale/unrouted Member 4 duplicate files were removed to keep the page-first flow clean.

---

## Remaining Team Architecture Sections

Member 1, Member 2, and Member 3 architecture diagrams can be appended after this section.

## Member 1 Architecture (Pending)

To be completed by Member 1.

## Member 2 Architecture (Completed)

This section captures the architecture designed for Member 2 features:
- Resource Booking Management with conflict-checking engine (409 Conflict handling)
- Official Timetable system with weekly grid views
- Role-specific dashboards for Students, Lecturers, Timetable Managers, and Resource Administators
- Pending request management and resource inventory/availability workflows

### 1) Backend (Spring Boot) - Layered Architecture

```mermaid
flowchart TD
		A[React Client] --> B[Booking/Resource Controllers]
		B --> C[Service Layer]
		C --> D[Repository Layer]
		D --> E[(PostgreSQL)]

		B --> F[DTO Validation]
		B --> G[Conflict Resolution Engine]
```

#### Member 2 backend components

- Controllers
	- BookingController
	- ResourceController
- Services
	- BookingService (handles schedule conflict logic)
	- ResourceService
- Data Models
	- Booking, Resource, BookingStatus
- Repositories
	- BookingRepository
	- ResourceRepository
- Migration
	- `V12__booking_enhancements.sql` for booking-purpose enhancements

### 2) Frontend (React) - Component Architecture

```mermaid
flowchart TD
		M[PortalTabContent] --> U[Student Booking View]
		M --> V[Lecturer Booking View]
		M --> W[Timetable Manager Views]
		M --> R[Resource Administator Views]

		U --> X[BookingPanel]
		V --> X
		W --> T[TimetableWeeklyGrid]
		W --> Z[PendingApprovalsPanel]
		R --> I[ResourceManagementPanel]
		R --> A[ResourceAvailabilityView]
```

#### Member 2 frontend responsibilities

- Booking request form and booking history for students/lecturers.
- Weekly timetable grid with approved-slot rendering and week navigation.
- Pending booking approvals panel for timetable manager.
- Resource inventory CRUD and availability views for resource administator.
- Error handling for HTTP 409 conflicts during overlapping booking attempts.
## Member 3 Architecture (Pending)

To be completed by Member 3.

