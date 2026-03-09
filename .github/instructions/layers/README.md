# Layers Guidelines

This folder contains **Spring Boot architecture layer** guidelines for the Warehouse Inventory System backend.

## Layer Structure

The backend follows a strict **layered architecture**:

```
Controller → Service → Repository → Entity/DTO
```

### Files

| File | Scope | Applies To |
|------|-------|-----------|
| [controller_layer.instructions.md](./controller_layer.instructions.md) | REST endpoints, HTTP binding | `src/main/java/**/controller/*.java` |
| [service_layer.instructions.md](./service_layer.instructions.md) | Business logic, transactions | `src/main/java/**/service/*.java` |
| [jpa_entities.instructions.md](./jpa_entities.instructions.md) | JPA entities, schema mapping | `src/main/java/**/entity/*.java` |

## Core Principles

1. **Thin Controllers** — No business logic, no DB calls
2. **Transaction Safety** — `@Transactional` on multi-entity writes
3. **Schema Compliance** — Entity fields match MySQL exactly
4. **DTO Validation** — Jakarta Validation on request boundaries
5. **Error Handling** — GlobalExceptionHandler centralizes responses

See [../project_context.instructions.md](../project_context.instructions.md) for full project context.
