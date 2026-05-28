package com.dutchvocabulary.repository;

import com.dutchvocabulary.config.TestConfig;
import com.dutchvocabulary.model.Difficulty;
import com.dutchvocabulary.model.VocabularyWord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository integration tests for VocabularyWordRepository.
 * Tests JPA queries and database interactions.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("VocabularyWord Repository Integration Tests")
class VocabularyWordRepositoryTest {

    @Autowired
    private VocabularyWordRepository repository;

    @BeforeEach
    void setUp() {
        // Clear existing data and add test data
        repository.deleteAll();

        repository.save(VocabularyWord.builder()
                .dutch("hallo")
                .english("hello")
                .category("greetings")
                .difficulty(Difficulty.A1)
                .build());

        repository.save(VocabularyWord.builder()
                .dutch("tot ziens")
                .english("goodbye")
                .category("greetings")
                .difficulty(Difficulty.A1)
                .build());

        repository.save(VocabularyWord.builder()
                .dutch("zijn")
                .english("to be")
                .category("verbs")
                .difficulty(Difficulty.A1)
                .build());

        repository.save(VocabularyWord.builder()
                .dutch("kunnen")
                .english("can")
                .category("verbs")
                .difficulty(Difficulty.B1)
                .build());

        repository.save(VocabularyWord.builder()
                .dutch("begrijpen")
                .english("to understand")
                .category("verbs")
                .difficulty(Difficulty.C2)
                .build());
    }

    @Test
    @Order(1)
    @DisplayName("findByCategory should return words in category")
    void findByCategory_ShouldReturnWordsInCategory() {
        List<VocabularyWord> greetings = repository.findByCategory("greetings");

        assertThat(greetings).hasSize(2);
        assertThat(greetings).allMatch(w -> w.getCategory().equals("greetings"));
    }

    @Test
    @Order(2)
    @DisplayName("findByCategory should return empty list for non-existent category")
    void findByCategory_NonExistent_ShouldReturnEmptyList() {
        List<VocabularyWord> result = repository.findByCategory("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    @Order(3)
    @DisplayName("findByDifficulty should filter by difficulty level")
    void findByDifficulty_ShouldFilterByDifficulty() {
        List<VocabularyWord> easyWords = repository.findByDifficulty(Difficulty.A1);
        List<VocabularyWord> hardWords = repository.findByDifficulty(Difficulty.C2);

        assertThat(easyWords).hasSize(3);
        assertThat(hardWords).hasSize(1);
        assertThat(easyWords).allMatch(w -> w.getDifficulty() == Difficulty.A1);
    }

    @Test
    @Order(4)
    @DisplayName("findByCategoryAndDifficulty should combine filters")
    void findByCategoryAndDifficulty_ShouldCombineFilters() {
        List<VocabularyWord> easyVerbs = repository.findByCategoryAndDifficulty("verbs", Difficulty.A1);

        assertThat(easyVerbs).hasSize(1);
        assertThat(easyVerbs.get(0).getDutch()).isEqualTo("zijn");
    }

    @Test
    @Order(5)
    @DisplayName("findAllCategories should return distinct categories")
    void findAllCategories_ShouldReturnDistinctCategories() {
        List<String> categories = repository.findAllCategories();

        assertThat(categories).containsExactlyInAnyOrder("greetings", "verbs");
    }

    @Test
    @Order(6)
    @DisplayName("searchWords should find by Dutch word")
    void searchWords_ShouldFindByDutchWord() {
        List<VocabularyWord> results = repository.searchWords("hallo");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDutch()).isEqualTo("hallo");
    }

    @Test
    @Order(7)
    @DisplayName("searchWords should find by English word")
    void searchWords_ShouldFindByEnglishWord() {
        List<VocabularyWord> results = repository.searchWords("hello");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getEnglish()).isEqualTo("hello");
    }

    @Test
    @Order(8)
    @DisplayName("searchWords should be case-insensitive")
    void searchWords_ShouldBeCaseInsensitive() {
        List<VocabularyWord> results = repository.searchWords("HALLO");

        assertThat(results).hasSize(1);
    }

    @Test
    @Order(9)
    @DisplayName("searchWords should find partial matches")
    void searchWords_ShouldFindPartialMatches() {
        List<VocabularyWord> results = repository.searchWords("hal");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDutch()).isEqualTo("hallo");
    }

    @Test
    @Order(10)
    @DisplayName("findRandomWords should return requested count")
    void findRandomWords_ShouldReturnRequestedCount() {
        List<VocabularyWord> randomWords = repository.findRandomWords(3);

        assertThat(randomWords).hasSize(3);
    }

    @Test
    @Order(11)
    @DisplayName("findRandomWords should not exceed available words")
    void findRandomWords_ShouldNotExceedAvailable() {
        List<VocabularyWord> randomWords = repository.findRandomWords(100);

        assertThat(randomWords).hasSizeLessThanOrEqualTo(5);
    }

    @Test
    @Order(12)
    @DisplayName("Should save word with all fields")
    void save_ShouldPersistAllFields() {
        VocabularyWord word = VocabularyWord.builder()
                .dutch("nieuw")
                .english("new")
                .category("adjectives")
                .difficulty(Difficulty.A1)
                .example("Dit is nieuw.")
                .exampleTranslation("This is new.")
                .pronunciation("neew")
                .build();

        VocabularyWord saved = repository.save(word);

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(w -> {
                    assertThat(w.getDutch()).isEqualTo("nieuw");
                    assertThat(w.getEnglish()).isEqualTo("new");
                    assertThat(w.getCategory()).isEqualTo("adjectives");
                    assertThat(w.getDifficulty()).isEqualTo(Difficulty.A1);
                    assertThat(w.getExample()).isEqualTo("Dit is nieuw.");
                    assertThat(w.getExampleTranslation()).isEqualTo("This is new.");
                    assertThat(w.getPronunciation()).isEqualTo("neew");
                });
    }

    @Test
    @Order(13)
    @DisplayName("Should update existing word")
    void update_ShouldModifyExistingWord() {
        VocabularyWord word = repository.findByCategory("greetings").get(0);
        Long originalId = word.getId();

        word.setEnglish("hi");
        repository.save(word);

        VocabularyWord updated = repository.findById(originalId).orElseThrow();
        assertThat(updated.getEnglish()).isEqualTo("hi");
    }

    @Test
    @Order(14)
    @DisplayName("Should delete word by id")
    void delete_ShouldRemoveWord() {
        VocabularyWord word = repository.findByCategory("greetings").get(0);
        Long id = word.getId();

        repository.deleteById(id);

        assertThat(repository.findById(id)).isEmpty();
    }
}

