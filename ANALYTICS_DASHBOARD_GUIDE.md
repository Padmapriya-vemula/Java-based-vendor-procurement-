# Analytics Dashboard Implementation Guide

## Overview
This implementation adds a comprehensive analytics dashboard with interactive charts to the Vendor Management System.

## Backend Changes

### New Files Created:
1. **DTOs** (Data Transfer Objects):
   - `DashboardOverviewDto.java` - Overview statistics
   - `PRStatusDto.java` - PR status distribution
   - `POStatusDto.java` - PO status distribution
   - `MonthlyTrendDto.java` - Monthly trend data
   - `VendorPOValueDto.java` - Vendor-wise PO values

2. **Service**:
   - `DashboardService.java` - Business logic for analytics

3. **Controller**:
   - `DashboardController.java` - REST API endpoints

### Modified Files:
- `VendorRepository.java` - Added `countByIsActive()` method

### API Endpoints:

#### 1. GET /api/dashboard/overview
Returns overall statistics.

**Sample Response:**
```json
{
  "totalVendors": 25,
  "activeVendors": 20,
  "inactiveVendors": 5,
  "totalPRs": 150,
  "totalPOs": 120,
  "totalProcurementAmount": 5500000.00
}
```

#### 2. GET /api/dashboard/pr-status
Returns PR count by status.

**Sample Response:**
```json
[
  { "status": "DRAFT", "count": 10 },
  { "status": "SUBMITTED", "count": 45 },
  { "status": "APPROVED", "count": 80 },
  { "status": "REJECTED", "count": 15 }
]
```

#### 3. GET /api/dashboard/po-status
Returns PO count by status.

**Sample Response:**
```json
[
  { "status": "OPEN", "count": 50 },
  { "status": "CLOSED", "count": 70 }
]
```

#### 4. GET /api/dashboard/monthly-trend
Returns monthly PR/PO counts and spending.

**Sample Response:**
```json
[
  {
    "month": "Jan 2026",
    "prCount": 25,
    "poCount": 20,
    "spending": 450000.00
  },
  {
    "month": "Feb 2026",
    "prCount": 30,
    "poCount": 25,
    "spending": 520000.00
  }
]
```

#### 5. GET /api/dashboard/vendor-po-values
Returns top 10 vendors by PO value.

**Sample Response:**
```json
[
  {
    "vendorId": 1,
    "vendorName": "Tech Solutions Inc",
    "totalValue": 1200000.00
  },
  {
    "vendorId": 5,
    "vendorName": "Office Supplies Co",
    "totalValue": 850000.00
  }
]
```

## Frontend Changes

### New Files Created:

1. **Services**:
   - `dashboardApi.js` - API service for dashboard endpoints

2. **Chart Components** (Reusable):
   - `BarChartComponent.js` - Bar chart wrapper
   - `PieChartComponent.js` - Pie chart wrapper
   - `LineChartComponent.js` - Line chart wrapper
   - `DonutChartComponent.js` - Donut chart wrapper

3. **Pages**:
   - `AnalyticsDashboard.js` - Main analytics dashboard page

### Modified Files:
- `App.js` - Added `/analytics` route
- `Dashboard.js` - Added Analytics Dashboard menu item

### Charts Implemented:

1. **Bar Chart** - PR vs PO count per month
2. **Pie Chart** - PR status distribution
3. **Line Chart** - Monthly procurement spending
4. **Donut Chart** - Vendor-wise PO value share
5. **Stacked Bar Chart** - PO status breakdown

### Dashboard Features:

- **KPI Cards**: Display key metrics (Total Vendors, Active Vendors, Total PRs, Total POs, Total Procurement Amount)
- **Interactive Charts**: Hover tooltips, legends, responsive design
- **Loading States**: Shows spinner while fetching data
- **Error Handling**: Displays error messages if API calls fail
- **Responsive Layout**: Works on desktop, tablet, and mobile

## Installation Steps

### Backend:
No additional dependencies required. The code uses existing Spring Boot and JPA libraries.

### Frontend:

1. Install Recharts library:
```bash
cd procurement-ui
npm install recharts
```

2. Start the React app:
```bash
npm start
```

## Usage

1. Start the Spring Boot backend
2. Start the React frontend
3. Login to the application
4. Click on "Analytics Dashboard" from the main dashboard
5. View interactive charts and statistics

## Performance Considerations

- Dashboard service uses in-memory aggregation for small datasets
- For large datasets, consider:
  - Adding database-level aggregation queries
  - Implementing caching (Redis)
  - Adding pagination for vendor PO values
  - Using scheduled jobs to pre-compute statistics

## Security

- All endpoints are protected by JWT authentication
- Uses existing security configuration
- Read-only operations (no data modification)

## Future Enhancements

- Add date range filters
- Export charts as images
- Add more drill-down capabilities
- Real-time updates using WebSockets
- Custom dashboard builder
- Scheduled email reports
