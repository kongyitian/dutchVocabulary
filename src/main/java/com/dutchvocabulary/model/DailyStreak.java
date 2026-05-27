package com.dutchvocabulary.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity tracking user's daily practice streaks.
 */
@Entity
@Table(name = "daily_streaks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyStreak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentStreak = 0;  // consecutive days practiced

    @Column(nullable = false)
    @Builder.Default
    private Integer longestStreak = 0;  // personal best

    @Column
    private LocalDate lastPracticeDate;  // last date user practiced

    @Column(nullable = false)
    @Builder.Default
    private Integer totalDaysPracticed = 0;

    @Column
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * Update streak based on practice today.
     */
    public void recordPractice() {
        LocalDate today = LocalDate.now();

        if (lastPracticeDate == null) {
            // First time practicing
            currentStreak = 1;
            totalDaysPracticed = 1;
        } else if (lastPracticeDate.equals(today)) {
            // Already practiced today, no change
            return;
        } else if (lastPracticeDate.equals(today.minusDays(1))) {
            // Practiced yesterday, streak continues
            currentStreak++;
            totalDaysPracticed++;
        } else {
            // Streak broken, start over
            currentStreak = 1;
            totalDaysPracticed++;
        }

        // Update longest streak if needed
        if (currentStreak > longestStreak) {
            longestStreak = currentStreak;
        }

        lastPracticeDate = today;
        updatedAt = LocalDateTime.now();
    }

    /**
     * Check if user practiced today.
     */
    public boolean hasPracticedToday() {
        return lastPracticeDate != null && lastPracticeDate.equals(LocalDate.now());
    }
}

