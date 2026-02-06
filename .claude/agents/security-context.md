# Security Context - WebSLT

**Agents**: security-reviewer, security-auditor
**Last Updated**: Feb 6, 2026
**Status**: Phase 1 Complete ✅

---

## Quick Facts

| Aspect | Implementation |
|--------|----------------|
| **Authentication** | JWT Bearer tokens (24h expiration) |
| **Password Hashing** | BCrypt (cost factor 12) |
| **Database** | PostgreSQL 15 with prepared statements (Exposed) |
| **CORS** | Configured whitelist (development + production origins) |
| **TLS** | Required in production (TLS 1.3) |
| **Secrets** | Environment variables + application.conf |

---

## Authentication & Authorization

### Current Implementation (Phase 1)

**JWT Token Flow:**
```
1. User registers/login → POST /api/v1/auth/register or /login
2. Server validates credentials (BCrypt.verify)
3. Server generates JWT with user ID in subject
4. Client stores access token + refresh token
5. Client sends: Authorization: Bearer <token>
6. Server validates JWT signature + expiration
7. Protected routes use authenticate("auth-jwt") { ... }
```

**JWT Configuration:**
- **Secret**: `JWT_SECRET` environment variable (required in production)
- **Expiration**: 24 hours (configurable via `JWT_EXPIRATION_HOURS`)
- **Algorithm**: HMAC256
- **Subject**: User UUID as string
- **Audience**: `webslt`
- **Realm**: `WebSLT Server`

**Password Requirements:**
- Minimum 8 characters (enforced in RegisterRequest.validate())
- BCrypt hashing with cost factor 12
- Never logged or exposed in responses

### Protected Routes

**Pattern:**
```kotlin
authenticate("auth-jwt") {
    post("/logout") {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.payload?.subject?.let { UUID.fromString(it) }
            ?: throw IllegalArgumentException("Invalid token")
        // ... proceed with userId
    }
}
```

**Files:**
- JWT config: `server/src/main/kotlin/be/tinvision/webslt/plugins/Security.kt`
- Auth service: `server/src/main/kotlin/be/tinvision/webslt/services/AuthServiceImpl.kt`

---

## Input Validation

### Current Validation (Phase 1)

**Email Validation:**
```kotlin
fun validateEmail(email: String): String? {
    if (email.isBlank()) return "Email is required"
    if (!email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))) {
        return "Invalid email format"
    }
    if (email.length > 255) return "Email too long"
    return null
}
```

**Password Validation:**
```kotlin
fun validatePassword(password: String): String? {
    if (password.isBlank()) return "Password is required"
    if (password.length < 8) return "Password must be at least 8 characters"
    if (password.length > 255) return "Password too long"
    return null
}
```

**All validation happens at route level** before calling services.

### Planned Validation (Phase 2+)

**Training Data:**
- Validate landmark structure (expected number of points)
- Validate landmark coordinate ranges (-1.0 to 1.0)
- Reject oversized payloads (max 1MB per submission)

**File Uploads:**
- Validate MIME types (models only: .tflite, .json)
- Scan for malicious content
- Enforce size limits (max 50MB for model files)

---

## CORS Configuration

**Location:** `server/src/main/kotlin/be/tinvision/webslt/plugins/CORS.kt`

**Current Configuration:**
```kotlin
install(CORS) {
    // Get origins from config
    val allowedOrigins = environment.config.propertyOrNull("webslt.cors.allowedOrigins")
        ?.getList() ?: listOf("http://localhost:5173")

    allowedOrigins.forEach { origin ->
        val cleanOrigin = origin.removePrefix("http://").removePrefix("https://")
        allowHost(cleanOrigin, schemes = listOf("http", "https"))
    }

    allowMethod(HttpMethod.Options)
    allowMethod(HttpMethod.Get)
    allowMethod(HttpMethod.Post)
    allowMethod(HttpMethod.Put)
    allowMethod(HttpMethod.Patch)
    allowMethod(HttpMethod.Delete)
    allowHeader(HttpHeaders.ContentType)
    allowHeader(HttpHeaders.Authorization)
}
```

**Environment Variable:**
```bash
CORS_ALLOWED_ORIGINS=http://localhost:5173,https://webslt.example.com
```

**Key Points:**
- ✅ Whitelist approach (no wildcard `*`)
- ✅ Supports both http and https schemes
- ✅ Allows Authorization header for JWT
- ❌ Never use `anyHost()` in production

---

## Data Protection

### Sensitive Data Handling

**What NEVER gets logged:**
- Passwords (raw or hashed)
- JWT tokens (access or refresh)
- Email addresses (use user IDs instead)
- Personal Identifiable Information (PII)

**Logging Pattern:**
```kotlin
// ✅ Good
logger.info("User registered: userId=${userId}")
logger.error("Login failed: userId=${userId}, reason=invalid_credentials")

// ❌ Bad
logger.info("User registered: email=${email}")  // PII leak
logger.debug("JWT token: ${token}")             // Security leak
```

**Database Storage:**
- ✅ Passwords: BCrypt hashed (12 rounds)
- ✅ User IDs: UUIDs (never expose sequential IDs)
- ✅ Timestamps: UTC timestamps
- ❌ NEVER store raw video (only landmark JSON)
- ❌ NEVER store plain-text secrets

### Data Exposure Prevention

**API Responses:**
```kotlin
// ✅ Good - Use UUIDs
@Serializable
data class UserResponse(
    val userId: String,  // UUID as string
    val email: String
)

// ❌ Bad - Internal DB ID exposed
data class UserResponse(
    val id: Long,  // Sequential ID = security risk
    val email: String
)
```

