---
applyTo: "**/*.prompt.md"
---

# Prompt File Authoring Guidelines

## Goals
- Create reusable prompt templates that guide AI agents through specific tasks.
- Ensure prompts are clear, actionable, and self-contained.
- Standardize the structure of `.prompt.md` files for consistent behavior.

## Required Structure
Each prompt file MUST include:
1) **YAML Header**: Frontmatter with `description` field.
2) **Title**: A clear `# Title` heading.
3) **Task Description**: Explanation of what the prompt accomplishes.
4) **Steps/Instructions**: Clear, numbered steps or sections.
5) **Default Behavior**: State any skip conditions or defaults.

## Template
```markdown
---
description: <Short description for chat input placeholder>
---

# <Prompt Title>

<Brief description of what this prompt does.>

## Task 1: <Section Name>

<Explanation of this task.>

**Steps:**
1. <Step 1>
2. <Step 2>
3. <Step 3>

## Task 2: <Section Name> (if applicable)

<Explanation of this task.>

---

**Default behavior**: <State any default skip conditions or behaviors.>
**To override**: <State how user can modify default behavior.>
```

## Naming Convention
- Use lowercase snake_case ending in `.prompt.md` (e.g., `create_api_endpoint.prompt.md`).
- Place in `.github/prompts/`.

## Best Practices
- Be explicit about what gets skipped by default (e.g., "Skip tests unless asked").
- Include clear override instructions for optional behaviors.
- Use imperative tone ("Update...", "Check...", "Verify...", "Implement...").
- Reference relevant `.instructions.md` files for format guidance.
- Target a specific agent or workflow where applicable.

## Examples

### Single-Task Prompt
```markdown
---
description: Add a new REST endpoint to the Warehouse API
---

# Create API Endpoint

Creates a complete endpoint stack: Entity → Repository → Service → Controller → DTO.

## Task 1: Define the endpoint contract

**Steps:**
1. Identify the HTTP method and path (e.g., GET /api/products/{id})
2. Define request body (DTO) if POST/PUT
3. Define response body (DTO)

## Task 2: Implement the stack

**Steps:**
1. Add DTO fields with Jakarta Validation annotations
2. Add Repository method if needed
3. Add Service method with @Transactional (if writes)
4. Add Controller method with @Valid and ResponseEntity
```

### Multi-Phase Prompt (uses WarehouseOrch)
```markdown
---
description: Validate and implement a backend feature end-to-end
---

# Validate Backend

Runs WarehouseSan pre-check → WarehouseArch implementation → WarehouseSan post-check.

**Default behavior**: Full three-phase workflow.
**To override**: Prefix with "arch only" to skip sanity checks.
```
