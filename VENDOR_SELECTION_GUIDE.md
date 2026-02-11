# Vendor Selection Implementation Guide

## Overview
Implemented proper vendor selection mechanism for PR & PO creation following real-world procurement practices.

## Business Rules Implemented

### Purchase Requisition (PR)
- ✅ **No vendor selection required** during PR creation
- ✅ PR contains only requirement details (description, quantity, amount)
- ✅ Vendor is decided **after PR approval** (not during creation)
- ✅ `vendorId` field is now optional in PR model

### Purchase Order (PO)
- ✅ **Must select an ACTIVE vendor** during PO creation
- ✅ Vendor selection via **searchable dropdown** (not manual input)
- ✅ Backend validates vendor exists and is active
- ✅ Rejects PO creation if vendor is inactive

## Backend Changes

### New API Endpoint

**GET /api/vendors/active**

Returns list of active vendors for selection.

**Response:**
```json
[
  {
    "vendorId": 1,
    "vendorName": "ABC Technologies",
    "rating": 4.5,
    "category": "IT Services",
    "location": "Mumbai"
  },
  {
    "vendorId": 5,
    "vendorName": "Office Supplies Co",
    "rating": 4.2,
    "category": "Office Supplies",
    "location": "Delhi"
  }
]
```

### Files Modified

1. **VendorController.java**
   - Added `getActiveVendors()` endpoint
   - Filters vendors by compliance status
   - Sorts by rating (highest first)

2. **PurchaseOrderController.java**
   - Added vendor validation in `createPurchaseOrder()`
   - Validates vendor exists
   - Validates vendor is active (compliance = true)
   - Throws exception if vendor is inactive

3. **RequisitionController.java**
   - Removed `vendorId` parameter from `createRequisition()`
   - Sets `vendorId` to null during PR creation

4. **PurchaseRequisition.java** (Model)
   - Made `vendorId` field optional (removed @NotNull)
   - Changed column to nullable

### Files Created

1. **ActiveVendorDto.java**
   - DTO for active vendor response
   - Contains: vendorId, vendorName, rating, category, location

## Frontend Changes

### Files Modified

1. **api.js**
   - Added `getActive()` method to vendorAPI

2. **CreateRequisition.js**
   - Removed vendor selection field
   - PR creation now only requires: requisitionNumber, description, quantity, totalAmount

3. **PurchaseOrders.js**
   - Added vendor dropdown with Autocomplete component
   - Fetches active vendors on component mount
   - Displays vendor name, rating, category, location
   - Disables manual vendor ID input
   - Shows error if no active vendors available
   - Validates vendor selection before PO creation

## Validation Flow

### PO Creation Validation

1. **Frontend Validation:**
   - Checks if vendor is selected
   - Disables "Create" button if no vendor selected
   - Shows error message if no active vendors exist

2. **Backend Validation:**
   ```java
   // Validate vendor exists
   Vendor vendor = vendorRepository.findById(vendorId)
       .orElseThrow(() -> new RuntimeException("Vendor not found"));
   
   // Validate vendor is active
   if (vendor.getCompliance() == null || !vendor.getCompliance()) {
       throw new RuntimeException("Cannot create PO for inactive vendor");
   }
   ```

## Sample API Requests

### Create PR (No Vendor Required)
```bash
POST /api/requisitions
Content-Type: application/x-www-form-urlencoded

requisitionNumber=REQ001
description=Office supplies needed
quantity=10
totalAmount=5000
```

### Get Active Vendors
```bash
GET /api/vendors/active
Authorization: Bearer <token>
```

### Create PO (With Vendor Validation)
```bash
POST /api/purchase-orders
Content-Type: application/x-www-form-urlencoded

title=Office Supplies Order
vendorId=5
```

**Success Response:**
```json
{
  "id": 1,
  "title": "Office Supplies Order",
  "vendorId": 5,
  "status": "OPEN",
  "totalAmount": 0.00
}
```

**Error Response (Inactive Vendor):**
```json
{
  "timestamp": "2026-01-18T19:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Cannot create PO for inactive vendor: XYZ Corp"
}
```

## UI/UX Features

### PR Creation Form
- Clean, simple form
- No vendor selection
- Fields: Requisition Number, Description, Quantity, Total Amount

### PO Creation Form
- Searchable vendor dropdown (Autocomplete)
- Shows vendor details: Name, Rating, Category, Location
- Disabled if no active vendors
- Helper text guides user
- Create button disabled until vendor selected

### Vendor Dropdown Features
- **Searchable**: Type to filter vendors
- **Rich display**: Shows vendor name, rating, category, location
- **Sorted**: By rating (highest first)
- **Validation**: Only active vendors shown
- **User-friendly**: No manual ID entry required

## Security & Best Practices

✅ **Backend re-validates** vendor status (doesn't trust frontend)
✅ **No manual vendor ID input** (prevents invalid entries)
✅ **Active vendors only** (compliance-based filtering)
✅ **Clear error messages** for users
✅ **Proper exception handling** in backend
✅ **Follows real-world procurement flow**

## Testing

### Test Scenarios

1. **PR Creation without Vendor** ✅
   - Create PR without selecting vendor
   - Should succeed

2. **PO Creation with Active Vendor** ✅
   - Select active vendor from dropdown
   - Should create PO successfully

3. **PO Creation with Inactive Vendor** ✅
   - Try to create PO with inactive vendor ID (via API)
   - Should fail with error message

4. **No Active Vendors** ✅
   - When no active vendors exist
   - Dropdown should be disabled
   - Should show helper message

## Migration Notes

If you have existing PRs with vendorId, they will remain unchanged. New PRs will have vendorId = null until vendor is assigned during approval process.

## Future Enhancements

- Add vendor assignment during PR approval workflow
- Add vendor performance metrics in dropdown
- Add vendor availability status
- Add multi-vendor comparison feature
- Add vendor recommendation based on PR requirements
