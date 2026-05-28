package com.dutchvocabulary.service;

import com.dutchvocabulary.dto.VocabularyWordDTO;
import com.dutchvocabulary.model.Difficulty;
import com.dutchvocabulary.model.VocabularyWord;
import com.dutchvocabulary.repository.VocabularyWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyWordRepository wordRepository;

    public List<VocabularyWord> getAllWords() {
        return wordRepository.findAll();
    }

    public Optional<VocabularyWord> getWordById(Long id) {
        return wordRepository.findById(id);
    }

    public List<VocabularyWord> getWordsByCategory(String category) {
        return wordRepository.findByCategory(category);
    }

    public List<VocabularyWord> getWordsByDifficulty(Difficulty difficulty) {
        return wordRepository.findByDifficulty(difficulty);
    }

    public List<String> getAllCategories() {
        return wordRepository.findAllCategories();
    }

    public List<VocabularyWord> searchWords(String searchTerm) {
        return wordRepository.searchWords(searchTerm);
    }

    @Transactional
    public VocabularyWord createWord(VocabularyWordDTO dto) {
        VocabularyWord word = VocabularyWord.builder()
                .dutch(dto.getDutch())
                .english(dto.getEnglish())
                .example(dto.getExample())
                .exampleTranslation(dto.getExampleTranslation())
                .category(dto.getCategory())
                .difficulty(dto.getDifficulty() != null ? dto.getDifficulty() : Difficulty.A1)
                .pronunciation(dto.getPronunciation())
                .build();
        return wordRepository.save(word);
    }

    @Transactional
    public Optional<VocabularyWord> updateWord(Long id, VocabularyWordDTO dto) {
        return wordRepository.findById(id).map(word -> {
            word.setDutch(dto.getDutch());
            word.setEnglish(dto.getEnglish());
            word.setExample(dto.getExample());
            word.setExampleTranslation(dto.getExampleTranslation());
            word.setCategory(dto.getCategory());
            if (dto.getDifficulty() != null) {
                word.setDifficulty(dto.getDifficulty());
            }
            word.setPronunciation(dto.getPronunciation());
            return wordRepository.save(word);
        });
    }

    @Transactional
    public boolean deleteWord(Long id) {
        if (wordRepository.existsById(id)) {
            wordRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<VocabularyWord> getRandomWords(int count) {
        return wordRepository.findRandomWords(count);
    }
}


