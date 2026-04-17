# Member 2 – Bookings Management: Backend Reference

## Package Structure

```
com.it3030.smartcampus/
├── model/
│   ├── Booking.java          ← JPA entity (bookings table)
│   ├── BookingStatus.java    ← Enum: PENDING | APPROVED | REJECTED | CANCELLED
│   ├── Resource.java         ← Stub for Member 1's Resource entity
│   └── User.java             ← Stub for Member 4's User entity
│
├── dto/
│   ├── BookingRequestDTO.java  ← Inbound: resourceId, userId, startTime, endTime, purpose
│   └── BookingResponseDTO.java ← Outbound: flat view of a booking + resource + user info
│
├── repository/
│   ├── BookingRepository.java  ← JPA repo + overlap-detection JPQL query
│   ├── ResourceRepository.java ← Stub (Member 1 owns this)
│   └── UserRepository.java     ← Stub (Member 4 owns this)
│
├── service/
│   └── BookingService.java     ← All business logic lives here
│
├── controller/
│   └── BookingController.java  ← REST endpoints under /api/bookings
│
└── exception/
    ├── BookingConflictException.java  ← HTTP 409
    ├── ResourceNotFoundException.java ← HTTP 404
    └── GlobalExceptionHandler.java    ← @RestControllerAdvice
```

## REST API Endpoints

| Method | Path                      | Role      | Description                            |
|--------|---------------------------|-----------|----------------------------------------|
| POST   | /api/bookings             | USER      | Submit a booking request (→ PENDING)   |
| GET    | /api/bookings             | ADMIN     | All bookings                           |
| GET    | /api/bookings/pending     | ADMIN     | Only PENDING bookings                  |
| GET    | /api/bookings/my-bookings | USER      | Current user's bookings                |
| GET    | /api/bookings/{id}        | ANY       | Single booking by id                   |
| PUT    | /api/bookings/{id}/approve| ADMIN     | Approve PENDING → APPROVED             |
| PUT    | /api/bookings/{id}/reject | ADMIN     | Reject  PENDING → REJECTED             |
| PUT    | /api/bookings/{id}/cancel | USER/ADMIN| Cancel PENDING/APPROVED → CANCELLED   |

## Double-Booking Prevention

The overlap check query in `BookingRepository`:

```sql
SELECT b FROM Booking b
WHERE b.resource.id = :resourceId
  AND b.status = APPROVED
  AND b.startTime < :endTime   -- existing starts before new ends
  AND b.endTime   > :startTime -- existing ends after new starts
```

This catches all interval overlaps including partial overlaps and containment.
The check also re-runs at **approval time** to prevent race conditions when two
admins approve competing pending bookings simultaneously.

## application.properties (local dev)

```properties
server.port=8081
spring.datasource.url=jdbc:postgresql://localhost:5432/smartcampus
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

Change `ddl-auto` to `validate` before going to production.

## Integration Notes for Team

- **Member 1 (Facilities):** Replace `Resource.java` stub with your full entity.
  Ensure your table is named `resources`. Keep `ResourceRepository`.
- **Member 4 (Auth):** Replace `User.java` stub with your entity.
  In `BookingController.getMyBookings()`, replace the hardcoded `userId=1`
  with a `SecurityContextHolder` lookup once Spring Security is wired up.
  Un-comment the `@PreAuthorize` annotations.
