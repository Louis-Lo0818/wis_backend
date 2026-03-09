# 03 - Feature: JWT Authentication

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
│  curl -X DELETE /api/products/1│  DELETE /api/products/1        │
│       ↓                        │       ↓                        │
│  💥 200 OK — data gone!        │  401 Unauthorized — token req  │
│  Anyone on the network          │  Only authenticated users      │
│  can destroy warehouse data     │  can mutate data               │
└────────────────────────────────┴────────────────────────────────┘
```

### 🎯 One-Liner

> Add stateless JWT authentication to secure all write operations while keeping read-only endpoints accessible for the dashboard.

---

## 🔧 The Spec

---

## 🎯 Overview

Implement Spring Security 6 with a stateless JWT filter chain. A new `/api/auth/login` endpoint issues JWTs. All `POST`, `PUT`, `DELETE` operations require a valid `Bearer` token. `GET` endpoints remain public for the React frontend's public dashboard views.

---

## 👥 User Stories

| Story | Acceptance Criteria |
|-------|-------------------|
| As a warehouse user, I want to log in and get a token | `POST /api/auth/login` with valid credentials → `200 OK + {token, expiresIn}` |
| As an authenticated user, I want to create/modify products | `POST /api/products` with `Authorization: Bearer <token>` → `201 Created` |
| As an unauthenticated caller, I should be rejected | `POST /api/products` without token → `401 Unauthorized` |
| As a public viewer, I can still see dashboard stats | `GET /api/dashboard` without token → `200 OK` |

---

## 🛠️ Technical Notes

### Layers Affected

| Layer | Change Required |
|-------|----------------|
| **Entity** | New `users` table: `id`, `username`, `password` (BCrypt), `role` |
| **Repository** | `UserRepository` — `findByUsername(String)` |
| **Service** | `AuthService` — login validation, JWT generation |
| **Controller** | `AuthController` — `POST /api/auth/login` |
| **DTO** | `LoginRequestDTO`, `LoginResponseDTO` (token + expiresIn) |
| **Config** | `SecurityConfig` — filter chain, permitted endpoints |
| **Security** | `JwtUtil` — generate/validate JWT; `JwtAuthenticationFilter` — per-request filter |

### API Contract

```
POST /api/auth/login
Request:  { "username": "admin", "password": "secret" }
Response: { "token": "eyJ...", "expiresIn": 86400 }
Errors:   401 Unauthorized — invalid credentials
          400 Bad Request — missing fields
```

### Security Configuration

```
Public (no auth): GET /api/products, GET /api/inventory, GET /api/dashboard, GET /api/transfers
                  POST /api/auth/login
                  GET /swagger-ui.html, GET /v3/api-docs/**

Protected (JWT):  POST, PUT, DELETE on /api/products
                  POST /api/transfers
                  POST /api/import
```

### JWT Spec

| Property | Value |
|---------|-------|
| Algorithm | HS256 |
| Expiry | 24 hours |
| Claim | `sub` = username, `roles` = ["WAREHOUSE_STAFF"] |
| Secret | Loaded from `application.yml` (`jwt.secret`) |

---

## ⚠️ Edge Cases

| Scenario | Expected Behavior |
|----------|------------------|
| Expired token | `401 Unauthorized` with message "Token expired" |
| Tampered token signature | `401 Unauthorized` with message "Invalid token" |
| Missing Authorization header | `401 Unauthorized` |
| Wrong password | `401 Unauthorized` (do NOT reveal which field is wrong) |
| First-time setup (no users) | Seed `admin` user in `data.sql` with BCrypt password |

---

## ❌ Out of Scope

- OAuth2 / social login (P3+)
- Refresh tokens (can add in P2 if needed)
- Per-endpoint role-based access (RBAC beyond simple auth)
- Password reset / email verification

---

## 🔗 Dependencies

- Depends on: Spring Security 6 (`spring-boot-starter-security`)
- Depends on: JWT library (`io.jsonwebtoken:jjwt-api:0.11.5`)
- Blocks: None (all other P1 features can coexist)

---

## 🖼️ Related Assets

- [JWT Auth Flow Diagram](../assets/03_auth_flow_diagram.asset.md)
