// Types for the Dutch Vocabulary API

export interface VocabularyWord {
  id: number;
  dutch: string;
  english: string;
  example?: string;
  exampleTranslation?: string;
  category?: string;
  difficulty: 'EASY' | 'MEDIUM' | 'HARD';
  pronunciation?: string;
}

export interface QuizQuestion {
  wordId: number;
  dutch: string;
  category?: string;
  difficulty: string;
  pronunciation?: string;
  example?: string;
  options?: string[];
}

export interface QuizAnswer {
  wordId: number;
  answer: string;
}

export interface QuizResult {
  wordId: number;
  dutch: string;
  correctAnswer: string;
  userAnswer: string;
  correct: boolean;
  currentStreak: number;
  successRate: number;
}

export interface Statistics {
  totalWords: number;
  wordsStudied: number;
  totalAttempts: number;
  totalCorrect: number;
  overallSuccessRate: number;
  wordsToReview: number;
}

export interface AuthResponse {
  token?: string;
  username?: string;
  displayName?: string;
  message?: string;
}

export interface Achievement {
  id: number;
  achievementType: string;
  title: string;
  description: string;
  icon: string;
  earnedAt: string;
}

export interface DailyStreakInfo {
  currentStreak: number;
  longestStreak: number;
  totalDaysPracticed: number;
  lastPracticeDate?: string;
  practicedToday: boolean;
}

export interface User {
  username: string;
  displayName: string;
}



