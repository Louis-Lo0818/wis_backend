# 04 - Feature: Pagination & Filtering

> Part of [WIS Backend Blueprint](./00_index.md)

**Priority:** P1  
**Difficulty:** `[KNOWN]`  
**Status:** `⏳ [TODO]`

---

## 📖 The Story

### 😤 The Pain → ✨ The Vision

```
┌─────────────────────────────────────────────────────────────────┐
│  BEFORE                        │  AFTER                         │
├────────────────────────────────┼────────────────────────────────┤
│  GET /api/products returns      │  GET /api/products?page=0      │
│  ALL 50,000 products            │  &size=20&sort=name,asc        │
│       ↓                        │       ↓                        │
│  💥 10s load time, UI freezes  │  ✅ <200ms, smooth paging      │
└────────────────────────────────┴────────────────────────────────┘
```

### 🎯 One-Liner

> Replace full-table-scan endpoints with Spring Data `Pageable` queries so the frontend can page through large datasets without performance degradation.

---

## 🔧 The Spec

---

## 🎯 Overview

Add `Pageable` support to the high-volume endpoints: `GET /api/products` and `GET /api/inventory`. Also add optional keyword search (`?search=`) and category filter (`?category=`) for products. Response wraps items in a `Page<DTO>` envelope with metadata (`totalElements`, `totalPages`, `page`, `size`).

---

## 👥 User Stories

| Story | Acceptance Criteria |
|-------|-------------------|
| As a user, I want the first 20 products | `GET /api/products?page=0&size=20` → 20 items + page metadata |
| As a user, I want products sorted by name | `GET /api/products?sort=name,asc` → alphabetical order |
| As a user, I want to search products | `GET /api/products?search=bolt` → products with "bolt" in name/code |
| As a user, I want to filter by category | `GET /api/products?category=fasteners` → filtered list |

---

## 🛠️ Technical Notes

### Layers Affected

| Layer | Change Required |
|-------|----------------|
| **Repository** | `findAll(Pageable)` (built-in) + `findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String, String, Pageable)` |
| **Service** | Accept `Pageable` param, return `Page<ProductDTO>` |
| **Controller** | Accept `@RequestParam` for `search`, `category`; pass `Pageable` (Spring auto-resolves from query params) |
| **DTO** | New `PagedResponseDTO<T>` wrapper (or use Spring's `Page<T>` directly) |

### API Contract

```
GET /api/products?page=0&size=20&sort=name,asc&search=bolt&category=fasteners

Response (200 OK):
{
  "content": [ { ProductDTO }, ... ],
  "totalElements": 150,
  "totalPages": 8,
  "page": 0,
  "size": 20
}
```

### Repository Methods

```java
Page<Product> findAll(Pageable pageable);

Page<Product> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
    String name, String code, Pageable pageable);

Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable);
```

---

## ⚠️ Edge Cases

| Scenario | Expected Behavior |
|----------|------------------|
| `size=0` | Return `400 Bad Request` — minimum size is 1 |
| `size=1000` | Cap at 100 items max (configure in service) |
| `page` beyond last page | Return empty `content: []`, still 200 OK |
| Invalid `sort` field (e.g., `sort=nonexistent,asc`) | Spring throws `PropertyReferenceException` → `400 Bad Request` via GlobalExceptionHandler |
| `search=` empty string | Return all results (treat as no filter) |

---

## ❌ Out of Scope

- Full-text search (Elasticsearch, Postgres FTS) — plain `LIKE` query is sufficient for now
- Cursor-based pagination — offset pagination adequate for this dataset size
- Pagination on `GET /api/transfers` and `GET /api/dashboard` (add in P2 if needed)

---

## 🔗 Dependencies

- Depends on: Existing `ProductRepository`, `InventoryRepository`
- Depends on: Spring Data JPA `Pageable` (built-in, no extra deps)
- Blocks: None
