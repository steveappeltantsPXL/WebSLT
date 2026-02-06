# WebApp - Admin Dashboard (Future)

This minimal React + TypeScript application is **reserved for backend admin dashboard** use.

## Current Status

- Contains demo Greeting component for testing Kotlin/JS shared module integration
- Dev server runs on port 8080
- Uses Kotlin Multiplatform shared module

## Future Use

This directory will be developed as an **admin dashboard** for WebSLT backend, including:
- User management UI
- Training data review interface
- Model management and deployment
- Analytics dashboard
- System health monitoring

## Build & Run

```bash
# Build shared Kotlin/JS module first
./gradlew :shared:jsBrowserDevelopmentLibraryDistribution

# Then run the app
cd webApp
npm install
npm run start
```

## Note

The real user-facing frontend for sign language translation is in the **separate WebSLT-Frontend repository** at `../WebSLT-Frontend/`.
