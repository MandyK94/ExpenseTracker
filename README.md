# ExpenseTracker
Project Scope – Cloud-Deployed Expense Tracker
1. Objective

Build a production-ready, cloud-deployed expense tracking application that demonstrates backend architecture, frontend integration, security, observability, CI/CD, and operational maturity using a modern Java stack.

The goal is not feature breadth, but engineering depth and correctness.

2. In Scope (Functional Requirements)
   2.1 User Management & Security

* Users can register and authenticate using email/password.

* Authentication is implemented using JWT-based stateless security.

* All protected APIs require a valid JWT.

* Users can only access and modify their own data.

2.2 Core Domain Model

The system manages the following entities:

* User

* Account

* Transaction

* Category

Each transaction:

* Belongs to a user

* Is associated with an account and category

* Contains amount, timestamp, description, and type (income/expense)

Data is persisted in PostgreSQL using JPA/Hibernate.

2.3 REST API

* CRUD APIs for:

   * Transactions

   * Categories

* APIs follow REST conventions and return consistent JSON responses.

* Input validation is enforced using @Valid.

* Transaction listing supports pagination.

* All APIs are documented using OpenAPI/Swagger.

2.4 Frontend (React + TypeScript)

The frontend provides:

* Login and registration screens

* Dashboard with recent transactions

* Transaction management (list, add, edit, delete)

* Category management

* Expense analytics using charts (weekly/monthly trends)

* Responsive, mobile-friendly UI

* Proper loading states and error handling

* JWT handling via a centralized HTTP client layer

3. Non-Functional Requirements
   3.1 Observability & Production Readiness

* Backend exposes Spring Boot Actuator endpoints.

* Application metrics are collected via Micrometer.

* Metrics are scraped by Prometheus.

* Grafana dashboards visualize API latency, throughput, and error rates.

* Health and readiness endpoints are enabled.

* Global exception handling with structured error responses.

3.2 Containerization & Deployment

* Backend packaged as an executable JAR and containerized with Docker.

* Frontend built as static assets and served via Nginx in Docker.

* docker-compose is provided for local development.

* Application is deployed to AWS EC2.

3.3 CI/CD

* GitHub Actions pipeline automates:

   * Backend build and tests

   * Frontend build

   * Docker image creation

   * Deployment to AWS EC2

3.4 Testing

* Repository layer tested using @DataJpaTest.

* Integration tests cover secured endpoints.

* Target backend test coverage: ≥ 80%.

4. Deliverables

* Public live demo URL

* Public Swagger/OpenAPI documentation

* Grafana dashboard (read-only or documented access)

* GitHub repository with full CI/CD automation

* README including:

   * Architecture diagram

   * Tech stack summary

   * Local setup instructions

   * Deployment overview

   * Links to demo, Swagger, and monitoring

* Short screen recording demonstrating key flows

5. Explicitly Out of Scope

The following are intentionally excluded to keep scope realistic:

* Multi-currency support

* Receipt uploads or OCR

* Bank integrations

* Team or organization accounts

* Advanced reporting or exports

* Background jobs or schedulers

* Event sourcing or audit trails

6. Success Criteria

The project is considered complete when:

* The application is publicly accessible

* CI/CD runs end-to-end without manual steps

* Metrics are visible in Grafana

* APIs are fully documented

* The system demonstrates production-ready engineering practices
