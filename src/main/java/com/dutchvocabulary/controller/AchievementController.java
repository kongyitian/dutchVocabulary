package com.dutchvocabulary.controller;

import com.dutchvocabulary.dto.AchievementDTO;
import com.dutchvocabulary.dto.DailyStreakDTO;
import com.dutchvocabulary.model.DailyStreak;
import com.dutchvocabulary.model.User;
import com.dutchvocabulary.repository.UserRepository;
import com.dutchvocabulary.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
            "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return userRepository.findByUsername(authentication.getName()).orElse(null);
    }

    /**
     * Get all achievements for the current user.
     */
    @GetMapping
    public ResponseEntity<List<AchievementDTO>> getAchievements() {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(achievementService.getUserAchievements(user));
    }

    /**
     * Get daily streak info for the current user.
     */
    @GetMapping("/streak")
    public ResponseEntity<DailyStreakDTO> getDailyStreak() {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.ok(DailyStreakDTO.builder()
                    .currentStreak(0)
                    .longestStreak(0)
                    .totalDaysPracticed(0)
                    .practicedToday(false)
                    .build());
        }

        DailyStreak streak = achievementService.getDailyStreak(user);
        return ResponseEntity.ok(DailyStreakDTO.builder()
                .currentStreak(streak.getCurrentStreak())
                .longestStreak(streak.getLongestStreak())
                .totalDaysPracticed(streak.getTotalDaysPracticed())
                .lastPracticeDate(streak.getLastPracticeDate())
                .practicedToday(streak.hasPracticedToday())
                .build());
    }

    /**
     * Get achievement summary (count).
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.ok(Map.of(
                    "achievementCount", 0,
                    "currentStreak", 0,
                    "longestStreak", 0
            ));
        }

        DailyStreak streak = achievementService.getDailyStreak(user);
        long achievementCount = achievementService.getAchievementCount(user);

        return ResponseEntity.ok(Map.of(
                "achievementCount", achievementCount,
                "currentStreak", streak.getCurrentStreak(),
                "longestStreak", streak.getLongestStreak(),
                "totalDaysPracticed", streak.getTotalDaysPracticed(),
                "practicedToday", streak.hasPracticedToday()
        ));
    }
}

