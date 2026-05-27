package com.dutchvocabulary.kafka;

import com.dutchvocabulary.event.AchievementEvent;
import com.dutchvocabulary.event.QuizAttemptEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka producer for quiz and achievement events.
 * Gracefully handles cases when Kafka is unavailable.
 */
@Service
@Slf4j
public class QuizEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final boolean kafkaEnabled;

    @Autowired(required = false)
    public QuizEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaEnabled = (kafkaTemplate != null);
        if (!kafkaEnabled) {
            log.info("📭 Kafka is not available - events will not be published");
        }
    }

    /**
     * Send a quiz attempt event to Kafka.
     */
    public void sendQuizAttemptEvent(QuizAttemptEvent event) {
        if (!kafkaEnabled) {
            log.debug("Kafka disabled - skipping quiz attempt event");
            return;
        }

        log.info("📤 Sending quiz attempt event: userId={}, wordId={}, correct={}",
                event.getUserId(), event.getWordId(), event.isCorrect());

        kafkaTemplate.send(KafkaTopicConfig.QUIZ_ATTEMPTS_TOPIC,
                event.getUserId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Quiz event sent successfully: offset={}",
                                result.getRecordMetadata().offset());
                    } else {
                        log.warn("Failed to send quiz event: {}", ex.getMessage());
                    }
                });
    }

    /**
     * Send an achievement event to Kafka.
     */
    public void sendAchievementEvent(AchievementEvent event) {
        if (!kafkaEnabled) {
            log.debug("Kafka disabled - skipping achievement event");
            return;
        }

        log.info("📤 Sending achievement event: userId={}, type={}",
                event.getUserId(), event.getAchievementType());

        kafkaTemplate.send(KafkaTopicConfig.ACHIEVEMENTS_TOPIC,
                event.getUserId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Achievement event sent successfully: offset={}",
                                result.getRecordMetadata().offset());
                    } else {
                        log.warn("Failed to send achievement event: {}", ex.getMessage());
                    }
                });
    }
}


