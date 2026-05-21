package com.dutchvocabulary.repository;

import com.dutchvocabulary.model.LearningProgress;
import com.dutchvocabulary.model.VocabularyWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningProgressRepository extends JpaRepository<LearningProgress, Long> {

    Optional<LearningProgress> findByWord(VocabularyWord word);

    Optional<LearningProgress> findByWordId(Long wordId);

    @Query("SELECT lp FROM LearningProgress lp ORDER BY lp.lastPracticed ASC NULLS FIRST")
    List<LearningProgress> findLeastRecentlyPracticed();

    @Query("SELECT lp FROM LearningProgress lp WHERE lp.correctCount * 1.0 / NULLIF(lp.attemptCount, 0) < :threshold")
    List<LearningProgress> findWeakWords(@Param("threshold") double threshold);

    @Query("SELECT AVG(lp.correctCount * 1.0 / NULLIF(lp.attemptCount, 0)) FROM LearningProgress lp WHERE lp.attemptCount > 0")
    Double getOverallSuccessRate();

    @Query("SELECT SUM(lp.attemptCount) FROM LearningProgress lp")
    Long getTotalAttempts();

    @Query("SELECT SUM(lp.correctCount) FROM LearningProgress lp")
    Long getTotalCorrect();

    @Query("SELECT COUNT(lp) FROM LearningProgress lp WHERE lp.attemptCount > 0")
    Long getWordsStudied();
}

