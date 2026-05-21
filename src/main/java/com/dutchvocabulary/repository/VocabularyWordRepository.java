package com.dutchvocabulary.repository;

import com.dutchvocabulary.model.Difficulty;
import com.dutchvocabulary.model.VocabularyWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabularyWordRepository extends JpaRepository<VocabularyWord, Long> {

    List<VocabularyWord> findByCategory(String category);

    List<VocabularyWord> findByDifficulty(Difficulty difficulty);

    List<VocabularyWord> findByCategoryAndDifficulty(String category, Difficulty difficulty);

    @Query("SELECT DISTINCT v.category FROM VocabularyWord v WHERE v.category IS NOT NULL")
    List<String> findAllCategories();

    @Query("SELECT v FROM VocabularyWord v WHERE LOWER(v.dutch) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(v.english) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<VocabularyWord> searchWords(@Param("searchTerm") String searchTerm);

    @Query(value = "SELECT * FROM vocabulary_words ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<VocabularyWord> findRandomWords(@Param("count") int count);
}


