# Development Workflows

## Branch Strategy

- Feature branches from `main`: `feature/<description>`
- Bug fixes: `bugfix/<description>`
- Hotfixes: `hotfix/<description>`
- Commit message format: `type: description` (feat, fix, refactor, perf, docs, test, chore)

## Build Commands

### Full Project
- `./gradlew build` -- Build everything
- `./gradlew clean build` -- Clean rebuild

### Server
- `./gradlew server:run` -- Run Ktor server (port 8080)
- `./gradlew server:run --continuous` -- Run with hot reload

### Shared Module
- `./gradlew :shared:jsBrowserDevelopmentLibraryDistribution` -- Build Kotlin/JS library
- Must be built before running the frontend

### Frontend (webApp)
- Requires shared JS built first
- `cd webApp && npm install` -- Install dependencies
- `cd webApp && npm run start` -- Dev server (port 8080)
- `cd webApp && npm run build` -- Production build

### Tests
- `./gradlew test` -- All tests
- `./gradlew server:test` -- Server tests only
- Test locations: `server/src/test/`, `shared/src/commonTest/`

### Docker
- `docker-compose up -d` -- Start all services
- Dockerfiles: `Dockerfile.backend`, `Dockerfile.frontend`

## Dependency Management

- All Gradle versions in `gradle/libs.versions.toml` (version catalog)
- Use version catalog aliases, never hardcode versions in build.gradle.kts
- Frontend deps in `webApp/package.json`
- Root `package.json` uses npm workspaces

## Verification Before Providing Code

**For AI agents**: Follow systematic verification to ensure code quality.

### Pre-Implementation
1. **Read relevant existing files** to understand patterns
2. **Verify dependencies** are available in `libs.versions.toml`
3. **Check imports** match actual package names
4. **Follow patterns** from existing code (DI, async, error handling)

### Post-Implementation
1. **Read back files created** to verify syntax
2. **Check build config** has all dependencies referenced
3. **Provide test command**: `./gradlew build`
4. **Provide run command**: `./gradlew server:run`
5. **Provide verification curl**: test endpoint with curl

### Never Provide Code That
- References non-existent dependencies
- Uses incorrect imports
- Breaks existing patterns
- Lacks proper error handling
- Won't compile on first try

See [`docs/Agent-Verification-Guide.md`](/docs/Agent-Verification-Guide.md) for complete checklist.

## Pre-Commit Checklist

- `./gradlew build` passes
- Tests pass
- Commit message follows `type: description` format

## Status

- [x] Gradle multi-project build working
- [x] Server run configuration
- [x] Shared KMP module compiles for JVM + JS
- [x] Frontend dev server working
- [x] Docker Compose + Dockerfiles
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Pre-commit hooks (ktlint)
- [ ] Production deployment pipeline