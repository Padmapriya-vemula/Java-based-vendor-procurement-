# API Testing Script

## 1. Test Registration

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Admin",
    "email": "testadmin@test.com",
    "password": "Admin@123",
    "roles": ["ADMIN"]
  }'
```

Expected: 201 Created with user details

---

## 2. Test Login

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testadmin@test.com",
    "password": "Admin@123"
  }'
```

Expected: 200 OK with token and user object (including role)

---

## 3. Test Dashboard (with token)

```bash
curl -X GET http://localhost:8081/api/dashboard/overview \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

Expected: Dashboard statistics

---

## 4. Test Create Vendor

```bash
curl -X POST http://localhost:8081/api/vendors \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tech Solutions Inc",
    "email": "tech@solutions.com",
    "phone": "9876543210",
    "category": "IT Services",
    "location": "Bangalore",
    "rating": 4.5,
    "compliance": true
  }'
```

Expected: 201 Created with vendor details

---

## 5. Test Get Active Vendors

```bash
curl -X GET http://localhost:8081/api/vendors/active \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

Expected: List of active vendors only

---

## 6. Test Create PR

```bash
curl -X POST http://localhost:8081/api/requisitions \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "requisitionNumber=PR-2024-001&description=Office Supplies&quantity=50&totalAmount=25000"
```

Expected: PR created without vendor

---

## 7. Test PR History

```bash
curl -X GET http://localhost:8081/api/requisitions/1/history \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

Expected: Array of history entries

---

## 8. Test Create PO

```bash
curl -X POST "http://localhost:8081/api/purchase-orders?title=Office Equipment&vendorId=1" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

Expected: PO created with active vendor

---

## 9. Test PO History

```bash
curl -X GET http://localhost:8081/api/purchase-orders/1/history \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

Expected: Array of history entries

---

## 10. Test Generate Report

```bash
curl -X GET "http://localhost:8081/api/reports/vendor?format=PDF" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  --output vendor-report.pdf
```

Expected: PDF file downloaded

---

## Role-Based Access Testing

### Test ADMIN Access (Full Access)
```bash
# Login as ADMIN
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@test.com","password":"Admin@123"}' \
  | jq -r '.token')

# Test all endpoints with ADMIN token
curl -X GET http://localhost:8081/api/vendors \
  -H "Authorization: Bearer $TOKEN"
```

### Test VENDOR Access (Limited)
```bash
# Login as VENDOR
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"vendor@test.com","password":"Vendor@123"}' \
  | jq -r '.token')

# Try to create vendor (should fail)
curl -X POST http://localhost:8081/api/vendors \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@test.com"}'
```

Expected: 403 Forbidden

---

## Database Verification

```sql
-- Check if history tables exist
SHOW TABLES LIKE '%history%';

-- Check PR history
SELECT * FROM pr_history ORDER BY created_at DESC LIMIT 5;

-- Check PO history
SELECT * FROM po_history ORDER BY created_at DESC LIMIT 5;

-- Check users and roles
SELECT u.id, u.name, u.email, r.name as role 
FROM users u 
JOIN user_roles ur ON u.id = ur.user_id 
JOIN roles r ON ur.role_id = r.id;

