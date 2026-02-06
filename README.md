# WebSLT - Backend Repository

This is the **backend** component of WebSLT, a sign language translation platform.

## Architecture

This is a Kotlin Multiplatform project with the following structure:

* **[/server](./server/src/main/kotlin)** - Ktor backend server (Kotlin, port 8080)
  - REST API endpoints for authentication, training data, and model serving
  - Database integration (PostgreSQL + Exposed)
  - JWT authentication

* **[/shared](./shared/src)** - Kotlin Multiplatform (JVM + JS targets)
  - Code shared between server and frontend
  - Platform detection, constants, validation logic
  - Compiles to TypeScript definitions for frontend use

* **[/webApp](./webApp)** - Minimal demo React application (for backend testing only)
  - Uses the Kotlin/JS shared library
  - Not the real frontend - see **WebSLT-Frontend** repository below

---

## Quick Start

### Prerequisites
- **JDK 11+**: `java -version`
- **PostgreSQL**: Docker recommended

### 1. Start Database
```bash
docker run --name webslt-postgres \
  -e POSTGRES_DB=webslt \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres:15
```

### 2. Build & Run (remove build artifacts first manualy if needed)
```bash
./gradlew clean build
./gradlew server:run
```

### 3. Verify
```bash
curl http://localhost:8080/api/v1/health
```

Expected response:
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

✅ Server is running!

### 4. Test Authentication
```bash
# Register user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"testpass123"}'
```

📖 **For more details**: See [docs/Running-Guide.md](./docs/Running-Guide.md)

---

## Documentation

- **[Running Guide](./docs/Running-Guide.md)** - How to build and run the server
- **[Testing Guide](./docs/Testing-Guide.md)** - API endpoint testing with curl
- **[Troubleshooting Guide](./docs/Troubleshooting.md)** - Common issues and solutions
- **[Agent Verification Guide](./docs/Agent-Verification-Guide.md)** - For AI agents
- **[Architecture](./docs/Architecture.md)** - System design and patterns
- **[Coding Rules](./docs/Coding-Rules.md)** - Kotlin + TypeScript conventions
- **[Testing Rules](./docs/Testing-Rules.md)** - Testing strategy

---

### Build and Run Server (Detailed)

To build and run the development version of the server, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :server:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :server:run
  ```

### Frontend (Separate Repository)

The real frontend is in a **separate repository**: **WebSLT-Frontend**

It contains:
- React 18 + TypeScript + Vite setup
- Camera capture with MediaPipe hand detection
- TensorFlow.js gesture classification (all in-browser)
- Translation UI with real-time inference
- Backend API integration

Location: Sibling directory `../WebSLT-Frontend/`

### Build and Run Demo webApp (Backend Testing Only)

To build and run the minimal demo web app for backend testing:

1. Install [Node.js](https://nodejs.org/en/download) (which includes `npm`)

2. Build Kotlin/JS shared code:
    - on macOS/Linux
      ```shell
      ./gradlew :shared:jsBrowserDevelopmentLibraryDistribution
      ```
    - on Windows
      ```shell
      .\gradlew.bat :shared:jsBrowserDevelopmentLibraryDistribution
      ```
3. Build and run the demo application
    ## 📦 Installation

      ```shell
      npm install
      npm run start
      ```

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…

## 🤝 Contributing

We welcome contributions! Please see [CONTRIBUTING.md](.github/CONTRIBUTING.md) for details on how to get started, our code of conduct, and the Developer Certificate of Origin (DCO).

## 📄 License

This project is licensed under the **GNU Affero General Public License v3.0 (AGPL-3.0)**. See the [LICENSE](LICENSE) file for details.

## 📝 Issue Tracking

Found a bug? Have a feature request? Please use our [Issue Tracker](https://github.com/steveappeltantsPXL/WebSLT-Frontend/issues) using the provided templates.