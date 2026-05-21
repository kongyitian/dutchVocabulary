package com.dutchvocabulary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for quiz questions - shows the Dutch word and expects English answer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestionDTO {

    private Long wordId;
    private String dutch;
    private String category;
    private String difficulty;
    private String pronunciation;
    private String example;  // Optional hint
}

