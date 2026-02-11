# End-to-End Testing Guide

## Prerequisites
1. MySQL running on localhost:3306
2. Database `spvms_db` created
3. Backend running on port 8081
4. Frontend running on port 3000

---

## Test Scenario 1: User Registration & Authentication

### Steps:
1. Open `http://localhost:3000/login`
2. Click "Register here"
3. Fill registration form:
   - Name: `Test Admin`
   - Email: `testadmin@test.com`
   - Password: `Admin@123` (must be 8+ chars with uppercase, lowercase, number, special char)
   - Role: `ADMIN`
4. Click "Create Account"

### Expected Results:
✅ Success message appears
✅ Form switches to login view
✅ Password strength indicator shows "Strong"

### Test Login:
1. Enter email: `testadmin@test.com`
2. Enter password: `Admin@123`
3. Click "Sign In"

### Expected Results:
✅ Redirects to Dashboard
✅ Token stored in localStorage (check DevTools)
✅ User name appears in header

---

## Test Scenario 2: Dashboard Analytics

### Steps:
1. Login as ADMIN
2. View Dashboard

### Expected Results:
✅ 4 KPI cards display: Total Vendors, Active PRs, Open POs, Total Spend
✅ PR Status Distribution (Pie Chart) shows data
✅ PO Status Distribution (Donut Chart) shows data
✅ Monthly Trend (Line Chart) shows last 6 months
✅ Vendor PO Values (Bar Chart) shows top vendors
✅ Quick Access cards are clickable

---

## Test Scenario 3: Vendor Management

### Test 3A: Create Active Vendor
1. Navigate to "Vendor Management"
2. Click "Add Vendor"
3. Fill form:
   - Name: `Tech Solutions Inc`
   - Email: `tech@solutions.com`
   - Phone: `9876543210` (exactly 10 digits)
   - Category: `IT Services`
   - Location: `Bangalore`
   - Rating: `4.5`
   - Compliance: `Active` (checked)
4. Click "Add Vendor"

### Expected Results:
✅ Vendor created successfully
✅ Appears in vendor list with green "Active" badge
✅ Phone validation rejects non-10-digit numbers

### Test 3B: Create Inactive Vendor
1. Create another vendor with Compliance: `Inactive` (unchecked)

### Expected Results:
✅ Vendor created with red "Inactive" badge
✅ Will NOT appear in PO vendor dropdown

### Test 3C: Search Vendors
1. Use search filters: Name, Category, Location, Compliance
2. Test each filter

### Expected Results:
✅ Results filter correctly
✅ Active/Inactive filter works

---

## Test Scenario 4: Purchase Requisition Workflow

### Test 4A: Create PR (No Vendor Required)
1. Navigate to "Purchase Requisitions"
2. Click "Create PR"
3. Fill form:
   - Requisition Number: `PR-2024-001`
   - Description: `Office Supplies`
   - Quantity: `50`
   - Total Amount: `25000`
   - Date: Select today's date
   - **DO NOT select vendor**
4. Click "Submit"

### Expected Results:
✅ PR created with status "DRAFT"
✅ PR Number auto-generated (PR123456789...)
✅ No vendor required at creation

### Test 4B: View PR History
1. Click "History" button on created PR

### Expected Results:
✅ Shows "PR Created" entry
✅ Status: DRAFT
✅ Timestamp displayed
✅ Remarks: "Purchase Requisition created"

### Test 4C: Approve PR
1. Click "Approve" button
2. Add comment: `Approved for procurement`
3. Click "Approve"

### Expected Results:
✅ Status changes to "APPROVED"
✅ History shows new entry: "PR Approved"
✅ Comment saved in history

### Test 4D: Reject PR
1. Create another PR
2. Click "Reject" button
3. Add comment: `Budget exceeded`
4. Click "Reject"

### Expected Results:
✅ Status changes to "REJECTED"
✅ History shows rejection with comment

### Test 4E: Delete PR
1. Click "Delete" button on any PR
2. Confirm deletion

### Expected Results:
✅ PR removed from list
✅ Confirmation dialog appears

---

## Test Scenario 5: Purchase Order Workflow

### Test 5A: Create PO (Active Vendor Required)
1. Navigate to "Purchase Orders"
2. Click "New Order"
3. Fill form:
   - Title: `Office Equipment Purchase`
   - Vendor: Select from dropdown (only active vendors shown)
4. Click "Create"

### Expected Results:
✅ Only active vendors appear in dropdown
✅ Inactive vendors NOT shown
✅ PO created with status "OPEN"
✅ Totals initialized to 0

