# Agent Verification Guide

**Purpose**: Systematic checklist for AI agents to verify code correctness before providing it to users.

**Goal**: Ensure all code compiles, follows patterns, and works on first try.

---

## Philosophy

> "Read before you write. Verify before you provide."

Agents should never provide code that:
- References non-existent dependencies
- Uses incorrect imports or package names
- Breaks established architectural patterns
- Lacks proper error handling
- Won't compile

---

## Pre-Implementation Verification

### Step 1: Understand the Request

- [ ] Read user's request carefully
- [ ] Identify what type of change is needed (new feature, bug fix, refactor)
- [ ] Determine scope (single file, multiple files, new dependencies)
- [ ] Check if similar code exists that can be reused

### Step 2: Explore Existing Code

**For Routes**:
- [ ] Read `routes/` directory to see existing pattern
- [ ] Check how authentication is applied (`authenticate("auth-jwt")`)
- [ ] Review error handling patterns (ApiResponse wrapper)
- [ ] Note HTTP status codes used

**For Services**:
- [ ] Read existing service interfaces and implementations
- [ ] Check how repositories are injected via constructor
- [ ] Review suspend function usage and error handling
- [ ] Note how validation is performed

**For Repositories**:
- [ ] Read existing repository implementations
- [ ] Check Exposed table definitions pattern
- [ ] Review how `withContext(Dispatchers.IO)` is used
- [ ] Note ResultRow mapping pattern

**For DTOs**:
- [ ] Check existing DTO structures
- [ ] Verify `@Serializable` annotation usage
- [ ] Review validation method patterns
- [ ] Note naming conventions (Request/Response suffixes)

### Step 3: Verify Dependencies

- [ ] Check `gradle/libs.versions.toml` for needed libraries
- [ ] Verify versions are compatible (same major versions)
- [ ] Check `server/build.gradle.kts` includes needed dependencies
- [ ] Ensure no duplicate or conflicting dependencies

### Step 4: Check Configuration

- [ ] Review `application.conf` for existing config patterns
- [ ] Determine if new config values are needed
- [ ] Check environment variable substitution pattern (${?VAR_NAME})
- [ ] Verify security-sensitive values use env vars

---

## Implementation Checklist

### While Writing Code

- [ ] Use correct package names (`be.tinvision.webslt.*`)
- [ ] Follow established naming conventions
- [ ] Apply appropriate annotations (`@Serializable`, `suspend`)
- [ ] Include proper error handling (try/catch, throw IllegalArgumentException)
- [ ] Use safe calls (`?.`) instead of null assertions (`!!`)
- [ ] Follow async patterns (`withContext(Dispatchers.IO)` for blocking calls)

### Dependency Injection

- [ ] Services receive dependencies via constructor
- [ ] Register new services in `config/KoinModule.kt`
- [ ] Use `get()` in Koin module to inject dependencies
- [ ] Inject services in routes with `by inject()`

### Database Code

- [ ] Use Exposed DSL (no raw SQL)
- [ ] Wrap DB calls in `withContext(Dispatchers.IO)` + `transaction { }`
- [ ] Define table structure as `object XxxTable : Table("table_name")`
- [ ] Map ResultRow to domain model with private extension function
- [ ] Use UUID for all primary keys
- [ ] Include created_at and updated_at timestamps

### API Endpoints

- [ ] Use `/api/v1/` prefix for all routes
- [ ] Apply authentication where needed (`authenticate("auth-jwt")`)
- [ ] Wrap responses in `ApiResponse<T>`
- [ ] Use correct HTTP status codes (200, 201, 400, 401, 404, 500)
- [ ] Validate input before processing
- [ ] Handle errors with try/catch and proper error messages

---

## Post-Implementation Verification

### Step 1: Syntax Verification

- [ ] **Read back files just created** to verify syntax
- [ ] Check all opening/closing braces match
- [ ] Verify all function signatures are complete
- [ ] Ensure no missing semicolons, commas, or parentheses
- [ ] Check string literals are properly closed

### Step 2: Import Verification

- [ ] All imports reference actual, available packages
- [ ] No wildcard imports (`import *`)
- [ ] Imports match what's in `libs.versions.toml`
- [ ] No duplicate imports
- [ ] Unused imports removed

**Example Check**:
```kotlin
// Verify each import exists
import be.tinvision.webslt.dto.ApiResponse  // ✓ File exists
import be.tinvision.webslt.services.AuthService  // ✓ File exists
import org.koin.ktor.ext.inject  // ✓ Koin dependency added
import com.auth0.jwt.JWT  // ✓ JWT dependency added
```

### Step 3: Dependency Verification

- [ ] Check `server/build.gradle.kts` has all needed `implementation(libs.xxx)`
- [ ] Verify `libs.versions.toml` defines all library versions
- [ ] No references to non-existent dependencies
- [ ] Dependencies use correct artifact names

