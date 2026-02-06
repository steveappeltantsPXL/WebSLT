# 📚 Documentation Index - For AI Agents

**Start here** to understand the project state and navigate documentation.

---

## 🚀 Start Here

**First time?** Read these in order:

1. **[PROJECT-CONTEXT.md](./.claude/PROJECT-CONTEXT.md)** ← **START HERE**
   - Quick facts, current phase, what's implemented
   - Build/run steps, key config files
   - Phase 2 roadmap

2. **[.claude/CLAUDE.md](./.claude/CLAUDE.md)**
   - Project overview
   - Key commands
   - Architecture diagram

3. **[docs/Running-Guide.md](./docs/Running-Guide.md)**
   - Prerequisites (JDK, PostgreSQL)
   - Configuration details
   - Build and run instructions
   - Environment variables

4. **[docs/Phase1-Verification.md](./docs/Phase1-Verification.md)**
   - What was implemented in Phase 1
   - What was fixed on Feb 6
   - Success criteria

---

## 📋 Documentation by Purpose

### "I want to understand the project"
- [.claude/PROJECT-CONTEXT.md](./.claude/PROJECT-CONTEXT.md) - **Most important**
- [.claude/rules/project-status.md](./.claude/rules/project-status.md) - Phase roadmap
- [.claude/CLAUDE.md](./.claude/CLAUDE.md) - Project summary
- [docs/Architecture.md](./docs/Architecture.md) - System design

### "I'm a specialized agent looking for context"
- [.claude/agents/README.md](./.claude/agents/README.md) - Agent context system guide
- [.claude/agents/kotlin-backend-context.md](./.claude/agents/kotlin-backend-context.md) - Backend patterns (kotlin-backend-advisor)
- [.claude/agents/security-context.md](./.claude/agents/security-context.md) - Security guidance (security-reviewer/auditor)
- [.claude/agents/test-context.md](./.claude/agents/test-context.md) - Testing patterns (test-architect)
- [.claude/agents/frontend-context.md](./.claude/agents/frontend-context.md) - Frontend patterns (general agents)

### "I want to build and run the backend"
- [docs/Running-Guide.md](./docs/Running-Guide.md) - Complete setup guide
- [docs/Troubleshooting.md](./docs/Troubleshooting.md) - When things break

### "I need to verify code before submitting"
- [docs/Agent-Verification-Guide.md](./docs/Agent-Verification-Guide.md) - AI agent checklist
- [docs/Phase1-Verification.md](./docs/Phase1-Verification.md) - Phase 1 verification results

### "I'm implementing Phase 2"
- See memory file: `~\.claude\projects\Projects-Visear-WebSLT\memory\Phase2-CoreAPI-Implementation.md`
- [.claude/rules/backend.md](./.claude/rules/backend.md) - Backend conventions
- [.claude/rules/api-design.md](./.claude/rules/api-design.md) - API patterns

### "I need to understand patterns and conventions"
- [docs/Coding-Rules.md](./docs/Coding-Rules.md) - Kotlin + TypeScript conventions
- [.claude/rules/backend.md](./.claude/rules/backend.md) - Backend rules
- [.claude/rules/database.md](./.claude/rules/database.md) - Database rules
- [docs/Testing-Rules.md](./docs/Testing-Rules.md) - Testing strategy

### "I need to test or debug"
- [docs/Testing-Guide.md](./docs/Testing-Guide.md) - API endpoint testing
- [docs/Troubleshooting.md](./docs/Troubleshooting.md) - Common issues
- [docs/Agent-Verification-Guide.md](./docs/Agent-Verification-Guide.md) - Verification steps

---

## 🗂️ Project Structure

