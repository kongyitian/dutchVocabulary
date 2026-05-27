package com.dutchvocabulary.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DailyStreak Model Tests")
class DailyStreakTest {

    private DailyStreak streak;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        streak = DailyStreak.builder()
                .user(testUser)
                .currentStreak(0)
                .longestStreak(0)
                .totalDaysPracticed(0)
                .build();
    }

    @Test
    @DisplayName("Should start streak on first practice")
    void recordPractice_FirstTime_ShouldStartStreak() {
        streak.recordPractice();

        assertThat(streak.getCurrentStreak()).isEqualTo(1);
        assertThat(streak.getLongestStreak()).isEqualTo(1);
        assertThat(streak.getTotalDaysPracticed()).isEqualTo(1);
        assertThat(streak.getLastPracticeDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Should not change when already practiced today")
    void recordPractice_AlreadyPracticedToday_ShouldNotChange() {
        streak.setLastPracticeDate(LocalDate.now());
        streak.setCurrentStreak(5);
        streak.setTotalDaysPracticed(10);

        streak.recordPractice();

        assertThat(streak.getCurrentStreak()).isEqualTo(5);
        assertThat(streak.getTotalDaysPracticed()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should continue streak when practiced yesterday")
    void recordPractice_PracticedYesterday_ShouldContinueStreak() {
        streak.setLastPracticeDate(LocalDate.now().minusDays(1));
        streak.setCurrentStreak(5);
        streak.setLongestStreak(5);
        streak.setTotalDaysPracticed(10);

        streak.recordPractice();

        assertThat(streak.getCurrentStreak()).isEqualTo(6);
        assertThat(streak.getLongestStreak()).isEqualTo(6);
        assertThat(streak.getTotalDaysPracticed()).isEqualTo(11);
    }

    @Test
    @DisplayName("Should reset streak when gap in practice")
    void recordPractice_MissedDays_ShouldResetStreak() {
        streak.setLastPracticeDate(LocalDate.now().minusDays(3));
        streak.setCurrentStreak(5);
        streak.setLongestStreak(5);
        streak.setTotalDaysPracticed(10);

        streak.recordPractice();

        assertThat(streak.getCurrentStreak()).isEqualTo(1);
        assertThat(streak.getLongestStreak()).isEqualTo(5); // Should preserve longest
        assertThat(streak.getTotalDaysPracticed()).isEqualTo(11);
    }

    @Test
    @DisplayName("Should update longest streak when current exceeds it")
    void recordPractice_NewLongest_ShouldUpdateLongestStreak() {
        streak.setLastPracticeDate(LocalDate.now().minusDays(1));
        streak.setCurrentStreak(10);
        streak.setLongestStreak(10);
        streak.setTotalDaysPracticed(20);

        streak.recordPractice();

        assertThat(streak.getCurrentStreak()).isEqualTo(11);
        assertThat(streak.getLongestStreak()).isEqualTo(11);
    }

    @Test
    @DisplayName("Should detect if practiced today")
    void hasPracticedToday_WhenPracticedToday_ShouldReturnTrue() {
        streak.setLastPracticeDate(LocalDate.now());

        assertThat(streak.hasPracticedToday()).isTrue();
    }

    @Test
    @DisplayName("Should detect if not practiced today")
    void hasPracticedToday_WhenNotPracticedToday_ShouldReturnFalse() {
        streak.setLastPracticeDate(LocalDate.now().minusDays(1));

        assertThat(streak.hasPracticedToday()).isFalse();
    }

    @Test
    @DisplayName("Should return false when never practiced")
    void hasPracticedToday_WhenNeverPracticed_ShouldReturnFalse() {
        assertThat(streak.hasPracticedToday()).isFalse();
    }

    @Test
    @DisplayName("Should update timestamp on practice")
    void recordPractice_ShouldUpdateTimestamp() {
        streak.recordPractice();

        assertThat(streak.getUpdatedAt()).isNotNull();
    }
}

