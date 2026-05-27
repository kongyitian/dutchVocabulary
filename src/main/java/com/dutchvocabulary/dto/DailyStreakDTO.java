package com.dutchvocabulary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyStreakDTO {
    private Integer currentStreak;
    private Integer longestStreak;
    private Integer totalDaysPracticed;
    private LocalDate lastPracticeDate;
    private boolean practicedToday;
}

