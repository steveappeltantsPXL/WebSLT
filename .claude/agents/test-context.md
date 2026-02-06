# Test Context - WebSLT

**Agent**: test-architect
**Last Updated**: Feb 6, 2026
**Status**: Phase 1 Complete ✅

---

## Quick Facts

| Aspect | Implementation |
|--------|----------------|
| **Server Tests** | Ktor Test Host + kotlin-test + JUnit |
| **Shared Tests** | kotlin-test (commonTest) |
| **Frontend Tests** | Not yet implemented (planned: Vitest + Testing Library) |
| **Test DB** | H2 in-memory (planned: Testcontainers PostgreSQL) |
| **Coverage Goal** | 80%+ for critical paths (auth, business logic) |

---

## Current Test Infrastructure

### Server Tests

**Location:** `server/src/test/kotlin/be/tinvision/webslt/`

**Dependencies:**
```kotlin
// server/build.gradle.kts
testImplementation(libs.ktor.serverTestHost)
testImplementation(libs.kotlin.testJunit)
testImplementation(libs.h2.database)
```

**Current Tests:**
- `ApplicationTest.kt` - Basic route test (GET /)

**Test Runner:**
```bash
./gradlew server:test
./gradlew test  # All modules
```

### Shared Tests

**Location:** `shared/src/commonTest/kotlin/be/tinvision/webslt/`

**Current Tests:**
- `SharedCommonTest.kt` - Greeting class test

---

## Testing Strategy

### Test Pyramid (Priority Order)

**1. Unit Tests (70%)** - Services, Repositories, DTOs
- Fast, isolated, no external dependencies
- Mock dependencies with MockK
- Test business logic thoroughly

**2. Integration Tests (20%)** - Routes, Database
- Test with real database (Testcontainers PostgreSQL)
- Test Ktor routes with test host
- Verify serialization/deserialization

**3. E2E Tests (10%)** - Full API flows
- Test complete user journeys
- Test authentication flows
- Verify cross-component behavior

### What to Test (ROI-Based)

**High Priority (MUST test):**
- ✅ Authentication flows (register, login, logout)
- ✅ Password validation
- ✅ JWT token generation/validation
- ✅ Input validation (email, password formats)
- ✅ Database operations (create, read, update)
- ✅ Error handling (StatusPages)

**Medium Priority (SHOULD test):**
- ⏳ Business logic in services
- ⏳ Repository query logic
- ⏳ DTO serialization/deserialization
- ⏳ CORS configuration

**Low Priority (COULD test):**
- ⏳ Simple getters/setters
- ⏳ Ktor plugin configurations
- ⏳ Framework internals

---

## Testing Patterns

### Route Tests (Ktor Test Host)

**Standard Pattern:**
```kotlin
class AuthRoutesTest {
    @Test
    fun `should register new user with valid input`() = testApplication {
        // Given
        application {
            configureSerialization()
            configureDependencyInjection()
            configureRouting()
        }

        // When
        val response = client.post("/api/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"test@example.com","password":"testpass123"}""")
        }

        // Then
        assertEquals(HttpStatusCode.Created, response.status)
        val body = response.body<ApiResponse<AuthResponse>>()
        assertTrue(body.success)
        assertNotNull(body.data?.accessToken)
    }

    @Test
    fun `should return 400 for invalid email`() = testApplication {
        // Given
        application { /* ... */ }

        // When
        val response = client.post("/api/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"invalid-email","password":"testpass123"}""")
        }

        // Then
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = response.body<ApiResponse<Nothing>>()
        assertFalse(body.success)
        assertContains(body.error!!, "Invalid email format")
    }
}
```

### Service Tests (MockK)

