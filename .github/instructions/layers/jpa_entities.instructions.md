---
applyTo: "src/main/java/**/entity/*.java"
---

# JPA Entity Guidelines

## Goals
- Ensure entities match the MySQL database schema exactly
- Enforce consistent use of Lombok and JPA annotations
- Prevent schema drift between Java entities and database tables

## Rules

1. **Match Schema Exactly**: Column names, types, and constraints must mirror the MySQL schema.
   - VARCHAR(50) → `@Column(length = 50)`
   - DECIMAL(10,2) → `BigDecimal` with `@Column(precision = 10, scale = 2)`
   - BIGINT → `Long`
   - INT → `Integer`
   - DATETIME → `LocalDateTime`

2. **Lombok Annotations**: Every entity MUST use `@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder`.

3. **Timestamp Handling**:
   - `created_at` → `@CreationTimestamp @Column(updatable = false)`
   - `updated_at` → `@UpdateTimestamp`

4. **Primary Key**: Always `@Id @GeneratedValue(strategy = GenerationType.IDENTITY)`.

5. **Relationships**: Use `@ManyToOne(fetch = FetchType.LAZY)` for foreign keys to avoid N+1 queries.

6. **Unique Constraints**: Model via `@Table(uniqueConstraints = ...)` or `@Column(unique = true)`.

7. **Naming**: Table name in `@Table(name = "snake_case")`. Entity class in PascalCase.
