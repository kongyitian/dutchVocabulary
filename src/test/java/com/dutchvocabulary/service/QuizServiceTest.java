package com.dutchvocabulary.service;

import com.dutchvocabulary.dto.QuizAnswerDTO;
import com.dutchvocabulary.dto.QuizQuestionDTO;
import com.dutchvocabulary.dto.QuizResultDTO;
import com.dutchvocabulary.dto.StatisticsDTO;
import com.dutchvocabulary.kafka.QuizEventProducer;
import com.dutchvocabulary.model.Difficulty;
import com.dutchvocabulary.model.LearningProgress;
import com.dutchvocabulary.model.User;
import com.dutchvocabulary.model.VocabularyWord;
import com.dutchvocabulary.repository.LearningProgressRepository;
import com.dutchvocabulary.repository.UserRepository;
import com.dutchvocabulary.repository.VocabularyWordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuizService Unit Tests")
class QuizServiceTest {

    @Mock
    private VocabularyWordRepository wordRepository;

    @Mock
    private LearningProgressRepository progressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuizEventProducer eventProducer;

    @Mock
    private AchievementService achievementService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private QuizService quizService;

    private VocabularyWord testWord1;
    private VocabularyWord testWord2;
    private VocabularyWord testWord3;
    private User testUser;

    @BeforeEach
    void setUp() {
        testWord1 = VocabularyWord.builder()
                .id(1L)
                .dutch("hallo")
                .english("hello")
                .category("greetings")
                .difficulty(Difficulty.A1)
                .example("Hallo!")
                .build();

        testWord2 = VocabularyWord.builder()
                .id(2L)
                .dutch("goedemorgen")
                .english("good morning")
                .category("greetings")
                .difficulty(Difficulty.A1)
                .build();

        testWord3 = VocabularyWord.builder()
                .id(3L)
                .dutch("eten")
                .english("to eat")
                .category("verbs")
                .difficulty(Difficulty.B1)
                .build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .displayName("Test User")
                .build();
    }