**Pattern with Mocks:**
```kotlin
class AuthServiceImplTest {
    private lateinit var authService: AuthService
    private lateinit var userRepository: UserRepository
    private val jwtConfig = JwtConfig(
        secret = "test-secret",
        expirationHours = 24,
        audience = "test",
        realm = "test"
    )

    @BeforeEach
    fun setup() {
        userRepository = mockk()
        authService = AuthServiceImpl(userRepository, jwtConfig)
    }

    @Test
    fun `should create user and return JWT on successful registration`() = runBlocking {
        // Given
        val email = "test@example.com"
        val password = "testpass123"
        val userId = UUID.randomUUID()

        every { runBlocking { userRepository.findByEmail(email) } } returns null
        every { runBlocking { userRepository.create(any()) } } returns User(
            id = userId,
            email = email,
            passwordHash = "hashed",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        // When
        val result = authService.register(email, password)

        // Then
        assertNotNull(result.accessToken)
        assertEquals(email, result.email)
        verify { runBlocking { userRepository.create(any()) } }
    }

    @Test
    fun `should throw exception for duplicate email`() {
        // Given
        val email = "test@example.com"
        every { runBlocking { userRepository.findByEmail(email) } } returns User(...)

        // When/Then
        assertFailsWith<IllegalArgumentException> {
            runBlocking { authService.register(email, "password") }
        }
    }
}
```

### Repository Tests (Testcontainers)

**Pattern with Real Database:**
```kotlin
class UserRepositoryImplTest {
    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:15").apply {
            withDatabaseName("webslt_test")
            withUsername("test")
            withPassword("test")
        }

        @JvmStatic
        @BeforeAll
        fun setupDatabase() {
            Database.connect(
                url = postgres.jdbcUrl,
                driver = "org.postgresql.Driver",
                user = postgres.username,
                password = postgres.password
            )
            // Run migrations
        }
    }

    private lateinit var repository: UserRepository

    @BeforeEach
    fun setup() {
        repository = UserRepositoryImpl()
        // Clear data
        transaction {
            UsersTable.deleteAll()
        }
    }

    @Test
    fun `should create user and retrieve by id`() = runBlocking {
        // Given
        val user = User(
            id = UUID.randomUUID(),
            email = "test@example.com",
            passwordHash = "hashed",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        // When
        val created = repository.create(user)
        val retrieved = repository.findById(created.id)

        // Then
        assertEquals(user.email, retrieved?.email)
    }
}
```

### DTO Validation Tests

**Pattern:**
```kotlin
class RegisterRequestTest {
    @Test
    fun `should validate correct email and password`() {
        // Given
        val request = RegisterRequest(
            email = "test@example.com",
            password = "testpass123"
        )

        // When
        val error = request.validate()

        // Then
        assertNull(error)
    }

    @Test
    fun `should reject invalid email format`() {
        // Given
        val request = RegisterRequest(
            email = "invalid-email",
            password = "testpass123"
        )

        // When
        val error = request.validate()

        // Then
        assertNotNull(error)
        assertContains(error!!, "Invalid email format")
    }

    @Test
    fun `should reject short password`() {
        // Given
        val request = RegisterRequest(
            email = "test@example.com",
            password = "short"
        )

        // When
        val error = request.validate()

        // Then
        assertNotNull(error)
        assertContains(error!!, "at least 8 characters")
    }
}
```

---

## Test Data Management

### Test Users
```kotlin
object TestData {
    val testUser1 = User(
        id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
        email = "test1@example.com",
        passwordHash = BCrypt.hashpw("testpass123", BCrypt.gensalt(12)),
        createdAt = Instant.parse("2026-01-01T00:00:00Z"),
        updatedAt = Instant.parse("2026-01-01T00:00:00Z")
    )

    val testUser2 = User(
        id = UUID.fromString("550e8400-e29b-41d4-a716-446655440001"),
        email = "test2@example.com",
        passwordHash = BCrypt.hashpw("testpass456", BCrypt.gensalt(12)),
        createdAt = Instant.parse("2026-01-02T00:00:00Z"),
        updatedAt = Instant.parse("2026-01-02T00:00:00Z")
    )
}
```

