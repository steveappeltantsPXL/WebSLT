# WebSLT Backend

Kotlin Multiplatform project using Ktor (backend) and React/TypeScript (frontend), built with Gradle.


## Project Structure

- `server/` — Ktor backend application
- `shared/` — Shared Kotlin Multiplatform code (JVM + JS)
- `webApp/` — React/TypeScript frontend (Vite)

## Key Commands

- Build: `./gradlew build`
- Run server: `./gradlew server:run`
- Run web app: `cd webApp && npm run dev`

## Rules

See `.claude/rules/` for detailed coding rules, workflows, and architecture docs.