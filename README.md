# Vendor Procurement Management System (SPVMS)

Enterprise-grade procurement platform for managing vendors, purchase requisitions, and purchase orders with comprehensive analytics and workflow automation.

## 🚀 Features

### Core Modules
- **User Authentication** - JWT-based auth with role-based access control (ADMIN, VENDOR, PROCUREMENT, FINANCE)
- **Vendor Management** - Create, search, and manage vendors with compliance tracking
- **Purchase Requisitions (PR)** - Create PRs without vendor assignment, approval workflow with history tracking
- **Purchase Orders (PO)** - Create POs with active vendors, item management, auto-close on delivery completion
- **Analytics Dashboard** - Real-time KPIs, charts (PR/PO status, monthly trends, vendor values)
- **Reports** - Generate PDF/Excel reports for vendors, PRs, and POs
- **History Tracking** - Complete audit trail for PR and PO status changes

### Key Highlights
✅ Modern UI with purple gradient theme  
✅ Phone validation (10 digits) for vendors  
✅ Password strength indicator  
✅ Active/Inactive vendor filtering  
✅ Token expiry (1 hour) with auto-redirect  
✅ Responsive design for mobile/tablet/desktop  
✅ Real-time total calculations for POs  
✅ History tracking with timestamps and remarks  

## 🛠️ Tech Stack

### Backend
- Java 17
- Spring Boot 3.2.5
- Spring Security with JWT
- MySQL 8.0
- JPA/Hibernate
- Maven
- iText (PDF generation)
- Apache POI (Excel generation)

### Frontend
- React 18
- Material-UI (MUI)
- Recharts (Analytics)
- Axios
- React Router

## 📋 Prerequisites

- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

## ⚙️ Installation

### 1. Database Setup
```sql
CREATE DATABASE spvms_db;
CREATE USER 'spvms_db'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON spvms_db.* TO 'spvms_db'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Backend Setup
```bash
cd Java-based-vendor-procurement-main
mvn clean install
mvn spring-boot:run
```
Backend runs on: `http://localhost:8081`

### 3. Frontend Setup
```bash
cd procurement-ui
npm install
npm start
```
Frontend runs on: `http://localhost:3000`

## 🔑 Default Credentials

**Email:** admin@company.com  
**Password:** Admin@123

## 📊 Database Schema

### Main Tables
- `users` - User accounts with roles
- `vendors` - Vendor information with compliance status
- `purchase_requisitions` - PR records
- `purchase_order` - PO records
- `purchase_order_item` - PO line items
- `pr_history` - PR audit trail
- `po_history` - PO audit trail

## 🧪 Testing

### Manual Testing
1. Open `test-runner.html` in browser
2. Follow `TESTING_GUIDE.md` for complete test scenarios
3. Use `API_TESTING.md` for API endpoint testing

### Test Scenarios Covered
- User registration and login
- Vendor CRUD operations
- PR creation and approval workflow
- PO creation with item management
- Report generation (PDF/Excel)
- Role-based access control
- Token expiry handling

## 📁 Project Structure

```
├── src/main/java/com/example/spvms/
│   ├── config/          # Security, JWT, CORS
│   ├── controllers/     # REST endpoints
│   ├── dto/            # Data transfer objects
│   ├── enums/          # Status enums
│   ├── model/          # JPA entities
│   ├── repository/     # Data access layer
│   └── service/        # Business logic
├── procurement-ui/
│   └── src/
│       ├── components/  # React components
│       ├── services/    # API services
│       └── utils/       # Helper functions
├── TESTING_GUIDE.md    # Complete testing guide
├── API_TESTING.md      # API testing documentation
└── test-runner.html    # Interactive test tool
```

## 🔐 Security Features

- Password hashing with BCrypt
- JWT token authentication (1-hour expiry)
- Role-based access control
- CORS configuration
- SQL injection prevention (JPA)
- XSS protection (React escaping)

## 📈 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get token

### Vendors
- `GET /api/vendors/search` - Search vendors
- `GET /api/vendors/active` - Get active vendors
- `POST /api/vendors` - Create vendor

### Purchase Requisitions
- `POST /api/requisitions` - Create PR
- `GET /api/requisitions/{id}/history` - Get PR history

### Purchase Orders
- `POST /api/purchase-orders` - Create PO
- `POST /api/purchase-orders/{id}/items` - Add item to PO
- `GET /api/purchase-orders/{id}/history` - Get PO history

### Dashboard
- `GET /api/dashboard/overview` - Get KPIs
- `GET /api/dashboard/pr-status` - PR status distribution
- `GET /api/dashboard/po-status` - PO status distribution
- `GET /api/dashboard/monthly-trend` - Monthly trend data
- `GET /api/dashboard/vendor-po-values` - Vendor PO values

### Reports
- `GET /api/reports/vendor?format=PDF` - Vendor report
- `GET /api/reports/pr?format=EXCEL` - PR report
- `GET /api/reports/po?format=PDF` - PO report

## 🎨 UI Screenshots

### Dashboard
- KPI cards with real-time data
- 4 interactive charts (Pie, Donut, Line, Bar)
- Quick access navigation

### Vendor Management
- Search and filter capabilities
- Active/Inactive status badges
- Phone validation (10 digits)

### Purchase Requisitions
- Create PR without vendor
- Approval/Rejection workflow
- History tracking with timeline

### Purchase Orders
- Active vendor dropdown
- Item management with auto-calculations
- Auto-close on delivery completion
- Complete history audit trail

## 🐛 Known Issues

1. Frontend role-based UI hiding not implemented (backend enforces)
2. No refresh token mechanism
3. No email notifications for approvals

## 🚀 Future Enhancements

- [ ] Refresh token implementation
- [ ] Email notifications
- [ ] Advanced search filters
- [ ] Bulk operations
- [ ] Export to multiple formats
- [ ] Real-time notifications
- [ ] Mobile app

## 📝 License

This project is for educational/demonstration purposes.

## 👥 Contributors

Developed as a comprehensive vendor procurement management solution.

## 📞 Support

For issues or questions, please refer to:
- `TESTING_GUIDE.md` - Complete testing documentation
- `API_TESTING.md` - API endpoint testing
- `test-runner.html` - Interactive testing tool

---

**Built with ❤️ using Spring Boot & React**
