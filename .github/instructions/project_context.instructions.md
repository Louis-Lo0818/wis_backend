---
applyTo: "src/**/*.java"
---

# Warehouse Inventory System — Backend Project Context

## Project Overview
This is the **Spring Boot (Java 17+) REST API backend** for the Warehouse Inventory System. It provides RESTful endpoints for the React frontend to manage products, inventory levels, and stock transfers across multiple warehouse locations.

The backend connects to a **MySQL 8.x** database and is consumed by a **React/TypeScript frontend** (port 5173).

## Tech Stack
- **Java 17+**
- **Spring Boot 3.2.x**
- **Spring Data JPA** (Hibernate 6.x ORM)
- **MySQL 8.x** (InnoDB, utf8mb4)
- **Gradle** (build tool)
- **Lombok** (boilerplate reduction)
- **OpenCSV** (CSV file parsing)
- **Springdoc OpenAPI** (Swagger UI)
- **JUnit 5 + Mockito** (testing)
- **H2** (in-memory DB for tests)

## Project Structure
```
src/
├── main/
│   ├── java/com/warehouse/inventory/
│   │   ├── WarehouseInventoryApplication.java    # @SpringBootApplication
│   │   ├── config/
│   │   │   ├── CorsConfig.java                   # CORS for React dev server
│   │   │   └── OpenApiConfig.java                # Swagger/OpenAPI config
│   │   ├── controller/
│   │   │   ├── ProductController.java            # /api/products
│   │   │   ├── InventoryController.java          # /api/inventory
│   │   │   ├── TransferController.java           # /api/transfers
│   │   │   ├── CsvImportController.java          # /api/import
│   │   │   └── DashboardController.java          # /api/dashboard
│   │   ├── service/
│   │   │   ├── ProductService.java
│   │   │   ├── InventoryService.java
│   │   │   ├── TransferService.java
│   │   │   └── CsvImportService.java
│   │   ├── repository/
│   │   │   ├── ProductRepository.java            # JpaRepository<Product, Long>
│   │   │   ├── InventoryRepository.java          # JpaRepository<Inventory, Long>
│   │   │   └── TransferLogRepository.java        # JpaRepository<TransferLog, Long>
│   │   ├── entity/
│   │   │   ├── Product.java                      # @Entity
│   │   │   ├── Inventory.java                    # @Entity
│   │   │   └── TransferLog.java                  # @Entity
│   │   ├── dto/
│   │   │   ├── ProductDTO.java
│   │   │   ├── InventoryDTO.java
│   │   │   ├── InventoryLevelDTO.java
│   │   │   ├── TransferRequestDTO.java
│   │   │   ├── TransferResponseDTO.java
│   │   │   ├── DashboardDTO.java
│   │   │   └── ImportResultDTO.java
│   │   └── exception/
│   │       ├── ResourceNotFoundException.java
│   │       ├── InsufficientQuantityException.java
│   │       └── GlobalExceptionHandler.java       # @ControllerAdvice
│   └── resources/
│       ├── application.yml
│       ├── schema.sql
│       └── data.sql
└── test/
    └── java/com/warehouse/inventory/
        ├── controller/
        ├── service/
        └── repository/
```

## Database Schema (MySQL 8.x)
Three tables: `product`, `inventory`, `transfer_log`

### product
| Column | Type | Constraint |
|--------|------|------------|
| id | BIGINT AUTO_INCREMENT | PK |
| code | VARCHAR(50) | UNIQUE, NOT NULL |
| name | VARCHAR(200) | NOT NULL |
| weight | DECIMAL(10,2) | DEFAULT 0.00 |
| created_at | DATETIME | DEFAULT NOW() |
| updated_at | DATETIME | ON UPDATE NOW() |

### inventory
| Column | Type | Constraint |
|--------|------|------------|
| id | BIGINT AUTO_INCREMENT | PK |
| product_id | BIGINT | FK → product.id |
| location | VARCHAR(50) | NOT NULL |
| quantity | INT | CHECK >= 0 |
| created_at | DATETIME | DEFAULT NOW() |
| updated_at | DATETIME | ON UPDATE NOW() |
Unique constraint: (product_id, location)

### transfer_log
| Column | Type | Constraint |
|--------|------|------------|
| id | BIGINT AUTO_INCREMENT | PK |
| product_id | BIGINT | FK → product.id |
| from_location | VARCHAR(50) | NOT NULL |
| to_location | VARCHAR(50) | NOT NULL |
| quantity | INT | CHECK > 0 |
| transferred_at | DATETIME | DEFAULT NOW() |

## REST API Endpoints
All prefixed with `/api`:

| Method | Endpoint | Controller | Description |
|--------|----------|-----------|-------------|
| GET | /api/products | ProductController | List all products |
| GET | /api/products/{code} | ProductController | Get product by code |
| POST | /api/products/import | ProductController | Bulk upsert products (JSON) |
| POST | /api/import/products | CsvImportController | Upload products CSV |
| POST | /api/import/inventory | CsvImportController | Upload inventory CSV |
| GET | /api/inventory | InventoryController | List all inventory levels |
| GET | /api/inventory/search?code=X | InventoryController | Search by product code |
| GET | /api/inventory/locations | InventoryController | List warehouse locations |
| POST | /api/transfers | TransferController | Transfer inventory |
| GET | /api/dashboard | DashboardController | Dashboard summary |

## Core Philosophy
1. **Read Before Write**: NEVER guess entity structure or repository methods. Read existing code first.
2. **Reuse, Don't Reinvent**: Use Spring Data JPA query derivation. Don't write raw SQL unless necessary.
3. **Consistency**: Follow existing patterns (Lombok, Builder, @Transactional).
4. **Layered Architecture**: Controller → Service → Repository. No business logic in controllers.
5. **Validation**: Use Jakarta Validation annotations (@NotBlank, @Positive) on DTOs.
