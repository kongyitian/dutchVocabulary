package com.dutchvocabulary.repository;

import com.dutchvocabulary.model.Achievement;
import com.dutchvocabulary.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    List<Achievement> findByUserOrderByEarnedAtDesc(User user);

    List<Achievement> findByUserIdOrderByEarnedAtDesc(Long userId);

    Optional<Achievement> findByUserAndAchievementType(User user, String achievementType);

    boolean existsByUserAndAchievementType(User user, String achievementType);

    long countByUser(User user);
}

