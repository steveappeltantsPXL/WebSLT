---
globs: server/src/main/kotlin/be/tinvision/webslt/repositories/**, server/src/main/kotlin/be/tinvision/webslt/models/**, server/src/main/resources/db/**
---
# Database Rules

## Technology Stack (to be added)

- PostgreSQL (primary database)
- Exposed (Kotlin SQL framework -- DSL style preferred)
- HikariCP (connection pooling)
- Flyway (schema migrations)

## Planned Schema

```sql
users          (id UUID PK, email, password_hash, created_at, updated_at)
user_settings  (user_id FK, sign_language, theme, notifications_enabled)
training_samples (id UUID PK, user_id FK, sign_label, landmarks_json, recorded_at, validated)
models         (id UUID PK, version, file_path, accuracy, created_at, is_active)
analytics_events (id UUID PK, user_id FK, event_type, payload_json, timestamp)
```

## Conventions

- All tables use UUID primary keys
- All tables include `created_at`, `updated_at` timestamps
- Flyway versioned migrations: `V1__create_users_table.sql`
- Migration files in `server/src/main/resources/db/migration/`
- Never modify existing migrations -- always create new ones

## Repository Pattern

- Interface in `repositories/` package defining operations
- Implementation uses Exposed DSL
- Wrap DB calls in `withContext(Dispatchers.IO)` + `transaction { }`
- Return domain models, not Exposed Row objects

## Connection Management

- HikariCP for connection pooling
- Configure via application.conf / environment variables
- Dev: H2 in-memory for quick iteration (optional)
- Test: Testcontainers PostgreSQL
- Production: PostgreSQL with connection pooling

## Domain Models

- Plain Kotlin data classes in `models/` package
- No framework annotations on domain models
- Exposed table definitions in repository implementation files
- DTOs separate from domain models (in `dto/` package)

## Don'ts

- No raw SQL strings (use Exposed DSL)
- No data mutations in GET request handlers
- No cascade deletes without explicit documentation
- No storing raw video/images in DB (use S3-compatible storage)

## Status

- [x] PostgreSQL driver dependency added
- [x] HikariCP configured
- [x] Exposed integration
- [x] Flyway migration setup
- [x] Users table + user_settings table + migration (V1)
- [ ] Training samples table + migration
- [ ] Models table + migration
- [ ] Analytics events table + migration
- [x] Repository interfaces (UserRepository)
- [x] Repository implementations (UserRepositoryImpl)
- [ ] Testcontainers test setup
