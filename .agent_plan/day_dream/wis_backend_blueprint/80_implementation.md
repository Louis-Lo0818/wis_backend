---
project: "Warehouse Inventory System (WIS) Backend"
current_phase: 1
phase_name: "JWT Auth + Pagination"
status: TODO
last_updated: "2026-03-09"
---

# 80 - Implementation Plan

> Part of [WIS Backend Blueprint](./00_index.md)

<!-- 
⚠️ CODE EXAMPLES ARE ILLUSTRATIVE, NOT PRESCRIPTIVE.
WarehouseArch determines actual file paths and implementation details.
-->

---

## 📊 Status Legend

| Icon | Status | Meaning |
|------|--------|---------|
| ⏳ | `[TODO]` | Not started |
| 🔄 | `[WIP]` | In progress |
| ✅ | `[DONE]` | Complete |
| 🚧 | `[BLOCKED:reason]` | Stuck |
| 🚫 | `[CUT]` | Removed from scope |

---

## 🦴 Phase 0: Walking Skeleton — ✅ `[DONE]`

**Goal:** *"Prove the full HTTP → Service → MySQL pipeline with all 5 core endpoint groups"*  
**Completed:** Before 2026-03-09

### Tasks ✅

| Status | Task | Layer | Difficulty |
|--------|------|-------|------------|
| ✅ | Products CRUD (`/api/products`) | All layers | `[KNOWN]` |
| ✅ | Inventory management (`/api/inventory`) | All layers | `[KNOWN]` |
| ✅ | Stock transfers (`/api/transfers`) | All layers | `[KNOWN]` |
| ✅ | CSV bulk import (`/api/import`) | Controller + Service | `[KNOWN]` |
| ✅ | Dashboard stats (`/api/dashboard`) | Controller + Service | `[KNOWN]` |
| ✅ | GlobalExceptionHandler | `exception/` | `[KNOWN]` |
| ✅ | JUnit tests (service + controller) | `src/test/` | `[KNOWN]` |

---

## 🔐 Phase 1: JWT Auth + Pagination

**Goal:** *"Secure all write operations with JWT and make read endpoints fast with pagination"*  
**Duration:** ≤2 weeks  
**Reference:** [03_feature_auth_jwt.md](./03_feature_auth_jwt.md), [04_feature_pagination.md](./04_feature_pagination.md)

### Exit Gate

- [ ] `mvn test` → All existing tests green + new auth/pagination tests green
- [ ] `POST /api/auth/login` with wrong credentials → `401`
- [ ] `POST /api/products` without token → `401`
- [ ] `GET /api/products?page=0&size=20` → Paginated response with metadata

### Tasks

| Status | Task | Layer | Difficulty |
|--------|------|-------|------------|
| ⏳ | Add `users` table to `schema.sql` + seed admin in `data.sql` | DB schema | `[KNOWN]` |
| ⏳ | Create `User` entity + `UserRepository` | Entity + Repo | `[KNOWN]` |
| ⏳ | Create `JwtUtil` (generate/validate HS256 JWT) | `security/` | `[KNOWN]` |
| ⏳ | Create `JwtAuthenticationFilter` | `security/` | `[KNOWN]` |
| ⏳ | Create `SecurityConfig` (permit GET endpoints, protect writes) | `config/` | `[KNOWN]` |
| ⏳ | Create `AuthController` + `AuthService` + `LoginRequestDTO` + `LoginResponseDTO` | All layers | `[KNOWN]` |
| ⏳ | Add Pageable to `ProductRepository` + search/filter support | Repository | `[KNOWN]` |
| ⏳ | Update `ProductService.getAll()` to return `Page<ProductDTO>` | Service | `[KNOWN]` |
| ⏳ | Update `ProductController.getAll()` to accept `Pageable` params | Controller | `[KNOWN]` |
| ⏳ | Add Pageable to `InventoryRepository` + `InventoryService` + `InventoryController` | All layers | `[KNOWN]` |
| ⏳ | Write auth tests (valid/invalid login, protected endpoints) | `src/test/` | `[KNOWN]` |
| ⏳ | Write pagination tests (page/size/sort params) | `src/test/` | `[KNOWN]` |

