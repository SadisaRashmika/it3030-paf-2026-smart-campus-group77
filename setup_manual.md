# SmartCampus Project Manual Setup Instructions

## Prerequisites
Before starting, ensure you have the following installed:
- **Java 21** (already installed on your system)
- **Node.js and npm** (required for frontend)
- **PostgreSQL** (required for backend database)

## Step 1: Set Up JAVA_HOME Environment Variable
Since I couldn't set this due to permission issues, you'll need to set it manually:

### Windows 10/11:
1. Press `Win + X` and select "System"
2. Click "Advanced system settings"
3. Click "Environment Variables"
4. Under "System variables", click "New"
5. Set:
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Java\jdk-21.0.10`
6. Click "OK" to save
7. Find "Path" in System variables and click "Edit"
8. Click "New" and add: `%JAVA_HOME%\bin`
9. Click "OK" to save all changes

### Alternative Method (if System variables don't work):
1. Open Command Prompt as Administrator
2. Run these commands:
```cmd
setx JAVA_HOME "C:\Program Files\Java\jdk-21.0.10" /M
setx Path "%JAVA_HOME%\bin;%Path%" /M
```
3. Close and reopen Command Prompt

### Verify JAVA_HOME:
Open Command Prompt and run:
```cmd
echo %JAVA_HOME%
java -version
```

## Step 2: Install PostgreSQL
1. Download PostgreSQL from https://www.postgresql.org/download/windows/
2. During installation:
   - Set password to: `UniHelp123` (as per project requirements)
   - Remember the port (default is 5432)
3. After installation, open pgAdmin or PostgreSQL command line
4. Create the database:
```sql
CREATE DATABASE smartcampus;
```

## Step 3: Install Frontend Dependencies
1. Open Command Prompt/Terminal
2. Navigate to the frontend directory:
```cmd
cd C:\Users\ASUS\Desktop\PAF\it3030-paf-2026-smart-campus-group77\frontend
```
3. Install dependencies:
```cmd
npm install
```

## Step 4: Install Backend Dependencies
1. Navigate to the smartcampus directory:
```cmd
cd C:\Users\ASUS\Desktop\PAF\it3030-paf-2026-smart-campus-group77\smartcampus
```
2. Install Maven dependencies:
```cmd
mvnw.cmd clean install
```

## Step 5: Start the Backend Server
1. In the smartcampus directory, run:
```cmd
mvnw.cmd spring-boot:run
```
2. The backend should start on `http://localhost:8081`

## Step 6: Start the Frontend Server
1. In a new terminal, navigate to the frontend directory:
```cmd
cd C:\Users\ASUS\Desktop\PAF\it3030-paf-2026-smart-campus-group77\frontend
```
2. Start the development server:
```cmd
npm run dev
```
3. The frontend should start on `http://localhost:5173`

## Step 7: Access the Application
1. Open your web browser
2. Navigate to: `http://localhost:8081/ui/index.html`
3. Use default admin credentials:
   - Email: `koffy.doggy@gmail.com`
   - Password: `12345`

## Step 8: User Activation (if needed)
1. Enter student/lecturer email and role
2. Click **Send OTP**
3. Read OTP from backend logs (console mail service)
4. Enter OTP + new password
5. Click **Verify and Activate**

## Troubleshooting JAVA_HOME Issues
If you're still getting the JAVA_HOME error, try these solutions:

### Solution 1: Use Full Java Path
Instead of relying on JAVA_HOME, use the full path to Java:
```cmd
"C:\Program Files\Java\jdk-21.0.10\bin\java.exe" -jar smartcampus/mvnw.cmd clean install
```

### Solution 2: Check Current JAVA_HOME
Verify what JAVA_HOME is currently set to:
```cmd
echo %JAVA_HOME%
```

### Solution 3: Set JAVA_HOME for Current Session
If you don't want to set it permanently:
```cmd
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.10
```

### Solution 4: Use Different Java Version
If you have multiple Java versions installed, try specifying the exact version:
```cmd
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.10
```

### Solution 5: Check Java Installation
Verify Java is installed correctly:
```cmd
"C:\Program Files\Java\jdk-21.0.10\bin\java.exe" -version
```

## Troubleshooting
- **Vite not recognized**: Try using `npx vite` instead of `vite`
- **Port conflicts**: Change the port numbers in application.properties if needed
- **Database connection issues**: Verify PostgreSQL is running and the database exists

## Project Structure
- **Backend**: Spring Boot Java application (port 8081)
- **Frontend**: React application with Vite (port 5173)
- **Database**: PostgreSQL with Flyway migrations

## Default Credentials
- **Admin Email**: koffy.doggy@gmail.com
- **Admin Password**: 12345