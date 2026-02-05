# Project Status & Roadmap

## Project: WebSLT (Web Sign Language Translation) - Backend Only

Sign language translation platform where ML inference runs in-browser.
This repository contains the **backend only** (Ktor server + Kotlin Multiplatform shared module).

**Frontend is in a separate repository**: `WebSLT-Frontend` (React + TypeScript, at sibling path)

## Current Phase: 0 -- Scaffolding

### What Exists

- [x] Kotlin Multiplatform project structure (server + shared + webApp)
- [x] Ktor server with Netty engine (single GET / route returning greeting)
- [x] Shared KMP module compiling for JVM + JS with TypeScript definitions
- [x] expect/actual platform abstraction pattern working
- [x] React 18 + TypeScript + Vite minimal webApp (demo only, see separate WebSLT-Frontend repo)
- [x] Basic Ktor test host test and shared common test
- [x] Docker Compose + Dockerfiles for backend and frontend
- [x] GitHub issue templates, CONTRIBUTING.md, PULL_REQUEST template
- [x] Gradle version catalog (libs.versions.toml) -- Kotlin 2.3.0, Ktor 3.3.3
- [x] Comprehensive docs/ (Architecture, Coding-Rules, Testing-Rules)
- [x] AGPL-3.0 license

### What Does NOT Exist Yet

- [ ] Any API endpoints beyond GET /
- [ ] kotlinx-serialization / content negotiation
- [ ] CORS configuration
- [ ] StatusPages error handling
- [ ] Database (PostgreSQL, Exposed, Flyway, HikariCP)
- [ ] Authentication (JWT, bcrypt)
- [ ] Koin dependency injection
- [ ] application.conf configuration file
- [ ] Any services, repositories, or domain models
- [ ] Any frontend components beyond demo Greeting
- [ ] Camera capture, MediaPipe, TensorFlow.js
- [ ] CI/CD pipeline

---

## Phase 1: Backend Foundation

Set up core backend infrastructure before implementing features.

1. Add kotlinx-serialization + content negotiation plugin
2. Add CORS plugin
3. Add StatusPages error handling
4. Create application.conf with environment variable support
5. Set up Koin dependency injection
6. Add PostgreSQL + HikariCP + Exposed
7. Set up Flyway migrations
8. Create users table + repository
9. Implement JWT authentication (register, login, refresh)
10. Create health check endpoint (`GET /api/v1/health`)

## Phase 2: Core API

Implement the essential backend endpoints.

1. ApiResponse<T> wrapper for consistent responses
2. Request/Response DTO framework with validation
3. User profile endpoints (GET/PATCH /api/v1/users/me)
4. Training data submission endpoint (POST /api/v1/training-data)
5. Model management endpoints (GET latest, download by version)
6. Input validation framework

## Phase 3: Frontend Foundation

Set up the real frontend application.

1. Camera capture component
2. MediaPipe hand detection integration
3. TensorFlow.js gesture recognition service
4. Translation display component
5. Backend API client service
6. Routing setup (react-router-dom)

## Phase 4: Integration

Connect frontend and backend.

1. Frontend auth flow (login, register, token management)
2. Training data contribution UI
3. Model download and caching in browser
4. Analytics event pipeline

## Phase 5: Polish

Production readiness.

1. CI/CD with GitHub Actions
2. Performance optimization
3. Accessibility (WCAG 2.1 AA)
4. Production deployment configuration
5. Monitoring and error tracking

---

## Reference Documentation

- `docs/Architecture.md` -- System design, deployment, data flows
- `docs/Coding-Rules.md` -- Kotlin + TypeScript conventions (1300+ lines)
- `docs/Testing-Rules.md` -- Test strategy, types, ROI analysis
- `docs/Frontend-Setup-Commands.md` -- npm install commands for ML dependencies
