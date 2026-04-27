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

6. Frontend structure cleanup
- Removed stale/unrouted Member 4 page files and duplicate legacy component files
- Kept page-first flow in main `src/pages` routing path and removed obsolete dead-file paths

7. Profile picture validation hardening
- Added backend server-side validation for profile-picture data URL format/type and size limit (2MB)

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
	- `frontend/src/member4-notifications-oauth/components/AdminUsersPanel.test.jsx`
- Backend test files:
	- `smartcampus/src/test/java/com/it3030/smartcampus/member4/controller/AuthControllerTest.java`
	- `smartcampus/src/test/java/com/it3030/smartcampus/member4/controller/NotificationControllerTest.java`
	- `smartcampus/src/test/java/com/it3030/smartcampus/member4/controller/RecoveryRequestControllerTest.java`

Current observed run summary (local):
- Frontend: 2 files, 4 tests passing
- Backend: 10 tests passing

### Command/Execution Clarification Added

- Documented that backend tests should run via:
	- backend folder: `npm test`
	- repository root: `npm run backend:test` or `smartcampus\mvnw.cmd -f smartcampus\pom.xml test`
- Clarified why plain `./mvnw.cmd test` fails at repository root (wrapper is under `smartcampus/`).

### CI Update Added

- Frontend CI workflow now runs tests before build:
	- `npm run test:run`
	- `npm run build`

---

## Member 1 - Enhanced Resource Management & Analytics

### Summary

Implemented a comprehensive real-world resource management system with advanced features including resource categorization, location management, maintenance tracking, rating systems, and detailed analytics. This module provides enterprise-level resource inventory management with modern UI/UX.

### Backend Contributions

1. **Enhanced Resource Models**
- ResourceCategory system with color coding and icons
- ResourceLocation with building/floor/room details and amenities
- EnhancedResource with comprehensive metadata (capacity, equipment, maintenance status)
- ResourceRating system with 5-star ratings and review text
- ResourceMaintenance with scheduling, tracking, and cost management
- ResourceAnalytics for usage statistics and performance metrics

2. **Advanced Repository Layer**
- Complex queries for resource filtering and search
- Analytics data aggregation and reporting
- Maintenance scheduling and status tracking
- Rating aggregation and sentiment analysis
- Location-based resource discovery

3. **Comprehensive Service Layer**
- Resource lifecycle management (create, update, delete, status tracking)
- Maintenance workflow automation (schedule, start, complete, cancel)
- Rating system with user feedback management
- Analytics generation (daily, monthly, custom date ranges)
- Resource utilization tracking and reporting

4. **RESTful API Controllers**
- Full CRUD operations for all entities
- Advanced filtering, searching, and pagination
- Role-based access control for admin operations
- Analytics endpoints with dashboard data
- Maintenance workflow management endpoints

5. **Database Schema Enhancements**
- Migration V16 with 7 new tables and comprehensive relationships
- Optimized indexes for performance
- Sample data with realistic campus resources
- Foreign key constraints and data integrity

### Frontend Contributions

1. **Modern Component Architecture**
- EnhancedResourceCard with rich resource information display
- ResourceAnalyticsDashboard with interactive charts and metrics
- ResourceMaintenancePanel with kanban board and calendar views
- ResourceRatingSystem with star ratings and review management
- Advanced search and filtering components

2. **Interactive Dashboard Pages**
- Resource inventory management with grid/list/table views
- Real-time analytics dashboard with charts and KPIs
- Maintenance scheduling and tracking interface
- Resource rating and review system
- Advanced filtering and search capabilities

3. **Professional UI/UX Features**
- Responsive design for all screen sizes
- Modern card-based layouts with hover effects
- Interactive data visualization with Recharts
- Modal dialogs for CRUD operations
- Status badges and progress indicators
- Loading states and error handling

4. **Service Layer Integration**
- Enhanced API service with comprehensive method coverage
- Authentication integration with authService
- Error handling and response processing
- Data caching and performance optimization

### Key Features Implemented

1. **Resource Management**
- Multi-category resource organization
- Location-based resource discovery
- Equipment and amenity tracking
- Capacity and availability management
- Image support for resources

2. **Maintenance System**
- Scheduled maintenance with reminders
- Technician assignment and tracking
- Cost management and reporting
- Emergency maintenance workflows
- Maintenance history and analytics

3. **Rating & Review System**
- 5-star rating system with decimal precision
- Text reviews with sentiment analysis
- User rating aggregation and statistics
- Review filtering and search
- Rating trends and analytics

