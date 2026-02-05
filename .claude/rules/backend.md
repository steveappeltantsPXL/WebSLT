---
globs: server/**
---
# Backend Rules (Ktor)

## Architecture

Three-layer architecture. See `docs/Architecture.md` for full diagrams.

- **Routes** (API layer) -- Receive HTTP requests, validate input, delegate to services
- **Services** (Business logic) -- Orchestrate operations, enforce business rules
- **Repositories** (Data layer) -- Database access, abstracted behind interfaces

## Target Directory Structure

```
server/src/main/kotlin/be/tinvision/webslt/
  Application.kt          # Entry point, module configuration
  plugins/                 # Ktor plugin installations (Serialization, CORS, Auth, StatusPages)
  routes/                  # Route definitions grouped by feature
  services/                # Business logic interfaces + implementations
  repositories/            # Data access interfaces + implementations
  models/                  # Domain entities (plain data classes)
  dto/                     # Request/Response data transfer objects
  config/                  # Configuration classes, Koin modules
```

## Ktor Conventions

- Install plugins in separate extension functions: `Application.configureSerialization()`
- Group routes by feature: `Route.trainingDataRoutes()`
- Use `call.receive<T>()` with kotlinx.serialization
- Respond with `ApiResponse<T>` wrapper for consistency
- Use StatusPages for centralized error handling
- CORS: allow only configured frontend origins

## Code Patterns

- Services: interface + impl, injected via Koin
- Repositories: interface + impl, use Exposed for SQL
- DTOs: `@Serializable` data classes, separate from domain models
- Validation: in DTO or dedicated validator, throw on failure
- Async: use coroutines, `withContext(Dispatchers.IO)` for blocking DB calls

## Dependencies to Add

When implementing features, these must be added to `gradle/libs.versions.toml`:
- `kotlinx-serialization-json` + `ktor-server-content-negotiation`
- `ktor-server-cors`
- `ktor-server-auth-jwt`
- `ktor-server-status-pages`
- `koin-ktor` (dependency injection)
- `exposed-core`, `exposed-dao`, `exposed-jdbc` (ORM)
- `postgresql` + `HikariCP` (database)

## Don'ts

- No ML inference on server (browser-only)
- No blocking calls on main dispatcher
- No `!!` operator -- use safe calls + Elvis
- No hardcoded config values -- use application.conf or env vars
- No logging of passwords, JWTs, or PII

## Status

- [x] Application.kt with embedded Netty server
- [x] Basic GET / route
- [x] Ktor test host configured
- [ ] kotlinx-serialization + content negotiation
- [ ] CORS plugin
- [ ] StatusPages error handling
- [ ] Koin DI setup
- [ ] application.conf
- [ ] Route structure (auth, users, training-data, models, analytics)
- [ ] Service layer interfaces + implementations
- [ ] Repository layer with Exposed
- [ ] JWT authentication plugin
