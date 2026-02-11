# 🚀 Complete Procurement System Setup Guide

## ✅ What's Implemented

### 🎯 **Backend (Spring Boot)**
- **Authentication**: JWT-based with email login
- **User Management**: Role-based access (ADMIN, PROCUREMENT, FINANCE, VENDOR)
- **Vendor Search**: Advanced filtering with pagination
- **Workflow Management**: Draft → Submitted → Approved/Rejected
- **Purchase Orders**: Line items with auto-calculations
- **Reporting**: JasperReports with PDF/Excel export
- **Security**: Method-level authorization

### 🎨 **Frontend (React)**
- **Login/Registration**: Material-UI forms
- **Dashboard**: Navigation and quick stats
- **Vendor Search**: Filtering and pagination
- **Purchase Orders**: CRUD operations with item management
- **Workflow**: Approval/rejection with history
- **Reports**: PDF/Excel download with filters

## 🚀 **How to Run**

### 1. **Start Backend (Spring Boot)**
```bash
cd Java-based-vendor-procurement-main
mvn spring-boot:run
```
- **URL**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html
- **Admin Login**: admin@company.com / Admin@123

### 2. **Start Frontend (React)**
```bash
cd procurement-ui
npm start
```
- **URL**: http://localhost:3000
- **Auto-opens in browser**

## 🔐 **Default Users**

| Email | Password | Role |
|-------|----------|------|
| admin@company.com | Admin@123 | ADMIN |

## 📱 **UI Features**

### **Login Page**
- Email/password authentication
- User registration
- Role selection

### **Dashboard**
- Navigation cards
- Quick statistics
- User profile display

### **Vendor Search**
- Filter by rating, location, category, compliance
- Pagination with 12 vendors per page
- Sort by rating (descending)
- Responsive card layout

### **Purchase Orders**
- Create new orders
- Add line items with quantity, price, tax, discount
- Auto-calculation of totals
- Update delivery status
- Status tracking (OPEN, CLOSED, CANCELLED)

### **Workflow Management**
- View pending approvals
- Approve/reject with comments
- View approval history
- Role-based access control

### **Reports**
- Vendor reports with filtering
- Purchase requisition reports
- Purchase order reports
- PDF and Excel download
- Date range filtering

## 🔧 **API Integration**

The React frontend automatically connects to the Spring Boot backend:
- **Base URL**: http://localhost:8080/api
- **Authentication**: JWT tokens in headers
- **Error Handling**: Automatic token refresh
- **CORS**: Configured for development

## 🎨 **UI Components**

- **Material-UI**: Modern design system
- **Responsive**: Works on desktop and mobile
- **Icons**: Material Design icons
- **Navigation**: App bar with breadcrumbs
- **Forms**: Validation and error handling
- **Tables**: Sortable and paginated
- **Dialogs**: Modal forms and confirmations

## 🚀 **Production Deployment**

### **Backend**
```bash
mvn clean package
java -jar target/spvms-0.0.1-SNAPSHOT.jar
```

### **Frontend**
```bash
npm run build
# Deploy build/ folder to web server
```

## 📊 **System Architecture**

```
┌─────────────────┐    HTTP/REST    ┌──────────────────┐
│   React UI      │ ◄──────────────► │  Spring Boot API │
│   (Port 3000)   │                 │   (Port 8080)    │
└─────────────────┘                 └──────────────────┘
                                             │
                                             ▼
                                    ┌──────────────────┐
                                    │   MySQL Database │
                                    │   (Port 3306)    │
                                    └──────────────────┘
```

## ✅ **Ready to Use!**

Your complete procurement system is now running with:
- ✅ Professional React UI
- ✅ Secure Spring Boot API
- ✅ MySQL database
- ✅ JWT authentication
- ✅ Role-based access
- ✅ Complete CRUD operations
- ✅ Advanced search and filtering
- ✅ Workflow management
- ✅ PDF/Excel reporting

**Access the system at: http://localhost:3000**