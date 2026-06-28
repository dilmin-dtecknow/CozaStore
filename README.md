# CozaStore

CozaStore is a Java EE based e-commerce web application that allows customers to browse products, manage their shopping cart, and place orders through a user-friendly web interface. The project follows the MVC architecture using JSP, Servlets, and JDBC.

---

## Features

- User Registration & Login
- Product Catalog
- Product Search
- Product Categories
- Shopping Cart
- Checkout Process
- Order Management
- User Profile Management
- Admin Dashboard
- Product Management (CRUD)
- Customer Management
- Responsive User Interface

---

## Technologies Used

### Backend
- Java
- Java EE (Servlets)
- JSP
- JDBC

### Frontend
- HTML5
- CSS3
- JavaScript
- Bootstrap

### Database
- MySQL

### Server
- GlassFish / Payara

### Build Tool
- Apache Ant

---

## Project Structure

```
CozaStore/
│
├── src/                 # Java source code
├── web/                 # JSP pages, CSS, JS, Images
├── lib/                 # External libraries
├── test/                # Test classes
├── build.xml            # Apache Ant build file
└── README.md
```

---

## MVC Architecture

```
Browser
    │
    ▼
JSP Pages
    │
    ▼
Servlet (Controller)
    │
    ▼
DAO Layer
    │
    ▼
MySQL Database
```

---

## Prerequisites

Before running this project, install:

- Java JDK 11 or later
- Apache Ant
- MySQL Server
- GlassFish Server or Payara Server
- NetBeans / IntelliJ IDEA / Eclipse

---

## Installation

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/CozaStore.git
```

### 2. Import the project

Open the project using your preferred IDE.

### 3. Create the database

Create a MySQL database and import the SQL file provided with the project.

Example:

```sql
CREATE DATABASE cozastore;
```

Update the database connection details in the JDBC configuration.

### 4. Build the project

```bash
ant clean
ant build
```

### 5. Deploy

Deploy the generated WAR file to GlassFish or Payara.

### 6. Run

Open your browser and navigate to:

```
http://localhost:8080/CozaStore
```

---

## Main Modules

- Authentication
- Products
- Categories
- Shopping Cart
- Checkout
- Orders
- Customer Profile
- Admin Dashboard

---

## Future Improvements

- Payment Gateway Integration
- Email Notifications
- Wishlist
- Product Reviews
- Inventory Management
- REST API
- JWT Authentication
- Role-Based Authorization
- Order Tracking

---

## Learning Objectives

This project demonstrates:

- Java EE Development
- MVC Design Pattern
- JDBC Database Connectivity
- Session Management
- CRUD Operations
- Web Application Development
- E-commerce Business Logic

---

## Author

**Dilmin Fernando**

Software Engineer | Java Developer | Full Stack Developer

---

## License

This project is intended for educational and learning purposes.
