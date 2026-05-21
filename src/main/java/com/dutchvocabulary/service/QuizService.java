package com.dutchvocabulary.service;

import com.dutchvocabulary.dto.*;
import com.dutchvocabulary.model.LearningProgress;
import com.dutchvocabulary.model.VocabularyWord;
import com.dutchvocabulary.repository.LearningProgressRepository;
import com.dutchvocabulary.repository.VocabularyWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final VocabularyWordRepository wordRepository;
    private final LearningProgressRepository progressRepository;

    /**
     * Generate a quiz with random words.
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
        Collections.shuffle(words);
        words = words.stream().limit(count).collect(Collectors.toList());

        return words.stream()
                .map(this::toQuizQuestion)
                .collect(Collectors.toList());
    }

    /**
     * Generate a smart quiz focusing on weak words (spaced repetition).
     */
    public List<QuizQuestionDTO> generateSmartQuiz(int count) {
        // Get words that need more practice (success rate < 70%)
        List<LearningProgress> weakProgress = progressRepository.findWeakWords(0.7);
        List<VocabularyWord> weakWords = weakProgress.stream()
                .map(LearningProgress::getWord)
                .collect(Collectors.toList());

        // Get least recently practiced words
        List<LearningProgress> leastRecent = progressRepository.findLeastRecentlyPracticed();
        List<VocabularyWord> leastRecentWords = leastRecent.stream()
                .map(LearningProgress::getWord)
                .collect(Collectors.toList());

        // Combine weak words first, then least recent, then random
        List<VocabularyWord> quizWords = new ArrayList<>();

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
                .map(this::toQuizQuestion)
                .collect(Collectors.toList());
    }

    /**
     * Submit an answer and record progress.
     */
    @Transactional
    public QuizResultDTO submitAnswer(QuizAnswerDTO answerDTO) {
        VocabularyWord word = wordRepository.findById(answerDTO.getWordId())
                .orElseThrow(() -> new RuntimeException("Word not found: " + answerDTO.getWordId()));

        // Check if answer is correct (case-insensitive, trim whitespace)
        boolean isCorrect = word.getEnglish().trim().equalsIgnoreCase(answerDTO.getAnswer().trim());

        // Get or create progress record
        LearningProgress progress = progressRepository.findByWord(word)
                .orElseGet(() -> LearningProgress.builder()
                        .word(word)
                        .correctCount(0)
                        .attemptCount(0)
                        .streak(0)
                        .build());

        // Record the attempt
        progress.recordAttempt(isCorrect);
        progressRepository.save(progress);

        return QuizResultDTO.builder()
                .wordId(word.getId())
                .dutch(word.getDutch())
                .correctAnswer(word.getEnglish())
                .userAnswer(answerDTO.getAnswer())
                .correct(isCorrect)
                .currentStreak(progress.getStreak())
                .successRate(progress.getSuccessRate())
                .build();
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
     * Get learning statistics.
     */
    public StatisticsDTO getStatistics() {
        Long totalWords = wordRepository.count();
        Long wordsStudied = progressRepository.getWordsStudied();
        Long totalAttempts = progressRepository.getTotalAttempts();
        Long totalCorrect = progressRepository.getTotalCorrect();
        Double overallSuccessRate = progressRepository.getOverallSuccessRate();
        Long wordsToReview = (long) progressRepository.findWeakWords(0.7).size();

        return StatisticsDTO.builder()
                .totalWords(totalWords)
                .wordsStudied(wordsStudied != null ? wordsStudied : 0L)
                .totalAttempts(totalAttempts != null ? totalAttempts : 0L)
                .totalCorrect(totalCorrect != null ? totalCorrect : 0L)
                .overallSuccessRate(overallSuccessRate != null ? overallSuccessRate * 100 : 0.0)
                .wordsToReview(wordsToReview)
                .build();
    }

    private QuizQuestionDTO toQuizQuestion(VocabularyWord word) {
        return QuizQuestionDTO.builder()
                .wordId(word.getId())
                .dutch(word.getDutch())
                .category(word.getCategory())
                .difficulty(word.getDifficulty().name())
                .pronunciation(word.getPronunciation())
                .example(word.getExample())
                .build();
    }
}

