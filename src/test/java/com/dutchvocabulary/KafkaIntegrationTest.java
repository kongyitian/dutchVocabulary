package com.dutchvocabulary;

import com.dutchvocabulary.dto.*;
import com.dutchvocabulary.event.AchievementEvent;
import com.dutchvocabulary.event.QuizAttemptEvent;
import com.dutchvocabulary.kafka.KafkaTopicConfig;
import com.dutchvocabulary.repository.VocabularyWordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Kafka event publishing.
 * Uses embedded Kafka broker to test event production and consumption.
 *
 * These tests use Spring's EmbeddedKafka which doesn't require Docker.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.main.allow-bean-definition-overriding=true"
        }
)
@AutoConfigureMockMvc
@ActiveProfiles("kafka-test")
@EmbeddedKafka(
        partitions = 1,
        topics = {KafkaTopicConfig.QUIZ_ATTEMPTS_TOPIC, KafkaTopicConfig.ACHIEVEMENTS_TOPIC},
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:0",
                "port=0"
        }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("kafka")
@DisplayName("Kafka Integration Tests with Embedded Kafka")
class KafkaIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VocabularyWordRepository wordRepository;

    private String authToken;
    private String username;

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.enabled", () -> "true");
        registry.add("spring.kafka.bootstrap-servers", () -> "${spring.embedded.kafka.brokers}");
        registry.add("spring.kafka.producer.key-serializer",
            () -> "org.apache.kafka.common.serialization.StringSerializer");
        registry.add("spring.kafka.producer.value-serializer",
            () -> "org.springframework.kafka.support.serializer.JsonSerializer");
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
        registry.add("spring.kafka.consumer.key-deserializer",
            () -> "org.apache.kafka.common.serialization.StringDeserializer");
        registry.add("spring.kafka.consumer.value-deserializer",
            () -> "org.springframework.kafka.support.serializer.JsonDeserializer");
        registry.add("spring.kafka.consumer.properties.spring.json.trusted.packages",
            () -> "com.dutchvocabulary.event");
        registry.add("spring.kafka.listener.missing-topics-fatal", () -> "false");
        // Use H2 for simplicity in Kafka tests - unique DB name for isolation
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:kafkatest" + System.currentTimeMillis() + ";DB_CLOSE_DELAY=-1");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.H2Dialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.sql.init.mode", () -> "always");
        registry.add("spring.jpa.defer-datasource-initialization", () -> "true");
        // JWT settings
        registry.add("app.jwt.secret", () -> "TestSecretKeyForJWTTokenGeneration12345678901234567890123456");
        registry.add("app.jwt.expiration", () -> "86400000");
    }

    @BeforeEach
    void setUp() throws Exception {
        username = "kafkauser_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Kafka Test User");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponse.class);
        authToken = authResponse.getToken();
    }

    private Consumer<String, QuizAttemptEvent> createQuizAttemptConsumer() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                "test-quiz-group-" + System.currentTimeMillis(), "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.dutchvocabulary.event");
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, QuizAttemptEvent.class.getName());

        DefaultKafkaConsumerFactory<String, QuizAttemptEvent> factory =
                new DefaultKafkaConsumerFactory<>(consumerProps);
        Consumer<String, QuizAttemptEvent> consumer = factory.createConsumer();
        consumer.subscribe(Collections.singletonList(KafkaTopicConfig.QUIZ_ATTEMPTS_TOPIC));
        return consumer;
    }

    private Consumer<String, AchievementEvent> createAchievementConsumer() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                "test-achievement-group-" + System.currentTimeMillis(), "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.dutchvocabulary.event");
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, AchievementEvent.class.getName());

        DefaultKafkaConsumerFactory<String, AchievementEvent> factory =
                new DefaultKafkaConsumerFactory<>(consumerProps);
        Consumer<String, AchievementEvent> consumer = factory.createConsumer();
        consumer.subscribe(Collections.singletonList(KafkaTopicConfig.ACHIEVEMENTS_TOPIC));
        return consumer;
    }

    @Test
    @Order(1)
    @DisplayName("Quiz answer should publish QuizAttemptEvent to Kafka")
    void quizAnswer_ShouldPublishEvent() throws Exception {
        Consumer<String, QuizAttemptEvent> consumer = createQuizAttemptConsumer();

        try {
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
                    .andExpect(jsonPath("$.correct").value(true));

            // Wait for and verify the Kafka event
            await().atMost(30, TimeUnit.SECONDS).pollInterval(Duration.ofSeconds(2)).untilAsserted(() -> {
                ConsumerRecords<String, QuizAttemptEvent> records =
                        consumer.poll(Duration.ofMillis(2000));

                assertThat(records.count()).isGreaterThan(0);

                QuizAttemptEvent event = records.iterator().next().value();
                assertThat(event).isNotNull();
                assertThat(event.getUsername()).isEqualTo(username);
                assertThat(event.getWordId()).isEqualTo(wordId);
                assertThat(event.isCorrect()).isTrue();
                assertThat(event.getCorrectAnswer()).isEqualTo(correctAnswer);
            });
        } finally {
            consumer.close();
        }
    }

    @Test
    @Order(2)
    @DisplayName("Incorrect answer should publish event with correct=false")
    void incorrectAnswer_ShouldPublishEventWithCorrectFalse() throws Exception {
        Consumer<String, QuizAttemptEvent> consumer = createQuizAttemptConsumer();

        try {
            Long wordId = wordRepository.findByCategory("greetings").get(0).getId();

            QuizAnswerDTO answer = QuizAnswerDTO.builder()
                    .wordId(wordId)
                    .answer("wrong_answer")
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answer)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.correct").value(false));

            await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
                ConsumerRecords<String, QuizAttemptEvent> records =
                        consumer.poll(Duration.ofMillis(1000));

                assertThat(records.count()).isGreaterThan(0);

                // Find the event for this specific answer
                boolean found = false;
                for (var record : records) {
                    if (record.value().getUserAnswer().equals("wrong_answer")) {
                        assertThat(record.value().isCorrect()).isFalse();
                        found = true;
                        break;
                    }
                }
                assertThat(found).isTrue();
            });
        } finally {
            consumer.close();
        }
    }

    @Test
    @Order(3)
    @DisplayName("First correct answer should publish FIRST_CORRECT achievement event")
    void firstCorrectAnswer_ShouldPublishAchievementEvent() throws Exception {
        // Create a new user to ensure it's their first correct answer
        String newUsername = "achievement_user_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(newUsername);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Achievement Test");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class);
        String newToken = authResponse.getToken();

        Consumer<String, AchievementEvent> consumer = createAchievementConsumer();

        try {
            Long wordId = wordRepository.findByCategory("greetings").get(0).getId();
            String correctAnswer = wordRepository.findById(wordId).get().getEnglish();

            QuizAnswerDTO answer = QuizAnswerDTO.builder()
                    .wordId(wordId)
                    .answer(correctAnswer)
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + newToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answer)))
                    .andExpect(status().isOk());

            await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
                ConsumerRecords<String, AchievementEvent> records =
                        consumer.poll(Duration.ofMillis(1000));

                boolean foundFirstCorrect = false;
                for (var record : records) {
                    if (record.value().getUsername().equals(newUsername) &&
                        record.value().getAchievementType().equals("FIRST_CORRECT")) {
                        foundFirstCorrect = true;
                        assertThat(record.value().getMessage()).contains("First Steps");
                        break;
                    }
                }
                assertThat(foundFirstCorrect).isTrue();
            });
        } finally {
            consumer.close();
        }
    }

    @Test
    @Order(4)
    @DisplayName("Event should contain streak information")
    void event_ShouldContainStreakInfo() throws Exception {
        Consumer<String, QuizAttemptEvent> consumer = createQuizAttemptConsumer();

        try {
            Long wordId = wordRepository.findByCategory("greetings").get(0).getId();
            String correctAnswer = wordRepository.findById(wordId).get().getEnglish();

            // Submit 3 correct answers to build streak
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

            await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
                ConsumerRecords<String, QuizAttemptEvent> records =
                        consumer.poll(Duration.ofMillis(1000));

                // Find event with streak >= 3
                int maxStreak = 0;
                for (var record : records) {
                    if (record.value().getUsername().equals(username)) {
                        maxStreak = Math.max(maxStreak, record.value().getCurrentStreak());
                    }
                }
                assertThat(maxStreak).isGreaterThanOrEqualTo(3);
            });
        } finally {
            consumer.close();
        }
    }

    @Test
    @Order(5)
    @DisplayName("STREAK_5 achievement should be published after 5 correct answers")
    void streak5_ShouldPublishAchievement() throws Exception {
        // Create a new user
        String streakUsername = "streak5_user_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(streakUsername);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Streak Test");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class);
        String streakToken = authResponse.getToken();

        Consumer<String, AchievementEvent> consumer = createAchievementConsumer();

        try {
            Long wordId = wordRepository.findByCategory("greetings").get(0).getId();
            String correctAnswer = wordRepository.findById(wordId).get().getEnglish();

            // Submit 5 correct answers on same word
            for (int i = 0; i < 5; i++) {
                QuizAnswerDTO answer = QuizAnswerDTO.builder()
                        .wordId(wordId)
                        .answer(correctAnswer)
                        .build();

                mockMvc.perform(post("/api/quiz/answer")
                                .header("Authorization", "Bearer " + streakToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(answer)))
                        .andExpect(status().isOk());
            }

            await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
                ConsumerRecords<String, AchievementEvent> records =
                        consumer.poll(Duration.ofMillis(1000));

                boolean foundStreak5 = false;
                for (var record : records) {
                    if (record.value().getUsername().equals(streakUsername) &&
                        record.value().getAchievementType().equals("STREAK_5")) {
                        foundStreak5 = true;
                        assertThat(record.value().getMessage()).contains("On Fire");
                        break;
                    }
                }
                assertThat(foundStreak5).isTrue();
            });
        } finally {
            consumer.close();
        }
    }

    @Test
    @Order(6)
    @DisplayName("Event should have valid timestamp")
    void event_ShouldHaveValidTimestamp() throws Exception {
        Consumer<String, QuizAttemptEvent> consumer = createQuizAttemptConsumer();

        try {
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

            await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
                ConsumerRecords<String, QuizAttemptEvent> records =
                        consumer.poll(Duration.ofMillis(1000));

                assertThat(records.count()).isGreaterThan(0);

                QuizAttemptEvent event = records.iterator().next().value();
                assertThat(event.getTimestamp()).isNotNull();
                assertThat(event.getEventId()).isNotNull();
            });
        } finally {
            consumer.close();
        }
    }

    @Test
    @Order(7)
    @DisplayName("Multiple rapid events should all be published")
    void multipleRapidEvents_ShouldAllBePublished() throws Exception {
        Consumer<String, QuizAttemptEvent> consumer = createQuizAttemptConsumer();

        try {
            var words = wordRepository.findByCategory("greetings");
            int eventCount = Math.min(5, words.size());

            // Rapidly submit multiple answers
            for (int i = 0; i < eventCount; i++) {
                Long wordId = words.get(i % words.size()).getId();
                String correctAnswer = words.get(i % words.size()).getEnglish();

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

            // Verify all events were published
            await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
                ConsumerRecords<String, QuizAttemptEvent> records =
                        consumer.poll(Duration.ofMillis(2000));

                int userEventCount = 0;
                for (var record : records) {
                    if (record.value().getUsername().equals(username)) {
                        userEventCount++;
                    }
                }
                assertThat(userEventCount).isGreaterThanOrEqualTo(eventCount);
            });
        } finally {
            consumer.close();
        }
    }

    @Test
    @Order(8)
    @DisplayName("Event should contain Dutch word information")
    void event_ShouldContainDutchWord() throws Exception {
        Consumer<String, QuizAttemptEvent> consumer = createQuizAttemptConsumer();

        try {
            var word = wordRepository.findByCategory("greetings").get(0);
            Long wordId = word.getId();
            String dutchWord = word.getDutch();
            String correctAnswer = word.getEnglish();

            QuizAnswerDTO answer = QuizAnswerDTO.builder()
                    .wordId(wordId)
                    .answer(correctAnswer)
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answer)))
                    .andExpect(status().isOk());

            await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
                ConsumerRecords<String, QuizAttemptEvent> records =
                        consumer.poll(Duration.ofMillis(1000));

                boolean foundEvent = false;
                for (var record : records) {
                    if (record.value().getUsername().equals(username) &&
                        record.value().getWordId().equals(wordId)) {
                        assertThat(record.value().getDutchWord()).isEqualTo(dutchWord);
                        foundEvent = true;
                        break;
                    }
                }
                assertThat(foundEvent).isTrue();
            });
        } finally {
            consumer.close();
        }
    }

    @Test
    @Order(9)
    @DisplayName("STREAK_10 achievement should be published after 10 correct answers")
    void streak10_ShouldPublishAchievement() throws Exception {
        // Create a new user
        String streak10Username = "streak10_user_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(streak10Username);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Streak 10 Test");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class);
        String streak10Token = authResponse.getToken();

        Consumer<String, AchievementEvent> consumer = createAchievementConsumer();

        try {
            Long wordId = wordRepository.findByCategory("greetings").get(0).getId();
            String correctAnswer = wordRepository.findById(wordId).get().getEnglish();

            // Submit 10 correct answers
            for (int i = 0; i < 10; i++) {
                QuizAnswerDTO answer = QuizAnswerDTO.builder()
                        .wordId(wordId)
                        .answer(correctAnswer)
                        .build();

                mockMvc.perform(post("/api/quiz/answer")
                                .header("Authorization", "Bearer " + streak10Token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(answer)))
                        .andExpect(status().isOk());
            }

            await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
                ConsumerRecords<String, AchievementEvent> records =
                        consumer.poll(Duration.ofMillis(1000));

                boolean foundStreak10 = false;
                for (var record : records) {
                    if (record.value().getUsername().equals(streak10Username) &&
                        record.value().getAchievementType().equals("STREAK_10")) {
                        foundStreak10 = true;
                        break;
                    }
                }
                assertThat(foundStreak10).isTrue();
            });
        } finally {
            consumer.close();
        }
    }

    @Test
    @Order(10)
    @DisplayName("Event should contain success rate")
    void event_ShouldContainSuccessRate() throws Exception {
        // Create a new user for clean metrics
        String metricsUsername = "metrics_user_" + System.currentTimeMillis();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(metricsUsername);
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Metrics Test");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class);
        String metricsToken = authResponse.getToken();

        Consumer<String, QuizAttemptEvent> consumer = createQuizAttemptConsumer();

        try {
            var word = wordRepository.findByCategory("greetings").get(0);
            Long wordId = word.getId();
            String correctAnswer = word.getEnglish();

            // Submit one correct and one incorrect answer
            QuizAnswerDTO correct = QuizAnswerDTO.builder()
                    .wordId(wordId)
                    .answer(correctAnswer)
                    .build();

            QuizAnswerDTO incorrect = QuizAnswerDTO.builder()
                    .wordId(wordId)
                    .answer("wrong")
                    .build();

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + metricsToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(correct)))
                    .andExpect(status().isOk());

            mockMvc.perform(post("/api/quiz/answer")
                            .header("Authorization", "Bearer " + metricsToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(incorrect)))
                    .andExpect(status().isOk());

            await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
                ConsumerRecords<String, QuizAttemptEvent> records =
                        consumer.poll(Duration.ofMillis(1000));

                boolean foundEventWithRate = false;
                for (var record : records) {
                    if (record.value().getUsername().equals(metricsUsername)) {
                        // Success rate should be between 0 and 100
                        double rate = record.value().getSuccessRate();
                        assertThat(rate).isBetween(0.0, 100.0);
                        foundEventWithRate = true;
                    }
                }
                assertThat(foundEventWithRate).isTrue();
            });
        } finally {
            consumer.close();
        }
    }
}


