# Kotlin Backend Context - WebSLT

**Agent**: kotlin-backend-advisor
**Last Updated**: Feb 6, 2026
**Status**: Phase 1 Complete ✅

---

## Quick Facts

| Aspect | Value |
|--------|-------|
| **Framework** | Ktor 3.3.3 (JVM + Netty) |
| **Language** | Kotlin 2.3.0 |
| **Database** | PostgreSQL 15 + Exposed ORM 0.41.1 |
| **DI** | Koin 3.5.6 |
| **Auth** | JWT (com.auth0:java-jwt 4.4.0) + BCrypt |
| **Migrations** | Flyway 9.22.3 |
| **Server Port** | 8080 |
| **Package** | `be.tinvision.webslt` |

---

## Architecture Overview

### Three-Layer Pattern
```
routes/      → HTTP handlers, validation, call.respond()
services/    → Business logic, orchestration
repositories/ → Database access (Exposed DSL)
```

### Current Structure
```
server/src/main/kotlin/be/tinvision/webslt/
├── Application.kt                    # Entry point (EngineMain)
├── plugins/                          # Ktor plugin config (7 files)
│   ├── Serialization.kt              # kotlinx.serialization + ContentNegotiation
│   ├── CORS.kt                       # Frontend origin whitelisting
│   ├── StatusPages.kt                # Centralized error handling
│   ├── Security.kt                   # JWT authentication
│   ├── DependencyInjection.kt        # Koin modules
│   └── ...
├── routes/                           # HTTP endpoints (2 files)
│   ├── HealthRoutes.kt               # GET /api/v1/health
│   └── AuthRoutes.kt                 # POST register, login, refresh, logout
├── services/                         # Business logic (2 files)
│   ├── AuthService.kt                # Interface
│   └── AuthServiceImpl.kt            # JWT + BCrypt implementation
├── repositories/                     # Data access (2 files)
│   ├── UserRepository.kt             # Interface
│   └── UserRepositoryImpl.kt         # Exposed DSL queries
├── models/                           # Domain models (User, UserSettings)
├── dto/                              # Request/Response DTOs (3 files)
└── config/                           # Database + Koin setup (2 files)
```

---

## Current Implementation (Phase 1)

### ✅ What Exists (5 Endpoints)

**Health Check:**
- `GET /api/v1/health` → `{ status: "ok", timestamp }`

**Authentication:**
- `POST /api/v1/auth/register` → Register user, return JWT
- `POST /api/v1/auth/login` → Login, return JWT
- `POST /api/v1/auth/refresh` → Refresh access token
- `POST /api/v1/auth/logout` → Invalidate session (protected)

**Infrastructure:**
- Ktor plugins: Serialization, CORS, StatusPages, Auth/JWT, Koin
- PostgreSQL connection pool (HikariCP)
- Database migrations (Flyway)
- User table + repository
- JWT token generation/validation
- BCrypt password hashing

### 📋 What's Planned (Phase 2)

**User Management:**
- `GET /api/v1/users/me` → Current user profile
- `PATCH /api/v1/users/me` → Update profile

**Training Data:**
- `POST /api/v1/training-data` → Submit landmark data
- `GET /api/v1/training-data/stats` → Contribution stats

**Model Management:**
- `GET /api/v1/models/latest` → Latest model metadata
- `GET /api/v1/models/{version}/download` → Download model file

---

## Key Patterns & Conventions

### Dependency Injection (Koin 3.5.6 + Ktor 3.3.3)

**IMPORTANT**: Route-level `inject()` is incompatible with Ktor 3.3.3.

✅ **Correct** (inject per route handler):
```kotlin
fun Route.myRoutes() {
    route("/api/v1/resource") {
        post {
            val myService = call.get<MyService>()  // ✅ Works
            // ... use myService
        }
    }
}
```

❌ **Wrong** (Route-level injection):
```kotlin
fun Route.myRoutes() {
    val myService: MyService by inject()  // ❌ NoClassDefFoundError
    route("/api/v1/resource") { ... }
}
```

### Service Layer

**Interface + Implementation:**
```kotlin
// services/MyService.kt
interface MyService {
    suspend fun doSomething(param: String): Result
}

// services/MyServiceImpl.kt
class MyServiceImpl(
    private val repository: MyRepository
) : MyService {
    override suspend fun doSomething(param: String): Result = withContext(Dispatchers.IO) {
        // Business logic
    }
}
```

**Register in Koin:**
```kotlin
// config/KoinModule.kt
single<MyService> { MyServiceImpl(get()) }
```

### Repository Layer

