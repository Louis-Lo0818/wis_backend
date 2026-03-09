# Warehouse Inventory System — Backend (Spring Boot)

REST API backend for the Warehouse Inventory System. Built with Spring Boot 3.x, Spring Data JPA, MySQL, and JUnit 5 (43 passing tests).

---

## Prerequisites

| Requirement | Version |
|---|---|
| Java | 21+ |
| Maven | 3.9+ (or use the included `mvnw` wrapper) |
| MySQL | 8.0+ |

---

## Database Setup

```sql
-- Run once in MySQL to create the schema
mysql -u root -p < src/main/resources/schema.sql

-- Optional: load sample seed data
mysql -u root -p warehouse_inventory < src/main/resources/data.sql
```

Or manually:
```sql
CREATE DATABASE IF NOT EXISTS warehouse_inventory
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

## Configuration

Edit `src/main/resources/application.yml` with your MySQL credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/warehouse_inventory?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: your_password_here
```

---

## Build

```bash
# Compile and package (skips tests)
./mvnw clean package -DskipTests

# Compile, run all tests, and package
./mvnw clean install
```

---

## Run Tests

```bash
# Run all 43 tests (uses H2 in-memory — no MySQL required)
./mvnw test

# Run a specific test class
./mvnw test -Dtest=TransferServiceTest
./mvnw test -Dtest=TransferControllerIntegrationTest
```

Test results are written to `target/surefire-reports/`.

---

## Start the Server

```bash
./mvnw spring-boot:run
```

The API starts on **http://localhost:8080**.

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | List all products |
| GET | `/api/products/{code}` | Get product by code |
| POST | `/api/products/import` | Bulk upsert products (JSON) |
| POST | `/api/import/products` | Upload products CSV |
| POST | `/api/import/inventory` | Upload inventory CSV |
| GET | `/api/inventory` | List all inventory levels |
| GET | `/api/inventory/search?code=X` | Search inventory by product code |
| GET | `/api/inventory/locations` | List all warehouse locations |
| POST | `/api/transfers` | Transfer inventory between locations |
| GET | `/api/dashboard` | Dashboard summary stats |

**Swagger UI:** http://localhost:8080/swagger-ui.html  
**OpenAPI JSON:** http://localhost:8080/v3/api-docs

---

## CSV File Formats

**Products CSV** — upload via `POST /api/import/products`:
```
code,name,weight
PRD001,Laptop Computer,2.50
PRD002,Wireless Mouse,0.10
```

**Inventory CSV** — upload via `POST /api/import/inventory`:
```
productCode,location,quantity
PRD001,TKO,150
PRD001,CSW,75
```

---

## Project Structure

```
src/
├── main/java/wis/my_spring_project/
│   ├── config/          # CorsConfig, OpenApiConfig
│   ├── controller/      # REST controllers (5 classes)
│   ├── service/         # Business logic (5 classes)
│   ├── repository/      # JPA repositories (3 interfaces)
│   ├── entity/          # JPA entities (Product, Inventory, TransferLog)
│   ├── dto/             # Request/Response DTOs (6 classes)
│   └── exception/       # GlobalExceptionHandler + custom exceptions
└── test/java/wis/my_spring_project/
    ├── controller/      # 4 integration test classes (26 tests)
    └── service/         # 2 unit test classes (16 tests)
```

---

## Tech Stack

- Spring Boot 3.5.x
- Spring Data JPA / Hibernate 6.x
- MySQL 8.0+ (H2 for tests)
- Jakarta Validation (Bean Validation 3.0)
- OpenCSV 5.x
- Springdoc OpenAPI 2.x (Swagger UI)
- JUnit 5 + Mockito
- Lombok
