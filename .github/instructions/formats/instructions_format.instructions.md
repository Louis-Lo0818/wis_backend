---
applyTo: "**/*.instructions.md"
---

# Instructions File Authoring Guidelines

## Goals
- Create rule sets that automatically apply when editing matching files.
- Ensure consistent code style, structure, and conventions across the Warehouse backend project.
- Provide clear, enforceable guidelines for AI agents.

## Required Structure
Each instructions file MUST include:
1) **YAML Header**: Frontmatter with `applyTo` glob pattern (and optional `description`).
2) **Title**: A clear `# Title` heading.
3) **Goals Section**: What these instructions aim to achieve.
4) **Rules/Guidelines**: The actual rules to enforce.
5) **Template/Examples**: If applicable, provide templates.

## Template
```markdown
---
applyTo: "<glob pattern for target files>"
---

# <Instructions Title>

## Goals
- <Goal 1>
- <Goal 2>

## Rules
1. **<Rule Name>**: <Description>
2. **<Rule Name>**: <Description>

## Template (if applicable)
\`\`\`
<Template content>
\`\`\`

## Examples (if applicable)
<example>
<Good example>
</example>
```

## applyTo Patterns
The `applyTo` field uses glob patterns to match files:
- `src/**/*.java` — All Java source files
- `src/main/java/**/controller/*.java` — Controller classes only
- `src/main/java/**/entity/*.java` — JPA entity classes only
- `src/test/**/*.java` — All test classes
- `**/*.agent.md` — All agent definition files
- `**/*.prompt.md` — All prompt files
- `**/.agent_plan/day_dream/**` — All planning documents
- `src/**/*.java, **/*.agent.md` — Multiple patterns (comma-separated)

## Naming Convention
- Use lowercase snake_case ending in `.instructions.md` (e.g., `service_layer.instructions.md`).
- Place in `.github/instructions/` or appropriate subfolder:
  - `.github/instructions/` — Core Spring Boot layer guidelines
  - `.github/instructions/agents/` — Agent authoring and format rules
  - `.github/instructions/formats/` — File format standards
  - `.github/instructions/planning/` — Planning artifact standards
  - `.github/instructions/workflows/` — Orchestration workflow presets

## Best Practices
- Be specific with `applyTo` patterns to avoid over-matching.
- Use imperative tone ("MUST", "NEVER", "ALWAYS").
- Provide clear examples of correct vs incorrect patterns.
- Keep rules concise and actionable.
- Reference other instruction files when rules depend on project context.
