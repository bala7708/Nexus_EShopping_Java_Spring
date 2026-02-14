# Nexus_EShopping_Java_Spring
NEXUS is a full-stack e-commerce platform built with Java 17 and Spring Boot 3. It features secure authentication with Spring Security, a session-based shopping cart, transactional order processing, and a complete admin dashboard. Designed using layered MVC architecture with JPA/Hibernate and production-oriented backend principles.

# 🛍️ NEXUS — Spring Boot E-Commerce Platform

> A full-stack e-commerce web application built with **Java 17**, **Spring Boot 3**, **Spring Security**, **JPA/Hibernate**, and **Thymeleaf** — featuring a complete customer storefront, session-based shopping cart, order tracking, and a fully functional admin dashboard.

---

## 📋 Table of Contents

- [Live Demo Credentials](#-live-demo-credentials)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Architecture Overview](#-architecture-overview)
- [Data Models](#-data-models)
- [API & URL Routes](#-api--url-routes)
- [Security Design](#-security-design)
- [Cart & Buy Flow](#-cart--buy-flow)
- [Order Status Lifecycle](#-order-status-lifecycle)
- [Admin Dashboard](#-admin-dashboard)
- [Database Schema](#-database-schema)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [Switching to MySQL / PostgreSQL](#-switching-to-mysql--postgresql)
- [Sample Data](#-sample-data)
- [Known Limitations & Future Improvements](#-known-limitations--future-improvements)

---

## 🔑 Live Demo Credentials

The app auto-seeds two accounts on first startup:

| Role  | Email            | Password   | Access                       |
|-------|------------------|------------|------------------------------|
| Admin | `admin@shop.com` | `admin123` | Full admin dashboard + shop  |
| User  | `user@shop.com`  | `user123`  | Shop, cart, orders           |

> You can also register a new account at `/auth/register`.

---

## ✨ Features

### 🛒 Customer Storefront
- **Product browsing** — responsive grid with category filters and keyword search
- **Product detail pages** — image, description, stock count, related products
- **Add to Cart** — session-based cart, persists across page navigations
- **Buy Now** — single-click add + redirect to checkout
- **Quantity control** — increment/decrement in cart, stock-capped
- **Clear Cart** — remove all items at once
- **Guest cart** — browse and add items without logging in; checkout requires login
- **Checkout flow** — shipping address form with pre-fill from user profile, step indicator bar
- **Order placement** — stock deducted on checkout, tracking number auto-generated
- **Order history** — full table of past orders with status badges
- **Order detail & tracking** — 5-stage visual timeline (Placed → Confirmed → Processing → Shipped → Delivered)

### 🛠️ Admin Dashboard (`/admin`)
- **Stats overview** — total revenue, orders, products, customers
- **Product management** — add, edit, delete, show/hide (toggle active), live image preview
- **User management** — create, edit, delete, enable/disable accounts
- **Order management** — view all orders, update status through 6 stages
- **Recent orders panel** on dashboard home

### 🔐 Authentication & Security
- Spring Security form-based login
- BCrypt password hashing
- Role-based access control (`ROLE_USER` / `ROLE_ADMIN`)
- Guest users can browse and add to cart; protected routes require login
- Admin routes completely separated from user routes
- Session invalidation on logout

---

## 🧰 Tech Stack

| Layer          | Technology                                      |
|----------------|-------------------------------------------------|
| Language       | Java 17                                         |
| Framework      | Spring Boot 3.2.0                               |
| Web MVC        | Spring MVC (Thymeleaf templates)                |
| Security       | Spring Security 6 + BCrypt                      |
| ORM / DB       | Spring Data JPA + Hibernate                     |
| Database       | H2 (in-memory, dev) — switchable to MySQL/PG    |
| Templating     | Thymeleaf 3 + thymeleaf-extras-springsecurity6  |
| Build tool     | Apache Maven 3                                  |
| Validation     | Jakarta Bean Validation                         |
| Dev tools      | Spring Boot DevTools                            |
| Frontend       | Vanilla CSS (custom design system), HTML5       |
| Fonts          | Google Fonts — Playfair Display + DM Sans       |

---

## 📁 Project Structure

```
ecommerce/
├── pom.xml
└── src/
    └── main/
        ├── java/com/ecommerce/
        │   ├── EcommerceApplication.java          # Spring Boot entry point
        │   │
        │   ├── config/
        │   │   ├── SecurityConfig.java            # Spring Security rules & filters
        │   │   └── DataInitializer.java           # Seeds admin user + 12 demo products
        │   │
        │   ├── controller/
        │   │   ├── AuthController.java            # /auth/login, /auth/register
        │   │   ├── ShopController.java            # /, /shop, /product/{id}
        │   │   ├── CartController.java            # /cart/*, /cart/checkout
        │   │   ├── OrderController.java           # /orders, /orders/{id}
        │   │   └── AdminController.java           # /admin/**
        │   │
        │   ├── model/
        │   │   ├── User.java                      # Entity: customers & admins
        │   │   ├── Product.java                   # Entity: catalog items
        │   │   ├── Order.java                     # Entity: placed orders
        │   │   ├── OrderItem.java                 # Entity: line items in an order
        │   │   └── CartItem.java                  # POJO: session-based cart item
        │   │
        │   ├── repository/
        │   │   ├── UserRepository.java
        │   │   ├── ProductRepository.java
        │   │   └── OrderRepository.java
        │   │
        │   └── service/
        │       ├── CustomUserDetailsService.java  # Spring Security integration
        │       ├── UserService.java
        │       ├── ProductService.java
        │       ├── CartService.java               # Session-scoped cart logic
        │       └── OrderService.java
        │
        └── resources/
            ├── application.properties
            ├── static/
            │   └── css/
            │       └── main.css                   # Full custom design system
            └── templates/
                ├── auth/
                │   ├── login.html
                │   └── register.html
                ├── user/
                │   ├── shop.html                  # Product grid with filters
                │   ├── product-detail.html        # Single product + related
                │   ├── cart.html                  # Cart with qty controls
                │   ├── checkout.html              # Shipping + payment form
                │   ├── orders.html                # Order history table
                │   └── order-detail.html          # Order + tracking timeline
                └── admin/
                    ├── dashboard.html             # Stats + recent orders
                    ├── products.html              # Product table
                    ├── product-form.html          # Add/Edit product
                    ├── users.html                 # User table
                    ├── user-form.html             # Add/Edit user
                    ├── orders.html                # All orders table
                    └── order-detail.html          # Order view + status update
```

---

## 🏗️ Architecture Overview

This application follows a classic **MVC layered architecture**:

```
Browser
  │
  ▼
Controller Layer          ← Handles HTTP requests, delegates to services
  │
  ▼
Service Layer             ← Business logic, transaction management
  │
  ▼
Repository Layer          ← Spring Data JPA interfaces → SQL queries
  │
  ▼
Database (H2 / MySQL)
```

**Session management** for the cart is handled separately via `HttpSession` — no database writes until the user places an order, keeping cart operations fast and stateless from a persistence standpoint.

**Security** is enforced at the URL-pattern level in `SecurityConfig`, with role checks (`hasRole('ADMIN')`) and authentication requirements (`authenticated()`) declared per URL prefix.

---

## 🗃️ Data Models

### `User`
```
id            BIGINT PK AUTO_INCREMENT
firstName     VARCHAR NOT NULL
lastName      VARCHAR NOT NULL
email         VARCHAR UNIQUE NOT NULL
password      VARCHAR NOT NULL          (BCrypt hash)
phone         VARCHAR
address       TEXT
role          ENUM(USER, ADMIN)
active        BOOLEAN DEFAULT TRUE
createdAt     TIMESTAMP
```

### `Product`
```
id            BIGINT PK AUTO_INCREMENT
name          VARCHAR NOT NULL
description   TEXT (max 1000 chars)
price         DECIMAL NOT NULL
stockQuantity INT
category      VARCHAR
imageUrl      VARCHAR
active        BOOLEAN DEFAULT TRUE
createdAt     TIMESTAMP
updatedAt     TIMESTAMP
```

### `Order`
```
id              BIGINT PK AUTO_INCREMENT
user_id         BIGINT FK → users.id
status          ENUM(PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
totalAmount     DECIMAL
shippingAddress TEXT
trackingNumber  VARCHAR
createdAt       TIMESTAMP
updatedAt       TIMESTAMP
```

### `OrderItem`
```
id          BIGINT PK AUTO_INCREMENT
order_id    BIGINT FK → orders.id
product_id  BIGINT FK → products.id
quantity    INT
unitPrice   DECIMAL                   (snapshot at time of purchase)
```

### `CartItem` (session-only, not persisted)
```
productId     Long
productName   String
unitPrice     BigDecimal
quantity      int
imageUrl      String
```

> **Why session cart?** Cart items are stored in `HttpSession` as a `List<CartItem>`. This avoids unnecessary DB writes for every cart interaction and is cleared automatically on session expiry. On checkout, the cart is validated against live stock and written to `Order` + `OrderItem` tables atomically.

---

## 🗺️ API & URL Routes

### Public Routes (no login required)
| Method | URL                        | Description                       |
|--------|----------------------------|-----------------------------------|
| GET    | `/`                        | Redirects to `/shop`              |
| GET    | `/shop`                    | Product grid (all)                |
| GET    | `/shop?category=X`         | Filter by category                |
| GET    | `/shop?search=keyword`     | Search by product name            |
| GET    | `/product/{id}`            | Product detail page               |
| GET    | `/cart`                    | View cart (works for guests)      |
| POST   | `/cart/add/{productId}`    | Add item to session cart          |
| POST   | `/cart/update/{productId}` | Update item quantity              |
| POST   | `/cart/remove/{productId}` | Remove item from cart             |
| POST   | `/cart/clear`              | Empty the entire cart             |
| GET    | `/auth/login`              | Login page                        |
| POST   | `/auth/login`              | Process login (Spring Security)   |
| GET    | `/auth/register`           | Registration page                 |
| POST   | `/auth/register`           | Create new user account           |
| GET    | `/auth/logout`             | Logout + session invalidation     |

### Authenticated Routes (login required)
| Method | URL                     | Description                          |
|--------|-------------------------|--------------------------------------|
| GET    | `/cart/checkout`        | Checkout page (with pre-fill)        |
| POST   | `/cart/checkout`        | Place order                          |
| POST   | `/cart/buy-now/{id}`    | Add to cart + redirect to checkout   |
| GET    | `/orders`               | My order history                     |
| GET    | `/orders/{id}`          | Order detail + tracking timeline     |

### Admin Routes (`ROLE_ADMIN` only)
| Method | URL                             | Description                        |
|--------|---------------------------------|------------------------------------|
| GET    | `/admin`                        | Dashboard with stats               |
| GET    | `/admin/products`               | All products table                 |
| GET    | `/admin/products/new`           | New product form                   |
| POST   | `/admin/products/save`          | Create product                     |
| GET    | `/admin/products/edit/{id}`     | Edit product form                  |
| POST   | `/admin/products/update/{id}`   | Update product                     |
| POST   | `/admin/products/delete/{id}`   | Delete product                     |
| POST   | `/admin/products/toggle/{id}`   | Toggle product active/hidden       |
| GET    | `/admin/users`                  | All users table                    |
| GET    | `/admin/users/new`              | New user form                      |
| POST   | `/admin/users/save`             | Create user                        |
| GET    | `/admin/users/edit/{id}`        | Edit user form                     |
| POST   | `/admin/users/update/{id}`      | Update user                        |
| POST   | `/admin/users/delete/{id}`      | Delete user                        |
| POST   | `/admin/users/toggle/{id}`      | Enable / disable user account      |
| GET    | `/admin/orders`                 | All orders table                   |
| GET    | `/admin/orders/{id}`            | Order detail + status panel        |
| POST   | `/admin/orders/{id}/status`     | Update order status                |

### Dev Tools
| URL            | Description                     |
|----------------|---------------------------------|
| `/h2-console`  | H2 in-browser database console  |

---

## 🔐 Security Design

Security is configured in `SecurityConfig.java` using Spring Security 6's lambda DSL.

```
Public               → /shop/**, /product/**, /auth/**, /cart (view/add/remove)
Authenticated        → /cart/checkout, /cart/buy-now/**, /orders/**
Admin only           → /admin/**
Dev only             → /h2-console/** (disable in production!)
```

**Password handling:**  
All passwords are hashed with `BCryptPasswordEncoder` before storage. Plain-text passwords are never stored or logged.

**CSRF:**  
CSRF protection is active on all POST endpoints. The H2 console is excluded from CSRF for dev convenience — remove this in production.

**Session:**  
Sessions expire after 30 minutes of inactivity (`server.servlet.session.timeout=30m`). On logout, the session is fully invalidated and the `JSESSIONID` cookie is deleted.

---

## 🛒 Cart & Buy Flow

```
User clicks "Add to Cart"
        │
        ▼
CartController.addToCart()
        │
        ├── Product found? (ProductService.findById)
        ├── In stock?
        ├── Quantity valid?
        │
        ▼
CartService.addToCart(session, product, qty)
        │
        ├── Item already in cart? → increment quantity (capped at stock)
        └── New item? → append CartItem to session list

─────────────────────────────────────────────────────

User clicks "Buy Now"
        │
        ▼
CartController.buyNow()   ← adds to cart, then
        │
        └──► redirect /cart/checkout

─────────────────────────────────────────────────────

User submits checkout form
        │
        ▼
CartController.placeOrder()
        │
        ├── Validate shippingAddress not empty
        ├── Load User from SecurityContext
        │
        ▼
OrderService.createOrder(user, cartItems, shippingAddress)
        │
        ├── For each CartItem:
        │     ├── Load Product from DB
        │     ├── Check stockQuantity >= requested qty
        │     ├── Create OrderItem (snapshot unitPrice)
        │     └── Decrement product.stockQuantity → save
        │
        ├── Create Order (status=PENDING, auto trackingNumber)
        ├── Save Order + cascade OrderItems
        │
        ▼
CartService.clearCart(session)
        │
        ▼
Redirect to /orders/{newOrderId}  ← success flash message
```

**Stock capping:** When updating quantity in the cart, `CartService` silently caps the value at `product.stockQuantity` so users can never request more than available.

**Price snapshot:** `unitPrice` is saved on each `OrderItem` at checkout time. If the product price changes later, historical orders are unaffected.

---

## 📦 Order Status Lifecycle

Orders move through 6 states, managed by the admin:

```
PENDING  ──►  CONFIRMED  ──►  PROCESSING  ──►  SHIPPED  ──►  DELIVERED
   │
   └──────────────────────────────────────────────────────►  CANCELLED
```

| Status       | Meaning                                    | Badge Color  |
|--------------|--------------------------------------------|--------------|
| `PENDING`    | Order received, awaiting admin action      | Gold         |
| `CONFIRMED`  | Admin has accepted the order               | Blue         |
| `PROCESSING` | Items being packed                         | Purple       |
| `SHIPPED`    | Dispatched, in transit                     | Teal         |
| `DELIVERED`  | Customer has received the package          | Green        |
| `CANCELLED`  | Order was cancelled                        | Red          |

The customer-facing **order tracking timeline** at `/orders/{id}` visually highlights the current stage using a 5-dot timeline (Placed → Confirmed → Processing → Shipped → Delivered).

---

## 🖥️ Admin Dashboard

The admin panel lives at `/admin` and is only accessible to users with `ROLE_ADMIN`.

### Dashboard Home
- **4 stat cards:** Total Revenue, Total Orders, Active Products, Registered Users
- **Pending orders** count highlighted
- **Recent 5 orders** table with quick-access Manage links
- **Quick action cards:** Add Product, Add User, Manage Orders

### Product Management (`/admin/products`)
- Full sortable product table with image thumbnails, price, stock, active/hidden status
- **Add product:** name, description, price, stock quantity, category (with autocomplete from existing categories), image URL with **live preview** as you type
- **Edit product:** same form, pre-populated
- **Toggle visibility:** show/hide product in the storefront without deleting it
- **Delete product:** with confirmation dialog

### User Management (`/admin/users`)
- Table of all users with role badges, join date, active status
- **Create user:** full form including password (encrypted automatically)
- **Edit user:** update all fields; leave password blank to keep existing
- **Enable/Disable account:** suspends login without deleting data
- **Delete user:** permanent removal

### Order Management (`/admin/orders`)
- All orders sorted newest-first with customer name, email, item count, total, status badge, tracking number
- **Order detail:** full item list with images, subtotals, customer info, shipping address
- **Status update:** dropdown to move order through any status stage

---

## 🗄️ Database Schema

The application uses **H2 in-memory database** by default (auto-configured by Spring Boot). Tables are created automatically by Hibernate from the JPA entity definitions on each startup (`ddl-auto=create-drop`).

### Entity Relationships

```
users ──────┐
            │ 1:N
            ▼
          orders ─────┐
                      │ 1:N
                      ▼
                 order_items
                      │ N:1
                      ▼
                   products
```

- One **User** → many **Orders**
- One **Order** → many **OrderItems**
- One **OrderItem** → one **Product**
- **CartItem** is not persisted (session-only)

### JPA Fetch Strategies

| Relationship              | Strategy | Reason                                      |
|---------------------------|----------|---------------------------------------------|
| `User → Orders`           | LAZY     | Don't load all orders when fetching a user  |
| `Order → User`            | LAZY     | Don't load user graph on every order query  |
| `Order → OrderItems`      | EAGER    | Always need items when viewing an order     |
| `OrderItem → Product`     | EAGER    | Always need product details per line item   |
| `OrderItem → Order`       | LAZY     | Avoid circular load                         |

---

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **Maven 3.8+**

Verify with:
```bash
java -version   # should say 17+
mvn -version    # should say 3.8+
```

### Clone & Run

```bash
# 1. Clone the repository
git clone https://github.com/your-username/nexus-ecommerce.git
cd nexus-ecommerce

# 2. Run with Maven
mvn spring-boot:run
```

The app starts on **http://localhost:8080**

On first run, `DataInitializer` seeds:
- 1 admin account (`admin@shop.com` / `admin123`)
- 1 sample user (`user@shop.com` / `user123`)
- 12 products across 4 categories

### Build JAR

```bash
mvn clean package -DskipTests
java -jar target/ecommerce-app-1.0.0.jar
```

---

## ⚙️ Configuration

All configuration lives in `src/main/resources/application.properties`:

```properties
# Server
server.port=8080

# H2 Database (in-memory, resets on restart)
spring.datasource.url=jdbc:h2:mem:ecommercedb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true          # Disable in production!
spring.h2.console.path=/h2-console

# JPA / Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop   # Change to 'update' for persistence
spring.jpa.show-sql=false

# Thymeleaf
spring.thymeleaf.cache=false             # Set to true in production

# Session timeout
server.servlet.session.timeout=30m
```

---

## 🔄 Switching to MySQL / PostgreSQL

**Step 1** — Replace the H2 dependency in `pom.xml`:

```xml
<!-- Remove H2 -->
<!-- Add MySQL -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Or PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Step 2** — Update `application.properties`:

```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/nexus_shop?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=false

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/nexus_shop
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=false
```

**Step 3** — Create the database:
```sql
CREATE DATABASE nexus_shop;
```

Hibernate will create all tables automatically on first startup with `ddl-auto=update`.

---

## 🌱 Sample Data

`DataInitializer.java` seeds the following on every clean startup:

### Products (12 items across 4 categories)

| Category      | Products                                                               |
|---------------|------------------------------------------------------------------------|
| Electronics   | Wireless Headphones, Mechanical Keyboard, Bluetooth Speaker            |
| Fashion       | Leather Watch, Running Sneakers, Leather Card Wallet                   |
| Home & Kitchen| Ceramic Pour-Over Set, Linen Throw Blanket, Smart LED Desk Lamp        |
| Sports        | Yoga Mat, Stainless Steel Water Bottle, Resistance Bands Set           |

All products include Unsplash image URLs, realistic descriptions, prices ($29.99–$299.99), and stock quantities (15–100 units).

---

## ⚠️ Known Limitations & Future Improvements

### Current Limitations

| Area | Limitation |
|------|------------|
| Payments | Demo only — no real payment gateway (Stripe/PayPal not integrated) |
| Images | Products use external URLs (Unsplash); no file upload system |
| Database | H2 is in-memory — all data is lost on restart unless switched to MySQL/PG |
| Email | No order confirmation emails sent |
| Search | Simple `LIKE` name search only; no full-text or category+name combined search |
| Cart | Session-based only; cart is lost if session expires or browser closes |
| Pagination | No pagination on product grid or admin tables (could be slow with many records) |

### Suggested Future Improvements

- **Real payment integration** — Stripe Checkout or Razorpay
- **Product image upload** — multipart file upload + local/S3 storage
- **Email notifications** — Spring Mail for order confirmation and status updates
- **Wishlist** — saved products per user
- **Product reviews & ratings**
- **Coupon / discount code system**
- **Inventory alerts** — notify admin when stock drops below threshold
- **Pagination** — Spring Data `Pageable` for product and admin tables
- **REST API layer** — expose endpoints as JSON for a future React/Vue frontend
- **Cart persistence** — save cart to DB for logged-in users so it survives session expiry
- **Docker support** — `Dockerfile` + `docker-compose.yml` with MySQL
- **Unit & integration tests** — JUnit 5 + Mockito + Spring Boot Test

---

## 🎨 Frontend Design

The UI uses a custom hand-written CSS design system (no Bootstrap, no Tailwind) with the following aesthetic:

- **Theme:** Dark luxury commerce — deep blacks with gold accents
- **Fonts:** [Playfair Display](https://fonts.google.com/specimen/Playfair+Display) (headings) + [DM Sans](https://fonts.google.com/specimen/DM+Sans) (body)
- **Color palette:**
  - Background: `#0d0d0f`
  - Card surface: `#16161a`
  - Accent gold: `#c9a84c`
  - Accent coral (danger): `#e05c4b`
  - Text primary: `#f0ede8`
- **Animations:** CSS `fadeIn` with staggered delays on product grid
- **Responsive:** Fluid grid with `auto-fill` columns, collapses to 1 column on mobile

---

## 🤝 Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Open a Pull Request

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👨‍💻 Author

Built with Spring Boot 3, Spring Security, JPA/Hibernate, Thymeleaf, and custom CSS.

> ⭐ If you found this project helpful, consider starring the repository!
