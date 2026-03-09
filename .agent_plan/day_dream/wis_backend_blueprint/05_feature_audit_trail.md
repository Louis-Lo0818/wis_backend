# 05 - Feature: Audit Trail

> Part of [WIS Backend Blueprint](./00_index.md)

**Priority:** P2  
**Difficulty:** `[KNOWN]`  
**Status:** `⏳ [TODO]`

---

## 📖 The Story

### 😤 The Pain → ✨ The Vision

```
┌─────────────────────────────────────────────────────────────────┐
│  BEFORE                        │  AFTER                         │
├────────────────────────────────┼────────────────────────────────┤
│  "Who moved 500 units of bolt  │  GET /api/audit/transfers/42   │
│   #SKU-42 last Thursday?"      │       ↓                        │
│       ↓                        │  { by: "jdoe", at: "2026-...", │
│  💥 No idea — no audit log     │    from: "A1", to: "B3",       │
│                                │    quantity: 500 }             │
└────────────────────────────────┴────────────────────────────────┘
```

### 🎯 One-Liner

> Record who performed each stock transfer, when, and from which source, into a permanent audit log queryable by the React frontend.

---

## 🔧 The Spec

---

## 🎯 Overview

When JWT auth is active (P1), capture the authenticated user's username on every stock transfer. Store this in an `audit_log` table alongside the transfer details. Expose a read-only `/api/audit/transfers` endpoint for accountability queries.

---

## 👥 User Stories

| Story | Acceptance Criteria |
|-------|-------------------|
| As a manager, I want to see who did each transfer | `GET /api/audit/transfers` returns log with `performedBy` username |
| As a manager, I want to filter by product | `GET /api/audit/transfers?productId=5` → transfers for that product |
| As the system, each transfer auto-logs the actor | Successful `POST /api/transfers` → audit record created atomically |

---

## 🛠️ Technical Notes

### Layers Affected

| Layer | Change Required |
|-------|----------------|
| **Entity** | New `AuditLog` entity → `audit_log` table |
| **Repository** | `AuditLogRepository` — `findByProductId(Long, Pageable)` |
| **Service** | `TransferService.transfer()` extended to write audit record; `AuditService` for queries |
| **Controller** | New `AuditController` — `GET /api/audit/transfers` |
| **DTO** | `AuditLogDTO` (transferId, productCode, fromLocation, toLocation, quantity, performedBy, timestamp) |

### New Table: `audit_log`

```sql
CREATE TABLE audit_log (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    transfer_id    BIGINT         NOT NULL,
    product_id     BIGINT         NOT NULL,
    from_location  VARCHAR(100)   NOT NULL,
    to_location    VARCHAR(100)   NOT NULL,
    quantity       INT            NOT NULL,
    performed_by   VARCHAR(100)   NOT NULL,  -- JWT username
    performed_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transfer_id) REFERENCES transfer_log(id),
    FOREIGN KEY (product_id)  REFERENCES product(id)
);
```

### API Contract

```
GET /api/audit/transfers?productId=5&page=0&size=20

Response (200 OK):
{
  "content": [
    {
      "id": 101,
      "transferId": 42,
      "productCode": "SKU-42",
      "fromLocation": "A1",
      "toLocation": "B3",
      "quantity": 500,
      "performedBy": "jdoe",
      "performedAt": "2026-03-09T14:23:00"
    }
  ],
  "totalElements": 1,
  "page": 0,
  "size": 20
}
```

---

## ⚠️ Edge Cases

| Scenario | Expected Behavior |
|----------|------------------|
| Transfer fails mid-way | Audit record NOT written (same @Transactional scope) |
| P1 auth not yet implemented | Use `"system"` as `performed_by` fallback |
| Querying audit for deleted product | Return records with `productCode` cached in audit row |

---

## ❌ Out of Scope

- Auditing product CREATE/UPDATE/DELETE (only transfers for now)
- Tamper-evident / immutable audit log (cryptographic hashing is P3 `[RESEARCH]`)
- Audit log retention policy / auto-purge

---

## 🔗 Dependencies

- Depends on: `TransferService` (P0 — already done)
- Ideally depends on: JWT Authentication (P1) for `performed_by` username
- Blocks: Nothing

---

## 🖼️ Related Assets

- [Audit Log Data Model](../assets/05_audit_log_data_model.asset.md)
