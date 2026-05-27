package com.dutchvocabulary.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Test configuration that provides mock beans for components
 * that are disabled or unavailable during testing.
 */
@TestConfiguration
public class TestConfig {

    /**
     * Provide a null KafkaTemplate for tests.
     * The QuizEventProducer handles null gracefully.
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return null;
    }
}

