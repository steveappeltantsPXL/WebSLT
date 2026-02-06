---
globs: webApp/** (DEPRECATED - see WebSLT-Frontend repository)
---
# Frontend Rules (React + TypeScript) - DEPRECATED

**NOTE**: These rules are deprecated. The frontend has been moved to a separate repository: `WebSLT-Frontend`.

This minimal webApp/ directory remains for backend testing/integration or purposes only.  
(maybe in the future we'll use it for a demo or a dashboard app for the backend)

---

# WebSLT-Frontend (Separate Repository)

All real frontend development happens in the sister repository at `../WebSLT-Frontend/`.

Refer to that project's `CLAUDE.md` and `webApp/` directory structure for actual frontend conventions.

---

# Archived Rules (for reference only)

## Current Setup

- React 18 + TypeScript + Vite
- Uses Kotlin/JS shared library (`shared` package imported as npm dependency)
- Dev server port: 8080 (configured in vite.config.ts)
- Build shared first: `./gradlew :shared:jsBrowserDevelopmentLibraryDistribution`

## Architecture

The frontend handles ALL ML inference in-browser. The backend is only for:
- Training data submission
- Model downloads
- Analytics events
- User authentication

## Component Conventions

- Functional components with hooks only (no class components)
- One component per file, file name matches component name
- Co-locate styles: `ComponentName/ComponentName.tsx` + `ComponentName.css`
- TypeScript strict mode enabled

## Code Patterns

- Local state: `useState`
- Shared state: context or zustand (when needed)
- Side effects: `useEffect` with cleanup functions
- Type all props with interfaces
- Use `import type` for type-only imports
- Avoid `any` -- use proper TypeScript types

## Shared Module Integration

- Import from `shared` package (Kotlin/JS compiled output)
- Shared module currently provides: Platform detection, constants, Greeting class
- Future: shared types, validation logic, API endpoint paths

## Installed Dependencies

- react, react-dom (18.x)
- shared (Kotlin/JS library, linked locally via npm workspaces)
- typescript, vite, @vitejs/plugin-react (dev)

## Dependencies to Add (when needed)

- `@tensorflow/tfjs` (ML inference in browser)
- `@mediapipe/hands` (hand landmark detection)
- `axios` or fetch wrapper (API client)
- `react-router-dom` (client-side routing)
- See `docs/Frontend-Setup-Commands.md` for install commands

## Don'ts

- Don't call server for real-time translation (all ML in browser)
- Don't use class components
- Don't skip TypeScript strict checks
- Don't put business logic in components (extract to hooks/services)

## Status

- [x] Vite + React + TypeScript scaffolding
- [x] Kotlin/JS shared module integration working
- [x] Demo Greeting component
- [ ] Camera capture component
- [ ] MediaPipe hand detection service
- [ ] TensorFlow.js gesture recognition service
- [ ] Translation display component
- [ ] Backend API client
- [ ] Routing setup (react-router-dom)
- [ ] State management