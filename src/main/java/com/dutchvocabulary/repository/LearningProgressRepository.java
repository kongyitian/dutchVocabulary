package com.dutchvocabulary.repository;

import com.dutchvocabulary.model.LearningProgress;
import com.dutchvocabulary.model.User;
import com.dutchvocabulary.model.VocabularyWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningProgressRepository extends JpaRepository<LearningProgress, Long> {

    Optional<LearningProgress> findByUserAndWord(User user, VocabularyWord word);

    Optional<LearningProgress> findByUserIdAndWordId(Long userId, Long wordId);

    @Query("SELECT lp FROM LearningProgress lp WHERE lp.user.id = :userId ORDER BY lp.lastPracticed ASC NULLS FIRST")
    List<LearningProgress> findLeastRecentlyPracticedByUser(@Param("userId") Long userId);

    @Query("SELECT lp FROM LearningProgress lp WHERE lp.user.id = :userId AND lp.correctCount * 1.0 / NULLIF(lp.attemptCount, 0) < :threshold")
    List<LearningProgress> findWeakWordsByUser(@Param("userId") Long userId, @Param("threshold") double threshold);

    @Query("SELECT AVG(lp.correctCount * 1.0 / NULLIF(lp.attemptCount, 0)) FROM LearningProgress lp WHERE lp.user.id = :userId AND lp.attemptCount > 0")
    Double getOverallSuccessRateByUser(@Param("userId") Long userId);

    @Query("SELECT SUM(lp.attemptCount) FROM LearningProgress lp WHERE lp.user.id = :userId")
    Long getTotalAttemptsByUser(@Param("userId") Long userId);

    @Query("SELECT SUM(lp.correctCount) FROM LearningProgress lp WHERE lp.user.id = :userId")
    Long getTotalCorrectByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(lp) FROM LearningProgress lp WHERE lp.user.id = :userId AND lp.attemptCount > 0")
    Long getWordsStudiedByUser(@Param("userId") Long userId);
}
