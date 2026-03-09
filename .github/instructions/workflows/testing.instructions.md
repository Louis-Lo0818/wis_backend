---
applyTo: "src/test/**/*.java"
---

# Testing Guidelines

## Goals
- Ensure consistent test structure and naming
- Enforce proper mocking patterns
- Cover critical business logic paths

## Rules

1. **Test Naming**: `methodName_scenario_expectedBehavior`
   - ✅ `transfer_success_deductsSourceAndAddsDestination`
   - ❌ `testTransfer`, `test1`

2. **Unit Tests (Service)**:
   - Use `@ExtendWith(MockitoExtension.class)`
   - Mock repositories with `@Mock`, inject service with `@InjectMocks`
   - Use `when().thenReturn()` for stubbing, `verify()` for interaction checks
   - Test happy path + all exception paths

3. **Integration Tests (Controller)**:
   - Use `@SpringBootTest` + `@AutoConfigureMockMvc`
   - Use `MockMvc` to simulate HTTP requests
   - Assert HTTP status codes and JSON response body
   - Use H2 in-memory database (test profile)

4. **Minimum Coverage for TransferService**:
   - Successful transfer (quantity deducted, destination upserted, log created)
   - Product not found → 404
   - Source location not found → 404
   - Insufficient quantity → 400
   - New destination created on first transfer → inventory upserted

5. **Assertions**: Use JUnit 5 `assertEquals`, `assertThrows`, `assertDoesNotThrow`. Prefer specific assertions over generic `assertTrue`.

6. **Test Data**: Use `@Builder` pattern to construct test entities. Keep test data minimal and focused.
