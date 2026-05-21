import { VocabularyWord, QuizQuestion, QuizAnswer, QuizResult, Statistics } from './types';

const API_BASE = '/api';

export const api = {
  // Words
  async getWords(): Promise<VocabularyWord[]> {
    const response = await fetch(`${API_BASE}/words`);
    return response.json();
  },

  async getWordsByCategory(category: string): Promise<VocabularyWord[]> {
    const response = await fetch(`${API_BASE}/words/category/${category}`);
    return response.json();
  },

  async getCategories(): Promise<string[]> {
    const response = await fetch(`${API_BASE}/words/categories`);
    return response.json();
  },

  async searchWords(query: string): Promise<VocabularyWord[]> {
    const response = await fetch(`${API_BASE}/words/search?q=${encodeURIComponent(query)}`);
    return response.json();
  },

  // Quiz
  async getQuiz(count: number = 10, category?: string): Promise<QuizQuestion[]> {
    let url = `${API_BASE}/quiz?count=${count}`;
    if (category) {
      url += `&category=${encodeURIComponent(category)}`;
    }
    const response = await fetch(url);
    return response.json();
  },

  async getSmartQuiz(count: number = 10): Promise<QuizQuestion[]> {
    const response = await fetch(`${API_BASE}/quiz/smart?count=${count}`);
    return response.json();
  },

  async submitAnswer(answer: QuizAnswer): Promise<QuizResult> {
    const response = await fetch(`${API_BASE}/quiz/answer`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(answer),
    });
    return response.json();
  },

  async submitQuiz(answers: QuizAnswer[]): Promise<QuizResult[]> {
    const response = await fetch(`${API_BASE}/quiz/submit`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(answers),
    });
    return response.json();
  },

  // Statistics
  async getStatistics(): Promise<Statistics> {
    const response = await fetch(`${API_BASE}/quiz/statistics`);
    return response.json();
  },
};

