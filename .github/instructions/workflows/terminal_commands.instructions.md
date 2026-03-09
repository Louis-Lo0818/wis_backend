---
applyTo: "**/*.agent.md, **/*.prompt.md"
---

# Terminal Commands Guidelines for Warehouse Backend

## Goals
- Prevent build and test failures when agents run Maven or Java commands via terminal.
- Standardize terminal command patterns across all Warehouse agents.
- Ensure commands run from the correct working directory.

## Rules

### 1. Always Run Commands from Project Root
All Maven commands MUST be run from the directory containing `pom.xml`.

**Pattern (PowerShell — Windows)**:
```powershell
cd C:\Users\User\wis_backend\my-spring-project; mvn <goal>
```

**Pattern (bash — Git Bash / WSL)**:
```bash
cd /c/Users/User/wis_backend/my-spring-project && mvn <goal>
```

### 2. Common Maven Commands

| Goal | Command | Notes |
|------|---------|-------|
| Compile only | `mvn compile` | Quick syntax check |
| Run all tests | `mvn test` | Uses H2 in-memory DB |
| Run single test class | `mvn test -Dtest=ProductServiceTest` | Class name, no package |
| Run single test method | `mvn test "-Dtest=ProductServiceTest#methodName"` | Quote on Windows |
| Build (skip tests) | `mvn package -DskipTests` | Produce JAR |
| Full verify | `mvn verify` | Compile + test + package |
| Clean build | `mvn clean package` | Remove target/, rebuild |
| Start app | `mvn spring-boot:run` | Requires MySQL running |

### 3. Apply to ALL Terminal Operations
This applies to:
- `mvn compile` — Verify compilation
- `mvn test` — Run JUnit tests
- `mvn spring-boot:run` — Start the app
- `mvn package` — Package JAR
- Any Spring Boot developer tools or CLI

### 4. Examples

**CORRECT ✓ — PowerShell**
```powershell
# Run tests
cd C:\Users\User\wis_backend\my-spring-project; mvn test

# Run one test class
cd C:\Users\User\wis_backend\my-spring-project; mvn test -Dtest=ProductServiceTest

# Compile and check for errors
cd C:\Users\User\wis_backend\my-spring-project; mvn compile
```

**INCORRECT ✗**
```powershell
# Missing cd — may run in wrong directory
mvn test

# Wrong separator on Windows (use ; not &&)
cd C:\Users\User\wis_backend && mvn test
```

### 5. Test Output Location
After running tests, reports appear at:
```
target/surefire-reports/
  *.txt — Human-readable summary
  *.xml — Detailed XML results
```

### 6. H2 Test Database
Tests use H2 in-memory database (see `src/test/resources/application.properties`).
**NEVER run tests against MySQL unless explicitly requested.**

### 7. Checking Compilation Errors
Use `mvn compile` (not `mvn test`) for a fast compilation check.  
Check VS Code Problems panel via `read/problems` tool for IDE-level errors.
