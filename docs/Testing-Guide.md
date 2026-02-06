# Testing Guide - WebSLT Backend API

**Purpose**: Comprehensive guide for testing all API endpoints manually with curl.

---

## Prerequisites

1. **Server running**: `./gradlew server:run`
2. **curl installed**: Test with `curl --version`
3. **jq installed** (optional, for pretty JSON): `brew install jq` or `apt install jq`

---

## Health Check Endpoint

### GET /api/v1/health

**Purpose**: Verify server is running

**Request**:
```bash
curl http://localhost:8080/api/v1/health
```

**Expected Response** (200 OK):
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

**What it tests**:
- ✅ Server is running
- ✅ Serialization works
- ✅ Basic routing works

---

## Authentication Endpoints

### POST /api/v1/auth/register

**Purpose**: Create new user account

**Request**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "securepass123"
  }'
```

**Expected Response** (201 Created):
```json
{
  "success": true,
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "alice@example.com",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1NTBlODQwMC1lMjliLTQxZDQtYTcxNi00NDY2NTU0NDAwMDAiLCJlbWFpbCI6ImFsaWNlQGV4YW1wbGUuY29tIiwiZXhwIjoxNzA3MjI2ODAwfQ.signature",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400
  },
  "error": null
}
```

**Save the tokens**:
```bash
# Save for later use
export ACCESS_TOKEN="<accessToken from response>"
export REFRESH_TOKEN="<refreshToken from response>"
```

**What it tests**:
- ✅ User creation in database
- ✅ Password hashing (bcrypt)
- ✅ JWT token generation
- ✅ Input validation

---

### Error Cases: Invalid Registration

**Empty email**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"","password":"test123"}'
```

**Expected Response** (400 Bad Request):
```json
{
  "success": false,
  "data": null,
  "error": "Email cannot be empty"
}
```

**Invalid email format**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"notanemail","password":"test123"}'
```

**Expected Response** (400 Bad Request):
```json
{
  "success": false,
  "data": null,
  "error": "Invalid email format"
}
```

**Password too short**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"short"}'
```

**Expected Response** (400 Bad Request):
```json
{
  "success": false,
  "data": null,
  "error": "Password must be at least 8 characters"
}
```

**Duplicate email**:
```bash
# Register same user twice
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"securepass123"}'
```

**Expected Response** (400 Bad Request):
```json
{
  "success": false,
  "data": null,
  "error": "User with this email already exists"
}
```

---

### POST /api/v1/auth/login

**Purpose**: Authenticate existing user

**Request**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "securepass123"
  }'
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "alice@example.com",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400
  },
  "error": null
}
```

**What it tests**:
- ✅ Password verification (bcrypt)
- ✅ User lookup by email
- ✅ JWT generation
- ✅ Access + refresh tokens

---

### Error Cases: Invalid Login

**Wrong password**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"wrongpassword"}'
```

**Expected Response** (401 Unauthorized):
```json
{
  "success": false,
  "data": null,
  "error": "Invalid credentials"
}
```

**Non-existent user**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"nonexistent@example.com","password":"password123"}'
```

**Expected Response** (401 Unauthorized):
```json
{
  "success": false,
  "data": null,
  "error": "Invalid credentials"
}
```

**What it tests**:
- ✅ Credential validation
- ✅ Error message consistency (don't reveal if user exists)
- ✅ Proper HTTP status codes

---

### POST /api/v1/auth/refresh

**Purpose**: Get new access token using refresh token

**Request**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}"
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "alice@example.com",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400
  },
  "error": null
}
```

**What it tests**:
- ✅ Token refresh mechanism
- ✅ JWT validation
- ✅ New token generation

---

### Error Cases: Invalid Refresh

**Invalid refresh token**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"invalid.token.here"}'
```

**Expected Response** (401 Unauthorized):
```json
{
  "success": false,
  "data": null,
  "error": "Invalid refresh token"
}
```

---

### POST /api/v1/auth/logout (Protected)

**Purpose**: Logout user (invalidate session)

**Request**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "data": "Logged out successfully",
  "error": null
}
```

**What it tests**:
- ✅ Protected route access
- ✅ JWT validation in Authorization header
- ✅ User ID extraction from token

---

### Error Cases: Logout

**Missing token**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/logout
```

**Expected Response** (401 Unauthorized):
- No JSON response, just HTTP 401

**Invalid token**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer invalid.token.here"
```

**Expected Response** (401 Unauthorized):
- No JSON response, authentication fails before reaching handler

