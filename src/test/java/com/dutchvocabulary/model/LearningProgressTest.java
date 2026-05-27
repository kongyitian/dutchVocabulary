package com.dutchvocabulary.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LearningProgress Model Tests")
class LearningProgressTest {

    private LearningProgress progress;
    private User testUser;
    private VocabularyWord testWord;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        testWord = VocabularyWord.builder()
                .id(1L)
                .dutch("hallo")
                .english("hello")
                .build();

        progress = LearningProgress.builder()
                .user(testUser)
                .word(testWord)
                .correctCount(0)
                .attemptCount(0)
                .streak(0)
                .build();
    }

    @Test
    @DisplayName("Should calculate success rate correctly")
    void getSuccessRate_ShouldCalculateCorrectly() {
        progress.setCorrectCount(7);
        progress.setAttemptCount(10);

        double rate = progress.getSuccessRate();

        assertThat(rate).isEqualTo(70.0);
    }

    @Test
    @DisplayName("Should return 0 success rate when no attempts")
    void getSuccessRate_WhenNoAttempts_ShouldReturnZero() {
        double rate = progress.getSuccessRate();

        assertThat(rate).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should increment streak on correct attempt")
    void recordAttempt_WhenCorrect_ShouldIncrementStreak() {
        progress.recordAttempt(true);

        assertThat(progress.getStreak()).isEqualTo(1);
        assertThat(progress.getCorrectCount()).isEqualTo(1);
        assertThat(progress.getAttemptCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should reset streak on incorrect attempt")
    void recordAttempt_WhenIncorrect_ShouldResetStreak() {
        progress.setStreak(5);
        progress.setCorrectCount(5);
        progress.setAttemptCount(5);

        progress.recordAttempt(false);

        assertThat(progress.getStreak()).isEqualTo(0);
        assertThat(progress.getCorrectCount()).isEqualTo(5);
        assertThat(progress.getAttemptCount()).isEqualTo(6);
    }

    @Test
    @DisplayName("Should update lastPracticed on attempt")
    void recordAttempt_ShouldUpdateLastPracticed() {
        assertThat(progress.getLastPracticed()).isNull();

        progress.recordAttempt(true);

        assertThat(progress.getLastPracticed()).isNotNull();
    }

    @Test
    @DisplayName("Should handle multiple correct attempts")
    void recordAttempt_MultipleCorrect_ShouldBuildStreak() {
        progress.recordAttempt(true);
        progress.recordAttempt(true);
        progress.recordAttempt(true);

        assertThat(progress.getStreak()).isEqualTo(3);
        assertThat(progress.getCorrectCount()).isEqualTo(3);
        assertThat(progress.getAttemptCount()).isEqualTo(3);
        assertThat(progress.getSuccessRate()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Should correctly track mixed attempts")
    void recordAttempt_Mixed_ShouldTrackCorrectly() {
        progress.recordAttempt(true);  // streak: 1
        progress.recordAttempt(true);  // streak: 2
        progress.recordAttempt(false); // streak: 0
        progress.recordAttempt(true);  // streak: 1

        assertThat(progress.getStreak()).isEqualTo(1);
        assertThat(progress.getCorrectCount()).isEqualTo(3);
        assertThat(progress.getAttemptCount()).isEqualTo(4);
        assertThat(progress.getSuccessRate()).isEqualTo(75.0);
    }
}

