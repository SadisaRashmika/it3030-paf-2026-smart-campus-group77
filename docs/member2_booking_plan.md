# Member 2 – Booking Management Module Plan

> **Status**: Planning Document — No code written yet.
> **Author**: Member 2
> **Last Updated**: 2026-04-18

---

## Golden Rule

> **Member 2 will NOT edit any file written by another member.**
> All Member 2 code lives exclusively inside:
> - `smartcampus/src/main/java/com/it3030/smartcampus/member2/` (backend)
> - `frontend/src/member2-bookings-management/` (frontend)
>
> The only shared file Member 2 may fill in are the **designated placeholder stub slots** inside `PortalTabContent.jsx` and `TopNavHeader.jsx` that were explicitly left blank for Member 2 (`TAB03` for student/lecturer). Editing your own stub slot is not "touching another member's work".

---

## Overview

This document outlines the full plan for implementing **Module B – Booking Management**. The module allows Students and Lecturers to request bookings for campus resources, and allows Admin users to approve or reject those requests. The system prevents double-booking conflicts.

---

## 1. Scope

### Core Features (Required)

| # | Feature | Description |
|---|---------|-------------|
| 1 | **Create Booking Request** | Authenticated user selects a resource, date, time range, purpose, and expected attendees. |
| 2 | **Booking Workflow** | `PENDING → APPROVED` or `PENDING → REJECTED`. Approved bookings can be `CANCELLED`. |
| 3 | **Conflict Prevention** | Rejects new booking if an APPROVED booking already occupies the same resource/time. Returns `HTTP 409`. |
| 4 | **Admin Review** | Admin can list all bookings and approve or reject with an optional reason. |
| 5 | **User Booking History** | Users view their own bookings; Admin sees all with filters. |

### Timetable Manager Features (Core — All 3 Tab Stubs Are Blank)

| # | Feature | Tab | Description |
|---|---------|-----|-------------|
| 6 | **Weekly Timetable Grid** | TAB01 | All APPROVED bookings displayed in a weekly calendar grid, filterable by resource. |
| 7 | **Resource Availability View** | TAB02 | All resources listed with free/busy status for the current week. |
| 8 | **Pending Booking Approvals** | TAB03 | All PENDING bookings with Approve/Reject actions — timetable manager acts as booking reviewer. |

> ✅ All 3 timetable manager tabs in `PortalTabContent.jsx` are currently empty `RolePanel` stubs. They are **not Member 4's feature code** — they are blank placeholders waiting to be filled in by Member 2.

---

## 2. What Already Exists

### Database (no changes needed by Member 2 to existing tables)

- ✅ `resources` table — `id`, `name`, `type`, `available`, `created_at`, `updated_at`
- ✅ `bookings` table — `id`, `user_id`, `resource_id`, `start_time`, `end_time`, `status`, `created_at`, `updated_at`
- ✅ `users` table — includes all roles

> **New migration needed**: `bookings` is missing `purpose`, `expected_attendees`, and `rejection_reason`. These will be added via `V12__booking_enhancements.sql` (V11 is current highest).

### Backend Placeholder Package (already created, empty)

```
smartcampus/src/main/java/com/it3030/smartcampus/member2-bookings-management/
  controller/   placeholder.txt
  dto/          placeholder.txt
  exception/    placeholder.txt
  model/        placeholder.txt
  repository/   placeholder.txt
  security/     placeholder.txt  ← Member 2 will NOT use this folder
  service/      placeholder.txt
```

> ⚠️ Java `package` declarations cannot use hyphens. Use: `package com.it3030.smartcampus.member2.controller;`

### Frontend Placeholder Folder (already created, empty)

```
frontend/src/member2-bookings-management/
  components/   placeholder.txt
  pages/        placeholder.txt
  services/     placeholder.txt
  utils/        placeholder.txt
```

### Tab Slots Reserved for Member 2

From `TopNavHeader.jsx` (already written by Member 4 — Member 2 does not edit labels):

| Role | Tab Key | Label Already Set |
|------|---------|------------------|
| `student` | TAB03 | `"Member 2"` ✅ |
| `lecturer` | TAB03 | `"Member 2"` ✅ |

From `PortalTabContent.jsx` — TAB03 for student/lecturer currently renders a blank `RolePanel` stub. Member 2 fills in **only** these stubs.

### Admin Tab Situation

