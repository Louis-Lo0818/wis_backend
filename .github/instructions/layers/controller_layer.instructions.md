---
applyTo: "src/main/java/**/controller/*.java"
---

# Controller Layer Guidelines

## Goals
- Keep controllers thin — delegate all business logic to services
- Enforce consistent REST API patterns
- Ensure proper validation and CORS configuration

## Rules

1. **Thin Controllers**: Controllers ONLY:
   - Accept HTTP requests
   - Call one service method
   - Return ResponseEntity with the result
   - NO business logic, NO repository calls, NO DTO mapping

2. **Annotations**: Every controller uses:
   - `@RestController`
   - `@RequestMapping("/api/resource")`
   - `@RequiredArgsConstructor`
   - `@CrossOrigin`

3. **Validation**: Use `@Valid` on `@RequestBody` parameters to trigger Jakarta Validation.

4. **Response Pattern**: Always return `ResponseEntity<T>`:
   ```java
   return ResponseEntity.ok(service.getAll());
   ```

5. **Path Variables**: Use `@PathVariable` for resource identifiers. Use `@RequestParam` for filters/search.

6. **Error Handling**: Do NOT catch exceptions in controllers. Let GlobalExceptionHandler handle them.

7. **File Upload**: Use `@RequestParam("file") MultipartFile file` for CSV uploads.
