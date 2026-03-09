---
applyTo: "**/warehouse_orchestrator.agent.md"
---

# WarehouseOrch Discussion Preset

## Goals
- Facilitate structured N-way discussions between Warehouse agents
- Reach consensus through Propose → Challenge → Synthesize phases
- Handle impasse gracefully with clear documentation

## When This Applies
Trigger patterns: "discuss", "debate", "compare", "what should we", "which approach", "pros and cons"

## Discussion Protocol

### Identification

1. **TOPIC**: Extract the main topic from the user request.
2. **PARTICIPANTS**: Identify which agents should participate:
    - If user said "auto" or "auto-invite" → Infer relevant agents, proceed without asking
    - Otherwise → Propose participant list, wait for user confirmation

### Phase Structure
Each discussion round has 3 phases:
1. **PROPOSE**: Each participant states their position (1-3 sentences)
2. **CHALLENGE**: Each participant responds to ONE divergent view
3. **SYNTHESIZE**: WarehouseOrch drafts consensus, participants confirm or reject

### Execution Flow

```
Round N (max 3):
  PROPOSE → All agents state position (sequential)
  CHALLENGE → Agents respond to divergences (sequential)
  SYNTHESIZE → Draft consensus
    → All ACCEPT? → Exit with consensus
    → Disagreement? → Next round
    → Max rounds hit? → Exit with impasse
```

## Orchestration Steps

### 1. Initialize Discussion
- Parse topic from user request
- Identify participants (default: WarehouseArch + WarehouseSan)
- Maximum 4 participants per discussion
- State: "Starting discussion on: [topic] with [participants]"

### 2. PROPOSE Phase
For each participant (sequentially):
```yaml
task: "State your position on: [topic]"
objective: "[The larger goal this discussion serves]"
context: "[Prior round summary if any]"
success_criteria: "Provide 1-3 sentence position statement"
output_format: "summary"
```

Report table of positions — highlight agreed and divergent points.

### 3. CHALLENGE Phase
For each participant (sequentially):
```yaml
task: "Respond to this divergent position: [most opposing view]"
context: "[All positions from PROPOSE phase]"
success_criteria: "Address the specific divergence with reasoning"
output_format: "summary"
```

Report table of challenges — note any position shifts.

### 4. SYNTHESIZE Phase
Draft synthesis identifying:
- Common ground
- Resolution for divergences
- Consensus statement

Present to all participants:
```yaml
task: "Do you ACCEPT or REJECT this synthesis: [synthesis]"
context: "[Key points from prior phases]"
success_criteria: "Reply ACCEPT or REJECT with brief reason"
output_format: "summary"
```

### 5. Evaluate Exit Condition
- **All ACCEPT** → Report final consensus to user
- **Some REJECT, rounds < 3** → Begin next round from PROPOSE
- **Rounds = 3** → Report impasse — summarize competing positions, let user decide

## Participant Selection Guide

| Topic | Suggested Agents |
|-------|-----------------|
| API design / endpoint shape | WarehouseArch + WarehouseSan |
| Long-term feature planning | WarehouseDream + WarehouseArch |
| Test strategy | WarehouseRed + WarehouseArch |
| Code quality approach | WarehouseIQGuard + WarehouseArch |
| Architecture trade-offs | WarehouseDream + WarehouseSan + WarehouseArch |