**What it tests**:
- ✅ Authentication middleware
- ✅ Token validation
- ✅ Proper authorization flow

---

## Testing Workflow

### Complete Authentication Flow

```bash
# 1. Register new user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"bob@example.com","password":"password123"}' \
  | jq -r '.data.accessToken' > token.txt

# 2. Save token
export TOKEN=$(cat token.txt)

# 3. Use protected endpoint
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer $TOKEN"

# 4. Try to use token again (should fail)
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer $TOKEN"
```

---

## Automated Testing Script

Save as `test-api.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"

echo "🧪 Testing WebSLT Backend API"
echo "================================"

# Test 1: Health Check
echo "✅ Testing health endpoint..."
curl -s $BASE_URL/api/v1/health | jq

# Test 2: Register
echo "✅ Registering new user..."
RESPONSE=$(curl -s -X POST $BASE_URL/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@example.com","password":"testpass123"}')

echo $RESPONSE | jq
TOKEN=$(echo $RESPONSE | jq -r '.data.accessToken')

# Test 3: Login
echo "✅ Logging in..."
curl -s -X POST $BASE_URL/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@example.com","password":"testpass123"}' | jq

# Test 4: Protected endpoint
echo "✅ Accessing protected endpoint..."
curl -s -X POST $BASE_URL/api/v1/auth/logout \
  -H "Authorization: Bearer $TOKEN" | jq

echo "================================"
echo "✅ All tests completed!"
```

Run with:
```bash
chmod +x test-api.sh
./test-api.sh
```

---

## Performance Testing

### Load Test Registration

```bash
# Create 10 users quickly
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/v1/auth/register \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"user$i@example.com\",\"password\":\"password123\"}" &
done
wait
```

### Measure Response Time

```bash
curl -w "@-" -o /dev/null -s \
  -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"securepass123"}' <<'EOF'
    time_total:  %{time_total}s\n
EOF
```

Expected: < 1 second for authentication

---

## Database Verification

### Check Users Table

```bash
docker exec -it webslt-postgres psql -U postgres -d webslt \
  -c "SELECT id, email, created_at FROM users;"
```

### Check Migrations

```bash
docker exec -it webslt-postgres psql -U postgres -d webslt \
  -c "SELECT * FROM flyway_schema_history;"
```

---

## Test Data Cleanup

### Remove Test Users

```bash
docker exec -it webslt-postgres psql -U postgres -d webslt \
  -c "DELETE FROM users WHERE email LIKE '%example.com';"
```

### Reset Database

```bash
# Stop server first
docker exec -it webslt-postgres psql -U postgres -d webslt \
  -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"

# Restart server (migrations will run automatically)
./gradlew server:run
```

---

## Integration Testing with Postman

**Import Collection** (create `WebSLT.postman_collection.json`):

```json
{
  "info": {
    "name": "WebSLT Backend",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "url": "{{baseUrl}}/api/v1/health"
      }
    },
    {
      "name": "Register",
      "request": {
        "method": "POST",
        "url": "{{baseUrl}}/api/v1/auth/register",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\"email\":\"{{testEmail}}\",\"password\":\"{{testPassword}}\"}"
        }
      }
    }
  ],
  "variable": [
    {"key": "baseUrl", "value": "http://localhost:8080"},
    {"key": "testEmail", "value": "test@example.com"},
    {"key": "testPassword", "value": "testpass123"}
  ]
}
```

---

## Test Checklist

Before deploying to production:

- [ ] Health endpoint returns 200
- [ ] Registration works with valid data
- [ ] Registration fails with invalid data (email, password)
- [ ] Registration fails with duplicate email
- [ ] Login works with correct credentials
- [ ] Login fails with wrong password
- [ ] Login fails with non-existent user
- [ ] Token refresh works
- [ ] Protected endpoints require authentication
- [ ] Protected endpoints work with valid token
- [ ] Protected endpoints fail with invalid token
- [ ] Database migrations run successfully
- [ ] Users are created in database
- [ ] Passwords are hashed (not plain text)

---

## Next Steps

- 📖 [Running Guide](./Running-Guide.md) - How to start the server
- 🐛 [Troubleshooting Guide](./Troubleshooting.md) - Common issues
- 🏗️ [Architecture](./Architecture.md) - System design
- ✅ [Agent Verification Guide](./Agent-Verification-Guide.md) - For AI agents

---

## Tips

- Use `| jq` for pretty JSON output
- Save tokens in environment variables
- Create test users with unique emails
- Clean up test data regularly
- Monitor server logs during testing
- Test error cases, not just happy path