**Exposed DSL (preferred):**
```kotlin
class MyRepositoryImpl : MyRepository {
    override suspend fun findById(id: UUID): MyEntity? = withContext(Dispatchers.IO) {
        transaction {
            MyTable.select { MyTable.id eq id }
                .map { it.toMyEntity() }
                .singleOrNull()
        }
    }
}
```

**Table Definition:**
```kotlin
object MyTable : Table("my_table") {
    val id = uuid("id")
    val name = varchar("name", 255)
    val createdAt = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
}
```

### Route Handlers

**Standard Pattern:**
```kotlin
fun Route.myRoutes() {
    route("/api/v1/resource") {
        post {
            val service = call.get<MyService>()
            val request = call.receive<MyRequest>()

            // Validate
            val error = request.validate()
            if (error != null) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Nothing>(success = false, data = null, error = error)
                )
            }

            // Execute
            val result = service.doSomething(request.param)
            call.respond(
                HttpStatusCode.OK,
                ApiResponse(success = true, data = result, error = null)
            )
        }
    }
}
```

### DTOs & Serialization

**All DTOs are @Serializable:**
```kotlin
@Serializable
data class MyRequest(
    val param: String
) {
    fun validate(): String? {
        if (param.isBlank()) return "param is required"
        if (param.length > 255) return "param too long"
        return null
    }
}

@Serializable
data class MyResponse(
    val id: String,  // UUIDs exposed as strings
    val value: String
)
```

---

## Configuration

### application.conf
Location: `server/src/main/resources/application.conf`

**Key Settings:**
- Database connection (URL, driver, username, password)
- JWT secret, expiration, audience, realm
- CORS allowed origins
- Server port

**Environment Variables:**
All values support `${ENV_VAR}` substitution with defaults.

### gradle/libs.versions.toml

**All dependency versions defined here.** Never hardcode versions in build.gradle.kts.

---

## Critical Constraints

### Security
- ✅ Use BCrypt for passwords (cost factor 12+)
- ✅ Use JWT Bearer tokens (24h expiration)
- ❌ NEVER log passwords, JWTs, emails, or PII
- ❌ NEVER expose internal database IDs (use UUIDs)

### Database
- ✅ Use Exposed DSL (never raw SQL)
- ✅ Wrap DB calls: `withContext(Dispatchers.IO) { transaction { ... } }`
- ✅ Create new Flyway migrations (never modify existing)
- ❌ NEVER store raw video (only landmark JSON)

### Code Style
- ✅ Prefer `val` over `var`
- ✅ Use safe calls (`?.`) + Elvis (`?:`)
- ❌ NEVER use `!!` operator
- ❌ NO blocking calls on main dispatcher

---

## Recent Fixes (Critical Learnings)

### 1. Application.kt - Config Loading
**Issue**: `Property webslt.database.url not found`
**Fix**: Use `EngineMain.main(args)` instead of `embeddedServer()`
**Why**: EngineMain automatically loads application.conf from classpath

### 2. DatabaseConfig.kt - Missing Credentials
**Issue**: `SCRAM authentication failed, no password provided`
**Fix**: Explicitly set USERNAME and PASSWORD in HikariCP config
**Why**: Environment variables need explicit mapping

### 3. CORS.kt - Invalid Scheme Parameter
**Issue**: `scheme should be specified as separate parameter schemes`
**Fix**: Use `schemes = listOf("http", "https")` parameter
**Why**: Ktor 3.x changed CORS API

### 4. Koin Injection - NoClassDefFoundError
**Issue**: `NoClassDefFoundError: io/ktor/server/routing/RoutingKt`
**Fix**: Use `call.get<Service>()` instead of Route-level `inject()`
**Why**: Koin 3.5.6 Route extensions incompatible with Ktor 3.3.3

### 5. Serialization Plugin Missing
**Issue**: `Serializer for class 'X' is not found`
**Fix**: Apply `kotlinSerialization` plugin in server/build.gradle.kts
**Why**: @Serializable annotations need compiler plugin to generate serializers

---

## Testing Endpoints

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

## Key References

- **Architecture**: `docs/Architecture.md`
- **Coding Rules**: `docs/Coding-Rules.md`
- **API Design**: `.claude/rules/api-design.md`
- **Backend Rules**: `.claude/rules/backend.md`
- **Database Rules**: `.claude/rules/database.md`
- **Security Rules**: `.claude/rules/security.md`
- **Project Status**: `.claude/rules/project-status.md`
- **Master Context**: `.claude/PROJECT-CONTEXT.md`

---

**When implementing new features:**
1. Read existing similar files first (routes, services, repositories)
2. Follow the three-layer pattern
3. Use Koin `call.get<Service>()` for injection
4. Validate at route level, business logic in service
5. Use Exposed DSL in repositories
6. Test with curl before committing
