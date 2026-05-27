package com.dutchvocabulary.kafka;

import com.dutchvocabulary.event.AchievementEvent;
import com.dutchvocabulary.event.QuizAttemptEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka producer for quiz and achievement events.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuizEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Send a quiz attempt event to Kafka.
     */
    public void sendQuizAttemptEvent(QuizAttemptEvent event) {
        log.info("📤 Sending quiz attempt event: userId={}, wordId={}, correct={}",
                event.getUserId(), event.getWordId(), event.isCorrect());

        kafkaTemplate.send(KafkaTopicConfig.QUIZ_ATTEMPTS_TOPIC,
                event.getUserId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Quiz event sent successfully: offset={}",
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send quiz event", ex);
                    }
                });
    }

    /**
     * Send an achievement event to Kafka.
     */
    public void sendAchievementEvent(AchievementEvent event) {
        log.info("📤 Sending achievement event: userId={}, type={}",
                event.getUserId(), event.getAchievementType());

        kafkaTemplate.send(KafkaTopicConfig.ACHIEVEMENTS_TOPIC,
                event.getUserId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Achievement event sent successfully: offset={}",
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send achievement event", ex);
                    }
                });
    }
}

