# Security Rules

## Authentication (planned)

- JWT Bearer tokens with short expiration (24h)
- Refresh tokens for session extension (stored server-side)
- bcrypt password hashing (cost factor 12+)
- Rate limiting on `/api/v1/auth/*` endpoints

## CORS (planned)

- Allow only configured frontend origins
- Development: `http://localhost:8080` (webApp dev server)
- Production: configured via `CORS_ALLOWED_ORIGINS` env var
- Allow methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
- Allow headers: Content-Type, Authorization

## Input Validation

- Validate ALL inputs server-side (never trust client-only validation)
- Use DTO validation methods or dedicated validators
- Reject oversized payloads
- Sanitize string inputs

## Data Protection

- TLS 1.3 for data in transit
- Never store raw video (only extracted landmarks)
- Use UUIDs for all external-facing IDs
- Anonymize data where possible

## Logging Constraints

- NEVER log: passwords, full JWTs, email addresses, PII
- DO log: user IDs (UUIDs), action types, timestamps, error codes
- Use structured logging (SLF4J + Logback)

## Secrets Management

- No hardcoded secrets in source code
- Use environment variables or application.conf with env var substitution
- `.env` files must be in `.gitignore`

## Planned Environment Variables

```
DATABASE_URL=postgresql://localhost:5432/webslt
JWT_SECRET=<generated-secret>
JWT_EXPIRATION_HOURS=24
S3_BUCKET=webslt-models
S3_ENDPOINT=http://localhost:9000
CORS_ALLOWED_ORIGINS=http://localhost:8080
```

## Status

- [x] Logback configured (console appender)
- [ ] CORS plugin installed
- [ ] JWT authentication
- [ ] bcrypt password hashing
- [ ] Rate limiting
- [ ] Input validation framework
- [ ] application.conf with env var substitution
- [ ] Secrets management (.env support)