### Test 5B: Add Items to PO
1. Click "Add Item" on created PO
2. Fill form:
   - Item Name: `Laptop`
   - Quantity: `5`
   - Unit Price: `50000`
   - Tax: `9000` (18% GST)
   - Discount: `5000`
3. Click "Add Item"

### Expected Results:
✅ Item added to PO
✅ Item Subtotal: 250000 (5 × 50000)
✅ Item Total: 254000 (250000 + 9000 - 5000)
✅ PO Subtotal updated: 250000
✅ PO Tax updated: 9000
✅ PO Discount updated: 5000
✅ PO Total updated: 254000

### Test 5C: Add Multiple Items
1. Add another item:
   - Item Name: `Mouse`
   - Quantity: `10`
   - Unit Price: `500`
   - Tax: `900`
   - Discount: `0`

### Expected Results:
✅ Both items shown in table
✅ PO totals recalculated correctly
✅ Subtotal: 255000
✅ Tax: 9900
✅ Total: 264900

### Test 5D: Update Item Delivery Status
1. Change first item status to "PARTIALLY_DELIVERED"
2. Change second item status to "DELIVERED"

### Expected Results:
✅ Status chips update with colors
✅ PO remains "OPEN" (not all items delivered)

### Test 5E: Complete All Deliveries
1. Change first item status to "DELIVERED"

### Expected Results:
✅ PO status auto-changes to "CLOSED"
✅ History logs: "All items delivered, PO closed"
✅ "Add Item" button disabled

### Test 5F: View PO History
1. Click "History" button

### Expected Results:
✅ Shows "PO Created" entry with vendor name
✅ Shows "Status Changed" entry when closed
✅ Remarks: "All items delivered, PO closed"

### Test 5G: Delete PO
1. Click "Delete" button
2. Confirm deletion

### Expected Results:
✅ PO and all items deleted
✅ Confirmation dialog appears

---

## Test Scenario 6: Reports Module

### Test 6A: Generate Vendor Report
1. Navigate to "Reports & Analytics"
2. Click "Vendor Report" card
3. Select format: PDF
4. Click "Generate Report"

### Expected Results:
✅ PDF downloads with vendor list
✅ Shows: Name, Email, Phone, Category, Location, Rating, Compliance

### Test 6B: Generate PR Report
1. Click "PR Report" card
2. Select format: Excel
3. Click "Generate Report"

### Expected Results:
✅ Excel file downloads
✅ Shows: PR Number, Description, Quantity, Amount, Status, Date

### Test 6C: Generate PO Report
1. Click "PO Report" card
2. Select format: PDF
3. Click "Generate Report"

### Expected Results:
✅ PDF downloads with PO details
✅ Shows: PO ID, Title, Vendor, Items, Totals, Status

---

## Test Scenario 7: Role-Based Access Control

### Test 7A: Create Users with Different Roles
1. Logout
2. Register 4 users:
   - `admin@test.com` - Role: ADMIN
   - `procurement@test.com` - Role: PROCUREMENT
   - `vendor@test.com` - Role: VENDOR
   - `finance@test.com` - Role: FINANCE

### Test 7B: Test ADMIN Access
1. Login as `admin@test.com`
2. Try accessing all modules

### Expected Results:
✅ Can access Dashboard
✅ Can access Vendor Management
✅ Can create/approve/reject PRs
✅ Can create/manage POs
✅ Can generate all reports
✅ Full access to all features

### Test 7C: Test PROCUREMENT Access
1. Login as `procurement@test.com`
2. Try accessing modules

### Expected Results:
✅ Can access Dashboard
✅ Can view Vendors
✅ Can create PRs
✅ Can create POs
✅ Can view reports
❌ Cannot delete vendors (if backend enforces)

### Test 7D: Test VENDOR Access
1. Login as `vendor@test.com`
2. Try accessing modules

### Expected Results:
✅ Can access Dashboard
✅ Can view own vendor profile
✅ Can view POs assigned to them
❌ Cannot create PRs
❌ Cannot access other vendors' data

### Test 7E: Test FINANCE Access
1. Login as `finance@test.com`
2. Try accessing modules

### Expected Results:
✅ Can access Dashboard
✅ Can view all reports
✅ Can approve high-value PRs
❌ Cannot create vendors
❌ Cannot create POs

---

## Test Scenario 8: Token Expiry & Session Management

### Test 8A: Token Expiry (1 Hour)
1. Login successfully
2. Wait 1 hour OR manually delete token from localStorage
3. Try to navigate to any page

### Expected Results:
✅ Automatically redirects to login page
✅ Shows "Session expired" or similar message
✅ 401 error handled gracefully

