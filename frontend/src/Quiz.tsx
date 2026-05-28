import React, { useState, useEffect, useCallback } from 'react';
import { api } from './api';
import { QuizQuestion, QuizResult } from './types';

interface Props {
  onBack: () => void;
}

const Quiz: React.FC<Props> = ({ onBack }) => {
  const [questions, setQuestions] = useState<QuizQuestion[]>([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answer, setAnswer] = useState('');
  const [results, setResults] = useState<QuizResult[]>([]);
  const [currentResult, setCurrentResult] = useState<QuizResult | null>(null);
  const [loading, setLoading] = useState(true);
  const [quizComplete, setQuizComplete] = useState(false);
  const [categories, setCategories] = useState<string[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>('');
  const [quizStarted, setQuizStarted] = useState(false);

  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      const cats = await api.getCategories();
      setCategories(cats);
    } catch (error) {
      console.error('Failed to load categories:', error);
    }
    setLoading(false);
  };

  const startQuiz = async (smart: boolean = false) => {
    setLoading(true);
    setError(null);
    try {
      let quizQuestions: QuizQuestion[];
      if (smart) {
        quizQuestions = await api.getSmartQuiz(10);
      } else {
        quizQuestions = await api.getQuiz(10, selectedCategory || undefined);
      }
      if (quizQuestions.length === 0) {
        setError('No quiz questions available. Try a different category.');
        return;
      }
      setQuestions(quizQuestions);
      setQuizStarted(true);
      setCurrentIndex(0);
      setResults([]);
      setCurrentResult(null);
      setQuizComplete(false);
    } catch (error) {
      console.error('Failed to load quiz:', error);
      setError('Failed to load quiz. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async () => {
    if (!answer.trim()) return;

    const question = questions[currentIndex];
    try {
      const result = await api.submitAnswer({
        wordId: question.wordId,
        answer: answer.trim(),
      });
      setCurrentResult(result);
      setResults([...results, result]);
    } catch (error) {
      console.error('Failed to submit answer:', error);
    }
  };

  const handleNext = useCallback(() => {
    setCurrentResult(null);
    setAnswer('');

    if (currentIndex + 1 >= questions.length) {
      setQuizComplete(true);
    } else {
      setCurrentIndex(currentIndex + 1);
    }
  }, [currentIndex, questions.length]);

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      if (currentResult) {
        handleNext();
      } else {
        handleSubmit();
      }
    }
  };

  const restartQuiz = () => {
    setQuizStarted(false);
    setQuestions([]);
    setCurrentIndex(0);
    setResults([]);
    setCurrentResult(null);
    setQuizComplete(false);
    setAnswer('');
  };

  if (loading) {
    return (
      <div className="card">
        <div className="loading">
          <div className="loading-spinner"></div>
          <p>Loading...</p>
        </div>
      </div>
    );
  }

  // Quiz selection screen
  if (!quizStarted) {
    return (
      <div className="card quiz-card">
        <h2 style={{ marginBottom: '30px', color: '#333' }}>🎯 Start a Quiz</h2>

        {error && (
          <div style={{
            background: '#f8d7da',
            color: '#721c24',
            padding: '12px 15px',
            borderRadius: '10px',
            marginBottom: '20px',
            textAlign: 'center'
          }}>
            ⚠️ {error}
          </div>
        )}

        <div style={{ marginBottom: '30px' }}>
          <label style={{ display: 'block', marginBottom: '10px', color: '#666' }}>
            Filter by category (optional):
          </label>
          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            style={{
              padding: '12px 20px',
              fontSize: '1rem',
              borderRadius: '10px',
              border: '2px solid #ddd',
              minWidth: '200px'
            }}
          >
            <option value="">All categories</option>
            {categories.map(cat => (
              <option key={cat} value={cat}>
                {cat.charAt(0).toUpperCase() + cat.slice(1)}
              </option>
            ))}
          </select>
        </div>

        <div style={{ display: 'flex', gap: '15px', justifyContent: 'center', flexWrap: 'wrap' }}>
          <button className="btn btn-primary" onClick={() => startQuiz(false)}>
            🎲 Random Quiz
          </button>
          <button className="btn btn-primary" onClick={() => startQuiz(true)}>
            🧠 Smart Quiz
          </button>
          <button className="btn btn-secondary" onClick={onBack}>
            ← Back
          </button>
        </div>

        <p style={{ marginTop: '20px', color: '#888', fontSize: '0.9rem' }}>
          Smart Quiz focuses on words you need to practice more!
        </p>
      </div>
    );
  }

  // Quiz complete screen
  if (quizComplete) {
    const correctCount = results.filter(r => r.correct).length;
    const percentage = Math.round((correctCount / results.length) * 100);

    return (
      <div className="card quiz-results">
        <h2 style={{ marginBottom: '20px', color: '#333' }}>🎉 Quiz Complete!</h2>

        <div className="score-circle">
          <div className="score">{percentage}%</div>
          <div className="label">{correctCount}/{results.length} correct</div>
        </div>

        <div className="results-list">
          {results.map((result, index) => (
            <div key={index} className="result-item">
              <div className="word-info">
                <div className="dutch">{result.dutch}</div>
                <div className="english">
                  {result.correct
                    ? result.correctAnswer
                    : `${result.userAnswer} → ${result.correctAnswer}`}
                </div>
              </div>
              <span className={`status ${result.correct ? 'correct' : 'incorrect'}`}>
                {result.correct ? '✓ Correct' : '✗ Wrong'}
              </span>
            </div>
          ))}
        </div>

        <div style={{ marginTop: '20px' }}>
          <button className="btn btn-primary" onClick={restartQuiz}>
            🔄 New Quiz
          </button>
          <button className="btn btn-secondary" onClick={onBack}>
            ← Back to Dashboard
          </button>
        </div>
      </div>
    );
  }

  // Quiz question screen
  const question = questions[currentIndex];

  return (
    <div className="card quiz-card">
      <div className="quiz-progress">
        Question {currentIndex + 1} of {questions.length}
      </div>

      <div className="dutch-word">{question.dutch}</div>

      <div className="word-meta">
        {question.category && (
          <span>{question.category}</span>
        )}
        <span>{question.difficulty}</span>
      </div>

      {question.example && (
        <div className="example">
          💡 {question.example}
        </div>
      )}

      <p style={{ color: '#666', marginBottom: '15px' }}>
        What is the English translation?
      </p>

      <input
        type="text"
        className={`answer-input ${currentResult ? (currentResult.correct ? 'correct' : 'incorrect') : ''}`}
        value={answer}
        onChange={(e) => setAnswer(e.target.value)}
        onKeyPress={handleKeyPress}
        placeholder="Type your answer..."
        disabled={!!currentResult}
        autoFocus
      />

      {currentResult && (
        <div className={`result-message ${currentResult.correct ? 'correct' : 'incorrect'}`}>
          {currentResult.correct
            ? '✓ Correct! Great job!'
            : `✗ Incorrect. The answer is: ${currentResult.correctAnswer}`}
        </div>
      )}

      <div>
        {!currentResult ? (
          <button className="btn btn-primary" onClick={handleSubmit}>
            Check Answer
          </button>
        ) : (
          <button className="btn btn-primary" onClick={handleNext}>
            {currentIndex + 1 >= questions.length ? 'See Results' : 'Next Question'} →
          </button>
        )}
      </div>

      <button
        className="btn btn-secondary"
        onClick={restartQuiz}
        style={{ marginTop: '10px' }}
      >
        ✗ Quit Quiz
      </button>
    </div>
  );
};

export default Quiz;

