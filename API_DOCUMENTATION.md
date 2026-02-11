# Procurement System API Documentation

## Authentication APIs

### POST /api/auth/register
**Request:**
```json
{
  "name": "John Doe",
  "email": "john.doe@company.com",
  "password": "SecurePass123",
  "roles": ["PROCUREMENT"]
}
```

**Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@company.com"
}
```

### POST /api/auth/login
**Request:**
```json
{
  "username": "john.doe@company.com",
  "password": "SecurePass123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## Vendor Search API

### GET /api/vendors/search
**Query Parameters:**
- `rating` (optional): Minimum rating (e.g., 4.0)
- `location` (optional): Location filter (e.g., "New York")
- `category` (optional): Category filter (e.g., "Electronics")
- `compliance` (optional): Compliance status (true/false)
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `sort` (optional): Sort criteria (e.g., "rating,desc")

**Example Request:**
```
GET /api/vendors/search?rating=4.0&location=New York&page=0&size=10&sort=rating,desc
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Tech Solutions Inc",
      "email": "contact@techsolutions.com",
      "phone": "+1-555-0123",
      "address": "123 Tech Street, New York, NY",
      "rating": 4.5,
      "location": "New York",
      "category": "Electronics",
      "compliance": true,
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "orders": [
        {
          "property": "rating",
          "direction": "DESC"
        }
      ]
    }
  },
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

## Workflow Management APIs

### POST /api/workflow/requisitions/{id}/submit
**Response:**
```json
{
  "message": "Requisition submitted successfully"
}
```

### POST /api/workflow/requisitions/{id}/approve
**Request:**
```json
{
  "comment": "Approved for procurement. Budget allocated."
}
```

**Response:**
```json
{
  "message": "Requisition approved successfully"
}
```

### POST /api/workflow/requisitions/{id}/reject
**Request:**
```json
{
  "comment": "Rejected due to insufficient budget allocation."
}
```

**Response:**
```json
{
  "message": "Requisition rejected successfully"
}
```

### GET /api/workflow/requisitions/{id}/history
**Response:**
```json
[
  {
    "id": 1,
    "requisitionId": 123,
    "action": "APPROVED",
    "comment": "Approved for procurement. Budget allocated.",
    "actionBy": "manager@company.com",
    "actionAt": "2024-01-15T14:30:00"
  },
  {
    "id": 2,
    "requisitionId": 123,
    "action": "SUBMITTED",
    "comment": "Initial submission",
    "actionBy": "user@company.com",
    "actionAt": "2024-01-15T10:00:00"
  }
]
```

## Purchase Order Management APIs

### POST /api/purchase-orders
**Request:**
```json
{
  "title": "Office Supplies Purchase Order",
  "subtotal": 1000.00,
  "tax": 80.00,
  "discount": 50.00,
  "totalAmount": 1030.00
}
```

**Response:**
```json
{
  "id": 1,
  "title": "Office Supplies Purchase Order",
  "subtotal": 1000.00,
  "tax": 80.00,
  "discount": 50.00,
  "totalAmount": 1030.00,
  "status": "OPEN",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00",
  "items": []
}
```

### POST /api/purchase-orders/{id}/items
**Request:**
```json
{
  "itemName": "Laptop Dell XPS 13",
  "quantity": 5,
  "unitPrice": 1200.00,
  "tax": 60.00,
  "discount": 100.00
}
```

**Response:**
```json
{
  "id": 1,
  "itemName": "Laptop Dell XPS 13",
  "quantity": 5,
  "unitPrice": 1200.00,
  "tax": 60.00,
  "discount": 100.00,
  "itemSubtotal": 6000.00,
  "itemTotal": 5960.00,
  "status": "PENDING",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

### PUT /api/purchase-orders/items/{itemId}/status
**Query Parameters:**
- `status`: ItemStatus (PENDING, PARTIALLY_DELIVERED, DELIVERED)

**Example Request:**
```
PUT /api/purchase-orders/items/1/status?status=DELIVERED
```

**Response:**
```json
{
  "id": 1,
  "itemName": "Laptop Dell XPS 13",
  "quantity": 5,
  "unitPrice": 1200.00,
  "tax": 60.00,
  "discount": 100.00,
  "itemSubtotal": 6000.00,
  "itemTotal": 5960.00,
  "status": "DELIVERED",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T15:45:00"
}
```

## Reporting APIs

### GET /api/reports/vendor
**Query Parameters:**
- `format` (optional): "pdf" or "excel" (default: "pdf")
- `minRating` (optional): Minimum vendor rating
- `location` (optional): Vendor location filter
- `category` (optional): Vendor category filter

**Example Request:**
```
GET /api/reports/vendor?format=excel&minRating=4.0&location=New York
```

**Response:** Binary file download (Excel or PDF)

### GET /api/reports/pr
**Query Parameters:**
- `format` (optional): "pdf" or "excel" (default: "pdf")
- `startDate` (optional): Start date filter (ISO format: 2024-01-01)
- `endDate` (optional): End date filter (ISO format: 2024-12-31)
- `status` (optional): RequisitionStatus (DRAFT, SUBMITTED, APPROVED, REJECTED)

**Example Request:**
```
GET /api/reports/pr?format=pdf&startDate=2024-01-01&endDate=2024-12-31&status=APPROVED
```

**Response:** Binary file download (Excel or PDF)

### GET /api/reports/po
**Query Parameters:**
- `format` (optional): "pdf" or "excel" (default: "pdf")
- `startDate` (optional): Start date filter
- `endDate` (optional): End date filter
- `vendorName` (optional): Vendor name filter

**Example Request:**
```
GET /api/reports/po?format=excel&startDate=2024-01-01&endDate=2024-12-31
```

**Response:** Binary file download (Excel or PDF)

## Error Response Format

All API errors follow this standard format:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "email": "Invalid email format",
    "password": "Password must be at least 8 characters"
  }
}
```

## Security Headers

All authenticated requests must include:
```
Authorization: Bearer <JWT_TOKEN>
```

## Role-Based Access Control

- **ADMIN**: Full access to all APIs
- **PROCUREMENT**: Access to vendors, purchase orders, workflow management
- **FINANCE**: Access to reports, purchase orders (read-only), workflow approval
- **VENDOR**: Limited access to own vendor profile and basic operations