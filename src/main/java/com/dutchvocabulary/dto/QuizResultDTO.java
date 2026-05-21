package com.dutchvocabulary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for quiz answer result.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResultDTO {

    private Long wordId;
    private String dutch;
    private String correctAnswer;
    private String userAnswer;
    private boolean correct;
    private int currentStreak;
    private double successRate;
}