### Step 4: Pattern Compliance

- [ ] Services follow interface + implementation pattern
- [ ] Repositories use Exposed DSL, not raw SQL
- [ ] DTOs have `@Serializable` annotation
- [ ] Routes use `ApiResponse<T>` wrapper
- [ ] Authentication applied consistently
- [ ] Error handling follows established patterns

### Step 5: Configuration Verification

- [ ] New config values added to `application.conf`
- [ ] Security-sensitive values use environment variables
- [ ] Config values accessed with `environment.config.property()`
- [ ] No hardcoded secrets in code

---

## Providing Code to Users

### Required Information

When providing code, always include:

1. **Files created/updated**: List all files with brief description
2. **Dependencies added**: List new dependencies (if any)
3. **Build command**: `./gradlew build`
4. **Run command**: `./gradlew server:run`
5. **Test command**: curl examples for new endpoints
6. **Expected response**: JSON response format

### Example Template

```
✅ Created: server/src/main/kotlin/be/tinvision/webslt/routes/FeatureRoutes.kt
✅ Created: server/src/main/kotlin/be/tinvision/webslt/services/FeatureService.kt
✅ Updated: config/KoinModule.kt (registered FeatureService)
✅ Updated: Application.kt (added featureRoutes())

Dependencies added:
- None (all dependencies already available)

Build & test:
```bash
./gradlew build
./gradlew server:run

# Test endpoint
curl -X GET http://localhost:8080/api/v1/feature \
  -H "Authorization: Bearer <token>"
```

Expected response (200 OK):
```json
{
  "success": true,
  "data": {  },
  "error": null
}
```
```

---

## Common Verification Failures

### 1. Missing Dependency

**Symptom**: "Cannot resolve symbol" or "Unresolved reference"
**Check**: Is the dependency in `libs.versions.toml` AND `build.gradle.kts`?
**Fix**: Add missing dependency to both files

### 2. Wrong Import

**Symptom**: "Cannot find class" or "Unresolved reference"
**Check**: Does the import path match the actual package structure?
**Fix**: Correct the import statement to match actual package

### 3. Pattern Mismatch

**Symptom**: Code works but doesn't follow project conventions
**Check**: Compare with existing similar files
**Fix**: Refactor to match established patterns

### 4. Missing DI Registration

**Symptom**: Runtime error "No such bean"
**Check**: Is the service registered in `KoinModule.kt`?
**Fix**: Add `single<Interface> { Implementation(get()) }` to Koin module

### 5. Configuration Missing

**Symptom**: Runtime error "Property not found"
**Check**: Is the config value in `application.conf`?
**Fix**: Add config value with environment variable fallback

---

## Verification Workflow Example

### Scenario: Adding a new "Profile" endpoint

**1. Pre-Implementation**:
```
[ ] Read routes/AuthRoutes.kt to understand pattern
[ ] Read services/AuthService.kt to see service structure
[ ] Check if User model has profile data
[ ] Verify JWT auth pattern
```

**2. Implementation**:
```
[ ] Create dto/profile/ProfileResponse.kt
[ ] Create routes/ProfileRoutes.kt
[ ] Update Application.kt to add profileRoutes()
```

**3. Post-Implementation**:
```
[ ] Read back ProfileResponse.kt - verify @Serializable present
[ ] Read back ProfileRoutes.kt - verify imports correct
[ ] Check Application.kt - verify route added
[ ] Verify no new dependencies needed
```

**4. Provide to User**:
```
✅ Created: dto/profile/ProfileResponse.kt
✅ Created: routes/ProfileRoutes.kt
✅ Updated: Application.kt

Build & test:
./gradlew build
curl http://localhost:8080/api/v1/users/me \
  -H "Authorization: Bearer <token>"
```

---

## Self-Check Questions

Before providing code, ask yourself:

1. **Did I read similar existing code first?**
2. **Are all dependencies actually available?**
3. **Did I verify the imports are correct?**
4. **Does the code follow established patterns?**
5. **Did I read back the files I created?**
6. **Can I provide a working build command?**
7. **Can I provide a working test command?**
8. **Is the code production-ready (error handling, validation)?**

If any answer is "No", go back and verify that aspect.

---

## Benefits of Systematic Verification

- ✅ Code compiles on first try
- ✅ Reduces back-and-forth debugging
- ✅ Maintains code quality and consistency
- ✅ Builds user trust
- ✅ Faster development cycles
- ✅ Fewer runtime errors

---

## Summary

**Golden Rule**: Never provide code without verifying it first.

**Three-Step Process**:
1. **Read** existing code to understand patterns
2. **Write** following those patterns
3. **Verify** by reading back and checking dependencies

This systematic approach ensures quality, working code every time.