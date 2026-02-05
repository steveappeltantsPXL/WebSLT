# Testing Rules

For comprehensive testing strategy and ROI analysis, see `docs/Testing-Rules.md`.

## Current Test Infrastructure

- Server: Ktor test host (`ktor-server-test-host-jvm`)
- Shared: kotlin-test (common), JUnit (JVM)
- Frontend: none yet

## Test Locations

- Server tests: `server/src/test/kotlin/be/tinvision/webslt/`
- Shared tests: `shared/src/commonTest/kotlin/be/tinvision/webslt/`
- Frontend tests: `webApp/src/__tests__/` (to be created)

## Naming Conventions

Kotlin -- backtick names describing behavior:
```kotlin
fun `should reject training data without authentication`()
```

TypeScript -- describe/it blocks:
```typescript
it('should display translation when gesture detected')
```

## Test Structure (Given/When/Then)

```kotlin
@Test
fun `should return 201 when valid data submitted`() = testApplication {
    // Given
    application { module() }

    // When
    val response = client.post("/api/v1/training-data") { ... }

    // Then
    assertEquals(HttpStatusCode.Created, response.status)
}
```

## What to Test

- Routes: HTTP status codes, response bodies, auth enforcement
- Services: business logic, validation, error cases
- Repositories: data access (with Testcontainers for PostgreSQL)
- Shared: validation logic, data transformations
- Frontend: component rendering, user interactions, hook behavior

## What NOT to Test

- Framework internals (Ktor, React)
- Trivial getters/setters
- Third-party library behavior

## Dependencies to Add

- Server: MockK (mocking), Testcontainers (PostgreSQL)
- Frontend: vitest, @testing-library/react, @testing-library/jest-dom

## Status

- [x] Ktor test host configured
- [x] Basic server route test (GET /)
- [x] Shared common test placeholder
- [ ] MockK integration
- [ ] Testcontainers PostgreSQL setup
- [ ] Frontend test setup (vitest)
- [ ] Service layer unit tests
- [ ] Repository integration tests
- [ ] Route tests with authentication
