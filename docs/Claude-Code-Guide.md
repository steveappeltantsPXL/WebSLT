# Claude Code Documentation Guide

How the documentation system works for this project and how to maintain it as the application grows.

---

## File Hierarchy

Claude Code reads these files automatically at the start of every session:

| File | Purpose | Shared via git? |
|------|---------|-----------------|
| `.claude/CLAUDE.md` | Project essentials (always loaded) | Yes |
| `.claude/rules/*.md` | Topic-specific rules (some path-scoped) | Yes |
| `CLAUDE.local.md` | Personal local preferences | No (gitignored) |
| `~/.claude/CLAUDE.md` | Your global preferences (all projects) | No |
| `~/.claude/projects/<project>/memory/MEMORY.md` | Claude's auto-memory | No |

---

## Our Project's Structure

```
.claude/
  CLAUDE.md                     # ~45 lines -- project identity, commands, constraints
  rules/
    workflows.md                # Branch strategy, build commands, commit conventions
    project-status.md           # THE master roadmap -- phases, what's done, what's next
    backend.md                  # Ktor conventions (only loads for server/** files)
    frontend.md                 # React/TS conventions (only loads for webApp/** files)
    shared.md                   # KMP shared module rules (only loads for shared/** files)
    api-design.md               # REST API standards (only loads for routes/dto files)
    database.md                 # DB patterns (only loads for repositories/models/migrations)
    testing.md                  # Testing strategy and conventions
    security.md                 # Auth, CORS, data protection rules
```

---

## How Each File Works

### CLAUDE.md (The Foundation)

Always loaded. Contains ONLY what Claude needs every single session:
- One-line project description
- Architecture overview (3 modules)
- Build/run commands that work today
- Package/group identifiers
- Critical constraints (no server ML, no raw video, UUIDs, no PII in logs)
- Pointers to rules/ and docs/

**Rule of thumb:** For each line, ask "Would removing this cause Claude to make mistakes?" If not, move it to a rule file.

### Path-Scoped Rules

Files with YAML frontmatter at the top only load when you're editing matching files:

```markdown
---
globs: server/**
---
# Backend Rules
...
```

This means `backend.md` only loads when you're working on server code, `frontend.md` only for webApp code, etc. This keeps Claude's context focused and relevant.

### project-status.md (The Growing Document)

This is the key file that tracks what's built vs. what's planned:
- Current development phase
- Checkbox lists: `[x]` for done, `[ ]` for pending
- Phased development roadmap with ordered tasks
- Updated after each feature is implemented

### Individual Rule Files

Each rule file contains:
1. **Conventions** -- How to write code for this area
2. **Patterns** -- Code examples and standards
3. **Dependencies** -- What needs to be added
4. **Don'ts** -- Common mistakes to avoid
5. **Status** -- What's implemented vs. planned (checkboxes)

---

## How Documents Grow With the Application

### After Implementing a Feature

1. **Update the relevant rule file's Status section** -- check off completed items
2. **Update project-status.md** -- move items from planned to done
3. **Add new conventions** discovered during implementation
4. **Add Decisions** entries for architectural choices:

```markdown
### Decision: Use Exposed DSL Over DAO
**Date:** 2026-02-05
**Rationale:** DSL gives more control over queries, less magic
**Files:** server/src/.../repositories/
```

5. **Add Known Issues** for technical debt:

```markdown
### Known Issues
- Rate limiting not implemented on auth endpoints
- Token refresh doesn't invalidate old tokens
```

### When Starting a New Phase

1. Update `project-status.md` current phase
2. Create any new rule files needed
3. Update dependency lists in relevant rule files

### Example: After Implementing JWT Auth

In `security.md`, change:
```markdown
- [ ] JWT authentication
```
to:
```markdown
- [x] JWT authentication (implemented 2026-02-10)
```

In `project-status.md`, check off:
```markdown
- [x] Implement JWT authentication (register, login, refresh)
```

In `backend.md`, add if you discovered a new pattern:
```markdown
## Auth Pattern
- Use `authenticate("jwt") { ... }` block for protected routes
- Extract user from `call.principal<JWTPrincipal>()`
```

---

## Memory System

### Auto-Memory (MEMORY.md)

Located at `~/.claude/projects/<project>/memory/MEMORY.md`. Claude writes to this automatically when it learns something important during a session. It persists across conversations.

Use for: recording gotchas, failed approaches, debugging insights.

### How Claude Remembers Between Sessions

Claude does NOT have continuous memory between conversations. Each new session starts fresh. What provides continuity:

1. **CLAUDE.md + rules/** -- Always loaded, contains project knowledge
2. **MEMORY.md** -- Auto-memory with session-specific learnings
3. **project-status.md** -- Shows exactly where development left off
4. **Git history** -- Claude can read recent commits to understand what changed
5. **Status checkboxes** -- In every rule file, showing what's done

This is why the status sections are critical -- they're how a new Claude session knows what was already implemented.

---

## Tips for Effective Use

1. **Keep CLAUDE.md under 50 lines** -- Claude ignores parts of overly long files
2. **Use path-scoping** -- Don't load backend rules when editing frontend
3. **Update status after every feature** -- This is the #1 way to maintain continuity
4. **Don't duplicate docs/** -- Rule files reference docs/ for deep detail
5. **Each rule file under 100 lines** -- Focused and scannable
6. **Commit rule files with code** -- They evolve together
7. **Use CLAUDE.local.md for personal stuff** -- Local URLs, personal preferences

---

## Quick Reference

| I want to... | Update this file |
|---------------|-----------------|
| Change build commands | `workflows.md` |
| Track feature progress | `project-status.md` + relevant rule file Status |
| Add a coding convention | Relevant rule file (backend.md, frontend.md, etc.) |
| Record an arch decision | Relevant rule file, add a Decision entry |
| Note a personal preference | `CLAUDE.local.md` (create if needed) |
| Fix something Claude keeps getting wrong | Add a rule to the relevant file |
