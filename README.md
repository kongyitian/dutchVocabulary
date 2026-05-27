# Dutch Vocabulary Learning Application

A Spring Boot REST API for learning Dutch vocabulary with English translations, featuring Kafka event streaming for analytics and achievements.

## Features

- **Vocabulary Management**: Add, update, delete, and search Dutch words
- **Quiz System**: Practice with random quizzes or smart quizzes (spaced repetition)
- **Progress Tracking**: Track your learning progress and statistics
- **Categories**: Words organized by categories (greetings, verbs, nouns, etc.)
- **Difficulty Levels**: EASY, MEDIUM, HARD
- **Web Frontend**: Built-in responsive Duolingo-style web UI
- **Achievements System**: Earn badges for streaks and milestones
- **Daily Streaks**: Track consecutive days of practice
- **User Authentication**: JWT-based authentication
- **Kafka Integration**: Event streaming for quiz analytics (optional)

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker (for PostgreSQL and Kafka)

### Run with Docker (PostgreSQL)

```bash
# Start PostgreSQL (and optionally Kafka)
docker-compose up -d postgres

# Run the application with PostgreSQL profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

### Run with H2 (Development)

```bash
# Run the application with H2 in-memory database (default)
./mvnw spring-boot:run
```

### Run Kafka (Optional)

```bash
# Start Kafka and Zookeeper for event streaming
docker-compose up -d kafka zookeeper

# Verify containers are running
docker ps
```

The application will be available at:
- **Web UI**: http://localhost:8080
- **API**: http://localhost:8080/api

## Web Interface

Simply open http://localhost:8080 in your browser to access the interactive Dutch vocabulary learning app with:

- 📊 **Dashboard** - View your learning statistics and daily streak
- 🏆 **Achievements** - Earn badges for milestones (5-streak, 10-streak, word mastery, etc.)
- 🔥 **Daily Streaks** - Track consecutive days of practice
- 🎯 **Practice** - Take quizzes (random or smart/spaced repetition)
- 📚 **Word List** - Browse and search all vocabulary words

## Running Tests

```bash
# Run all tests
./mvnw test

# Run tests with coverage report
./mvnw test jacoco:report
```

## Database Configuration

### H2 (Development - Default)
Data stored in memory, lost on restart.

Access H2 Console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:dutchvocabulary`
- Username: `sa`
- Password: (leave empty)

### PostgreSQL (Production)
For production, use the `postgres` profile:

```bash
# Using environment variables
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=dutchvocabulary
export DB_USERNAME=postgres
export DB_PASSWORD=postgres

./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

Or start PostgreSQL with Docker:
```bash
docker-compose up -d postgres
```

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and get JWT token |
| GET | `/api/auth/me` | Get current user info |

### Vocabulary Words

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/words` | Get all vocabulary words |
| GET | `/api/words/{id}` | Get a word by ID |
| GET | `/api/words/category/{category}` | Get words by category |
| GET | `/api/words/difficulty/{difficulty}` | Get words by difficulty |
| GET | `/api/words/categories` | Get all categories |
| GET | `/api/words/search?q={term}` | Search words |
| POST | `/api/words` | Create a new word |
| PUT | `/api/words/{id}` | Update a word |
| DELETE | `/api/words/{id}` | Delete a word |

### Quiz

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/quiz?count=10` | Generate a random quiz |
| GET | `/api/quiz?count=10&category=verbs` | Quiz with category filter |
| GET | `/api/quiz/smart?count=10` | Smart quiz (focuses on weak words) |
| POST | `/api/quiz/answer` | Submit a single answer |
| POST | `/api/quiz/submit` | Submit multiple answers |
| GET | `/api/quiz/statistics` | Get learning statistics |

### Achievements

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/achievements` | Get user achievements |
| GET | `/api/achievements/streak` | Get daily streak info |
| GET | `/api/achievements/summary` | Get achievement summary |

## Project Structure

```
src/main/java/com/dutchvocabulary/
├── DutchVocabularyApplication.java
├── controller/
│   ├── AuthController.java
│   ├── VocabularyController.java
│   ├── QuizController.java
│   └── AchievementController.java
├── service/
│   ├── VocabularyService.java
│   ├── QuizService.java
│   └── AchievementService.java
├── repository/
│   ├── VocabularyWordRepository.java
│   ├── LearningProgressRepository.java
│   ├── UserRepository.java
│   └── AchievementRepository.java
├── model/
│   ├── VocabularyWord.java
│   ├── LearningProgress.java
│   ├── User.java
│   ├── Achievement.java
│   └── DailyStreak.java
├── security/
│   ├── SecurityConfig.java
│   ├── JwtUtils.java
│   └── JwtAuthenticationFilter.java
├── kafka/
│   └── QuizEventProducer.java
└── dto/
    └── ... (DTOs)
```

## Kafka Events

When users answer quiz questions, events are published to Kafka topics:

### Topics
| Topic | Description |
|-------|-------------|
| `quiz-attempts` | Every quiz answer (correct/incorrect, streak, success rate) |
| `achievements` | Achievement unlocks (FIRST_CORRECT, STREAK_5, STREAK_10, WORD_MASTERED) |

### Running Without Kafka
The app will still work without Kafka - events will simply not be published (with a warning log).

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | PostgreSQL host | localhost |
| `DB_PORT` | PostgreSQL port | 5432 |
| `DB_NAME` | Database name | dutchvocabulary |
| `DB_USERNAME` | Database username | postgres |
| `DB_PASSWORD` | Database password | postgres |
| `JWT_SECRET` | JWT signing key | (hardcoded for dev) |

## License

MIT License
