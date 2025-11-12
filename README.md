# Loan Calculator API

A robust Spring Boot REST API for calculating loan payments and generating detailed amortization schedules. Built with enterprise-grade practices including comprehensive validation, database persistence, and extensive testing.

## Features

- Amortization Schedule Generation - Complete month-by-month payment breakdown
- Database Persistence - All calculations stored in MySQL with full audit trail
- Input Validation - Comprehensive validation with meaningful error messages
- OpenAPI Documentation - Interactive API docs with Swagger UI
- Comprehensive Testing - Unit, integration, and mock tests
- Docker Ready - Complete containerized development environment
- Automated Setup - Makefile automation for development workflow

## Technology Stack

- **Framework**: Spring Boot 3.5.7
- **Language**: Java 21
- **Database**: MySQL 8.0 with Liquibase migrations
- **Persistence**: Spring Data JPA + Hibernate
- **Validation**: Jakarta Bean Validation
- **API Docs**: OpenAPI 3.0 + Swagger UI
- **Build Tool**: Maven
- **Testing**: JUnit 5 + Mockito

## Documentation

- **Architecture Diagrams**: See `docs/diagrams/` for interactive class and sequence diagrams
- **API Documentation**: Available at `/swagger-ui.html` when running

## Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **Docker & Docker Compose**
- **Make** (Unix/Linux/macOS)

## Quick Start

### 1. Clone and Navigate
```bash
git clone <your-repo-url>
cd loan-calculator
```

### 2. Start Everything (Database + Application)
```bash
make
```
This command will:
- Start MySQL 8.0 in a Docker container
- Wait for database readiness
- Run the Spring Boot application on port 8090

### 3. Verify Installation
Once running, visit:
- **API**: http://localhost:8090/api/loans
- **API Documentation**: http://localhost:8090/swagger-ui.html

### 4. Test the API
Try the API with a simple curl command:

```bash
curl -X POST http://localhost:8090/api/loans \
  -H "Content-Type: application/json" \
  -d '{"amount": 10000.00, "annualInterestPercent": 5.0, "numberOfMonths": 12}'
```

For API documentation, visit: http://localhost:8090/swagger-ui.html

## Project Structure

```
loan-calculator/
├── src/
│   ├── main/
│   │   ├── java/com/example/loancalculator/
│   │   │   ├── controller/          # REST API endpoints
│   │   │   ├── service/             # Business logic
│   │   │   ├── model/               # JPA entities & DTOs
│   │   │   ├── mapper/              # Entity-DTO mapping
│   │   │   ├── repostiory/          # Data access layer
│   │   │   └── exception/           # Global exception handling
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/changelog/        # Liquibase migrations
│   └── test/                        # test suites
├── docker-compose.yml               # Database container
├── Makefile                         # Development automation
└── pom.xml                         # Maven configuration
```

## Development

### Available Make Commands

```bash
make                 # Start everything (default)
make docker-compose  # Start MySQL only
make wait_for_db     # Start MySQL and wait for readiness
make clean          # Stop containers
make clean_volumes  # Stop containers and remove data
make help           # Show all available commands
```

### Manual Setup (Alternative)

1. **Start Database**:
   ```bash
   docker-compose up -d
   ```

2. **Wait for Database**:
   ```bash
   # Wait manually or check with:
   docker-compose exec mysql mysqladmin ping -h "127.0.0.1" -uroot -ptest123
   ```

3. **Run Application**:
   ```bash
   mvn spring-boot:run
   ```

### Configuration

**Database Connection** (application.properties):
- Host: localhost:3307
- Database: loan_calculator
- Username: root
- Password: test123

## Database Schema

The application uses Liquibase for database versioning:

- **`loans`**: Stores loan calculation metadata
- **`installments`**: Detailed payment schedule (one-to-many relationship)

Schema automatically created/managed through Liquibase migrations.
