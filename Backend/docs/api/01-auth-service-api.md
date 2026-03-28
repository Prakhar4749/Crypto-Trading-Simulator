# Auth Service API Documentation

**Base URL**: `http://localhost:8081`  
**Standard Response Format**:  
All responses are wrapped in a standard structure:
```json
{
  "success": true/false,
  "message": "Human readable message",
  "data": {},
  "error": {
    "code": "ERROR_CODE",
    "status": 404,
    "path": "/api/path",
    "timestamp": "18-03-2026 11:30:00 PM IST",
    "details": ["field: message"]
  }
}
```

---

### 1. POST /auth/signup
**Service**: auth-service  
**Description**: Registers a new user and returns a JWT token.  
**Auth Required**: No  

#### Request
**Headers**:
| Header | Value | Required |
|--------|-------|----------|
| Content-Type | application/json | Yes |

**Body**:
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "mobile": "9876543210"
}
```

#### Response
**Success (200)**:
```json
{
  "success": true,
  "message": "Signup Success",
  "data": {
    "jwt": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

**Error Responses**:
| Status | Error Code | Scenario | Message |
|--------|------------|----------|---------|
| 400 | VALIDATION_ERROR | Invalid email/password | "Validation failed. Please check your input." |
| 409 | DUPLICATE_RESOURCE | Email already exists | "User with email john@*** already exists" |
| 500 | INTERNAL_ERROR | Server error | "An unexpected error occurred." |

---

### 2. POST /auth/signin
**Service**: auth-service  
**Description**: Authenticates a user. If 2FA is enabled, returns a session ID.  
**Auth Required**: No  

#### Request
**Body**:
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

#### Response
**Success (200)**:
```json
{
  "success": true,
  "message": "Login Success",
  "data": { "jwt": "..." }
}
```

**Error Responses**:
| Status | Error Code | Scenario | Message |
|--------|------------|----------|---------|
| 401 | UNAUTHORIZED | Wrong password | "Invalid email or password" |
| 404 | RESOURCE_NOT_FOUND | Email not found | "User not found: john@example.com" |

---

### 3. POST /auth/two-factor/otp/{otp}
**Service**: auth-service  
**Description**: Verifies 2FA OTP for a given session.  
**Auth Required**: No  

#### Parameters
| Param | Type | In | Required | Description |
|-------|------|----|----------|-------------|
| otp | String | Path | Yes | 6-digit OTP |
| id | String | Query | Yes | Session ID from signin |

#### Response
**Success (200)**:
```json
{
  "success": true,
  "message": "Two factor authentication verified",
  "data": { "jwt": "..." }
}
```

**Error Responses**:
| Status | Error Code | Scenario | Message |
|--------|------------|----------|---------|
| 400 | INVALID_OTP | Wrong or Expired OTP | "OTP validation failed: Invalid or expired OTP" |
| 400 | INVALID_INPUT | Missing session ID | "Required parameter missing: id" |

---

### 9. GET /api/users/profile
**Service**: auth-service  
**Description**: Returns the profile of the authenticated user.  
**Auth Required**: Yes  

#### Request
**Headers**:
| Header | Value | Required |
|--------|-------|----------|
| Authorization | Bearer <jwt> | Yes |

#### Response
**Success (200)**:
```json
{
  "success": true,
  "message": "Profile fetched successfully",
  "data": { "email": "...", "fullName": "...", "role": "ROLE_USER" }
}
```

**Error Responses**:
| Status | Error Code | Scenario | Message |
|--------|------------|----------|---------|
| 401 | UNAUTHORIZED | Invalid token | "Unauthorized" |
| 404 | RESOURCE_NOT_FOUND | User deleted but token valid | "User not found: 1" |
