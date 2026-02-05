---
globs: shared/**
---
# Shared Module Rules (Kotlin Multiplatform)

## Purpose

Code shared between JVM (server) and JS (frontend) targets.
Use for: constants, data models, validation logic, platform abstractions.

## Targets

- **JVM**: consumed by server module
- **JS**: compiled to browser library, consumed by webApp
  - Output module name: `shared`
  - Generates TypeScript definitions
  - ES2015 target

## Build Commands

- `./gradlew :shared:jsBrowserDevelopmentLibraryDistribution` -- Build JS library (dev)
- `./gradlew :shared:jvmJar` -- Build JVM artifact
- JS output: linked via npm workspaces to `webApp/`

## Conventions

- Use `expect`/`actual` for platform-specific implementations
- Common code in `commonMain`, platform code in `jvmMain`/`jsMain`
- Public API for JS: annotate with `@JsExport`
- Keep platform implementations minimal -- push logic to common

## Current Files

- `Platform.kt` -- expect/actual for platform detection
- `Greeting.kt` -- demo @JsExport class
- `Constants.kt` -- SERVER_PORT = 8080

## What Belongs Here (future)

- API endpoint path constants (shared between server routes and frontend client)
- Data model classes (sign labels, landmark structures, API request/response shapes)
- Validation logic (landmark count validation, input constraints)
- Constants (thresholds, limits, configuration defaults)

## What Does NOT Belong Here

- Server-only logic (database queries, auth, Ktor plugins)
- Frontend-only logic (React components, DOM, browser APIs)
- Platform-specific libraries

## Status

- [x] KMP configured for JVM + JS
- [x] TypeScript definition generation
- [x] expect/actual pattern working
- [x] @JsExport demo class
- [ ] API path constants
- [ ] Shared data models (landmarks, training data)
- [ ] Shared validation logic
- [ ] kotlinx-serialization in commonMain
