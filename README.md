# IT3030 PAF 2026 - Smart Campus Operations Hub (Group 77)

Spring Boot REST API + React web client for the IT3030 group coursework scenario.

This repository currently contains the most complete implementation in the Member 4 scope:
- Notifications
- OAuth/authentication improvements
- Role-protected admin operations
- Account recovery workflow with temporary password
- Profile picture support and rendering

## Tech Stack

- Backend: Java 21, Spring Boot 3.5, Spring Security, OAuth2 client, JPA/Hibernate, Flyway
- Frontend: React 18, React Router, Vite 5, Tailwind CSS
- Database: PostgreSQL
- CI: GitHub Actions

## Repository Structure

- `smartcampus` - Spring Boot backend
- `frontend` - React client
- `docs` - assignment documentation (member contributions, endpoint catalog, architecture notes)
- `.github/workflows/ci.yml` - build/test workflow

## Current Documentation

- Member contributions: [docs/team_contributions.md](docs/team_contributions.md)
- Endpoint list: [docs/api_endpoints.md](docs/api_endpoints.md)
- Architecture notes: [docs/architecture_diagrams.md](docs/architecture_diagrams.md)
- Local setup reference: [docs/setup_instructions.md](docs/setup_instructions.md)

## Prerequisites

- JDK 21
- Node.js 20+
- PostgreSQL 14+

## Backend Configuration

Backend reads config from:
- `smartcampus/src/main/resources/application.properties`
- optional `.env.properties` files (imported via Spring config import)

Important defaults currently used:
- Backend port: `8081`
- Frontend base URL for OAuth redirect: `http://localhost:5173`
- DB URL: `jdbc:postgresql://localhost:5432/smartcampus_db`
- DB username: `postgres`
- DB password fallback: `UniHelp123`
- Admin seed user ID: `ADMIN001`
- Admin seed email: `koffy.doggy@gmail.com`
- Admin seed password: `12345`

Google OAuth config is environment-driven:
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`

Mail config is environment-driven:
- `EMAIL_USER`
- `EMAIL_PASS`
- `MAIL_ENABLED`

## Quick Start (Local)

### 1) Create database

Create PostgreSQL DB:

```sql
CREATE DATABASE smartcampus_db;
```

### 2) Start backend

From repository root (recommended command):

```powershell
& ".\smartcampus\mvnw.cmd" -f ".\smartcampus\pom.xml" spring-boot:run
```

### 3) Start frontend

In a new terminal:

```powershell
Set-Location ".\frontend"
npm install
npm run dev
```

Vite runs on `http://localhost:5173` and proxies API/OAuth routes to backend `8081`.

## Build Commands

### Frontend build

```powershell
Set-Location ".\frontend"
npm run build
```

### Backend package (skip tests)

```powershell
& ".\smartcampus\mvnw.cmd" -f ".\smartcampus\pom.xml" -DskipTests package
```

## Test Commands (Verified)

### Frontend tests

```powershell
Set-Location ".\frontend"
npm test
```

### Backend tests from backend folder

```powershell
Set-Location ".\smartcampus"
npm test
```

### Backend tests from repository root

```powershell
npm run backend:test
```

or directly:

```powershell
& ".\smartcampus\mvnw.cmd" -f ".\smartcampus\pom.xml" test
```

Important: running `./mvnw.cmd test` from repository root fails because the Maven wrapper file exists under `smartcampus/`, not at root.

## CI Workflow

GitHub Actions workflow: [\.github/workflows/ci.yml](.github/workflows/ci.yml)

Jobs:
- Backend test: `./smartcampus/mvnw -f smartcampus/pom.xml test`
- Frontend build: `npm ci` + `npm run build` in `frontend`

## Member 4 Functional Scope (Implemented)

### Authentication and OAuth improvements
- Login/logout/session APIs
- Forgot-password OTP flow
- OAuth2/OIDC integration with role-mapped access
- Prevent existing DB names from being overwritten by Google profile names

### Notifications
- Login success notifications
- Password change notifications
- Notification list/read/read-all/delete
- Notification panel in navigation (including admin visibility)

### Account recovery
- Public recovery request submission with ID photo evidence
- Admin approve/reject recovery requests
- Temporary password issuance with expiry handling

### Profile picture
- Upload and persist profile picture (data URL)
- Navbar avatar rendering with initials fallback
- Admin activity list avatars with initials fallback

### Member 4 test coverage currently included
- Frontend unit test: `frontend/src/services/authService.test.js`
- Backend unit test: `smartcampus/src/test/java/com/it3030/smartcampus/member4/controller/AuthControllerTest.java`
- Spring context test: `smartcampus/src/test/java/com/it3030/smartcampus/SmartcampusApplicationTests.java`

### Reliability fix included
- Member 4 legacy API wrapper now uses the active session-aware frontend API client (`frontend/src/member4-notifications-oauth/services/member4Api.js`), avoiding mixed client behavior.

## API Summary

A detailed endpoint table is maintained in [docs/api_endpoints.md](docs/api_endpoints.md).

## Known Gaps / Ongoing Work

- Member 1, Member 2, Member 3 modules still need final integration and documentation completion in this repository.
- Automated test coverage for Member 4 can be expanded further for viva evidence.

## AI Usage Disclosure

AI-assisted coding tools were used during development for implementation support, debugging, and documentation drafting. Final code decisions, validation, and integration were performed by the team.
