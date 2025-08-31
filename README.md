# Wallet & Settlement Microservice

A comprehensive Spring Boot microservice for managing customer wallet balances and daily reconciliation with external providers. This system enables customers to maintain credit balances for consuming third-party services like CRB, credit scoring, and KYC document verification.

## 📋 Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Quick Start with Docker](#quick-start-with-docker)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Development Setup](#development-setup)
- [Testing](#testing)

## ✨ Features

### Wallet Management
- **Balance Management**: Track customer wallet balances with full transaction history
- **Top-up Operations**: Secure balance increase functionality
- **Consumption Control**: Balance deduction with insufficient funds protection
- **Transaction Ledger**: Complete audit trail of all wallet operations
- **Idempotency**: Protection against double deductions on retries

### Settlement & Reconciliation
- **Daily Reconciliation**: Automated comparison between internal and external transactions
- **Mismatch Detection**: Identifies missing transactions and amount discrepancies
- **Report Generation**: CSV export capabilities for reconciliation reports
- **External Provider Integration**: Support for CSV/JSON transaction reports

### Infrastructure
- **Message Queuing**: RabbitMQ integration for asynchronous transaction processing
- **Database**: PostgreSQL with JPA/Hibernate for data persistence
- **API Documentation**: Swagger/OpenAPI integration

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Spring Boot   │    │   PostgreSQL    │    │   RabbitMQ      │
│   Application   │◄──►│   Database      │    │   Message       │
│   (Port 8065)   │    │   (Port 5432)   │    │   Broker        │
└─────────────────┘    └─────────────────┘    │   (Port 5672)   │
                                              └─────────────────┘
```

**Key Components:**
- **Wallet Module**: Balance management and transaction processing
- **Reconciliation Module**: Daily settlement and mismatch detection
- **Ledger System**: Complete transaction audit trail
- **Queue Processing**: Asynchronous transaction handling

## 🛠️ Prerequisites

- Docker & Docker Compose
- Git
- Java 17+ (for local development)
- Gradle 7+ (for local development)

## 🚀 Quick Start with Docker

### 1. Clone and Setup
```bash
git clone <repository-url>
cd wallet-app
chmod +x setup.sh && ./setup.sh
```

### 2. Start All Services
```bash
docker-compose up --build
```

### 3. Access the Application
- **Wallet API**: http://localhost:8065
- **Swagger UI**: http://localhost:8065/swagger-ui.html
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)

## 📚 API Documentation

**All API documentation is available through Swagger UI**: http://localhost:8065/swagger-ui.html

Swagger provides:
- **Complete API Reference**: All endpoints with detailed descriptions
- **Interactive Testing**: Test APIs directly from the browser interface
- **Request/Response Schemas**: Full data models with validation rules
- **Real-time Examples**: Live request/response examples
- **Authentication Details**: Security requirements for each endpoint

### Key API Endpoints

- **Wallet Top-up**: `POST /wallets/{customerId}/topup`
- **Balance Consumption**: `POST /wallets/{customerId}/consume`
- **Balance Inquiry**: `GET /wallets/{customerId}/balance`
- **Reconciliation Report**: `GET /api/v1/reconciliation/report`
- **Export Report**: `GET /api/v1/reconciliation/export`

> 💡 **Primary Documentation**: Use Swagger UI for complete API documentation, testing, and examples. All endpoint details, schemas, and validation rules are documented there.

## ⚙️ Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | `jdbc:postgresql://localhost:5432/wallet` | Database connection URL |
| `DB_USERNAME` | `zackius` | Database username |
| `DB_PASSWORD` | `zackius` | Database password |
| `SPRING_RABBITMQ_HOST` | `localhost` | RabbitMQ host |
| `SPRING_RABBITMQ_PORT` | `5672` | RabbitMQ port |
| `SPRING_RABBITMQ_USERNAME` | `guest` | RabbitMQ username |
| `SPRING_RABBITMQ_PASSWORD` | `guest` | RabbitMQ password |
| `RABBIT_QUEUES_COLLECTION_LEDGER_REQUESTS` | `collection.ledger.request.v1` | Collection queue name |
| `RABBIT_QUEUES_SPENDING_LEDGER_REQUESTS` | `spending.ledger.request.v1` | Spending queue name |

### Application Profiles

- **local**: For local development
- **docker**: For containerized deployment
- **test**: For unit testing

## 🔧 Development Setup

### Local Development (without Docker)

1. **Start Dependencies:**
   ```bash
   # PostgreSQL
   docker run -d --name postgres -e POSTGRES_DB=wallet -e POSTGRES_USER=zackius -e POSTGRES_PASSWORD=zackius -p 5432:5432 postgres:15-alpine
   
   # RabbitMQ
   docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management-alpine
   ```

2. **Run Application:**
   ```bash
   ./gradlew bootRun
   ```

### Building the Application

```bash
# Build JAR
./gradlew clean build

# Run tests
./gradlew test

# Build Docker image
docker build -t wallet-app .
```

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
```

### Integration Tests
```bash
./gradlew integrationTest
```

### API Testing Examples

For quick testing without Swagger UI, you can use these curl commands:

**Test Top-up:**
```bash
curl -X POST http://localhost:8065/wallets/CUST001/topup \
  -H "Content-Type: application/json" \
  -d '{"amount": 100.00, "description": "Initial top-up"}'
```

**Test Consumption:**
```bash
curl -X POST http://localhost:8065/wallets/CUST001/consume \
  -H "Content-Type: application/json" \
  -d '{"amount": 25.00, "serviceType": "KYC_VERIFICATION", "description": "KYC document verification"}'
```

**Check Balance:**
```bash
curl http://localhost:8065/wallets/CUST001/balance
```

## 🗄️ Database Schema

### Core Tables
- **customers**: Customer information
- **collections**: Top-up transactions
- **spending**: Consumption transactions
- **reconciliation_reports**: Daily reconciliation results

### Key Features
- **ACID Transactions**: Ensures data consistency
- **Audit Trail**: Complete transaction history
- **Optimistic Locking**: Prevents concurrent modification issues

## 🔄 Message Queue Processing

### Queue Configuration
- **Collection Queue**: `collection.ledger.request.v1`
- **Spending Queue**: `spending.ledger.request.v1`

### Message Flow
1. API receives transaction request
2. Transaction validated and processed
3. Message queued for asynchronous processing
4. Ledger entries created
5. Balance updated atomically

## 🛡️ Security Features

- **Input Validation**: Comprehensive request validation
- **Transaction Integrity**: ACID compliance for all operations
- **Idempotency Keys**: Prevention of duplicate transactions
- **Balance Protection**: Insufficient funds validation

## 📁 Project Structure

```
wallet-app/
├── src/main/java/com/example/wallet_app/
│   ├── config/
│   │   ├── auth/WebSecurityConfiguration.java
│   │   ├── rabbitmq/RabbitMQConfig.java
│   │   └── swagger/SwaggerConfig.java
│   ├── controllers/
│   │   ├── CustomerController.java
│   │   └── CollectionController.java
│   ├── domain/
│   │   ├── customer/
│   │   └── wallet/
│   ├── persistence/
│   │   ├── customer/
│   │   ├── collection/
│   │   └── spending/
│   └── exceptions/
├── src/main/resources/
│   └── application.properties
├── docker-compose.yml
├── Dockerfile
└── README.md
```

## 🚀 Deployment

### Production Deployment
1. Update environment variables for production
2. Configure external database and message broker
3. Deploy using Docker Compose or Kubernetes

### Scaling Considerations
- **Database**: Configure connection pooling for high load
- **Message Processing**: Scale worker instances for queue processing
- **Load Balancing**: Use multiple application instances behind a load balancer

## 🐛 Troubleshooting

### Common Issues

**Queue Declaration Failures:**
```bash
# Restart with fresh volumes
docker-compose down -v
docker-compose up --build
```

**Database Connection Issues:**
- Verify PostgreSQL is running and accessible
- Check database credentials in environment variables

**RabbitMQ Connection Issues:**
- Ensure RabbitMQ management plugin is enabled
- Verify queue configuration in definitions.json

### Logs
```bash
# View application logs
docker-compose logs wallet-app

# View all services logs
docker-compose logs

# Follow logs in real-time
docker-compose logs -f
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Built with Spring Boot, PostgreSQL, RabbitMQ, Docker, and documented with Swagger** 🚀
