package com.dutchvocabulary.model;

/**
 * Enum representing CEFR (Common European Framework of Reference) language proficiency levels.
 * A1-A2: Basic User
 * B1-B2: Independent User
 * C1-C2: Proficient User
 */
public enum Difficulty {
    A1("Beginner"),
    A2("Elementary"),
    B1("Intermediate"),
    B2("Upper Intermediate"),
    C1("Advanced"),
    C2("Proficient");

    private final String description;

    Difficulty(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