4. **Analytics & Reporting**
- Resource utilization metrics
- Booking and usage statistics
- Revenue tracking and reporting
- Performance dashboards
- Custom date range analytics

5. **Advanced Search & Filtering**
- Multi-criteria resource search
- Category and location filtering
- Status-based filtering
- Real-time search results
- Sorting options (name, rating, usage, etc.)

### Member 1 Endpoint Count Evidence

Member 1 implemented comprehensive REST APIs with 40+ endpoints across 6 controllers:
- GET: Examples include `/api/member1/enhanced-resources`, `/api/member1/categories`, `/api/member1/analytics/dashboard`
- POST: Examples include `/api/member1/enhanced-resources`, `/api/member1/maintenance`, `/api/member1/ratings`
- PUT: Examples include `/api/member1/enhanced-resources/{id}`, `/api/member1/categories/{id}`
- PATCH: Examples include `/api/member1/maintenance/{id}/start`, `/api/member1/maintenance/{id}/complete`
- DELETE: Examples include `/api/member1/enhanced-resources/{id}`, `/api/member1/categories/{id}`

### Key Files (Member 1)

- Backend models/services/controllers under:
	- `smartcampus/src/main/java/com/it3030/smartcampus/member1/**`
- Database migration:
	- `smartcampus/src/main/resources/db/migration/V16__member1_enhanced_resources.sql`
- Frontend components/pages/services under:
	- `frontend/src/member1-facilities-assets/**`
- Shared integration points:
	- `frontend/src/services/authService.js`
	- `frontend/src/layouts/MainLayout.jsx`

### Database Schema Enhancements

- **resource_categories**: Category management with colors and icons
- **resource_locations**: Location hierarchy with building/floor/room details
- **enhanced_resources**: Comprehensive resource management with metadata
- **resource_ratings**: User rating and review system
- **resource_maintenance**: Maintenance scheduling and tracking
- **resource_analytics**: Usage statistics and performance metrics

### Testing Evidence Added (Member 1)

- Comprehensive sample data for testing all features
- Database constraints and validation testing
- API endpoint integration testing
- Frontend component interaction testing
- Analytics data generation and reporting testing

### Real-World Features

1. **Enterprise Resource Management**
- Multi-tenant resource categorization
- Location-based resource discovery
- Equipment and amenity tracking
- Capacity management and utilization

2. **Maintenance Operations**
- Preventive maintenance scheduling
- Emergency maintenance workflows
- Technician assignment and tracking
- Cost management and budgeting

3. **User Experience Enhancement**
- Interactive resource discovery
- Rating and review system
- Real-time availability status
- Mobile-responsive design

4. **Business Intelligence**
- Resource utilization analytics
- Performance metrics and KPIs
- Revenue tracking and reporting
- Trend analysis and forecasting

### Current Status

- Full resource management lifecycle implemented
- Maintenance workflows operational
- Rating and review system functional
- Analytics dashboard with real-time data
- Professional UI/UX with modern design patterns

## Member 2 - Booking Management, Timetable, and Resource Availability

### Summary

Implemented the full Member 2 module scope around booking lifecycle management, approval workflow, conflict prevention, weekly timetable visibility, and resource inventory/availability management across backend + frontend.

### Backend Contributions

1. Booking lifecycle APIs
- Create booking request
- List own bookings
- Cancel booking (owner)
- Approve/reject booking (timetable manager or admin)

2. Timetable and approvals APIs
- List pending bookings for review
- List approved bookings for a requested weekly date range

3. Resource management APIs
- List available resources for authenticated users
- Create/update/delete resources for admin and resource administator roles

4. Conflict prevention and validation logic
- Overlap check against already approved bookings
- 409 conflict response handling on booking collisions
- Time-range and past-date validation for create-booking flow

5. Database migration and schema support
- Added booking enhancement migration `V12__booking_enhancements.sql`
- Added `purpose`, `expected_attendees`, and `rejection_reason` fields to `bookings`
- Added index for approved booking conflict checks

### Frontend Contributions

1. Booking experience for student/lecturer
- Booking request form with resource/time/purpose/attendees inputs
- My booking history view with status badges and cancel actions

2. Timetable manager experience
- Weekly master timetable view with resource filtering
- Pending approvals panel with approve/reject flow

3. Resource administator experience
- Resource inventory CRUD panel
- Resource availability status panel

