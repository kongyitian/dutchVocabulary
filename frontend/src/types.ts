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

