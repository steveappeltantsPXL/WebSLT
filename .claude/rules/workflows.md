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
