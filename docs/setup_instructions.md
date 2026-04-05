## SmartCampus Local Setup (Member 4)

### 1. Create PostgreSQL database

Run this once in pgAdmin Query Tool:

```sql
CREATE DATABASE smartcampus;
```

Use these connection values in your local environment:

- Host: `localhost`
- Port: `5432`
- Database: `smartcampus`
- Username: `postgres`
- Password: `UniHelp123`

### 2. Start backend on port 8081

From the `smartcampus` folder:

```powershell
.\mvnw.cmd spring-boot:run
```

Flyway will automatically create tables on startup from:

- `smartcampus/src/main/resources/db/migration/V1__initial_schema.sql`

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