    private void setupAnonymousUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        SecurityContextHolder.setContext(securityContext);
    }

    private void setupAuthenticatedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testUser.getUsername());
        when(authentication.getName()).thenReturn(testUser.getUsername());
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
    }

    @Test
    @DisplayName("Should generate quiz with specified count")
    void generateQuiz_ShouldReturnQuestionsWithOptions() {
        List<VocabularyWord> allWords = Arrays.asList(testWord1, testWord2, testWord3);
        when(wordRepository.findAll()).thenReturn(allWords);

        List<QuizQuestionDTO> result = quizService.generateQuiz(2, null, null);

        assertThat(result).hasSizeLessThanOrEqualTo(2);
        assertThat(result.get(0).getOptions()).isNotEmpty();
    }

    @Test
    @DisplayName("Should generate quiz filtered by category")
    void generateQuiz_WithCategory_ShouldFilterByCategory() {
        List<VocabularyWord> greetings = Arrays.asList(testWord1, testWord2);
        when(wordRepository.findByCategory("greetings")).thenReturn(greetings);

        List<QuizQuestionDTO> result = quizService.generateQuiz(2, "greetings", null);

        assertThat(result).hasSizeLessThanOrEqualTo(2);
        result.forEach(q -> assertThat(q.getCategory()).isEqualTo("greetings"));
    }

    @Test
    @DisplayName("Should generate quiz filtered by difficulty")
    void generateQuiz_WithDifficulty_ShouldFilterByDifficulty() {
        List<VocabularyWord> allWords = Arrays.asList(testWord1, testWord2, testWord3);
        when(wordRepository.findAll()).thenReturn(allWords);

        List<QuizQuestionDTO> result = quizService.generateQuiz(3, null, "A1");

        // All questions should be EASY difficulty
        result.forEach(q -> assertThat(q.getDifficulty()).isEqualTo("A1"));
    }

    @Test
    @DisplayName("Should submit correct answer and return success")
    void submitAnswer_WhenCorrect_ShouldReturnCorrectResult() {
        setupAuthenticatedUser();

        when(wordRepository.findById(1L)).thenReturn(Optional.of(testWord1));
        when(progressRepository.findByUserAndWord(any(User.class), any(VocabularyWord.class)))
                .thenReturn(Optional.empty());
        when(progressRepository.save(any(LearningProgress.class))).thenAnswer(i -> i.getArgument(0));
        // recordDailyPractice returns DailyStreak, not void
        when(achievementService.recordDailyPractice(any(User.class))).thenReturn(null);

        QuizAnswerDTO answerDTO = QuizAnswerDTO.builder()
                .wordId(1L)
                .answer("hello")
                .build();

        QuizResultDTO result = quizService.submitAnswer(answerDTO);

        assertThat(result.isCorrect()).isTrue();
        assertThat(result.getDutch()).isEqualTo("hallo");
        assertThat(result.getCorrectAnswer()).isEqualTo("hello");
        assertThat(result.getUserAnswer()).isEqualTo("hello");
    }

    @Test
    @DisplayName("Should submit incorrect answer and return failure")
    void submitAnswer_WhenIncorrect_ShouldReturnIncorrectResult() {
        setupAuthenticatedUser();

        when(wordRepository.findById(1L)).thenReturn(Optional.of(testWord1));
        when(progressRepository.findByUserAndWord(any(User.class), any(VocabularyWord.class)))
                .thenReturn(Optional.empty());
        when(progressRepository.save(any(LearningProgress.class))).thenAnswer(i -> i.getArgument(0));
        when(achievementService.recordDailyPractice(any(User.class))).thenReturn(null);

        QuizAnswerDTO answerDTO = QuizAnswerDTO.builder()
                .wordId(1L)
                .answer("goodbye")
                .build();

        QuizResultDTO result = quizService.submitAnswer(answerDTO);

        assertThat(result.isCorrect()).isFalse();
        assertThat(result.getUserAnswer()).isEqualTo("goodbye");
        assertThat(result.getCorrectAnswer()).isEqualTo("hello");
    }

    @Test
    @DisplayName("Should handle case-insensitive answer matching")
    void submitAnswer_CaseInsensitive_ShouldAcceptCorrectAnswer() {
        setupAuthenticatedUser();

        when(wordRepository.findById(1L)).thenReturn(Optional.of(testWord1));
        when(progressRepository.findByUserAndWord(any(User.class), any(VocabularyWord.class)))
                .thenReturn(Optional.empty());
        when(progressRepository.save(any(LearningProgress.class))).thenAnswer(i -> i.getArgument(0));
        when(achievementService.recordDailyPractice(any(User.class))).thenReturn(null);

        QuizAnswerDTO answerDTO = QuizAnswerDTO.builder()
                .wordId(1L)
                .answer("HELLO")
                .build();

        QuizResultDTO result = quizService.submitAnswer(answerDTO);

        assertThat(result.isCorrect()).isTrue();
    }

    @Test
    @DisplayName("Should handle trimmed answer matching")
    void submitAnswer_WithWhitespace_ShouldTrimAndMatch() {
        setupAuthenticatedUser();

        when(wordRepository.findById(1L)).thenReturn(Optional.of(testWord1));
        when(progressRepository.findByUserAndWord(any(User.class), any(VocabularyWord.class)))
                .thenReturn(Optional.empty());
        when(progressRepository.save(any(LearningProgress.class))).thenAnswer(i -> i.getArgument(0));
        when(achievementService.recordDailyPractice(any(User.class))).thenReturn(null);

        QuizAnswerDTO answerDTO = QuizAnswerDTO.builder()
                .wordId(1L)
                .answer("  hello  ")
                .build();

        QuizResultDTO result = quizService.submitAnswer(answerDTO);

        assertThat(result.isCorrect()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when word not found")
    void submitAnswer_WhenWordNotFound_ShouldThrowException() {
        setupAuthenticatedUser();
        when(wordRepository.findById(999L)).thenReturn(Optional.empty());

        QuizAnswerDTO answerDTO = QuizAnswerDTO.builder()
                .wordId(999L)
                .answer("hello")
                .build();

        assertThatThrownBy(() -> quizService.submitAnswer(answerDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Word not found");
    }

    @Test
    @DisplayName("Should submit multiple answers")
    void submitQuiz_ShouldProcessAllAnswers() {
        setupAuthenticatedUser();

        when(wordRepository.findById(1L)).thenReturn(Optional.of(testWord1));
        when(wordRepository.findById(2L)).thenReturn(Optional.of(testWord2));
        when(progressRepository.findByUserAndWord(any(User.class), any(VocabularyWord.class)))
                .thenReturn(Optional.empty());
        when(progressRepository.save(any(LearningProgress.class))).thenAnswer(i -> i.getArgument(0));
        when(achievementService.recordDailyPractice(any(User.class))).thenReturn(null);

        List<QuizAnswerDTO> answers = Arrays.asList(
                QuizAnswerDTO.builder().wordId(1L).answer("hello").build(),
                QuizAnswerDTO.builder().wordId(2L).answer("wrong answer").build()
        );

        List<QuizResultDTO> results = quizService.submitQuiz(answers);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).isCorrect()).isTrue();
        assertThat(results.get(1).isCorrect()).isFalse();
    }

    @Test
    @DisplayName("Should get statistics for anonymous user")
    void getStatistics_ForAnonymousUser_ShouldReturnEmptyStats() {
        setupAnonymousUser();
        when(wordRepository.count()).thenReturn(100L);

        StatisticsDTO result = quizService.getStatistics();

        assertThat(result.getTotalWords()).isEqualTo(100L);
        assertThat(result.getWordsStudied()).isEqualTo(0L);
        assertThat(result.getTotalAttempts()).isEqualTo(0L);
        assertThat(result.getTotalCorrect()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should get statistics for authenticated user")
    void getStatistics_ForAuthenticatedUser_ShouldReturnUserStats() {
        setupAuthenticatedUser();

        when(wordRepository.count()).thenReturn(100L);
        when(progressRepository.getWordsStudiedByUser(1L)).thenReturn(50L);
        when(progressRepository.getTotalAttemptsByUser(1L)).thenReturn(200L);
        when(progressRepository.getTotalCorrectByUser(1L)).thenReturn(150L);
        when(progressRepository.getOverallSuccessRateByUser(1L)).thenReturn(0.75);
        when(progressRepository.findWeakWordsByUser(anyLong(), anyDouble())).thenReturn(Arrays.asList());

        StatisticsDTO result = quizService.getStatistics();

        assertThat(result.getTotalWords()).isEqualTo(100L);
        assertThat(result.getWordsStudied()).isEqualTo(50L);
        assertThat(result.getTotalAttempts()).isEqualTo(200L);
        assertThat(result.getTotalCorrect()).isEqualTo(150L);
        assertThat(result.getOverallSuccessRate()).isEqualTo(75.0);
    }

    @Test
    @DisplayName("Should generate smart quiz for authenticated user")
    void generateSmartQuiz_ForAuthenticatedUser_ShouldPrioritizeWeakWords() {
        setupAuthenticatedUser();

        List<VocabularyWord> allWords = Arrays.asList(testWord1, testWord2, testWord3);
        when(wordRepository.findAll()).thenReturn(allWords);
        when(progressRepository.findWeakWordsByUser(anyLong(), anyDouble())).thenReturn(Arrays.asList());
        when(progressRepository.findLeastRecentlyPracticedByUser(anyLong())).thenReturn(Arrays.asList());
        when(wordRepository.findRandomWords(anyInt())).thenReturn(Arrays.asList(testWord1));

        List<QuizQuestionDTO> result = quizService.generateSmartQuiz(5);

        assertThat(result).isNotEmpty();
    }
}







