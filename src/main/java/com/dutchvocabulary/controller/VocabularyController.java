package com.dutchvocabulary.controller;

import com.dutchvocabulary.dto.VocabularyWordDTO;
import com.dutchvocabulary.model.Difficulty;
import com.dutchvocabulary.model.VocabularyWord;
import com.dutchvocabulary.service.VocabularyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VocabularyController {

    private final VocabularyService vocabularyService;

    /**
     * Get all vocabulary words.
     */
    @GetMapping
    public ResponseEntity<List<VocabularyWord>> getAllWords() {
        return ResponseEntity.ok(vocabularyService.getAllWords());
    }

    /**
     * Get a word by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VocabularyWord> getWordById(@PathVariable Long id) {
        return vocabularyService.getWordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get words by category.
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<VocabularyWord>> getWordsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(vocabularyService.getWordsByCategory(category));
    }

    /**
     * Get words by difficulty.
     */
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<VocabularyWord>> getWordsByDifficulty(@PathVariable String difficulty) {
        try {
            Difficulty diff = Difficulty.valueOf(difficulty.toUpperCase());
            return ResponseEntity.ok(vocabularyService.getWordsByDifficulty(diff));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all available categories.
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        return ResponseEntity.ok(vocabularyService.getAllCategories());
    }

    /**
     * Search words by Dutch or English term.
     */
    @GetMapping("/search")
    public ResponseEntity<List<VocabularyWord>> searchWords(@RequestParam String q) {
        return ResponseEntity.ok(vocabularyService.searchWords(q));
    }

    /**
     * Create a new vocabulary word.
     */
    @PostMapping
    public ResponseEntity<VocabularyWord> createWord(@Valid @RequestBody VocabularyWordDTO dto) {
        VocabularyWord created = vocabularyService.createWord(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing word.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VocabularyWord> updateWord(
            @PathVariable Long id,
            @Valid @RequestBody VocabularyWordDTO dto) {
        return vocabularyService.updateWord(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a word.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
        if (vocabularyService.deleteWord(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

