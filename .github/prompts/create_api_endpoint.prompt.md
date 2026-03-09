---
description: "Create a new REST API endpoint with full layered implementation"
---

# Create New API Endpoint

Create a new REST API endpoint following the Warehouse Inventory layered architecture pattern.

## Steps

### 1. Define the Endpoint
- Determine HTTP method (GET/POST/PUT/DELETE), path, request/response DTOs
- Check if the entity and repository already exist

### 2. Implementation Order (Bottom-Up)
Follow this exact order to avoid compilation errors:

**Step 1: Entity** (if new table needed)
```java
@Entity @Table(name = "table_name")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EntityName { ... }
```

**Step 2: Repository**
```java
@Repository
public interface EntityRepository extends JpaRepository<Entity, Long> { ... }
```

**Step 3: DTO** (with Jakarta Validation)
```java
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EntityDTO {
    @NotBlank(message = "Field is required")
    private String field;
}
```

**Step 4: Service** (with @Transactional for writes)
```java
@Service @RequiredArgsConstructor
public class EntityService {
    private final EntityRepository repository;
    // business logic + DTO mapping
}
```

**Step 5: Controller** (thin, delegates to service)
```java
@RestController @RequestMapping("/api/resource")
@RequiredArgsConstructor @CrossOrigin
public class EntityController {
    private final EntityService service;
}
```

**Step 6: Exception Handling** (if new exception types needed)
- Add new exception class
- Add handler method to GlobalExceptionHandler

### 3. Verify
- Run `./gradlew build` to compile
- Run `./gradlew test` to verify tests pass
- Check Swagger UI at http://localhost:8080/swagger-ui.html
- Test endpoint with curl or Postman

---

**Default behavior**: Create full stack (entity → repo → DTO → service → controller).
**To override**: Say "service only" or "controller only" to create partial layers.
