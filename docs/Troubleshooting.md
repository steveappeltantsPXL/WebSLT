# Troubleshooting Guide - WebSLT Backend

**Quick solutions to common issues**

---

## Recent Fixes (Phase 1 - Feb 2026)

These errors have been fixed in the current version but documented for reference:

### ✅ FIXED: CORS Configuration Error
**Error**: `scheme should be specified as a separate parameter schemes`

**Cause**: CORS configuration was passing full URLs with protocols to `allowHost()`. Ktor expects only the host.

**Solution**: CORS.kt now uses `allowHost("host", schemes = listOf("http", "https"))` and strips protocols from configured origins.

### ✅ FIXED: Database Authentication Error
**Error**: `The server requested SCRAM-based authentication, but no password was provided`

**Cause**: HikariCP connection pool wasn't configured with username and password.

**Solution**: Added `DATABASE_USERNAME` and `DATABASE_PASSWORD` to `application.conf` with defaults `postgres`/`password`.

### ✅ FIXED: Application Config Not Loading
**Error**: `Property webslt.database.url not found`

**Cause**: `embeddedServer()` doesn't automatically load `application.conf`.

**Solution**: Changed main() to use `EngineMain.main(args)` which loads `application.conf` automatically.

---

## Build Issues

### Issue: "Cannot find symbol" or "Unresolved reference"

**Symptoms**:
```
error: cannot find symbol
import com.auth0.jwt.JWT;
       ^
```

**Cause**: Missing dependency or incorrect import

**Solution**:

1. **Check if dependency exists**:  
```bash
grep -r "jwt" gradle/libs.versions.toml
```

2. **Verify it's in build.gradle.kts**:
```kotlin
// server/build.gradle.kts should have:
implementation(libs.jwt)
```

3. **Sync Gradle** (if using IDE):
   - IntelliJ: File → Reload All Gradle Projects
   - Or run: `./gradlew --refresh-dependencies`

4. **Clean and rebuild**:
```bash
./gradlew clean build
```

---

### Issue: "Could not resolve dependency"

**Symptoms**:
```
Could not resolve com.auth0:java-jwt:4.4.0
```

**Cause**: Network issues or incorrect version

**Solution**:

1. **Check internet connection**

