package com.dutchvocabulary.kafka;

import com.dutchvocabulary.event.AchievementEvent;
import com.dutchvocabulary.event.QuizAttemptEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer for quiz and achievement events.
 * Processes events for analytics and notifications.
 */
@Service
@Slf4j
public class QuizEventConsumer {

    /**
     * Consume quiz attempt events for analytics.
     */
    @KafkaListener(
            topics = KafkaTopicConfig.QUIZ_ATTEMPTS_TOPIC,
            groupId = "quiz-analytics-group",
            properties = {"spring.json.value.default.type=com.dutchvocabulary.event.QuizAttemptEvent"}
    )
    public void consumeQuizAttempt(QuizAttemptEvent event) {
        log.info("📊 [Analytics] Quiz attempt received:");
        log.info("   User: {} | Word: {} ({}) | Answer: {} | Correct: {}",
                event.getUsername(),
                event.getDutchWord(),
                event.getCorrectAnswer(),
                event.getUserAnswer(),
                event.isCorrect() ? "✅" : "❌");
        log.info("   Streak: {} | Success Rate: {}%",
                event.getCurrentStreak(), String.format("%.1f", event.getSuccessRate()));

        // Here you could:
        // - Store analytics in a separate database (InfluxDB, TimescaleDB)
        // - Update real-time dashboards (WebSocket)
        // - Send to external analytics service (Mixpanel, Amplitude)
        // - Train ML models for personalized recommendations
    }

    /**
     * Consume achievement events for notifications.
     */
    @KafkaListener(
            topics = KafkaTopicConfig.ACHIEVEMENTS_TOPIC,
            groupId = "achievement-notification-group",
            properties = {"spring.json.value.default.type=com.dutchvocabulary.event.AchievementEvent"}
    )
    public void consumeAchievement(AchievementEvent event) {
        log.info("🏆 [Achievement] {} earned: {}",
                event.getUsername(), event.getAchievementType());
        log.info("   Message: {}", event.getMessage());

        // Here you could:
        // - Send push notification to user's device
        // - Update user's achievement list in database
        // - Trigger gamification rewards
        // - Post to social features (leaderboard)
    }
}


