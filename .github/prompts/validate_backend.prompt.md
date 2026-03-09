---
description: "Run full backend validation: compilation, tests, API contracts, and architecture check"
---

# Backend Validation Check

Run a comprehensive validation of the Warehouse Inventory backend.

## Checks to Perform

### 1. Compilation Check
- Run `./gradlew compileJava` to verify the project compiles
- Fix any compilation errors before proceeding

### 2. Test Suite
- Run `./gradlew test` to execute all JUnit tests
- Review test failures and categorize: unit test vs integration test

### 3. Architecture Compliance
Verify layered architecture is maintained:
- [ ] No business logic in controllers (controllers only call services)
- [ ] All multi-write service methods have @Transactional
- [ ] All entities match MySQL schema exactly
- [ ] All DTOs have Jakarta Validation annotations
- [ ] GlobalExceptionHandler catches all custom exceptions
- [ ] @CrossOrigin or CorsConfig allows React frontend origin

### 4. API Contract Validation
Verify each endpoint matches the frontend contract:

| Endpoint | Expected Response | DTO Class |
|----------|-------------------|-----------|
| GET /api/products | ProductDTO[] | ProductDTO |
| GET /api/products/{code} | ProductDTO | ProductDTO |
| POST /api/products/import | ProductDTO[] | ProductDTO |
| POST /api/import/products | ImportResultDTO | ImportResultDTO |
| POST /api/import/inventory | ImportResultDTO | ImportResultDTO |
| GET /api/inventory | InventoryLevelDTO[] | InventoryLevelDTO |
| GET /api/inventory/search?code=X | InventoryLevelDTO[] | InventoryLevelDTO |
| GET /api/inventory/locations | String[] | - |
| POST /api/transfers | {status, message} | TransferRequestDTO (input) |
| GET /api/dashboard | DashboardDTO | DashboardDTO |

### 5. Database Schema Check
- Verify entity field types match MySQL column types
- Verify unique constraints are modeled
- Verify foreign key relationships are correct

---

**Default behavior**: Run all checks and report findings.
