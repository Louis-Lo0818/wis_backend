---
applyTo: "**/.agent_plan/day_dream/**"
---

# Blueprint Document Authoring Guidelines

## Goals
- Standardize vision, implementation, architecture, and feature documents for the Warehouse Inventory backend.
- Enforce constraints that keep planning documents focused and actionable.
- Ensure consistency across all WarehouseDream-generated artifacts.

---

## Tier Selection

Templates are tiered based on planning scope:

| Tier | Use When | Template |
|------|----------|----------|
| **Simple** | ≤2 features, single layer change, no external deps | `simple.template.md` |
| **Blueprint** | ≥3 features OR cross-layer changes OR external integrations | `blueprint/` folder |

### Auto-Detection Rules

```yaml
use_blueprint_tier:
  - feature_count >= 3
  - cross_layer_changes: true        # e.g., entity + service + controller
  - has_external_integration: true   # e.g., Redis, JWT, Flyway, PDF export
```

---

## Templates Location

All templates at: `.agent_plan/day_dream/templates/`

### Simple Tier
| Template | Purpose | Line Limit |
|----------|---------|------------|
| `simple.template.md` | Single-file vision + quick start | ≤200 lines |

### Blueprint Tier
| Template | Purpose | Line Limit |
|----------|---------|------------|
| `blueprint/00_index.template.md` | Navigation hub with flowchart | ≤150 lines |
| `blueprint/01_executive_summary.template.md` | Vision, goals, non-goals | ≤150 lines |
| `blueprint/02_architecture.template.md` | System diagrams, layer overview | ≤200 lines |
| `blueprint/NN_feature.template.md` | Per-feature Spring Boot details | ≤150 lines |
| `blueprint/NN_feature_simple.template.md` | Lightweight feature (80% of cases) | ≤100 lines |
| `blueprint/80_implementation.template.md` | Phased roadmap | ≤200 lines per phase |
| `blueprint/99_references.template.md` | External links | No limit |

### Assets (Multi-Modal Artifacts)
| Template | Purpose | Line Limit |
|----------|---------|------------|
| `assets/asset.template.md` | Architecture diagrams, API contracts, ER models | ≤100 lines |

**Asset Types:** `diagram`, `data-model`, `mockup`, `infrastructure`, `design`, `storyboard`, `other`  
**Naming:** `{feature_id}_{description}.asset.md` (e.g., `03_auth_flow_diagram.asset.md`)

---

## Status Syntax

Use hybrid emoji + text markers:

| Emoji | Text | Meaning |
|-------|------|---------|
| ⏳ | `[TODO]` | Not started |
| 🔄 | `[WIP]` | In progress |
| ✅ | `[DONE]` | Complete |
| 🚧 | `[BLOCKED:reason]` | Stuck (kebab-case reason) |
| 🚫 | `[CUT]` | Removed from scope |

**Example:** `⏳ [TODO]`, `🔄 [WIP]`, `✅ [DONE]`

---

## Difficulty Labels

Every feature/task MUST have a difficulty label:

| Label | Meaning | P0 Allowed? |
|-------|---------|-------------|
| `[KNOWN]` | Standard Spring Boot pattern, proven approach | ✅ Yes |
| `[EXPERIMENTAL]` | Approach exists but needs validation in this stack | ✅ Yes |
| `[RESEARCH]` | Active problem, no proven solution in this context | ❌ No |

---

## Phasing Rules (Walking Skeleton)

| Rule | Detail |
|------|--------|
| **Walking Skeleton First** | Phase 0 proves the integration works (e.g., JWT filter returns 401) |
| **No RESEARCH in P0** | Proven patterns only in Phase 0 |
| **Incremental Value** | Each phase delivers working, testable functionality |
| **Hard Duration Limit** | Phase 0 ≤ 5 days; Phase 1 ≤ 2 weeks |

---

## Document Set for Blueprint Tier

Minimum required documents (5):
1. `00_index.md` — Navigation hub
2. `01_executive_summary.md` — Vision and scope
3. `02_architecture.md` — Layer diagrams
4. `80_implementation.md` — Phased task tracking
5. At least one `NN_feature_*.md` — Feature detail

Optional additions:
- `03_feature_auth.md`, `04_feature_audit.md`, etc.
- `assets/` subfolder for diagrams and data models
- `99_references.md` for external docs and Spring Boot references

---

## Folder Structure

```
.agent_plan/
  day_dream/
    templates/          ← Authoring templates (not edited by agents)
      simple.template.md
      blueprint/
      assets/
    {vision_name}/      ← Actual vision documents (e.g., wis_backend_blueprint/)
      00_index.md
      01_executive_summary.md
      02_architecture.md
      03_feature_*.md
      80_implementation.md
      assets/
  red_team/             ← WarehouseRed attack findings (per feature)
```
