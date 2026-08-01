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
* Ollama (local LLM) for the AI Advisor module

**Frontend**

* React 18 + Vite
* React Router
* Axios

\---

## Project Structure

```
Expense-tracker/
├── backend/        ← Spring Boot REST API (+ AI Advisor module)
└── frontend/       ← React app
```

\---

## Backend Features

* JWT-based authentication (register, login)
* Expense CRUD — create, read, update, delete — scoped to the logged-in user
* Category management with auto-create
* Pagination support on expense listing
* DTO pattern — request and response models separated from entities
* Oracle sequences for primary key generation
* **AI Advisor module** — chat + auto-generated insights, powered by a local Ollama model, grounded in your real expense data (no data leaves your machine)



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

### 4\. Set up the AI Advisor (Ollama — local & free)

The advisor calls a locally running [Ollama](https://ollama.com) model, so nothing is sent to a third-party API and there's no API key or bill.

```bash
# 1. Install Ollama: https://ollama.com/download
# 2. Pull a model (llama3.2 is a good default — small and fast on CPU)
ollama pull llama3.2
# 3. Start the Ollama server (leave this running in its own terminal)
ollama serve
```

`ollama.base-url` and `ollama.model` in `application.properties` control which model is used — change `ollama.model` if you pull a different one (e.g. `mistral`, `llama3.1`, `phi3`).

### 5\. Run the backend

```bash
cd backend
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080`

### 6\. Run the frontend

```bash
cd frontend
npm install
cp .env.example .env   # defaults to http://localhost:8080, edit if needed
npm run dev
```

The app starts at `http://localhost:5173`. Register an account, log in, and start tracking.

\---

## Running everything together

You'll want three things running at once, each in its own terminal:

1. `ollama serve` (AI advisor)
2. `cd backend && ./mvnw spring-boot:run` (API, port 8080)
3. `cd frontend && npm run dev` (UI, port 5173)

If the advisor endpoints return a "could not reach Ollama" error, it just means step 1 isn't running yet — everything else in the app works fine without it.

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

### AI Advisor

|Method|Endpoint|Description|Auth required|
|-|-|-|-|
|GET|/api/advisor/insights|Auto-generated tips based on your expense data|Yes|
|POST|/api/advisor/chat|Chat with the advisor about your spending|Yes|

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

**Chat with the Advisor**

```json
POST /api/advisor/chat
Authorization: Bearer <token>

{
  "message": "Where is most of my money going?",
  "history": []
}
```

\---

## Roadmap

* \[ ] Input validation with Bean Validation
* \[ ] Global exception handler
* \[ ] Analytics endpoints (monthly trend, top categories)
* \[ ] CSV export
* \[x] React frontend
* \[x] AI Advisor module (local LLM via Ollama)

\---

## Author

**Abirami** — [github.com/](https://github.com/YOUR_USERNAME)Abirami-2007

