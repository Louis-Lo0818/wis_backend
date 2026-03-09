---
applyTo: "**/.agent_plan/day_dream/assets/**"
---

# Day Dream Asset Authoring Guidelines

## Purpose
Asset files capture non-code artifacts that support planning: API diagrams, database schemas, architecture mockups, data models, and design documents for the Warehouse Inventory System.

---

## Asset Types

| Type | Use For | Example Filename |
|------|---------|------------------|
| `diagram` | Architecture, flow, sequence, ER diagrams | `02_auth_flow_diagram.asset.md` |
| `data-model` | Entity relationships, MySQL schema sketches | `03_inventory_data_model.asset.md` |
| `mockup` | API contract sketches, response shape drafts | `04_transfer_api_mockup.asset.md` |
| `infrastructure` | Deployment, Docker, CI/CD topology | `01_infra_layout.asset.md` |
| `design` | Naming conventions, error handling specs | `05_error_design.asset.md` |
| `storyboard` | User journey through the API | `06_csv_import_storyboard.asset.md` |
| `other` | Anything not above | `07_research_notes.asset.md` |

---

## Naming Convention

```
{feature_id}_{description}.asset.md
```

- **feature_id**: Two-digit number matching related feature doc (e.g., `03` for `03_feature_auth.md`)
- **description**: Lowercase, underscore-separated description
- **Example**: `03_auth_flow_diagram.asset.md`, `02_inventory_er_diagram.asset.md`

---

## Required Sections

Every asset file MUST include:

```markdown
# {Asset Title}

**Type:** {diagram|data-model|mockup|infrastructure|design|storyboard|other}  
**Related Feature:** [Feature Title](../blueprint/NN_feature.md)  
**Status:** `⏳ [TODO]` | `🔄 [WIP]` | `✅ [DONE]` | `🚧 [BLOCKED:reason]` | `🚫 [CUT]`

## Context
Why this asset exists and what problem it addresses.

## The Artifact
The actual content: Mermaid diagram, ASCII API contract, schema sketch, etc.

## Constraints
Limitations, assumptions, or fixed requirements this asset respects.

## Related Features
Links to other features or assets this depends on or affects.
```

---

## Content Guidelines

### Diagrams
- Use **Mermaid** for all flowcharts, sequences, ER diagrams, state machines
- Maximum diagram complexity: Fit on one screen (no scrolling)
- For complex systems, split into multiple focused diagrams

### API Contract Mockups
- Show request/response JSON shape in code blocks
- Label with HTTP method + path
- Include error response shapes

### Data Models
- Use Mermaid ER diagrams or ASCII table sketches
- Include MySQL column types matching `schema.sql`

---

## Line Limits

| Section | Limit |
|---------|-------|
| Context | ~20 lines |
| Constraints | ~10 lines |
| Related Features | ~10 lines |
| **Total (excluding diagrams)** | **≤100 lines** |

Diagrams and code blocks do NOT count toward line limit.

---

## Linking Convention

### From Feature to Asset
In a feature doc, link assets like:
```markdown
## Related Assets
- [Auth Flow Diagram](../assets/03_auth_flow_diagram.asset.md) — JWT token flow
```

### From Asset to Feature
In each asset, link the related feature:
```markdown
**Related Feature:** [JWT Authentication](../blueprint/03_feature_auth.md)
```
