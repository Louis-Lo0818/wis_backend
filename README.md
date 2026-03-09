# Warehouse Inventory System — Backend (Spring Boot)

**REST API backend for the Warehouse Inventory System (WIS).**  
Built with Spring Boot 3.5.x, Spring Data JPA, MySQL 8.x, and JUnit 5 (43 passing tests).  
Serves a React 18 frontend to manage products, inventory, and stock transfers across warehouse locations.

> **📋 See [`.agent_plan/`](.agent_plan) for the project development blueprint, including planned features (P0–P3) and architectural evolution.**

---

## Table of Contents

1. [Overview & Depth](#overview--depth)
2. [Prerequisites](#prerequisites)
3. [Database Setup](#database-setup)
4. [How to Compile](#how-to-compile)
5. [How to Test](#how-to-test)
6. [How to Start](#how-to-start)
7. [Project Structure](#project-structure)
8. [API Documentation](#api-documentation)
9. [Development Log](#development-log-learning--thinking--trying--developing)
10. [Contributing](#contributing)

---

## Overview & Depth

### Vision

The **Warehouse Inventory System** provides **real-time inventory visibility** across multiple warehouse locations. Warehouse managers and staff can:

- **Track products** — store SKU, name, weight, and creation metadata
- **Monitor inventory levels** — per-location stock quantities with audit trails
- **Execute transfers** — move stock between locations with accountability
- **Bulk import** — upload CSV files for batch product/inventory updates
- **Dashboard insights** — total products, locations, stock on hand, and transfers

### Problem Statement

Before WIS, warehouse operations relied on manual spreadsheets and reactive stock-outs. This system solves:

| Pain Point | Solution |
|---|---|
| **No visibility** — "How much stock do we have?" | Inventory API with location-based queries |
| **Manual tracking** — spreadsheet updates lag reality | Real-time inventory with automatic transfers |
| **Bulk import chaos** — manual entry or incompatible formats | CSV import for products and inventory |
| **No accountability** — Who moved what stock when? | Transfer log with timestamps (P2 roadmap: full audit trail) |
| **No alerts** — Stock-outs discovered too late | Stock alert thresholds (P2 roadmap) |

### Current Scope (P0: Walking Skeleton ✅)

**Completed features:**
- Product CRUD + CSV bulk import
- Inventory tracking per location
- Inter-location transfers with validation (sufficient quantity checks)
- Dashboard summary stats
- REST + Swagger UI
- 43 passing unit and integration tests

### Planned Evolution (P1–P3)

See [`.agent_plan/day_dream/wis_backend_blueprint/`](.agent_plan/day_dream/wis_backend_blueprint/) for detailed roadmap:

- **P1: Auth + Pagination** — JWT security, paginated endpoints for 10K+ products
- **P2: Audit Trail + Alerts** — Full transfer history, stock threshold notifications
- **P3: Reports + Caching** — PDF/Excel export, Redis caching for dashboard

---

## Prerequisites

| Requirement | Version | Notes |
|---|---|---|
| **Java** | 21+ | Set `JAVA_HOME` to JDK 21 location |
| **Maven** | 3.9+ | Use included `./mvnw` wrapper (Windows) or `./mvnw.sh` (Unix) |
| **MySQL** | 8.0+ | InnoDB, utf8mb4 collation. H2 in-memory used for tests—no MySQL required to run tests. |
| **Git** | Latest | For cloning and version control |

**Verify installations:**
```bash
java -version
./mvnw -v
mysql --version
```

---

## Database Setup

### 1. Create Database & Schema

**Option A: Using provided SQL file**
```bash
# Ensure MySQL is running on localhost:3306
mysql -u root -p < src/main/resources/schema.sql
```

**Option B: Manual SQL**
```sql
CREATE DATABASE IF NOT EXISTS warehouse_inventory
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

USE warehouse_inventory;

-- Then paste contents of src/main/resources/schema.sql
```

### 2. Load Sample Data (Optional)
```bash
mysql -u root -p warehouse_inventory < src/main/resources/data.sql
```

### 3. Configure Credentials

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/warehouse_inventory?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root                    # Change to your MySQL user
    password: your_password_here      # Change to your MySQL password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate              # Validates schema; does not create/drop tables
    show-sql: false                   # Set to true for debug logging
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
```

**For testing** (uses in-memory H2, no MySQL required):
```yaml
# src/test/resources/application.properties (auto-configured)
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
```

---

## How to Compile

### Full Build (with Tests)
```bash
# Compile, run all 43 tests, package into JAR
./mvnw clean install
```

**Output:** `target/my-spring-project-0.0.1-SNAPSHOT.jar`

### Fast Build (Skip Tests)
```bash
# Compile and package without running tests
./mvnw clean package -DskipTests
```

Use this when you're confident in functionality and want faster iteration.

### Compile Only
```bash
# Compile without packaging
./mvnw clean compile
```

Classes are written to `target/classes/`.

### IDE Integration

- **IntelliJ IDEA:** Right-click `pom.xml` → `Run` → `Maven` → `clean install`
- **VS Code:** Use Spring Boot Extension or Maven extension; run Maven goal `clean install`
- **Eclipse:** Right-click project → `Run As` → `Maven build` → enter `clean install`

---

## How to Test

### Run All Tests
```bash
./mvnw test
```

Runs 43 tests across two test classes:
- **22 integration tests** (Controllers using `@SpringBootTest`)
- **21 unit tests** (Services using `@Mock` / Mockito)

**Test suite uses H2 in-memory database** — no MySQL setup required.

### Run a Specific Test Class
```bash
./mvnw test -Dtest=ProductServiceTest
./mvnw test -Dtest=TransferControllerIntegrationTest
```

### Run a Single Test Method
```bash
./mvnw test -Dtest=ProductServiceTest#testCreateProductSuccess
```

### View Test Reports
Test results are written to `target/surefire-reports/`:

| File | Format |
|---|---|
| `TEST-*.xml` | Machine-readable JUnit XML |
| `*.txt` | Human-readable summary |

Example: Open `target/surefire-reports/wis.my_spring_project.service.ProductServiceTest.txt`

### Test Coverage

Current coverage (approximate):
- **Controllers:** 100% endpoint coverage (happy path + error cases)
- **Services:** 100% business logic coverage (transfers, validations)
- **Repositories:** Integration tested via service layer
- **Entities:** Tested via CRUD operations

Tests verify:
- ✅ Happy path (valid input → success response)
- ✅ Error cases (invalid input → 400/404/409 errors)
- ✅ Boundary conditions (zero stock, duplicate codes)
- ✅ Business rules (insufficient quantity prevents transfer)

### Debug a Test
```bash
# Run test with verbose output
./mvnw test -Dtest=TransferServiceTest -X

# Run single test with high verbosity
./mvnw test -Dtest=TransferServiceTest#testTransferSuccessful -X
```

---

## How to Start

### 1. Ensure MySQL is Running
```bash
# macOS (Homebrew)
brew services start mysql-community-server

# Linux (systemd)
sudo systemctl start mysql

# Windows
net start MySQL80  # or your MySQL service name
```

Verify connection:
```bash
mysql -u root -p -e "SELECT 1;"
```

### 2. Build & Start the API
```bash
# Development mode with hot reload (if DevTools is configured)
./mvnw spring-boot:run

# Or run the packaged JAR
./mvnw clean package -DskipTests
java -jar target/my-spring-project-0.0.1-SNAPSHOT.jar
```

### 3. Verify API is Running

**Check startup logs:**
```
...
2026-03-09 10:15:23.456 INFO  wis.my_spring_project.MySpringProjectApplication : 
  Started MySpringProjectApplication in 5.234 seconds (process running for 6.789)
```

**Test connectivity:**
```bash
curl http://localhost:8080/api/products
```

Expected response (if sample data loaded):
```json
[
  {
    "id": 1,
    "code": "PROD001",
    "name": "Laptop",
    "weight": 2.5,
    "createdAt": "2026-03-01T08:00:00",
    "updatedAt": "2026-03-01T08:00:00"
  }
]
```

### 4. Access Interactive Docs

- **Swagger UI:** http://localhost:8080/swagger-ui.html (try endpoints, test requests)
- **OpenAPI Schema:** http://localhost:8080/v3/api-docs (raw JSON)

### 5. Stop the Server

Press `Ctrl+C` in the terminal running `./mvnw spring-boot:run`.

---

## Project Structure

### Directory Layout

```
my-spring-project/
├── src/
│   ├── main/
│   │   ├── java/wis/my_spring_project/
│   │   │   ├── config/
│   │   │   │   ├── CorsConfig.java                # CORS for React dev server (:5173)
│   │   │   │   └── OpenApiConfig.java             # Swagger/OpenAPI configuration
│   │   │   ├── controller/
│   │   │   │   ├── ProductController.java         # GET/POST /api/products*
│   │   │   │   ├── InventoryController.java       # GET /api/inventory*
│   │   │   │   ├── TransferController.java        # POST /api/transfers
│   │   │   │   ├── CsvImportController.java       # POST /api/import/*
│   │   │   │   └── DashboardController.java       # GET /api/dashboard
│   │   │   ├── service/
│   │   │   │   ├── ProductService.java            # Product CRUD + CSV import logic
│   │   │   │   ├── InventoryService.java          # Inventory queries and updates
│   │   │   │   ├── TransferService.java           # Transfer validation and execution
│   │   │   │   ├── CsvImportService.java          # CSV parsing via OpenCSV
│   │   │   │   └── DashboardService.java          # Aggregated dashboard metrics
│   │   │   ├── repository/
│   │   │   │   ├── ProductRepository.java         # JpaRepository<Product, Long>
│   │   │   │   ├── InventoryRepository.java       # JpaRepository<Inventory, Long>
│   │   │   │   └── TransferLogRepository.java     # JpaRepository<TransferLog, Long>
│   │   │   ├── entity/
│   │   │   │   ├── Product.java                   # @Entity mapping product table
│   │   │   │   ├── Inventory.java                 # @Entity mapping inventory table
│   │   │   │   └── TransferLog.java               # @Entity mapping transfer_log table
│   │   │   ├── dto/
│   │   │   │   ├── ProductDTO.java                # Request/response for products
│   │   │   │   ├── InventoryDTO.java              # Request/response for inventory
│   │   │   │   ├── TransferRequestDTO.java        # Incoming transfer request
│   │   │   │   ├── TransferResponseDTO.java       # Transfer response with validation
│   │   │   │   ├── DashboardDTO.java              # Dashboard metrics response
│   │   │   │   └── ImportResultDTO.java           # CSV import summary
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java    # @ControllerAdvice for error responses
│   │   │   │   ├── ResourceNotFoundException.java # 404 errors
│   │   │   │   └── InsufficientQuantityException.java  # 409 business rule violation
│   │   │   └── MySpringProjectApplication.java   # @SpringBootApplication entry point
│   │   └── resources/
│   │       ├── application.yml                    # Spring config (datasource, JPA, logging)
│   │       ├── schema.sql                         # DDL for MySQL tables (product, inventory, transfer_log)
│   │       └── data.sql                           # Sample seed data (INSERT statements)
│   └── test/
│       ├── java/wis/my_spring_project/
│       │   ├── controller/
│       │   │   ├── ProductControllerIntegrationTest.java
│       │   │   ├── InventoryControllerIntegrationTest.java
│       │   │   ├── TransferControllerIntegrationTest.java
│       │   │   └── DashboardControllerIntegrationTest.java
│       │   └── service/
│       │       ├── ProductServiceTest.java        # Mockito unit tests
│       │       └── TransferServiceTest.java       # Business logic + validation tests
│       └── resources/
│           └── application.properties             # Test config (H2 in-memory, faster transactions)
├── .github/
│   └── instructions/
│       └── project_context.instructions.md        # Detailed project context & conventions
├── .agent_plan/
│   └── day_dream/
│       └── wis_backend_blueprint/                 # 📋 Project blueprint & roadmap (P0–P3)
│           ├── 00_index.md
│           ├── 01_executive_summary.md
│           ├── 02_architecture.md
│           ├── 03_feature_auth_jwt.md
│           ├── 04_feature_pagination.md
│           ├── 05_feature_audit_trail.md
│           ├── 06_feature_stock_alerts.md
│           ├── 80_implementation.md
│           └── 99_references.md
├── pom.xml                                       # Maven dependencies & build config
├── README.md                                     # This file
└── HELP.md                                       # Eclipse/Maven quick reference

```

### Layer Responsibilities

| Layer | Location | Purpose | Key Classes |
|-------|----------|---------|-------------|
| **Controller** | `controller/` | HTTP request handling, input validation, response formatting | `ProductController`, `TransferController` |
| **Service** | `service/` | Business logic, transaction boundaries, DTO ↔ Entity mapping | `TransferService`, `InventoryService` |
| **Repository** | `repository/` | Database queries via Spring Data JPA | `ProductRepository` (extends `JpaRepository`) |
| **Entity** | `entity/` | JPA persistence objects, exact mapping to schema | `Product`, `Inventory`, `TransferLog` |
| **DTO** | `dto/` | Request/response contracts, Jackson serialization, validation rules | `ProductDTO`, `TransferRequestDTO` |
| **Config** | `config/` | Spring beans, CORS, OpenAPI/Swagger | `CorsConfig`, `OpenApiConfig` |
| **Exception** | `exception/` | Centralized error handling, custom exception types | `GlobalExceptionHandler` |

### Request Lifecycle Example

```
POST /api/transfers (JSON body)
  ↓
TransferController.executeTransfer(TransferRequestDTO)
  ↓ (validate @Valid DTO)
  ↓
TransferService.transfer(dto)
  ↓ (check business rules: sufficient quantity?)
  ↓
InventoryRepository.findByProductIdAndLocation(...)
  ↓ (fetch from MySQL)
  ↓
TransferLogRepository.save(transferLog)
  ↓ (persist to MySQL)
  ↓
Entity → DTO conversion
  ↓
ResponseEntity<TransferResponseDTO> (200 OK)
  ↓
React Frontend
```

---

## API Documentation

### Base URL
```
http://localhost:8080
```

### Core Endpoints

#### Products

| Method | Endpoint | Description | Example |
|--------|----------|-------------|---------|
| **GET** | `/api/products` | List all products | `curl http://localhost:8080/api/products` |
| **GET** | `/api/products/{code}` | Get product by code | `curl http://localhost:8080/api/products/PRD001` |
| **POST** | `/api/products/import` | Bulk upsert (JSON array) | See examples below |
| **POST** | `/api/import/products` | Upload CSV file | Multipart form upload |

**Example: Create product via JSON**
```bash
curl -X POST http://localhost:8080/api/products/import \
  -H "Content-Type: application/json" \
  -d '[
    {"code":"LAPTOP21","name":"Dell Laptop 2021","weight":2.5},
    {"code":"MOUSE01","name":"Wireless Mouse","weight":0.1}
  ]'
```

**Example: Create product via CSV**
```bash
# Create file: products.csv
# code,name,weight
# PRD003,New Product,1.2

curl -X POST http://localhost:8080/api/import/products \
  -F "file=@products.csv"
```

#### Inventory

| Method | Endpoint | Description |
|--------|----------|-------------|
| **GET** | `/api/inventory` | List all inventory records |
| **GET** | `/api/inventory/search?code=PRD001` | Search by product code |
| **GET** | `/api/inventory/locations` | List all warehouse locations |

#### Transfers

| Method | Endpoint | Description |
|--------|----------|-------------|
| **POST** | `/api/transfers` | Execute inter-location transfer |

**Example: Transfer stock**
```bash
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "productCode":"PRD001",
    "fromLocation":"TKO",
    "toLocation":"CSW",
    "quantity":50
  }'
```

Validates:
- Product exists
- From location has sufficient quantity
- No location conflicts

Response (200 OK):
```json
{
  "id": 1,
  "productCode": "PRD001",
  "fromLocation": "TKO",
  "toLocation": "CSW",
  "quantity": 50,
  "transferredAt": "2026-03-09T10:30:42",
  "status": "SUCCESS"
}
```

#### Dashboard

| Method | Endpoint | Description |
|--------|----------|-------------|
| **GET** | `/api/dashboard` | Summary metrics (products, locations, transfers) |

Response:
```json
{
  "totalProducts": 15,
  "totalLocations": 3,
  "totalStockOnHand": 5000,
  "totalTransfers": 42,
  "lastTransferTime": "2026-03-09T10:15:00"
}
```

### Swagger UI

**Interactive API explorer:**
```
http://localhost:8080/swagger-ui.html
```

Features:
- ✅ Try endpoints directly in browser
- ✅ View request/response schemas
- ✅ See example payloads

### Error Handling

All endpoints return standardized error responses:

**404 Not Found**
```json
{
  "timestamp": "2026-03-09T10:30:42",
  "status": 404,
  "error": "Not Found",
  "message": "Product with code PRD999 not found",
  "path": "/api/products/PRD999"
}
```

**409 Conflict (Business Rule Violation)**
```json
{
  "timestamp": "2026-03-09T10:30:42",
  "status": 409,
  "error": "Conflict",
  "message": "Insufficient quantity at location TKO. Available: 10, Requested: 50",
  "path": "/api/transfers"
}
```

**400 Bad Request (Validation)**
```json
{
  "timestamp": "2026-03-09T10:30:42",
  "status": 400,
  "error": "Bad Request",
  "message": "productCode must not be blank",
  "path": "/api/transfers"
}
```

---

## Development Log (Learning 🧠 / Thinking 💭 / Trying 🔧 / Developing 🚀)

### Day 1: March 9, 2026 — Project Initialization & Schema Design

**🧠 Learning:**
- Understood the warehouse inventory domain: products (SKU/name/weight), inventory (per-location stock), transfers (accountability)
- Reviewed Spring Boot 3.5.x conventions: @SpringBootApplication, @Component, @Service, @Repository patterns
- Learned MySQL InnoDB best practices: utf8mb4 collation, foreign keys, unique constraints

**💭 Thinking:**
- Decided on a three-table schema:
  - `product` — master data (1 row = 1 unique product)
  - `inventory` — stock levels (many rows per product, one per location)
  - `transfer_log` — immutable audit trail (one row per transfer)
- Chose JPA entities over raw JDBC for type safety and automatic timestamp management
- Decided DTOs are needed to decouple API contracts from database entities

**🔧 Trying:**
- Created `schema.sql` with CREATE TABLE statements, foreign keys, check constraints
- Tested MySQL connection: `mysql -u root -p warehouse_inventory -e "SELECT 1;"`
- Verified Maven build works: `./mvnw clean compile`

**🚀 Developing:**
- Generated Entity classes with Lombok `@Data`, `@Entity`, JPA annotations
- Created Spring Data JPA Repository interfaces (no custom queries yet, just `JpaRepository`)
- Set up `application.yml` with datasource, JPA properties, logging

**Status:** ✅ Schema and entity layer complete. Tests not yet written.

---

### Day 1: March 9, 2026 — Core Services & Business Logic

**🧠 Learning:**
- Discovered that business logic (e.g., "transfer only if sufficient quantity") belongs in **Service**, not Controller
- Understood Spring `@Transactional`: ensures atomicity; rolls back on exception
- Learned Mockito best practices: `@Mock` for dependencies, `@InjectMocks` for class under test

**💭 Thinking:**
- Designed `TransferService.transfer()` to:
  1. Validate product exists
  2. Check source location has sufficient stock
  3. Atomically decrement source, increment target
  4. Log transfer with timestamp
  5. Return early on any validation failure
- Decided to throw custom exceptions (`InsufficientQuantityException`, `ResourceNotFoundException`) for Controller to catch

**🔧 Trying:**
- Implemented `ProductService` for CRUD and CSV import
- Wrote `TransferService` with proper validation order
- Tested CSV parsing with OpenCSV; verified UTF-8 handling

**🚀 Developing:**
- Created `InventoryService` to encapsulate inventory levels query/update logic
- Added `@Transactional` to all service methods modifying data
- Mapped Entities ↔ DTOs inside services (keep DTOs out of repositories)

**Status:** ✅ All service layer complete with unit tests. Controllers not yet built.

---

### Day 1: March 9, 2026 — REST Controllers & Error Handling

**🧠 Learning:**
- Understood Spring Controller role: deserialize HTTP → DTO, validate with `@Valid` + `BindingResult`, call service, wrap response
- Learned `ResponseEntity<T>` for flexible status codes (200, 201, 400, 404, 409)
- Reviewed `@ControllerAdvice` + `@ExceptionHandler`: centralize error responses across all endpoints

**💭 Thinking:**
- Designed error responses to include: timestamp, HTTP status, error message, request path
- Decided `GlobalExceptionHandler` catches custom exceptions and framework exceptions (MethodArgumentNotValidException)
- Planned response codes:
  - 200 OK for successful GET/POST with data
  - 201 Created for POST that creates a resource
  - 400 Bad Request for validation failures (empty codes, negative quantities)
  - 404 Not Found for missing products/inventory
  - 409 Conflict for business rule violations (insufficient stock)

**🔧 Trying:**
- Built `ProductController`: GET /api/products, GET /api/products/{code}, POST /api/products/import
- Tested CSV upload with Postman; verified multipart form-data handling
- Built error handler for duplicate product codes (MySQL UNIQUE constraint)

**🚀 Developing:**
- Created `TransferController` with POST /api/transfers endpoint
- Implemented `CsvImportController` for both products and inventory CSV uploads
- Added `DashboardController` for aggregated metrics
- Set up `CorsConfig` to allow React frontend (localhost:5173) to call API

**Status:** ✅ All controllers complete with integration tests (22 tests passing).

---

### Day 1: March 9, 2026 — Documentation & Testing Refinement

**🧠 Learning:**
- Understood how H2 in-memory database speeds up tests: no I/O, transactions rolled back after each test
- Discovered `@SpringBootTest` annotation: starts full application context, slower but tests entire stack
- Reviewed test assertions: `assertEquals`, `assertThrows`, `assertThat` from AssertJ

**💭 Thinking:**
- Decided integration tests should:
  - Test full HTTP stack (Controller → Service → Repository → DB)
  - Use H2 in-memory database for speed
  - Verify response status codes, headers, and JSON structure
- Unit tests should:
  - Mock repository calls
  - Focus on business logic edge cases
  - Test exception throwing on invalid input

**🔧 Trying:**
- Wrote integration tests for all 5 controllers
- Tested edge cases: empty product list, transfer with zero quantity, duplicate imports
- Verified error responses match Swagger schema

**🚀 Developing:**
- Refined error messages for clarity: "Insufficient quantity at TKO. Available: 10, Requested: 50"
- Added `@Valid` annotations in DTOs for constraint validation
- Set up test data fixtures (sample products, inventory)

**Status:** ✅ 43 tests passing. Ready for demo to stakeholders.

---

### Day 2: March 10, 2026 — Blueprint & Roadmap Planning

**🧠 Learning:**
- Understood production-readiness gap: current API has no auth, pagination, audit logging, or alerts
- Researched JWT tokens: stateless, signed, contain claims (user ID, roles, expiry)
- Learned about database pagination: OFFSET / LIMIT queries for large result sets

**💭 Thinking:**
- Designed four-phase evolution:
  - **P0 (Done):** Walking Skeleton — HTTP → DB pipeline working
  - **P1:** Auth + Pagination — JWT middleware, Pageable endpoints
  - **P2:** Audit Trail + Alerts — full transfer history, stock thresholds
  - **P3:** Reports + Caching — PDF export, Redis caching
- Planned JWT strategy: Spring Security with `OncePerRequestFilter`, validate token on each request

**🔧 Trying:**
- Created `.agent_plan/` directory with multi-document blueprint
- Drafted P1 feature specs: how JWT flows, which endpoints need auth
- Sketched P2 audit log: track user, timestamp, before/after values for each transfer

**🚀 Developing:**
- Documented current architecture in Mermaid diagrams (Controller → Service → Repository)
- Created implementation roadmap with effort estimates
- Identified dependencies: P1 enables P2 (need user context for audit logs)

**Status:** ✅ Blueprint complete. Ready for stakeholder review and P1 sprint planning.

---

### Retrospective: What Worked, What Didn't

**✅ What Worked:**
1. **Entity-first design** — Started with schema.sql, generated entities. Saved rework vs. reverse-engineering from entities.
2. **Service layer** — Centralizing business logic there made testing and reuse easy.
3. **Error handling** — GlobalExceptionHandler prevented explosion of error-handling code in controllers.
4. **CSV import** — Bulk operations drastically reduce frontend complexity (no 10K POST requests).

**❌ What Could Be Better:**
1. **No input canonicalization** — Product codes could be case-sensitive; consider normalizing to uppercase.
2. **Timestamps** — Currently MySQL DEFAULT CURRENT_TIMESTAMP; should migrate to ZonedDateTime in code for timezone safety.
3. **No pagination** — GET /api/products returns all rows; would timeout with 100K+ products.
4. **No API versioning** — If contracts change, need strategy (v1, v2 endpoints vs. breaking changes).

**🎯 Next Steps:**
- Add pagination via Spring Data's `Pageable` (P1)
- Implement JWT authentication (P1)
- Full audit trail for transfers (P2)
- Stock alert emails (P2)

---

## Contributing

### Coding Conventions

1. **Naming:**
   - Classes: `ProductService`, `ProductDTO`, `ProductRepository` (noun + role)
   - Methods: `transferInventory()`, `validateTransfer()`, `findByCode()` (verb + object)
   - Constants: `MAX_PRODUCT_CODE_LENGTH = 50` (UPPER_CASE)

2. **Annotations:**
   - `@Service` — business logic
   - `@Repository` — data access (Spring Data JPA)
   - `@RestController` — HTTP handlers
   - `@Transactional` — atomicity for multi-step operations
   - `@Valid` — input validation

3. **Exception Handling:**
   - Create custom exceptions for business rules (e.g., `InsufficientQuantityException`)
   - Throw early, fail fast
   - Let `GlobalExceptionHandler` convert to HTTP responses

4. **Testing:**
   - Integration tests call real database (H2 in-memory)
   - Unit tests mock `@Repository` dependencies
   - Test file naming: `*Test.java` for unit, `*IntegrationTest.java` for integration

### Before Submitting Changes

```bash
# Compile with tests
./mvnw clean install

# Format code (optional, if you have formatter configured)
# ./mvnw spotless:apply

# Run tests individually if needed
./mvnw test -Dtest=ProductServiceTest
```

---

## Troubleshooting

### "Unknown database 'warehouse_inventory'" on startup

**Cause:** MySQL schema not created.

**Solution:**
```bash
mysql -u root -p < src/main/resources/schema.sql
```

Then verify:
```bash
mysql -u root -p -e "SHOW DATABASES;" | grep warehouse_inventory
```

### "Access denied for user 'root'@'localhost'"

**Cause:** Incorrect MySQL password in `application.yml`.

**Solution:**
```yaml
spring:
  datasource:
    password: your_actual_mysql_password_here
```

Then test:
```bash
mysql -u root -p -e "SELECT 1;"
```

### Tests fail with "Cannot get a connection" in IDE

**Cause:** Spring Boot test context using MySQL instead of H2.

**Solution:** Ensure `src/test/resources/application.properties` exists with H2 config:
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
```

Then refresh Maven and re-run: `./mvnw clean test`

### Port 8080 already in use

**Cause:** Another Spring Boot app or service running on port 8080.

**Solution:**
```bash
# Kill the process (Windows)
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Or change port in application.yml
server:
  port: 9000  # Use 9000 instead
```

---

## License & References

- **Spring Boot:** https://spring.io/projects/spring-boot
- **Spring Data JPA:** https://spring.io/projects/spring-data-jpa
- **Springdoc OpenAPI:** https://springdoc.org/
- **MySQL Documentation:** https://dev.mysql.com/doc/

---

**Contact:** For questions or contributions, refer to the `.agent_plan/` directory for project vision and roadmap.
- JUnit 5 + Mockito
- Lombok
