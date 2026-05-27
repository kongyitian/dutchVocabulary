package com.dutchvocabulary.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka topic configuration.
 */
@Configuration
public class KafkaTopicConfig {

    public static final String QUIZ_ATTEMPTS_TOPIC = "quiz-attempts";
    public static final String ACHIEVEMENTS_TOPIC = "achievements";

    @Bean
    public NewTopic quizAttemptsTopic() {
        return TopicBuilder.name(QUIZ_ATTEMPTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic achievementsTopic() {
        return TopicBuilder.name(ACHIEVEMENTS_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}

