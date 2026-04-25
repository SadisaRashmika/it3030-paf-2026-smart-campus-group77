## SmartCampus Local Setup (Member 4)

### 1. Create PostgreSQL database

Run this once in pgAdmin Query Tool:

```sql
CREATE DATABASE smartcampus_db;
```

Use these connection values in your local environment:

- Host: `localhost`
- Port: `5432`
- Database: `smartcampus_db`
- Username: `postgres`
- Password: `UniHelp123`

### 2. Start backend on port 8081

From the `smartcampus` folder:

```powershell
.\mvnw.cmd spring-boot:run
```

From repository root (equivalent command):

```powershell
& ".\smartcampus\mvnw.cmd" -f ".\smartcampus\pom.xml" spring-boot:run
```

Note: `./mvnw.cmd ...` from repository root will fail because there is no wrapper file at root.

Flyway will automatically create tables on startup from:

- `smartcampus/src/main/resources/db/migration/V1__initial_schema.sql`
- `smartcampus/src/main/resources/db/migration/V12__booking_enhancements.sql` (Member 2 booking fields)

### 3. Open the UI (no browser auth popup)

Open this URL in your browser:

- `http://localhost:8081/ui/index.html`

### 4. Login credentials

Default admin credentials:

- Email: `koffy.doggy@gmail.com`
- Password: `12345`

### 5. Activation flow

1. Enter student/lecturer email and role.
2. Click **Send OTP**.
3. Read OTP from backend logs (console mail service).
4. Enter OTP + new password.
5. Click **Verify and Activate**.

### 6. Admin flow

After admin login, the same page shows user management with:

- Active / pending / suspicious status
- OTP request count
- Failed OTP count

### 7. Run tests (recommended commands)

Frontend tests:

```powershell
Set-Location ".\frontend"
npm test
```

Backend tests from backend folder:

```powershell
Set-Location ".\smartcampus"
npm test
```

Backend tests from repository root:

```powershell
npm run backend:test
```

or:

```powershell
& ".\smartcampus\mvnw.cmd" -f ".\smartcampus\pom.xml" test
```

---

## Member 2 Module Quick Run/Check

### Roles and Pages

- Student/Lecturer: booking request + booking history in portal bookings tab
- Timetable Manager: weekly timetable + pending approvals
- Resource Administator: resource inventory + availability views

### APIs used by Member 2 frontend

- `/api/member1/resources`
- `/api/member2/bookings`
- `/api/member2/bookings/mine`
- `/api/member2/bookings/pending`
- `/api/member2/bookings/weekly`

### Validation commands

Frontend build:

```powershell
Set-Location ".\frontend"
npm run build
```

Backend package:

```powershell
Set-Location ".\smartcampus"
.\mvnw.cmd -DskipTests package
```

---

## Member 3 Module Quick Run/Check

### Roles and Pages

- Student: create and track personal tickets in the portal ticket tab
- Ticket Administrator: manage all tickets, update status, assign technicians, and manage comments
- Admin: retains account and portal administration, but no longer handles ticket management tabs

### APIs used by Member 3 frontend

- `/api/member3/tickets`
- `/api/member3/tickets/my`
- `/api/member3/tickets/all`
- `/api/member3/tickets/assigned`
- `/api/member3/tickets/{id}`
- `/api/member3/tickets/{id}/status`
- `/api/member3/tickets/{id}/assign`
- `/api/member3/tickets/{id}/comments`

### Validation commands

Frontend build:

```powershell
Set-Location ".\frontend"
npm run build
```

Backend package:

```powershell
Set-Location ".\smartcampus"
.\mvnw.cmd -DskipTests package
```

Backend run check:

```powershell
Set-Location ".\smartcampus"
.\mvnw.cmd spring-boot:run
```

