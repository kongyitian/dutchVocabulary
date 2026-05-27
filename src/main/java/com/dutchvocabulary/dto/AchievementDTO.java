package com.dutchvocabulary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementDTO {
    private Long id;
    private String achievementType;
    private String title;
    private String description;
    private String icon;
    private LocalDateTime earnedAt;
}

