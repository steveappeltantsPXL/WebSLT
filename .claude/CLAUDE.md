# WebSLT

Sign language translation platform. ML inference runs in-browser; backend handles data collection, auth, and model serving.

## Architecture

- `server/` -- Ktor backend (Kotlin, Netty, port 8080)
- `shared/` -- Kotlin Multiplatform (JVM + JS targets, generates TypeScript definitions)
- `webApp/` -- React + TypeScript frontend (Vite, consumes shared via Kotlin/JS)

## Key Commands

- Build all: `./gradlew build`
- Run server: `./gradlew server:run`
- Build shared JS: `./gradlew :shared:jsBrowserDevelopmentLibraryDistribution`
- Install frontend deps: `cd webApp && npm install`
- Run frontend: `cd webApp && npm run start`
- Run tests: `./gradlew test`

## Package

- Group: `be.tinvision.webslt`
- Package: `be.tinvision.webslt`

## Critical Constraints

- Backend does NOT do ML inference (browser-only via TensorFlow.js)
- Never store raw video (only extracted landmark data)
- Use UUIDs in API responses, never expose internal database IDs
- No PII in logs (no passwords, JWTs, emails)
- Prefer `val` over `var`, avoid `!!` operator

## Current Status

Scaffolding phase. See `.claude/rules/project-status.md` for roadmap.

## Detailed Rules

See `.claude/rules/` for conventions per concern (backend, frontend, API design, database, testing, security).

## Reference Docs

- `docs/Architecture.md` -- System architecture, deployment, data flows
- `docs/Coding-Rules.md` -- Kotlin + TypeScript conventions, SOLID, Clean Architecture
- `docs/Testing-Rules.md` -- Testing strategy, test types, priorities
