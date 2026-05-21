package com.dutchvocabulary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for quiz questions - shows the Dutch word with multiple choice options.
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
    private List<String> options;  // 4 multiple choice options
}


