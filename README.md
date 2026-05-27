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
- **Kafka Integration**: Event streaming for quiz analytics (optional)

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker (for Kafka)

### Run Kafka with Docker

```bash
# Start Kafka and Zookeeper
docker-compose up -d

# Verify containers are running
docker ps
```

### Run the Application

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
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
- 🎯 **Practice** - Take quizzes (random or smart/spaced repetition)
- 📚 **Word List** - Browse and search all vocabulary words

![Dashboard](docs/dashboard.png)

## API Endpoints

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

## Example Usage

### Add a new word
```bash
curl -X POST http://localhost:8080/api/words \
  -H "Content-Type: application/json" \
  -d '{
    "dutch": "appel",
    "english": "apple",
    "category": "nouns",
    "difficulty": "EASY",
    "example": "Ik eet een appel.",
    "exampleTranslation": "I eat an apple."
  }'
```

### Get a quiz
```bash
curl http://localhost:8080/api/quiz?count=5
```

### Submit an answer
```bash
curl -X POST http://localhost:8080/api/quiz/answer \
  -H "Content-Type: application/json" \
  -d '{
    "wordId": 1,
    "answer": "hello"
  }'
```

### Get statistics
```bash
curl http://localhost:8080/api/quiz/statistics
```

## Database

The application uses H2 in-memory database for development. Data is persisted in `./data/dutchvocabulary`.

Access H2 Console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/dutchvocabulary`
- Username: `sa`
- Password: (leave empty)

## Project Structure

```
src/main/java/com/dutchvocabulary/
├── DutchVocabularyApplication.java
├── controller/
│   ├── VocabularyController.java
│   └── QuizController.java
├── service/
│   ├── VocabularyService.java
│   └── QuizService.java
├── repository/
│   ├── VocabularyWordRepository.java
│   └── LearningProgressRepository.java
├── model/
│   ├── VocabularyWord.java
│   ├── LearningProgress.java
│   └── Difficulty.java
└── dto/
    ├── VocabularyWordDTO.java
    ├── QuizQuestionDTO.java
    ├── QuizAnswerDTO.java
    ├── QuizResultDTO.java
    └── StatisticsDTO.java
```

## Next Steps

- [x] Add user authentication (Spring Security)
- [x] Add Kafka event streaming
- [ ] Implement audio pronunciation
- [ ] Add more vocabulary categories
- [ ] Export/import vocabulary lists

## Kafka Events

When users answer quiz questions, events are published to Kafka topics:

### Topics
| Topic | Description |
|-------|-------------|
| `quiz-attempts` | Every quiz answer (correct/incorrect, streak, success rate) |
| `achievements` | Achievement unlocks (FIRST_CORRECT, STREAK_5, STREAK_10, WORD_MASTERED) |

### Sample Log Output
```
📤 Sending quiz attempt event: userId=1, wordId=5, correct=true
📊 [Analytics] Quiz attempt received:
   User: john | Word: hallo (hello) | Answer: hello | Correct: ✅
   Streak: 5 | Success Rate: 85.0%
🏆 [Achievement] john earned: STREAK_5
   Message: Amazing! 5 correct answers in a row! 🔥
```

### Running Without Kafka
The app will still work without Kafka - events will simply not be published (with a warning log).

