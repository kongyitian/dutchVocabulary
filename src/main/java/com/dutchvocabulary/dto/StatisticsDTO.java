package com.dutchvocabulary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for overall learning statistics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDTO {

    private Long totalWords;
    private Long wordsStudied;
    private Long totalAttempts;
    private Long totalCorrect;
    private Double overallSuccessRate;
    private Long wordsToReview;  // Words with success rate < 70%
}

