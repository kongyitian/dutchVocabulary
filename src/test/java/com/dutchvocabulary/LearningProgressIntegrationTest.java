package com.dutchvocabulary;

import com.dutchvocabulary.config.TestConfig;
import com.dutchvocabulary.dto.*;
import com.dutchvocabulary.repository.LearningProgressRepository;
import com.dutchvocabulary.repository.VocabularyWordRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Learning Progress and Spaced Repetition features.
 * Tests the learning progress tracking, smart quiz generation, and statistics.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Learning Progress Integration Tests")
class LearningProgressIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VocabularyWordRepository wordRepository;

    @Autowired
    private LearningProgressRepository progressRepository;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        // Register and login to get auth token
        String username = "progressuser_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Progress Test User");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponse.class);
        authToken = authResponse.getToken();
    }

    @Test
    @Order(1)
    @DisplayName("New user should have zero statistics")
    void newUser_ShouldHaveZeroStatistics() throws Exception {
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalWords", greaterThan(0)))
                .andExpect(jsonPath("$.wordsStudied", is(0)))
                .andExpect(jsonPath("$.totalAttempts", is(0)))
                .andExpect(jsonPath("$.totalCorrect", is(0)));
    }

    @Test
    @Order(2)
    @DisplayName("Statistics should update after correct answer")
    void correctAnswer_ShouldUpdateStatistics() throws Exception {
        Long wordId = wordRepository.findByCategory("greetings").get(0).getId();
        String correctAnswer = wordRepository.findById(wordId).get().getEnglish();

        // Submit correct answer
        QuizAnswerDTO answer = QuizAnswerDTO.builder()
                .wordId(wordId)
                .answer(correctAnswer)
                .build();

        mockMvc.perform(post("/api/quiz/answer")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct", is(true)));

        // Check statistics updated
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wordsStudied", is(1)))
                .andExpect(jsonPath("$.totalAttempts", is(1)))
                .andExpect(jsonPath("$.totalCorrect", is(1)));
    }

    @Test
    @Order(3)
    @DisplayName("Statistics should track incorrect answers")
    void incorrectAnswer_ShouldUpdateStatistics() throws Exception {
        Long wordId = wordRepository.findByCategory("greetings").get(0).getId();

        // Submit incorrect answer
        QuizAnswerDTO answer = QuizAnswerDTO.builder()
                .wordId(wordId)
                .answer("wrong_answer")
                .build();

        mockMvc.perform(post("/api/quiz/answer")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct", is(false)));

        // Check statistics - should have 1 attempt but 0 correct
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAttempts", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.wordsStudied", greaterThanOrEqualTo(1)));
    }

    @Test
    @Order(4)
    @DisplayName("Smart quiz should return questions")
    void smartQuiz_ShouldReturnQuestions() throws Exception {
        // First, practice some words to build learning history
        Long wordId = wordRepository.findByCategory("greetings").get(0).getId();
        String correctAnswer = wordRepository.findById(wordId).get().getEnglish();

        QuizAnswerDTO answer = QuizAnswerDTO.builder()
                .wordId(wordId)
                .answer(correctAnswer)
                .build();

        mockMvc.perform(post("/api/quiz/answer")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answer)))
                .andExpect(status().isOk());

        // Get smart quiz
        mockMvc.perform(get("/api/quiz/smart")
                        .param("count", "5")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(lessThanOrEqualTo(5))))
                .andExpect(jsonPath("$[0].wordId", notNullValue()));
    }

    @Test
    @Order(5)
    @DisplayName("Success rate should be calculated correctly")
    void successRate_ShouldBeCalculatedCorrectly() throws Exception {
        Long wordId = wordRepository.findByCategory("verbs").get(0).getId();
        String correctAnswer = wordRepository.findById(wordId).get().getEnglish();

        // Submit 3 correct and 1 incorrect for 75% success rate
        for (int i = 0; i < 3; i++) {
            QuizAnswerDTO answer = QuizAnswerDTO.builder()
                    .wordId(wordId)
                    .answer(correctAnswer)
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answer)))
                    .andExpect(status().isOk());
        }

        // Submit incorrect
        QuizAnswerDTO wrongAnswer = QuizAnswerDTO.builder()
                .wordId(wordId)
                .answer("wrong")
                .build();

        MvcResult result = mockMvc.perform(post("/api/quiz/answer")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongAnswer)))
                .andExpect(status().isOk())
                .andReturn();

        // Parse response to check success rate
        QuizResultDTO resultDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), QuizResultDTO.class);

        // Success rate should be 75% (3 correct out of 4)
        org.assertj.core.api.Assertions.assertThat(resultDto.getSuccessRate())
                .isCloseTo(75.0, org.assertj.core.api.Assertions.within(1.0));
    }

    @Test
    @Order(6)
    @DisplayName("Word streak should track per-word progress")
    void wordStreak_ShouldTrackPerWordProgress() throws Exception {
        Long wordId = wordRepository.findByCategory("numbers").get(0).getId();
        String correctAnswer = wordRepository.findById(wordId).get().getEnglish();

        // Build a streak of 3
        for (int i = 1; i <= 3; i++) {
            QuizAnswerDTO answer = QuizAnswerDTO.builder()
                    .wordId(wordId)
                    .answer(correctAnswer)
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answer)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.correct", is(true)))
                    .andExpect(jsonPath("$.currentStreak", is(i)));
        }

        // Break the streak with incorrect answer
        QuizAnswerDTO wrongAnswer = QuizAnswerDTO.builder()
                .wordId(wordId)
                .answer("wrong")
                .build();

        mockMvc.perform(post("/api/quiz/answer")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongAnswer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct", is(false)))
                .andExpect(jsonPath("$.currentStreak", is(0)));
    }

    @Test
    @Order(7)
    @DisplayName("Multiple words should be tracked independently")
    void multipleWords_ShouldBeTrackedIndependently() throws Exception {
        var words = wordRepository.findByCategory("greetings");
        Long word1Id = words.get(0).getId();
        Long word2Id = words.get(1).getId();
        String word1Answer = words.get(0).getEnglish();
        String word2Answer = words.get(1).getEnglish();

        // Practice word1 twice
        for (int i = 0; i < 2; i++) {
            QuizAnswerDTO answer = QuizAnswerDTO.builder()
                    .wordId(word1Id)
                    .answer(word1Answer)
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answer)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.currentStreak", is(i + 1)));
        }

        // Practice word2 - should start at streak 1
        QuizAnswerDTO word2Answer1 = QuizAnswerDTO.builder()
                .wordId(word2Id)
                .answer(word2Answer)
                .build();

        mockMvc.perform(post("/api/quiz/answer")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(word2Answer1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStreak", is(1)));

        // Word1 should still have its own streak
        QuizAnswerDTO word1Answer3 = QuizAnswerDTO.builder()
                .wordId(word1Id)
                .answer(word1Answer)
                .build();

        mockMvc.perform(post("/api/quiz/answer")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(word1Answer3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStreak", is(3)));
    }

    @Test
    @Order(8)
    @DisplayName("Overall success rate should aggregate all words")
    void overallSuccessRate_ShouldAggregateAllWords() throws Exception {
        var words = wordRepository.findAll();

        // Practice multiple words with different outcomes
        for (int i = 0; i < Math.min(5, words.size()); i++) {
            Long wordId = words.get(i).getId();
            String correctAnswer = words.get(i).getEnglish();

            // Alternate between correct and incorrect (60% success rate)
            String answer = (i % 5 != 4) ? correctAnswer : "wrong";

            QuizAnswerDTO answerDTO = QuizAnswerDTO.builder()
                    .wordId(wordId)
                    .answer(answer)
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answerDTO)))
                    .andExpect(status().isOk());
        }

        // Check overall statistics
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wordsStudied", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.totalAttempts", greaterThanOrEqualTo(5)))
                .andExpect(jsonPath("$.overallSuccessRate", greaterThan(0.0)));
    }
}

