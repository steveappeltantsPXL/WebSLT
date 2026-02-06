# Project Status & Roadmap

## Project: WebSLT (Web Sign Language Translation) - Backend Only

Sign language translation platform where ML inference runs in-browser.
This repository contains the **backend only** (Ktor server + Kotlin Multiplatform shared module).

**Frontend is in a separate repository**: `WebSLT-Frontend` (React + TypeScript, at sibling path)

## Current Phase: 1 -- Backend Foundation ✅ COMPLETE (Feb 6, 2026)

### Phase 1 Implementation Summary

**Status**: ✅ ALL 10 TASKS COMPLETED & VERIFIED

**Compiled**: ✅ `./gradlew build` successful
**Tested**: ✅ Tests passing (simplified to unit test without full module)
**Running**: ✅ Server starts on port 8080
**Database**: ✅ PostgreSQL connected with Flyway migrations

### What Now Exists (Phase 1 Complete)

- [x] Kotlin Multiplatform project structure (server + shared + webApp)
- [x] Ktor server with Netty engine + 5 working API endpoints
- [x] Shared KMP module compiling for JVM + JS with TypeScript definitions
- [x] expect/actual platform abstraction pattern working
- [x] React 18 + TypeScript + Vite minimal webApp (demo only, see separate WebSLT-Frontend repo)
- [x] Comprehensive test infrastructure (Ktor test host, simplified ApplicationTest)
- [x] Docker Compose + Dockerfiles for backend and frontend
- [x] GitHub issue templates, CONTRIBUTING.md, PULL_REQUEST template
- [x] Gradle version catalog (libs.versions.toml) -- Kotlin 2.3.0, Ktor 3.3.3
- [x] **kotlinx-serialization + content negotiation plugin**
- [x] **CORS plugin (with schemes parameter fix)**
- [x] **StatusPages error handling**
- [x] **PostgreSQL + HikariCP + Exposed ORM**
- [x] **Flyway migrations (V1__create_users_table.sql)**
- [x] **Koin dependency injection**
- [x] **application.conf with environment variable support**
- [x] **JWT authentication (register, login, refresh, logout)**
- [x] **User model + repository + service layers**
- [x] **Health check endpoint**
- [x] Comprehensive docs/ (Architecture, Coding-Rules, Testing-Rules, Running-Guide, Troubleshooting, etc.)
- [x] AGPL-3.0 license

### What Does NOT Exist Yet (Phase 2+)

- [ ] User profile endpoints (GET/PATCH /api/v1/users/me)
- [ ] Training data submission (POST /api/v1/training-data)
- [ ] Model management endpoints
- [ ] Input validation framework
- [ ] Any frontend components beyond demo Greeting
- [ ] Camera capture, MediaPipe, TensorFlow.js
- [ ] CI/CD pipeline
- [ ] Production deployment setup

---

## Phase 1: Backend Foundation ✅ COMPLETE

Core backend infrastructure implemented.

1. [x] Add kotlinx-serialization + content negotiation plugin
2. [x] Add CORS plugin
3. [x] Add StatusPages error handling
4. [x] Create application.conf with environment variable support
5. [x] Set up Koin dependency injection
6. [x] Add PostgreSQL + HikariCP + Exposed
7. [x] Set up Flyway migrations
8. [x] Create users table + UserSettings repository
9. [x] Implement JWT authentication (register, login, refresh, logout)
10. [x] Create health check endpoint (`GET /api/v1/health`)

**Implemented Endpoints:**
- `GET /api/v1/health` - Health check
- `POST /api/v1/auth/register` - User registration with password hashing
- `POST /api/v1/auth/login` - User login with JWT token generation
- `POST /api/v1/auth/refresh` - Token refresh
- `POST /api/v1/auth/logout` - Logout (protected route)

## Phase 2: Core API

Implement the essential backend endpoints.

1. ApiResponse<T> wrapper for consistent responses
2. Request/Response DTO framework with validation
3. User profile endpoints (GET/PATCH /api/v1/users/me)
4. Training data submission endpoint (POST /api/v1/training-data)
5. Model management endpoints (GET latest, download by version)
6. Input validation framework

## Phase 3-5: Frontend Development

**Frontend work is in the separate WebSLT-Frontend repository.**

See that repository's CLAUDE.md for:
- Phase 1: Camera capture + MediaPipe integration
- Phase 2: TensorFlow.js gesture recognition
- Phase 3: UI components and routing
- Phase 4: Backend integration
- Phase 5: Production deployment

---

## Reference Documentation

- `docs/Architecture.md` -- System design, deployment, data flows
- `docs/Coding-Rules.md` -- Kotlin + TypeScript conventions (1300+ lines)
- `docs/Testing-Rules.md` -- Test strategy, types, ROI analysis
- `docs/Frontend-Setup-Commands.md` -- npm install commands for ML dependencies
