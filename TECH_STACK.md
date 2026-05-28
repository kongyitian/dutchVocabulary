# Technology Stack & Architecture

This document provides a comprehensive overview of the technologies, patterns, and techniques used in the Dutch Vocabulary Learning Application.

---

## Backend (Java/Spring Boot)

### Core Framework
| Category | Technologies |
|----------|-------------|
| **Framework** | Spring Boot 3.2.5, Spring MVC |
| **Security** | Spring Security, JWT (JSON Web Tokens) for authentication |
| **Database Layer** | Spring Data JPA, Hibernate ORM |
| **Databases** | H2 (in-memory for development), PostgreSQL (production) |
| **Messaging** | Spring Kafka (optional event streaming) |
| **Testing** | JUnit 5, Testcontainers, Spring Test, Awaitility |
| **Build Tool** | Maven with Maven Wrapper |

---

## Frontend

### UI Technologies
| Category | Technologies |
|----------|-------------|
| **Static UI** | Vanilla HTML/CSS/JavaScript (served from Spring Boot) |
| **React Application** | React 18, TypeScript (in `/frontend` folder) |
| **Styling** | Custom CSS with CSS Variables, Duolingo-inspired design |
| **API Communication** | Fetch API with JWT Bearer tokens |

---

## Architecture Patterns

The application follows modern software architecture principles:

- **RESTful API** - Clean endpoint design (`/api/words`, `/api/quiz`, `/api/auth`)
- **DTO Pattern** - Separate Data Transfer Objects for API requests/responses
- **Repository Pattern** - Spring Data JPA repositories with custom queries
- **Service Layer** - Business logic encapsulated in dedicated service classes
- **Builder Pattern** - Lombok `@Builder` for fluent object construction
- **Event-Driven Architecture** (optional) - Kafka events for analytics and system decoupling

---

## Key Features Implementation

### Authentication & Authorization
- **Technique**: JWT tokens stored in `localStorage`
- **Security**: Bearer authentication headers
- **Framework**: Spring Security with custom JWT filters

### Quiz System
- **Question Generation**: Multiple-choice with shuffled answer options
- **Learning Algorithm**: Spaced repetition algorithm
- **Difficulty Adjustment**: Adaptive based on user performance

### Progress Tracking
- **Metrics**: Per-word statistics (attempts, correct count, streak)
- **Persistence**: JPA entities with automatic timestamp tracking
- **Analytics**: Real-time progress calculation

### Daily Streaks
- **Implementation**: Date-based tracking with streak continuation logic
- **Reset Logic**: Automatic streak reset after missed days
- **Motivation**: Gamification to encourage daily practice

### Achievements System
- **Architecture**: Event-driven badge system
- **Triggers**: Milestone-based achievement unlocking
- **Notifications**: Real-time achievement notifications

---

## DevOps & Infrastructure

### Containerization
- **Docker Compose** - Multi-container setup for PostgreSQL, Kafka, and Zookeeper
- **Profiles** - Environment-specific configurations (dev, postgres profile for production)
- **Testcontainers** - Docker-based integration testing with real database instances

### Configuration Management
- **Application Profiles**: `application.properties`, `application-postgres.properties`
- **Environment Variables**: Docker Compose environment configuration
- **Property Management**: Spring's externalized configuration

---

## Code Quality & Best Practices

### Code Enhancement
- **Lombok** - Reduces boilerplate code
  - `@Data` - Generates getters, setters, toString, equals, hashCode
  - `@Builder` - Implements builder pattern
  - `@RequiredArgsConstructor` - Constructor injection
  - `@Slf4j` - Logger field injection

### Validation
- **Jakarta Bean Validation** - Declarative validation with annotations
  - `@NotNull` - Null checks
  - `@Valid` - Nested object validation
  - `@Size`, `@Min`, `@Max` - Value constraints