### Dependencies to Add to `pom.xml`

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-api</artifactId>
  <version>0.11.5</version>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-impl</artifactId>
  <version>0.11.5</version>
  <scope>runtime</scope>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-jackson</artifactId>
  <version>0.11.5</version>
  <scope>runtime</scope>
</dependency>
```

### P1 Completion Checklist

- [ ] All Phase 1 tasks marked ✅
- [ ] `mvn test` passes
- [ ] Swagger UI still accessible at `/swagger-ui.html`
- [ ] CORS still allows React frontend at `:5173`
- [ ] No H2 test breakage from Spring Security changes

---

## 📋 Phase 2: Audit Trail + Stock Alerts

**Goal:** *"Make transfers accountable and stock levels proactively monitored"*  
**Duration:** ≤2 weeks  
**Reference:** [05_feature_audit_trail.md](./05_feature_audit_trail.md), [06_feature_stock_alerts.md](./06_feature_stock_alerts.md)

### Exit Gate

- [ ] `GET /api/audit/transfers` → Returns transfer audit records with `performedBy`
- [ ] `GET /api/alerts/active` → Returns products below threshold
- [ ] `mvn test` → All tests green

### Tasks

| Status | Task | Layer | Difficulty |
|--------|------|-------|------------|
| ⏳ | Add `audit_log` table to `schema.sql` | DB schema | `[KNOWN]` |
| ⏳ | Create `AuditLog` entity + `AuditLogRepository` | Entity + Repo | `[KNOWN]` |
| ⏳ | Extend `TransferService.transfer()` to write audit record | Service | `[KNOWN]` |
| ⏳ | Create `AuditController` + `AuditService` + `AuditLogDTO` | All layers | `[KNOWN]` |
| ⏳ | Add `min_stock` column to `product` table | DB schema | `[KNOWN]` |
| ⏳ | Add `minStock` field to `Product` entity + `ProductDTO` | Entity + DTO | `[KNOWN]` |
| ⏳ | Add `stock_alert` table to `schema.sql` | DB schema | `[KNOWN]` |
| ⏳ | Create `StockAlert` entity + `StockAlertRepository` | Entity + Repo | `[KNOWN]` |
| ⏳ | Extend `InventoryService` to check threshold after transfer | Service | `[EXPERIMENTAL]` |
| ⏳ | Create `AlertController` (GET active, POST dismiss) | Controller | `[KNOWN]` |
| ⏳ | Write audit trail tests | `src/test/` | `[KNOWN]` |
| ⏳ | Write stock alert threshold tests | `src/test/` | `[KNOWN]` |

---

## 🚀 Phase 3: Reports + Caching + Flyway

**Goal:** *"Complete production-readiness: exports, performance, and schema versioning"*

### Tasks (Placeholder)

| Status | Task | Layer | Difficulty |
|--------|------|-------|------------|
| ⏳ | Apache POI Excel export for inventory snapshot | Service + Controller | `[EXPERIMENTAL]` |
| ⏳ | Spring Cache + Redis for `GET /api/products` | Service + Config | `[EXPERIMENTAL]` |
| ⏳ | Flyway migration scripts (V1 = current schema) | DB + Config | `[KNOWN]` |
| ⏳ | Spring Actuator health/metrics endpoint | Config | `[KNOWN]` |

---

## ⚠️ Exception Handling Reference

| Exception Class | When Thrown | HTTP Status |
|----------------|-------------|-------------|
| `ResourceNotFoundException` | Entity not found by ID | `404 Not Found` |
| `InsufficientQuantityException` | Transfer qty > available stock | `422 Unprocessable Entity` |
| `MethodArgumentNotValidException` | `@Valid` DTO validation fails | `400 Bad Request` |
| `PropertyReferenceException` | Invalid `sort` field in Pageable | `400 Bad Request` |
| `BadCredentialsException` | Wrong login credentials (P1) | `401 Unauthorized` |
