package com.dutchvocabulary.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when a user earns an achievement.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementEvent {
    private String eventId;
    private Long userId;
    private String username;
    private String achievementType;  // STREAK_5, STREAK_10, FIRST_CORRECT, WORD_MASTERED
    private String message;
    private LocalDateTime timestamp;
}

