# Contributing to WebSLT Backend

First off, thanks for taking the time to contribute! 🎉

**Important**: This is the WebSLT **backend** repository (Ktor server + Kotlin Multiplatform shared module). For frontend contributions (React + TypeScript), please visit the separate [WebSLT-Frontend](https://github.com/steveappeltantsPXL/WebSLT-Frontend) repository.

We welcome contributions of all kinds, including:
- API endpoint implementations
- Business logic services
- Database schema and repositories
- Shared Kotlin code (JVM + JS targets)
- Authentication and authorization
- Bug fixes and performance improvements
- Documentation improvements

## How to Contribute

### 1. Fork and Clone
Fork the repository on GitHub and clone your fork locally:
```bash
git clone https://github.com/steveappeltantsPXL/WebSLT.git
cd WebSLT
```

### 2. Create a Branch
Create a new branch for your feature or fix. Please use descriptive names:
```bash
# Good examples for backend work:
git checkout -b feature/jwt-authentication
git checkout -b feature/training-data-api
git checkout -b bugfix/database-connection-pool
git checkout -b refactor/expose-repository-pattern
```

### 3. Development Setup

#### Prerequisites
- JDK 17 or higher
- PostgreSQL (when implementing database features)
- Gradle (wrapper included)

#### Build Commands
```bash
# Build the entire project
./gradlew build

# Run the Ktor server (port 8080)
./gradlew server:run

# Build shared Kotlin/JS library (required before frontend development)
./gradlew :shared:jsBrowserDevelopmentLibraryDistribution

# Run all tests
./gradlew test

# Run backend tests only
./gradlew server:test

# Run shared module tests
./gradlew shared:test
```

#### Technology Stack
- **Kotlin** 2.3.0
- **Ktor** 3.3.3 (Netty engine)
- **Kotlin Multiplatform** (JVM + JS targets)
- **PostgreSQL + Exposed ORM** (planned, Phase 1)
- **JWT Authentication** (planned, Phase 1)
- **Package**: `be.tinvision.webslt`

### 4. Make Changes
Implement your changes. Ensure your code follows the project's coding standards and passes all tests.

#### Coding Standards
This project follows strict coding conventions documented in `.claude/rules/`:
- **Backend**: [backend.md](../.claude/rules/backend.md) - Ktor architecture, route/service/repository patterns
- **Shared Module**: [shared.md](../.claude/rules/shared.md) - Kotlin Multiplatform conventions, expect/actual patterns
- **API Design**: [api-design.md](../.claude/rules/api-design.md) - RESTful conventions, response formats
- **Database**: [database.md](../.claude/rules/database.md) - Schema design, Exposed ORM patterns
- **Testing**: [testing.md](../.claude/rules/testing.md) - Test structure, naming conventions
- **Security**: [security.md](../.claude/rules/security.md) - Authentication, input validation, logging constraints

For comprehensive coding rules, see [docs/Coding-Rules.md](../docs/Coding-Rules.md).

#### Key Conventions
- Use three-layer architecture: Routes → Services → Repositories
- All API responses wrapped in `ApiResponse<T>`
- Use UUIDs for external IDs (never expose database IDs)
- No `!!` operator - use safe calls + Elvis operator
- No logging of passwords, JWTs, or PII
- All tests use backtick names describing behavior

### 5. Test Your Changes
Before committing, ensure all tests pass:
```bash
# Run all tests
./gradlew test

# Verify your changes don't break existing functionality
./gradlew build
```

All contributions must include appropriate tests:
- **Route tests**: Verify HTTP status codes, response bodies, authentication
- **Service tests**: Test business logic, validation, error handling
- **Repository tests**: Use Testcontainers for PostgreSQL integration tests (when DB layer exists)
- **Shared module tests**: Test validation logic, data transformations

See [docs/Testing-Rules.md](../docs/Testing-Rules.md) for comprehensive testing strategy.

### 6. Commit Changes
Commit your changes with clear, concise messages following the format `type: description`:
```bash
# Backend examples:
git commit -s -m "feat: add JWT authentication endpoints"
git commit -s -m "fix: correct database connection pool timeout"
git commit -s -m "refactor: extract validation logic to shared module"
```

### 7. Push and Pull Request
Push your branch to your fork and open a Pull Request (PR) against the `main` branch of the original repository.
```bash
git push origin feature/jwt-authentication
```

In your PR description:
- Explain what changes you made and why
- Reference any related issues
- Describe how you tested your changes
- Note any breaking changes or migration requirements

## Sign Your Work and DCO

To improve tracking of who did what, we require a "sign-off" on all commits. This certifies that you have the rights to submit your code under the AGPL-3.0 license.

The sign-off is a simple line at the end of the explanation for the patch. Your signature certifies that you wrote the patch or otherwise have the right to pass it on as an open-source patch.

### How to Sign Off
To sign off a commit, add the `-s` flag to your commit command:
```bash
git commit -s -m "feat: my change"
```
This will automatically append `Signed-off-by: Your Name <your.email@example.com>` to your commit message.

## Developer Certificate of Origin (DCO)

All contributions to this project must be accompanied by a sign-off. By signing off your contribution, you certify that you have the right to submit it under the open source license indicated.

Developer Certificate of Origin
Version 1.1

Copyright (C) 2004, 2006 The Linux Foundation and its contributors.

Everyone is permitted to copy and distribute verbatim copies of this
license document, but changing it is not allowed.


Developer's Certificate of Origin 1.1

By making a contribution to this project, I certify that:

(a) The contribution was created in whole or in part by me and I
    have the right to submit it under the open source license
    indicated in the file; or

(b) The contribution is based upon previous work that, to the best
    of my knowledge, is covered under an appropriate open source
    license and I have the right under that license to submit that
    work with modifications, whether created in whole or in part
    by me, under the same open source license (unless I am
    permitted to submit under a different license), as indicated
    in the file; or

(c) The contribution was provided directly to me by some other
    person who certified (a), (b) or (c) and I have not modified
    it.

(d) I understand and agree that this project and the contribution
    are public and that a record of the contribution (including all
    personal information I submit with it, including my sign-off) is
    maintained indefinitely and may be redistributed consistent with
    this project or the open source license(s) involved.

## Code of Conduct

We are committed to providing a friendly, safe, and welcoming environment for all.
We have adopted the [Contributor Covenant](https://www.contributor-covenant.org/) as our standard.

Please read and respect the [Contributor Covenant Code of Conduct v2.1](https://www.contributor-covenant.org/version/2/1/code_of_conduct/) when participating in this community.