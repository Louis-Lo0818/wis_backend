---
applyTo: "src/main/java/**/service/*.java"
---

# Service Layer Guidelines

## Goals
- Enforce proper business logic placement in the service layer
- Ensure transaction safety for multi-entity operations
- Maintain consistent DTO mapping patterns

## Rules

1. **@Service + @RequiredArgsConstructor**: Every service class uses these annotations. Dependencies injected via constructor (final fields).

2. **@Transactional**: REQUIRED on any method that:
   - Modifies multiple entities
   - Involves read-then-write patterns
   - Calls multiple repository save/delete operations

3. **Exception Throwing**:
   - Entity not found → `throw new ResourceNotFoundException("message")`
   - Business rule violation → `throw new InsufficientQuantityException("message")`
   - NEVER catch and swallow exceptions silently

4. **DTO Mapping**: Use private `toDTO(Entity)` methods. Keep mapping logic in the service, not in controllers or entities.

5. **No Direct Entity Exposure**: Controllers never receive or return JPA entities. Always use DTOs.

6. **Upsert Pattern**: For import operations, use `findByCode().orElse(new Entity())` then set fields and save.

7. **Query Methods**: Call repository methods. NEVER write raw SQL in services.
