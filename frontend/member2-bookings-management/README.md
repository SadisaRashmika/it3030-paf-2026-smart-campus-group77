# Member 2 – Bookings Management: Frontend Reference

## Component Overview

```
frontend/member2-bookings-management/
├── services/
│   └── bookingService.js          ← All Axios API calls
│
├── components/
│   ├── BookingForm.jsx            ← User booking request form
│   └── BookingForm.css
│
└── pages/
    ├── MyBookingsPage.jsx         ← User's booking history + cancel
    ├── MyBookingsPage.css
    ├── AdminBookingDashboard.jsx  ← Admin approval/rejection panel
    └── AdminBookingDashboard.css
```

## How to Use

### 1. Install Axios (if not already)

```bash
npm install axios
```

### 2. BookingForm (User)

```jsx
import BookingForm from './member2-bookings-management/components/BookingForm';

// With a dropdown of resources fetched from your API:
<BookingForm
  resources={[{ id: 1, name: 'Lab 101', location: 'Block A' }]}
  userId={currentUser.id}
  onSuccess={() => navigate('/my-bookings')}
/>

// Or minimal (manual resource ID input):
<BookingForm userId={1} />
```

### 3. MyBookingsPage (User)

```jsx
import MyBookingsPage from './member2-bookings-management/pages/MyBookingsPage';

<MyBookingsPage userId={currentUser.id} />
```

### 4. AdminBookingDashboard (Admin)

```jsx
import AdminBookingDashboard from './member2-bookings-management/pages/AdminBookingDashboard';

<AdminBookingDashboard />
```

## API Base URL

Configured in `services/bookingService.js`:

```js
const API_URL = 'http://localhost:8081/api/bookings';
```

Update this to your deployed backend URL for staging/production.

## Status Colour Coding

| Status    | Colour          |
|-----------|-----------------|
| PENDING   | 🟡 Amber/Yellow |
| APPROVED  | 🟢 Green        |
| REJECTED  | 🔴 Red          |
| CANCELLED | ⚫ Grey         |

## Testing Checklist

- [ ] Submit a booking via `BookingForm` → appears as PENDING in `MyBookingsPage`
- [ ] Submit conflicting booking → see amber "time slot unavailable" warning
- [ ] Admin sees pending request in `AdminBookingDashboard` at top of list
- [ ] Admin clicks Approve → status turns green in both views
- [ ] User cancels APPROVED booking → status turns grey
- [ ] Admin approves two conflicting pending bookings → second gets inline ⚠️ conflict error
