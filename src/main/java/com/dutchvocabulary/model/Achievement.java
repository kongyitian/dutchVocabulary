package com.dutchvocabulary.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity storing user achievements.
 */
@Entity
@Table(name = "achievements", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "achievement_type"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String achievementType;  // FIRST_CORRECT, STREAK_5, STREAK_10, etc.

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column
    private String icon;  // emoji or icon name

    @Column
    @Builder.Default
    private LocalDateTime earnedAt = LocalDateTime.now();
}

