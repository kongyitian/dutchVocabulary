package com.dutchvocabulary.controller;

import com.dutchvocabulary.dto.*;
import com.dutchvocabulary.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuizController {

    private final QuizService quizService;

    /**
     * Generate a random quiz.
     * @param count Number of questions (default: 10)
     * @param category Optional category filter
     * @param difficulty Optional difficulty filter (EASY, MEDIUM, HARD)
     */
    @GetMapping
    public ResponseEntity<List<QuizQuestionDTO>> generateQuiz(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty) {
        return ResponseEntity.ok(quizService.generateQuiz(count, category, difficulty));
    }

    /**
     * Generate a smart quiz focusing on weak words and spaced repetition.
     * @param count Number of questions (default: 10)
     */
    @GetMapping("/smart")
    public ResponseEntity<List<QuizQuestionDTO>> generateSmartQuiz(
            @RequestParam(defaultValue = "10") int count) {
        return ResponseEntity.ok(quizService.generateSmartQuiz(count));
    }

    /**
     * Submit an answer to a single question.
     */
    @PostMapping("/answer")
    public ResponseEntity<QuizResultDTO> submitAnswer(@Valid @RequestBody QuizAnswerDTO answerDTO) {
        return ResponseEntity.ok(quizService.submitAnswer(answerDTO));
    }

    /**
     * Submit answers for an entire quiz.
     */
    @PostMapping("/submit")
    public ResponseEntity<List<QuizResultDTO>> submitQuiz(@Valid @RequestBody List<QuizAnswerDTO> answers) {
        return ResponseEntity.ok(quizService.submitQuiz(answers));
    }

    /**
     * Get learning statistics.
     */
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsDTO> getStatistics() {
        return ResponseEntity.ok(quizService.getStatistics());
    }
}