-- Check active vendors
SELECT vendor_id, name, compliance FROM vendors WHERE compliance = true;
```

---

## Frontend Testing Checklist

### Manual Browser Tests:

1. **Registration Flow**
   - [ ] Open http://localhost:3000/login
   - [ ] Click "Register here"
   - [ ] Fill form with valid data
   - [ ] Password strength indicator works
   - [ ] Phone validation (10 digits)
   - [ ] Success message appears
   - [ ] Switches to login view

2. **Login Flow**
   - [ ] Enter credentials
   - [ ] Token saved in localStorage
   - [ ] User role saved
   - [ ] Redirects to dashboard

3. **Dashboard**
   - [ ] KPI cards show numbers
   - [ ] All 4 charts render
   - [ ] Quick access cards work

4. **Vendor Management**
   - [ ] Create vendor with phone validation
   - [ ] Active/Inactive toggle works
   - [ ] Search filters work
   - [ ] Vendor cards display correctly

5. **Purchase Requisitions**
   - [ ] Create PR without vendor
   - [ ] View history
   - [ ] Approve/Reject workflow
   - [ ] Delete PR

6. **Purchase Orders**
   - [ ] Create PO with active vendor dropdown
   - [ ] Add items
   - [ ] Totals calculate correctly
   - [ ] Update item status
   - [ ] PO auto-closes when all delivered
   - [ ] View history

7. **Reports**
   - [ ] Generate Vendor PDF
   - [ ] Generate PR Excel
   - [ ] Generate PO PDF
   - [ ] Files download correctly

8. **Token Expiry**
   - [ ] Wait 1 hour or delete token
   - [ ] Try to access page
   - [ ] Redirects to login

---

## Performance Benchmarks

- Page load: < 2 seconds
- API response: < 500ms
- Chart rendering: < 1 second
- Report generation: < 3 seconds

---

## Browser Compatibility

Test on:
- [ ] Chrome (latest)
- [ ] Firefox (latest)
- [ ] Edge (latest)
- [ ] Safari (latest)

---

## Mobile Responsiveness

Test on:
- [ ] iPhone (375px width)
- [ ] iPad (768px width)
- [ ] Desktop (1920px width)

---

## Security Checklist

- [ ] Passwords hashed in database
- [ ] JWT tokens expire after 1 hour
- [ ] 401 errors handled
- [ ] SQL injection prevented (JPA)
- [ ] XSS prevented (React escaping)
- [ ] CORS configured correctly
- [ ] Sensitive data not in console logs

---

## Common Issues & Solutions

### Issue: Port 8080 already in use
**Solution**: Changed to 8081 in application.properties

### Issue: MySQL connection failed
**Solution**: Added allowPublicKeyRetrieval=true and dialect

### Issue: Registration fails
**Solution**: Fixed request payload (removed confirmPassword)

### Issue: History not showing
**Solution**: Created PRHistory/POHistory tables and endpoints

### Issue: Token not included in requests
**Solution**: Axios interceptor adds token automatically

### Issue: Charts not rendering
**Solution**: Recharts library installed and configured

---

## Test Data Setup

Run this SQL to create test data:

```sql
-- Insert test users
INSERT INTO users (name, email, password) VALUES
('Admin User', 'admin@test.com', '$2a$10$...'), -- Use bcrypt hash
('Vendor User', 'vendor@test.com', '$2a$10$...'),
('Procurement User', 'procurement@test.com', '$2a$10$...');

-- Insert test vendors
INSERT INTO vendors (name, email, phone, category, location, rating, compliance) VALUES
('Tech Solutions', 'tech@solutions.com', '9876543210', 'IT', 'Bangalore', 4.5, true),
('Office Supplies Co', 'office@supplies.com', '9876543211', 'Supplies', 'Mumbai', 4.0, true),
('Inactive Vendor', 'inactive@vendor.com', '9876543212', 'Other', 'Delhi', 3.0, false);

-- Insert test PRs
INSERT INTO purchase_requisitions (pr_number, requisition_number, requester_id, total_amount, quantity, description, requisition_date, status) VALUES
('PR1234567890', 'PR-2024-001', 1, 25000, 50, 'Office Supplies', CURDATE(), 'DRAFT'),
('PR1234567891', 'PR-2024-002', 1, 50000, 100, 'IT Equipment', CURDATE(), 'APPROVED');

-- Insert test POs
INSERT INTO purchase_order (title, vendor_id, subtotal, tax, discount, total_amount, status) VALUES
('Office Equipment', 1, 250000, 45000, 5000, 290000, 'OPEN'),
('IT Hardware', 2, 100000, 18000, 0, 118000, 'CLOSED');
```

---

## Final Sign-Off Checklist

- [ ] All test scenarios pass
- [ ] No console errors
- [ ] No database errors
- [ ] All reports generate
- [ ] History tracking works
- [ ] Role-based access enforced
- [ ] Token expiry works
- [ ] UI is responsive
- [ ] Performance acceptable
- [ ] Documentation complete
