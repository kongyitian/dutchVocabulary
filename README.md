# Dutch Vocabulary Learning Application

A Spring Boot REST API for learning Dutch vocabulary with English translations.

## Features

- **Vocabulary Management**: Add, update, delete, and search Dutch words
- **Quiz System**: Practice with random quizzes or smart quizzes (spaced repetition)
- **Progress Tracking**: Track your learning progress and statistics
- **Categories**: Words organized by categories (greetings, verbs, nouns, etc.)
- **Difficulty Levels**: EASY, MEDIUM, HARD

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Run the Application

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

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

- [ ] Add user authentication (Spring Security)
- [ ] Add frontend (React/Vue/Thymeleaf)
- [ ] Implement audio pronunciation
- [ ] Add more vocabulary categories
- [ ] Export/import vocabulary lists