### Logging
- **SLF4J** - Simple Logging Facade for Java
- **Lombok Integration** - `@Slf4j` annotation for automatic logger injection

### Resilience
- **Graceful Degradation** - Application works without Kafka (optional feature)
- **Error Handling** - Comprehensive exception handling with meaningful error responses
- **Transaction Management** - Spring's `@Transactional` for data consistency

---

## Testing Strategy

### Test Layers
1. **Unit Tests** - JUnit 5 for isolated component testing
2. **Integration Tests** - Spring Test with test database
3. **E2E Tests** - Full user journey testing with Testcontainers
4. **Asynchronous Testing** - Awaitility for event-driven components

### Test Infrastructure
- **Testcontainers** - Real PostgreSQL and Kafka instances for integration tests
- **Spring Test** - Application context loading and MockMvc for API testing
- **Test Profiles** - Separate configurations for test environments

---

## API Design

### Endpoint Structure
```
/api/auth           - Authentication endpoints (login, register)
/api/words          - Word management (CRUD operations)
/api/quiz           - Quiz generation and submission
/api/progress       - Learning progress tracking
/api/achievements   - User achievements and badges
```

### Request/Response Format
- **Content Type**: `application/json`
- **Authentication**: Bearer token in Authorization header
- **Error Format**: Standardized error responses with status codes

---

## Database Schema

### Entity Relationships
- **User** - Core user entity with authentication details
- **Word** - Dutch vocabulary words with translations
- **LearningProgress** - User-word relationship tracking progress
- **QuizAttempt** - Historical quiz attempts
- **Achievement** - Badge definitions and user achievements

### Database Technologies
- **Development**: H2 in-memory database for rapid development
- **Production**: PostgreSQL for reliability and scalability
- **ORM**: Hibernate with JPA annotations

---

## Security Implementation

### Authentication Flow
1. User submits credentials to `/api/auth/login`
2. Server validates credentials and generates JWT token
3. Client stores token in `localStorage`
4. Subsequent requests include token in `Authorization: Bearer <token>` header
5. Server validates token on each request

### Security Features
- **Password Encryption** - BCrypt password encoding
- **JWT Tokens** - Stateless authentication with expiration
- **CORS Configuration** - Controlled cross-origin resource sharing
- **SQL Injection Prevention** - Parameterized queries via JPA

---

## Build & Deployment

### Build Process
- **Maven Wrapper** - Ensures consistent Maven version across environments
- **Multi-Stage Build** - Separate compilation and runtime phases
- **Profile Activation** - Environment-specific builds

### Deployment Options
- **Standalone JAR** - Self-contained executable with embedded Tomcat
- **Docker Compose** - Multi-container deployment with dependencies
- **Cloud Ready** - 12-factor app principles for cloud deployment

---

## Development Workflow

### Local Development
1. Start infrastructure: `docker-compose up -d`
2. Run backend: `./mvnw spring-boot:run`
3. Run frontend: `cd frontend && npm start`
4. Access application at `http://localhost:8080`

### Testing
```bash
./mvnw test                    # Run all tests
./mvnw test -Dtest=ClassName   # Run specific test
./mvnw verify                  # Run integration tests
```

### Building
```bash
./mvnw clean package           # Build JAR
./mvnw clean install           # Build and install to local repo
```

---

## Performance Optimizations

- **Connection Pooling** - HikariCP for database connections
- **Lazy Loading** - JPA lazy fetching strategies
- **Caching** - Spring Cache abstraction (optional)
- **Async Processing** - Kafka for non-blocking operations
- **Database Indexing** - Optimized queries with proper indexes

---

## Future Enhancements

- **Metrics & Monitoring** - Spring Boot Actuator, Micrometer
- **API Documentation** - Swagger/OpenAPI specification
- **Caching Layer** - Redis for session management
- **CDN Integration** - Frontend asset delivery
- **Microservices** - Service decomposition for scalability

---

*Last Updated: May 28, 2026*