**Never expose:**
- Database sequential IDs
- Password hashes
- Internal error stack traces (in production)
- File system paths
- Database connection strings

---

## SQL Injection Prevention

**Current Protection:**

✅ **Using Exposed ORM** (prepared statements by default):
```kotlin
// ✅ Safe - Exposed DSL uses prepared statements
UsersTable.select { UsersTable.email eq email }

// ❌ Never do this - raw SQL injection risk
exec("SELECT * FROM users WHERE email = '$email'")
```

**All database queries use Exposed DSL** - no raw SQL allowed.

---

## Security Headers (Planned)

**To be added in production:**
```kotlin
install(DefaultHeaders) {
    header("X-Content-Type-Options", "nosniff")
    header("X-Frame-Options", "DENY")
    header("X-XSS-Protection", "1; mode=block")
    header("Strict-Transport-Security", "max-age=31536000; includeSubDomains")
}
```

---

## Rate Limiting (Planned)

**Endpoints requiring rate limiting:**
- `POST /api/v1/auth/register` - Prevent account spam (5/minute)
- `POST /api/v1/auth/login` - Prevent brute force (10/minute)
- `POST /api/v1/training-data` - Prevent data spam (100/hour)

**Implementation:** Use Ktor's RateLimiting plugin (Phase 3)

---

## Secrets Management

### Current Approach

**application.conf with environment variables:**
```hocon
webslt {
    database {
        url = ${?DATABASE_URL}
        driver = ${?DATABASE_DRIVER}
        username = ${?DATABASE_USERNAME}
        password = ${?DATABASE_PASSWORD}
    }
    jwt {
        secret = ${?JWT_SECRET}
        expirationHours = ${?JWT_EXPIRATION_HOURS}
    }
}
```

**Required in Production:**
- `JWT_SECRET` - Strong random string (min 32 characters)
- `DATABASE_PASSWORD` - PostgreSQL password
- `DATABASE_USERNAME` - PostgreSQL username

**Never commit:**
- `.env` files (in `.gitignore`)
- Hardcoded secrets in source code
- Production credentials

---

## Known Vulnerabilities & Fixes

### Fixed in Phase 1

**1. Missing Authentication on Logout**
- ✅ **Fixed**: Wrapped `/logout` in `authenticate("auth-jwt") { ... }`
- Ensures only authenticated users can logout

**2. Weak Password Requirements**
- ✅ **Fixed**: Minimum 8 characters enforced in RegisterRequest.validate()
- Consider adding complexity requirements in Phase 2

**3. JWT Secret in Application.conf**
- ✅ **Fixed**: Uses environment variable `JWT_SECRET`
- Default value only for development (must override in production)

### Pending (Phase 2+)

**1. No Rate Limiting**
- Risk: Brute force attacks, spam
- Plan: Add RateLimiting plugin

**2. No HTTPS Enforcement**
- Risk: Man-in-the-middle attacks
- Plan: Enforce TLS 1.3 in production deployment

**3. No Refresh Token Rotation**
- Risk: Stolen refresh tokens remain valid
- Plan: Implement refresh token rotation + revocation list

**4. No Account Lockout**
- Risk: Unlimited login attempts
- Plan: Lock account after N failed attempts

---

## Security Review Checklist

When reviewing new code, check:

### Authentication & Authorization
- [ ] Protected routes use `authenticate("auth-jwt") { ... }`
- [ ] User ID extracted from JWT principal, not request body
- [ ] Passwords validated (min 8 chars)
- [ ] Passwords hashed with BCrypt before storage

### Input Validation
- [ ] All user inputs validated at route level
- [ ] Email format validated with regex
- [ ] String lengths checked (prevent DoS)
- [ ] Numeric ranges validated

### Data Exposure
- [ ] API responses use UUIDs, not sequential IDs
- [ ] No password hashes in responses
- [ ] No internal error details in production responses
- [ ] No PII in log statements

### SQL Injection
- [ ] All queries use Exposed DSL (no raw SQL)
- [ ] No string concatenation in queries
- [ ] Parameters properly bound

### CORS
- [ ] Only whitelisted origins allowed
- [ ] No `anyHost()` usage
- [ ] Necessary headers allowed (Authorization, Content-Type)

### Logging
- [ ] No passwords logged
- [ ] No JWT tokens logged
- [ ] No email addresses logged (use user IDs)
- [ ] No stack traces in production logs

---

## Compliance Notes

**GDPR Considerations (Future):**
- Right to erasure: User deletion capability
- Data portability: Export user data endpoint
- Consent tracking: Store consent timestamps

**Accessibility Data:**
- Sign language training data: Not PII (landmark coordinates only)
- No video stored: Privacy by design
- User opt-in required: Explicit consent for data submission

---

## Key References

- **Security Rules**: `.claude/rules/security.md`
- **Backend Rules**: `.claude/rules/backend.md`
- **API Design**: `.claude/rules/api-design.md`
- **Coding Rules**: `docs/Coding-Rules.md`
- **Application Config**: `server/src/main/resources/application.conf`
- **JWT Setup**: `server/src/main/kotlin/be/tinvision/webslt/plugins/Security.kt`

---

**When implementing security features:**
1. Validate all inputs at system boundaries
2. Use parameterized queries (Exposed DSL)
3. Never log sensitive data
4. Test auth flows with curl + invalid tokens
5. Review OWASP Top 10 before release
