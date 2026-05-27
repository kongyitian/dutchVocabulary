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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Quiz API endpoints.
 * Tests the full quiz flow including progress tracking.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Quiz API Integration Tests")
class QuizIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VocabularyWordRepository wordRepository;

    @Autowired
    private LearningProgressRepository progressRepository;

    private String authToken;
    private String username;

    @BeforeEach
    void setUp() throws Exception {
        // Register and login to get auth token
        username = "quizuser_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Quiz Test User");

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
    @DisplayName("GET /api/quiz - Should generate random quiz")
    void generateQuiz_ShouldReturnQuestions() throws Exception {
        mockMvc.perform(get("/api/quiz")
                        .param("count", "5")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].wordId", notNullValue()))
                .andExpect(jsonPath("$[0].dutch", notNullValue()))
                .andExpect(jsonPath("$[0].options", hasSize(4)));
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/quiz - Should filter by category")
    void generateQuiz_WithCategory_ShouldFilterByCategory() throws Exception {
        mockMvc.perform(get("/api/quiz")
                        .param("count", "5")
                        .param("category", "greetings")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].category", everyItem(is("greetings"))));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/quiz - Should filter by difficulty")
    void generateQuiz_WithDifficulty_ShouldFilterByDifficulty() throws Exception {
        mockMvc.perform(get("/api/quiz")
                        .param("count", "5")
                        .param("difficulty", "EASY")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].difficulty", everyItem(is("EASY"))));
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/quiz/smart - Should generate smart quiz")
    void generateSmartQuiz_ShouldReturnQuestions() throws Exception {
        mockMvc.perform(get("/api/quiz/smart")
                        .param("count", "5")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(lessThanOrEqualTo(5))))
                .andExpect(jsonPath("$[0].wordId", notNullValue()));
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/quiz/answer - Should submit correct answer")
    void submitAnswer_Correct_ShouldReturnSuccessResult() throws Exception {
        // Get a word to answer
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct", is(true)))
                .andExpect(jsonPath("$.correctAnswer", is(correctAnswer)))
                .andExpect(jsonPath("$.currentStreak", greaterThanOrEqualTo(1)));
    }

    @Test
    @Order(6)
    @DisplayName("POST /api/quiz/answer - Should submit incorrect answer")
    void submitAnswer_Incorrect_ShouldReturnFailureResult() throws Exception {
        Long wordId = wordRepository.findByCategory("greetings").get(0).getId();

        QuizAnswerDTO answer = QuizAnswerDTO.builder()
                .wordId(wordId)
                .answer("wrong_answer_xyz")
                .build();

        mockMvc.perform(post("/api/quiz/answer")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct", is(false)))
                .andExpect(jsonPath("$.currentStreak", is(0)));
    }

    @Test
    @Order(7)
    @DisplayName("POST /api/quiz/answer - Should handle case insensitive answers")
    void submitAnswer_CaseInsensitive_ShouldBeCorrect() throws Exception {
        Long wordId = wordRepository.findByCategory("greetings").get(0).getId();
        String correctAnswer = wordRepository.findById(wordId).get().getEnglish();

        QuizAnswerDTO answer = QuizAnswerDTO.builder()
                .wordId(wordId)
                .answer(correctAnswer.toUpperCase()) // Submit uppercase
                .build();

        mockMvc.perform(post("/api/quiz/answer")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct", is(true)));
    }

    @Test
    @Order(8)
    @DisplayName("POST /api/quiz/submit - Should submit multiple answers")
    void submitQuiz_ShouldProcessAllAnswers() throws Exception {
        var words = wordRepository.findByCategory("greetings");

        List<QuizAnswerDTO> answers = Arrays.asList(
                QuizAnswerDTO.builder()
                        .wordId(words.get(0).getId())
                        .answer(words.get(0).getEnglish())
                        .build(),
                QuizAnswerDTO.builder()
                        .wordId(words.get(1).getId())
                        .answer("wrong_answer")
                        .build()
        );

        mockMvc.perform(post("/api/quiz/submit")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answers)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].correct", is(true)))
                .andExpect(jsonPath("$[1].correct", is(false)));
    }

    @Test
    @Order(9)
    @DisplayName("GET /api/quiz/statistics - Should return user statistics")
    void getStatistics_ShouldReturnStats() throws Exception {
        // Submit some answers first
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

        // Now check statistics
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalWords", greaterThan(0)))
                .andExpect(jsonPath("$.wordsStudied", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.totalAttempts", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.totalCorrect", greaterThanOrEqualTo(1)));
    }

    @Test
    @Order(10)
    @DisplayName("Quiz flow - Complete quiz should update progress")
    void completeQuizFlow_ShouldUpdateProgress() throws Exception {
        // Generate a quiz
        MvcResult quizResult = mockMvc.perform(get("/api/quiz")
                        .param("count", "3")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();

        // Parse quiz questions
        String quizJson = quizResult.getResponse().getContentAsString();
        QuizQuestionDTO[] questions = objectMapper.readValue(quizJson, QuizQuestionDTO[].class);

        // Answer all questions correctly
        for (QuizQuestionDTO question : questions) {
            String correctAnswer = wordRepository.findById(question.getWordId())
                    .get().getEnglish();

            QuizAnswerDTO answer = QuizAnswerDTO.builder()
                    .wordId(question.getWordId())
                    .answer(correctAnswer)
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answer)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.correct", is(true)));
        }

        // Verify statistics updated
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCorrect", greaterThanOrEqualTo(3)));
    }
}

