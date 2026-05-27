package com.dutchvocabulary.service;

import com.dutchvocabulary.dto.AchievementDTO;
import com.dutchvocabulary.kafka.QuizEventProducer;
import com.dutchvocabulary.model.Achievement;
import com.dutchvocabulary.model.DailyStreak;
import com.dutchvocabulary.model.LearningProgress;
import com.dutchvocabulary.model.User;
import com.dutchvocabulary.model.VocabularyWord;
import com.dutchvocabulary.repository.AchievementRepository;
import com.dutchvocabulary.repository.DailyStreakRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AchievementService Unit Tests")
class AchievementServiceTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private DailyStreakRepository dailyStreakRepository;

    @Mock
    private QuizEventProducer eventProducer;

    @InjectMocks
    private AchievementService achievementService;

    private User testUser;
    private Achievement testAchievement;
    private DailyStreak testStreak;
    private LearningProgress testProgress;
    private VocabularyWord testWord;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .displayName("Test User")
                .build();

        testWord = VocabularyWord.builder()
                .id(1L)
                .dutch("hallo")
                .english("hello")
                .build();

        testAchievement = Achievement.builder()
                .id(1L)
                .user(testUser)
                .achievementType("FIRST_CORRECT")
                .title("First Steps")
                .description("You got your first correct answer!")
                .icon("🎉")
                .earnedAt(LocalDateTime.now())
                .build();

        testStreak = DailyStreak.builder()
                .id(1L)
                .user(testUser)
                .currentStreak(5)
                .longestStreak(10)
                .totalDaysPracticed(30)
                .lastPracticeDate(LocalDate.now())
                .build();

        testProgress = LearningProgress.builder()
                .id(1L)
                .user(testUser)
                .word(testWord)
                .correctCount(1)
                .attemptCount(1)
                .streak(1)
                .build();
    }

    @Test
    @DisplayName("Should get user achievements")
    void getUserAchievements_ShouldReturnAchievementDTOs() {
        List<Achievement> achievements = Arrays.asList(testAchievement);
        when(achievementRepository.findByUserOrderByEarnedAtDesc(testUser)).thenReturn(achievements);

        List<AchievementDTO> result = achievementService.getUserAchievements(testUser);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("First Steps");
        assertThat(result.get(0).getIcon()).isEqualTo("🎉");
    }

    @Test
    @DisplayName("Should get daily streak for user")
    void getDailyStreak_WhenExists_ShouldReturnStreak() {
        when(dailyStreakRepository.findByUser(testUser)).thenReturn(Optional.of(testStreak));

        DailyStreak result = achievementService.getDailyStreak(testUser);

        assertThat(result.getCurrentStreak()).isEqualTo(5);
        assertThat(result.getLongestStreak()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should return new streak when none exists")
    void getDailyStreak_WhenNotExists_ShouldReturnNewStreak() {
        when(dailyStreakRepository.findByUser(testUser)).thenReturn(Optional.empty());

        DailyStreak result = achievementService.getDailyStreak(testUser);

        assertThat(result.getCurrentStreak()).isEqualTo(0);
        assertThat(result.getLongestStreak()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should record daily practice and update streak")
    void recordDailyPractice_ShouldUpdateStreak() {
        DailyStreak streakWithoutPracticeToday = DailyStreak.builder()
                .user(testUser)
                .currentStreak(5)
                .longestStreak(10)
                .totalDaysPracticed(30)
                .lastPracticeDate(LocalDate.now().minusDays(1))
                .build();

        when(dailyStreakRepository.findByUser(testUser)).thenReturn(Optional.of(streakWithoutPracticeToday));
        when(dailyStreakRepository.save(any(DailyStreak.class))).thenAnswer(i -> i.getArgument(0));

        DailyStreak result = achievementService.recordDailyPractice(testUser);

        assertThat(result.getCurrentStreak()).isEqualTo(6);
        assertThat(result.getTotalDaysPracticed()).isEqualTo(31);
        verify(dailyStreakRepository).save(any(DailyStreak.class));
    }

    @Test
    @DisplayName("Should award first correct achievement")
    void checkQuizAchievements_FirstCorrect_ShouldAwardAchievement() {
        LearningProgress firstCorrectProgress = LearningProgress.builder()
                .user(testUser)
                .word(testWord)
                .correctCount(1)
                .attemptCount(1)
                .streak(1)
                .build();

        lenient().when(achievementRepository.existsByUserAndAchievementType(any(User.class), anyString())).thenReturn(false);
        when(achievementRepository.save(any(Achievement.class))).thenAnswer(i -> i.getArgument(0));

        achievementService.checkQuizAchievements(testUser, true, 0, 1, firstCorrectProgress);

        verify(achievementRepository).save(argThat(a -> a.getAchievementType().equals("FIRST_CORRECT")));
    }

    @Test
    @DisplayName("Should award streak 5 achievement")
    void checkQuizAchievements_Streak5_ShouldAwardAchievement() {
        lenient().when(achievementRepository.existsByUserAndAchievementType(any(User.class), anyString())).thenReturn(false);
        when(achievementRepository.save(any(Achievement.class))).thenAnswer(i -> i.getArgument(0));

        achievementService.checkQuizAchievements(testUser, true, 4, 5, testProgress);

        verify(achievementRepository).save(argThat(a -> a.getAchievementType().equals("STREAK_5")));
    }

    @Test
    @DisplayName("Should award streak 10 achievement")
    void checkQuizAchievements_Streak10_ShouldAwardAchievement() {
        lenient().when(achievementRepository.existsByUserAndAchievementType(any(User.class), anyString())).thenReturn(false);
        when(achievementRepository.save(any(Achievement.class))).thenAnswer(i -> i.getArgument(0));

        achievementService.checkQuizAchievements(testUser, true, 9, 10, testProgress);

        verify(achievementRepository).save(argThat(a -> a.getAchievementType().equals("STREAK_10")));
    }

    @Test
    @DisplayName("Should not award duplicate achievement")
    void awardAchievement_WhenAlreadyEarned_ShouldReturnFalse() {
        when(achievementRepository.existsByUserAndAchievementType(testUser, "FIRST_CORRECT")).thenReturn(true);

        boolean result = achievementService.awardAchievement(testUser, "FIRST_CORRECT",
                "First Steps", "Test", "🎉");

        assertThat(result).isFalse();
        verify(achievementRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should award new achievement")
    void awardAchievement_WhenNew_ShouldSaveAndReturnTrue() {
        when(achievementRepository.existsByUserAndAchievementType(testUser, "NEW_ACHIEVEMENT")).thenReturn(false);
        when(achievementRepository.save(any(Achievement.class))).thenReturn(testAchievement);

        boolean result = achievementService.awardAchievement(testUser, "NEW_ACHIEVEMENT",
                "Test Title", "Test Description", "🏆");

        assertThat(result).isTrue();
        verify(achievementRepository).save(any(Achievement.class));
    }

    @Test
    @DisplayName("Should get achievement count")
    void getAchievementCount_ShouldReturnCount() {
        when(achievementRepository.countByUser(testUser)).thenReturn(5L);

        long result = achievementService.getAchievementCount(testUser);

        assertThat(result).isEqualTo(5L);
    }

    @Test
    @DisplayName("Should award word master achievement when mastered")
    void checkQuizAchievements_WordMastered_ShouldAwardAchievement() {
        LearningProgress masteredProgress = LearningProgress.builder()
                .user(testUser)
                .word(testWord)
                .correctCount(10)
                .attemptCount(10)
                .streak(10)
                .build();

        // Use lenient stubbing since multiple achievements may be checked
        lenient().when(achievementRepository.existsByUserAndAchievementType(any(User.class), anyString())).thenReturn(false);
        when(achievementRepository.save(any(Achievement.class))).thenAnswer(i -> i.getArgument(0));

        achievementService.checkQuizAchievements(testUser, true, 9, 10, masteredProgress);

        verify(achievementRepository).save(argThat(a -> a.getAchievementType().equals("WORD_MASTER_1")));
    }
}




