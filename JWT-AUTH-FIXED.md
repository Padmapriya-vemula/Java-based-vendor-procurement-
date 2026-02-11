# JWT Authentication & Requisition API - FIXED

## 🔧 **Root Causes Fixed:**

1. **SecurityConfig** - Added JWT filter and allowed requisition endpoints
2. **JWT Filter** - Now extracts roles from token and creates proper authorities
3. **Role Mapping** - JWT roles now properly mapped to Spring Security authorities
4. **DTO Pattern** - Server-controlled fields (status, prNumber, createdBy) excluded from request

---

## 🚀 **Correct API Usage**

### 1. **Login & Get JWT Token**
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "demo2",
  "password": "your_password"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "demo2",
  "roles": "ROLE_ADMIN"
}
```

### 2. **Create Requisition (FIXED)**
```bash
POST /api/purchase-requisitions
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "requisitionNumber": "REQ-2024-001",
  "vendorId": 1,
  "totalAmount": 50000.00,
  "quantity": 10,
  "description": "Office supplies procurement",
  "requisitionDate": "2024-01-15"
}
```

**✅ What's Excluded (Server-Controlled):**
- `status` - Auto-set to DRAFT
- `prNumber` - Auto-generated (PR1705312200000)
- `requesterId` - Set from JWT username
- `createdAt/updatedAt` - Auto-set

---

## 📋 **Swagger UI Testing**

### Step 1: Authorize in Swagger
1. Click **"Authorize"** button
2. Enter: `Bearer eyJhbGciOiJIUzI1NiJ9...`
3. Click **"Authorize"**

### Step 2: Test POST /api/purchase-requisitions
```json
{
  "requisitionNumber": "REQ-2024-001",
  "vendorId": 1,
  "totalAmount": 50000.00,
  "quantity": 10,
  "description": "Office supplies procurement",
  "requisitionDate": "2024-01-15"
}
```

---

## 🔍 **Troubleshooting Guide**

### Still Getting 403?

#### Check 1: JWT Token Format
```bash
# ❌ Wrong
Authorization: eyJhbGciOiJIUzI1NiJ9...

# ✅ Correct
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

#### Check 2: JWT Payload
Decode your JWT at [jwt.io](https://jwt.io) - should contain:
```json
{
  "sub": "demo2",
  "roles": "ROLE_ADMIN",
  "iat": 1705312200,
  "exp": 1705398600
}
```

#### Check 3: Role Format
- JWT should contain: `"roles": "ROLE_ADMIN"`
- NOT: `"roles": "ADMIN"` or `"roles": ["ROLE_ADMIN"]`

#### Check 4: Token Expiry
```bash
# Check if token is expired
echo "eyJhbGciOiJIUzI1NiJ9..." | base64 -d
```

---

## 🛡️ **Security Configuration Summary**

### Endpoints & Required Roles:
```java
"/api/auth/**"                    // Public
"/api/vendors/search"             // Public  
"/api/purchase-requisitions/**"   // ROLE_ADMIN
"/api/requisitions/**"            // ROLE_ADMIN
"/api/approvals/**"               // ROLE_ADMIN, ROLE_MANAGER
```

### JWT → Authority Mapping:
```
JWT: "roles": "ROLE_ADMIN"
→ Spring Security: SimpleGrantedAuthority("ROLE_ADMIN")
→ hasRole("ADMIN") ✅
```

---

## 📝 **Complete Working Example**

### 1. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "demo2",
    "password": "your_password"
  }'
```

### 2. Create Requisition
```bash
curl -X POST http://localhost:8080/api/purchase-requisitions \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "requisitionNumber": "REQ-2024-001",
    "vendorId": 1,
    "totalAmount": 50000.00,
    "quantity": 10,
    "description": "Office supplies procurement",
    "requisitionDate": "2024-01-15"
  }'
```

### 3. Expected Response
```json
{
  "id": 1,
  "prNumber": "PR1705312200000",
  "requisitionNumber": "REQ-2024-001",
  "requesterId": 1,
  "vendorId": 1,
  "status": "DRAFT",
  "totalAmount": 50000.00,
  "quantity": 10,
  "description": "Office supplies procurement",
  "requisitionDate": "2024-01-15",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

---

## ⚡ **Key Fixes Applied**

1. **SecurityConfig**: Added JWT filter and proper endpoint permissions
2. **JwtAuthFilter**: Extracts roles from JWT and creates authorities
3. **JwtService**: Added `extractRoles()` method
4. **Controller**: Uses DTO pattern and passes authenticated username
5. **Service**: Handles user lookup and server-controlled field management
6. **DTO**: Excludes server-controlled fields (status, prNumber, requesterId)

**Result**: JWT authentication now works correctly with role-based authorization! 🎉