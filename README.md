# Expense Tracker

A full-stack personal expense tracking application with a Java Spring Boot backend and Oracle Database.

\---

## Tech Stack

**Backend**

* Java 17
* Spring Boot 3
* Spring Security + JWT Authentication
* Hibernate / Spring Data JPA
* Oracle Database (XE)
* Lombok

\---

## Project Structure

```
Expense-tracker/
├── backend/        ← Spring Boot REST API
└── frontend/       ← React app (coming soon)
```

\---

## Backend Features

* JWT-based authentication (register, login)
* Expense CRUD — create, read, update, delete
* Category management with auto-create
* Pagination support on expense listing
* DTO pattern — request and response models separated from entities
* Oracle sequences for primary key generation



\---

## Getting Started

### Prerequisites

* Java 17+
* Maven
* Oracle Database XE running locally

### 1\. Clone the repository

```bash
git clone https://github.com/Abirami-2007/expense-tracker.git
cd expense-tracker/backend
```

### 2\. Set up the database

Create a user in Oracle:

```sql
CREATE USER expense\_user IDENTIFIED BY yourpassword;
GRANT CONNECT, RESOURCE TO expense\_user;
```

### 3\. Configure application.properties

Copy the example file and fill in your values:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Edit `application.properties`:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
spring.datasource.username=expense\_user
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format\_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect

jwt.secret=your\_secret\_key\_minimum\_32\_characters\_long
```

### 4\. Run the application

```bash
cd backend
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080`

\---

## API Endpoints

### Auth

|Method|Endpoint|Description|Auth required|
|-|-|-|-|
|POST|/auth/register|Register new user|No|
|POST|/auth/login|Login, get token|No|

### Expenses

|Method|Endpoint|Description|Auth required|
|-|-|-|-|
|POST|/api/expense|Create expense|Yes|
|GET|/api/expense|Get all expenses|Yes|
|GET|/api/expense/page|Get expenses (paginated)|Yes|
|GET|/api/expense/{id}|Get expense by ID|Yes|
|PUT|/api/expense/{id}|Update expense|Yes|
|DELETE|/api/expense/{id}|Delete expense|Yes|

### Using the JWT token

After login, add the token to every request header:

```
Authorization: Bearer <your\_token\_here>
```

\---

## Sample Requests

**Register**

```json
POST /auth/register
{
  "email": "user@example.com",
  "password": "yourpassword"
}
```

**Login**

```json
POST /auth/login
{
  "email": "user@example.com",
  "password": "yourpassword"
}
```

**Create Expense**

```json
POST /api/expense
Authorization: Bearer <token>

{
  "title": "Lunch",
  "amount": 250.00,
  "category": "Food",
  "expensedate": "2026-07-05"
}
```

\---

## Roadmap

* \[ ] Input validation with Bean Validation
* \[ ] Global exception handler
* \[ ] Analytics endpoints (monthly trend, top categories)
* \[ ] CSV export
* \[ ] React frontend

\---

## Author

Abirami

GitHub:
https://github.com/Abirami-2007

