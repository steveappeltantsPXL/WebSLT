---
globs: server/src/main/kotlin/be/tinvision/webslt/routes/**, server/src/main/kotlin/be/tinvision/webslt/dto/**
---
# API Design Rules

## URL Conventions

- Base path: `/api/v1/`
- Resource-oriented: nouns, not verbs
- Plural resource names: `/users`, `/models`
- Nested for ownership: `/users/me/settings`
- Kebab-case: `/training-data` not `/trainingData`

## Planned Endpoints

### Phase 1 (Foundation)
```
GET    /api/v1/health                     # Health check
POST   /api/v1/auth/register              # Create account
POST   /api/v1/auth/login                 # Get JWT
POST   /api/v1/auth/refresh               # Refresh token
POST   /api/v1/auth/logout                # Invalidate token
```

### Phase 2 (Core API)
```
GET    /api/v1/users/me                   # Current user profile
PATCH  /api/v1/users/me                   # Update profile
POST   /api/v1/training-data              # Submit landmarks
GET    /api/v1/training-data/stats        # Contribution stats
GET    /api/v1/models/latest              # Latest model metadata
GET    /api/v1/models/{version}/download  # Download model
```

### Phase 3 (Analytics)
```
POST   /api/v1/analytics/events           # Batch event ingestion
GET    /api/v1/analytics/dashboard        # Admin stats
```

## Response Format

All responses are wrapped in ApiResponse:
```json
{ "success": true, "data": { }, "error": null }
```
```json
{ "success": false, "data": null, "error": "Validation failed" }
```

## HTTP Status Codes

- 200: Success (GET, PATCH)
- 201: Created (POST that creates a resource)
- 204: No Content (DELETE)
- 400: Validation error (malformed input)
- 401: Unauthorized (missing/invalid JWT)
- 403: Forbidden (valid JWT, insufficient permissions)
- 404: Resource not found
- 429: Rate limited
- 500: Internal server error

## DTO Conventions

- Request DTOs: `Create*Request`, `Update*Request`
- Response DTOs: `*Response`
- All DTOs: `@Serializable` data classes
- Never expose internal database IDs (use UUIDs)
- Timestamps: ISO 8601 format

## Authentication

- JWT Bearer token in `Authorization` header
- Short-lived access tokens (24h) + refresh tokens
- Rate limiting on auth endpoints

## Pagination

- Query params: `?page=1&size=20`
- Response: include `totalItems`, `totalPages`, `currentPage`

## Status

- [x] GET / returns greeting (placeholder)
- [x] API versioning (/api/v1/ prefix)
- [x] ApiResponse<T> wrapper
- [x] GET /api/v1/health endpoint
- [ ] Request/Response DTO classes
- [ ] Auth endpoints (register, login, refresh, logout)
- [ ] User endpoints
- [ ] Training data endpoints
- [ ] Model endpoints
- [ ] Analytics endpoints
- [ ] Pagination support