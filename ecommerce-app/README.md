# NEXUS — Spring Boot E-Commerce Application

A full-stack e-commerce platform with Admin Dashboard, built with Spring Boot 3, Spring Security, JPA/H2, and Thymeleaf.

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+

### Run the Application
```bash
mvn spring-boot:run
```

Then open: **http://localhost:8080**

---

## 👤 Default Credentials

| Role  | Email            | Password   |
|-------|------------------|------------|
| Admin | admin@shop.com   | admin123   |
| User  | user@shop.com    | user123    |

---

## 🗺️ Application Pages

### Customer Storefront
| URL                  | Description                          |
|----------------------|--------------------------------------|
| `/`                  | Shop homepage with product grid      |
| `/shop?category=X`   | Filter by category                   |
| `/shop?search=query` | Search products                      |
| `/product/{id}`      | Product detail page                  |
| `/cart`              | Shopping cart                        |
| `/cart/checkout`     | Checkout (requires login)            |
| `/orders`            | My orders list (requires login)      |
| `/orders/{id}`       | Order detail with tracking timeline  |

### Authentication
| URL              | Description       |
|------------------|-------------------|
| `/auth/login`    | Login page        |
| `/auth/register` | Registration page |
| `/auth/logout`   | Logout            |

### Admin Dashboard (`/admin/**` — Admin role required)
| URL                        | Description                     |
|----------------------------|---------------------------------|
| `/admin`                   | Dashboard with stats            |
| `/admin/products`          | Product list (add/edit/delete)  |
| `/admin/products/new`      | Add new product                 |
| `/admin/products/edit/{id}`| Edit product                    |
| `/admin/users`             | User management                 |
| `/admin/users/new`         | Create new user                 |
| `/admin/orders`            | All orders                      |
| `/admin/orders/{id}`       | Order detail + status update    |

### Dev Tools
| URL            | Description              |
|----------------|--------------------------|
| `/h2-console`  | H2 database browser      |

---

## 🏗️ Architecture

```
com.ecommerce/
├── config/
│   ├── SecurityConfig.java       # Spring Security rules
│   └── DataInitializer.java      # Seeds demo data on startup
├── controller/
│   ├── AuthController.java       # Login / Register
│   ├── ShopController.java       # Browse shop
│   ├── CartController.java       # Cart + Checkout
│   ├── OrderController.java      # User order history
│   └── AdminController.java      # Full admin panel
├── model/
│   ├── User.java                 # User entity (USER/ADMIN roles)
│   ├── Product.java              # Product entity
│   ├── Order.java                # Order with status tracking
│   ├── OrderItem.java            # Line items in an order
│   └── CartItem.java             # Session-based cart item
├── repository/                   # Spring Data JPA interfaces
├── service/
│   ├── UserService.java
│   ├── ProductService.java
│   ├── OrderService.java
│   ├── CartService.java          # Session-based cart
│   └── CustomUserDetailsService.java
└── EcommerceApplication.java
```

## 🎨 Features

### Customer Features
- ✅ Browse products with category filters + search
- ✅ Product detail pages
- ✅ Session-based shopping cart (add/update/remove)
- ✅ User registration & login
- ✅ Checkout with shipping address
- ✅ Order history with tracking timeline
- ✅ 6-stage order status tracking (Pending → Delivered)

### Admin Dashboard
- ✅ Stats: revenue, orders, products, users
- ✅ Add / Edit / Delete products with image preview
- ✅ Show/Hide (toggle) product visibility
- ✅ Create / Edit / Delete users
- ✅ Enable/Disable user accounts
- ✅ View all orders
- ✅ Update order status (6 stages)
- ✅ Automatic tracking number generation

## 🔧 Configuration

To use a persistent database (MySQL), replace `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/nexus_shop
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

And add the MySQL dependency to `pom.xml`.

## 🛍 Sample Data

The app auto-seeds 12 products across 4 categories (Electronics, Fashion, Home & Kitchen, Sports) on first run.
