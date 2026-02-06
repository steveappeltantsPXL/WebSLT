# Agent Context Files

This directory contains project-specific context for Claude Code agents. These files help agents understand your project's architecture, conventions, and current state.

## Purpose

When Claude launches specialized agents (kotlin-backend-advisor, security-reviewer, test-architect, etc.), these context files provide:
- Project-specific architecture and patterns
- Current implementation status
- Links to detailed documentation
- Key constraints and conventions

## Structure (Reusable Template)

Each agent context file follows this pattern:

```markdown
# {Agent Name} Context - {Project Name}

## Quick Facts
- Technology stack
- Current phase/status
- Key constraints

## Architecture Overview
- High-level structure
- Important patterns
- Links to detailed docs

## Current Implementation
- What exists
- What's planned
- What to avoid

## Key References
- Links to detailed rules
- Related documentation
```

## How to Use in Other Projects

1. Copy this `agents/` directory to your new project's `.claude/` folder
2. Update each context file with your project specifics:
   - Replace technology stack (Ktor → Spring, React → Vue, etc.)
   - Update current phase and implementation status
   - Adjust links to your project's documentation
   - Keep the structure - it helps agents quickly orient

3. Keep context files **concise** (200-400 lines max)
   - Link to detailed docs rather than duplicating them
   - Focus on what agents need to know quickly
   - Update as your project progresses

## Available Context Files

- `kotlin-backend-context.md` - Backend architecture, Ktor patterns, API design
- `security-context.md` - Security requirements, auth patterns, compliance
- `test-context.md` - Testing strategy, patterns, current coverage
- `frontend-context.md` - Frontend architecture, React patterns, state management

## Maintenance

Update context files when:
- Moving to a new project phase
- Changing major architectural decisions
- Adding significant new patterns or conventions
- Fixing critical issues that agents should know about

**Last Updated**: Feb 6, 2026 (Phase 1 Complete)
