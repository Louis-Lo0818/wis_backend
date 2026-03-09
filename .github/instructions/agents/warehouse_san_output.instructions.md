---
applyTo: "**/warehouse_san_checker.agent.md"
---

# WarehouseSan Output Format Instructions

## Goals
- Standardize WarehouseSan output for consistent parsing by WarehouseOrch.
- Enable efficient machine-readable output when invoked as a subagent.
- Provide actionable fix recommendations based on severity and difficulty.

## Output Mode Detection
WarehouseSan MUST detect its invocation context:
- **SUBAGENT mode**: Called via `runSubagent` / `agent` tool by WarehouseOrch.
- **DIRECT mode**: User interacting directly in chat.

## Severity Levels
| Level | Meaning | Action Required |
|-------|---------|-----------------|
| `BLOCKER` | Critical architectural or schema issue preventing implementation | MUST fix before proceeding |
| `WARNING` | Significant concern that should be addressed | SHOULD fix |
| `SUGGESTION` | Minor improvement opportunity | MAY fix |

## Fix Difficulty Levels
| Difficulty | Criteria | Fix Recommendation |
|------------|----------|-------------------|
| `EASY` | Single-file change, annotation tweak, rename | Fix for ALL severity levels |
| `MEDIUM` | Multi-file changes, service/controller refactor | Fix for `WARNING` and `BLOCKER` only |
| `HARD` | Architectural change, touches entity schema or multiple layers | Fix for `BLOCKER` only |

## Difficulty Reasoning (Required)
Each issue MUST include a brief explanation of why it's classified at that difficulty:
- **EASY examples**: "add missing @Transactional", "rename DTO field", "add @Valid"
- **MEDIUM examples**: "update service + controller consistently", "refactor DTO mapping in 2 files"
- **HARD examples**: "requires schema change + entity + migration", "affects 3+ service methods"

## SUBAGENT Mode Output (JSON)
When invoked as subagent, output ONLY valid JSON with NO surrounding text:

```json
{
  "status": "VALID|NEEDS_FIX|INVALID",
  "passed": true,
  "issues": [
    {
      "severity": "BLOCKER|WARNING|SUGGESTION",
      "difficulty": "EASY|MEDIUM|HARD",
      "difficulty_reason": "brief explanation why this difficulty",
      "description": "clear issue description",
      "fix_suggested": true,
      "fix_hint": "brief guidance on how to fix"
    }
  ],
  "summary": "one-line summary of overall status"
}
```

### JSON Field Definitions
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `status` | string | Yes | `VALID` (no issues), `NEEDS_FIX` (has issues), `INVALID` (fundamentally flawed) |
| `passed` | boolean | Yes | `true` if no blockers and implementation can proceed |
| `issues` | array | Yes | List of issues found (empty array if none) |
| `summary` | string | Yes | One-line human-readable summary |

### Issue Object Fields
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `severity` | string | Yes | `BLOCKER`, `WARNING`, or `SUGGESTION` |
| `difficulty` | string | Yes | `EASY`, `MEDIUM`, or `HARD` |
| `difficulty_reason` | string | Yes | Brief explanation of difficulty classification |
| `description` | string | Yes | Clear description of the issue |
| `fix_suggested` | boolean | Yes | Whether a fix is recommended based on severity+difficulty matrix |
| `fix_hint` | string | If fix_suggested | Brief guidance on how to resolve |

## DIRECT Mode Output (Conversational)
When user interacts directly, use structured conversational format:

```
**Status**: VALID | NEEDS_FIX | INVALID

**Issues Found**: N

---

### Issue 1 — [SEVERITY] [DIFFICULTY]
**Problem**: {description}
**Fix**: {fix_hint}

---

**Summary**: {one-line summary}
```

## Warehouse-Specific Validation Checklist
When reviewing Spring Boot code, WarehouseSan MUST check:

| Check | What to Verify |
|-------|---------------|
| **Schema Compliance** | Entity fields match MySQL column names/types exactly |
| **Transaction Safety** | Multi-entity writes wrapped in `@Transactional` |
| **Layered Architecture** | No business logic in controllers; no repository calls in controllers |
| **Validation** | DTOs annotated with Jakarta Validation (`@NotBlank`, `@Positive`, etc.) |
| **Exception Handling** | Services throw proper exceptions caught by `GlobalExceptionHandler` |
| **API Contracts** | Response shapes match what the React frontend expects |
| **Lombok Consistency** | `@Getter`, `@Setter`, `@Builder`, `@Data` used consistently |
| **N+1 Queries** | No lazy loading inside loops |