2. **Verify dependency exists**:
   - Visit [Maven Central](https://mvnrepository.com/)
   - Search for the artifact

3. **Try with `--refresh-dependencies`**:
```bash
./gradlew build --refresh-dependencies
```

4. **Check proxy settings** (if behind corporate firewall):
```bash
# Add to gradle.properties
systemProp.http.proxyHost=proxy.company.com
systemProp.http.proxyPort=8080
systemProp.https.proxyHost=proxy.company.com
systemProp.https.proxyPort=8080
```

---

### Issue: "Kotlin version mismatch"

**Symptoms**:
```
Module was compiled with an incompatible version of Kotlin
```

**Cause**: Mixing different Kotlin versions

**Solution**:

1. **Check all Kotlin versions**:
```bash
grep "kotlin =" gradle/libs.versions.toml
```

2. **Ensure consistency**:
```toml
kotlin = "2.3.0"  # All should use same version
```

3. **Clean and rebuild**:
```bash
./gradlew clean build --refresh-dependencies
```

---

## Runtime Issues

### Issue: Database connection failed

**Symptoms**:
```
org.postgresql.util.PSQLException: Connection refused
```

**Cause**: PostgreSQL not running or wrong connection string

**Solution**:

1. **Check if PostgreSQL is running**:
```bash
docker ps | grep webslt-postgres
```

2. **If not running, start it**:
```bash
docker start webslt-postgres
```

3. **If container doesn't exist, create it**:
```bash
docker run --name webslt-postgres \
  -e POSTGRES_DB=webslt \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres:15
```

4. **Verify connection string** in `application.conf`:
```hocon
url = "jdbc:postgresql://localhost:5432/webslt"
```

5. **Test connection manually**:
```bash
docker exec -it webslt-postgres psql -U postgres -d webslt -c "SELECT 1;"
```

---

### Issue: Flyway migration failed

**Symptoms**:
```
FlywayException: Validate failed: Migration checksum mismatch
```

**Cause**: Migration file was modified after being applied

**Solution**:

**Option A: Reset database (development only)**:
```bash
docker exec -it webslt-postgres psql -U postgres -d webslt \
  -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"

# Restart server (migrations will run again)
./gradlew server:run
```

**Option B: Repair Flyway**:
```bash
# Connect to database
docker exec -it webslt-postgres psql -U postgres -d webslt

# Delete problematic migration record
DELETE FROM flyway_schema_history WHERE version = 'X';
```

**Prevention**:
- Never modify existing migration files
- Create new migrations instead (V2, V3, etc.)

---

### Issue: Port 8080 already in use

**Symptoms**:
```
java.net.BindException: Address already in use
```

**Cause**: Another process is using port 8080

**Solution**:

**Option A: Kill the process**:

*Linux/macOS*:
```bash
# Find process
lsof -i :8080

# Kill it
kill -9 <PID>
```

*Windows*:
```cmd
# Find process
netstat -ano | findstr :8080

# Kill it
taskkill /PID <PID> /F
```

**Option B: Change port**:
```bash
export PORT=8081
./gradlew server:run
```

Or in `application.conf`:
```hocon
deployment {
    port = 8081
}
```

---

### Issue: JWT authentication not working

**Symptoms**:
```
401 Unauthorized
```

**Cause**: Token invalid, expired, or secret mismatch

**Solution**:

1. **Check token format**:
```bash
echo "Bearer <your-token-here>"
# Should start with "Bearer " followed by token
```

2. **Verify JWT secret is consistent**:
```bash
# Check application.conf
grep "jwt.secret" server/src/main/resources/application.conf

# Check environment variable
echo $JWT_SECRET
```

3. **Check token expiration**:
   - Tokens expire after 24 hours (default)
   - Login again to get fresh token

4. **Test with curl**:
```bash
curl -v -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer $TOKEN"

# Look for HTTP status code
```

5. **Check Security.kt configuration**:
```kotlin
// Ensure algorithm matches AuthServiceImpl
val algorithm = Algorithm.HMAC256(jwtSecret)
```

---

### Issue: "No such bean" at runtime

**Symptoms**:
```
org.koin.core.error.NoBeanDefFoundException:
No definition found for 'AuthService'
```

**Cause**: Service not registered in Koin module

**Solution**:

1. **Check KoinModule.kt**:
```kotlin
val appModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single<AuthService> { AuthServiceImpl(get(), get<Application>().environment) }
    // Make sure your service is here!
}
```

2. **Verify DI is initialized**:
```kotlin
// In Application.kt
fun Application.module() {
    configureDependencyInjection()  // Must be called first!
    // ...
}
```

3. **Check injection syntax in routes**:
```kotlin
fun Route.authRoutes() {
    val authService: AuthService by inject()  // Correct
    // Not: val authService = inject<AuthService>()
}
```

---

## Configuration Issues

### Issue: Environment variables not loaded

**Symptoms**:
- Using default values instead of environment variables

**Cause**: Environment variables not set correctly

**Solution**:

1. **Check environment variables**:
```bash
echo $DATABASE_URL
echo $JWT_SECRET
```

2. **Set variables before running**:
```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/webslt
export JWT_SECRET=your-secret-key
./gradlew server:run
```

3. **Or use IDE run configuration**:
   - IntelliJ → Run → Edit Configurations
   - Add environment variables

4. **Verify config syntax in application.conf**:
```hocon
jwt {
    secret = "default-value"
    secret = ${?JWT_SECRET}  # ${?...} for optional override
}
```

---

### Issue: CORS errors in browser

**Symptoms**:
```
Access to fetch at 'http://localhost:8080/api/v1/...' from origin 'http://localhost:5173'
has been blocked by CORS policy
```

**Cause**: CORS not configured for frontend origin

**Solution**:

1. **Check CORS.kt**:
```kotlin
allowHost("localhost:5173")
```

2. **Or set environment variable**:
```bash
export CORS_ALLOWED_ORIGINS=http://localhost:5173
```

3. **Verify CORS plugin is installed**:
```kotlin
// In Application.kt
configureCORS()  // Must be called!
```

4. **Test with curl**:
```bash
curl -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: POST" \
  -X OPTIONS http://localhost:8080/api/v1/health -v
```

Should see:
```
Access-Control-Allow-Origin: http://localhost:5173
```

---

## Dependency Issues

### Issue: Exposed SQL error

**Symptoms**:
```
org.jetbrains.exposed.exceptions.ExposedSQLException:
Table 'users' doesn't exist
```

**Cause**: Database migrations didn't run

**Solution**:

1. **Check Flyway logs**:
```
Look for: "Successfully applied 1 migration"
```

2. **Verify migration files exist**:
```bash
ls server/src/main/resources/db/migration/
# Should see: V1__create_users_table.sql
```

3. **Check migration syntax**:
```sql
-- V1__create_users_table.sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    -- ...
);
```

4. **Manually check database**:
```bash
docker exec -it webslt-postgres psql -U postgres -d webslt \
  -c "\dt"  # List tables
```

---

### Issue: BCrypt errors

**Symptoms**:
```
java.lang.NoClassDefFoundError: at/favre/lib/crypto/bcrypt/BCrypt
```

**Cause**: BCrypt dependency missing

**Solution**:

1. **Add to libs.versions.toml**:
```toml
bcrypt = { module = "at.favre.lib:bcrypt", version = "0.10.1" }
```

2. **Add to server/build.gradle.kts**:
```kotlin
implementation(libs.bcrypt)
```

3. **Rebuild**:
```bash
./gradlew clean build
```

---

## Testing Issues

### Issue: Tests fail with "Table not found"

**Cause**: Test database not initialized

**Solution**:

Use Testcontainers or H2 in-memory database for tests:

```kotlin
// In test configuration
@BeforeEach
fun setup() {
    Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
    transaction {
        SchemaUtils.create(UsersTable)
    }
}
```

---

### Issue: 401 on protected endpoints in tests

**Cause**: Missing authentication in test

**Solution**:

```kotlin
@Test
fun `test protected endpoint`() = testApplication {
    // Generate test token
    val token = generateTestToken()

    val response = client.get("/api/v1/users/me") {
        header(HttpHeaders.Authorization, "Bearer $token")
    }

    assertEquals(HttpStatusCode.OK, response.status)
}
```

---

## Performance Issues

### Issue: Slow database queries

**Cause**: Missing indexes or N+1 queries

**Solution**:

1. **Add indexes to migration**:
```sql
CREATE INDEX idx_users_email ON users(email);
```

2. **Check query count**:
```kotlin
// Enable SQL logging in logback.xml
<logger name="Exposed" level="DEBUG"/>
```

3. **Use batch operations**:
```kotlin
// Instead of loop:
users.forEach { userRepository.update(it) }

// Use batch:
userRepository.batchUpdate(users)
```

---

### Issue: Server slow to start

**Cause**: Database connection timeout or large migrations

**Solution**:

1. **Check database is reachable**:
```bash
time psql -h localhost -U postgres -d webslt -c "SELECT 1;"
```

2. **Increase connection timeout**:
```kotlin
// In DatabaseConfig.kt
connectionTimeout = 60000  // 60 seconds
```

3. **Optimize Flyway migrations**:
- Split large migrations
- Add indexes after bulk inserts
- Use COPY instead of INSERT for large data

---

## Common Error Messages

| Error                     | Cause                  | Solution                     |
|---------------------------|------------------------|------------------------------|
| `ClassNotFoundException`  | Missing dependency     | Add to build.gradle.kts      |
| `NoSuchMethodError`       | Version mismatch       | Sync all dependency versions |
| `BindException`           | Port in use            | Kill process or change port  |
| `PSQLException`           | Database connection    | Start PostgreSQL             |
| `401 Unauthorized`        | Missing/invalid token  | Check JWT token and secret   |
| `NoBeanDefFoundException` | Service not registered | Add to KoinModule            |
| `ExposedSQLException`     | Migration didn't run   | Check Flyway logs            |

---

## Debug Checklist

When something doesn't work:

1. [ ] Check server logs for error stack trace
2. [ ] Verify database is running (`docker ps`)
3. [ ] Check correct port (8080 by default)
4. [ ] Verify environment variables are set
5. [ ] Check application.conf for typos
6. [ ] Ensure migrations ran successfully
7. [ ] Verify JWT secret is consistent
8. [ ] Check CORS configuration for frontend origin
9. [ ] Test with curl before testing with frontend
10. [ ] Read the full error message (don't skip details!)

---

## Getting More Help

1. **Enable debug logging** (`logback.xml`):
```xml
<logger name="be.tinvision.webslt" level="DEBUG"/>
<logger name="Exposed" level="DEBUG"/>
```

2. **Check database state**:
```bash
docker exec -it webslt-postgres psql -U postgres -d webslt
```

3. **Test individual components**:
   - Database: `psql` connection
   - Server: Health endpoint
   - Auth: Register/login endpoints

4. **Review documentation**:
   - [Running Guide](./Running-Guide.md)
   - [Testing Guide](./Testing-Guide.md)
   - [Architecture](./Architecture.md)

---

## Prevention

Avoid common issues by:

- ✅ Always run `./gradlew build` before `server:run`
- ✅ Keep dependency versions in sync
- ✅ Never modify existing migrations
- ✅ Use environment variables for secrets
- ✅ Test endpoints with curl before integrating
- ✅ Check logs immediately when errors occur
- ✅ Keep PostgreSQL running during development

---

## Still Stuck?

1. Check `.claude/rules/` for project conventions
2. Review `docs/Architecture.md` for system design
3. Read error messages carefully (full stack trace)
4. Try the "turn it off and on again" approach:
   - Stop server
   - Stop database
   - Clean build: `./gradlew clean`
   - Restart database
   - Rebuild: `./gradlew build`
   - Restart server: `./gradlew server:run`