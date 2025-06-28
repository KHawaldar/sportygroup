## 🧪 1. Testing

### ✅ Simulated concurrent access tested with multiple threads

- `TicketConcurrencyTest.concurrentAssignmentOnlyOneSucceeds()` verifies that only one agent can assign to a ticket under concurrent access.
- Uses a thread pool to simulate two agents racing to assign the same ticket.
- Assertion ensures only one assignment succeeds, proving lock effectiveness.

---

### ✅ Test for lock TTL expiry after simulated crash

- `TicketConcurrencyTest.lockIsReleasedAfterSimulatedCrashByTTL()` simulates a lock being held and not released (i.e., application crash).
- Waits for the lease time to expire.
- Ensures a subsequent thread can acquire the lock, validating Redisson's TTL-based auto-unlock mechanism.

---

### ✅ End-to-end integration test using MockMvc

- `TicketControllerIntegrationTest.testCreateUpdateAndAssign()` covers:
    - Ticket creation
    - Status update (`IN_PROGRESS`)
    - Agent assignment (`agent001`)
- Validates the full request-response lifecycle using Spring’s `MockMvc`.
- Asserts correct HTTP status codes and expected JSON response fields.

---

### ✅ Validation scenario tested

- `TicketControllerIntegrationTest.whenUSerIdIs_Empty_thenReturnBadRequest()` ensures `@Valid` constraints are enforced.
- Returns `400 Bad Request` with a proper error message when `userId` is null.

---

### 💤 Disabled test for non-existent ticket update

- `whenUpdating_ticketNotFound()` (currently `@Disabled`) verifies proper `404 Not Found` when attempting to update a ticket that doesn't exist.
- Expected to return a JSON response with `statuscode: 404` and a descriptive message.

---

### ✅ Isolated Redis environment using Testcontainers

- Both integration and concurrency tests run against a real Redis instance provisioned via **Testcontainers**.
- Ensures test environment is aligned with production-like Redis behavior.
- No external Redis dependency required for test execution.
