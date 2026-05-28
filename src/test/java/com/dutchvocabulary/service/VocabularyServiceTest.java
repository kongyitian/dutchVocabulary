package com.dutchvocabulary.service;

import com.dutchvocabulary.dto.VocabularyWordDTO;
import com.dutchvocabulary.model.Difficulty;
import com.dutchvocabulary.model.VocabularyWord;
import com.dutchvocabulary.repository.VocabularyWordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VocabularyService Unit Tests")
class VocabularyServiceTest {

    @Mock
    private VocabularyWordRepository wordRepository;

    @InjectMocks
    private VocabularyService vocabularyService;

    private VocabularyWord testWord;
    private VocabularyWordDTO testWordDTO;

    @BeforeEach
    void setUp() {
        testWord = VocabularyWord.builder()
                .id(1L)
                .dutch("hallo")
                .english("hello")
                .category("greetings")
                .difficulty(Difficulty.A1)
                .example("Hallo, hoe gaat het?")
                .exampleTranslation("Hello, how are you?")
                .pronunciation("hah-LOH")
                .build();

        testWordDTO = VocabularyWordDTO.builder()
                .dutch("goedemorgen")
                .english("good morning")
                .category("greetings")
                .difficulty(Difficulty.A1)
                .example("Goedemorgen!")
                .exampleTranslation("Good morning!")
                .pronunciation("khoo-duh-MOR-khun")
                .build();
    }

    @Test
    @DisplayName("Should get all words")
    void getAllWords_ShouldReturnAllWords() {
        List<VocabularyWord> words = Arrays.asList(testWord);
        when(wordRepository.findAll()).thenReturn(words);

        List<VocabularyWord> result = vocabularyService.getAllWords();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDutch()).isEqualTo("hallo");
        verify(wordRepository).findAll();
    }

    @Test
    @DisplayName("Should get word by ID")
    void getWordById_WhenExists_ShouldReturnWord() {
        when(wordRepository.findById(1L)).thenReturn(Optional.of(testWord));

        Optional<VocabularyWord> result = vocabularyService.getWordById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getDutch()).isEqualTo("hallo");
    }

    @Test
    @DisplayName("Should return empty when word not found")
    void getWordById_WhenNotExists_ShouldReturnEmpty() {
        when(wordRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<VocabularyWord> result = vocabularyService.getWordById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should get words by category")
    void getWordsByCategory_ShouldReturnMatchingWords() {
        List<VocabularyWord> words = Arrays.asList(testWord);
        when(wordRepository.findByCategory("greetings")).thenReturn(words);

        List<VocabularyWord> result = vocabularyService.getWordsByCategory("greetings");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("greetings");
    }

    @Test
    @DisplayName("Should get words by difficulty")
    void getWordsByDifficulty_ShouldReturnMatchingWords() {
        List<VocabularyWord> words = Arrays.asList(testWord);
        when(wordRepository.findByDifficulty(Difficulty.A1)).thenReturn(words);

        List<VocabularyWord> result = vocabularyService.getWordsByDifficulty(Difficulty.A1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDifficulty()).isEqualTo(Difficulty.A1);
    }

    @Test
    @DisplayName("Should get all categories")
    void getAllCategories_ShouldReturnDistinctCategories() {
        List<String> categories = Arrays.asList("greetings", "verbs", "nouns");
        when(wordRepository.findAllCategories()).thenReturn(categories);

        List<String> result = vocabularyService.getAllCategories();

        assertThat(result).hasSize(3);
        assertThat(result).contains("greetings", "verbs", "nouns");
    }

    @Test
    @DisplayName("Should search words")
    void searchWords_ShouldReturnMatchingWords() {
        List<VocabularyWord> words = Arrays.asList(testWord);
        when(wordRepository.searchWords("hallo")).thenReturn(words);

        List<VocabularyWord> result = vocabularyService.searchWords("hallo");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDutch()).isEqualTo("hallo");
    }

    @Test
    @DisplayName("Should create a new word")
    void createWord_ShouldSaveAndReturnWord() {
        VocabularyWord savedWord = VocabularyWord.builder()
                .id(2L)
                .dutch(testWordDTO.getDutch())
                .english(testWordDTO.getEnglish())
                .category(testWordDTO.getCategory())
                .difficulty(testWordDTO.getDifficulty())
                .build();

        when(wordRepository.save(any(VocabularyWord.class))).thenReturn(savedWord);

        VocabularyWord result = vocabularyService.createWord(testWordDTO);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getDutch()).isEqualTo("goedemorgen");
        verify(wordRepository).save(any(VocabularyWord.class));
    }

    @Test
    @DisplayName("Should create word with default difficulty when not provided")
    void createWord_WhenNoDifficulty_ShouldDefaultToMedium() {
        VocabularyWordDTO dtoWithoutDifficulty = VocabularyWordDTO.builder()
                .dutch("test")
                .english("test")
                .build();

        when(wordRepository.save(any(VocabularyWord.class))).thenAnswer(invocation -> {
            VocabularyWord word = invocation.getArgument(0);
            word.setId(1L);
            return word;
        });

        VocabularyWord result = vocabularyService.createWord(dtoWithoutDifficulty);

        assertThat(result.getDifficulty()).isEqualTo(Difficulty.B1);
    }

    @Test
    @DisplayName("Should update existing word")
    void updateWord_WhenExists_ShouldUpdateAndReturn() {
        when(wordRepository.findById(1L)).thenReturn(Optional.of(testWord));
        when(wordRepository.save(any(VocabularyWord.class))).thenReturn(testWord);

        Optional<VocabularyWord> result = vocabularyService.updateWord(1L, testWordDTO);

        assertThat(result).isPresent();
        assertThat(result.get().getDutch()).isEqualTo("goedemorgen");
        verify(wordRepository).save(any(VocabularyWord.class));
    }

    @Test
    @DisplayName("Should return empty when updating non-existent word")
    void updateWord_WhenNotExists_ShouldReturnEmpty() {
        when(wordRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<VocabularyWord> result = vocabularyService.updateWord(999L, testWordDTO);

        assertThat(result).isEmpty();
        verify(wordRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete existing word")
    void deleteWord_WhenExists_ShouldReturnTrue() {
        when(wordRepository.existsById(1L)).thenReturn(true);
        doNothing().when(wordRepository).deleteById(1L);

        boolean result = vocabularyService.deleteWord(1L);

        assertThat(result).isTrue();
        verify(wordRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should return false when deleting non-existent word")
    void deleteWord_WhenNotExists_ShouldReturnFalse() {
        when(wordRepository.existsById(999L)).thenReturn(false);

        boolean result = vocabularyService.deleteWord(999L);

        assertThat(result).isFalse();
        verify(wordRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should get random words")
    void getRandomWords_ShouldReturnRequestedCount() {
        List<VocabularyWord> words = Arrays.asList(testWord);
        when(wordRepository.findRandomWords(5)).thenReturn(words);

        List<VocabularyWord> result = vocabularyService.getRandomWords(5);

        assertThat(result).hasSize(1);
        verify(wordRepository).findRandomWords(5);
    }
}

