# Running Guide - WebSLT Backend

**Quick Start**: Get the WebSLT backend running in 5 minutes.

---

## Prerequisites

### 1. Java Development Kit (JDK)

**Required**: JDK 11 or higher

```bash
# Check your Java version
java -version

# Expected output (example):
openjdk version "17.0.2" 2022-01-18
OpenJDK Runtime Environment (build 17.0.2+8)
OpenJDK 64-Bit Server VM (build 17.0.2+8, mixed mode)
```

**Don't have Java?**
- **Windows**: Download from [Adoptium](https://adoptium.net/)
- **macOS**: `brew install openjdk@17`
- **Linux**: `sudo apt install openjdk-17-jdk`

### 2. PostgreSQL Database

**Option A: Docker (Recommended)**

```bash
# Start PostgreSQL container
docker run --name webslt-postgres \
  -e POSTGRES_DB=webslt \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres:15

# Verify it's running
docker ps | grep webslt-postgres

# Expected output:
# CONTAINER ID   IMAGE         STATUS         PORTS
# abc123...      postgres:15   Up 2 seconds   0.0.0.0:5432->5432/tcp
```

**Useful Docker Commands**:
```bash
# Stop the database
docker stop webslt-postgres

# Start again
docker start webslt-postgres

# View logs
docker logs webslt-postgres

# Remove container (if needed)
docker stop webslt-postgres && docker rm webslt-postgres
```

**Option B: Native PostgreSQL**

If you have PostgreSQL installed natively:

```bash
# Create database
createdb webslt

# Or using psql
psql -U postgres
CREATE DATABASE webslt;
\q
```

---

## Configuration

### Default Configuration

The application works out-of-the-box with defaults in `application.conf`:

```hocon
# Database (defaults assume Docker container with postgres:postgres)
url = jdbc:postgresql://localhost:5432/webslt
driver = org.postgresql.Driver
username = postgres
password = password

# JWT
secret = change-me-in-production-use-environment-variable
expirationHours = 24

# CORS (allows frontend on port 5173)
allowedOrigins = http://localhost:5173

# Server
port = 8080
```

**Important**: The default credentials (`postgres`/`password`) match the Docker command shown in Prerequisites. If you're using a different PostgreSQL setup, override with environment variables.

### Environment Variables (Optional)

For custom configuration, set environment variables:

**Linux/macOS**:
```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/webslt
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=password
export JWT_SECRET=your-super-secret-key-minimum-32-characters-long
export CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:8080
export PORT=8080
```

**Windows PowerShell**:
```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/webslt"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="password"
$env:JWT_SECRET="your-super-secret-key-minimum-32-characters-long"
$env:CORS_ALLOWED_ORIGINS="http://localhost:5173,http://localhost:8080"
$env:PORT="8080"
```

**Windows CMD**:
```cmd
set DATABASE_URL=jdbc:postgresql://localhost:5432/webslt
set DATABASE_USERNAME=postgres
set DATABASE_PASSWORD=password
set JWT_SECRET=your-super-secret-key-minimum-32-characters-long
set CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:8080
set PORT=8080
```

### Using .env File (Alternative)

Create `.env` file in project root (⚠️ **Add to `.gitignore`** - never commit credentials):

```bash
# Database (must match PostgreSQL container or native setup)
DATABASE_URL=jdbc:postgresql://localhost:5432/webslt
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=password
DATABASE_DRIVER=org.postgresql.Driver

# JWT (MUST CHANGE IN PRODUCTION!)
JWT_SECRET=your-super-secret-key-minimum-32-characters-long-change-this
JWT_EXPIRATION_HOURS=24
JWT_AUDIENCE=webslt
JWT_REALM=WebSLT Server

# CORS (allows frontend dev servers)
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:8080

# Server
PORT=8080
```

**Note**: The application loads configuration from `application.conf` first, then overrides with environment variables. You can either:
- Set environment variables directly
- Use a tool like `direnv` or `dotenv` to load `.env` automatically
- Source the `.env` file manually: `source .env` (Linux/macOS)

---

## Building the Project

### Clean Build

```bash
# From project root directory
./gradlew clean build
```

**On Windows**:
```bash
.\gradlew.bat clean build
```

**Expected Output** (success):
```
BUILD SUCCESSFUL in 45s
23 actionable tasks: 23 executed
```

**If build fails**:
- Check Java version (`java -version` should be 11+)
- Check internet connection (Gradle downloads dependencies)
- See [Troubleshooting Guide](./Troubleshooting.md) for common issues

---

## Running the Server

### Development Mode

```bash
./gradlew server:run
```

**Expected Output**:
```
> Task :server:run
2026-02-05 14:30:00.123 [main] INFO  Application - Autoreload is disabled
2026-02-05 14:30:00.456 [main] INFO  ktor.application - Responding at http://0.0.0.0:8080
2026-02-05 14:30:00.789 [main] INFO  DatabaseConfig - Database initialized successfully
<=========----> 80% EXECUTING [1m 23s]
> :server:run
```

**Server is ready when you see**: `Responding at http://0.0.0.0:8080`

### Running in Background

```bash
# Linux/macOS
nohup ./gradlew server:run > server.log 2>&1 &

# Get process ID
echo $!

# View logs
tail -f server.log

# Stop server
kill <process-id>
```

### Using IntelliJ IDEA

1. Open project in IntelliJ IDEA
2. Wait for Gradle sync to complete
3. Find `Application.kt`
4. Click green ▶️ icon next to `fun main()`
5. Select "Run 'ApplicationKt'"

**Run Configuration** (already exists):
- Main class: `be.tinvision.webslt.ApplicationKt`
- Module: `webslt.server.main`
- Environment variables: (set as needed)

---

## Verifying the Server

### 1. Health Check

```bash
curl http://localhost:8080/api/v1/health
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "status": "ok",
    "timestamp": 1707140400000
  },
  "error": null
}
```

✅ If you see this, the server is running correctly!

### 2. Database Connection

Check server logs for:
```
INFO  DatabaseConfig - Database initialized successfully
INFO  Flyway - Successfully validated 1 migration
INFO  Flyway - Creating Schema History table
```

✅ If you see these, database connection is working!

### 3. Test Registration

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"testpass123"}'
```

**Expected Response** (201 Created):
```json
{
  "success": true,
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "test@example.com",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400
  },
  "error": null
}
```

✅ If you get a token back, everything is working!

For more endpoint tests, see [Testing Guide](./Testing-Guide.md).

---

## Stopping the Server

### Ctrl+C Method

In the terminal where server is running:
```bash
Press: Ctrl + C
```

Server will shut down gracefully.

### Gradle Stop

If server is running in background:
```bash
./gradlew --stop
```

### Finding and Killing Process

**Linux/macOS**:
```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill <PID>

