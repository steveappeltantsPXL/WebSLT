# 🎯 WebSLT Backend - Complete Project Context

**Last Updated**: Feb 6, 2026 | **Phase**: 1 (Complete & Verified) | **Status**: Ready for Phase 2

---

## Quick Facts

| Aspect            | Value                                                    |
|-------------------|----------------------------------------------------------|
| **Language**      | Kotlin 2.3.0                                             |
| **Framework**     | Ktor 3.3.3 (JVM) + Netty                                 |
| **Database**      | PostgreSQL 15 + Exposed ORM + Flyway migrations          |
| **Auth**          | JWT tokens + BCrypt password hashing                     |
| **Package**       | `be.tinvision.webslt`                                    |
| **Server Port**   | 8080                                                     |
| **Build System**  | Gradle 8.14.3 with version catalog                       |
| **Frontend Repo** | Separate: WebSLT-Frontend (React 18 + TypeScript + Vite) |

---

## Phase 1: ✅ COMPLETE (Feb 6, 2026)

### Implemented Features

**API Endpoints** (5 total):
- `GET /api/v1/health` - Health check
- `POST /api/v1/auth/register` - Register user + JWT
- `POST /api/v1/auth/login` - Login + JWT
- `POST /api/v1/auth/refresh` - Refresh token
- `POST /api/v1/auth/logout` - Logout (protected)

**Infrastructure**:
- ✅ Ktor plugins (Serialization, CORS, StatusPages, Auth/JWT, Dependency Injection)
- ✅ PostgreSQL connection pool (HikariCP)
- ✅ Database migrations (Flyway)
- ✅ User authentication (JWT + BCrypt)
- ✅ Configuration management (application.conf + env vars)

**Code Structure** (752 lines Kotlin):
- `plugins/` - Ktor plugin configuration (7 files, 159 lines)
- `routes/` - HTTP endpoints (2 files, 179 lines)
- `services/` - Business logic (2 files, 134 lines)
- `repositories/` - Data access (2 files, 96 lines)
- `models/` - Domain models (User, UserSettings)
- `dto/` - Request/Response objects (3 files, 63 lines)
- `config/` - Database + DI setup (2 files, 60 lines)

---

## Critical Build & Run Steps

### Build
```bash
./gradlew clean build
```
**Result**: Compiles all modules, runs tests, copies resources to build/

### Run Server
```bash
./gradlew server:run
```
**Prerequisites**:
- PostgreSQL running with: `postgres`/`password` on `localhost:5432`
- Database exists: `webslt`

**Expected Output**:
```
✅ HikariPool-1 - Added connection
✅ Flyway migrations - Successfully validated 1 migration
✅ Database initialized successfully
✅ Application running at http://0.0.0.0:8080
```

### PostgreSQL Setup (Docker)
```bash
docker run --name webslt-postgres \
  -e POSTGRES_DB=webslt \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres:15
```

---

## Key Configuration Files

### application.conf
Location: `server/src/main/resources/application.conf`
- Ktor deployment settings
- Database connection (url, driver, username, password)
- JWT secrets and expiration
- CORS allowed origins

**Default Credentials** (matches Docker):
- Database: `postgres` / `password`
- JWT Secret: `change-me-in-production-use-environment-variable`

### gradle/libs.versions.toml
Centralized dependency management - ALL versions defined here.
Never hardcode versions in build.gradle.kts.

Key dependencies:
- Ktor: 3.3.3
- Kotlin: 2.3.0
- Exposed: 0.41.1
- PostgreSQL Driver: 42.7.2
- JWT: 4.4.0
- BCrypt: 0.10.1
- Flyway: 9.22.3

---

## Recent Fixes (Feb 6, 2026)

These were the final bugs preventing Phase 1 completion:

### 1. ✅ Application.kt - Config Not Loading
**Error**: `Property webslt.database.url not found`
**Fix**: Changed `main()` to use `EngineMain.main(args)` instead of `embeddedServer()`
- Automatically loads `application.conf` from classpath
- Resolves all config properties correctly

### 2. ✅ DatabaseConfig.kt - Missing Credentials
**Error**: `The server requested SCRAM-based authentication, but no password was provided`
**Fix**: Added USERNAME and PASSWORD to HikariCP config
- Reads from `application.conf` and environment variables
- Defaults to `postgres`/`password`

### 3. ✅ CORS.kt - Invalid Scheme
**Error**: `scheme should be specified as a separate parameter schemes`
**Fix**: Updated `allowHost()` calls to use `schemes` parameter
- Strips `http://` and `https://` from configured origins
- Supports both schemes: `listOf("http", "https")`

---

## Phase 2: Core API (Ready to Implement)

