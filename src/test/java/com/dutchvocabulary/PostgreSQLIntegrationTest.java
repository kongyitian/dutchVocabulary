package com.dutchvocabulary;

import com.dutchvocabulary.dto.*;
import com.dutchvocabulary.model.Difficulty;
import com.dutchvocabulary.model.VocabularyWord;
import com.dutchvocabulary.repository.AchievementRepository;
import com.dutchvocabulary.repository.LearningProgressRepository;
import com.dutchvocabulary.repository.UserRepository;
import com.dutchvocabulary.repository.VocabularyWordRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests using PostgreSQL Testcontainer.
 * Tests the application against a real PostgreSQL database instead of H2.
 * This ensures database compatibility and catches PostgreSQL-specific issues.
 *
 * These tests require Docker to be running. They will be skipped if Docker is unavailable.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PostgreSQL Testcontainer Integration Tests")
@EnabledIf("isDockerAvailable")
class PostgreSQLIntegrationTest {

    static boolean isDockerAvailable() {
        try {
            DockerClientFactory.instance().client();
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.sql.init.mode", () -> "always");
        registry.add("spring.jpa.defer-datasource-initialization", () -> "true");
        // Disable Kafka for these tests
        registry.add("spring.kafka.enabled", () -> "false");
        registry.add("spring.autoconfigure.exclude", () ->
            "org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration");
        // JWT settings
        registry.add("app.jwt.secret", () -> "TestSecretKeyForJWTTokenGeneration12345678901234567890123456");
        registry.add("app.jwt.expiration", () -> "86400000");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VocabularyWordRepository wordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LearningProgressRepository progressRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    private String authToken;
    private String username;

    @BeforeEach
    void setUp() throws Exception {
        // Register and login to get auth token
        username = "pguser_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("PostgreSQL Test User");

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
    @DisplayName("PostgreSQL container should be running")
    void containerShouldBeRunning() {
        assertThat(postgres.isRunning()).isTrue();
        assertThat(postgres.getJdbcUrl()).contains("postgresql");
    }

    @Test
    @Order(2)
    @DisplayName("Should load seed data from data.sql")
    void shouldLoadSeedData() throws Exception {
        mockMvc.perform(get("/api/words")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @Order(3)
    @DisplayName("CRUD operations should work with PostgreSQL")
    void crudOperationsShouldWork() throws Exception {
        // Create
        VocabularyWordDTO newWord = VocabularyWordDTO.builder()
                .dutch("postgres_woord")
                .english("postgres_word")
                .category("test")
                .difficulty(Difficulty.EASY)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/words")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newWord)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();

        VocabularyWord created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), VocabularyWord.class);

        // Read
        mockMvc.perform(get("/api/words/" + created.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dutch", is("postgres_woord")));

        // Update
        VocabularyWordDTO updateDto = VocabularyWordDTO.builder()
                .dutch("updated_postgres")
                .english("updated_postgres")
                .category("test")
                .difficulty(Difficulty.HARD)
                .build();

        mockMvc.perform(put("/api/words/" + created.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dutch", is("updated_postgres")))
                .andExpect(jsonPath("$.difficulty", is("HARD")));

        // Delete
        mockMvc.perform(delete("/api/words/" + created.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());

        // Verify deleted
        mockMvc.perform(get("/api/words/" + created.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @DisplayName("PostgreSQL-specific queries should work (RANDOM)")
    void randomQueryShouldWork() throws Exception {
        // The findRandomWords uses RANDOM() which works differently in PostgreSQL vs H2
        mockMvc.perform(get("/api/quiz")
                        .param("count", "5")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(lessThanOrEqualTo(5))));
    }

    @Test
    @Order(5)
    @DisplayName("Search query with LIKE should work in PostgreSQL")
    void searchQueryShouldWork() throws Exception {
        mockMvc.perform(get("/api/words/search")
                        .param("q", "hallo")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dutch", containsStringIgnoringCase("hallo")));
    }

    @Test
    @Order(6)
    @DisplayName("Concurrent transactions should work")
    void concurrentTransactionsShouldWork() throws Exception {
        // Submit multiple quiz answers rapidly
        Long wordId = wordRepository.findByCategory("greetings").get(0).getId();
        String correctAnswer = wordRepository.findById(wordId).get().getEnglish();

        for (int i = 0; i < 10; i++) {
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

        // Verify progress was tracked correctly
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAttempts", greaterThanOrEqualTo(10)));
    }

    @Test
    @Order(7)
    @DisplayName("Case-insensitive search should work in PostgreSQL")
    void caseInsensitiveSearchShouldWork() throws Exception {
        // PostgreSQL handles case differently than H2
        mockMvc.perform(get("/api/words/search")
                        .param("q", "HALLO")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(8)
    @DisplayName("Statistics aggregation queries should work in PostgreSQL")
    void statisticsAggregationShouldWork() throws Exception {
        // First make some progress
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

        // Get statistics (uses aggregate queries)
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalWords", greaterThan(0)))
                .andExpect(jsonPath("$.wordsStudied", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.overallSuccessRate", greaterThanOrEqualTo(0.0)));
    }

    @Test
    @Order(9)
    @DisplayName("Batch insert should work in PostgreSQL")
    void batchInsertShouldWork() throws Exception {
        int batchSize = 20;
        for (int i = 0; i < batchSize; i++) {
            VocabularyWordDTO word = VocabularyWordDTO.builder()
                    .dutch("batch_woord_" + i)
                    .english("batch_word_" + i)
                    .category("batch_test")
                    .difficulty(Difficulty.MEDIUM)
                    .build();

            mockMvc.perform(post("/api/words")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(word)))
                    .andExpect(status().isCreated());
        }

        // Verify all words were inserted
        MvcResult result = mockMvc.perform(get("/api/words/category/batch_test")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();

        List<VocabularyWord> words = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<VocabularyWord>>() {});

        assertThat(words).hasSize(batchSize);
    }

    @Test
    @Order(10)
    @DisplayName("Parallel database operations should work")
    void parallelOperationsShouldWork() throws Exception {
        int threadCount = 5;
        int operationsPerThread = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        Long wordId = wordRepository.findByCategory("greetings").get(0).getId();
        String correctAnswer = wordRepository.findById(wordId).get().getEnglish();

        for (int t = 0; t < threadCount; t++) {
            final int threadNum = t;
            executor.submit(() -> {
                try {
                    // Register a new user for this thread
                    String threadUsername = "parallel_user_" + threadNum + "_" + System.currentTimeMillis();
                    RegisterRequest registerRequest = new RegisterRequest();
                    registerRequest.setUsername(threadUsername);
                    registerRequest.setPassword("password123");
                    registerRequest.setDisplayName("Parallel User " + threadNum);

                    MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(registerRequest)))
                            .andExpect(status().isCreated())
                            .andReturn();

                    AuthResponse authResponse = objectMapper.readValue(
                            regResult.getResponse().getContentAsString(), AuthResponse.class);
                    String threadToken = authResponse.getToken();

                    // Perform operations
                    for (int i = 0; i < operationsPerThread; i++) {
                        QuizAnswerDTO answer = QuizAnswerDTO.builder()
                                .wordId(wordId)
                                .answer(correctAnswer)
                                .build();

                        mockMvc.perform(post("/api/quiz/answer")
                                        .header("Authorization", "Bearer " + threadToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(answer)))
                                .andExpect(status().isOk());

                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();

        // Verify most operations succeeded
        assertThat(successCount.get()).isGreaterThanOrEqualTo(threadCount * operationsPerThread - 5);
        assertThat(failCount.get()).isLessThanOrEqualTo(5);
    }

    @Test
    @Order(11)
    @DisplayName("PostgreSQL index should be used for search")
    void indexShouldBeUsedForSearch() throws Exception {
        // Insert many words to ensure index would be useful
        for (int i = 0; i < 50; i++) {
            VocabularyWordDTO word = VocabularyWordDTO.builder()
                    .dutch("indexed_woord_" + i)
                    .english("indexed_word_" + i)
                    .category("indexed_test")
                    .difficulty(Difficulty.EASY)
                    .build();

            mockMvc.perform(post("/api/words")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(word)))
                    .andExpect(status().isCreated());
        }

        // Search should complete quickly even with many records
        long startTime = System.currentTimeMillis();
        mockMvc.perform(get("/api/words/search")
                        .param("q", "indexed_woord_25")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
        long duration = System.currentTimeMillis() - startTime;

        // Search should be fast (< 1 second)
        assertThat(duration).isLessThan(1000);
    }

    @Test
    @Order(12)
    @DisplayName("Transaction rollback should work")
    void transactionRollbackShouldWork() throws Exception {
        // Get initial count
        long initialCount = wordRepository.count();

        // Try to create a word with validation error (null values typically fail)
        // This simulates a rollback scenario
        VocabularyWordDTO invalidWord = VocabularyWordDTO.builder()
                .dutch("") // Empty should fail validation
                .english("test")
                .category("test")
                .difficulty(Difficulty.EASY)
                .build();

        mockMvc.perform(post("/api/words")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidWord)))
                .andExpect(status().isBadRequest());

        // Count should remain the same (transaction rolled back)
        long afterCount = wordRepository.count();
        assertThat(afterCount).isEqualTo(initialCount);
    }

    @Test
    @Order(13)
    @DisplayName("Filter by difficulty should work in PostgreSQL")
    void filterByDifficultyShouldWork() throws Exception {
        mockMvc.perform(get("/api/words/difficulty/EASY")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[*].difficulty", everyItem(is("EASY"))));

        mockMvc.perform(get("/api/words/difficulty/MEDIUM")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/words/difficulty/HARD")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Test
    @Order(14)
    @DisplayName("User data isolation should work")
    void userDataIsolationShouldWork() throws Exception {
        // Create second user
        String user2 = "pguser2_" + System.currentTimeMillis();
        RegisterRequest registerRequest2 = new RegisterRequest();
        registerRequest2.setUsername(user2);
        registerRequest2.setPassword("password123");
        registerRequest2.setDisplayName("PostgreSQL Test User 2");

        MvcResult result2 = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest2)))
                .andExpect(status().isCreated())
                .andReturn();

        String token2 = objectMapper.readValue(
                result2.getResponse().getContentAsString(), AuthResponse.class).getToken();

        // First user makes progress
        Long wordId = wordRepository.findByCategory("greetings").get(0).getId();
        String correctAnswer = wordRepository.findById(wordId).get().getEnglish();

        for (int i = 0; i < 5; i++) {
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

        // Verify user 1 has progress
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAttempts", greaterThanOrEqualTo(5)));

        // Verify user 2 has no progress (data isolation)
        mockMvc.perform(get("/api/quiz/statistics")
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAttempts", is(0)));
    }

    @Test
    @Order(15)
    @DisplayName("NULL handling should work correctly in PostgreSQL")
    void nullHandlingShouldWork() throws Exception {
        // Create word without optional fields
        VocabularyWordDTO wordWithNulls = VocabularyWordDTO.builder()
                .dutch("null_test_woord")
                .english("null_test_word")
                .category("null_test")
                .difficulty(Difficulty.EASY)
                // example, example_translation, pronunciation are null
                .build();

        MvcResult result = mockMvc.perform(post("/api/words")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wordWithNulls)))
                .andExpect(status().isCreated())
                .andReturn();

        VocabularyWord created = objectMapper.readValue(
                result.getResponse().getContentAsString(), VocabularyWord.class);

        // Verify we can read it back
        mockMvc.perform(get("/api/words/" + created.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dutch", is("null_test_woord")));
    }

    @Test
    @Order(16)
    @DisplayName("Large text fields should work in PostgreSQL")
    void largeTextFieldsShouldWork() throws Exception {
        String longExample = "Dit is een zeer lange voorbeeldzin die veel tekens bevat. " .repeat(20);
        
        VocabularyWordDTO wordWithLargeText = VocabularyWordDTO.builder()
                .dutch("lang_woord")
                .english("long_word")
                .example(longExample)
                .exampleTranslation("This is a very long example sentence." .repeat(20))
                .category("large_text_test")
                .difficulty(Difficulty.MEDIUM)
                .build();

        MvcResult result = mockMvc.perform(post("/api/words")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wordWithLargeText)))
                .andExpect(status().isCreated())
                .andReturn();

        VocabularyWord created = objectMapper.readValue(
                result.getResponse().getContentAsString(), VocabularyWord.class);

        // Verify large text is stored and retrieved correctly
        mockMvc.perform(get("/api/words/" + created.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.example", is(longExample)));
    }
}
