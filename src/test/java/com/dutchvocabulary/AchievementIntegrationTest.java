package com.dutchvocabulary;

import com.dutchvocabulary.config.TestConfig;
import com.dutchvocabulary.dto.*;
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
 * Integration tests for Achievement API endpoints.
 * Tests achievements and daily streaks.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Achievement API Integration Tests")
class AchievementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VocabularyWordRepository wordRepository;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        // Register and login to get auth token
        String username = "achieveuser_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Achievement Test User");

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
    @DisplayName("GET /api/achievements - Should return empty achievements for new user")
    void getAchievements_NewUser_ShouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/api/achievements")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/achievements/streak - Should return initial streak info")
    void getDailyStreak_NewUser_ShouldReturnInitialStreak() throws Exception {
        mockMvc.perform(get("/api/achievements/streak")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStreak", is(0)))
                .andExpect(jsonPath("$.longestStreak", is(0)))
                .andExpect(jsonPath("$.totalDaysPracticed", is(0)))
                .andExpect(jsonPath("$.practicedToday", is(false)));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/achievements/summary - Should return achievement summary")
    void getAchievementSummary_NewUser_ShouldReturnSummary() throws Exception {
        mockMvc.perform(get("/api/achievements/summary")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.achievementCount", is(0)))
                .andExpect(jsonPath("$.currentStreak", is(0)))
                .andExpect(jsonPath("$.longestStreak", is(0)));
    }

    @Test
    @Order(4)
    @DisplayName("Should earn FIRST_CORRECT achievement after first correct answer")
    void firstCorrectAnswer_ShouldEarnAchievement() throws Exception {
        // Submit a correct answer
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
                .andExpect(jsonPath("$.correct", is(true)));

        // Check achievements
        mockMvc.perform(get("/api/achievements")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.achievementType == 'FIRST_CORRECT')]", hasSize(1)));
    }

    @Test
    @Order(5)
    @DisplayName("Should update streak after practicing")
    void practice_ShouldUpdateStreak() throws Exception {
        // Submit a correct answer to trigger daily practice
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

        // Check streak was updated
        mockMvc.perform(get("/api/achievements/streak")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStreak", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.totalDaysPracticed", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.practicedToday", is(true)));
    }

    @Test
    @Order(6)
    @DisplayName("Should earn STREAK_5 achievement after 5 correct answers on same word")
    void fiveCorrectAnswers_ShouldEarnStreakAchievement() throws Exception {
        var words = wordRepository.findAll();
        // Use the same word 5 times to build per-word streak
        Long wordId = words.get(0).getId();
        String correctAnswer = words.get(0).getEnglish();

        // Submit 5 correct answers in a row (same word to build streak)
        for (int i = 0; i < 5; i++) {
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
        }

        // Check for STREAK_5 achievement
        mockMvc.perform(get("/api/achievements")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.achievementType == 'STREAK_5')]", hasSize(1)));
    }

    @Test
    @Order(7)
    @DisplayName("Achievement count should increase as achievements are earned")
    void achievementCount_ShouldIncrease() throws Exception {
        // Submit a correct answer
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

        // Check summary has achievement count > 0
        mockMvc.perform(get("/api/achievements/summary")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.achievementCount", greaterThanOrEqualTo(1)));
    }

    @Test
    @Order(8)
    @DisplayName("Unauthenticated access to achievements should return empty list")
    void getAchievements_Unauthenticated_ShouldReturnEmpty() throws Exception {
        // The achievements endpoint is public but returns empty for unauthenticated users
        mockMvc.perform(get("/api/achievements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}

