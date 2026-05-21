package com.dutchvocabulary.dto;

import com.dutchvocabulary.model.Difficulty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VocabularyWordDTO {

    private Long id;

    @NotBlank(message = "Dutch word is required")
    private String dutch;

    @NotBlank(message = "English translation is required")
    private String english;

    private String example;
    private String exampleTranslation;
    private String category;
    private Difficulty difficulty;
    private String pronunciation;
}

