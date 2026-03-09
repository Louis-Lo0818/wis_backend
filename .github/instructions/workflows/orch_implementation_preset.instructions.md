---
applyTo: "**/warehouse_orchestrator.agent.md"
---

# WarehouseOrch Implementation Preset

## Goals
- Orchestrate implementation workflows with mandatory quality gates
- Ensure pre and post sanity checks via WarehouseSan
- Maintain context efficiency through delegation

## When This Applies
Trigger patterns: "implement", "build", "create", "fix", "add feature", "add endpoint", "modify"

## Implementation Protocol

### Phase Structure
```
PRE-CHECK (WarehouseSan) → IMPLEMENT (WarehouseArch) → POST-CHECK (WarehouseSan) → [DOC-UPDATE (WarehouseDream)]
        ↓                          ↓                            ↓                           ↓
   Validate Feasibility       Build Feature               Validate Result           Update Blueprint
```

### Phase Flow
```
Phase 1: PRE-CHECK
  → WarehouseSan validates feasibility
  → If FAILED: Report blockers, HALT
  → If PASSED: Continue

Phase 2: IMPLEMENT
  → WarehouseArch implements feature
  → If FAILED: Report issues, suggest fixes
  → If PASSED: Continue

Phase 3: POST-CHECK
  → WarehouseSan validates implementation
  → If FAILED: Return to Phase 2 (max 2 retries)
  → If PASSED: Continue

Phase 4: DOC-UPDATE (Conditional)
  → Trigger: Source is blueprint implementation doc (80_implementation.md)
  → WarehouseDream updates the implementation doc
  → Mark completed tasks, update status
  → If NOT triggered: Skip to Finalize
```

## Orchestration Steps

### 1. Initialize Implementation
- Parse feature description from user request
- Identify target layers (entity, repository, service, controller, DTO)
- State: "Starting implementation workflow for: [feature]"

### 2. Phase 1: PRE-CHECK
Invoke WarehouseSan:
```yaml
task: "Pre-implementation sanity check for: [feature description]"
context: "User wants to implement: [full request]. Target layers: [entity/service/controller]"
success_criteria: "Validate feasibility, check schema alignment, identify risks"
output_format: "JSON (SUBAGENT mode)"
```

**Evaluate Response:**
- If `passed: true` or no BLOCKERs → Continue to Phase 2
- If `passed: false` or BLOCKERs found → Report to user, HALT workflow

### 3. Phase 2: IMPLEMENT
Invoke WarehouseArch:
```yaml
task: "Implement: [feature description]"
objective: "[The larger goal this implementation serves]"
context: "Pre-check passed. WarehouseSan notes: [summary]"
autonomy_guidance: |
  Your goal is OBJECTIVE COMPLETION, not just task execution.
  If completing the objective requires related work, do it and report it.
success_criteria: "Complete implementation following Spring Boot layered architecture"
output_format: "summary"
execution_guidance: |
  <implementation_standards>
  ## Pre-Coding Verification
  - Read existing entities to confirm field names and types match schema.sql
  - Check repository interfaces for existing query methods before creating new ones
  - Read service layer for existing patterns and DTO mapping conventions
  
  ## Coding Standards
  - **Structure**: Layered Architecture — Entity → Repository → Service → Controller
  - **File Size**: ~300 lines target, 500 max
  - **Imports**: Use IDE-verified imports. NEVER invent class names.
  - **Lombok**: @Getter, @Setter, @Builder, @RequiredArgsConstructor consistently
  
  ## Patterns
  - Use @Transactional for any method writing to multiple entities
  - Use GlobalExceptionHandler — NEVER catch RuntimeExceptions in controllers
  - Use @Valid on @RequestBody parameters
  - Use ResponseEntity<T> return types in controllers
  
  ## Anti-Hallucination Checklist
  - [ ] Read entity class before mapping DTO fields
  - [ ] Verify repository method names compile (JPA derived query rules)
  - [ ] Confirm MySQL column names match entity @Column annotations
  - [ ] Run: mvn compile OR mvn test to verify
  </implementation_standards>
```

**Evaluate Response:**
- If success → Continue to Phase 3
- If failure → Report issues to user, ask for guidance

### 4. Phase 3: POST-CHECK
Invoke WarehouseSan:
```yaml
task: "Post-implementation validation for: [feature]"
context: "WarehouseArch implemented: [summary of changes]"
success_criteria: "Validate correctness, schema alignment, API contract"
output_format: "JSON (SUBAGENT mode)"
```

**Evaluate Response:**
- If `passed: true` → Continue to Phase 4
- If `passed: false` → Return to Phase 2, decrement retry counter
- If retries exhausted → Report to user, HALT

### 5. Phase 4: DOC-UPDATE (Conditional)
Check: Was this triggered from `80_implementation.md`?
- **YES** → Invoke WarehouseDream to mark tasks complete
- **NO** → Skip

### 6. Finalize
Report to user:
- **Status**: SUCCESS / PARTIAL / FAILED
- **What was built**: Files modified/created, endpoints added
- **Frontend Impact**: Any API contract changes
- **Outstanding items**: Warnings or suggestions from WarehouseSan
