# 06 - Feature: Stock Alert Thresholds

> Part of [WIS Backend Blueprint](./00_index.md)

**Priority:** P2  
**Difficulty:** `[EXPERIMENTAL]`  
**Status:** `⏳ [TODO]`

---

## 📖 The Story

### 😤 The Pain → ✨ The Vision

```
┌─────────────────────────────────────────────────────────────────┐
│  BEFORE                        │  AFTER                         │
├────────────────────────────────┼────────────────────────────────┤
│  Stock of SKU-7 hits 0         │  Stock drops below threshold   │
│       ↓                        │       ↓                        │
│  💥 Discovered at order time   │  ✅ Alert record created auto  │
│  Team scrambles to reorder     │  GET /api/alerts → actionable  │
└────────────────────────────────┴────────────────────────────────┘
```

### 🎯 One-Liner

> Let warehouse managers set a minimum stock threshold per product, and auto-create an alert record whenever inventory drops below it after a transfer.

---

## 🔧 The Spec

---

## 🎯 Overview

Add a `min_stock` field to the `product` table. After every successful transfer, the `InventoryService` checks if the resulting stock for a location falls below `min_stock`. If so, a record is inserted into the `stock_alert` table. The frontend polls `GET /api/alerts/active` to show a badge count.

**Experimental Risk**: The threshold-check + alert-insert must be transactional. If alert creation fails, the transfer must NOT roll back (alerts are advisory, not blocking).

---

## 👥 User Stories

| Story | Acceptance Criteria |
|-------|-------------------|
| As a manager, I want to set min stock | `PUT /api/products/{id}/threshold` with `{"minStock": 50}` → `200 OK` |
| As a manager, I want to see active alerts | `GET /api/alerts/active` → list of products below threshold |
| As the system, alert is created on low stock | Post-transfer, if inventory < minStock → alert record created |
| As a manager, I want to dismiss an alert | `POST /api/alerts/{id}/dismiss` → alert marked resolved |

---

## 🛠️ Technical Notes

### Layers Affected

| Layer | Change Required |
|-------|----------------|
| **Entity** | `Product.minStock` field (nullable, default null = no threshold); new `StockAlert` entity |
| **Repository** | `StockAlertRepository` — `findByResolvedFalse()` |
| **Service** | `InventoryService.checkAndCreateAlert()` — called after transfer, separate transaction |
| **Controller** | `AlertController` — `GET /api/alerts/active`, `POST /api/alerts/{id}/dismiss` |
| **DTO** | `StockAlertDTO`, `ThresholdUpdateDTO` |

### New Table: `stock_alert`

```sql
CREATE TABLE stock_alert (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id  BIGINT       NOT NULL,
    location    VARCHAR(100) NOT NULL,
    current_qty INT          NOT NULL,
    min_stock   INT          NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved    BOOLEAN      NOT NULL DEFAULT FALSE,
    resolved_at DATETIME,
    FOREIGN KEY (product_id) REFERENCES product(id)
);
```

---

## ⚠️ Edge Cases

| Scenario | Expected Behavior |
|----------|------------------|
| `minStock` is null | Skip threshold check — no alert created |
| Multiple transfers in quick succession | One alert per check — dedup by `product_id + location + resolved=false` |
| Alert creation fails | Log error, do NOT roll back the transfer |
| Stock recovers above threshold | Alert remains until manually dismissed |

---

## ❌ Out of Scope

- Email/webhook push notifications (P3 — `[RESEARCH]`)
- Automatic reorder generation
- Per-location threshold (threshold is product-wide for now)

---

## 🔗 Dependencies

- Depends on: `TransferService`, `InventoryService` (P0 — done)
- Depends on: Product entity update (`min_stock` column in schema)
- Blocks: Nothing
