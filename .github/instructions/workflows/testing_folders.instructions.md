---
applyTo: "src/test/**/*.java, **/.agent_plan/red_team/**"
---

# Testing Folders Guidelines

## Purpose
Define where Warehouse agents should place testing artifacts, exploratory scripts, and adversarial findings.

**Core Principle**: *"Every folder has one job. When in doubt, use this decision tree."*

## Folder Responsibility Matrix

| Folder | Scope | Purpose | Persistence | Owner |
|--------|-------|---------|-------------|-------|
| `src/test/java/.../service/` | Single service | Service unit tests (Mockito) | **Permanent** (git-tracked) | WarehouseArch |
| `src/test/java/.../controller/` | Single controller | Controller integration tests (@SpringBootTest) | **Permanent** (git-tracked) | WarehouseArch |
| `src/test/java/.../repository/` | Repository layer | JPA query tests | **Permanent** (git-tracked) | WarehouseArch |
| `.agent_plan/red_team/` | Per-feature | WarehouseRed attack scripts, edge case findings | **Session-to-Session** | WarehouseRed |

## Decision Tree

### Step 1: Is this a formal JUnit test?
- **YES** → Continue to Step 2
- **NO** → Use `.agent_plan/red_team/` for attack notes

### Step 2: What layer does it test?
- **Service layer** (business logic, Mockito mocks) → `src/test/java/.../service/`
- **Controller layer** (HTTP endpoints, MockMvc) → `src/test/java/.../controller/`
- **Repository layer** (JPA queries) → `src/test/java/.../repository/`

### Step 3: Is this adversarial / edge case research?
- **YES** → Use `.agent_plan/red_team/{feature_name}/`
- Contains Markdown attack findings, not Java test code

## Test Naming Conventions

| Test Type | Class Suffix | Example |
|-----------|-------------|---------|
| Service unit test | `Test` | `ProductServiceTest` |
| Controller integration | `IntegrationTest` | `ProductControllerIntegrationTest` |
| Repository test | `RepositoryTest` | `ProductRepositoryTest` |

## Test Method Naming

Use `given_when_then` format:
```java
@Test
void givenValidProduct_whenCreate_thenReturns201()
void givenProductNotFound_whenGetById_thenThrowsNotFoundException()
void givenInsufficientStock_whenTransfer_thenThrowsException()
```

## Quick Reference

| Scenario | Folder |
|----------|--------|
| Unit test for ProductService | `src/test/java/.../service/ProductServiceTest.java` |
| Integration test for ProductController | `src/test/java/.../controller/ProductControllerIntegrationTest.java` |
| JPA query test for InventoryRepository | `src/test/java/.../repository/InventoryRepositoryTest.java` |
| WarehouseRed edge case findings | `.agent_plan/red_team/products/attack_notes.md` |
| Transfer boundary attack notes | `.agent_plan/red_team/transfers/boundary_tests.md` |

## Test Configuration
- Test resources: `src/test/resources/application.properties` (H2 DB config)
- H2 schema: loaded from `src/main/resources/schema.sql` (Spring auto-config)
- Test data: `src/test/resources/data.sql` or `@BeforeEach` setup methods