### Test 8B: Logout
1. Click logout button (if implemented)
2. Or manually clear localStorage

### Expected Results:
✅ Token removed
✅ Redirects to login
✅ Cannot access protected routes

---

## Test Scenario 9: Data Validation

### Test 9A: Vendor Phone Validation
1. Try creating vendor with phone: `123` (less than 10 digits)

### Expected Results:
❌ Error: "Phone number must be exactly 10 digits"
✅ Form submission blocked

### Test 9B: Password Strength
1. Try registering with password: `weak`

### Expected Results:
❌ Password strength shows "Weak"
✅ Submit button disabled
✅ Requires 8+ chars with mixed case, number, special char

### Test 9C: Email Validation
1. Try registering with email: `invalid-email`

### Expected Results:
❌ Error: "Invalid email format"
✅ Form submission blocked

### Test 9D: Required Fields
1. Try creating PR without filling required fields

### Expected Results:
❌ Error messages for empty fields
✅ Form submission blocked

---

## Test Scenario 10: UI/UX Validation

### Test 10A: Responsive Design
1. Resize browser window
2. Test on mobile viewport (DevTools)

### Expected Results:
✅ Layout adapts to screen size
✅ Cards stack vertically on mobile
✅ Navigation remains accessible

### Test 10B: Loading States
1. Observe loading indicators during API calls

### Expected Results:
✅ Circular progress shown during data fetch
✅ Buttons show loading state during submission
✅ No blank screens

### Test 10C: Error Handling
1. Stop backend server
2. Try any operation

### Expected Results:
✅ Error alerts displayed
✅ User-friendly error messages
✅ No app crashes

### Test 10D: Theme Consistency
1. Navigate through all pages

### Expected Results:
✅ Purple gradient theme consistent (#667eea to #764ba2)
✅ Light background (#f8f9fa)
✅ Rounded corners (borderRadius: 3)
✅ Smooth hover animations
✅ Professional typography

---

## Database Validation Queries

Run these in MySQL to verify data:

```sql
-- Check users
SELECT id, name, email, roles FROM users;

-- Check vendors
SELECT vendor_id, name, email, phone, compliance FROM vendors;

-- Check PRs
SELECT id, pr_number, status, total_amount FROM purchase_requisitions;

-- Check POs
SELECT id, title, vendor_id, status, total_amount FROM purchase_order;

-- Check PO Items
SELECT id, purchase_order_id, item_name, quantity, item_total, status FROM purchase_order_item;

-- Check PR History
SELECT pr_id, status, action, remarks, created_at FROM pr_history ORDER BY created_at DESC;

-- Check PO History
SELECT po_id, status, action, remarks, created_at FROM po_history ORDER BY created_at DESC;
```

---

## Performance Testing

### Test Load Time
1. Open DevTools > Network tab
2. Navigate to Dashboard

### Expected Results:
✅ Page loads in < 2 seconds
✅ API calls complete in < 500ms
✅ Charts render smoothly

---

## Bug Tracking Template

| Bug ID | Module | Description | Steps to Reproduce | Expected | Actual | Status |
|--------|--------|-------------|-------------------|----------|--------|--------|
| BUG-001 | | | | | | |

---

## Final Checklist

- [ ] All users can register and login
- [ ] Dashboard shows correct analytics
- [ ] Vendors can be created with phone validation
- [ ] Active/Inactive vendor filtering works
- [ ] PRs can be created without vendor
- [ ] PR approval/rejection workflow works
- [ ] PR history tracks all changes
- [ ] POs require active vendor selection
- [ ] PO items calculate totals correctly
- [ ] PO auto-closes when all items delivered
- [ ] PO history tracks all changes
- [ ] Reports generate PDF and Excel
- [ ] Token expires after 1 hour
- [ ] 401 errors redirect to login
- [ ] Role-based access enforced
- [ ] All forms validate input
- [ ] UI theme is consistent
- [ ] No console errors
- [ ] Database tables populated correctly

---

## Known Issues & Limitations

1. **Frontend Role-Based UI**: Currently all UI elements visible to all roles. Backend enforces access control.
2. **Refresh Token**: Not implemented. User must re-login after 1 hour.
3. **Email Notifications**: Not implemented for PR approvals.
4. **Audit Trail**: Limited to PR/PO history. No user action logs.

---

## Next Steps for Production

1. Implement frontend role-based UI hiding
2. Add refresh token mechanism
3. Add email notifications
4. Add comprehensive audit logging
5. Add unit and integration tests
6. Add API documentation (Swagger)
7. Add deployment scripts
8. Add monitoring and logging
