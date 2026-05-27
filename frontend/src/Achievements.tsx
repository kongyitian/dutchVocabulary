import React, { useState, useEffect } from 'react';
import { api } from './api';
import { Achievement, DailyStreakInfo } from './types';

const Achievements: React.FC = () => {
  const [achievements, setAchievements] = useState<Achievement[]>([]);
  const [streak, setStreak] = useState<DailyStreakInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [achievementsData, streakData] = await Promise.all([
        api.getAchievements(),
        api.getDailyStreak(),
      ]);
      setAchievements(achievementsData);
      setStreak(streakData);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load achievements');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="card">
        <div className="loading">
          <div className="loading-spinner"></div>
          <p>Loading achievements...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="card">
        <div className="error-message">
          ⚠️ {error}
          <button className="btn btn-secondary" onClick={loadData} style={{ marginTop: '10px' }}>
            Try Again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="card">
      <h2 style={{ marginBottom: '20px', color: '#333' }}>🏆 Achievements</h2>

      {/* Daily Streak Section */}
      {streak && (
        <div className="streak-section">
          <div className="streak-card">
            <div className="streak-flame">🔥</div>
            <div className="streak-info">
              <div className="streak-number">{streak.currentStreak}</div>
              <div className="streak-label">Day Streak</div>
            </div>
            <div className="streak-status">
              {streak.practicedToday ? (
                <span className="practiced-today">✓ Practiced today!</span>
              ) : (
                <span className="not-practiced">Practice today to keep your streak!</span>
              )}
            </div>
          </div>

          <div className="streak-stats">
            <div className="streak-stat">
              <div className="stat-value">{streak.longestStreak}</div>
              <div className="stat-label">Longest Streak</div>
            </div>
            <div className="streak-stat">
              <div className="stat-value">{streak.totalDaysPracticed}</div>
              <div className="stat-label">Total Days</div>
            </div>
          </div>
        </div>
      )}

      {/* Achievements List */}
      <h3 style={{ margin: '30px 0 15px', color: '#333' }}>
        🎖️ Your Badges ({achievements.length})
      </h3>

      {achievements.length === 0 ? (
        <div className="empty-state">
          <p>🎯 No achievements yet!</p>
          <p>Complete quizzes to earn badges and achievements.</p>
        </div>
      ) : (
        <div className="achievements-grid">
          {achievements.map((achievement) => (
            <div key={achievement.id} className="achievement-card">
              <div className="achievement-icon">{achievement.icon}</div>
              <div className="achievement-details">
                <div className="achievement-title">{achievement.title}</div>
                <div className="achievement-description">{achievement.description}</div>
                <div className="achievement-date">
                  {new Date(achievement.earnedAt).toLocaleDateString()}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Available Achievements (hints) */}
      <div className="available-achievements">
        <h3 style={{ margin: '30px 0 15px', color: '#666' }}>📋 Available Achievements</h3>
        <div className="achievement-hints">
          <div className="hint-item">🎉 <strong>First Steps</strong> - Get your first correct answer</div>
          <div className="hint-item">🔥 <strong>On Fire</strong> - 5 correct answers in a row</div>
          <div className="hint-item">🏆 <strong>Unstoppable</strong> - 10 correct answers in a row</div>
          <div className="hint-item">👑 <strong>Legendary</strong> - 25 correct answers in a row</div>
          <div className="hint-item">📅 <strong>Week Warrior</strong> - Practice 7 days in a row</div>
          <div className="hint-item">🗓️ <strong>Monthly Master</strong> - Practice 30 days in a row</div>
          <div className="hint-item">📚 <strong>Word Master</strong> - Master a word (90%+ accuracy)</div>
        </div>
      </div>
    </div>
  );
};

export default Achievements;

