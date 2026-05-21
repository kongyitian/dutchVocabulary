package com.dutchvocabulary.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for submitting an answer to a quiz question.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAnswerDTO {

    @NotNull(message = "Word ID is required")
    private Long wordId;

    @NotNull(message = "Answer is required")
    private String answer;
}

