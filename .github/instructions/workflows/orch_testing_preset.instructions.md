---
applyTo: "**/warehouse_orchestrator.agent.md"
---

# WarehouseOrch Testing Preset

## Goals
- Orchestrate comprehensive testing workflows for the Warehouse backend
- Coordinate spec tests (WarehouseArch) and adversarial tests (WarehouseRed)
- Ensure quality through strategic validation

## When This Applies
Trigger patterns: "test", "validate", "attack", "check endpoints", "verify", "QA", "run tests"

## Testing Protocol

### Phase Structure
```
PLAN (WarehouseSan) → SPEC-TEST (WarehouseArch) → ATTACK (WarehouseRed) → FINAL (WarehouseSan)
        ↓                      ↓                        ↓                       ↓
   Review Test Plan        Run JUnit Tests         Edge Case Attacks       Final Validation
```

### Phase Flow
```
Phase 1: PLAN
  → WarehouseSan reviews test scope
  → If issues: Revise scope
  → If approved: Continue

Phase 2: SPEC-TEST (Loop)
  → WarehouseArch writes and runs JUnit/integration tests
  → If failures: Fix and re-run (max 3 cycles)
  → If all pass: Continue

Phase 3: ATTACK
  → WarehouseRed attacks implementation
  → If BLOCKERs found: Return to Phase 2
  → If no BLOCKERs: Continue

Phase 4: FINAL
  → WarehouseSan final validation
  → Complete
```

## Orchestration Steps

### 1. Initialize Testing
- Parse testing scope from user request
- Identify target layers (service unit tests OR controller integration tests)
- State: "Starting testing workflow for: [target]"

### 2. Phase 1: PLAN
Invoke WarehouseSan:
```yaml
task: "Review test plan for: [target]"
context: "User wants comprehensive tests. Identify test priorities, risks, and edge cases."
success_criteria: "Approve test plan or identify gaps"
output_format: "summary"
```

### 3. Phase 2: SPEC-TEST (Loop)
**Cycle Counter:** Start at 1, max 3

Invoke WarehouseArch:
```yaml
task: "Write and run tests for: [target]"
objective: "[The larger goal this testing serves]"
context: "Test plan: [WarehouseSan summary]. Cycle #[N]."
success_criteria: "All tests pass via: mvn test"
output_format: "summary"
execution_guidance: |
  <testing_standards>
  ## Test Types
  - Service layer: @ExtendWith(MockitoExtension.class), mock Repository
  - Controller integration: @SpringBootTest + @AutoConfigureMockMvc, H2 in-memory
  
  ## Test Conventions
  - @Test methods: given_when_then naming (e.g., givenValidProduct_whenCreate_thenReturns201)
  - Verify happy path AND exception paths
  - Assert response status, body fields, and error messages
  
  ## Run Command
  mvn test -Dtest=<TestClassName>
  OR
  mvn test (all tests)
  
  ## H2 In-Memory DB
  Tests use H2. Ensure schema.sql and data.sql load correctly for test context.
  </testing_standards>
```

**Evaluate:** All tests pass → Phase 3. Any failure → Fix and re-run (max 3 cycles).

### 4. Phase 3: ATTACK
Invoke WarehouseRed:
```yaml
task: "Adversarial testing for: [target]"
objective: "Find edge cases that break the API contract or expose bugs"
context: "Spec tests passed: [WarehouseArch summary]. Attack known edge cases."
success_criteria: "Report all BLOCKERs, WARNINGs found"
output_format: "summary"
```

**Evaluate:** No BLOCKERs → Phase 4. BLOCKERs found → Return to Phase 2.

### 5. Phase 4: FINAL
Invoke WarehouseSan for final validation summary.

### 6. Report
- **Status**: PASSED / PARTIAL / FAILED
- **Test Count**: Tests written/run
- **Issues Found**: Any remaining warnings
- **Coverage Notes**: Layers tested
