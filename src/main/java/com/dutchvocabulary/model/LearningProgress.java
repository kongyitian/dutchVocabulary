package com.dutchvocabulary.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity tracking user's learning progress for each word.
 */
@Entity
@Table(name = "learning_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private VocabularyWord word;

    @Column(nullable = false)
    @Builder.Default
    private Integer correctCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer attemptCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer streak = 0;  // Consecutive correct answers

    @Column
    private LocalDateTime lastPracticed;

    @Column
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Calculate the success rate for this word.
     */
    public double getSuccessRate() {
        if (attemptCount == 0) return 0.0;
        return (double) correctCount / attemptCount * 100;
    }

    /**
     * Record a practice attempt.
     */
    public void recordAttempt(boolean correct) {
        this.attemptCount++;
        if (correct) {
            this.correctCount++;
            this.streak++;
        } else {
            this.streak = 0;
        }
        this.lastPracticed = LocalDateTime.now();
    }
}