### Planned Endpoints
```
GET    /api/v1/users/me                   # Current user profile
PATCH  /api/v1/users/me                   # Update profile
POST   /api/v1/training-data              # Submit landmarks
GET    /api/v1/training-data/stats        # Contribution stats
GET    /api/v1/models/latest              # Latest model metadata
GET    /api/v1/models/{version}/download  # Download model
```

### Implementation Guide
See: `~\.claude\projects\Projects-Visear-WebSLT\memory\Phase2-CoreAPI-Implementation.md`

---

## Documentation Map

| File                               | Purpose                        | Status     |
|------------------------------------|--------------------------------|------------|
| `README.md`                        | Project overview + quick start | ✅ Updated  |
| `.claude/CLAUDE.md`                | Project summary                | ✅ Updated  |
| `.claude/rules/project-status.md`  | Phase roadmap                  | ✅ Updated  |
| `docs/Running-Guide.md`            | Build & run instructions       | ✅ Complete |
| `docs/Troubleshooting.md`          | Common issues & solutions      | ✅ Complete |
| `docs/Phase1-Verification.md`      | Verification checklist         | ✅ NEW      |
| `docs/Architecture.md`             | System design                  | ✅ Exists   |
| `docs/Coding-Rules.md`             | Conventions                    | ✅ Exists   |
| `docs/Testing-Rules.md`            | Testing strategy               | ✅ Exists   |
| `docs/Agent-Verification-Guide.md` | For AI agents                  | ✅ Exists   |

---

## Code Patterns & Conventions

### Services
```kotlin
// Interface in services/AuthService.kt
interface AuthService {
    suspend fun register(email: String, password: String): AuthResult
    suspend fun login(email: String, password: String): AuthResult
}

// Implementation in services/AuthServiceImpl.kt
class AuthServiceImpl : AuthService {
    override suspend fun register(...) = withContext(Dispatchers.IO) {
        // Business logic
    }
}
```

### Repositories
```kotlin
// Interface in repositories/UserRepository.kt
interface UserRepository {
    suspend fun create(user: User): User
    suspend fun findById(id: UUID): User?
}

// Implementation uses Exposed DSL
class UserRepositoryImpl : UserRepository {
    override suspend fun create(user: User): User = withContext(Dispatchers.IO) {
        transaction {
            UsersTable.insert { ... }
            user
        }
    }
}
```

### Routes
```kotlin
// In routes/AuthRoutes.kt
fun Route.authRoutes() {
    route("/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            val result = authService.register(request.email, request.password)
            call.respond(HttpStatusCode.Created, ApiResponse.success(result))
        }
    }
}
```

### Database with Exposed
```kotlin
object UsersTable : Table("users") {
    val id = uuid("id")
    val email = varchar("email", 255)
    val passwordHash = varchar("password_hash", 255)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    override val primaryKey = PrimaryKey(id)
}

// Use in transaction block:
transaction {
    UsersTable.insert { ... }
    UsersTable.select(UsersTable.id eq userId).map { it.toUser() }
}
```

---

## Important Rules

**Do:**
- Use `val` over `var` (immutability preferred)
- Validate input at system boundaries (routes)
- Use coroutines with `withContext(Dispatchers.IO)` for DB calls
- Use UUIDs in API responses (never expose DB IDs)
- Handle errors with ApiResponse wrapper

**Don't:**
- Use `!!` operator (unsafe)
- Do ML inference on backend (browser-only)
- Store raw video (only landmarks)
- Log passwords, JWTs, or PII
- Modify existing Flyway migrations (create new ones)

---

## Common Commands

```bash
# Build
./gradlew build                                    # Full build
./gradlew clean build                              # Clean rebuild
./gradlew :shared:jsBrowserDevelopmentLibraryDistribution  # Build Kotlin/JS

# Run
./gradlew server:run                               # Start server (port 8080)
./gradlew test                                     # Run tests

# Project management
git status                                         # Check changes
git diff                                           # View changes

# Database
docker start webslt-postgres                       # Start DB
docker logs webslt-postgres                        # View DB logs
docker ps                                          # List containers
```

---

## Test Endpoints (with curl)

### Health Check
```bash
curl http://localhost:8080/api/v1/health
```

### Register User
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"testpass123"}'
```

### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"testpass123"}'
```

---

## Future AI Agent Notes

When implementing Phase 2 or beyond:

1. **Read existing code first** - Understand patterns in Phase 1
2. **Check libs.versions.toml** - All dependencies centralized there
3. **Follow DI pattern** - Use Koin modules (KoinModule.kt)
4. **Use Exposed DSL** - Never raw SQL
5. **Test with curl** - Verify endpoints work before submitting
6. **Update documentation** - Keep docs/ in sync with code changes
7. **Reference Phase2-CoreAPI-Implementation.md** - Detailed plan ready to execute

---

**This document is the single source of truth for WebSLT Backend status.**
Update it whenever Phase transitions or critical changes are made.