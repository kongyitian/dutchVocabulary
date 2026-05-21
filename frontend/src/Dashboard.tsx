import React, { useState, useEffect } from 'react';
import { api } from './api';
import { Statistics } from './types';

interface Props {
  onStartQuiz: () => void;
}

const Dashboard: React.FC<Props> = ({ onStartQuiz }) => {
  const [stats, setStats] = useState<Statistics | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      const data = await api.getStatistics();
      setStats(data);
    } catch (error) {
      console.error('Failed to load statistics:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="card">
        <div className="loading">
          <div className="loading-spinner"></div>
          <p>Loading statistics...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="card">
      <h2 style={{ marginBottom: '20px', color: '#333' }}>📊 Your Progress</h2>

      {stats && (
        <div className="stats-grid">
          <div className="stat-item">
            <div className="number">{stats.totalWords}</div>
            <div className="label">Total Words</div>
          </div>
          <div className="stat-item">
            <div className="number">{stats.wordsStudied}</div>
            <div className="label">Words Studied</div>
          </div>
          <div className="stat-item">
            <div className="number">{stats.totalAttempts}</div>
            <div className="label">Total Attempts</div>
          </div>
          <div className="stat-item">
            <div className="number">{stats.totalCorrect}</div>
            <div className="label">Correct Answers</div>
          </div>
          <div className="stat-item">
            <div className="number">{stats.overallSuccessRate.toFixed(0)}%</div>
            <div className="label">Success Rate</div>
          </div>
          <div className="stat-item">
            <div className="number">{stats.wordsToReview}</div>
            <div className="label">Words to Review</div>
          </div>
        </div>
      )}

      <div style={{ textAlign: 'center', marginTop: '30px' }}>
        <button className="btn btn-primary" onClick={onStartQuiz}>
          🎯 Start Practice Quiz
        </button>
      </div>
    </div>
  );
};

export default Dashboard;

