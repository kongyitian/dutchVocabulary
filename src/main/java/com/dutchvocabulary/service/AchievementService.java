package com.dutchvocabulary.service;

import com.dutchvocabulary.dto.AchievementDTO;
import com.dutchvocabulary.event.AchievementEvent;
import com.dutchvocabulary.kafka.QuizEventProducer;
import com.dutchvocabulary.model.Achievement;
import com.dutchvocabulary.model.DailyStreak;
import com.dutchvocabulary.model.LearningProgress;
import com.dutchvocabulary.model.User;
import com.dutchvocabulary.repository.AchievementRepository;
import com.dutchvocabulary.repository.DailyStreakRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing user achievements and daily streaks.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final DailyStreakRepository dailyStreakRepository;
    private final QuizEventProducer eventProducer;

    /**
     * Get all achievements for a user.
     */
    public List<AchievementDTO> getUserAchievements(User user) {
        return achievementRepository.findByUserOrderByEarnedAtDesc(user).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get daily streak info for a user.
     */
    public DailyStreak getDailyStreak(User user) {
        return dailyStreakRepository.findByUser(user)
                .orElseGet(() -> DailyStreak.builder()
                        .user(user)
                        .currentStreak(0)
                        .longestStreak(0)
                        .totalDaysPracticed(0)
                        .build());
    }

    /**
     * Record daily practice and update streak.
     */
    @Transactional
    public DailyStreak recordDailyPractice(User user) {
        DailyStreak streak = dailyStreakRepository.findByUser(user)
                .orElseGet(() -> DailyStreak.builder()
                        .user(user)
                        .currentStreak(0)
                        .longestStreak(0)
                        .totalDaysPracticed(0)
                        .build());

        boolean wasFirstPracticeToday = !streak.hasPracticedToday();
        int previousStreak = streak.getCurrentStreak();

        streak.recordPractice();
        dailyStreakRepository.save(streak);

        // Check for streak-based achievements
        if (wasFirstPracticeToday) {
            checkDailyStreakAchievements(user, previousStreak, streak.getCurrentStreak());
        }

        return streak;
    }

    /**
     * Check and award achievements based on quiz performance.
     */
    @Transactional
    public void checkQuizAchievements(User user, boolean correct, int previousStreak,
                                       int currentStreak, LearningProgress progress) {
        // First correct answer
        if (correct && progress.getCorrectCount() == 1) {
            awardAchievement(user, "FIRST_CORRECT", "First Steps",
                    "You got your first correct answer!", "🎉");
        }

        // Answer streak milestones
        if (currentStreak >= 5 && previousStreak < 5) {
            awardAchievement(user, "STREAK_5", "On Fire",
                    "5 correct answers in a row!", "🔥");
        }
        if (currentStreak >= 10 && previousStreak < 10) {
            awardAchievement(user, "STREAK_10", "Unstoppable",
                    "10 correct answers in a row!", "🏆");
        }
        if (currentStreak >= 25 && previousStreak < 25) {
            awardAchievement(user, "STREAK_25", "Legendary",
                    "25 correct answers in a row!", "👑");
        }
        if (currentStreak >= 50 && previousStreak < 50) {
            awardAchievement(user, "STREAK_50", "Perfectionist",
                    "50 correct answers in a row!", "💎");
        }

        // Word mastered (90%+ success rate with 10+ attempts)
        if (progress.getAttemptCount() >= 10 && progress.getSuccessRate() >= 90) {
            awardAchievement(user, "WORD_MASTER_" + progress.getWord().getId(),
                    "Word Master",
                    "Mastered '" + progress.getWord().getDutch() + "'!", "📚");
        }
    }

    /**
     * Check and award daily streak achievements.
     */
    private void checkDailyStreakAchievements(User user, int previousStreak, int currentStreak) {
        if (currentStreak >= 7 && previousStreak < 7) {
            awardAchievement(user, "DAILY_STREAK_7", "Week Warrior",
                    "Practiced 7 days in a row!", "📅");
        }
        if (currentStreak >= 30 && previousStreak < 30) {
            awardAchievement(user, "DAILY_STREAK_30", "Monthly Master",
                    "Practiced 30 days in a row!", "🗓️");
        }
        if (currentStreak >= 100 && previousStreak < 100) {
            awardAchievement(user, "DAILY_STREAK_100", "Centurion",
                    "Practiced 100 days in a row!", "💯");
        }
    }

    /**
     * Award an achievement to a user (if not already earned).
     */
    @Transactional
    public boolean awardAchievement(User user, String type, String title,
                                     String description, String icon) {
        // Check if already earned
        if (achievementRepository.existsByUserAndAchievementType(user, type)) {
            return false;  // Already has this achievement
        }

        // Create and save achievement
        Achievement achievement = Achievement.builder()
                .user(user)
                .achievementType(type)
                .title(title)
                .description(description)
                .icon(icon)
                .earnedAt(LocalDateTime.now())
                .build();

        achievementRepository.save(achievement);
        log.info("🏆 Achievement unlocked: {} - {} for user {}", type, title, user.getUsername());

        // Publish Kafka event
        try {
            AchievementEvent event = AchievementEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .username(user.getUsername())
                    .achievementType(type)
                    .message(title + ": " + description)
                    .timestamp(LocalDateTime.now())
                    .build();
            eventProducer.sendAchievementEvent(event);
        } catch (Exception e) {
            log.warn("Failed to publish achievement event: {}", e.getMessage());
        }

        return true;
    }

    /**
     * Get count of achievements for a user.
     */
    public long getAchievementCount(User user) {
        return achievementRepository.countByUser(user);
    }

    private AchievementDTO toDTO(Achievement achievement) {
        return AchievementDTO.builder()
                .id(achievement.getId())
                .achievementType(achievement.getAchievementType())
                .title(achievement.getTitle())
                .description(achievement.getDescription())
                .icon(achievement.getIcon())
                .earnedAt(achievement.getEarnedAt())
                .build();
    }
}