All admin tabs (TAB01–TAB05) are fully used by Member 4:
- TAB01 → Home, TAB02 → Activity, TAB03 → User Management, TAB04 → Role Management, TAB05 → Recovery Tickets

**Member 2 does NOT add a new admin tab** (that would require editing Member 4's files).

> ✅ **Solution**: Admin reviews booking requests through a **dedicated API** (accessible via Postman/Swagger or a minimal standalone page at `/bookings-admin`). Admin can still use the system — they just won't have a portal tab. This is acceptable since the backend REST API fulfils the requirement.

---

## 3. What Member 2 Builds (Self-Contained)

### 3.1 Database Migration

**New file**: `smartcampus/src/main/resources/db/migration/V12__booking_enhancements.sql`

```sql
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS purpose VARCHAR(500);
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS expected_attendees INTEGER;
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS rejection_reason TEXT;

CREATE INDEX IF NOT EXISTS idx_bookings_resource_status
    ON bookings(resource_id, status);
```

---

### 3.2 Backend – Spring Boot

All files go under package `com.it3030.smartcampus.member2.*`.

#### Models

| File | Description |
|------|-------------|
| `Booking.java` | JPA entity → `bookings` table. FK to `UserAccount` (Member 4, `Integer` id, `@ManyToOne`, read-only reference). FK to `Resource`. Fields: `startTime`, `endTime`, `status` (enum), `purpose`, `expectedAttendees`, `rejectionReason`. |
| `Resource.java` | JPA entity → `resources` table. Fields: `id`, `name`, `type`, `available`. No FK complications. |
| `BookingStatus.java` | Enum: `PENDING`, `APPROVED`, `REJECTED`, `CANCELLED` |

> ✅ **`UserAccount.java` is from Member 4's package — Member 2 uses it as a read-only `@ManyToOne` reference. No editing of that file.**

#### DTOs

| File | Description |
|------|-------------|
| `CreateBookingRequest.java` | Input: `resourceId`, `startTime`, `endTime`, `purpose`, `expectedAttendees` |
| `BookingResponse.java` | Output: full booking details — id, resource name, user id, status, purpose, times, rejectionReason |
| `ApproveRejectRequest.java` | Input for admin: optional `rejectionReason` |
| `ResourceResponse.java` | Output: resource dropdown item |

#### Repository

| File | Description |
|------|-------------|
| `BookingRepository.java` | Custom JPQL query for conflict check: overlapping APPROVED bookings on same resource |
| `ResourceRepository.java` | `findAll()` and `findByAvailable(true)` |

#### Service

| File | Description |
|------|-------------|
| `BookingService.java` | All booking business logic: create (with conflict check), approve, reject, cancel, list mine, list all |
| `ResourceService.java` | List available resources |

#### Controller

| File | Description |
|------|-------------|
| `BookingController.java` | All REST endpoints (see Section 4). Uses `@PreAuthorize` for role enforcement — **no edits to `SmartCampusSecurityConfig.java`**. |
| `ResourceController.java` | `GET /api/member2/resources` |

#### Exception

| File | Description |
|------|-------------|
| `BookingConflictException.java` | Thrown on time overlap → caught by Member 4's global `ApiExceptionHandler` (which handles `IllegalArgumentException` globally). Member 2 extends `RuntimeException` from `IllegalArgumentException` so it's auto-handled. |

---

### 3.3 Frontend – React

All files go under `frontend/src/member2-bookings-management/`.

#### Services

| File | Description |
|------|-------------|
| `bookingApi.js` | Uses `requestJson()` from `frontend/src/services/apiClient.js`. No Axios. Covers: get resources, create booking, get mine, cancel, approve, reject, list all, list PENDING. |

#### Components

| File | Who Sees It | Description |
|------|-------------|-------------|
| `BookingRequestForm.jsx` | Student, Lecturer | Form: resource dropdown, date, start/end time, purpose, attendees. Shows 409 conflict error clearly. |
| `MyBookingsList.jsx` | Student, Lecturer | Card/table list of own bookings with status badges. Cancel button on APPROVED ones. |
| `BookingStatusBadge.jsx` | Shared | Colour-coded badge: PENDING (yellow), APPROVED (green), REJECTED (red), CANCELLED (grey) |
| `BookingPanel.jsx` | Student, Lecturer | Parent wrapper combining `BookingRequestForm` + `MyBookingsList` — plugged into TAB03 stub for student/lecturer. |
| `TimetableWeeklyGrid.jsx` | Timetable Manager | Weekly calendar grid of all APPROVED bookings, filterable by resource. Plugged into TAB01 stub. |
| `ResourceAvailabilityView.jsx` | Timetable Manager | Lists all resources with their weekly booking status (free/busy). Plugged into TAB02 stub. |
| `PendingApprovalsPanel.jsx` | Timetable Manager | Table of all PENDING bookings with Approve / Reject buttons and reject reason input. Plugged into TAB03 stub. |

> ✅ No `AdminBookingPanel` needed — timetable manager handles approvals via TAB03.

#### Files Member 2 Will Edit (Stub Slots Only)

**`frontend/src/components/PortalTabContent.jsx`** — Member 2 fills in **only** these five stub blocks:

```jsx
import BookingPanel from "../member2-bookings-management/components/BookingPanel";
import TimetableWeeklyGrid from "../member2-bookings-management/components/TimetableWeeklyGrid";
import ResourceAvailabilityView from "../member2-bookings-management/components/ResourceAvailabilityView";
import PendingApprovalsPanel from "../member2-bookings-management/components/PendingApprovalsPanel";

// student TAB03 stub
if (role === "student" && tab === "TAB03") {
    return <BookingPanel user={user} />;
}

// lecturer TAB03 stub
if (role === "lecturer" && tab === "TAB03") {
    return <BookingPanel user={user} />;
}

// timetable_manager TAB01 stub
if (role === "timetable_manager" && tab === "TAB01") {
    return <TimetableWeeklyGrid />;
}

// timetable_manager TAB02 stub
if (role === "timetable_manager" && tab === "TAB02") {
    return <ResourceAvailabilityView />;
}

// timetable_manager TAB03 stub
if (role === "timetable_manager" && tab === "TAB03") {
    return <PendingApprovalsPanel user={user} />;
}
```

> ✅ Only the 5 designated blank stub blocks are replaced. No other line in that file is touched.

---

## 4. API Endpoints

All under `/api/member2`.

| Method | Path | Access | Description | Responses |
|--------|------|--------|-------------|-----------|
| GET | `/api/member2/resources` | Authenticated | List available resources | `200` |
| POST | `/api/member2/bookings` | Student, Lecturer | Create booking request | `201`, `409` conflict |
| GET | `/api/member2/bookings/mine` | Authenticated | My booking history | `200` |
| GET | `/api/member2/bookings` | Admin, Timetable Manager | All bookings (filterable by status/resource) | `200` |
| GET | `/api/member2/bookings/pending` | Timetable Manager | All PENDING bookings only | `200` |
| GET | `/api/member2/bookings/weekly` | Timetable Manager | All APPROVED bookings for week view | `200` |
| PATCH | `/api/member2/bookings/{id}/approve` | Admin, Timetable Manager | Approve a booking | `200` |
| PATCH | `/api/member2/bookings/{id}/reject` | Admin, Timetable Manager | Reject with optional reason | `200` |
| PATCH | `/api/member2/bookings/{id}/cancel` | Owner | Cancel own APPROVED booking | `200` |

---

## 5. Security Strategy (Zero Config File Edits)

Member 2 uses `@PreAuthorize` annotations on each controller method. This works because Member 4 already added `@EnableMethodSecurity` to `SmartCampusSecurityConfig.java`.

```java
// BookingController.java — examples

@GetMapping
@PreAuthorize("hasAnyRole('ADMIN', 'TIMETABLE_MANAGER')")
public List<BookingResponse> getAllBookings(...) { ... }

@GetMapping("/pending")
@PreAuthorize("hasRole('TIMETABLE_MANAGER')")
public List<BookingResponse> getPendingBookings(...) { ... }

@GetMapping("/weekly")
@PreAuthorize("hasRole('TIMETABLE_MANAGER')")
public List<BookingResponse> getWeeklyBookings(...) { ... }

@PostMapping
@PreAuthorize("hasAnyRole('STUDENT', 'LECTURER')")
public BookingResponse createBooking(...) { ... }

@PatchMapping("/{id}/approve")
@PreAuthorize("hasAnyRole('ADMIN', 'TIMETABLE_MANAGER')")
public BookingResponse approveBooking(...) { ... }

@PatchMapping("/{id}/reject")
@PreAuthorize("hasAnyRole('ADMIN', 'TIMETABLE_MANAGER')")
public BookingResponse rejectBooking(...) { ... }

@PatchMapping("/{id}/cancel")
@PreAuthorize("isAuthenticated()")
public BookingResponse cancelBooking(...) { ... }
```

> ✅ **`SmartCampusSecurityConfig.java` is never edited.**

---

## 6. Conflict Check Logic

Applied in `BookingService.java` **before** saving a new booking:

```
SELECT * FROM bookings
WHERE resource_id = :resourceId
  AND status = 'APPROVED'
  AND start_time < :newEndTime
  AND end_time > :newStartTime
```

If any row is found → throw `BookingConflictException` (which extends `IllegalArgumentException`) → Member 4's `ApiExceptionHandler` returns `HTTP 400`. To get a clean `HTTP 409`, Member 2's `BookingController` catches it manually and returns `ResponseEntity.status(409)`.

> **Note**: PENDING requests don't block new requests. Only APPROVED bookings lock the slot.

---

## 7. Testing Plan

### Backend Tests

| File | Location | What It Tests |
|------|----------|---------------|
| `BookingControllerTest.java` | `src/test/.../member2/controller/` | Create (201), conflict (409), approve, reject, cancel, list |
| `BookingServiceTest.java` | `src/test/.../member2/service/` | Conflict detection logic (unit test, no HTTP) |

### Frontend Tests

| File | What It Tests |
|------|---------------|
| `bookingApi.test.js` | Mock `requestJson` — create booking, list mine, cancel |
| `BookingPanel.test.jsx` | Renders form and list without crashing |

---

## 8. File Creation Checklist

### Backend (all in `member2` package)
- [ ] `V12__booking_enhancements.sql`
- [ ] `Booking.java`
- [ ] `Resource.java`
- [ ] `BookingStatus.java`
- [ ] `CreateBookingRequest.java`
- [ ] `BookingResponse.java`
- [ ] `ApproveRejectRequest.java`
- [ ] `ResourceResponse.java`
- [ ] `BookingRepository.java`
- [ ] `ResourceRepository.java`
- [ ] `BookingService.java`
- [ ] `ResourceService.java`
- [ ] `BookingController.java`
- [ ] `ResourceController.java`
- [ ] `BookingConflictException.java`
- [ ] `BookingControllerTest.java`
- [ ] `BookingServiceTest.java`

### Frontend (all in `member2-bookings-management/`)
- [ ] `bookingApi.js`
- [ ] `BookingRequestForm.jsx`
- [ ] `MyBookingsList.jsx`
- [ ] `BookingStatusBadge.jsx`
- [ ] `BookingPanel.jsx` (student + lecturer wrapper)
- [ ] `TimetableWeeklyGrid.jsx` (timetable manager TAB01)
- [ ] `ResourceAvailabilityView.jsx` (timetable manager TAB02)
- [ ] `PendingApprovalsPanel.jsx` (timetable manager TAB03)
- [ ] `bookingApi.test.js`
- [ ] `BookingPanel.test.jsx`

### Shared File Edits (Member 2 stub slots only)
- [ ] `PortalTabContent.jsx` — fill in 5 stubs: `student TAB03`, `lecturer TAB03`, `timetable_manager TAB01`, `timetable_manager TAB02`, `timetable_manager TAB03`

---

## 9. What Member 2 Will NOT Touch

| File | Reason |
|------|--------|
| `SmartCampusSecurityConfig.java` | Uses `@PreAuthorize` instead |
| `TopNavHeader.jsx` | Tab labels already set (`"Member 2"`); no new tab added for admin |
| `MainLayout.jsx` | No changes needed |
| `App.jsx` | No new routes needed |
| `authService.js` | Member 2 uses `bookingApi.js` instead |
| `NotificationService.java` | Not integrating booking notifications (to avoid dependency) |
| Any Member 4 component/service file | Fully isolated |

---

## 10. Tab Stub Summary

All stub slots Member 2 fills in `PortalTabContent.jsx`:

| Role | Tab | Component | Type |
|------|-----|-----------|------|
| `student` | TAB03 | `BookingPanel` | Core |
| `lecturer` | TAB03 | `BookingPanel` | Core |
| `timetable_manager` | TAB01 | `TimetableWeeklyGrid` | Core |
| `timetable_manager` | TAB02 | `ResourceAvailabilityView` | Core |
| `timetable_manager` | TAB03 | `PendingApprovalsPanel` | Core |
