# Approval Workflow API Documentation

## Workflow States
- **DRAFT** → **SUBMITTED** → **APPROVED** / **REJECTED**

## API Endpoints

### 1. Submit Requisition for Approval
```
POST /api/approvals/requisitions/{id}/submit
```
**Authorization**: Required (Requester only)
**Description**: Submit a draft requisition for approval

**Response**:
```json
"Requisition submitted successfully"
```

### 2. Approve Requisition
```
POST /api/approvals/requisitions/{id}/approve
```
**Authorization**: Required (MANAGER/ADMIN role only)
**Body**:
```json
{
  "comment": "Approved for procurement"
}
```

**Response**:
```json
"Requisition approved successfully"
```

### 3. Reject Requisition
```
POST /api/approvals/requisitions/{id}/reject
```
**Authorization**: Required (MANAGER/ADMIN role only)
**Body**:
```json
{
  "comment": "Budget constraints - please revise"
}
```

**Response**:
```json
"Requisition rejected successfully"
```

### 4. Get Approval History
```
GET /api/approvals/requisitions/{id}/history
```
**Authorization**: Required
**Response**:
```json
[
  {
    "id": 1,
    "requisitionId": 123,
    "action": "APPROVED",
    "comment": "Approved for procurement",
    "actionBy": "manager@company.com",
    "actionAt": "2024-01-15T10:30:00"
  }
]
```

### 5. Get Pending Approvals
```
GET /api/approvals/requisitions/pending
```
**Authorization**: Required (MANAGER/ADMIN role)
**Response**:
```json
[
  {
    "id": 123,
    "prNumber": "PR1705312200000",
    "requisitionNumber": "REQ-2024-001",
    "requesterId": 1,
    "status": "SUBMITTED",
    "totalAmount": 50000.00,
    "description": "Office supplies procurement",
    "requisitionDate": "2024-01-15",
    "createdAt": "2024-01-15T09:00:00"
  }
]
```

## Security Features

### Authorization Rules
1. **Submit**: Only the requester can submit their own requisitions
2. **Approve/Reject**: Only users with MANAGER or ADMIN roles
3. **Self-Approval Prevention**: Users cannot approve their own requisitions
4. **Status Validation**: Only SUBMITTED requisitions can be approved/rejected

### Automatic PR ID Generation
- Format: `PR{timestamp}`
- Example: `PR1705312200000`
- Generated automatically on requisition creation

## Workflow Rules

### State Transitions
- **DRAFT** → **SUBMITTED**: Only by requester
- **SUBMITTED** → **APPROVED**: Only by authorized approvers
- **SUBMITTED** → **REJECTED**: Only by authorized approvers
- **REJECTED** → **DRAFT**: Manual status reset (if needed)

### Validation Rules
1. Only draft requisitions can be submitted
2. Only submitted requisitions can be approved/rejected
3. Comments are optional but recommended
4. All actions are logged with timestamp and user

## Error Responses

### Common Errors
```json
{
  "error": "Only submitted requisitions can be approved"
}
```

```json
{
  "error": "You cannot approve your own requisition"
}
```

```json
{
  "error": "You do not have permission to approve requisitions"
}
```

## Usage Examples

### Complete Workflow Example
```bash
# 1. Create requisition (auto-generates PR ID)
POST /api/purchase-requisitions
{
  "requisitionNumber": "REQ-2024-001",
  "requesterId": 1,
  "totalAmount": 50000.00,
  "quantity": 10,
  "description": "Office supplies"
}

# 2. Submit for approval
POST /api/approvals/requisitions/123/submit

# 3. Manager approves
POST /api/approvals/requisitions/123/approve
{
  "comment": "Approved within budget"
}

# 4. Check approval history
GET /api/approvals/requisitions/123/history
```

## Database Schema

### Purchase Requisitions
- `pr_number`: Auto-generated unique identifier
- `status`: ENUM (DRAFT, SUBMITTED, APPROVED, REJECTED)
- Default status: DRAFT

### Approval History
- `requisition_id`: Foreign key to purchase_requisitions
- `action`: APPROVED or REJECTED
- `comment`: Approver's comment
- `action_by`: Username of approver
- `action_at`: Timestamp of action