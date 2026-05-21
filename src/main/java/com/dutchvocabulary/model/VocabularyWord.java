package com.dutchvocabulary.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a Dutch vocabulary word with its English translation.
 */
@Entity
@Table(name = "vocabulary_words")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VocabularyWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Dutch word is required")
    @Column(nullable = false)
    private String dutch;

    @NotBlank(message = "English translation is required")
    @Column(nullable = false)
    private String english;

    @Column
    private String example;  // Example sentence in Dutch

    @Column
    private String exampleTranslation;  // Example sentence translated to English

    @Column
    private String category;  // e.g., "verbs", "nouns", "adjectives", "common phrases"

    @Enumerated(EnumType.STRING)
    @Column
    @Builder.Default
    private Difficulty difficulty = Difficulty.MEDIUM;

    @Column
    private String pronunciation;  // Optional pronunciation guide

    @Column
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

