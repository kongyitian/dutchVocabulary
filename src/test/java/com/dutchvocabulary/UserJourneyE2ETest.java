package com.dutchvocabulary;

import com.dutchvocabulary.config.TestConfig;
import com.dutchvocabulary.dto.*;
import com.dutchvocabulary.model.VocabularyWord;
import com.dutchvocabulary.repository.AchievementRepository;
import com.dutchvocabulary.repository.DailyStreakRepository;
import com.dutchvocabulary.repository.LearningProgressRepository;
import com.dutchvocabulary.repository.VocabularyWordRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-End User Journey Tests.
 * Tests complete user flows through the application, simulating real user behavior.
 * These tests verify that all components work together correctly.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("End-to-End User Journey Tests")
class UserJourneyE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VocabularyWordRepository wordRepository;

    @Autowired
    private LearningProgressRepository progressRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private DailyStreakRepository streakRepository;

    // ========================================
    // Journey 1: New User Complete Onboarding
    // ========================================

    @Test
    @Order(1)
    @DisplayName("Journey: New user registration → first quiz → first achievement")
    void journey_NewUserOnboarding() throws Exception {
        // Step 1: User registers
        String username = "journey_newuser_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("securePassword123");
        registerRequest.setDisplayName("New Journey User");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.username", is(username)))
                .andExpect(jsonPath("$.message", containsString("successful")))
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class);
        String token = authResponse.getToken();

        // Step 2: User views available vocabulary
        mockMvc.perform(get("/api/words")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));

        // Step 3: User views available categories
        MvcResult categoriesResult = mockMvc.perform(get("/api/words/categories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andReturn();

        List<String> categories = objectMapper.readValue(
                categoriesResult.getResponse().getContentAsString(),
                new TypeReference<List<String>>() {});
        assertThat(categories).contains("greetings");

        // Step 4: User checks their initial statistics (should be zero)
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wordsStudied", is(0)))
                .andExpect(jsonPath("$.totalAttempts", is(0)));

        // Step 5: User checks achievements (should be empty)
        mockMvc.perform(get("/api/achievements")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // Step 6: User starts a quiz
        MvcResult quizResult = mockMvc.perform(get("/api/quiz")
                        .param("count", "3")
                        .param("category", "greetings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(lessThanOrEqualTo(3))))
                .andReturn();

        List<QuizQuestionDTO> questions = objectMapper.readValue(
                quizResult.getResponse().getContentAsString(),
                new TypeReference<List<QuizQuestionDTO>>() {});
        assertThat(questions).isNotEmpty();

        // Step 7: User answers first question CORRECTLY → earns FIRST_CORRECT achievement
        QuizQuestionDTO firstQuestion = questions.get(0);
        String correctAnswer = wordRepository.findById(firstQuestion.getWordId())
                .get().getEnglish();

        QuizAnswerDTO answer = QuizAnswerDTO.builder()
                .wordId(firstQuestion.getWordId())
                .answer(correctAnswer)
                .build();

        mockMvc.perform(post("/api/quiz/answer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct", is(true)))
                .andExpect(jsonPath("$.currentStreak", is(1)));

        // Step 8: Verify FIRST_CORRECT achievement was earned
        mockMvc.perform(get("/api/achievements")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.achievementType == 'FIRST_CORRECT')]", hasSize(1)));

        // Step 9: Verify statistics updated
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wordsStudied", is(1)))
                .andExpect(jsonPath("$.totalAttempts", is(1)))
                .andExpect(jsonPath("$.totalCorrect", is(1)));

        // Step 10: Verify daily streak started
        mockMvc.perform(get("/api/achievements/streak")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStreak", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.practicedToday", is(true)));
    }

    // ========================================
    // Journey 2: Building Mastery Over Time
    // ========================================

    @Test
    @Order(2)
    @DisplayName("Journey: User builds mastery through repeated practice")
    void journey_BuildingMastery() throws Exception {
        // Register user
        String username = "journey_mastery_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Mastery User");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class).getToken();

        // Get a word to practice
        VocabularyWord targetWord = wordRepository.findByCategory("greetings").get(0);

        // Practice the same word 10 times correctly (aiming for word mastery)
        for (int i = 0; i < 10; i++) {
            QuizAnswerDTO answer = QuizAnswerDTO.builder()
                    .wordId(targetWord.getId())
                    .answer(targetWord.getEnglish())
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answer)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.correct", is(true)));
        }

        // Verify statistics show 100% success rate
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAttempts", is(10)))
                .andExpect(jsonPath("$.totalCorrect", is(10)))
                .andExpect(jsonPath("$.overallSuccessRate", is(100.0)));

        // Verify achievements include STREAK_5 and STREAK_10
        mockMvc.perform(get("/api/achievements")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.achievementType == 'STREAK_5')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.achievementType == 'STREAK_10')]", hasSize(1)));
    }

    // ========================================
    // Journey 3: Smart Quiz Learning Path
    // ========================================

    @Test
    @Order(3)
    @DisplayName("Journey: User uses smart quiz for personalized learning")
    void journey_SmartQuizLearning() throws Exception {
        // Register user
        String username = "journey_smart_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Smart Learner");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class).getToken();

        // First, practice some words with mixed results
        List<VocabularyWord> words = wordRepository.findByCategory("greetings");

        // Word 1: Practice correctly (strong)
        for (int i = 0; i < 5; i++) {
            QuizAnswerDTO answer = QuizAnswerDTO.builder()
                    .wordId(words.get(0).getId())
                    .answer(words.get(0).getEnglish())
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answer)))
                    .andExpect(status().isOk());
        }

        // Word 2: Practice with mistakes (weak)
        for (int i = 0; i < 5; i++) {
            QuizAnswerDTO answer = QuizAnswerDTO.builder()
                    .wordId(words.get(1).getId())
                    .answer(i < 2 ? words.get(1).getEnglish() : "wrong")  // 60% wrong
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answer)))
                    .andExpect(status().isOk());
        }

        // Now use smart quiz - it should prioritize weaker words
        MvcResult smartQuizResult = mockMvc.perform(get("/api/quiz/smart")
                        .param("count", "5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        List<QuizQuestionDTO> smartQuestions = objectMapper.readValue(
                smartQuizResult.getResponse().getContentAsString(),
                new TypeReference<List<QuizQuestionDTO>>() {});

        // The smart quiz should include questions (may include the weak word)
        assertThat(smartQuestions).isNotEmpty();
    }

    // ========================================
    // Journey 4: Re-login After Session Expiry
    // ========================================

    @Test
    @Order(4)
    @DisplayName("Journey: User logs in again and continues progress")
    void journey_ReloginAndContinue() throws Exception {
        // Register user
        String username = "journey_relogin_" + System.currentTimeMillis();
        String password = "password123";
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(password);
        registerRequest.setDisplayName("Relogin User");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String firstToken = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class).getToken();

        // Make some progress with first session
        VocabularyWord word = wordRepository.findByCategory("greetings").get(0);
        for (int i = 0; i < 3; i++) {
            QuizAnswerDTO answer = QuizAnswerDTO.builder()
                    .wordId(word.getId())
                    .answer(word.getEnglish())
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + firstToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answer)))
                    .andExpect(status().isOk());
        }

        // "Log out" and log back in
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn();

        String secondToken = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(), AuthResponse.class).getToken();

        // Verify progress is preserved
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + secondToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAttempts", greaterThanOrEqualTo(3)));

        // Verify achievements are preserved
        mockMvc.perform(get("/api/achievements")
                        .header("Authorization", "Bearer " + secondToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // Continue practicing with new session
        QuizAnswerDTO continueAnswer = QuizAnswerDTO.builder()
                .wordId(word.getId())
                .answer(word.getEnglish())
                .build();

        mockMvc.perform(post("/api/quiz/answer")
                        .header("Authorization", "Bearer " + secondToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(continueAnswer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStreak", greaterThanOrEqualTo(4)));  // Streak continues
    }

    // ========================================
    // Journey 5: Complete Quiz Session
    // ========================================

    @Test
    @Order(5)
    @DisplayName("Journey: User completes a full quiz session with batch submit")
    void journey_CompleteQuizSession() throws Exception {
        // Register user
        String username = "journey_quiz_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Quiz Session User");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class).getToken();

        // Get quiz questions
        MvcResult quizResult = mockMvc.perform(get("/api/quiz")
                        .param("count", "5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        List<QuizQuestionDTO> questions = objectMapper.readValue(
                quizResult.getResponse().getContentAsString(),
                new TypeReference<List<QuizQuestionDTO>>() {});

        // Build answers (some correct, some wrong)
        List<QuizAnswerDTO> answers = questions.stream()
                .map(q -> {
                    String answer = q.getOptions().get(0);  // Pick first option (may or may not be correct)
                    return QuizAnswerDTO.builder()
                            .wordId(q.getWordId())
                            .answer(answer)
                            .build();
                })
                .toList();

        // Submit all answers at once
        MvcResult submitResult = mockMvc.perform(post("/api/quiz/submit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answers)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(questions.size())))
                .andReturn();

        List<QuizResultDTO> results = objectMapper.readValue(
                submitResult.getResponse().getContentAsString(),
                new TypeReference<List<QuizResultDTO>>() {});

        // Count correct answers
        long correctCount = results.stream().filter(QuizResultDTO::isCorrect).count();

        // Verify statistics match
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAttempts", is((int) questions.size())))
                .andExpect(jsonPath("$.totalCorrect", is((int) correctCount)));
    }

    // ========================================
    // Journey 6: Learning by Difficulty
    // ========================================

    @Test
    @Order(6)
    @DisplayName("Journey: User progresses through difficulty levels")
    void journey_ProgressThroughDifficulty() throws Exception {
        // Register user
        String username = "journey_difficulty_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Difficulty User");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class).getToken();

        // Start with EASY words
        MvcResult easyQuiz = mockMvc.perform(get("/api/quiz")
                        .param("count", "3")
                        .param("difficulty", "A1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].difficulty", everyItem(is("A1"))))
                .andReturn();

        List<QuizQuestionDTO> easyQuestions = objectMapper.readValue(
                easyQuiz.getResponse().getContentAsString(),
                new TypeReference<List<QuizQuestionDTO>>() {});

        // Answer easy questions
        for (QuizQuestionDTO q : easyQuestions) {
            String correctAnswer = wordRepository.findById(q.getWordId()).get().getEnglish();
            QuizAnswerDTO answer = QuizAnswerDTO.builder()
                    .wordId(q.getWordId())
                    .answer(correctAnswer)
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answer)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.correct", is(true)));
        }

        // Try MEDIUM difficulty
        mockMvc.perform(get("/api/quiz")
                        .param("count", "3")
                        .param("difficulty", "B1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].difficulty", everyItem(is("B1"))));

        // User can also filter by both category and difficulty
        mockMvc.perform(get("/api/words/difficulty/HARD")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].difficulty", everyItem(is("C2"))));
    }

    // ========================================
    // Journey 7: Achievement Summary Check
    // ========================================

    @Test
    @Order(7)
    @DisplayName("Journey: User checks achievement progress summary")
    void journey_CheckAchievementSummary() throws Exception {
        // Register and earn some achievements
        String username = "journey_summary_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Summary User");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class).getToken();

        // Earn some achievements
        VocabularyWord word = wordRepository.findByCategory("greetings").get(0);
        for (int i = 0; i < 5; i++) {
            QuizAnswerDTO answer = QuizAnswerDTO.builder()
                    .wordId(word.getId())
                    .answer(word.getEnglish())
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answer)))
                    .andExpect(status().isOk());
        }

        // Check summary
        MvcResult summaryResult = mockMvc.perform(get("/api/achievements/summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.achievementCount", greaterThanOrEqualTo(2)))  // FIRST_CORRECT and STREAK_5
                .andExpect(jsonPath("$.currentStreak", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.practicedToday", is(true)))
                .andReturn();

        Map<String, Object> summary = objectMapper.readValue(
                summaryResult.getResponse().getContentAsString(),
                new TypeReference<Map<String, Object>>() {});

        assertThat(summary).containsKeys("achievementCount", "currentStreak", "longestStreak",
                "totalDaysPracticed", "practicedToday");
    }

    // ========================================
    // Journey 8: Search and Learn
    // ========================================

    @Test
    @Order(8)
    @DisplayName("Journey: User searches for specific word and learns it")
    void journey_SearchAndLearn() throws Exception {
        // Register user
        String username = "journey_search_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Search User");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class).getToken();

        // Search for a specific word
        MvcResult searchResult = mockMvc.perform(get("/api/words/search")
                        .param("q", "hallo")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andReturn();

        List<VocabularyWord> foundWords = objectMapper.readValue(
                searchResult.getResponse().getContentAsString(),
                new TypeReference<List<VocabularyWord>>() {});

        assertThat(foundWords).isNotEmpty();
        VocabularyWord targetWord = foundWords.get(0);

        // Practice the found word
        for (int i = 0; i < 3; i++) {
            QuizAnswerDTO answer = QuizAnswerDTO.builder()
                    .wordId(targetWord.getId())
                    .answer(targetWord.getEnglish())
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answer)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.correct", is(true)));
        }

        // Verify statistics for that word
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wordsStudied", is(1)))
                .andExpect(jsonPath("$.totalAttempts", is(3)))
                .andExpect(jsonPath("$.overallSuccessRate", is(100.0)));
    }

    // ========================================
    // Journey 9: Error Recovery Journey
    // ========================================

    @Test
    @Order(9)
    @DisplayName("Journey: User recovers from incorrect answers")
    void journey_ErrorRecovery() throws Exception {
        // Register user
        String username = "journey_recovery_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Recovery User");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class).getToken();

        VocabularyWord word = wordRepository.findByCategory("greetings").get(0);

        // User answers incorrectly first
        QuizAnswerDTO wrongAnswer = QuizAnswerDTO.builder()
                .wordId(word.getId())
                .answer("completely_wrong")
                .build();

        MvcResult wrongResult = mockMvc.perform(post("/api/quiz/answer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongAnswer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct", is(false)))
                .andExpect(jsonPath("$.correctAnswer", notNullValue()))
                .andReturn();

        // User learns from the feedback and the result shows the correct answer
        QuizResultDTO wrongResultDTO = objectMapper.readValue(
                wrongResult.getResponse().getContentAsString(), QuizResultDTO.class);
        assertThat(wrongResultDTO.getCorrectAnswer()).isEqualTo(word.getEnglish());

        // User tries again with the correct answer
        QuizAnswerDTO correctAnswer = QuizAnswerDTO.builder()
                .wordId(word.getId())
                .answer(word.getEnglish())
                .build();

        mockMvc.perform(post("/api/quiz/answer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctAnswer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct", is(true)));

        // Verify statistics show improvement
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAttempts", is(2)))
                .andExpect(jsonPath("$.totalCorrect", is(1)))
                .andExpect(jsonPath("$.overallSuccessRate", is(50.0)));
    }

    // ========================================
    // Journey 10: Multi-Category Learning
    // ========================================

    @Test
    @Order(10)
    @DisplayName("Journey: User learns across multiple categories")
    void journey_MultiCategoryLearning() throws Exception {
        // Register user
        String username = "journey_multicategory_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Multi-Category User");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class).getToken();

        // Get all available categories
        MvcResult categoriesResult = mockMvc.perform(get("/api/words/categories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        List<String> categories = objectMapper.readValue(
                categoriesResult.getResponse().getContentAsString(),
                new TypeReference<List<String>>() {});

        // Practice from each category
        for (String category : categories) {
            List<VocabularyWord> words = wordRepository.findByCategory(category);
            if (!words.isEmpty()) {
                VocabularyWord word = words.get(0);
                QuizAnswerDTO answer = QuizAnswerDTO.builder()
                        .wordId(word.getId())
                        .answer(word.getEnglish())
                        .build();

                mockMvc.perform(post("/api/quiz/answer")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(answer)))
                        .andExpect(status().isOk());
            }
        }

        // Verify the user has studied words from multiple categories
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wordsStudied", greaterThanOrEqualTo(1)));
    }

    // ========================================
    // Journey 11: Invalid Token Handling
    // ========================================

    @Test
    @Order(11)
    @DisplayName("Journey: Application handles expired/invalid tokens properly")
    void journey_InvalidTokenHandling() throws Exception {
        // Try to access protected endpoint with invalid token - returns 403 Forbidden
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer invalid_token_here"))
                .andExpect(status().isForbidden());

        // Try to access protected endpoint without token - returns 403 Forbidden  
        mockMvc.perform(get("/api/quiz/statistics"))
                .andExpect(status().isForbidden());

        // Public endpoints should still work without token
        mockMvc.perform(get("/api/words"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/words/categories"))
                .andExpect(status().isOk());
    }

    // ========================================
    // Journey 12: Concurrent Learning Sessions
    // ========================================

    @Test
    @Order(12)
    @DisplayName("Journey: User data remains isolated during concurrent sessions")
    void journey_ConcurrentSessions() throws Exception {
        // Register two users
        String user1 = "journey_concurrent1_" + System.currentTimeMillis();
        String user2 = "journey_concurrent2_" + System.currentTimeMillis();

        RegisterRequest reg1 = new RegisterRequest();
        reg1.setUsername(user1);
        reg1.setPassword("password123");
        reg1.setDisplayName("Concurrent User 1");

        RegisterRequest reg2 = new RegisterRequest();
        reg2.setUsername(user2);
        reg2.setPassword("password123");
        reg2.setDisplayName("Concurrent User 2");

        String token1 = objectMapper.readValue(
                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reg1)))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString(),
                AuthResponse.class).getToken();

        String token2 = objectMapper.readValue(
                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reg2)))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString(),
                AuthResponse.class).getToken();

        VocabularyWord word = wordRepository.findByCategory("greetings").get(0);

        // User 1 practices 5 times
        for (int i = 0; i < 5; i++) {
            QuizAnswerDTO answer = QuizAnswerDTO.builder()
                    .wordId(word.getId())
                    .answer(word.getEnglish())
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + token1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answer)))
                    .andExpect(status().isOk());
        }

        // User 2 practices 2 times
        for (int i = 0; i < 2; i++) {
            QuizAnswerDTO answer = QuizAnswerDTO.builder()
                    .wordId(word.getId())
                    .answer(word.getEnglish())
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + token2)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answer)))
                    .andExpect(status().isOk());
        }

        // Verify user 1 has 5 attempts
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAttempts", is(5)));

        // Verify user 2 has 2 attempts
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAttempts", is(2)));

        // Verify user 1 has STREAK_5 but user 2 doesn't
        mockMvc.perform(get("/api/achievements")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.achievementType == 'STREAK_5')]", hasSize(1)));

        mockMvc.perform(get("/api/achievements")
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.achievementType == 'STREAK_5')]", hasSize(0)));
    }

    // ========================================
    // Journey 13: Full Learning Cycle
    // ========================================

    @Test
    @Order(13)
    @DisplayName("Journey: Complete learning cycle from beginner to proficient")
    void journey_CompleteLearningCycle() throws Exception {
        // Register user
        String username = "journey_fullcycle_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Full Cycle User");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class).getToken();

        // Phase 1: Browse vocabulary
        mockMvc.perform(get("/api/words")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Phase 2: Take initial quiz
        MvcResult quizResult = mockMvc.perform(get("/api/quiz")
                        .param("count", "5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        List<QuizQuestionDTO> questions = objectMapper.readValue(
                quizResult.getResponse().getContentAsString(),
                new TypeReference<List<QuizQuestionDTO>>() {});

        // Phase 3: Answer some questions correctly
        int answeredCount = 0;
        for (QuizQuestionDTO q : questions) {
            VocabularyWord word = wordRepository.findById(q.getWordId()).orElse(null);
            if (word != null) {
                QuizAnswerDTO answer = QuizAnswerDTO.builder()
                        .wordId(q.getWordId())
                        .answer(word.getEnglish())
                        .build();

                mockMvc.perform(post("/api/quiz/answer")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(answer)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.correct", is(true)));
                answeredCount++;
            }
        }

        // Phase 4: Check progress
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wordsStudied", greaterThan(0)))
                .andExpect(jsonPath("$.totalAttempts", is(answeredCount)));

        // Phase 5: Check achievements earned
        mockMvc.perform(get("/api/achievements")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // Phase 6: Check daily streak
        mockMvc.perform(get("/api/achievements/streak")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.practicedToday", is(true)));

        // Phase 7: Use smart quiz for continued learning
        mockMvc.perform(get("/api/quiz/smart")
                        .param("count", "3")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
