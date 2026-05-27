package com.dutchvocabulary.service;

import com.dutchvocabulary.dto.*;
import com.dutchvocabulary.event.AchievementEvent;
import com.dutchvocabulary.event.QuizAttemptEvent;
import com.dutchvocabulary.kafka.QuizEventProducer;
import com.dutchvocabulary.model.LearningProgress;
import com.dutchvocabulary.model.User;
import com.dutchvocabulary.model.VocabularyWord;
import com.dutchvocabulary.repository.LearningProgressRepository;
import com.dutchvocabulary.repository.UserRepository;
import com.dutchvocabulary.repository.VocabularyWordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {

    private final VocabularyWordRepository wordRepository;
    private final LearningProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final QuizEventProducer eventProducer;
    private final AchievementService achievementService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
            "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return userRepository.findByUsername(authentication.getName()).orElse(null);
    }

    /**
     * Generate a quiz with random words and multiple choice options.
     */
    public List<QuizQuestionDTO> generateQuiz(int count, String category, String difficulty) {
        List<VocabularyWord> words;

        if (category != null && !category.isBlank()) {
            words = wordRepository.findByCategory(category);
        } else {
            words = wordRepository.findAll();
        }

        // Filter by difficulty if specified
        if (difficulty != null && !difficulty.isBlank()) {
            words = words.stream()
                    .filter(w -> w.getDifficulty().name().equalsIgnoreCase(difficulty))
                    .collect(Collectors.toList());
        }

        // Shuffle and limit
        List<VocabularyWord> allWords = new ArrayList<>(words);
        Collections.shuffle(words);
        words = words.stream().limit(count).collect(Collectors.toList());

        return words.stream()
                .map(w -> toQuizQuestionWithOptions(w, allWords))
                .collect(Collectors.toList());
    }

    /**
     * Generate a smart quiz focusing on weak words (spaced repetition).
     */
    public List<QuizQuestionDTO> generateSmartQuiz(int count) {
        User user = getCurrentUser();
        List<VocabularyWord> allWords = wordRepository.findAll();

        List<VocabularyWord> quizWords = new ArrayList<>();

        if (user != null) {
            // Get words that need more practice (success rate < 70%)
            List<LearningProgress> weakProgress = progressRepository.findWeakWordsByUser(user.getId(), 0.7);
            List<VocabularyWord> weakWords = weakProgress.stream()
                    .map(LearningProgress::getWord)
                    .collect(Collectors.toList());

            // Get least recently practiced words
            List<LearningProgress> leastRecent = progressRepository.findLeastRecentlyPracticedByUser(user.getId());
            List<VocabularyWord> leastRecentWords = leastRecent.stream()
                    .map(LearningProgress::getWord)
                    .collect(Collectors.toList());

            // Add weak words (priority)
            for (VocabularyWord word : weakWords) {
                if (quizWords.size() >= count) break;
                if (!quizWords.contains(word)) {
                    quizWords.add(word);
                }
            }

            // Add least recent
            for (VocabularyWord word : leastRecentWords) {
                if (quizWords.size() >= count) break;
                if (!quizWords.contains(word)) {
                    quizWords.add(word);
                }
            }
        }

        // Fill remaining with random words
        if (quizWords.size() < count) {
            List<VocabularyWord> randomWords = wordRepository.findRandomWords(count - quizWords.size());
            for (VocabularyWord word : randomWords) {
                if (quizWords.size() >= count) break;
                if (!quizWords.contains(word)) {
                    quizWords.add(word);
                }
            }
        }

        Collections.shuffle(quizWords);

        return quizWords.stream()
                .map(w -> toQuizQuestionWithOptions(w, allWords))
                .collect(Collectors.toList());
    }

    /**
     * Submit an answer and record progress.
     */
    @Transactional
    public QuizResultDTO submitAnswer(QuizAnswerDTO answerDTO) {
        User user = getCurrentUser();

        VocabularyWord word = wordRepository.findById(answerDTO.getWordId())
                .orElseThrow(() -> new RuntimeException("Word not found: " + answerDTO.getWordId()));

        // Check if answer is correct (case-insensitive, trim whitespace)
        boolean isCorrect = word.getEnglish().trim().equalsIgnoreCase(answerDTO.getAnswer().trim());

        int currentStreak = 0;
        double successRate = 0.0;
        int previousStreak = 0;

        if (user != null) {
            // Get or create progress record for this user
            LearningProgress progress = progressRepository.findByUserAndWord(user, word)
                    .orElseGet(() -> LearningProgress.builder()
                            .user(user)
                            .word(word)
                            .correctCount(0)
                            .attemptCount(0)
                            .streak(0)
                            .build());

            previousStreak = progress.getStreak();

            // Record the attempt
            progress.recordAttempt(isCorrect);
            progressRepository.save(progress);

            currentStreak = progress.getStreak();
            successRate = progress.getSuccessRate();

            // Record daily practice (for streak tracking)
            achievementService.recordDailyPractice(user);

            // Check and award achievements
            achievementService.checkQuizAchievements(user, isCorrect, previousStreak, currentStreak, progress);

            // Publish Kafka event for analytics
            publishQuizAttemptEvent(user, word, answerDTO.getAnswer(), isCorrect, currentStreak, successRate);
        }

        return QuizResultDTO.builder()
                .wordId(word.getId())
                .dutch(word.getDutch())
                .correctAnswer(word.getEnglish())
                .userAnswer(answerDTO.getAnswer())
                .correct(isCorrect)
                .currentStreak(currentStreak)
                .successRate(successRate)
                .build();
    }

    /**
     * Publish quiz attempt event to Kafka.
     */
    private void publishQuizAttemptEvent(User user, VocabularyWord word, String userAnswer,
                                          boolean correct, int streak, double successRate) {
        try {
            QuizAttemptEvent event = QuizAttemptEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .username(user.getUsername())
                    .wordId(word.getId())
                    .dutchWord(word.getDutch())
                    .correctAnswer(word.getEnglish())
                    .userAnswer(userAnswer)
                    .correct(correct)
                    .currentStreak(streak)
                    .successRate(successRate)
                    .timestamp(LocalDateTime.now())
                    .build();

            eventProducer.sendQuizAttemptEvent(event);
        } catch (Exception e) {
            log.warn("Failed to publish quiz attempt event (Kafka may be unavailable): {}", e.getMessage());
        }
    }

    /**
     * Submit multiple answers at once.
     */
    @Transactional
    public List<QuizResultDTO> submitQuiz(List<QuizAnswerDTO> answers) {
        return answers.stream()
                .map(this::submitAnswer)
                .collect(Collectors.toList());
    }

    /**
     * Get learning statistics for current user.
     */
    public StatisticsDTO getStatistics() {
        User user = getCurrentUser();
        Long totalWords = wordRepository.count();

        if (user == null) {
            return StatisticsDTO.builder()
                    .totalWords(totalWords)
                    .wordsStudied(0L)
                    .totalAttempts(0L)
                    .totalCorrect(0L)
                    .overallSuccessRate(0.0)
                    .wordsToReview(0L)
                    .build();
        }

        Long wordsStudied = progressRepository.getWordsStudiedByUser(user.getId());
        Long totalAttempts = progressRepository.getTotalAttemptsByUser(user.getId());
        Long totalCorrect = progressRepository.getTotalCorrectByUser(user.getId());
        Double overallSuccessRate = progressRepository.getOverallSuccessRateByUser(user.getId());
        Long wordsToReview = (long) progressRepository.findWeakWordsByUser(user.getId(), 0.7).size();

        return StatisticsDTO.builder()
                .totalWords(totalWords)
                .wordsStudied(wordsStudied != null ? wordsStudied : 0L)
                .totalAttempts(totalAttempts != null ? totalAttempts : 0L)
                .totalCorrect(totalCorrect != null ? totalCorrect : 0L)
                .overallSuccessRate(overallSuccessRate != null ? overallSuccessRate * 100 : 0.0)
                .wordsToReview(wordsToReview)
                .build();
    }

    private QuizQuestionDTO toQuizQuestionWithOptions(VocabularyWord word, List<VocabularyWord> allWords) {
        // Generate 4 options including the correct answer
        List<String> options = new ArrayList<>();
        options.add(word.getEnglish());

        // Add 3 random wrong answers
        List<VocabularyWord> otherWords = allWords.stream()
                .filter(w -> !w.getId().equals(word.getId()))
                .collect(Collectors.toList());
        Collections.shuffle(otherWords);

        for (int i = 0; i < Math.min(3, otherWords.size()); i++) {
            String wrongAnswer = otherWords.get(i).getEnglish();
            if (!options.contains(wrongAnswer)) {
                options.add(wrongAnswer);
            }
        }

        // Make sure we have 4 options
        while (options.size() < 4 && options.size() < allWords.size()) {
            for (VocabularyWord w : otherWords) {
                if (!options.contains(w.getEnglish())) {
                    options.add(w.getEnglish());
                    break;
                }
            }
        }

        Collections.shuffle(options);

        return QuizQuestionDTO.builder()
                .wordId(word.getId())
                .dutch(word.getDutch())
                .category(word.getCategory())
                .difficulty(word.getDifficulty().name())
                .pronunciation(word.getPronunciation())
                .example(word.getExample())
                .options(options)
                .build();
    }
}