# Force kill if needed
kill -9 <PID>
```

**Windows**:
```cmd
# Find process using port 8080
netstat -ano | findstr :8080

# Kill process
taskkill /PID <PID> /F
```

---

## Project Structure

```
WebSLT/
├── server/                      # Backend server
│   ├── src/main/kotlin/         # Kotlin source code
│   │   └── be/tinvision/webslt/
│   │       ├── Application.kt   # Entry point
│   │       ├── plugins/         # Ktor plugins
│   │       ├── routes/          # API endpoints
│   │       ├── services/        # Business logic
│   │       ├── repositories/    # Data access
│   │       ├── models/          # Domain models
│   │       ├── dto/             # Request/Response DTOs
│   │       └── config/          # Configuration
│   └── src/main/resources/
│       ├── application.conf     # Runtime configuration
│       ├── logback.xml          # Logging configuration
│       └── db/migration/        # Database migrations
├── shared/                      # Shared code (KMP)
├── gradle/                      # Gradle wrapper
├── build.gradle.kts             # Root build config
└── settings.gradle.kts          # Project structure
```

---

## Development Workflow

### 1. Start Database

```bash
docker start webslt-postgres
# or
docker run --name webslt-postgres ...
```

### 2. Build Project

```bash
./gradlew build
```

### 3. Run Server

```bash
./gradlew server:run
```

### 4. Test Endpoints

```bash
curl http://localhost:8080/api/v1/health
```

### 5. Make Changes

Edit code in `server/src/main/kotlin/`

### 6. Rebuild & Restart

```bash
# Stop server (Ctrl+C)
./gradlew build
./gradlew server:run
```

**Note**: Auto-reload is not enabled by default. You must restart after changes.

---

## Hot Reload (Optional)

To enable auto-reload on code changes:

**In build.gradle.kts**:
```kotlin
application {
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}
```

**Then run**:
```bash
./gradlew server:run --continuous
```

Server will rebuild and restart on file changes.

---

## Production Deployment

### Environment Variables Required

```bash
DATABASE_URL=jdbc:postgresql://prod-db-host:5432/webslt
DATABASE_DRIVER=org.postgresql.Driver
JWT_SECRET=<generated-strong-secret>  # min 32 chars
JWT_EXPIRATION_HOURS=24
CORS_ALLOWED_ORIGINS=https://your-frontend.com
PORT=8080
```

### Build for Production

```bash
./gradlew build
./gradlew installDist

# Run the distribution
./server/build/install/server/bin/server
```

### Docker Deployment

```bash
# Build Docker image
docker build -f Dockerfile.backend -t webslt-backend:latest .

# Run container
docker run -d \
  --name webslt-backend \
  -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host:5432/webslt \
  -e JWT_SECRET=your-secret \
  webslt-backend:latest
```

---

## Monitoring

### Application Logs

Logs output to console by default (configured in `logback.xml`):

```
2026-02-05 14:30:00.123 [main] INFO  Application - Server started
2026-02-05 14:30:01.456 [worker] INFO  AuthRoutes - User registered: test@example.com
```

### Database Migrations

Check Flyway status:
```bash
docker exec -it webslt-postgres psql -U postgres -d webslt -c \
  "SELECT * FROM flyway_schema_history;"
```

### Health Monitoring

Set up periodic health checks:
```bash
# Every 30 seconds
watch -n 30 'curl -s http://localhost:8080/api/v1/health | jq'
```

---

## Quick Reference

| Command | Purpose |
|---------|---------|
| `./gradlew build` | Compile project |
| `./gradlew server:run` | Run server |
| `./gradlew clean` | Clean build artifacts |
| `./gradlew test` | Run tests |
| `docker start webslt-postgres` | Start database |
| `curl http://localhost:8080/api/v1/health` | Health check |

---

## Next Steps

- 📖 [Testing Guide](./Testing-Guide.md) - How to test all endpoints
- 🐛 [Troubleshooting Guide](./Troubleshooting.md) - Common issues and solutions
- 🏗️ [Architecture](./Architecture.md) - System design and patterns
- ✅ [Agent Verification Guide](./Agent-Verification-Guide.md) - For AI agents

---

## Getting Help

- Check [Troubleshooting Guide](./Troubleshooting.md) first
- Review server logs for error details
- Verify database is running (`docker ps`)
- Ensure correct Java version (`java -version`)
- Check port 8080 is not in use

If issues persist, check:
- `.claude/rules/` - Project conventions
- `docs/` - Additional documentation
- GitHub Issues (if repository is public)
