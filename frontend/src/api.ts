import { VocabularyWord, QuizQuestion, QuizAnswer, QuizResult, Statistics, AuthResponse, Achievement, DailyStreakInfo } from './types';

const API_BASE = '/api';

// Token management
let authToken: string | null = localStorage.getItem('authToken');

export const setAuthToken = (token: string | null) => {
  authToken = token;
  if (token) {
    localStorage.setItem('authToken', token);
  } else {
    localStorage.removeItem('authToken');
  }
};

export const getAuthToken = () => authToken;

export const isAuthenticated = () => !!authToken;

const getAuthHeaders = (): HeadersInit => {
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };
  if (authToken) {
    headers['Authorization'] = `Bearer ${authToken}`;
  }
  return headers;
};

export const api = {
  // Auth
  async login(username: string, password: string): Promise<AuthResponse> {
    const response = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Login failed');
    }
    const data = await response.json();
    if (data.token) {
      setAuthToken(data.token);
    }
    return data;
  },

  async register(username: string, password: string, displayName: string): Promise<AuthResponse> {
    const response = await fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password, displayName }),
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Registration failed');
    }
    const data = await response.json();
    if (data.token) {
      setAuthToken(data.token);
    }
    return data;
  },

  logout() {
    setAuthToken(null);
  },

  // Words (public)
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

  // Quiz (requires auth)
  async getQuiz(count: number = 10, category?: string): Promise<QuizQuestion[]> {
    let url = `${API_BASE}/quiz?count=${count}`;
    if (category) {
      url += `&category=${encodeURIComponent(category)}`;
    }
    const response = await fetch(url, { headers: getAuthHeaders() });
    if (!response.ok) {
      throw new Error('Failed to load quiz');
    }
    return response.json();
  },

  async getSmartQuiz(count: number = 10): Promise<QuizQuestion[]> {
    const response = await fetch(`${API_BASE}/quiz/smart?count=${count}`, {
      headers: getAuthHeaders(),
    });
    if (!response.ok) {
      throw new Error('Failed to load smart quiz');
    }
    return response.json();
  },

  async submitAnswer(answer: QuizAnswer): Promise<QuizResult> {
    const response = await fetch(`${API_BASE}/quiz/answer`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(answer),
    });
    if (!response.ok) {
      throw new Error('Failed to submit answer');
    }
    return response.json();
  },

  async submitQuiz(answers: QuizAnswer[]): Promise<QuizResult[]> {
    const response = await fetch(`${API_BASE}/quiz/submit`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(answers),
    });
    if (!response.ok) {
      throw new Error('Failed to submit quiz');
    }
    return response.json();
  },

  // Statistics (requires auth)
  async getStatistics(): Promise<Statistics> {
    const response = await fetch(`${API_BASE}/quiz/statistics`, {
      headers: getAuthHeaders(),
    });
    if (!response.ok) {
      throw new Error('Failed to load statistics');
    }
    return response.json();
  },

  // Achievements (requires auth)
  async getAchievements(): Promise<Achievement[]> {
    const response = await fetch(`${API_BASE}/progress/achievements`, {
      headers: getAuthHeaders(),
    });
    if (!response.ok) {
      throw new Error('Failed to load achievements');
    }
    return response.json();
  },

  // Daily Streak (requires auth)
  async getDailyStreak(): Promise<DailyStreakInfo> {
    const response = await fetch(`${API_BASE}/progress/streak`, {
      headers: getAuthHeaders(),
    });
    if (!response.ok) {
      throw new Error('Failed to load streak info');
    }
    return response.json();
  },
};
