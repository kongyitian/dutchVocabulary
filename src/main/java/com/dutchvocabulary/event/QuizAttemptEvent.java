package com.dutchvocabulary.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when a user answers a quiz question.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttemptEvent {
    private String eventId;
    private Long userId;
    private String username;
    private Long wordId;
    private String dutchWord;
    private String correctAnswer;
    private String userAnswer;
    private boolean correct;
    private int currentStreak;
    private double successRate;
    private LocalDateTime timestamp;
}