4. Timetable UX improvements
- Rolling 7-day window and weekly navigation controls
- Previous week / next week / today actions
- Live refresh behavior and approved-slot rendering improvements

5. Frontend UI consistency pass
- Aligned Member 2 UI typography and palette with shared portal style
- Removed decorative emojis from Member 2 text labels

### Member 2 Endpoint Count Evidence

Member 2 implemented more than four endpoints and used multiple HTTP methods:
- GET: examples include `/api/member1/resources`, `/api/member2/bookings/mine`, `/api/member2/bookings/weekly`
- POST: examples include `/api/member2/bookings`, `/api/member1/resources`
- PATCH: examples include `/api/member2/bookings/{id}/approve`, `/api/member2/bookings/{id}/reject`, `/api/member2/bookings/{id}/cancel`
- PUT: examples include `/api/member1/resources/{id}`
- DELETE: examples include `/api/member1/resources/{id}`

### Key Files (Member 2)

- Backend controllers/services/repositories under:
	- `smartcampus/src/main/java/com/it3030/smartcampus/member2/**`
- Backend migration:
	- `smartcampus/src/main/resources/db/migration/V12__booking_enhancements.sql`
- Frontend module components/pages/services under:
	- `frontend/src/member2-bookings-management/**`
- Shared integration points:
	- `frontend/src/components/PortalTabContent.jsx`
	- `frontend/src/components/TopNavHeader.jsx`

### Testing Evidence Added (Member 2)

- Backend test file:
	- `smartcampus/src/test/java/com/it3030/smartcampus/member2/service/BookingServiceTest.java`

Current observed run summary (local):
- Member 2 backend service tests: passing
- Frontend production build: passing

## Member 3 - Maintenance and Incident Ticketing

### Summary

Implemented the merged Member 3 ticketing module for reporting, tracking, assigning, and resolving maintenance and incident tickets.

### Backend Contributions

1. Ticket lifecycle APIs
- Create ticket
- List current user's tickets
- Retrieve single ticket with access checks
- Update ticket
- Delete ticket

2. Ticket admin workflows
- List all tickets for privileged users
- Update ticket status with reject/resolved transitions
- Assign technicians to tickets

3. Comment and attachment workflows
- Add comments to a ticket
- Edit and delete owned comments
- Preserve attachment history in ticket detail responses

4. Database and access support
- Ticketing tables and schema support for tickets, comments, and attachments
- Ticket administrator role support for privileged management actions

### Frontend Contributions

1. Student ticket dashboard
- Create-ticket modal
- Search and status filtering
- Ticket card grid and ticket detail flow

2. Admin ticket management
- All-ticket table with status summaries
- Technician assignment workflow
- Delete and view actions for privileged users

3. Ticket detail and comment UX
- Status actions, rejection reason, and resolution notes
- Comment list and edit/delete controls
- Attachment viewing in detail panel

4. Frontend styling updates
- Aligned ticketing UI with the shared amber/slate portal theme
- Restored emoji cues where they help distinguish ticket categories and locations
- Added internal scrolling to the create-ticket modal so actions remain reachable on smaller screens

### Member 3 Endpoint Count Evidence

Member 3 implemented more than four endpoints and used multiple HTTP methods:
- GET: examples include `/api/member3/tickets/my`, `/api/member3/tickets/all`, `/api/member3/tickets/{id}`
- POST: examples include `/api/member3/tickets`, `/api/member3/tickets/{id}/comments`
- PATCH: examples include `/api/member3/tickets/{id}/status`, `/api/member3/tickets/{id}/assign`
- PUT: examples include `/api/member3/tickets/{id}`, `/api/member3/tickets/{id}/comments/{commentId}`
- DELETE: examples include `/api/member3/tickets/{id}`, `/api/member3/tickets/{id}/comments/{commentId}`

### Key Files (Member 3)

- Backend controllers/services/models/repositories under:
	- `smartcampus/src/main/java/com/it3030/smartcampus/member3_ticketing/**`
- Frontend module components/services under:
	- `frontend/src/member3-ticketing/**`
- Shared integration points:
	- `frontend/src/components/PortalTabContent.jsx`
	- `frontend/src/components/TopNavHeader.jsx`
	- `frontend/src/layouts/MainLayout.jsx`

### Testing Evidence Added (Member 3)

- Frontend production build passes after the ticketing UI refresh.
- Backend package build passes with the merged ticketing module in place.

### Current Status

- Ticket Administrator owns the admin ticket management and technician assignment tabs.
- Student users keep the ticket creation and personal ticket history flow.


