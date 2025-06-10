# Banking System Backend

A comprehensive banking system built with microservices architecture and Docker support.

## Table of Contents
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [Features](#features)
- [Getting Started](#getting-started)
- [Module Overview](#module-overview)
- [API Documentation](#api-documentation)
- [Database](#database)

## Technology Stack
- **Backend**: Java, Spring Boot
- **Security**: Spring Security (Basic Auth + Token Based)
- **Database**: PostgreSQL
- **DevOps**: Docker, Maven
- **Testing**: JUnit
- **Other Tools**: Liquibase, Fast2SMS
- **Architecture**: Microservices

## System Architecture
The system consists of 4 main modules:
1. BackOffice System (Admin Portal)
2. Online Banking (Customer Portal)
3. Transaction Scheduling System
4. BankData (Common Library)

## Features

### BackOffice System (Port: 8080)
- **Dual Role System**:
  - Capturer: Creates and manages customer accounts
  - Authorizer: Approves/declines account requests
- **Account Management**
- **Customer Verification**
- **Admin Dashboard**

### Online Banking System (Port: 8081)
- Secure customer login
- Account balance checking
- Transaction history
- Schedule future-dated transactions
- Fund transfers

### Transaction Scheduling System (Port: 8082)
- Automated batch processing
- Future-dated transaction handling
- Cron job (40-second intervals)
- Transaction logging

### BankData Module
- Common functionality library
- Shared data models
- Database operations
- Utility services

## Getting Started

### Prerequisites
- Java 11 or higher
- Docker
- PostgreSQL
- Maven

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/banking-system.git
```

2. Build the project:
```bash
mvn clean install
```

3. Start the services using Docker:
```bash
docker-compose up --build
```

### Access Points
- BackOffice System: http://localhost:8080
- Online Banking: http://localhost:8081
- Transaction System: http://localhost:8082
- PgAdmin: http://localhost:5050

## Database Access (PgAdmin)

### PgAdmin Credentials
- URL: http://localhost:5050
- Email: test@gmail.com
- Password: 1234

### Database Connection Details
- Hostname: host.docker.internal
- Port: 5432
- Username: postgres
- Database: banking_application
- Password: 1234

## Development

### Building Modules
```bash
cd BackOfficeSystem
mvn clean install

cd ../OnlineBanking
mvn clean install

cd ../TransactionScheduling
mvn clean install
```

### Running Tests
```bash
mvn test
```

### Docker Compose
Services are orchestrated using docker-compose:
```bash
docker-compose up --build    # Start all services
docker-compose down         # Stop all services
```

## Security
- Basic Authentication for initial login
- Token-based authentication for subsequent requests
- Encrypted password storage
- SMS-based credentials delivery

## Documentation
- API Documentation: `/swagger-ui.html`
- Database Schema: `/documentation/schema.md`

## Contributing
1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## Acknowledgments
- Spring Framework team
- PostgreSQL team
- Docker team