```
WebSLT/                          ← Root project
├── .claude/                      ← AI agent context
│   ├── CLAUDE.md                 ← Project overview
│   ├── PROJECT-CONTEXT.md        ← **MASTER STATUS** ← READ THIS
│   ├── DOCUMENTATION-INDEX.md    ← You are here
│   └── rules/                    ← Conventions per concern
│       ├── api-design.md
│       ├── backend.md            ← Backend-specific rules
│       ├── database.md
│       ├── project-status.md     ← Phase roadmap
│       ├── security.md
│       ├── testing.md
│       └── workflows.md
├── docs/                         ← User-facing documentation
│   ├── Running-Guide.md          ← **How to build & run**
│   ├── Troubleshooting.md        ← **Common issues**
│   ├── Phase1-Verification.md    ← Verification checklist
│   ├── Agent-Verification-Guide.md
│   ├── Architecture.md
│   ├── Coding-Rules.md
│   ├── Testing-Guide.md
│   ├── Testing-Rules.md
│   └── ...
├── server/                       ← Backend (Ktor)
│   ├── src/main/kotlin/be/tinvision/webslt/
│   │   ├── Application.kt        ← Entry point (uses EngineMain)
│   │   ├── plugins/              ← Ktor configuration
│   │   ├── routes/               ← API endpoints
│   │   ├── services/             ← Business logic
│   │   ├── repositories/         ← Data access (Exposed)
│   │   ├── models/               ← Domain models
│   │   ├── dto/                  ← Request/Response objects
│   │   └── config/               ← DB + DI setup
│   └── src/main/resources/
│       └── application.conf      ← **Configuration file**
├── shared/                       ← Kotlin Multiplatform
│   ├── src/commonMain/kotlin/    ← JVM + JS shared code
│   └── src/jsMain/kotlin/        ← JavaScript-specific
├── gradle/                       ← Build configuration
│   └── libs.versions.toml        ← **All dependency versions**
└── README.md                     ← Quick start
```

---

## 🔍 Key Files to Know

| File | Purpose | Last Updated |
|------|---------|--------------|
| **.claude/PROJECT-CONTEXT.md** | **Master status + context** | Feb 6, 2026 |
| **gradle/libs.versions.toml** | All dependency versions | Feb 6, 2026 |
| **server/src/main/resources/application.conf** | Configuration | Feb 6, 2026 |
| **server/src/main/kotlin/.../Application.kt** | Entry point | Feb 6, 2026 |
| **docs/Running-Guide.md** | Build & run | Feb 6, 2026 |
| **docs/Troubleshooting.md** | Common issues | Feb 6, 2026 |

---

## ⚠️ Critical to Remember

### Configuration Loading
- Application MUST use `EngineMain.main(args)` (not `embeddedServer()`)
- Automatically loads `application.conf` from classpath
- Environment variables override config values

### Database
- Must be running: PostgreSQL 15
- Default credentials: `postgres` / `password`
- Migrations run automatically via Flyway

### CORS
- Uses `allowHost()` with `schemes` parameter
- Strips `http://` and `https://` from origins
- Example: `allowHost("localhost:5173", schemes = listOf("http", "https"))`

### Phase 1 is Complete ✅
- 5 API endpoints working
- Database connected
- Tests passing
- Ready for Phase 2

---

## 📝 When to Update This Index

Update `.claude/PROJECT-CONTEXT.md` and this file whenever:
- Phase changes (scaffolding → implementation → testing → production)
- Major features added
- Critical bugs fixed
- Documentation significant

## 🤖 AI Agent Workflow

1. **Read** [PROJECT-CONTEXT.md](./.claude/PROJECT-CONTEXT.md) first
2. **Check** the phase status (Phase 1 Complete, Phase 2 Ready)
3. **Find** relevant docs from the index above
4. **Read** `.claude/rules/` files for your specific task
5. **Reference** existing code as examples
6. **Verify** code before submitting (use Agent-Verification-Guide.md)
7. **Update** this documentation when work is complete

---

**Last Updated**: Feb 6, 2026
**Maintained by**: AI Agents + Users
**Questions?** Check [PROJECT-CONTEXT.md](./.claude/PROJECT-CONTEXT.md) first