### Test JWT Tokens
```kotlin
object TestTokens {
    fun generateValidToken(userId: UUID, expiresIn: Long = 3600000): String {
        return JWT.create()
            .withSubject(userId.toString())
            .withAudience("test")
            .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
            .sign(Algorithm.HMAC256("test-secret"))
    }

    fun generateExpiredToken(userId: UUID): String {
        return JWT.create()
            .withSubject(userId.toString())
            .withAudience("test")
            .withExpiresAt(Date(System.currentTimeMillis() - 1000))
            .sign(Algorithm.HMAC256("test-secret"))
    }
}
```

---

## Dependencies to Add

### Server Testing
```kotlin
// server/build.gradle.kts
testImplementation("io.mockk:mockk:1.13.8")
testImplementation("org.testcontainers:postgresql:1.19.3")
testImplementation("org.testcontainers:junit-jupiter:1.19.3")
```

### Frontend Testing (Planned)
```bash
cd webApp
npm install --save-dev vitest @testing-library/react @testing-library/jest-dom
```

---

## Current Coverage (Phase 1)

**Server:**
- ✅ Basic route test (GET /)
- ⏳ Auth route tests (register, login, logout, refresh)
- ⏳ Service layer tests (AuthServiceImpl)
- ⏳ Repository tests (UserRepositoryImpl)
- ⏳ DTO validation tests

**Shared:**
- ✅ Greeting class test
- ⏳ Constants tests
- ⏳ Platform abstraction tests

**Frontend:**
- ⏳ Not yet implemented

---

## Test Execution

### Run Tests
```bash
# All tests
./gradlew test

# Server only
./gradlew server:test

# Shared only
./gradlew shared:test

# With coverage report
./gradlew test jacocoTestReport
```

### Watch Mode (Continuous Testing)
```bash
./gradlew test --continuous
```

### Debug Tests
```bash
./gradlew test --debug-jvm
```

---

## CI/CD Integration (Planned)

**GitHub Actions Workflow:**
```yaml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Run tests
        run: ./gradlew test
      - name: Upload coverage
        uses: codecov/codecov-action@v3
```

---

## Testing Anti-Patterns to Avoid

❌ **Don't:**
- Test framework internals (Ktor, Exposed)
- Test third-party library behavior
- Write tests that depend on external services
- Hard-code test data in test methods
- Skip cleanup in repository tests
- Use production database for tests

✅ **Do:**
- Test your business logic thoroughly
- Use mocks for external dependencies
- Use Testcontainers for database tests
- Centralize test data in TestData objects
- Clean up after each test
- Use H2/Testcontainers for test databases

---

## Naming Conventions

### Test Class Names
```kotlin
// ✅ Good
class AuthServiceImplTest
class UserRepositoryImplTest
class AuthRoutesTest

// ❌ Bad
class TestAuthService
class AuthTests
```

### Test Method Names (Backtick Style)
```kotlin
// ✅ Good - Describes behavior
@Test
fun `should return 201 when user registers with valid email and password`()

@Test
fun `should throw exception when email already exists`()

// ❌ Bad - Not descriptive
@Test
fun testRegister()

@Test
fun registerTest()
```

---

## Phase 2 Testing Priorities

**User Management Tests:**
- GET /api/v1/users/me (with valid JWT)
- PATCH /api/v1/users/me (update profile)
- Test 401 for missing/invalid JWT

**Training Data Tests:**
- POST /api/v1/training-data (valid landmark JSON)
- Validate landmark structure
- Test rejection of oversized payloads

**Model Management Tests:**
- GET /api/v1/models/latest
- GET /api/v1/models/{version}/download
- Test 404 for non-existent versions

---

## Key References

- **Testing Rules**: `docs/Testing-Rules.md`
- **Testing Strategy**: `.claude/rules/testing.md`
- **Backend Rules**: `.claude/rules/backend.md`
- **Current Tests**: `server/src/test/kotlin/be/tinvision/webslt/`
- **Shared Tests**: `shared/src/commonTest/kotlin/be/tinvision/webslt/`

---

**When writing tests:**
1. Follow Given/When/Then structure
2. Use descriptive backtick names
3. Test happy path + error cases
4. Mock external dependencies
5. Clean up test data after each test
6. Aim for 80%+ coverage on critical paths
